package ir.map.navigationsdk;

import okhttp3.HttpUrl;

public class UrlBuilder {

    //region const
    private static String BASE_URL = "https://map.ir/";

    static final String ROUTE = "sound/routes/";
    //endregion const

    //region methods
    static HttpUrl.Builder routeUrl(RouteRequest requestBody) {
        String routeTypeEndPoint;

        if (requestBody.getRouteType().equals("route"))
            routeTypeEndPoint = (requestBody.hasRoutePlan() ? requestBody.getRoutePlan() : requestBody.getRouteType());
        else
            routeTypeEndPoint = requestBody.getRouteType();

        StringBuilder otherDestinations = new StringBuilder();

        if (requestBody.hasOtherDestinations()) {
            for (int i = 0; i < requestBody.getOtherDestinations().size(); i++) {
                otherDestinations
                        .append(";")
                        .append(requestBody.getOtherDestinations().get(i).getLongitude())
                        .append(",")
                        .append(requestBody.getOtherDestinations().get(i).getLatitude());
            }
        }

        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL
                + ROUTE
                + routeTypeEndPoint
                + "/v1/driving"
                + "/" + requestBody.getStartLongitude()
                + "," + requestBody.getStartLatitude()
                + ";" + requestBody.getEndLongitude()
                + "," + requestBody.getEndLatitude()
                + otherDestinations.toString()
        )
                .newBuilder()
                .addQueryParameter("alternatives", String.valueOf(requestBody.isAlternatives()))
                .addQueryParameter("steps", String.valueOf(requestBody.needSteps()));

        if (requestBody.hasRouteOverView())
            urlBuilder.addQueryParameter("overview", requestBody.getRouteOverView());

        return urlBuilder;
    }
    //endregion methods
}
