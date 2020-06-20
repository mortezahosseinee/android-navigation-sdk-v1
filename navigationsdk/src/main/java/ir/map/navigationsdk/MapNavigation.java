package ir.map.navigationsdk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.io.IOException;

import ir.map.navigationsdk.model.RouteResponse;
import ir.map.navigationsdk.model.base.MapirError;
import ir.map.navigationsdk.model.base.MapirResponse;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static ir.map.navigationsdk.HttpUtils.createError;
import static ir.map.navigationsdk.HttpUtils.createResponse;
import static ir.map.navigationsdk.UrlBuilder.ROUTE;

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

    /**
     * This method is used to get route response.
     *
     * @param requestBody This is object of type {@link RouteRequest}
     * @param listener    This is callback we use to return response
     */
    public void route(RouteRequest requestBody, ResponseListener<RouteResponse> listener) {
        execute(listener, UrlBuilder.routeUrl(requestBody), ROUTE);
    }

    private void execute(final ResponseListener listener, HttpUrl.Builder urlBuilder, final String api) {
        client.newCall(
                new Request.Builder()
                        .url(urlBuilder.build().toString())
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
                                    listener.onError(createError(response.code(), api));
                                else {
                                    try {
                                        MapirResponse tempResponse = createResponse(response.body(), api);
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
    //endregion Methods
}
