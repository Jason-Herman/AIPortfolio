package edu.gatech.health.controller;

import edu.gatech.health.helpers.Helper;
import edu.gatech.health.helpers.Parsers;
import edu.gatech.health.manager.FhirManager;
import edu.gatech.health.objects.*;
import edu.gatech.health.objects.request.RequestPrototypeAllergies;
import edu.gatech.health.objects.request.RequestPrototypeImmunization;
import edu.gatech.health.objects.request.RequestPrototypeSocialHistory;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Trevoris Jefferson on 3/13/2016.
 * Primary Web resource handler Controller
 */
@Controller
public class WebController implements ErrorController{
    @RequestMapping("/")
    public String homePage(Model model){
        return "homepage";
    }

    @RequestMapping("/patientSearch")
    public String patientSearch(@RequestParam(value="id", required=false, defaultValue = "") String id,
                                @RequestParam(value="name", required=false, defaultValue = "") String name,
                                @RequestParam(value="fName", required=false, defaultValue = "") String fName,
                                @RequestParam(value="lName", required=false, defaultValue = "") String lName,
                                Model model){
        /** Access the single instance of FhirManager */

        FhirManager fhirManager = FhirManager.getInstance();
        List<SearchPatient> patients = new ArrayList<SearchPatient>();

        /** Check for any existing input data type
         *  Preference searching by ID first */
        if (!id.isEmpty()){
            patients = Parsers.parsePatientDataForSearch(fhirManager.getPatientById(id));
        }else if (!name.isEmpty()){
            patients = Parsers.parsePatientDataForSearch(fhirManager.getPatientByName(name));
        }else if (!fName.isEmpty() && !lName.isEmpty()){
            patients = Parsers.parsePatientDataForSearch(fhirManager.getPatientByFullName(fName, lName));
        }

        model.addAttribute("patients", patients);

        return "patientsearch";
    }

    @RequestMapping("/patientDetails")
    public String patientDetails(@RequestParam(value="id", required=false, defaultValue = "") String id,
                                Model model){
        /** Access the single instance of FhirManager and Patient */
        FhirManager fhirManager = FhirManager.getInstance();
        Patient patient = Patient.getInstance();
        List<Observation> observations = new ArrayList<Observation>();
        List<Observation> riskfactors = new ArrayList<>();
        //List<AvailableDate> availableDates = new ArrayList<AvailableDate>();
        List<String> availableDates = new ArrayList<String>();
        List<Condition> conditions = new ArrayList<Condition>();
        List<RequestPrototypeImmunization> immunizations = new ArrayList<>();
        List<RequestPrototypeSocialHistory> socialHistory = new ArrayList<>();
        List<RequestPrototypeAllergies> allergies = new ArrayList<>();


        //Empty patient details before loading
        patient.blankPatient();


        Parsers.parsePatientData(fhirManager.getPatientById(id), patient);

        Parsers.parseObervationsForPatient(fhirManager.getObservationsByPatientId(id), observations, riskfactors);

        Parsers.parseProtoTypeImmunizaton(immunizations);
        Parsers.parseProtoTypeSocialHistory(socialHistory);
        Parsers.parsePrototypeAllergies(allergies);
        System.out.println("observations " + observations.size());
        System.out.println("riskfactors " + riskfactors.size());
        Collections.sort(observations);
        Helper.parseObservationsByDate(observations, availableDates);

        conditions = Parsers.parseConditionForPatient(fhirManager.getConditionsByPatientId(id));
        patient.setConditions(conditions);
        //Parsers.parseConditionsForPatientJson(fhirManager.getConditionsByPatientIdJson(id));

        //System.out.println(fhirManager.getConditionsByPatientIdJson(id));
        /*for (Observation o : observations){
            List<Component> c = o.getComponents();
            for (int i=0; i < c.size(); i++){
                System.out.println("" + o.getObservationId() + " " + o.getEffectiveDate() + " " + c.get(i).getCode()
                + " " + c.get(i).getDisplay() + " " + c.get(i).getSystem());
            }
        }*/



        StringBuilder address = new StringBuilder();
        address.append(patient.getAddressLine1());
        address.append("</br>");
        if (patient.getAddressLine2() != null
                && !patient.getAddressLine2().isEmpty()) {
            address.append(patient.getAddressLine2());
            address.append("</br>");
        }
        address.append(patient.getCity());
        address.append(", ");
        address.append(patient.getState());
        address.append(" ");
        address.append(patient.getZipCode());

        model.addAttribute("patient", patient);
        model.addAttribute("address", address);
        model.addAttribute("measurements", observations);
        model.addAttribute("riskfactors", riskfactors);
        model.addAttribute("immunizations", immunizations);
        model.addAttribute("socialhistory", socialHistory);
        model.addAttribute("allergies", allergies);
        //model.addAttribute("availableDates", availableDates);

        return "patientdetails";
    }

    @RequestMapping("/error")
    public String errorPage(Model model){
        return "errorpage";
    }

    @Override
    public String getErrorPath(){
        /* Reroute errors to this page */
        return "/error";
    }
}
