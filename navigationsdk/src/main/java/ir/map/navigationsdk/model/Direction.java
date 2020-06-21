package ir.map.navigationsdk.model;

import java.util.List;

import ir.map.navigationsdk.R;

public class Direction {

    private String name;
    private String to;
    private String order;
    private String orderType;
    private Integer imageSourceId;
    private List<String> instructions;

    public Direction(String name, String to, String order, String orderType, List<String> instructions) {
        this.name = name;
        this.to = to;
        this.order = order;
        this.orderType = orderType;
        this.instructions = instructions;

        switch (orderType) {
            case "1": imageSourceId = R.drawable.bottom_sheet_background;
            case "2": imageSourceId = R.drawable.ic_compass;
            case "3": imageSourceId = R.drawable.ic_logo;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public List<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
