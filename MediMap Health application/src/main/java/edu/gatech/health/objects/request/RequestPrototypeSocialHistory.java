package edu.gatech.health.objects.request;

import edu.gatech.health.objects.request.RequestPrototypeImmunization;

/**
 * Created by Trevoris Jefferson on 4/30/2016.
 * Social history prototype
 */
public class RequestPrototypeSocialHistory {
    private String socialHistoryRisk;

    public RequestPrototypeSocialHistory(){}

    public String getSocialHistoryRisk() {
        return socialHistoryRisk;
    }

    public void setSocialHistoryRisk(String socialHistoryRisk) {
        this.socialHistoryRisk = socialHistoryRisk;
    }
}
