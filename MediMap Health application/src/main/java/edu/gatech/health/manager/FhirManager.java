package edu.gatech.health.manager;

import ca.uhn.fhir.context.FhirContext;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import edu.gatech.health.helpers.Constants;
import edu.gatech.health.helpers.Utility;
import edu.gatech.health.objects.request.RequestAllergyIntolerance;
import edu.gatech.health.objects.request.RequestPatient;
import org.hl7.fhir.instance.model.*;
import org.hl7.fhir.instance.model.api.IIdType;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;


/**
 * Created by Trevoris Jefferson on 3/13/2016.
 * Connection management class for FHIR.
 * Implement connections to FHIR DB here
 */
public class FhirManager {

    private static FhirManager instance = new FhirManager();

    public static FhirManager getInstance() { return instance; }

    private FhirManager (){}

    public String getPatientById(String id) {

        StringBuffer strOutput = null;
        try {

            String encodedParam=java.net.URLEncoder.encode(id,"UTF-8");

            URL url = new URL(Constants.mihinFhirBase + Constants.patient + "?_format=xml&_count=50&_id=" + encodedParam);

            strOutput = new StringBuffer();
            strOutput.append(Utility.makeGetRequest(url));



        } catch (MalformedURLException e) {
            e.printStackTrace();
            strOutput = new StringBuffer();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            strOutput = new StringBuffer();
        }
        return strOutput.toString();
    }

    public String getPatientByName(String name) {

        StringBuffer strOutput = null;
        try {

            String encodedParam=java.net.URLEncoder.encode(name,"UTF-8");
            URL url = new URL(Constants.mihinFhirBase + Constants.patient + "?_format=xml&_count=50&name=" + encodedParam);

            strOutput = new StringBuffer();
            strOutput.append(Utility.makeGetRequest(url));



        } catch (MalformedURLException e) {
            e.printStackTrace();
            strOutput = new StringBuffer();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            strOutput = new StringBuffer();
        }
        return strOutput.toString();
    }


    public String getPatientByFullName(String firstName, String lastName) {

        StringBuffer strOutput = null;
        StringBuilder strInput = new StringBuilder();
        try {

            //Adding values to properly encode into URL

            strInput.append(java.net.URLEncoder.encode(firstName ,"UTF-8"));
            strInput.append("&family=");
            strInput.append(java.net.URLEncoder.encode(lastName ,"UTF-8"));

            URL url = new URL(Constants.mihinFhirBase + Constants.patient + "?_format=xml&_count=50&given=" + strInput.toString());

            //System.out.println("Request URL: " + url.toString());

            strOutput = new StringBuffer();
            strOutput.append(Utility.makeGetRequest(url));



        } catch (MalformedURLException e) {
            e.printStackTrace();
            strOutput = new StringBuffer();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            strOutput = new StringBuffer();
        }
        return strOutput.toString();
    }

    public String getObservationsByPatientId(String id) {

        StringBuffer strOutput = null;
        try {

            String encodedParam=java.net.URLEncoder.encode(id,"UTF-8");

            //URL url = new URL(Constants.mihinFhirBase + Constants.observation + "?_format=xml&patient._id=" + encodedParam);
            URL url = new URL(Constants.mihinFhirBase + Constants.observation + "?_format=xml&_sort=date&_count=50&patient._id=" + encodedParam);

            strOutput = new StringBuffer();
            strOutput.append(Utility.makeGetRequest(url));



        } catch (MalformedURLException e) {
            e.printStackTrace();
            strOutput = new StringBuffer();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            strOutput = new StringBuffer();
        }
        return strOutput.toString();
    }

    public String getConditionsByPatientId(String id) {

        StringBuffer strOutput = null;
        try {

            String encodedParam=java.net.URLEncoder.encode(id,"UTF-8");

            URL url = new URL(Constants.mihinFhirBase + Constants.condition + "?_format=xml&_sort=date-recorded&_count=50&patient._id=" + encodedParam);

            strOutput = new StringBuffer();
            strOutput.append(Utility.makeGetRequest(url));



        } catch (MalformedURLException e) {
            e.printStackTrace();
            strOutput = new StringBuffer();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            strOutput = new StringBuffer();
        }
        return strOutput.toString();
    }

    public String getConditionsByPatientIdJson(String id) {

        StringBuffer strOutput = null;
        try {

            String encodedParam=java.net.URLEncoder.encode(id,"UTF-8");

            URL url = new URL(Constants.mihinFhirBase + Constants.condition + "?_format=json&patient._id=" + encodedParam);

            strOutput = new StringBuffer();
            strOutput.append(Utility.makeGetRequest(url));



        } catch (MalformedURLException e) {
            e.printStackTrace();
            strOutput = new StringBuffer();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            strOutput = new StringBuffer();
        }
        return strOutput.toString();
    }

    public String addConditionsByJson(String request) {

        StringBuffer strOutput = null;
        try {

            String encodedParam=java.net.URLEncoder.encode(request,"UTF-8");

            URL url = new URL(Constants.mihinFhirBase + Constants.condition + "?_format=json&patient._id=" + encodedParam);

            strOutput = new StringBuffer();
            strOutput.append(Utility.makePostRequest(url, request));



        } catch (MalformedURLException e) {
            e.printStackTrace();
            strOutput = new StringBuffer();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            strOutput = new StringBuffer();
        }
        return strOutput.toString();
    }

    public void checkClient(){
        FhirContext context = FhirContext.forDstu2Hl7Org();
        IGenericClient client = context.newRestfulGenericClient(Constants.mihinFhirBase);

        org.hl7.fhir.instance.model.Patient patient = client.read(org.hl7.fhir.instance.model.Patient.class, "Patient-18679");

        List<HumanName> names = patient.getName();
        for (int i=0; i<names.size(); i++){
            HumanName name = names.get(i);
            List<StringType> s = name.getFamily();

            for (int j=0;j<s.size(); j++){
                System.out.println(s.get(j).getValue());
            }
            System.out.println(name.getGiven().toString());
        }


        /*FhirContext ctx = FhirContext.forDstu2();
        IGenericClient client = ctx.newRestfulGenericClient(Constants.mihinFhirBase);
        Bundle results = client.search().forResource()*/

                /*client.search().forResource(org.hl7.fhir.instance.model.Patient.class)
                .returnBundle(org.hl7.fhir.instance.model.Bundle.class);*/

        //org.hl7.fhir.instance.model.Patient.SP_FAMILY.matches("ld")

        /*FhirContext ctx = FhirContext.forDstu2();

        IGenericClient client = ctx.newRestfulGenericClient(Constants.mihinFhirBase);*/

        /*Bundle results = client.search().forResource(Patient.class).where(Patient.FAMILY.matches().value("Boyle"))
                .returnBundle(ca.uhn.fhir.model.dstu.).execute();*/
    }

    public void createConditionTest(){
        FhirContext context = FhirContext.forDstu2Hl7Org();
        IGenericClient client = context.newRestfulGenericClient(Constants.mihinFhirBase);

        Condition condition = new Condition();
        CodeableConcept c = new CodeableConcept();
        c.setText("1");
        c.addCoding().setCode("1").setDisplay("ill");

        Reference ref = new Reference();
        Reference ref2 = new Reference();
        ref.setId("Practitioner/Practitioner-18392");
        ref.addExtension().setId("Practitioner/Practitioner-18392");
        ref.setReference("Practitioner/Practitioner-18392").setDisplay("Dr. No");
        //condition.setPatient(ref);

        condition.setSeverity(c);
        condition.setNotes("details in provison");
        condition.setAsserter(ref);
        ref2.setId("Patient-18679");
        ref2.setReference("Patient/Patient-18679").setDisplay("Sylvia Child");
        condition.setPatient(ref2);
        Date date = new Date();
        condition.setDateRecorded(date);
        MethodOutcome outcom = client.create().resource(condition).prettyPrint().encodedJson().execute();

        IIdType id = outcom.getId();
        System.out.println("Created : " + outcom.getCreated() + " " + id.getValue() + " " + outcom.toString());
    }

    public String getAllergyIntolerancesByPatient(String id) {

        StringBuffer strOutput = null;
        try {

            String encodedParam=java.net.URLEncoder.encode(id,"UTF-8");

            URL url = new URL(Constants.mihinFhirBase + Constants.allergyIntolerance + "?_format=xml&patient._id=" + encodedParam);

            strOutput = new StringBuffer();
            strOutput.append(Utility.makeGetRequest(url));



        } catch (MalformedURLException e) {
            e.printStackTrace();
            strOutput = new StringBuffer();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            strOutput = new StringBuffer();
        }
        return strOutput.toString();
    }

    public String createPatient(RequestPatient requestPatient){
        StringBuffer strOutput = null;
        try {

            URL url = new URL(Constants.mihinFhirBase + Constants.patient);

            strOutput = new StringBuffer();
            strOutput.append(Utility.makePostRequest(url, requestPatient));



        } catch (MalformedURLException e) {
            e.printStackTrace();
            strOutput = new StringBuffer();
        }
        return strOutput.toString();
    }

    public String addAllergyIntolerance(RequestAllergyIntolerance requestAllergyIntolerance){
        StringBuffer strOutput = null;
        try {

            URL url = new URL(Constants.mihinFhirBase + Constants.allergyIntolerance);

            strOutput = new StringBuffer();
            strOutput.append(Utility.makePostRequest(url, requestAllergyIntolerance));



        } catch (MalformedURLException e) {
            e.printStackTrace();
            strOutput = new StringBuffer();
        }
        return strOutput.toString();
    }
}
