package edu.gatech.health.objects;

import java.util.List;

/**
 * Created by Jason on 4/7/2016.
 */
public class AllergyIntolerance {

    private List<Identifier> identifiers;
    private List<Substance> substances;

    public List<Identifier> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<Identifier> identifiers) {
        this.identifiers = identifiers;
    }

    public List<Substance> getSubstances() {
        return substances;
    }

    public void setSubstances(List<Substance> substances) {
        this.substances = substances;
    }



}
