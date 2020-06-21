package ir.map.navigationsdk.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import ir.map.navigationsdk.model.base.MapirResponse;
import ir.map.navigationsdk.model.inner.BannerInstruction;
import ir.map.navigationsdk.model.inner.Component;
import ir.map.navigationsdk.model.inner.Intersection;
import ir.map.navigationsdk.model.inner.Leg;
import ir.map.navigationsdk.model.inner.Maneuver;
import ir.map.navigationsdk.model.inner.Primary;
import ir.map.navigationsdk.model.inner.RouteItem;
import ir.map.navigationsdk.model.inner.Step;
import ir.map.navigationsdk.model.inner.VoiceInstruction;
import ir.map.navigationsdk.model.inner.WayPoint;

public class RouteResponse extends MapirResponse {

    private List<RouteItem> routes;
    private List<WayPoint> wayPoints;

    private RouteResponse(List<RouteItem> routes, List<WayPoint> wayPoints) {
        this.routes = routes;
        this.wayPoints = wayPoints;
    }

    public List<RouteItem> getRoutes() {
        return routes;
    }

    public List<WayPoint> getWayPoints() {
        return wayPoints;
    }

    public static MapirResponse createRouteResponse(String data) {
        try {
            JSONObject tempData = new JSONObject(data);

            JSONArray tempRouteItems = new JSONArray(tempData.get("routes").toString());
            List<RouteItem> routeItems = new ArrayList<>();
            for (int i = 0; i < tempRouteItems.length(); i++) {
                JSONObject tempRouteItem = new JSONObject(tempRouteItems.get(i).toString());

                //region legs
                JSONArray tempLegs = new JSONArray(tempRouteItem.get("legs").toString());
                List<Leg> legs = new ArrayList<>();
                for (int j = 0; j < tempLegs.length(); j++) {
                    JSONObject tempLeg = new JSONObject(tempLegs.get(j).toString());

                    //region steps
                    JSONArray tempSteps = new JSONArray(tempLeg.get("steps").toString());
                    List<Step> steps = new ArrayList<>();
                    for (int k = 0; k < tempSteps.length(); k++) {
                        JSONObject tempStep = new JSONObject(tempSteps.get(k).toString());

                        //region intersection
                        JSONArray tempIntersections = new JSONArray(tempStep.get("intersections").toString());
                        List<Intersection> intersections = new ArrayList<>();
                        for (int l = 0; l < tempIntersections.length(); l++) {
                            JSONObject tempIntersection = new JSONObject(tempIntersections.get(l).toString());

                            JSONArray tempEntry = new JSONArray(tempIntersection.get("entry").toString());
                            List<Boolean> entries = new ArrayList<>();
                            for (int m = 0; m < tempEntry.length(); m++)
                                entries.add(tempEntry.getBoolean(m));

                            JSONArray tempBearing = new JSONArray(tempIntersection.get("bearings").toString());
                            List<Integer> bearings = new ArrayList<>();
                            for (int m = 0; m < tempBearing.length(); m++)
                                bearings.add(tempBearing.getInt(m));

                            JSONArray tempLocation = new JSONArray(tempIntersection.get("location").toString());
                            List<Double> location = new ArrayList<>();
                            for (int m = 0; m < tempLocation.length(); m++)
                                location.add(tempLocation.getDouble(m));

                            int in = -1;
                            int out = -1;
                            if (tempIntersection.has("in"))
                                in = tempIntersection.getInt("in");

                            if (tempIntersection.has("out"))
                                out = tempIntersection.getInt("out");

                            Intersection item = new Intersection(
                                    in,
                                    out,
                                    entries,
                                    bearings,
                                    location
                            );

                            intersections.add(item);
                        }
                        //endregion intersection

                        //region maneuver
                        JSONObject tempManeuver = new JSONObject(tempStep.getString("maneuver"));

                        JSONArray tempLocationManeuver = new JSONArray(tempManeuver.getString("location"));
                        List<Double> location = new ArrayList<>();
                        for (int m = 0; m < tempLocationManeuver.length(); m++)
                            location.add(tempLocationManeuver.getDouble(m));

                        String modifier = null;
                        if (tempManeuver.has("modifier"))
                            modifier = tempManeuver.getString("modifier");

                        Maneuver tempManeuverObject = new Maneuver(
                                tempManeuver.getInt("bearing_after"),
                                location,
                                tempManeuver.getInt("bearing_before"),
                                tempManeuver.getString("type"),
                                modifier,
                                ""
                        );
                        //endregion maneuver

                        Step item = new Step(
                                intersections,
                                tempStep.getString("driving_side"),
                                tempStep.getString("geometry"),
                                tempStep.getString("mode"),
                                tempStep.getInt("duration"),
                                tempManeuverObject,
                                tempStep.getInt("weight"),
                                tempStep.getInt("distance"),
                                tempStep.getString("name")
                        );

                        steps.add(item);
                    }
                    //endregion steps

                    Leg item = new Leg(
                            tempLeg.getDouble("distance"),
                            tempLeg.getDouble("duration"),
                            tempLeg.getString("summary"),
                            tempLeg.getDouble("weight"),
                            steps
                    );

                    legs.add(item);
                }
                //endregion legs

                RouteItem item = new RouteItem(
                        tempRouteItem.getString("geometry"),
                        tempRouteItem.getDouble("distance"),
                        tempRouteItem.getDouble("duration"),
                        tempRouteItem.getString("weight_name"),
                        tempRouteItem.getDouble("weight"),
                        legs
                );

                routeItems.add(item);
            }

            JSONArray tempWayPoints = new JSONArray(tempData.get("waypoints").toString());
            List<WayPoint> wayPoints = new ArrayList<>();
            for (int i = 0; i < tempWayPoints.length(); i++) {
                JSONObject tempWayPoint = new JSONObject(tempWayPoints.get(i).toString());

                JSONArray tempLocation = new JSONArray(tempWayPoint.getString("location"));

                WayPoint item = new WayPoint(
                        tempWayPoint.getString("hint"),
                        tempWayPoint.getDouble("distance"),
                        tempWayPoint.getString("name"),
                        tempLocation.getDouble(1),
                        tempLocation.getDouble(0)
                );

                wayPoints.add(item);
            }

            return new RouteResponse(
                    routeItems,
                    wayPoints
            );
        } catch (Exception e) {
            return null;
        }
    }

    public static MapirResponse createSoundRouteResponse(String data) {
        try {
            JSONObject tempData = new JSONObject(data);

            JSONArray tempRouteItems = new JSONArray(tempData.get("routes").toString());
            List<RouteItem> routeItems = new ArrayList<>();
            for (int i = 0; i < tempRouteItems.length(); i++) {
                JSONObject tempRouteItem = new JSONObject(tempRouteItems.get(i).toString());

                //region legs
                JSONArray tempLegs = new JSONArray(tempRouteItem.get("legs").toString());
                List<Leg> legs = new ArrayList<>();
                for (int j = 0; j < tempLegs.length(); j++) {
                    JSONObject tempLeg = new JSONObject(tempLegs.get(j).toString());

                    //region steps
                    JSONArray tempSteps = new JSONArray(tempLeg.get("steps").toString());
                    List<Step> steps = new ArrayList<>();
                    for (int k = 0; k < tempSteps.length(); k++) {
                        JSONObject tempStep = new JSONObject(tempSteps.get(k).toString());

                        //region intersection
                        JSONArray tempIntersections = new JSONArray(tempStep.get("intersections").toString());
                        List<Intersection> intersections = new ArrayList<>();
                        for (int l = 0; l < tempIntersections.length(); l++) {
                            JSONObject tempIntersection = new JSONObject(tempIntersections.get(l).toString());

                            JSONArray tempEntry = new JSONArray(tempIntersection.get("entry").toString());
                            List<Boolean> entries = new ArrayList<>();
                            for (int m = 0; m < tempEntry.length(); m++)
                                entries.add(tempEntry.getBoolean(m));

                            JSONArray tempBearing = new JSONArray(tempIntersection.get("bearings").toString());
                            List<Integer> bearings = new ArrayList<>();
                            for (int m = 0; m < tempBearing.length(); m++)
                                bearings.add(tempBearing.getInt(m));

                            JSONArray tempLocation = new JSONArray(tempIntersection.get("location").toString());
                            List<Double> location = new ArrayList<>();
                            for (int m = 0; m < tempLocation.length(); m++)
                                location.add(tempLocation.getDouble(m));

                            int in = -1;
                            int out = -1;
                            if (tempIntersection.has("in"))
                                in = tempIntersection.getInt("in");

                            if (tempIntersection.has("out"))
                                out = tempIntersection.getInt("out");

                            Intersection item = new Intersection(
                                    in,
                                    out,
                                    entries,
                                    bearings,
                                    location
                            );

                            intersections.add(item);
                        }
                        //endregion intersection

                        //region bannerInstruction
                        JSONArray tempBannerInstructions = new JSONArray(tempStep.get("bannerInstructions").toString());
                        List<BannerInstruction> bannerInstructions = new ArrayList<>();
                        for (int l = 0; l < tempBannerInstructions.length(); l++) {
                            JSONObject tempBannerInstruction = new JSONObject(tempBannerInstructions.get(l).toString());

                            Double distanceAlongGeometry = tempBannerInstruction.getDouble("distanceAlongGeometry");
                            String secondary = tempBannerInstruction.getString("secondary");

                            JSONObject tempPrimary = new JSONObject(tempBannerInstruction.getString("primary"));

                            String text = tempPrimary.getString("text");
                            String type = tempPrimary.getString("type");
                            String modifier = tempPrimary.getString("modifier");

                            JSONArray tempComponents = new JSONArray(tempPrimary.get("components").toString());
                            List<Component> components = new ArrayList<>();
                            for (int m = 0; m < tempComponents.length(); m++) {
                                JSONObject tempComponent = new JSONObject(tempComponents.get(m).toString());

                                String componentText = tempComponent.getString("text");
                                String componentType = tempComponent.getString("type");

                                Component component = new Component(
                                        componentType,
                                        componentText
                                );

                                components.add(component);
                            }

                            Primary primary = new Primary(
                                    components,
                                    modifier,
                                    text,
                                    type
                            );

                            BannerInstruction item = new BannerInstruction(
                                    distanceAlongGeometry,
                                    primary,
                                    secondary
                            );

                            bannerInstructions.add(item);
                        }
                        //endregion bannerInstruction

                        //region voiceInstruction
                        JSONArray tempVoiceInstructions = new JSONArray(tempStep.get("voiceInstructions").toString());
                        List<VoiceInstruction> voiceInstructions = new ArrayList<>();
                        for (int l = 0; l < tempVoiceInstructions.length(); l++) {
                            JSONObject tempVoiceInstruction = new JSONObject(tempVoiceInstructions.get(l).toString());

                            Double distanceAlongGeometry = tempVoiceInstruction.getDouble("distanceAlongGeometry");
                            String announcement = tempVoiceInstruction.getString("announcement");
                            String ssmlAnnouncement = tempVoiceInstruction.getString("ssmlAnnouncement");

                            VoiceInstruction item = new VoiceInstruction(
                                    distanceAlongGeometry,
                                    announcement,
                                    ssmlAnnouncement
                            );

                            voiceInstructions.add(item);
                        }
                        //endregion voiceInstruction

                        //region maneuver
                        JSONObject tempManeuver = new JSONObject(tempStep.getString("maneuver"));

                        JSONArray tempLocationManeuver = new JSONArray(tempManeuver.getString("location"));
                        List<Double> location = new ArrayList<>();
                        for (int m = 0; m < tempLocationManeuver.length(); m++)
                            location.add(tempLocationManeuver.getDouble(m));

                        String modifier = null;
                        if (tempManeuver.has("modifier"))
                            modifier = tempManeuver.getString("modifier");

                        Maneuver tempManeuverObject = new Maneuver(
                                tempManeuver.getInt("bearing_after"),
                                location,
                                tempManeuver.getInt("bearing_before"),
                                tempManeuver.getString("type"),
                                modifier,
                                tempManeuver.getString("instruction")
                        );
                        //endregion maneuver

                        Step item = new Step(
                                intersections,
                                bannerInstructions,
                                voiceInstructions,
                                tempStep.getString("driving_side"),
                                tempStep.getString("geometry"),
                                tempStep.getString("mode"),
                                tempStep.getInt("duration"),
                                tempManeuverObject,
                                tempStep.getInt("weight"),
                                tempStep.getInt("distance"),
                                tempStep.getString("name")
                        );

                        steps.add(item);
                    }
                    //endregion steps

                    Leg item = new Leg(
                            tempLeg.getDouble("distance"),
                            tempLeg.getDouble("duration"),
                            tempLeg.getString("summary"),
                            tempLeg.getDouble("weight"),
                            steps
                    );

                    legs.add(item);
                }
                //endregion legs

                RouteItem item = new RouteItem(
                        tempRouteItem.getString("geometry"),
                        tempRouteItem.getDouble("distance"),
                        tempRouteItem.getDouble("duration"),
                        tempRouteItem.getString("weight_name"),
                        tempRouteItem.getDouble("weight"),
                        legs
                );

                routeItems.add(item);
            }

            JSONArray tempWayPoints = new JSONArray(tempData.get("waypoints").toString());
            List<WayPoint> wayPoints = new ArrayList<>();
            for (int i = 0; i < tempWayPoints.length(); i++) {
                JSONObject tempWayPoint = new JSONObject(tempWayPoints.get(i).toString());

                JSONArray tempLocation = new JSONArray(tempWayPoint.getString("location"));

                WayPoint item = new WayPoint(
                        tempWayPoint.getString("hint"),
                        tempWayPoint.getDouble("distance"),
                        tempWayPoint.getString("name"),
                        tempLocation.getDouble(1),
                        tempLocation.getDouble(0)
                );

                wayPoints.add(item);
            }

            return new RouteResponse(
                    routeItems,
                    wayPoints
            );
        } catch (Exception e) {
            return null;
        }
    }
}