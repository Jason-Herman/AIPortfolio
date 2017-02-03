package edu.gatech.health.objects.request;

/**
 * Created by Trevoris Jefferson on 4/30/2016.
 * Immunization Prototype
 */
public class RequestPrototypeImmunization {
    private String immunizationName;
    private String immunizationDate;

    public RequestPrototypeImmunization(){}

    public String getImmunizationName() {
        return immunizationName;
    }

    public void setImmunizationName(String immunizationName) {
        this.immunizationName = immunizationName;
    }

    public String getImmunizationDate() {
        return immunizationDate;
    }

    public void setImmunizationDate(String immunizationDate) {
        this.immunizationDate = immunizationDate;
    }
}
