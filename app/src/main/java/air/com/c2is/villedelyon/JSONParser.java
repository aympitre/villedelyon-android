package air.com.c2is.villedelyon;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.appevents.AppEventsLogger;
import com.facebook.FacebookSdk;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.Reader;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONObject;
import org.apache.http.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpEntity;
import org.json.JSONException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.NameValuePair;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import org.apache.http.message.BasicNameValuePair;
import java.util.ArrayList;
import android.util.Base64;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.auth.UsernamePasswordCredentials;

public class JSONParser {

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    // constructor
    public JSONParser() {}

    public JSONObject getJSONFromUrl(String url) {
        // Making HTTP request
        try {
            URL myUrl = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) myUrl.openConnection();
            is = urlConnection.getInputStream();

        } catch (IOException e) {
            Log.wtf("myTag", "mon defaultHttpClient : " + e.getMessage());
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            //Log.d("myTag", "mon json : " + json);
        } catch (Exception e) {
            Log.wtf("myTag", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
            Log.wtf("myTag","retour json : " + jObj.get("name").toString());
        } catch (JSONException e) {
            Log.wtf("myTag", "Error parsing data " + e.toString() + '/' + json);
        }

        // return JSON String
        return jObj;

    }
}