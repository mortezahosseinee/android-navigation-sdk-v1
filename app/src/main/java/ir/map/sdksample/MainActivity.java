package ir.map.sdksample;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ir.map.navigationsdk.MapNavigation;
import ir.map.servicesdk.MapService;
import ir.map.servicesdk.ResponseListener;
import ir.map.servicesdk.enums.RouteOverView;
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
        )
                .routeOverView(RouteOverView.FULL)
                .build();

        MapService mapService = new MapService();
        mapService.route(requestBody, new ResponseListener<RouteResponse>() {
            @Override
            public void onSuccess(RouteResponse response) {
                Toast.makeText(MainActivity.this, "پاسخ مسیریابی دریافت شد", Toast.LENGTH_SHORT).show();

                mapNavigation.setRoute(response);
                mapNavigation.startNavigation(MainActivity.this);
            }

            @Override
            public void onError(MapirError error) {
                Toast.makeText(MainActivity.this, "مشکلی در مسیریابی پیش آمده", Toast.LENGTH_SHORT).show();
            }
        });

    }
}