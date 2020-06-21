package ir.map.sdksample;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ir.map.navigationsdk.MapNavigation;
import ir.map.navigationsdk.ResponseListener;
import ir.map.navigationsdk.RouteRequest;
import ir.map.navigationsdk.model.RouteResponse;
import ir.map.navigationsdk.model.base.MapirError;
import ir.map.navigationsdk.model.enums.RouteOverView;
import ir.map.navigationsdk.model.enums.RouteType;

public class MainActivity extends AppCompatActivity {

    private MapNavigation mapNavigation = new MapNavigation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapNavigation.setOrigin(35.732541, 51.422680);
        mapNavigation.setDestination(35.722580, 51.451678);

        RouteRequest requestBody = new RouteRequest.Builder(
                35.732541, 51.422680,
                35.722580, 51.451678,
                RouteType.DRIVING
        )
                .routeOverView(RouteOverView.FULL)
                .build();

        mapNavigation.route(requestBody, new ResponseListener<RouteResponse>() {
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