package ir.map.navigationsdk;

import ir.map.navigationsdk.model.base.MapirError;

public interface ResponseListener<T> {
    void onSuccess(T response);

    void onError(MapirError error);
}
