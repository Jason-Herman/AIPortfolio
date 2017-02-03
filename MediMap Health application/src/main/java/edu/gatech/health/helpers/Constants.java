package edu.gatech.health.helpers;

/**
 * Created by Trevoris Jefferson on 3/13/2016.
 * Store commonly used static data here
 */
public class Constants {

    /** URLs */
    public static String gtFhirBase = "http://polaris.i3l.gatech.edu:8080/gt-fhir-webapp/base/";
    public static String mihinFhirBase = "http://52.72.172.54:8080/fhir/baseDstu2/";
    public static String fhirPatientRequest = "http://polaris.i3l.gatech.edu:8080/gt-fhir-webapp/base/Patient";
    public static String mihinPatientRequetst = "http://52.72.172.54:8080/fhir/baseDstu2/Patient";


    /** Names */
    public static String patient = "Patient";
    public static String condition = "Condition";
    public static String observation = "Observation";
    public static String allergyIntolerance = "AllergyIntolerance";
    public static String diagnosticReport = "DiagnosticReport";

    /** SQL */
    public static String medicalDB = "meddb";
    public static String medicalDBPort = "3306";
    public static String medicalConnection = "medimap-cs6440.c67t2hnnil4w.us-west-2.rds.amazonaws.com";
    public static String conditiontable = "condition";
}
