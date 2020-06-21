package ir.map.navigationsdk.model.inner;

public class VoiceInstruction {

    private Double distanceAlongGeometry;
    private String announcement;
    private String ssmlAnnouncement;

    public VoiceInstruction(Double distanceAlongGeometry, String announcement, String ssmlAnnouncement) {
        this.distanceAlongGeometry = distanceAlongGeometry;
        this.announcement = announcement;
        this.ssmlAnnouncement = ssmlAnnouncement;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    public Double getDistanceAlongGeometry() {
        return distanceAlongGeometry;
    }

    public void setDistanceAlongGeometry(Double distanceAlongGeometry) {
        this.distanceAlongGeometry = distanceAlongGeometry;
    }

    public String getSsmlAnnouncement() {
        return ssmlAnnouncement;
    }

    public void setSsmlAnnouncement(String ssmlAnnouncement) {
        this.ssmlAnnouncement = ssmlAnnouncement;
    }
}
