package edu.gatech.health.objects.request;

/**
 * Created by Trevoris Jefferson on 4/30/2016.
 * Allergies Prototype
 */
public class RequestPrototypeAllergies {
    private String allergy;
    private String severity;

    public RequestPrototypeAllergies(){}

    public String getAllergy() {
        return allergy;
    }

    public void setAllergy(String allergy) {
        this.allergy = allergy;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }
}
