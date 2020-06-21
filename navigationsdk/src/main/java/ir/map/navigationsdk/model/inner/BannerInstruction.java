package ir.map.navigationsdk.model.inner;

public class BannerInstruction {

    private Double distanceAlongGeometry;
    private Primary primary;
    private String secondary;

    public BannerInstruction(Double distanceAlongGeometry, Primary primary, String secondary) {
        this.distanceAlongGeometry = distanceAlongGeometry;
        this.primary = primary;
        this.secondary = secondary;
    }

    public Double getDistanceAlongGeometry() {
        return distanceAlongGeometry;
    }

    public void setDistanceAlongGeometry(Double distanceAlongGeometry) {
        this.distanceAlongGeometry = distanceAlongGeometry;
    }

    public Primary getPrimary() {
        return primary;
    }

    public void setPrimary(Primary primary) {
        this.primary = primary;
    }

    public String getSecondary() {
        return secondary;
    }

    public void setSecondary(String secondary) {
        this.secondary = secondary;
    }
}
