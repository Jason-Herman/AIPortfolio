package edu.gatech.health.objects;


/**
 * Created by Trevoris Jefferson on 4/17/2016.
 * Class for holding patient condition details
 */
public class Condition {
    private String conditionId;
    private String asserter;
    private String conditionDisplay;
    private String conditionDescription;
    private String clinicalStatus;
    private int serverityCode;
    private String serverityDesc;
    private String dateRecorded;
    private String onsetDateTime;
    private String onsetDateTimeDisplay;
    private String conditionx;
    private String conditiony;
    private String asserterId;
    private String patientName;
    private String patientId;

    public Condition() {
        this.conditionId = "";
        this.asserter = "";
        this.conditionDisplay = "";
        this.conditionDescription = "";
        this.clinicalStatus = "";
        this.serverityCode = 0;
        this.serverityDesc = "";
        this.dateRecorded = "";
        this.onsetDateTime = "";
    }

    public String getConditionId() {
        return conditionId;
    }

    public void setConditionId(String conditionId) {
        this.conditionId = conditionId;
    }

    public String getAsserter() {
        return asserter;
    }

    public void setAsserter(String asserter) {
        this.asserter = asserter;
    }

    public String getConditionDisplay() {
        return conditionDisplay;
    }

    public void setConditionDisplay(String conditionDisplay) {
        this.conditionDisplay = conditionDisplay;
    }

    public String getConditionDescription() {
        return conditionDescription;
    }

    public void setConditionDescription(String conditionDescription) {
        this.conditionDescription = conditionDescription;
    }

    public String getClinicalStatus() {
        return clinicalStatus;
    }

    public void setClinicalStatus(String clinicalStatus) {
        this.clinicalStatus = clinicalStatus;
    }

    public int getServerityCode() {
        return serverityCode;
    }

    public void setServerityCode(int serverityCode) {
        this.serverityCode = serverityCode;
    }

    public String getServerityDesc() {
        return serverityDesc;
    }

    public void setServerityDesc(String serverityDesc) {
        this.serverityDesc = serverityDesc;
    }

    public String getDateRecorded() {
        return dateRecorded;
    }

    public void setDateRecorded(String dateRecorded) {
        this.dateRecorded = dateRecorded;
    }

    public String getOnsetDateTime() {
        return onsetDateTime;
    }

    public void setOnsetDateTime(String onsetDateTime) {
        this.onsetDateTime = onsetDateTime;
    }

    public String getOnsetDateTimeDisplay() {
        return onsetDateTimeDisplay;
    }

    public void setOnsetDateTimeDisplay(String onsetDateTimeDisplay) {
        this.onsetDateTimeDisplay = onsetDateTimeDisplay;
    }

    public String getConditionx() {
        return conditionx;
    }

    public void setConditionx(String conditionx) {
        this.conditionx = conditionx;
    }

    public String getConditiony() {
        return conditiony;
    }

    public void setConditiony(String conditiony) {
        this.conditiony = conditiony;
    }

    public String getAsserterId() {
        return asserterId;
    }

    public void setAsserterId(String asserterId) {
        this.asserterId = asserterId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
}
