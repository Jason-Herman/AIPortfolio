package edu.gatech.health.helpers;

import com.google.gson.Gson;
import edu.gatech.health.objects.*;
import edu.gatech.health.objects.request.RequestPrototypeAllergies;
import edu.gatech.health.objects.request.RequestPrototypeImmunization;
import edu.gatech.health.objects.request.RequestPrototypeSocialHistory;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Trevoris Jefferson on 3/13/2016.
 * Implement parser logic here
 */
public class Parsers {
    public static void parsePatientData(String xml){
        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = db.parse(new org.xml.sax.InputSource(new StringReader(xml)));
            System.out.println(doc.getElementsByTagName("Patient"));
            int patientCount = doc.getElementsByTagName("Patient").getLength();
            System.out.println("Elements: " + patientCount);

            //Parse Child elements
            for (int p = 0; p < patientCount; p++){
                NodeList children = doc.getElementsByTagName("Patient").item(p).getChildNodes();
                NodeList subChildren = null;
                Node child = null;

                for (int i = 0; i < children.getLength(); i++) {
                    child = children.item(i);
                    if (child.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) child;

                        //Logic for Patient Names
                        if (element.getTagName().equals("name")) {
                            subChildren = element.getChildNodes();
                            Node subChild = null;
                            for (int j = 0; j < subChildren.getLength(); j++) {
                                subChild = subChildren.item(j);
                                if (subChild.getNodeType() == Node.ELEMENT_NODE) {
                                    Element subElement = (Element) subChild;
                                    System.out.println(subElement.getTagName() + ": " + subElement.getAttribute("value"));
                                }
                            }
                        }
                        //Logic for address
                        else if (element.getTagName().equals("address")) {
                            subChildren = element.getChildNodes();
                            Node subChild = null;
                            for (int j = 0; j < subChildren.getLength(); j++) {
                                subChild = subChildren.item(j);
                                if (subChild.getNodeType() == Node.ELEMENT_NODE) {
                                    Element subElement = (Element) subChild;
                                    System.out.println(subElement.getTagName() + " : " + subElement.getAttribute("value"));
                                }
                            }
                        } else {
                            //Logic for other tags
                            System.out.println(element.getTagName() + ": " + element.getAttribute("value"));
                        }
                    }
                }
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void parsePatientData(String xml, Patient patient){
        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = db.parse(new org.xml.sax.InputSource(new StringReader(xml)));
            //System.out.println(doc.getElementsByTagName("Patient"));
            int patientCount = doc.getElementsByTagName("Patient").getLength();
            System.out.println("Elements: " + patientCount);
            StringBuilder givenName = new StringBuilder();

            //Parse Child elements
            for (int p = 0; p < patientCount; p++){
                NodeList children = doc.getElementsByTagName("Patient").item(p).getChildNodes();
                NodeList subChildren = null;
                Node child = null;

                for (int i = 0; i < children.getLength(); i++) {
                    child = children.item(i);
                    int givenIndex = 0;
                    if (child.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) child;

                        //Logic for Patient Names
                        if (element.getTagName().equals("name")) {
                            subChildren = element.getChildNodes();
                            Node subChild = null;
                            for (int j = 0; j < subChildren.getLength(); j++) {
                                subChild = subChildren.item(j);
                                if (subChild.getNodeType() == Node.ELEMENT_NODE) {
                                    Element subElement = (Element) subChild;
                                    if (subElement.getTagName().equals("family"))
                                        patient.setPatientLname(subElement.getAttribute("value"));
                                    else if (subElement.getTagName().equals("given")) {
                                        if (subElement.getAttribute("value").length() > 1) {
                                            if (givenIndex==0) {
                                                givenName.append(subElement.getAttribute("value"));
                                                givenName.append(" ");
                                                patient.setPatientFname(subElement.getAttribute("value"));
                                                givenIndex++;
                                            }
                                        }
                                        else if (subElement.getAttribute("value").length() == 1) {
                                            givenName.append(subElement.getAttribute("value"));
                                            givenName.append(".");
                                            patient.setPatientMidName(subElement.getAttribute("value") + ".");
                                        }
                                    }
                                    //System.out.println(subElement.getTagName() + ": " + subElement.getAttribute("value"));
                                }
                            }
                        }
                        //Logic for address
                        else if (element.getTagName().equals("address")) {
                            subChildren = element.getChildNodes();
                            Node subChild = null;
                            for (int j = 0; j < subChildren.getLength(); j++) {
                                subChild = subChildren.item(j);
                                if (subChild.getNodeType() == Node.ELEMENT_NODE) {
                                    Element subElement = (Element) subChild;
                                    //System.out.println(subElement.getTagName() + " : " + subElement.getAttribute("value"));
                                    if (subElement.getTagName().equals("use")){
                                        patient.setAddressType(subElement.getAttribute("value"));
                                    }else if (subElement.getTagName().equals("line")){
                                        patient.setAddressLine1(subElement.getAttribute("value"));
                                    }else if (subElement.getTagName().equals("city")){
                                        patient.setCity(subElement.getAttribute("value"));
                                    }else if (subElement.getTagName().equals("state")){
                                        patient.setState(subElement.getAttribute("value"));
                                    }else if (subElement.getTagName().equals("postalCode")){
                                        patient.setZipCode(subElement.getAttribute("value"));
                                    }
                                }
                            }
                        } else {
                            //Logic for other tags
                            //System.out.println(element.getTagName() + ": " + element.getAttribute("value"));
                            if (element.getTagName().equals("gender")){
                                patient.setGender(element.getAttribute("value"));
                            }else if (element.getTagName().equals("birthDate")){
                                patient.setBirthDt(Utility.getStandardDate(element.getAttribute("value")));
                            }else if (element.getTagName().equals("id")){
                                patient.setPatientId(element.getAttribute("value"));
                                //patient.setPatientId(Integer.parseInt(element.getAttribute("value")));

                            }
                        }
                    }
                }
            }

            //patient.setPatientFname(givenName.toString());

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<SearchPatient> parsePatientDataForSearch(String xml){
        List<SearchPatient> patientSearch = new ArrayList<SearchPatient>();
        SearchPatient patient = null;
        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = db.parse(new org.xml.sax.InputSource(new StringReader(xml)));
            //System.out.println(doc.getElementsByTagName("Patient"));
            int patientCount = doc.getElementsByTagName("Patient").getLength();
            System.out.println("Elements: " + patientCount);
            StringBuilder givenName = new StringBuilder();

            //Parse Child elements
            for (int p = 0; p < patientCount; p++){
                NodeList children = doc.getElementsByTagName("Patient").item(p).getChildNodes();
                NodeList subChildren = null;
                Node child = null;
                patient = new SearchPatient();

                for (int i = 0; i < children.getLength(); i++) {
                    child = children.item(i);
                    if (child.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) child;

                        //Logic for Patient Names
                        if (element.getTagName().equals("name")) {
                            subChildren = element.getChildNodes();
                            Node subChild = null;
                            for (int j = 0; j < subChildren.getLength(); j++) {
                                subChild = subChildren.item(j);
                                if (subChild.getNodeType() == Node.ELEMENT_NODE) {
                                    Element subElement = (Element) subChild;
                                    if (subElement.getTagName().equals("family"))
                                        patient.setLastName(subElement.getAttribute("value"));
                                    else if (subElement.getTagName().equals("given")) {
                                        if (subElement.getAttribute("value").length() > 1) {
                                            patient.setFirstName(subElement.getAttribute("value"));
                                        }
                                        else if (subElement.getAttribute("value").length() == 1) {
                                            patient.setMiddleName(subElement.getAttribute("value"));
                                        }
                                    }
                                    //System.out.println(subElement.getTagName() + ": " + subElement.getAttribute("value"));
                                }
                            }
                        } else {
                            //Logic for other tags
                            //System.out.println(element.getTagName() + ": " + element.getAttribute("value"));
                            if (element.getTagName().equals("gender")){
                                patient.setGender(element.getAttribute("value"));
                            }else if (element.getTagName().equals("birthDate")){
                                patient.setDateOfBirth(Utility.getStandardDate(element.getAttribute("value")));
                            }else if (element.getTagName().equals("id")){
                                patient.setId(element.getAttribute("value"));
                            }
                        }
                    }
                }

                patientSearch.add(patient);
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return patientSearch;
    }

    public static void parseObervationsForPatient(String xml, List<Observation> observations, List<Observation> riskfactros){
        Observation observation = null;
        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = db.parse(new org.xml.sax.InputSource(new StringReader(xml)));
            //System.out.println(doc.getElementsByTagName("Patient"));
            int observationCount = doc.getElementsByTagName("Observation").getLength();
            System.out.println("Elements: " + observationCount);

            //Parse Child elements
            for (int p = 0; p < observationCount; p++){
                NodeList children = doc.getElementsByTagName("Observation").item(p).getChildNodes();
                NodeList subChildren = null;
                Node child = null;
                observation = new Observation();
                List<Component> components = new ArrayList<Component>();

                for (int i = 0; i < children.getLength(); i++) {
                    child = children.item(i);
                    if (child.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) child;

                        Component component = new Component();
                        //Logic for Observation details
                        if (element.getTagName().equals("component")) {
                            subChildren = element.getChildNodes();
                            Node subChild = null;

                            for (int j = 0; j < subChildren.getLength(); j++) {
                                //component = new Component();
                                subChild = subChildren.item(j);
                                //System.out.println("Subchild: " + subChild.getNodeType() + " " + subChild.toString());
                                if (subChild.getNodeType() == Node.ELEMENT_NODE) {
                                    Element subElement = (Element) subChild;
                                    NodeList subSubChildren = null;

                                    if (subElement.getTagName().equals("code") || subElement.getTagName().equals("valueCodeableConcept")) {
                                        subSubChildren = subElement.getChildNodes();
                                        Node subSubChild = null;
                                        for (int k = 0; k < subSubChildren.getLength(); k++){
                                            subSubChild = subSubChildren.item(k);
                                            if (subSubChild.getNodeType() == Node.ELEMENT_NODE){
                                                Element subSubElement = (Element) subSubChild;
                                                NodeList codingChildren = subSubElement.getChildNodes();
                                                for (int l=0; l<codingChildren.getLength(); l++) {
                                                    Node codingChild = codingChildren.item(l);
                                                    if (codingChild.getNodeType() == Node.ELEMENT_NODE) {
                                                        Element codingElement = (Element) codingChild;
                                                        //System.out.println("codingElement: " + codingElement.getTagName());
                                                        if (codingElement.getTagName().equals("system")) {
                                                            component.setSystem(codingElement.getAttribute("value"));
                                                        } else if (codingElement.getTagName().equals("code")) {
                                                            component.setCode(codingElement.getAttribute("value"));
                                                        } else if (codingElement.getTagName().equals("display")) {
                                                            component.setDisplay(codingElement.getAttribute("value"));
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    else if (subElement.getTagName().equals("valueQuantity")) {
                                        subSubChildren = subElement.getChildNodes();
                                        Node subSubChild = null;
                                        for (int k = 0; k < subSubChildren.getLength(); k++){
                                            subSubChild = subSubChildren.item(k);
                                            if (subSubChild.getNodeType() == Node.ELEMENT_NODE){
                                                Element subSubElement = (Element) subSubChild;
                                                //System.out.println("subSubElement: " + subSubElement.getTagName() + " " + subSubElement.getAttribute("value"));
                                                if (subSubElement.getTagName().equals("value")){
                                                    component.setValue(subSubElement.getAttribute("value"));
                                                }else if (subSubElement.getTagName().equals("unit")){
                                                    component.setUnit(subSubElement.getAttribute("value"));
                                                }

                                            }
                                        }
                                    }
                                    //System.out.println(subElement.getTagName() + ": " + subElement.getAttribute("value"));
                                }
                            }
                            components.add(component);
                        } else if (element.getTagName().equals("id")) {
                            observation.setObservationId(element.getAttribute("value"));
                        } else if (element.getTagName().equals("status")) {
                            observation.setStatus(element.getAttribute("value"));
                        }  else if (element.getTagName().equals("effectiveDateTime")) {
                            observation.setEffectiveDate(Utility.getStandardDate(element.getAttribute("value")));
                            DateTime dt = new DateTime(element.getAttribute("value"));
                            DateTimeFormatter fmt = DateTimeFormat.forPattern("MMM dd yyyy hh:mm:ss aaa");
                            observation.setEffectiveDateDisplay(dt.toString(fmt));
                        } else if (element.getTagName().equals("performer")) {
                            subChildren = element.getChildNodes();
                            Node subChild;

                            for (int j = 0; j < subChildren.getLength(); j++) {
                                subChild = subChildren.item(j);
                                if (subChild.getNodeType() == Node.ELEMENT_NODE) {
                                    Element subElement = (Element) subChild;
                                    //System.out.println("subChild: " + subElement.getTagName());
                                    if (subElement.getTagName().equals("reference")) {
                                        observation.setPerformerReference(subElement.getAttribute("value"));
                                    }else if (subElement.getTagName().equals("display")){
                                        observation.setPerformerName(subElement.getAttribute("value"));
                                    }
                                }
                            }
                        } else if (element.getTagName().equals("category")) {
                            subChildren = element.getChildNodes();
                            Node subChild;

                            for (int j = 0; j < subChildren.getLength(); j++) {
                                subChild = subChildren.item(j);
                                if (subChild.getNodeType() == Node.ELEMENT_NODE) {
                                    Element subElement = (Element) subChild;
                                    if (subElement.getTagName().equals("coding")) {
                                        NodeList subSubChildren = null;
                                        subSubChildren = subElement.getChildNodes();
                                        Node subSubChild = null;
                                        for (int k = 0; k < subSubChildren.getLength(); k++){
                                            subSubChild = subSubChildren.item(k);
                                            if (subSubChild.getNodeType() == Node.ELEMENT_NODE){
                                                Element subSubElement = (Element) subSubChild;
                                                if (subSubElement.getTagName().equals("code")){
                                                    observation.setCategorycode(subSubElement.getAttribute("value"));
                                                }

                                            }
                                        }
                                    }
                                }
                            }
                        }else {
                            //Logic for other tags
                            //System.out.println(element.getTagName() + ": " + element.getAttribute("value"));
                        }

                        observation.setComponents(components);
                    }
                }
                /*
                observations.add(observation);
                System.out.println(observation);
                System.out.println(observation.getStatus());
                System.out.println(observation.getCategorycode());
                System.out.println(observation.getComponents());
                System.out.println(observation.getPerformerReference());

                System.out.println(observation.getCategorycode()==null);
                */
                if (observation.getCategorycode() == null){
                    riskfactros.add(observation);
                }else{observations.add(observation);}

                /*if (observation.getCategorycode() != null && !observation.getCategorycode().isEmpty()){
                    observations.add(observation);
                    break;
                }else{
                    riskfactros.add(observation);
                    break;
                }*/



            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<AllergyIntolerance> parseAllergyIntolerancesForPatient(String xml){
        List<AllergyIntolerance> allergyIntolerances = new ArrayList<AllergyIntolerance>();
        AllergyIntolerance allergyIntolerance = null;
        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = db.parse(new org.xml.sax.InputSource(new StringReader(xml)));
            //System.out.println(doc.getElementsByTagName("Patient"));
            int allergyIntoleranceCount = doc.getElementsByTagName("AllergyIntolerance").getLength();
            System.out.println("Elements: " + allergyIntoleranceCount);

            //Parse Child elements
            for (int p = 0; p < allergyIntoleranceCount; p++){
                NodeList children = doc.getElementsByTagName("AllergyIntolerance").item(p).getChildNodes();
                NodeList subChildren = null;
                Node child = null;
                allergyIntolerance = new AllergyIntolerance();
                List<Identifier> identifiers = new ArrayList<Identifier>();
                List<Substance> substances = new ArrayList<Substance>();

                for (int i = 0; i < children.getLength(); i++) {
                    child = children.item(i);
                    if (child.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) child;

                        //Logic for AllergyIntolerance details
                        if (element.getTagName().equals("identifier")) {
                            subChildren = element.getChildNodes();
                            Node subChild = null;
                            Identifier identifier = null;
                            for (int j = 0; j < subChildren.getLength(); j++) {
                                identifier = new Identifier();
                                subChild = subChildren.item(j);
                                if (subChild.getNodeType() == Node.ELEMENT_NODE) {
                                    Element subElement = (Element) subChild;
                                    NodeList subSubChildren = null;

                                    if (subElement.getTagName().equals("system")) {
                                        identifier.setSystem(subElement.getAttribute("value"));
                                    } else if (subElement.getTagName().equals("value")) {
                                        identifier.setValue(subElement.getAttribute("value"));
                                    }
                                }
                                identifiers.add(identifier);
                            }
                        }else if (element.getTagName().equals("substance")) {
                            subChildren = element.getChildNodes();
                            Node subChild = null;
                            Substance substance = null;
                            for (int j = 0; j < subChildren.getLength(); j++) {
                                substance = new Substance();
                                subChild = subChildren.item(j);
                                if (subChild.getNodeType() == Node.ELEMENT_NODE) {
                                    Element subElement = (Element) subChild;
                                    NodeList subSubChildren = null;
                                    if (subElement.getTagName().equals("coding")) {
                                        subSubChildren = subElement.getChildNodes();
                                        Node subSubChild = null;
                                        for (int k = 0; k < subSubChildren.getLength(); k++) {
                                            subSubChild = subSubChildren.item(k);
                                            if (subSubChild.getNodeType() == Node.ELEMENT_NODE) {
                                                Element subSubElement = (Element) subSubChild;
                                                if (subSubElement.getTagName().equals("system")) {
                                                    substance.setSystem(subSubElement.getAttribute("value"));
                                                } else if (subSubElement.getTagName().equals("code")) {
                                                    substance.setValue(subSubElement.getAttribute("value"));
                                                }
                                            }
                                        }
                                    }
                                    substances.add(substance);
                                }
                            }
                            //System.out.println(subElement.getTagName() + ": " + subElement.getAttribute("value"));
                        }
                    } else {
                        //Logic for other tags
                        //System.out.println(element.getTagName() + ": " + element.getAttribute("value"));
                    }
                    allergyIntolerance.setIdentifiers(identifiers);
                    allergyIntolerance.setSubstances(substances);
                }
            }
            allergyIntolerances.add(allergyIntolerance);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allergyIntolerances;
    }

    public static List<Condition> parseConditionForPatient(String xml){
        List<Condition> conditions = new ArrayList<Condition>();
        Condition condition = null;

        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = db.parse(new org.xml.sax.InputSource(new StringReader(xml)));
            //System.out.println(doc.getElementsByTagName("Patient"));
            int conditionCount = doc.getElementsByTagName("Condition").getLength();
            System.out.println("Elements: " + conditionCount);

            //Parse Child elements
            for (int p = 0; p < conditionCount; p++){
                NodeList children = doc.getElementsByTagName("Condition").item(p).getChildNodes();
                NodeList subChildren = null;
                Node child = null;
                condition = new Condition();

                for (int i = 0; i < children.getLength(); i++) {
                    child = children.item(i);
                    if (child.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) child;

                        //Logic for Observation details
                        if (element.getTagName().equals("severity")) {
                            subChildren = element.getChildNodes();
                            Node subChild = null;

                            for (int j = 0; j < subChildren.getLength(); j++) {
                                subChild = subChildren.item(j);
                                //System.out.println("Subchild: " + subChild.getNodeType() + " " + subChild.toString());
                                if (subChild.getNodeType() == Node.ELEMENT_NODE) {
                                    Element subElement = (Element) subChild;
                                    NodeList subSubChildren = null;

                                    if (subElement.getTagName().equals("coding")) {
                                        subSubChildren = subElement.getChildNodes();
                                        Node subSubChild = null;
                                        for (int k = 0; k < subSubChildren.getLength(); k++){
                                            subSubChild = subSubChildren.item(k);
                                            if (subSubChild.getNodeType() == Node.ELEMENT_NODE){
                                                Element subSubElement = (Element) subSubChild;
                                                if (subSubElement.getNodeType() == Node.ELEMENT_NODE) {
                                                    if (subSubElement.getTagName().equals("display")) {
                                                        condition.setServerityDesc(subSubElement.getAttribute("value"));
                                                    }else if (subSubElement.getTagName().equals("code")) {
                                                        condition.setServerityCode(Integer.parseInt(subSubElement.getAttribute("value")));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    //System.out.println(subElement.getTagName() + ": " + subElement.getAttribute("value"));
                                }
                            }
                        } else if (element.getTagName().equals("code")) {
                            subChildren = element.getChildNodes();
                            Node subChild = null;

                            for (int j = 0; j < subChildren.getLength(); j++) {
                                subChild = subChildren.item(j);
                                //System.out.println("Subchild: " + subChild.getNodeType() + " " + subChild.toString());
                                if (subChild.getNodeType() == Node.ELEMENT_NODE) {
                                    Element subElement = (Element) subChild;
                                    NodeList subSubChildren = null;

                                    if (subElement.getTagName().equals("coding")) {
                                        subSubChildren = subElement.getChildNodes();
                                        Node subSubChild = null;
                                        for (int k = 0; k < subSubChildren.getLength(); k++){
                                            subSubChild = subSubChildren.item(k);
                                            if (subSubChild.getNodeType() == Node.ELEMENT_NODE){
                                                Element subSubElement = (Element) subSubChild;
                                                if (subSubElement.getNodeType() == Node.ELEMENT_NODE){
                                                    if (subSubElement.getTagName().equals("display")) {
                                                        condition.setConditionDescription(subSubElement.getAttribute("value"));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    else if (subElement.getTagName().equals("text")) {
                                        condition.setConditionDisplay(subElement.getAttribute("value"));
                                    }
                                    //System.out.println(subElement.getTagName() + ": " + subElement.getAttribute("value"));
                                }
                            }
                        } else if (element.getTagName().equals("id")) {
                            condition.setConditionId(element.getAttribute("value"));
                        } else if (element.getTagName().equals("dateRecorded")) {
                            condition.setDateRecorded(Utility.getStandardDate(element.getAttribute("value")));
                        } else if (element.getTagName().equals("clinicalStatus")) {
                            condition.setClinicalStatus(element.getAttribute("value"));
                        }  else if (element.getTagName().equals("onsetDateTime")) {
                            condition.setOnsetDateTime(Utility.getStandardDate(element.getAttribute("value")));
                            DateTime dt = new DateTime(element.getAttribute("value"));
                            DateTimeFormatter fmt = DateTimeFormat.forPattern("MMM dd yyyy hh:mm:ss aaa");
                            condition.setOnsetDateTimeDisplay(dt.toString(fmt));
                        } else if (element.getTagName().equals("asserter")) {
                            subChildren = element.getChildNodes();
                            Node subChild;

                            for (int j = 0; j < subChildren.getLength(); j++) {
                                subChild = subChildren.item(j);
                                if (subChild.getNodeType() == Node.ELEMENT_NODE) {
                                    Element subElement = (Element) subChild;
                                    //System.out.println("subChild: " + subElement.getTagName());
                                    if (subElement.getTagName().equals("display")) {
                                        condition.setAsserter(subElement.getAttribute("value"));
                                    }else if (subElement.getTagName().equals("reference")) {
                                        condition.setAsserterId(subElement.getAttribute("value"));
                                    }
                                }
                            }
                        } else if (element.getTagName().equals("patient")) {
                            subChildren = element.getChildNodes();
                            Node subChild;

                            for (int j = 0; j < subChildren.getLength(); j++) {
                                subChild = subChildren.item(j);
                                if (subChild.getNodeType() == Node.ELEMENT_NODE) {
                                    Element subElement = (Element) subChild;
                                    //System.out.println("subChild: " + subElement.getTagName());
                                    if (subElement.getTagName().equals("display")) {
                                        condition.setPatientName(subElement.getAttribute("value"));
                                    }else if (subElement.getTagName().equals("reference")) {
                                        condition.setPatientId(subElement.getAttribute("value"));
                                    }
                                }
                            }
                        } else if (element.getTagName().equals("dateRecorded")) {
                            condition.setClinicalStatus(element.getAttribute("value"));
                        } else {
                            //Logic for other tags
                            //System.out.println(element.getTagName() + ": " + element.getAttribute("value"));
                        }
                        //observation.setComponents(components);
                    }
                }
                conditions.add(condition);
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return conditions;
    }

    public static List<Condition> parseConditionsForPatientJson(String json){
        Gson gson = new Gson();

        /*Condition condition = gson.fromJson(json, Condition.class);

        System.out.println("Conditions: " + condition.entry.size());


        List<Condition> cl = new ArrayList<Condition>();
        for (int i=0; i<condition.entry.size(); i++){
            System.out.println(condition.entry.get(0));
        }*/

        List<Condition> c = new ArrayList<Condition>();

        return c;
    }

    public static void parseProtoTypeImmunizaton(List<RequestPrototypeImmunization> immunizations){
        //Creating 3 fake immunizatons
        RequestPrototypeImmunization prototypeImmunization1 = new RequestPrototypeImmunization();
        RequestPrototypeImmunization prototypeImmunization2 = new RequestPrototypeImmunization();
        RequestPrototypeImmunization prototypeImmunization3 = new RequestPrototypeImmunization();

        prototypeImmunization1.setImmunizationName("Hepatitis B");
        prototypeImmunization2.setImmunizationName("Influenza");
        prototypeImmunization3.setImmunizationName("Measles, mumps, rubella");

        prototypeImmunization1.setImmunizationDate("2016-02-05");
        prototypeImmunization2.setImmunizationDate("2016-04-16");
        prototypeImmunization3.setImmunizationDate("2018-10-11");

        immunizations.add(prototypeImmunization1);
        immunizations.add(prototypeImmunization2);
        immunizations.add(prototypeImmunization3);
    }

    public static void parseProtoTypeSocialHistory(List<RequestPrototypeSocialHistory> socialHistories){
        RequestPrototypeSocialHistory socialHistory = new RequestPrototypeSocialHistory();
        RequestPrototypeSocialHistory socialHistory1 = new RequestPrototypeSocialHistory();

        socialHistory.setSocialHistoryRisk("Heart Disease");
        socialHistory1.setSocialHistoryRisk("Skin Cancer");

        socialHistories.add(socialHistory);
        socialHistories.add(socialHistory1);
    }

    public static void parsePrototypeAllergies(List<RequestPrototypeAllergies> allergies){
        RequestPrototypeAllergies allergy = new RequestPrototypeAllergies();
        RequestPrototypeAllergies allergy1 = new RequestPrototypeAllergies();
        RequestPrototypeAllergies allergy2 = new RequestPrototypeAllergies();
        RequestPrototypeAllergies allergy3 = new RequestPrototypeAllergies();

        allergy.setAllergy("Grass");
        allergy.setSeverity("Minor");

        allergy1.setAllergy("Pollen");
        allergy1.setSeverity("Major");

        allergy2.setAllergy("Cat");
        allergy2.setSeverity("Minor");

        allergy3.setAllergy("Dust");
        allergy3.setSeverity("Critical");

        allergies.add(allergy);
        allergies.add(allergy1);
        allergies.add(allergy2);
        allergies.add(allergy3);
    }
}
