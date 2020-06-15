package ir.map.navigationsdk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.mapbox.mapboxsdk.geometry.LatLng;

import ir.map.servicesdk.response.RouteResponse;
import okhttp3.OkHttpClient;

import static ir.map.navigationsdk.model.RouteResponse.createRouteResponse;

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

//    private void execute(final ResponseListener listener, HttpUrl.Builder urlBuilder, final String api) {
//        client.newCall(
//                new Request.Builder()
//                        .url(urlBuilder.build().toString())
//                        .addHeader("x-api-key", MapirNavigation.getApiKey())
//                        .addHeader("MapIr-SDK", MapirNavigation.getUserAgent())
//                        .get()
//                        .build()
//        ).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                call.cancel();
//
//                new Handler(Looper.getMainLooper()).post(
//                        new Runnable() {
//                            @Override
//                            public void run() {
//                                listener.onError(new MapirError("Client Connection Error", 1000));
//                            }
//                        }
//                );
//            }
//
//            @Override
//            public void onResponse(Call call, final Response response) {
//                new Handler(Looper.getMainLooper()).post(
//                        new Runnable() {
//                            @Override
//                            public void run() {
////                                if (!response.isSuccessful())
////                                    listener.onError(createError(response.code(), api));
////                                else {
////                                    try {
////                                        MapirResponse tempResponse = createResponse(response.body(), api);
////
////                                        if (tempResponse instanceof StaticMapResponse) {
////                                            new BitmapWorkerTask(listener).execute(((StaticMapResponse) tempResponse).getData());
////                                        } else
////                                            listener.onSuccess(tempResponse);
////                                    } catch (IOException e) {
////                                        e.printStackTrace();
////                                    }
////                                }
//                            }
//                        }
//                );
//            }
//        });
//    }
    //endregion Methods
}
