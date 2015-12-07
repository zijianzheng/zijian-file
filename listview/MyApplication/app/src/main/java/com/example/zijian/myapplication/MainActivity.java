package com.example.zijian.myapplication;

import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity {


    // Reading text file from assets folder
    StringBuffer sb = new StringBuffer();
    BufferedReader br = null;
    try {
        br = new BufferedReader(new InputStreamReader(getAssets().open("jsonshoutdata.txt")));
        String temp;
        while ((temp = br.readLine()) != null)
            sb.append(temp);
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        try {
            br.close(); // stop reading
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    myjsonstring = sb.toString();
    // Try to parse JSON
    try {
        urlist = new ArrayList<HashMap<String, String>>();
        // Creating JSONObject from String
        JSONObject jsonObjMain = new JSONObject(myjsonstring);

        // Creating JSONArray from JSONObject
        JSONArray jsonArray = jsonObjMain.getJSONArray("message");

        // JSONArray has four JSONObject
        for (int i = 0; i < jsonArray.length(); i++) {

            // Creating JSONObject from JSONArray
            JSONObject jsonObj = jsonArray.getJSONObject(i);

            // Getting data from individual JSONObject
            String message = jsonObj.getString("msg");

            HashMap<String, String> map = new HashMap<String, String>();

            map.put("msg", msg );

            urlist.add(map);

        }

    } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }

}



}
