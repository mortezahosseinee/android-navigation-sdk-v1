package ir.map.navigationsdk.model.enums;

public enum RouteType {
    DRIVING("route"),
    BICYCLE("bicycle"),
    WALKING("foot");

    private String value;

    RouteType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
