package edu.gatech.health.objects.request;

import java.util.List;

/**
 * Created by Trevoris Jefferson on 3/27/2016.
 * Object to be populated by incoming JSON
 */
public class RequestPatient {
    private String resourceType;
    private List<Name> name;
    private String gender;
    private String birthDate;
    private List<Address> address;
    private boolean active;

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public List<Name> getName() {
        return name;
    }

    public void setName(List<Name> name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public List<Address> getAddress() {
        return address;
    }

    public void setAddress(List<Address> address) {
        this.address = address;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    class Name{
        public List<String> family;
        public List<String> given;

        public Name(String family, String given){
            this.family.add(family);
            this.given.add(given);
        }
    }

    class Address{
        public List<String> line;
        public String city;
        public String state;
        public String postal;

        public Address(String line, String city, String state, String postal){
            this.line.add(line);
            this.city=city;
            this.state=state;
            this.postal=postal;
        }
    }
}
