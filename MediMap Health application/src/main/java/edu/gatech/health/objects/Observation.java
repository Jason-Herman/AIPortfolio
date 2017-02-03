package edu.gatech.health.objects;

import edu.gatech.health.helpers.*;

import java.util.List;

/**
 * Created by Trevoris on 3/25/2016.
 * Observation.java
 */
public class Observation implements Comparable<Observation>{

    private String observationId;
    private String status;
    private String effectiveDate;
    private String effectiveDateDisplay;
    private List<Component> components;
    private String performerReference;
    private String performerName;
    private String categorycode;

    public String getObservationId() {
        return observationId;
    }

    public void setObservationId(String observationId) {
        this.observationId = observationId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public List<Component> getComponents() {
        return components;
    }

    public void setComponents(List<Component> components) {
        this.components = components;
    }

    public String getPerformerReference() {
        return performerReference;
    }

    public void setPerformerReference(String performerReference) {
        this.performerReference = performerReference;
    }

    public String getPerformerName() {
        return performerName;
    }

    public void setPerformerName(String performerName) {
        this.performerName = performerName;
    }

    public String getEffectiveDateDisplay() {
        return effectiveDateDisplay;
    }

    public void setEffectiveDateDisplay(String effectiveDateDisplay) {
        this.effectiveDateDisplay = effectiveDateDisplay;
    }

    public int compareTo(Observation o){
        return (Utility.getComparableDate(this.effectiveDate)).compareTo(Utility.getComparableDate(o.effectiveDate));
    }

    public String getCategorycode() {
        return categorycode;
    }

    public void setCategorycode(String categorycode) {
        this.categorycode = categorycode;
    }
}
