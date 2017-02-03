package edu.gatech.health.objects.request;

import edu.gatech.health.objects.Identifier;
import edu.gatech.health.objects.Patient;
import edu.gatech.health.objects.Substance;

import java.util.List;

/**
 * Created by Jason on 4/7/2016.
 */
public class RequestAllergyIntolerance {

    private String resourceType;
    private List<Identifier> indentifier;
    private Substance substance;
    private Patient patient;

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public List<Identifier> getIndentifier() {
        return indentifier;
    }

    public void setIndentifier(List<Identifier> indentifier) {
        this.indentifier = indentifier;
    }

    public Substance getSubstance() {
        return substance;
    }

    public void setSubstance(Substance substance) {
        this.substance = substance;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    class Identifier{
        public String system;
        public String value;

        public Identifier(String system, String value){
            this.system = system;
            this.value = value;
        }
    }

    class Substance{
        public List<Coding> coding;

        public Substance(List<Coding> coding){
            this.coding.add((Coding) coding);
        }

        class Coding{
            public String system;
            public String code;
            Coding(String system, String code){
                this.system = system;
                this.code = code;
            }
        }
    }

    class Patient{
        public String reference;

        public Patient(String reference){
            this.reference = reference;
        }
    }

}
