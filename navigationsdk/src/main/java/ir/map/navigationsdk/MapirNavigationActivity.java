package ir.map.navigationsdk;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
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
import com.mapbox.mapboxsdk.plugins.annotation.LineOptions;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;

import ir.map.sdk_map.MapirStyle;
import ir.map.sdk_map.maps.MapView;
import ir.map.servicesdk.response.RouteResponse;

public class MapirNavigationActivity extends AppCompatActivity {

    MapboxMap map;
    Style mapStyle;
    MapView mapView;

    private LatLng origin;
    private LatLng destination;

    private LocationComponent locationComponent = null;

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
    }

    void showRouteOnMap(String geometry) {
        LineManager lineManager = new LineManager(mapView, map, mapStyle);
        LineString routeLine = LineString.fromPolyline(geometry, 5);
        LineOptions lineOptions = new LineOptions()
                .withGeometry(routeLine)
                .withLineColor("#ff5252")
                .withLineWidth(5f);
        lineManager.create(lineOptions);

        origin = new LatLng(routeLine.coordinates().get(0).latitude(), routeLine.coordinates().get(0).longitude());

//        addSymbolSourceAndLayerToMap();

        map.easeCamera(CameraUpdateFactory.newLatLngZoom(origin, 18), 100, new MapboxMap.CancelableCallback() {
            @Override
            public void onCancel() {
            }

            @Override
            public void onFinish() {
                map.animateCamera(CameraUpdateFactory.tiltTo(60), new MapboxMap.CancelableCallback() {
                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onFinish() {
                        map.animateCamera(CameraUpdateFactory.bearingTo(90));
                    }
                });
            }
        });
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

//    private void addSymbolSourceAndLayerToMap() {
//        // Add source to map
//        List<Feature> samplePointsFeatures = new ArrayList<>();
//        Feature sampleFeature = Feature.fromGeometry(Point.fromLngLat(origin.getLongitude(), origin.getLatitude()));
//        samplePointsFeatures.add(sampleFeature);
//
//        FeatureCollection featureCollection = FeatureCollection.fromFeatures(samplePointsFeatures);
//        GeoJsonSource geoJsonSource = new GeoJsonSource("sample_source_id", featureCollection);
//        mapStyle.addSource(geoJsonSource);
//
//        // Add image to map
//        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.map_marker);
//        mapStyle.addImage("sample_image_id", icon);
//
//        // Add layer to map
//        SymbolLayer symbolLayer = new SymbolLayer("sample_layer_id", "sample_source_id");
//        symbolLayer.setProperties(
//                PropertyFactory.iconImage("sample_image_id"),
//                PropertyFactory.iconSize(1.5f),
//                PropertyFactory.iconOpacity(.8f),
//                PropertyFactory.iconRotate(locationComponent != null ? locationComponent.getLastKnownLocation().getBearing() : 0)
//        );
//        mapStyle.addLayer(symbolLayer);
//    }

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
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}