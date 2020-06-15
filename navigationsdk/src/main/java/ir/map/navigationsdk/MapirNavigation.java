package ir.map.navigationsdk;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;

import ir.map.sdk_map.Mapir;

public final class MapirNavigation {

    private static String _apiKey;
    private static String userAgent;

    public static void init(@NonNull Context context, @NonNull String apiKey) {
        if (apiKey != null && !apiKey.isEmpty()) {
            if (context != null) {
                _apiKey = apiKey;
                userAgent = userAgentString(context);

                Mapir.getInstance(context, apiKey);
            } else throw new RuntimeException("Provide nonNull Context.");
        } else
            throw new RuntimeException("No APIKEY Provided, Please visit https://corp.map.ir/registration/ to get new APIKEY");
    }

    private static String userAgentString(@NonNull Context context) {
        return String.format("Android/%s(%s)(%s)-ServiceSdk/%s-%s(%s)/%s-(%s)",
                Build.VERSION.SDK_INT,
                Build.VERSION.RELEASE,
                Build.CPU_ABI,
                BuildConfig.MAPIR_NAVIGATION_SDK_VERSION,
                context.getPackageName(),
                getApplicationName(context),
                Build.BRAND,
                Build.MODEL
        );
    }

    static String getApiKey() {
        return _apiKey;
    }

    static String getUserAgent() {
        return userAgent;
    }

    private static String getApplicationName(Context context) {
        return context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
    }
}
