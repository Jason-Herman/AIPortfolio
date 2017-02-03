package edu.gatech.health.manager;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.*;
import ca.uhn.fhir.model.base.resource.BaseOperationOutcome;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import edu.gatech.health.helpers.Constants;
import edu.gatech.health.objects.*;
import edu.gatech.health.objects.request.RequestCondition;
import edu.gatech.health.objects.request.RequestPatient;
import edu.gatech.health.objects.request.RequestUpdatePatient;
import org.hl7.fhir.instance.model.*;
import org.hl7.fhir.instance.model.Bundle;
import org.hl7.fhir.instance.model.Condition;
import org.hl7.fhir.instance.model.Patient;
import org.hl7.fhir.instance.model.api.IIdType;
import org.joda.time.DateTime;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * Created by Trevoris Jefferson on 4/25/2016.
 * Class to connect to FHIR DB by API
 */
public class FhirClientConnectors {

    private static FhirClientConnectors instance = new FhirClientConnectors();

    /*private FhirContext context = FhirContext.forDstu2Hl7Org();
    private IGenericClient client = context.newRestfulGenericClient(Constants.mihinFhirBase);*/

    public static FhirClientConnectors getInstance() {return instance;}

    private FhirClientConnectors(){};

    public void testConditionSearch(){
        FhirContext context = FhirContext.forDstu2Hl7Org();
        IGenericClient client = context.newRestfulGenericClient(Constants.mihinFhirBase);

        Condition condition = new Condition();
        try {
        String encodedParam=java.net.URLEncoder.encode("Patient-18679","UTF-8");


            URL url = new URL(Constants.mihinFhirBase + Constants.condition + "?_format=xml&_sort=date-recorded&_count=50&patient._id=" + encodedParam);

            //Patient.SP_IDENTIFIER.matches("Patient-18679")
            Bundle response = client.search().byUrl(url.toString())
                    .returnBundle(Bundle.class).execute();

            System.out.println("Bundle count " + response.getTotal());
        }  catch (MalformedURLException e) {
                e.printStackTrace();
        }  catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public String addConditionDetails(RequestCondition requestCondition){
        FhirContext context = FhirContext.forDstu2Hl7Org();
        IGenericClient client = context.newRestfulGenericClient(Constants.mihinFhirBase);

        Condition condition = new Condition();

        setCondition(condition, requestCondition);

        //Returning response
        MethodOutcome response = client.create().resource(condition).prettyPrint().encodedJson().execute();

        IIdType id = response.getId();
        String output = "0";
        if (response.getCreated()){
            output = id.getValue();
        }

        //System.out.println("Created : " + response.getCreated() + " " + id.getValue() + " " + response.toString());
        return output;
    }

    public String updateConditionDetails(RequestCondition requestCondition){
        FhirContext context = FhirContext.forDstu2Hl7Org();
        IGenericClient client = context.newRestfulGenericClient(Constants.mihinFhirBase);

        Condition condition = new Condition();

        setCondition(condition, requestCondition);

        condition.setId(requestCondition.getConditionid());

        //Returning response
        MethodOutcome response = client.update().resource(condition).prettyPrint().encodedJson().execute();

        IIdType id = response.getId();
        String output = "0";
        /*if (response.getCreated()){
            output = id.getValue();
        }*/

        //System.out.println("Created : " + response.getCreated() + " " + id.getValue() + " " + response.toString());
        return output;
    }

    public String deleteConditionDetails(RequestCondition requestCondition){
        FhirContext context = FhirContext.forDstu2Hl7Org();
        IGenericClient client = context.newRestfulGenericClient(Constants.mihinFhirBase);

        Condition condition = new Condition();

        setCondition(condition, requestCondition);

        condition.setId("Condition/" + requestCondition.getConditionid());

        //Returning response
        BaseOperationOutcome response = client.delete().resource(condition).prettyPrint().encodedJson().execute();

        String output = "0";
        /*if (response.getCreated()){
            output = id.getValue();
        }*/

        //System.out.println("Created : " + response.getCreated() + " " + id.getValue() + " " + response.toString());
        return output;
    }

    private void setCondition(Condition condition, RequestCondition requestCondition){
        Reference ref = new Reference();

        //Set related Patient Details
        ref.setId(requestCondition.getPatientid());
        ref.setReference("Patient/" +requestCondition.getPatientid());
        //ref.setDisplay();
        condition.setPatient(ref);

        //Set asserter practitioner details
        ref = new Reference();
        ref.setId("Practitioner/" + requestCondition.getAsserter());
        //ref.addExtension().setId("Practitioner/" + requestCondition.getAsserter());
        ref.setReference("Practitioner/" + requestCondition.getAsserter());
        ref.setDisplay("Dr. No");
        condition.setAsserter(ref);

        //Set Date Recorded
        DateTime dt = new DateTime(requestCondition.getDateRecorded());

        Date date = dt.toDate();
        condition.setDateRecorded(date);

        //Set condition code
        CodeableConcept c;
        c = new CodeableConcept();
        c.setText(requestCondition.getCodedisplay());
        c.addCoding().setDisplay(requestCondition.getNotes());
        condition.setCode(c);

        condition.setNotes(requestCondition.getNotes());


        condition.setClinicalStatus(requestCondition.getClinicalStatus());

        //Set Clinical status
        c = new CodeableConcept();
        c.setText(requestCondition.getSeveritycode());
        String severityDesc = "";
        if (requestCondition.getSeveritycode().equals("1")){
            severityDesc = "ill";
        } else if (requestCondition.getSeveritycode().equals("2")){
            severityDesc = "sick";
        } else if (requestCondition.getSeveritycode().equals("3")){
            severityDesc = "critical";
        }
        c.addCoding().setCode(requestCondition.getSeveritycode()).setDisplay(severityDesc);

        condition.setSeverity(c);


        //Set Onset Datetime
        //Type onset = new DateTimeType();


        //Type onsetValue = new StringType();
        //onset.addExtension().setValue("");
        //onset.addExtension().setValue(requestCondition.getOnsetDateTime());

        //condition.setOnset(onset);
        //condition.setOnset(new StringType(requestCondition.getOnsetDateTime()));

        condition.setOnset(new DateTimeType(requestCondition.getOnsetDateTime()));
    }

    public void getPatientDetails(String patientId, edu.gatech.health.objects.Patient outPatient){
        FhirContext context = FhirContext.forDstu2Hl7Org();
        IGenericClient client = context.newRestfulGenericClient(Constants.mihinFhirBase);

        /*FhirContext context = FhirContext.forDstu1();
        IGenericClient custClient = context.newRestfulGenericClient(Constants.mihinFhirBase);*/

        //Returning response
        //Patient patient = client.read(Patient.class, patientId);
        Patient patient = new Patient();

        // Use the client to read back the new instance using the
        // ID we retrieved from the read
        //patient = client.search().byUrl();

        try {
            //System.out.println("getting patient details fhir for " + patientId);



            String encodedParam=java.net.URLEncoder.encode(patientId,"UTF-8");

            URL url = new URL(Constants.mihinFhirBase + Constants.patient + "?_format=xml&_count=1&_id=" + encodedParam);

            patient = client.read(Patient.class, patientId);

            if (patient != null) {

                List<HumanName> names = patient.getName();

                //Set names
                boolean hasFirstName = false;
                boolean hasMidName = false;
                boolean hasLastName = false;
                for (int u = 0; u < names.size(); u++) {
                    List<StringType> given = names.get(u).getGiven();
                    List<StringType> family = names.get(u).getFamily();
                    List<StringType> prefix = names.get(u).getPrefix();
                    List<StringType> suffix = names.get(u).getSuffix();

                    for (int i = 0; i < given.size(); i++) {
                        if (i == 0 && !hasFirstName) {
                            outPatient.setPatientFname(given.get(i).getValue());
                            hasFirstName=true;
                        } else if (i == 1 && !hasMidName) {
                            outPatient.setPatientMidName(given.get(i).getValue());
                            hasMidName=true;
                        } else {
                            break;
                        }
                        //System.out.println("given: " + given.get(i).getValue());
                    }

                    for (int i = 0; i < family.size(); i++) {
                        if (i == 0 && !hasLastName) {
                            outPatient.setPatientLname(family.get(i).getValue());
                            hasLastName=true;
                        } else {
                            break;
                        }
                        //System.out.println("family: " + family.get(i).getValue());
                    }

                    for (int i = 0; i < prefix.size(); i++) {
                        if (i == 0) {
                            outPatient.setPrefix(prefix.get(i).getValue());
                        } else {
                            break;
                        }
                        //System.out.println("prefix: " + prefix.get(i).getValue());
                    }

                    for (int i = 0; i < suffix.size(); i++) {
                        if (i == 0) {
                            outPatient.setSuffix(suffix.get(i).getValue());
                        } else {
                            break;
                        }
                        //System.out.println("prefix: " + suffix.get(i).getValue());
                    }

                }

                //Get gender
                Enumerations.AdministrativeGender gender = patient.getGender();
                outPatient.setGender(gender.toCode());

                //Get Birth date
                if (patient.getBirthDate() != null) {
                    outPatient.setBirthDt(patient.getBirthDate().toString());
                }

                //Get Address
                if (patient.getAddress() != null) {
                    List<Address> addresses = patient.getAddress();
                    boolean hasAddress = false;

                    for (int i=0; i<addresses.size(); i++){
                        if (i==0 && !hasAddress){
                            List<StringType> addrLine = addresses.get(i).getLine();
                            for (int u=0; u<addrLine.size(); u++){
                                //System.out.println("Address line " + i + " " + addrLine.get(u).getValue());
                                if (u==0) {
                                    outPatient.setAddressLine1(addrLine.get(u).getValue());
                                }else if (u==1){
                                    outPatient.setAddressLine2(addrLine.get(u).getValue());
                                }
                            }

                            outPatient.setCity(addresses.get(i).getCity());
                            outPatient.setState(addresses.get(i).getState());
                            outPatient.setZipCode(addresses.get(i).getPostalCode());
                            hasAddress=true;
                            /*System.out.println("City " + addresses.get(i).getCity());

                            System.out.println("State " + addresses.get(i).getState());

                            System.out.println("Zip " + addresses.get(i).getPostalCode());*/

                        }else{
                            break;
                        }
                    }
                }

                //Get Phone
                if (patient.getTelecom() != null){
                    List<ContactPoint> phone = patient.getTelecom();
                    boolean hasPhone = false;
                    boolean hasEmail = false;
                    for (int i=0; i<phone.size(); i++){
                        ContactPoint.ContactPointSystem c = phone.get(i).getSystem();
                        if (c.getDisplay().equals("Phone") && !hasPhone){
                            outPatient.setPhonenum(phone.get(i).getValue());
                            hasPhone=true;
                        }else if (c.getDisplay().equals("Email") && !hasEmail){
                            outPatient.setEmail(phone.get(i).getValue());
                            hasEmail=true;
                        }
                        //System.out.println("Phone num " + phone.get(i).getValue() + " " + c.getDisplay());
                    }
                }

            /*IdType idT = new IdType();
            IdDt idDT = new IdDt();

            idT.setValue(patientId);
            idDT.setValue(patientId);

            Bundle response = client.search().byUrl(url.toString()).returnBundle(Bundle.class).execute();
            //ca.uhn.fhir.model.api.Bundle bundle = client.search().byUrl(url.toString()).returnBundle(ca.uhn.fhir.model.api.Bundle.class);

            List<Property> ps = response.children();
            System.out.println("bundle: " + ps.size());

            for (int i=0; i<ps.size(); i++){
                System.out.println(ps.get(i).getName());
            }*/


                //patient = client.read().resource(Patient.class).withIdAndVersion(patientId, "1").execute();
                //patient = client.vread(Patient.class, patientId, "1");
                //patient = client.vread(Patient.class, idDT, "1");
                //patient = client.read().resource(Patient.class).withUrl(url.toString()).prettyPrint().encodedJson().execute();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }



    }

    public String updatePatientDetails(RequestUpdatePatient requestPatient){
        FhirContext context = FhirContext.forDstu2Hl7Org();
        IGenericClient client = context.newRestfulGenericClient(Constants.mihinFhirBase);

        //Patient patient = new Patient();

        System.out.println("Patient ID " + requestPatient.getPatientId());
        Patient patient = client.read(Patient.class, requestPatient.getPatientId());

        setPatient(patient, requestPatient);

        //IdDt updatedId = new IdDt();
        /*updatedId.
        updatedId.withVersion("1");*/
        //updatedId.setValue("Patient/" + requestPatient.getPatientId());

        //patient.setId(requestPatient.getPatientId());
        //patient.setId(updatedId);

        //Returning response
        MethodOutcome response = client.update().resource(patient).execute();

        IIdType id = response.getId();
        String output = "0";
        /*if (response.getCreated()){
            output = id.getValue();
        }*/

        //System.out.println("Created : " + response.getCreated() + " " + id.getValue() + " " + response.toString());
        return output;
    }

    private void setPatient(Patient patient, RequestUpdatePatient requestPatient){
        Reference ref = new Reference();

        //Set Name
        patient.addName().addGiven(requestPatient.getPatientFname());
        if (requestPatient.getPatientMidName() != null && !requestPatient.getPatientMidName().isEmpty()) {
            patient.addName().addGiven(requestPatient.getPatientMidName());
        }
        patient.addName().addFamily(requestPatient.getPatientLname());

        //Set Birth Date
        DateTime dt = new DateTime(requestPatient.getBirthDt());
        Date date = dt.toDate();
        patient.setBirthDate(date);


        //Set Gender
        if (requestPatient.getGender().equals("male")) {
            patient.setGender(Enumerations.AdministrativeGender.MALE);
        } else if (requestPatient.getGender().equals("female")){
            patient.setGender(Enumerations.AdministrativeGender.FEMALE);
        } else if (requestPatient.getGender().equals("other")){
            patient.setGender(Enumerations.AdministrativeGender.OTHER);
        } else{
            patient.setGender(Enumerations.AdministrativeGender.UNKNOWN);
        }

        //Set Address
        Address address = new Address();
        address.addLine(requestPatient.getAddressLine1()).addLine(requestPatient.getAddressLine2());
        address.setCity(requestPatient.getCity());
        address.setState(requestPatient.getState());
        address.setPostalCode(requestPatient.getZipCode());

        patient.addAddress(address);

        //Set Phone
        ContactPoint contactPoint = new ContactPoint();
        contactPoint.setUse(ContactPoint.ContactPointUse.HOME);
        contactPoint.setValue(requestPatient.getPhonenum());
        contactPoint.setSystem(ContactPoint.ContactPointSystem.PHONE);
        patient.addTelecom(contactPoint);

        //Set Email
        ContactPoint contactPointEmail = new ContactPoint();
        contactPoint.setUse(ContactPoint.ContactPointUse.HOME);
        contactPoint.setValue(requestPatient.getEmail());
        contactPoint.setSystem(ContactPoint.ContactPointSystem.EMAIL);
        patient.addTelecom(contactPointEmail);

    }

    public void getImmunizationsList(String patientId, edu.gatech.health.objects.Patient outPatient){
        FhirContext context = FhirContext.forDstu2Hl7Org();
        IGenericClient client = context.newRestfulGenericClient(Constants.mihinFhirBase);

        /*FhirContext context = FhirContext.forDstu1();
        IGenericClient custClient = context.newRestfulGenericClient(Constants.mihinFhirBase);*/

        //Returning response
        //Patient patient = client.read(Patient.class, patientId);
        Patient patient = new Patient();

        // Use the client to read back the new instance using the
        // ID we retrieved from the read
        //patient = client.search().byUrl();

        try {
            //System.out.println("getting patient details fhir for " + patientId);



            String encodedParam=java.net.URLEncoder.encode(patientId,"UTF-8");

            URL url = new URL(Constants.mihinFhirBase + Constants.patient + "?_format=xml&_count=1&_id=" + encodedParam);

            patient = client.read(Patient.class, patientId);

            /*
            if (patient != null) {

                List<HumanName> names = patient.getName();

                //Set names
                boolean hasFirstName = false;
                boolean hasMidName = false;
                boolean hasLastName = false;
                for (int u = 0; u < names.size(); u++) {
                    List<StringType> given = names.get(u).getGiven();
                    List<StringType> family = names.get(u).getFamily();
                    List<StringType> prefix = names.get(u).getPrefix();
                    List<StringType> suffix = names.get(u).getSuffix();

                    for (int i = 0; i < given.size(); i++) {
                        if (i == 0 && !hasFirstName) {
                            outPatient.setPatientFname(given.get(i).getValue());
                            hasFirstName=true;
                        } else if (i == 1 && !hasMidName) {
                            outPatient.setPatientMidName(given.get(i).getValue());
                            hasMidName=true;
                        } else {
                            break;
                        }
                        //System.out.println("given: " + given.get(i).getValue());
                    }

                    for (int i = 0; i < family.size(); i++) {
                        if (i == 0 && !hasLastName) {
                            outPatient.setPatientLname(family.get(i).getValue());
                            hasLastName=true;
                        } else {
                            break;
                        }
                        //System.out.println("family: " + family.get(i).getValue());
                    }

                    for (int i = 0; i < prefix.size(); i++) {
                        if (i == 0) {
                            outPatient.setPrefix(prefix.get(i).getValue());
                        } else {
                            break;
                        }
                        //System.out.println("prefix: " + prefix.get(i).getValue());
                    }

                    for (int i = 0; i < suffix.size(); i++) {
                        if (i == 0) {
                            outPatient.setSuffix(suffix.get(i).getValue());
                        } else {
                            break;
                        }
                        //System.out.println("prefix: " + suffix.get(i).getValue());
                    }

                }

                //Get gender
                Enumerations.AdministrativeGender gender = patient.getGender();
                outPatient.setGender(gender.toCode());

                //Get Birth date
                if (patient.getBirthDate() != null) {
                    outPatient.setBirthDt(patient.getBirthDate().toString());
                }

                //Get Address
                if (patient.getAddress() != null) {
                    List<Address> addresses = patient.getAddress();
                    boolean hasAddress = false;

                    for (int i=0; i<addresses.size(); i++){
                        if (i==0 && !hasAddress){
                            List<StringType> addrLine = addresses.get(i).getLine();
                            for (int u=0; u<addrLine.size(); u++){
                                //System.out.println("Address line " + i + " " + addrLine.get(u).getValue());
                                if (u==0) {
                                    outPatient.setAddressLine1(addrLine.get(u).getValue());
                                }else if (u==1){
                                    outPatient.setAddressLine2(addrLine.get(u).getValue());
                                }
                            }

                            outPatient.setCity(addresses.get(i).getCity());
                            outPatient.setState(addresses.get(i).getState());
                            outPatient.setZipCode(addresses.get(i).getPostalCode());
                            hasAddress=true;

                        }else{
                            break;
                        }
                    }
                }

                //Get Phone
                if (patient.getTelecom() != null){
                    List<ContactPoint> phone = patient.getTelecom();
                    boolean hasPhone = false;
                    boolean hasEmail = false;
                    for (int i=0; i<phone.size(); i++){
                        ContactPoint.ContactPointSystem c = phone.get(i).getSystem();
                        if (c.getDisplay().equals("Phone") && !hasPhone){
                            outPatient.setPhonenum(phone.get(i).getValue());
                            hasPhone=true;
                        }else if (c.getDisplay().equals("Email") && !hasEmail){
                            outPatient.setEmail(phone.get(i).getValue());
                            hasEmail=true;
                        }
                        //System.out.println("Phone num " + phone.get(i).getValue() + " " + c.getDisplay());
                    }
                }

            }
            */

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }


    public void testFhirReadUpdate(){
        FhirContext context = FhirContext.forDstu2Hl7Org();
        IGenericClient client = context.newRestfulGenericClient(Constants.mihinFhirBase);

        // Use the client to read back the new instance using the
        // ID we retrieved from the read
        Patient patient = client.read(Patient.class, "Patient-16801");

        // Print the ID of the newly created resource
        System.out.println("Found ID:    " + patient.getId());

        // Change the gender and send an update to the server
        //patient.setGender(AdministrativeGenderCodesEnum.F);
        patient.setGender(Enumerations.AdministrativeGender.FEMALE);
        MethodOutcome outcome = client.update().resource(patient).execute();

        System.out.println("Now have ID: " + outcome.getId());
    }
}
