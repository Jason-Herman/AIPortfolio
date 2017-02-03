package edu.gatech.health.manager;

import com.google.gson.Gson;
import edu.gatech.health.helpers.Parsers;
import edu.gatech.health.objects.AvailableDate;
import edu.gatech.health.objects.Condition;
import edu.gatech.health.objects.Patient;
import edu.gatech.health.objects.SearchPatient;
import edu.gatech.health.objects.query.PatientSearchCriteria;
import edu.gatech.health.objects.query.TestCoordinate;
import edu.gatech.health.objects.query.TestCoordinateRepository;
import edu.gatech.health.objects.request.RequestCondition;
import edu.gatech.health.objects.request.RequestCoordinate;
import edu.gatech.health.objects.request.RequestPatient;
import edu.gatech.health.objects.request.RequestUpdatePatient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Trevoris Jeffferson on 3/27/2016.
 * Restful Client Manger
 * Used to make REST calls for this application
 */
@RestController
public class RestClientManager {

    @RequestMapping("/patientsById")
    @GET
    @Produces(value = "application/json")
    public List<SearchPatient> getListPatientsById(@RequestParam(value = "id", required = false, defaultValue = "") String id) {
        FhirManager fhirManager = FhirManager.getInstance();
        List<SearchPatient> patients = new ArrayList<SearchPatient>();

        /** Check for any existing input data type
         *  Preference searching by ID first */
        if (!id.isEmpty()) {
            patients = Parsers.parsePatientDataForSearch(fhirManager.getPatientById(id));
        }

        return patients;
    }

    @RequestMapping("/patientsByName")
    @GET
    @Produces(value = "application/json")
    public List<SearchPatient> getListPatientsByName(@RequestParam(value = "name", required = false, defaultValue = "") String name) {
        FhirManager fhirManager = FhirManager.getInstance();
        List<SearchPatient> patients = new ArrayList<SearchPatient>();

        /** Check for any existing input data type
         *  Preference searching by ID first */
        if (!name.isEmpty()) {
            patients = Parsers.parsePatientDataForSearch(fhirManager.getPatientByName(name));
        }

        return patients;
    }

    @RequestMapping("/patientsByFullName")
    @GET
    @Produces(value = "application/json")
    public List<SearchPatient> getListPatientsByName(@RequestParam(value = "name", required = false, defaultValue = "") String name,
                                                     @RequestParam(value = "fName", required = false, defaultValue = "") String fName,
                                                     @RequestParam(value = "lName", required = false, defaultValue = "") String lName) {
        FhirManager fhirManager = FhirManager.getInstance();
        List<SearchPatient> patients = new ArrayList<SearchPatient>();

        /** Check for any existing input data type
         *  Preference searching by ID first */
        if (!name.isEmpty()) {
            patients = Parsers.parsePatientDataForSearch(fhirManager.getPatientByName(name));
        } else if (!fName.isEmpty() && !lName.isEmpty()) {
            patients = Parsers.parsePatientDataForSearch(fhirManager.getPatientByFullName(fName, lName));
        }

        return patients;
    }

    @RequestMapping("/patientsByFullNamePost")
    @POST
    @Produces(value = "application/json")
    public List<SearchPatient> getListPatientsByNamePost(@RequestBody String request) {
        FhirManager fhirManager = FhirManager.getInstance();
        List<SearchPatient> patients = new ArrayList<SearchPatient>();

        //System.out.println("Request: " + request);

        /**
         * Convert request data to java object
         */
        Gson gson = new Gson();
        PatientSearchCriteria searchCriteria = gson.fromJson(request, PatientSearchCriteria.class);

        //System.out.printf("Criteria: (%s) %s %s", searchCriteria.getId(), searchCriteria.getfName(), searchCriteria.getlName());
        /** Check for any existing input data type
         *  Preference searching by name first */
        if (searchCriteria != null && (searchCriteria.getName() != null && !searchCriteria.getName().isEmpty())) {
            patients = Parsers.parsePatientDataForSearch(fhirManager.getPatientByName(searchCriteria.getName()));
        } else if (searchCriteria != null && (searchCriteria.getfName() != null && !searchCriteria.getfName().isEmpty()
                && searchCriteria.getlName() != null && !searchCriteria.getlName().isEmpty())) {
            patients = Parsers.parsePatientDataForSearch(
                    fhirManager.getPatientByFullName(searchCriteria.getfName(), searchCriteria.getlName()));
        }

        return patients;
    }

    @RequestMapping("/createPatient")
    @POST
    @Consumes(value = "application/json")
    @Produces(value = "application/json")
    public String createPatient(@RequestBody String request) {
        FhirManager fhirManager = FhirManager.getInstance();

        //Convert string into Java object
        Gson gson = new Gson();
        RequestPatient requestPatient = gson.fromJson(request, RequestPatient.class);

        fhirManager.createPatient(requestPatient);

        return "response";
    }

    @RequestMapping("/createCoordinate")
    @POST
    @Consumes(value = "application/json")
    @Produces(value = "application/json")
    public String createCoordinate(@RequestBody String request,
                                   TestCoordinateRepository repository) {

        Gson gson = new Gson();
        RequestCoordinate requestCoordinate = gson.fromJson(request, RequestCoordinate.class);

        System.out.printf("create coordinate request: %s %d %d %d %s\n", requestCoordinate.getPatientId(),
                requestCoordinate.getConditionId(), requestCoordinate.getCoordinateX(),
                requestCoordinate.getCoordinateY(), requestCoordinate.getConditionDesc());

        repository.save(new TestCoordinate(requestCoordinate.getPatientId(), requestCoordinate.getCoordinateX(),
                requestCoordinate.getCoordinateY(), requestCoordinate.getConditionDesc()));

        List<TestCoordinate> testCoordinates = repository.findByPatientId(requestCoordinate.getPatientId());

        for (TestCoordinate testCoordinate : testCoordinates) {
            System.out.printf("Test coordinate: %s %d %d %d %s \n", testCoordinate.getPatientId(), testCoordinate.getConditionId(),
                    testCoordinate.getX(), testCoordinate.getY(), testCoordinate.getDescription());
        }

        return "response";
    }

    @RequestMapping("/availableDatesList")
    @POST
    @Produces(value = "application/json")
    public List<String> getListAvailableOfDates() {
        AvailableDate availableDate = AvailableDate.getInstance();

        return availableDate.getAvailableDatesList();
    }

    @RequestMapping("/conditionsList")
    @POST
    @Produces(value = "application/json")
    public List<Condition> getListConditionsForPatient() {
        Patient patient = Patient.getInstance();
        List<Condition> conditions = patient.getConditions();


        QueryManger qm = new QueryManger();

        try {
            qm.getListOfConditions(patient.getPatientId(), conditions);
        } catch (Exception e) {
            System.out.println("query exception: " + e);
        }


        return conditions;
    }

    @RequestMapping("/addCondition")
    @POST
    @Consumes(value = "application/json")
    @Produces(value = "application/json")
    public String addCondition(@RequestBody String request) {
        //Patient patient = Patient.getInstance();
        //List<Condition> conditions = patient.getConditions();

        Gson gson = new Gson();
        RequestCondition reqCondition = gson.fromJson(request, RequestCondition.class); /*gson.toJson(request);*/

        org.hl7.fhir.instance.model.Condition conditionModel = new org.hl7.fhir.instance.model.Condition();


        System.out.println("Request Patient ID: " + reqCondition.getPatientid());
        System.out.println(request);

        //FhirManager f = FhirManager.getInstance();
        //f.checkClient();
        //f.createConditionTest();

        //Add condition to fhirDB
        FhirClientConnectors fhirClientConnectors = FhirClientConnectors.getInstance();

        String response = fhirClientConnectors.addConditionDetails(reqCondition);


        /*QueryManger qm = new QueryManger();

        try{
            qm.getListOfConditions(patient.getPatientId(), conditions);
        } catch ( Exception e) {
            System.out.println("query exception: " + e);
        }*/

        if (response.equals("0")) {
            return "failure";
        } else {
            QueryManger qm = new QueryManger();

            try {
                String[] idArr = response.split("/");

                Condition condition = new Condition();
                condition.setConditionId(idArr[6]);
                condition.setConditionx(reqCondition.getCoordinatex());
                condition.setConditiony(reqCondition.getCoordinatey());
                qm.addCondition(reqCondition.getPatientid(), condition);

            } catch (Exception e) {
                System.out.println("query exception: " + e);
                return "SQL query failure for coordinates";
            }
        }
        return "success";
    }

    @RequestMapping("/updateCondition")
    @POST
    @Consumes(value = "application/json")
    @Produces(value = "application/json")
    public String updateCondition(@RequestBody String request) {
        Gson gson = new Gson();
        RequestCondition reqCondition = gson.fromJson(request, RequestCondition.class); /*gson.toJson(request);*/

        org.hl7.fhir.instance.model.Condition conditionModel = new org.hl7.fhir.instance.model.Condition();


        System.out.println("Request Patient ID: " + reqCondition.getPatientid());
        System.out.println(request);

        //Add condition to fhirDB
        FhirClientConnectors fhirClientConnectors = FhirClientConnectors.getInstance();

        String response = fhirClientConnectors.updateConditionDetails(reqCondition);


        /*QueryManger qm = new QueryManger();

        try{
            qm.getListOfConditions(patient.getPatientId(), conditions);
        } catch ( Exception e) {
            System.out.println("query exception: " + e);
        }*/

        if (response.equals("0")) {
            return "failure";
        } else {
            return "success";
        }
    }

    @RequestMapping("/deleteCondition")
    @POST
    @Consumes(value = "application/json")
    @Produces(value = "application/json")
    public String deleteCondition(@RequestBody String request) {
        Gson gson = new Gson();
        RequestCondition reqCondition = gson.fromJson(request, RequestCondition.class); /*gson.toJson(request);*/

        org.hl7.fhir.instance.model.Condition conditionModel = new org.hl7.fhir.instance.model.Condition();


        System.out.println("Request Patient ID: " + reqCondition.getPatientid());
        System.out.println(request);

        //Add condition to fhirDB
        FhirClientConnectors fhirClientConnectors = FhirClientConnectors.getInstance();

        String response = fhirClientConnectors.deleteConditionDetails(reqCondition);

        if (response.equals("0")) {
            return "failure";
        } else {
            return "success";
        }
    }

    @RequestMapping("/moveCondition")
    @POST
    @Consumes(value = "application/json")
    @Produces(value = "application/json")
    public String moveCondition(@RequestBody String request) {
        Gson gson = new Gson();
        RequestCondition reqCondition = gson.fromJson(request, RequestCondition.class);

        QueryManger qm = new QueryManger();

        System.out.println("Request: " + request);
        try {


            Condition condition = new Condition();
            condition.setPatientId(reqCondition.getPatientid());
            condition.setConditionId(reqCondition.getConditionid());
            condition.setConditionx(reqCondition.getCoordinatex());
            condition.setConditiony(reqCondition.getCoordinatey());

            //Check if condition exists
            List<Condition> conditions = new ArrayList<>();
            qm.getSpecificOfCondition(condition.getPatientId(), condition.getConditionId(), conditions);

            if (conditions.size() > 0){
                //Condition match, move it
                qm.moveCondition(condition);
            }else{
                //Create the condition
                qm.addCondition(condition.getPatientId(), condition);
            }




        } catch (Exception e) {
            System.out.println("query exception: " + e);
            return "SQL query failure for coordinates";
        }

        return "success";
    }

    @RequestMapping("/getPatientDetails")
    @POST
    @Consumes(value = "application/json")
    @Produces(value = "application/json")
    public Patient getPatientDetails(@RequestBody String request){

        System.out.println("patient ID: " + request);

        Patient patient = Patient.getInstance();

        FhirClientConnectors fhirClientConnectors = FhirClientConnectors.getInstance();

        fhirClientConnectors.getPatientDetails(request, patient);

        return patient;
    }

    @RequestMapping("/updatePatient")
    @POST
    @Consumes(value = "application/json")
    @Produces(value = "application/json")
    public String updatePatient(@RequestBody String request){

        System.out.println("patient ID: " + request);

        Gson gson = new Gson();
        RequestUpdatePatient reqPatient = gson.fromJson(request, RequestUpdatePatient.class);

        Patient patient = Patient.getInstance();

        FhirClientConnectors fhirClientConnectors = FhirClientConnectors.getInstance();

        fhirClientConnectors.updatePatientDetails(reqPatient);
        //fhirClientConnectors.getPatientDetails(request, patient);

        return "Success";
    }
}
