package ir.map.navigationsdk;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.OnLocationClickListener;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.LineManager;
import com.mapbox.mapboxsdk.style.layers.BackgroundLayer;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.turf.TurfMeasurement;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ir.map.navigationsdk.model.RouteResponse;
import ir.map.navigationsdk.model.base.MapirError;
import ir.map.navigationsdk.model.base.MapirResponse;
import ir.map.navigationsdk.model.enums.RouteOverView;
import ir.map.navigationsdk.model.enums.RouteType;
import ir.map.sdk_map.MapirStyle;
import ir.map.sdk_map.maps.MapView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.mapbox.mapboxsdk.style.layers.Property.CIRCLE_PITCH_ALIGNMENT_MAP;
import static com.mapbox.mapboxsdk.style.layers.Property.ICON_PITCH_ALIGNMENT_MAP;
import static com.mapbox.mapboxsdk.style.layers.Property.LINE_JOIN_ROUND;
import static com.mapbox.turf.TurfConstants.UNIT_METRES;
import static ir.map.navigationsdk.HttpUtils.createError;
import static ir.map.navigationsdk.HttpUtils.createResponse;

public class MapirNavigationActivity extends AppCompatActivity {

    //region Initialize
    private OkHttpClient client = new OkHttpClient();
    //endregion Initialize

    MapboxMap map;
    Style mapStyle;
    MapView mapView;

    private LatLng origin;
    private LatLng destination;
    private LineString mainRouteLineString;

    private LocationComponent locationComponent = null;
    private LocationEngine locationEngine;
    private long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    private MapirNavigationActivityLocationCallback callback = new MapirNavigationActivityLocationCallback(this);

    private Point firstLastPoint = null;
    private Point secondLastPoint = null;
    private double bearing = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapir_navigation);

        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }

        try {
            getActionBar().hide();
        } catch (NullPointerException e) {
        }

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                map = mapboxMap;
                map.setStyle(new Style.Builder().fromUri(MapirStyle.MAIN_MOBILE_VECTOR_STYLE), new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        mapStyle = style;

                        enableLocationComponent();

                        Gson gson = new Gson();

                        showRouteOnMap(
                                gson.fromJson(getIntent().getExtras().getString("routeResponse"), RouteResponse.class).getRoutes().get(0).getGeometry()
                        );

                        MapirNavigationActivity.this.origin = gson.fromJson(getIntent().getExtras().getString("origin"), LatLng.class);
                        MapirNavigationActivity.this.destination = gson.fromJson(getIntent().getExtras().getString("destination"), LatLng.class);
                    }
                });
            }
        });

        executeRouteRequest(new ResponseListener() {
            @Override
            public void onSuccess(Object response) {

            }

            @Override
            public void onError(MapirError error) {

            }
        });
    }

    void showRouteOnMap(String geometry) {
        LineManager lineManager = new LineManager(mapView, map, mapStyle);
        LineString routeLine = LineString.fromPolyline(geometry, 5);

//        LineOptions lineOptions = new LineOptions()
//                .withGeometry(routeLine)
//                .withLineColor("#66b2ff")
//                .withLineGapWidth(8f)
//                .withLineWidth(15f);
//        lineManager.create(lineOptions);

        List<Point> coordinates = new ArrayList<>();
        for (int i = 1; i < TurfMeasurement.distance(routeLine.coordinates().get(0), routeLine.coordinates().get(routeLine.coordinates().size() - 1)) * 1000; i++)
            coordinates.add(TurfMeasurement.along(routeLine, i, UNIT_METRES));

        coordinates.add(routeLine.coordinates().get(routeLine.coordinates().size() - 1));

        mainRouteLineString = LineString.fromLngLats(coordinates);

        BackgroundLayer backgroundLayer = new BackgroundLayer("shadow_background_layer");
        backgroundLayer.setProperties(
                PropertyFactory.backgroundColor("#c0c0c0"),
                PropertyFactory.backgroundOpacity(0.4f)
        );
        mapStyle.addLayer(backgroundLayer);

        FeatureCollection featureCollection = FeatureCollection.fromFeature(Feature.fromGeometry(routeLine));
        GeoJsonSource geoJsonSourceMain = new GeoJsonSource("route_line_source_id", featureCollection);
        GeoJsonSource geoJsonSourceBelow = new GeoJsonSource("route_line_below_source_id", featureCollection);
        mapStyle.addSource(geoJsonSourceMain);
        mapStyle.addSource(geoJsonSourceBelow);

        // Add layer to map
        LineLayer belowLineLayer = new LineLayer("route_line_below_layer_id", "route_line_below_source_id");
        belowLineLayer.setProperties(
                PropertyFactory.lineWidth(32f),
                PropertyFactory.lineColor("#66b2ff"),
                PropertyFactory.lineOpacity(0.4f),
                PropertyFactory.lineJoin(LINE_JOIN_ROUND)
        );
        mapStyle.addLayer(belowLineLayer);

        LineLayer shadowLineLayer = new LineLayer("route_line_shadow_layer_id", "route_line_source_id");
        shadowLineLayer.setProperties(
                PropertyFactory.lineWidth(2f),
                PropertyFactory.lineGapWidth(32f),
                PropertyFactory.lineColor("#000"),
                PropertyFactory.lineJoin(LINE_JOIN_ROUND)
        );
        mapStyle.addLayer(shadowLineLayer);

        LineLayer lineLayer = new LineLayer("route_line_layer_id", "route_line_source_id");
        lineLayer.setProperties(
                PropertyFactory.lineWidth(32f),
                PropertyFactory.lineColor("#66b2ff"),
                PropertyFactory.lineOpacity(0.8f),
                PropertyFactory.lineJoin(LINE_JOIN_ROUND)
        );
        mapStyle.addLayer(lineLayer);

        origin = new LatLng(routeLine.coordinates().get(0).latitude(), routeLine.coordinates().get(0).longitude());

        map.easeCamera(CameraUpdateFactory.newLatLngZoom(origin, 22), 100, new MapboxMap.CancelableCallback() {
            @Override
            public void onCancel() {
            }

            @Override
            public void onFinish() {
                map.animateCamera(CameraUpdateFactory.tiltTo(70), new MapboxMap.CancelableCallback() {
                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onFinish() {
                        startSimulation(coordinates);
//                        map.animateCamera(CameraUpdateFactory.bearingTo(180));
                    }
                });
            }
        });
    }

    private void startSimulation(List<Point> coordinates) {
        locationComponent.setLocationComponentEnabled(false);

        Timer timer = new Timer();
        timer.schedule(new FakeGps(coordinates), 0, 500);

        addSymbolSourceAndLayerToMap();
    }

    private void boundToRoute(List<Point> coordinates) {
        List<LatLng> points = new ArrayList<>();

        for (Point point : coordinates)
            points.add(new LatLng(point.latitude(), point.longitude()));

        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .includes(points)
                .build();

        map.easeCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50), 5000);
    }

    private void addSymbolSourceAndLayerToMap() {
        Point _point = null;
        List<Point> points = mainRouteLineString.coordinates();
        if (!points.isEmpty()) {
            _point = points.get(0);
            points.remove(0);
            mainRouteLineString = LineString.fromLngLats(points);
        }

        FeatureCollection routeLineMainFeatureCollection = FeatureCollection.fromFeature(Feature.fromGeometry(mainRouteLineString));
        GeoJsonSource routeLineMainSource = mapStyle.getSourceAs("route_line_source_id");
        if (routeLineMainSource != null)
            routeLineMainSource.setGeoJson(routeLineMainFeatureCollection);

        if (_point != null) {
            Feature sampleFeature = Feature.fromGeometry(_point);
            FeatureCollection featureCollection = FeatureCollection.fromFeature(sampleFeature);

            // Add source to map
            GeoJsonSource locationSource = mapStyle.getSourceAs("location_symbol_source_id");
            if (locationSource != null)
                locationSource.setGeoJson(featureCollection);
            else {
                GeoJsonSource geoJsonSource = new GeoJsonSource("location_symbol_source_id", featureCollection);
                mapStyle.addSource(geoJsonSource);

                // Add image to map
                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.map_marker);
                mapStyle.addImage("location_symbol_image_id", icon);

                CircleLayer currentLocationCircleLayer = new CircleLayer("location_circle_layer_id", "location_symbol_source_id");
                currentLocationCircleLayer.setProperties(
                        PropertyFactory.circleColor("#a0a0a0"),
                        PropertyFactory.circleRadius(42f),
                        PropertyFactory.circlePitchAlignment(CIRCLE_PITCH_ALIGNMENT_MAP)
                );
                mapStyle.addLayer(currentLocationCircleLayer);

                // Add layer to map
                SymbolLayer symbolLayer = new SymbolLayer("location_symbol_layer_id", "location_symbol_source_id");
                symbolLayer.setProperties(
                        PropertyFactory.iconImage("location_symbol_image_id"),
                        PropertyFactory.iconSize(1.5f),
                        PropertyFactory.iconPitchAlignment(ICON_PITCH_ALIGNMENT_MAP)
                );
                mapStyle.addLayer(symbolLayer);
            }
        }
    }

    private void enableLocationComponent() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Create and customize the LocationComponent's options
            LocationComponentOptions customLocationComponentOptions = LocationComponentOptions.builder(this)
                    .elevation(5)
                    .gpsDrawable(R.mipmap.map_marker)
                    .accuracyAlpha(.6f)
                    .minZoomIconScale(1)
                    .maxZoomIconScale(1.5f)
                    .accuracyColor(Color.RED)
                    .build();

            // Get an instance of the component
            locationComponent = map.getLocationComponent();
            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(this, mapStyle)
                            .locationComponentOptions(customLocationComponentOptions)
                            .build();

            // Activate with options
            locationComponent.activateLocationComponent(locationComponentActivationOptions);

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING_GPS_NORTH);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.GPS);

            initLocationEngine();

            // Add the location icon click listener
            locationComponent.addOnLocationClickListener(new OnLocationClickListener() {
                @Override
                public void onLocationComponentClick() {
                }
            });
        } else {
            PermissionsManager permissionsManager = new PermissionsManager(new PermissionsListener() {
                @Override
                public void onExplanationNeeded(List<String> permissionsToExplain) {
                }

                @Override
                public void onPermissionResult(boolean granted) {
                    if (granted)
                        enableLocationComponent();
                    else
                        Toast.makeText(MapirNavigationActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
            });
            permissionsManager.requestLocationPermissions(this);
        }
    }

    /**
     * Set up the LocationEngine and the parameters for querying the device's location
     */
    @SuppressLint("MissingPermission")
    private void initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

        locationEngine.requestLocationUpdates(request, new LocationEngineCallback<LocationEngineResult>() {
            @Override
            public void onSuccess(LocationEngineResult result) {

            }

            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        }, getMainLooper());

        locationEngine.getLastLocation(callback);
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();

        if (locationEngine != null)
            locationEngine.removeLocationUpdates(callback);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    class FakeGps extends TimerTask {
        private List<Point> coordinates;
        private int index = 0;

        public FakeGps(List<Point> coordinates) {
            this.coordinates = coordinates;
        }

        public void run() {
            Point currentLocation = coordinates.get(index);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    map.easeCamera(CameraUpdateFactory.bearingTo(TurfMeasurement.bearing(currentLocation, coordinates.get(index + 1))), new MapboxMap.CancelableCallback() {
                        @Override
                        public void onCancel() {
                        }

                        @Override
                        public void onFinish() {
                            addSymbolSourceAndLayerToMap();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLocation.latitude(), currentLocation.longitude())), new MapboxMap.CancelableCallback() {
                                        @Override
                                        public void onCancel() {
                                        }

                                        @Override
                                        public void onFinish() {
                                            if (index != coordinates.size() - 1)
                                                index++;
                                        }
                                    });
                                }
                            }, 1);
                        }
                    });
                }
            });

            if (index == coordinates.size() - 1)
                this.cancel();
        }
    }

    private static class MapirNavigationActivityLocationCallback implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<MapirNavigationActivity> activityWeakReference;

        MapirNavigationActivityLocationCallback(MapirNavigationActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location has changed.
         *
         * @param result the LocationEngineResult object which has the last known location within it.
         */
        @Override
        public void onSuccess(LocationEngineResult result) {
            MapirNavigationActivity activity = activityWeakReference.get();

            if (activity != null) {
                Location location = result.getLastLocation();

                if (location == null) {
                    return;
                }

                if (activity.secondLastPoint == null)
                    activity.secondLastPoint = Point.fromLngLat(result.getLastLocation().getLongitude(), result.getLastLocation().getLatitude());
                else if (activity.firstLastPoint == null) {
                    activity.firstLastPoint = Point.fromLngLat(result.getLastLocation().getLongitude(), result.getLastLocation().getLatitude());
                    activity.bearing = TurfMeasurement.bearing(activity.secondLastPoint, activity.firstLastPoint);
                } else {
                    activity.secondLastPoint = activity.firstLastPoint;
                    activity.firstLastPoint = Point.fromLngLat(result.getLastLocation().getLongitude(), result.getLastLocation().getLatitude());
                    activity.bearing = TurfMeasurement.bearing(activity.secondLastPoint, activity.firstLastPoint);

                    Point currentLocation = activity.firstLastPoint;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.map.easeCamera(CameraUpdateFactory.bearingTo(TurfMeasurement.bearing(activity.secondLastPoint, activity.firstLastPoint)), new MapboxMap.CancelableCallback() {
                                @Override
                                public void onCancel() {
                                }

                                @Override
                                public void onFinish() {
                                    activity.addSymbolSourceAndLayerToMap();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            activity.map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLocation.latitude(), currentLocation.longitude())), new MapboxMap.CancelableCallback() {
                                                @Override
                                                public void onCancel() {
                                                }

                                                @Override
                                                public void onFinish() {
                                                }
                                            });
                                        }
                                    }, 1);
                                }
                            });
                        }
                    });
                }

                if (activity.map != null && result.getLastLocation() != null)
                    activity.map.getLocationComponent().forceLocationUpdate(result.getLastLocation());
            }
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location can not be captured
         *
         * @param exception the exception message
         */
        @Override
        public void onFailure(@NonNull Exception exception) {
            Log.d("LocationChangeActivity", exception.getLocalizedMessage());
            MapirNavigationActivity activity = activityWeakReference.get();
            if (activity != null) {
                Toast.makeText(activity, exception.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void executeRouteRequest(final ResponseListener listener) {
        RouteRequest requestBody = new RouteRequest.Builder(
                35.732483, 51.422414,
                35.722580, 51.451678,
                RouteType.DRIVING
        )
                .routeOverView(RouteOverView.FULL)
                .build();

        client.newCall(
                new Request.Builder()
                        .url(UrlBuilder.routeUrl(requestBody).build().toString())
                        .addHeader("x-api-key", MapirNavigation.getApiKey())
                        .addHeader("MapIr-SDK", MapirNavigation.getUserAgent())
                        .get()
                        .build()
        ).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();

                new Handler(Looper.getMainLooper()).post(
                        new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(new MapirError("Client Connection Error", 1000));
                            }
                        }
                );
            }

            @Override
            public void onResponse(Call call, final Response response) {
                new Handler(Looper.getMainLooper()).post(
                        new Runnable() {
                            @Override
                            public void run() {
                                if (!response.isSuccessful())
                                    listener.onError(createError(response.code(), "sound_route"));
                                else {
                                    try {
                                        MapirResponse tempResponse = createResponse(response.body(), "sound_route");
                                        listener.onSuccess(tempResponse);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                );
            }
        });
    }
}