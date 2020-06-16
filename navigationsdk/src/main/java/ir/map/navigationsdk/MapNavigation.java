package ir.map.navigationsdk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.mapbox.mapboxsdk.geometry.LatLng;

import ir.map.servicesdk.response.RouteResponse;
import okhttp3.OkHttpClient;

/**
 * MapNavigation prepares map.ir navigation
 *
 * @author Morteza Hosseini
 * @version 1.0.0
 * @since 2020-06-15
 */
public class MapNavigation {

    //region Initialize
    private OkHttpClient client = new OkHttpClient();
    private LatLng origin;
    private LatLng destination;
    private RouteResponse routeResponse;
    //endregion Initialize

    //region Methods
    public void setOrigin(double latitude, double longitude) {
        origin = new LatLng(latitude, longitude);
    }

    public void setDestination(double latitude, double longitude) {
        destination = new LatLng(latitude, longitude);
    }

    public void setRoute(RouteResponse routeResponse) {
        this.routeResponse = routeResponse;
    }

    public void startNavigation(Context uiContext) {
        Intent mIntent = new Intent(uiContext, MapirNavigationActivity.class);
        Bundle mBundle = new Bundle();

        try {
            Gson gson = new Gson();
            String originJson = gson.toJson(origin);
            String destinationJson = gson.toJson(destination);
            String routeResponseJson = gson.toJson(routeResponse);

            mBundle.putString("origin", originJson);
            mBundle.putString("destination", destinationJson);
            mBundle.putString("routeResponse", routeResponseJson);
            mIntent.putExtras(mBundle);

            uiContext.startActivity(mIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //endregion Methods
}
