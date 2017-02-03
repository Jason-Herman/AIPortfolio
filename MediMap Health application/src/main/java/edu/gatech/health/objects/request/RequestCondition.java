package edu.gatech.health.objects.request;

/**
 * Created by Trevoris Jefferson on 4/25/2016.
 * Condition type for Request
 */
public class RequestCondition {
    private String patientid;
    private String conditionid;
    private String asserter;
    private String dateRecorded;
    private String codingsystem;
    private String codecode;
    private String codedisplay;
    private String codetext;
    private String clinicalStatustype;
    private String severitysystem;
    private String severitycode;
    private String onsetDateTime;
    private String notes;
    private String clinicalStatus;
    private String coordinatex;
    private String coordinatey;

    public String getPatientid() {
        return patientid;
    }

    public void setPatientid(String patientid) {
        this.patientid = patientid;
    }

    public String getConditionid() {
        return conditionid;
    }

    public void setConditionid(String conditionid) {
        this.conditionid = conditionid;
    }

    public String getAsserter() {
        return asserter;
    }

    public void setAsserter(String asserter) {
        this.asserter = asserter;
    }

    public String getDateRecorded() {
        return dateRecorded;
    }

    public void setDateRecorded(String dateRecorded) {
        this.dateRecorded = dateRecorded;
    }

    public String getCodingsystem() {
        return codingsystem;
    }

    public void setCodingsystem(String codingsystem) {
        this.codingsystem = codingsystem;
    }

    public String getCodecode() {
        return codecode;
    }

    public void setCodecode(String codecode) {
        this.codecode = codecode;
    }

    public String getCodedisplay() {
        return codedisplay;
    }

    public void setCodedisplay(String codedisplay) {
        this.codedisplay = codedisplay;
    }

    public String getCodetext() {
        return codetext;
    }

    public void setCodetext(String codetext) {
        this.codetext = codetext;
    }

    public String getClinicalStatustype() {
        return clinicalStatustype;
    }

    public void setClinicalStatustype(String clinicalStatustype) {
        this.clinicalStatustype = clinicalStatustype;
    }

    public String getSeveritysystem() {
        return severitysystem;
    }

    public void setSeveritysystem(String severitysystem) {
        this.severitysystem = severitysystem;
    }

    public String getSeveritycode() {
        return severitycode;
    }

    public void setSeveritycode(String severitycode) {
        this.severitycode = severitycode;
    }

    public String getOnsetDateTime() {
        return onsetDateTime;
    }

    public void setOnsetDateTime(String onsetDateTime) {
        this.onsetDateTime = onsetDateTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getClinicalStatus() {
        return clinicalStatus;
    }

    public void setClinicalStatus(String clinicalStatus) {
        this.clinicalStatus = clinicalStatus;
    }

    public String getCoordinatex() {
        return coordinatex;
    }

    public void setCoordinatex(String coordinatex) {
        this.coordinatex = coordinatex;
    }

    public String getCoordinatey() {
        return coordinatey;
    }

    public void setCoordinatey(String coordinatey) {
        this.coordinatey = coordinatey;
    }
}
