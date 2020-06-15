package ir.map.sdksample;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ir.map.navigationsdk.MapNavigation;
import ir.map.servicesdk.MapService;
import ir.map.servicesdk.ResponseListener;
import ir.map.servicesdk.enums.RouteType;
import ir.map.servicesdk.model.base.MapirError;
import ir.map.servicesdk.request.RouteRequest;
import ir.map.servicesdk.response.RouteResponse;

public class MainActivity extends AppCompatActivity {

//    MapboxMap map;
//    Style mapStyle;
//    MapView mapView;

    // variables for adding location layer
//    private PermissionsManager permissionsManager;
//    private LocationComponent locationComponent;
//
//    private DirectionsRoute selectedRoute;
//
//    LatLng destination = new LatLng(35.743168, 51.388636);

    private MapNavigation mapNavigation = new MapNavigation();
    private MapService mapService = new MapService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapNavigation.setOrigin(35.740312, 51.422625);
        mapNavigation.setDestination(35.722580, 51.451678);

        RouteRequest requestBody = new RouteRequest.Builder(
                35.732483, 51.422414,
                35.722580, 51.451678,
                RouteType.DRIVING
        ).build();

        MapService mapService = new MapService();
        mapService.route(requestBody, new ResponseListener<RouteResponse>() {
            @Override
            public void onSuccess(RouteResponse response) {
                Toast.makeText(MainActivity.this, "پاسخ مسیریابی دریافت شد", Toast.LENGTH_SHORT).show();

                ObjectMapper mapper = new ObjectMapper();
                try {
                    String json = mapper.writeValueAsString(response);

                    mapNavigation.setRoute(response);
                    mapNavigation.startNavigation(MainActivity.this);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(MapirError error) {
                Toast.makeText(MainActivity.this, "مشکلی در مسیریابی پیش آمده", Toast.LENGTH_SHORT).show();
            }
        });

//        mapView = findViewById(R.id.map_view);
//        mapView.onCreate(savedInstanceState);
//        mapView.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(@NonNull MapboxMap mapboxMap) {
//                map = mapboxMap;
//                map.setStyle(new Style.Builder().fromUri(MapirStyle.MAIN_MOBILE_VECTOR_STYLE), new Style.OnStyleLoaded() {
//                    @Override
//                    public void onStyleLoaded(@NonNull Style style) {
//                        mapStyle = style;
//
//                        enableLocationComponent();
//
//                        addDestinationIconSymbolLayer();
//
//                        getRoute();
//                    }
//                });
//            }
//        });
    }

//    private void addDestinationIconSymbolLayer() {
//        mapStyle.addImage("destination-icon-id", BitmapFactory.decodeResource(this.getResources(), R.drawable.mapbox_marker_icon_default));
//        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
//        mapStyle.addSource(geoJsonSource);
//        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
//        destinationSymbolLayer.withProperties(
//                iconImage("destination-icon-id"),
//                iconAllowOverlap(true),
//                iconIgnorePlacement(true)
//        );
//        mapStyle.addLayer(destinationSymbolLayer);
//    }

//    private void getRoute() {
//        LatLng origin = new LatLng(locationComponent.getLastKnownLocation().getLatitude(), locationComponent.getLastKnownLocation().getLongitude());
//
//        GeoJsonSource source = mapStyle.getSourceAs("destination-source-id");
//        if (source != null)
//            source.setGeoJson(Feature.fromGeometry(Point.fromLngLat(destination.getLongitude(), destination.getLatitude())));
//
//        RouteRequest requestBody = new RouteRequest.Builder(
//                origin.getLatitude(), origin.getLongitude(),
//                destination.getLatitude(), destination.getLongitude(),
//                RouteType.DRIVING
//        ).build();
//
//        MapService mapService = new MapService();
//        mapService.route(requestBody, new ResponseListener<RouteResponse>() {
//            @Override
//            public void onSuccess(RouteResponse response) {
//                Toast.makeText(MainActivity.this, "پاسخ مسیریابی دریافت شد", Toast.LENGTH_SHORT).show();
//
//                ObjectMapper mapper = new ObjectMapper();
//                try {
//                    String json = mapper.writeValueAsString(response.getRoutes().get(0));
//
//                    selectedRoute = DirectionsRoute.fromJson(json);
//                    navigate(destination);
//                } catch (JsonProcessingException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onError(MapirError error) {
//                Toast.makeText(MainActivity.this, "مشکلی در مسیریابی پیش آمده", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

//    private void navigate(LatLng destination) {
//        CameraPosition cameraPosition;
//        CameraPosition.Builder cameraPositionBuilder = new CameraPosition.Builder().zoom(14.0);
//        cameraPositionBuilder.target(destination);
//        cameraPosition = cameraPositionBuilder.build();
//
//        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
//                .initialMapCameraPosition(cameraPosition)
//                .directionsRoute(selectedRoute)
//                .build();
//        NavigationLauncher.startNavigation(this, options);
//    }

//    @SuppressWarnings({"MissingPermission"})
//    private void enableLocationComponent() {
//        // Check if permissions are enabled and if not request
//        if (PermissionsManager.areLocationPermissionsGranted(this)) {
//            // Activate the MapboxMap LocationComponent to show user location
//            // Adding in LocationComponentOptions is also an optional parameter
//            locationComponent = map.getLocationComponent();
//            locationComponent.activateLocationComponent(this, mapStyle);
//            locationComponent.setLocationComponentEnabled(true);
//            // Set the component's camera mode
//            locationComponent.setCameraMode(CameraMode.TRACKING);
//        } else {
//            permissionsManager = new PermissionsManager(this);
//            permissionsManager.requestLocationPermissions(this);
//        }
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }

//    @Override
//    public void onExplanationNeeded(List<String> permissionsToExplain) {
//        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    public void onPermissionResult(boolean granted) {
//        if (granted)
//            enableLocationComponent();
//        else {
//            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
//            finish();
//        }
//    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        mapView.onStart();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        mapView.onResume();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        mapView.onPause();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        mapView.onStop();
//    }
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//        mapView.onLowMemory();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mapView.onDestroy();
//    }
//
//    @Override
//    protected void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        mapView.onSaveInstanceState(outState);
//    }
//
//    @Override
//    public void onPointerCaptureChanged(boolean hasCapture) {
//    }
}