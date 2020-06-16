package ir.map.navigationsdk;

import java.io.IOException;

import ir.map.navigationsdk.model.base.MapirError;
import ir.map.navigationsdk.model.base.MapirResponse;
import okhttp3.ResponseBody;

import static ir.map.navigationsdk.UrlBuilder.ROUTE;
import static ir.map.navigationsdk.model.RouteResponse.createRouteResponse;

public class HttpUtils {
    static MapirResponse createResponse(ResponseBody body, String api) throws IOException {
        switch (api) {
            case ROUTE:
                return createRouteResponse(body.string());
        }

        return null;
    }

    static MapirError createError(int statusCode, String api) {
        if (statusCode == 400)
            return new MapirError(api + " api: bad request.", 400);
        else if (statusCode == 401)
            return new MapirError(api + " api: get valid apiKey from https://corp.map.ir", 401);
        else if (statusCode == 402)
            return new MapirError(api + " api: bad request.", 402);
        else if (statusCode == 404)
            return new MapirError(api + " api: not found response.", 404);

        return null;
    }
}
