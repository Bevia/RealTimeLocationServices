package com.corebaseit.realtimelocationservices.rest;

import com.corebaseit.realtimelocationservices.models.LocationModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vbevia on 09/07/16.
 */
public class RestLocations {

    public static List<LocationModel> parseLocationFeed(String content) {

        try {

            /**
             * Grab the content from caller:
             */
            JSONObject theContent = new JSONObject(content);
            /**
             * getting object string from JSON :
             */
            JSONObject mainObject = theContent.getJSONObject("corebaseit");
            /**
             * getting the Sections Array string from JSON:
             */
            JSONArray sectionsArray = mainObject.getJSONArray("sections");
            /**
             * Array list to store JSON retrived data:
             */
            List<LocationModel> sectionsModelList = new ArrayList<>();

            for (int i = 0; i < sectionsArray.length(); i++) {

                /**
                 * Grab all the objects inside the array:
                 */

                JSONObject objs = sectionsArray.getJSONObject(i);

                LocationModel locationModelData = new LocationModel();

                locationModelData.setNumber(objs.getInt("number"));
                locationModelData.setCity(objs.getString("city"));
                locationModelData.setAddress(objs.getString("address"));
                locationModelData.setLatitude(objs.getDouble("latitude"));
                locationModelData.setLongitude(objs.getDouble("longitude"));

               /* Log.d("RESULTS", "in REST: " + locationModelData.getLongitude() + "  "
                + locationModelData.getLatitude() + "\n  " +  locationModelData.getCity() +  "   " +
                        locationModelData.getNumber() + "   " + locationModelData.getAddress());*/

                sectionsModelList.add(locationModelData);
            }

            return sectionsModelList;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
