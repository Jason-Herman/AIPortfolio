package edu.gatech.health.objects;

/**
 * Created by Trevoirs on 3/25/2016.
 *
 * Component.class
 * Subelement of Observation
 */
public class Component {
    private String system;
    private String code;
    private String display;
    private String value;
    private String unit;

    public Component(){
        this.system = "";
        this.code = "";
        this.display = "";
        this.value = "";
        this.unit = "";
    }


    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
