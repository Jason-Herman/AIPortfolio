package edu.gatech.health.helpers;


import com.google.gson.Gson;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Trevoris Jefferson on 3/13/2016.
 * Implement commonly used methods here
 */
public class Utility {


    public static String makeGetRequest (URL url){
        StringBuffer strOutput = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/xml+fhir");
            connection.setRequestProperty("Accept-Charset", "utf-8");
            //connection.setRequestProperty("Accept-Encoding", "gzip");

            if (connection.getResponseCode() != 200) {
                throw new RuntimeException("Connection failed: " + connection.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String output;
            strOutput = new StringBuffer();
            while ((output = br.readLine()) != null) {
                //System.out.println(output);
                strOutput.append(output);
            }

            connection.disconnect();


        } catch (MalformedURLException e) {
            e.printStackTrace();
            strOutput = new StringBuffer();
        } catch (IOException e) {
            e.printStackTrace();
            strOutput = new StringBuffer();
        }
        return strOutput.toString();
    }

    public static String makePostRequest (URL url, Object requestObject){
        StringBuffer strOutput = null;
        try {
            //String parsedRequest = JSONObject.escape(request);
            Gson gson = new Gson();
            //System.out.println("Url: " + url + "\nrequest data: " + gson.toJson(requestObject));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/xml+fhir");
            connection.setRequestProperty("Accept-Charset", "utf-8");
            connection.setRequestProperty("Content-Type", "application/json+fhir; charset=UTF-8");
            //connection.setRequestProperty("Accept-Encoding", "gzip");
            connection.setDoOutput(true);

            //Append request data to POST connection
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(gson.toJson(requestObject));
            outputStream.flush();
            outputStream.close();

            if (connection.getResponseCode() >= 400) {
                throw new RuntimeException("Connection failed: " + connection.getResponseCode()
                        + "\nReason: " + connection.getResponseMessage());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String output;
            strOutput = new StringBuffer();
            while ((output = br.readLine()) != null) {
                //System.out.println(output);
                strOutput.append(output);
            }

            connection.disconnect();


        } catch (MalformedURLException e) {
            e.printStackTrace();
            strOutput = new StringBuffer();
        } catch (IOException e) {
            e.printStackTrace();
            strOutput = new StringBuffer();
        }
        return strOutput.toString();
    }

    //Determine if a string is a number type
    public static boolean isNumeric(String s) {
        return s.matches("[-+]?\\d*\\.?\\d+");
    }

    //Convert FHIR date format to standard MM/dd/yyyy
    public static String getStandardDate(String unformattedDate){
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            Date date = sdf.parse(unformattedDate);
            sdf = new SimpleDateFormat("MM/dd/yyyy");
            return sdf.format(date);
        } catch (ParseException e) {
            return unformattedDate;
        }
    }

    //Convert FHIR date format to a comparable date format yyyy-MM-dd
    public static String getComparableDate(String unformattedDate){
        String pattern = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            Date date = sdf.parse(unformattedDate);
            sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(date);
        } catch (ParseException e) {
            return unformattedDate;
        }
    }
}
