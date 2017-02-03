package edu.gatech.health.objects;

import java.util.List;

/**
 * Created by Trevoris Jefferson on 4/16/2016.
 * Available Date class
 */
public class AvailableDate {

    private static AvailableDate instance = new AvailableDate();

    public static AvailableDate getInstance() { return instance; }

    private AvailableDate(){};

    private String id;
    private String date;
    private List<String> availableDatesList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getAvailableDatesList() {
        return availableDatesList;
    }

    public void setAvailableDatesList(List<String> availableDatesList) {
        this.availableDatesList = availableDatesList;
    }

    /*@Override
    public boolean equals(Object obj) {
        boolean exists = false;

        if (obj instanceof AvailableDate){
            String compareVal = ((AvailableDate) obj).date;
            if (this.date.equals(compareVal)){
                exists = true;
            }
        }
        return exists;
    }*/
}
