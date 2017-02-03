package edu.gatech.health.helpers;

import edu.gatech.health.objects.AvailableDate;
import edu.gatech.health.objects.Component;
import edu.gatech.health.objects.Observation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Trevoris on 4/16/2016.
 * Helper class for java objects
 */
public class Helper {

    /**
     * Parse the observations list and group all observations by date
     * @param observations List of observations
     * @param availableDates List of Dates
     */
    public static void parseObservationsByDate(List<Observation> observations, List<String> availableDates){

        Map<String, List<Component>> componentMap = new HashMap<String, List<Component>>();
        Map<String, List<AvailableDate>> availableDateMap = new HashMap<String, List<AvailableDate>>();

        AvailableDate availableDate = AvailableDate.getInstance();

        for (Observation observation : observations) {
            //Check if date not already in availableDate list

            availableDate.setId(observation.getObservationId());
            availableDate.setDate(observation.getEffectiveDate().replaceAll("/", "-"));

            //availableDates.add(availableDate);

            if (!availableDates.contains(availableDate.getDate())){
                availableDates.add(availableDate.getDate());
            }
            /*if (availableDateMap.containsKey(observation.getEffectiveDate())){
                availableDateMap.get(observation.getEffectiveDate()).add(availableDate);
            }else{
                List<AvailableDate> availDates = new ArrayList<AvailableDate>();
                availDates.add(availableDate);
                availableDateMap.put(observation.getEffectiveDate(), availDates);
            }*/
        }
        availableDate.setAvailableDatesList(availableDates);
    }
}
