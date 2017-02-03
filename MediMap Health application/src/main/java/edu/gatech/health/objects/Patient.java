package edu.gatech.health.objects;

import java.util.List;

/**
 * Created by Trevoris Jefferson on 3/13/2016.
 * Singleton implemenation of Patient
 */
public class Patient {
    private static Patient ourInstance = new Patient();

    public static Patient getInstance() {
        return ourInstance;
    }

    private Patient() {
    }

    private String patientId;
    private String patientFname;
    private String patientMidName;
    private String patientLname;
    private String prefix;
    private String suffix;
    private String gender;
    private String birthDt;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zipCode;
    private String phonenum;
    private String email;
    private String addressType;
    private List<Condition> conditions;

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientFname() {
        return patientFname;
    }

    public void setPatientFname(String patientFname) {
        this.patientFname = patientFname;
    }

    public String getPatientLname() {
        return patientLname;
    }

    public void setPatientLname(String patientLname) {
        this.patientLname = patientLname;
    }


    public String getPatientMidName() {
        return patientMidName;
    }

    public void setPatientMidName(String patientMidName) {
        this.patientMidName = patientMidName;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthDt() {
        return birthDt;
    }

    public void setBirthDt(String birthDt) {
        this.birthDt = birthDt;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String printPatientDetails(){
        return String.format("Patient: %s %s (%d)", this.getPatientFname(), this.getPatientLname(), this.getPatientId());
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    public void blankPatient(){
        this.setPatientFname("");
        this.setPatientLname("");
        this.setPatientId("");
        this.setAddressLine1("");
        this.setAddressLine2("");
        this.setCity("");
        this.setState("");
        this.setZipCode("");
        this.setGender("");
        this.setBirthDt("");
    }
}
