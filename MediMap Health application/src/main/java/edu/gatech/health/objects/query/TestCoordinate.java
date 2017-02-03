package edu.gatech.health.objects.query;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Trevoris on 4/10/2016.
 * Test class
 */
@Entity
public class TestCoordinate {

    @Id
    private String patientId;
    @GeneratedValue
    private long conditionId;
    private long x;
    private long y;
    private String description;

    protected TestCoordinate() {}

    public TestCoordinate(String patientId, long x, long y, String description) {
        this.patientId = patientId;
        this.x = x;
        this.y = y;
        this.description = description;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public long getConditionId() {
        return conditionId;
    }

    public void setConditionId(long conditionId) {
        this.conditionId = conditionId;
    }

    public long getX() {
        return x;
    }

    public void setX(long x) {
        this.x = x;
    }

    public long getY() {
        return y;
    }

    public void setY(long y) {
        this.y = y;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
