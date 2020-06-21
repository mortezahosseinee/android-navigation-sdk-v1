package ir.map.navigationsdk.model.inner;

import java.util.List;

public class Primary {
    private List<Component> components;
    private String modifier;
    private String text;
    private String type;

    public Primary(List<Component> components, String modifier, String text, String type) {
        this.components = components;
        this.modifier = modifier;
        this.text = text;
        this.type = type;
    }

    public List<Component> getComponents() {
        return components;
    }

    public void setComponents(List<Component> components) {
        this.components = components;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
