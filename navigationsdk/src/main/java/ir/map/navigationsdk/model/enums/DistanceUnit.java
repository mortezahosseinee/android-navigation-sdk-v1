package ir.map.navigationsdk.model.enums;

public enum DistanceUnit {
    KILOMETER("km"),
    METER("m");

    private String value;

    DistanceUnit(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
