package air.com.c2is.villedelyon;

import java.net.URLDecoder;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import android.os.Handler;

public class SeDeplacer extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private DataBaseHelper myDbHelper;

    public Button myMenu1;
    public Button myMenu2;
    public Button myMenu3;

    public int flagLegende = 0;

    public LinearLayout layLegende;
    public WebView      description;
    public ImageButton  btLegende;
    public int flagMap = 0;
    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    public String urlAuto       = "https://download.data.grandlyon.com/wfs/grandlyon";
    public String urlParking    = "http://appvilledelyon.c2is.fr/opendata.php?flag=1";
    public String urlPmr        = "https://download.data.grandlyon.com/wfs/grandlyon";

    public String urlVelov      = "http://appvilledelyon.c2is.fr/opendata.php?flag=0";
    public String urlParcVelo   = "https://download.data.grandlyon.com/wfs/grandlyon";

    public List<NameValuePair> nameValuePairAuto;
    public List<NameValuePair> nameValuePairParking;
    public List<NameValuePair> nameValuePairPmr;
    public List<NameValuePair> nameValuePairVelov;
    public List<NameValuePair> nameValuePairParcVelo;

    public DialogLoading myDialLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);
        tracker = analytics.newTracker(getResources().getString(R.string.google_analytics_id));

        // Init des parametres des webservices
            nameValuePairAuto = new ArrayList<NameValuePair>(2);
            nameValuePairAuto.add(new BasicNameValuePair("SERVICE"      , "WFS"));
            nameValuePairAuto.add(new BasicNameValuePair("VERSION"      , "2.0.0"));
            nameValuePairAuto.add(new BasicNameValuePair("outputformat" , "GEOJSON"));
            nameValuePairAuto.add(new BasicNameValuePair("maxfeatures"  , "2000"));
            nameValuePairAuto.add(new BasicNameValuePair("request"      , "GetFeature"));
            nameValuePairAuto.add(new BasicNameValuePair("typename"     , "pvo_patrimoine_voirie.pvostationautopartage"));

            nameValuePairParking = new ArrayList<NameValuePair>(2);
        /*
        nameValuePairParking.add(new BasicNameValuePair("SERVICE"      , "WFS"));
            nameValuePairParking.add(new BasicNameValuePair("VERSION"      , "2.0.0"));
            nameValuePairParking.add(new BasicNameValuePair("outputformat" , "GEOJSON"));
            nameValuePairParking.add(new BasicNameValuePair("maxfeatures"  , "2000"));
            nameValuePairParking.add(new BasicNameValuePair("request"      , "GetFeature"));
            nameValuePairParking.add(new BasicNameValuePair("typename"     , "pvo_patrimoine_voirie.pvoparkingtr"));
*/
            nameValuePairPmr = new ArrayList<NameValuePair>(2);
            nameValuePairPmr.add(new BasicNameValuePair("SERVICE"      , "WFS"));
            nameValuePairPmr.add(new BasicNameValuePair("VERSION"      , "2.0.0"));
            nameValuePairPmr.add(new BasicNameValuePair("outputformat" , "GEOJSON"));
            nameValuePairPmr.add(new BasicNameValuePair("maxfeatures"  , "2000"));
            nameValuePairPmr.add(new BasicNameValuePair("request"      , "GetFeature"));
            nameValuePairPmr.add(new BasicNameValuePair("typename"     , "vdl_deplacements.emplacement_pmr"));

        nameValuePairVelov = new ArrayList<NameValuePair>(2);
        /*
        nameValuePairVelov.add(new BasicNameValuePair("SERVICE"      , "WFS"));
        nameValuePairVelov.add(new BasicNameValuePair("VERSION"      , "2.0.0"));
        nameValuePairVelov.add(new BasicNameValuePair("outputformat" , "GEOJSON"));
        nameValuePairVelov.add(new BasicNameValuePair("maxfeatures"  , "3030"));
        nameValuePairVelov.add(new BasicNameValuePair("request"      , "GetFeature"));
        nameValuePairVelov.add(new BasicNameValuePair("typename"     , "jcd_jcdecaux.jcdvelov"));
        nameValuePairVelov.add(new BasicNameValuePair("SRSNAME"     , "urn:ogc:def:crs:EPSG::4326"));
*/
        nameValuePairParcVelo = new ArrayList<NameValuePair>(2);
        nameValuePairParcVelo.add(new BasicNameValuePair("SERVICE"      , "WFS"));
        nameValuePairParcVelo.add(new BasicNameValuePair("VERSION"      , "2.0.0"));
        nameValuePairParcVelo.add(new BasicNameValuePair("outputformat" , "GEOJSON"));
        nameValuePairParcVelo.add(new BasicNameValuePair("maxfeatures"  , "3030"));
        nameValuePairParcVelo.add(new BasicNameValuePair("request"      , "GetFeature"));
        nameValuePairParcVelo.add(new BasicNameValuePair("typename"     , "pvo_patrimoine_voirie.pvostationnementvelo"));
        nameValuePairParcVelo.add(new BasicNameValuePair("SRSNAME"     , "urn:ogc:def:crs:EPSG::4326"));

        myDialLoading         = new DialogLoading(this);

        if (Config.MENU_ACTIVITE==2) {
            tracker.setScreenName("/Se deplacer à velo");
        }else if (Config.MENU_ACTIVITE==3) {
            tracker.setScreenName("/Se deplacer en voiture");
        } else {
            tracker.setScreenName("/Se deplacer à pied");
        }

        tracker.send(new HitBuilders.ScreenViewBuilder().build());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_se_deplacer);

        Config.mySeDeplacer = this;

        FacebookSdk.sdkInitialize(getApplicationContext());
        Config.myActu  =this;
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "Oswald-Regular.ttf");
        TextView myTitre    = (TextView) findViewById(R.id.titre);

        myTitre.setTypeface(myTypeface);

        description = (WebView) findViewById(R.id.description);
        WebSettings settings = description.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        description.setBackgroundColor(Color.TRANSPARENT);

        layLegende = (LinearLayout) findViewById(R.id.layLegende);

        ImageButton myBtMenu = (ImageButton) findViewById(R.id.bt_menu);

        ImageView myLogo     = (ImageView) findViewById(R.id.logo);
        // ACTION DES BOUTONS DE LA HOME
        myLogo.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        startActivity(new Intent(SeDeplacer.this, MainActivity.class));
                    }
                }
        );

        // ACTION DES BOUTONS DE LA HOME
        myBtMenu.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        startActivity(new Intent(SeDeplacer.this, MainActivity.class));
                    }
                }
        );

        ImageButton myBtParam   = (ImageButton) findViewById(R.id.bt_param);
        ImageButton myBtFavoris = (ImageButton) findViewById(R.id.bt_favoris);
        myBtParam.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(SeDeplacer.this, Parametre.class);
                        startActivityForResult(intent, 2);
                    }
                }
        );
        myBtFavoris.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(SeDeplacer.this, favoris.class);
                        startActivityForResult(intent, 2);

                    }
                }
        );

        ImageButton btClose = (ImageButton) findViewById(R.id.btClose);
        btClose.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        closeLegende();
                    }
                }
        );

        btLegende  = (ImageButton) findViewById(R.id.btLegende);
        btLegende.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (flagLegende==1) {
                            closeLegende();
                        }else{
                            openLegende();
                        }
                    }
                }
        );

        myMenu1    = (Button) findViewById(R.id.bt_menu1);
        myMenu2    = (Button) findViewById(R.id.bt_menu2);
        myMenu3    = (Button) findViewById(R.id.bt_menu3);

        if (Config.MENU_ACTIVITE==2) {
            myMenu2.setTextColor(getResources().getColor(R.color.blanc));
            myMenu2.setBackground(getResources().getDrawable(R.drawable.menu_actif));
        }else if (Config.MENU_ACTIVITE==3) {
            myMenu3.setTextColor(getResources().getColor(R.color.blanc));
            myMenu3.setBackground(getResources().getDrawable(R.drawable.menu_actif));
        }else{
            myMenu1.setTextColor(getResources().getColor(R.color.blanc));
            myMenu1.setBackground(getResources().getDrawable(R.drawable.menu_actif));
        }

        myMenu1.setTypeface(myTypeface);
        myMenu2.setTypeface(myTypeface);
        myMenu3.setTypeface(myTypeface);

        // *** Bouton du menu
        myMenu1.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Config.MENU_ACTIVITE = 1;
                        setRollMenu();
                        closeLegende();

                        mMap.clear();
                        if (flagMap!=1) {
                            flagMap = 1;
                            mMap.clear();

                            myAsyncTask2 myWebFetch = new myAsyncTask2();
                            myWebFetch.flag = 1;
                            myWebFetch.execute();
                        }
                    }
                }
        );
        myMenu2.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Config.MENU_ACTIVITE = 2;
                        setRollMenu();
                        closeLegende();

                        showLoading();

                        final Handler handler = new Handler();

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mMap.clear();
                                if (flagMap!=2) {
                                    flagMap = 2;
                                    mMap.clear();

                                    myAsyncTask2 myWebFetch = new myAsyncTask2();
                                    myWebFetch.flag = 2;
                                    myWebFetch.execute();
                                }
                            }
                        }, 100);

                    }
                }
        );
        myMenu3.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Config.MENU_ACTIVITE = 3;
                        setRollMenu();
                        closeLegende();

                        showLoading();

                        final Handler handler = new Handler();

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mMap.clear();
                                if (flagMap!=3) {
                                    flagMap = 3;
                                    mMap.clear();

                                    myAsyncTask2 myWebFetch = new myAsyncTask2();
                                    myWebFetch.flag = 3;
                                    myWebFetch.execute();
                                }
                            }
                        }, 100);

                    }
                }
        );

        myDbHelper = new DataBaseHelper(this.getBaseContext());
        try {
            myDbHelper.openDataBase();
            Log.d("myTag", "ouverture bdd ok");
        }catch(SQLException sqle){
            Log.d("myTag", "ouverture bdd KO");
            throw sqle;
        }

        Config.majNbeFav((TextView) findViewById(R.id.txt_nbe_favoris), this.getBaseContext());
        setUpMapIfNeeded();
    }

    public void loadLegende(int p_param) {
        if (p_param==3) {
            description.loadUrl("file:///android_asset/legende3.html");
        }else if (p_param==2) {
            description.loadUrl("file:///android_asset/legende2.html");
        }else{
            description.loadUrl("file:///android_asset/legende1.html");
        }

    }

    public void showLoading() {
        myDialLoading.show();
    }
    public void hideLoading() {
        myDialLoading.hide();
    }

    public void openLegende () {
        layLegende.setVisibility(View.VISIBLE);
        loadLegende(Config.MENU_ACTIVITE);
        flagLegende = 1;
    }
    public void closeLegende () {
        layLegende.setVisibility(View.GONE);
        flagLegende = 0;
    }

    public void goReveilOn() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_notif)
                .setTitle("Réveil")
                .setMessage("Appuyer pour couper la sonnerie.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Config.mp.stop();
                        //                            stopReveil();
                        //                          killAlarme();

                        Config.flagDirectSavoir = 1;

                        Intent intent = new Intent(SeDeplacer.this, Reveil.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent, 0);

                        finish();
                    }

                })
                .show();


        SharedPreferences sharedPref = getSharedPreferences("vdl", Context.MODE_WORLD_WRITEABLE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("alert_vdl", "");
        editor.commit();
    }

    public void setRollMenu() {
        if (Config.MENU_ACTIVITE == 2) {
            myMenu1.setTextColor(getResources().getColor(R.color.rouge));
            myMenu1.setBackground(getResources().getDrawable(R.drawable.menu));
            myMenu2.setTextColor(getResources().getColor(R.color.blanc));
            myMenu2.setBackground(getResources().getDrawable(R.drawable.menu_actif));
            myMenu3.setTextColor(getResources().getColor(R.color.rouge));
            myMenu3.setBackground(getResources().getDrawable(R.drawable.menu));

        } else if (Config.MENU_ACTIVITE == 3) {
            myMenu1.setTextColor(getResources().getColor(R.color.rouge));
            myMenu1.setBackground(getResources().getDrawable(R.drawable.menu));
            myMenu2.setTextColor(getResources().getColor(R.color.rouge));
            myMenu2.setBackground(getResources().getDrawable(R.drawable.menu));
            myMenu3.setTextColor(getResources().getColor(R.color.blanc));
            myMenu3.setBackground(getResources().getDrawable(R.drawable.menu_actif));

        } else {
            myMenu1.setTextColor(getResources().getColor(R.color.blanc));
            myMenu1.setBackground(getResources().getDrawable(R.drawable.menu_actif));
            myMenu2.setTextColor(getResources().getColor(R.color.rouge));
            myMenu2.setBackground(getResources().getDrawable(R.drawable.menu));
            myMenu3.setTextColor(getResources().getColor(R.color.rouge));
            myMenu3.setBackground(getResources().getDrawable(R.drawable.menu));
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
        Config.myActu  =this;
        setUpMapIfNeeded();
        Config.majNbeFav((TextView) findViewById(R.id.txt_nbe_favoris), this.getBaseContext());
        Config.showAlertNotif(this);
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }

            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                public void onMapLoaded() {
                    zoomTheMap();
                }
            });
        }
    }


    /*
    public GoogleMap.OnCameraChangeListener getCameraChangeListener()
    {
        return new GoogleMap.OnCameraChangeListener()
        {
            @Override
            public void onCameraChange(CameraPosition position)
            {
                Log.d("Zoom", "Zoom: " + position.zoom);
                if (position.zoom>18) {
                    loadPmr();
                }else{
                    killPmr();
                }
            }
        };
    }
    */

    private void showMapVoiture2() {
        Cursor c = myDbHelper.loadParking();

        try {
            if (c != null) {
                while (c.moveToNext()) {
                    MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_parking));
                    markerOptions.position(new LatLng(c.getFloat(0), c.getFloat(1)));
                    markerOptions.title(c.getString(2));
                    mMap.addMarker(markerOptions);
                }
            }
        } catch (SQLException sqle) {
            throw sqle;
        }

        c = myDbHelper.loadAutolib();

        try {
            if (c != null) {
                while (c.moveToNext()) {
                    MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_autolib));
                    markerOptions.position(new LatLng(c.getFloat(0), c.getFloat(1)));
                    markerOptions.title("Station Autolib");
                    mMap.addMarker(markerOptions);
                }
            }
        } catch (SQLException sqle) {
            throw sqle;
        }

        loadPmr();
    }

    public void loadPmr() {
        if (flagMap==3) {
            Cursor c = myDbHelper.loadPmr();

            try {
                if (c != null) {
                    while (c.moveToNext()) {
                        MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_pmr));
                        markerOptions.position(new LatLng(c.getFloat(0), c.getFloat(1)));
                        markerOptions.title("Stationnement PMR");
                        markerOptions.snippet(c.getString(2) + " " + c.getString(3));

                        mMap.addMarker(markerOptions);
                    }
                }
            } catch (SQLException sqle) {
                throw sqle;
            }
        }
    }

    public void zoomTheMap() {
        Location location = mMap.getMyLocation();

        if (location != null) {
            LatLng myLocation = new LatLng(location.getLatitude(),
                    location.getLongitude());

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
        }

    }

    public void killPmr() {
        if (flagMap==3) {



        }
    }

    private void showMapVelo() {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            // VELO'V
            JSONParser jParser  = new JSONParser();
            JSONObject json     = jParser.getJSONFromUrl(urlVelov, nameValuePairVelov);
            JSONArray contacts  = null;

            try {
                contacts = json.getJSONArray("features");

                for (int i = 0; i < contacts.length(); i++) {

                    JSONObject c = contacts.getJSONObject(i);

                    JSONObject o = c.getJSONObject("properties");
                    int nbeEmpl         = o.getInt("available_bikes");
                    int nbeEmplRestant  = o.getInt("available_bike_stands");

                 //   Log.d("myTag", "velo : " + nbeEmpl + "/" + (nbeEmpl+nbeEmplRestant) + " vélos disponibles");

                    MarkerOptions markerOptions = new MarkerOptions();

                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_velib));

                    markerOptions.position(new LatLng(o.getDouble("lat"), o.getDouble("lng")));

                    markerOptions.title("Station Velo'v");
                    markerOptions.snippet(nbeEmpl + "/" + (nbeEmpl+nbeEmplRestant) + " vélos disponibles");

                    mMap.addMarker(markerOptions);

                }
            } catch (JSONException e) {
                Log.d("myTag", "mon erreur : " + e.getMessage());
                e.printStackTrace();
            }


            // PARC A VELO
            jParser     = new JSONParser();
            json        = jParser.getJSONFromUrl(urlParcVelo, nameValuePairParcVelo);
            contacts    = null;

            try {
                contacts = json.getJSONArray("features");

                for (int i = 0; i < contacts.length(); i++) {
                    JSONObject c    = contacts.getJSONObject(i);

                    JSONObject o    = c.getJSONObject("properties");
                    String adresse  = o.getString("adresse");
                    String commune  = o.getString("commune");
                    JSONObject g    = c.getJSONObject("geometry");

                    MarkerOptions markerOptions = new MarkerOptions();

                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_neutre));
                    markerOptions.position(new LatLng(g.getJSONArray("coordinates").getDouble(1), g.getJSONArray("coordinates").getDouble(0)));

                    markerOptions.title("Parc à vélo");
                    markerOptions.snippet(URLDecoder.decode(adresse, "UTF-8"));

                    mMap.addMarker(markerOptions);
                }
            } catch (JSONException e) {
                Log.d("myTag", "mon erreur : " + e.getMessage());
                e.printStackTrace();
            }

        } catch (Exception e) {
            Log.d("myTag", "mon Exception : " + e.getMessage());
            //	Toast.makeText(Config.myResVehicule,"erreur : " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }



    private void showMapVelo2() {
        Cursor c = myDbHelper.loadVelo();

        try {
            if (c != null) {
                while (c.moveToNext()) {
                    MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_velib));
                    markerOptions.position(new LatLng(c.getFloat(0), c.getFloat(1)));
                    markerOptions.title("Station Velov");
                    mMap.addMarker(markerOptions);
                }
            }
        } catch (SQLException sqle) {
            throw sqle;
        }


    }

    private void showMapVoiture() {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            // AUTO-PARTAGE
            JSONParser jParser  = new JSONParser();
            JSONObject json     = jParser.getJSONFromUrl(urlAuto, nameValuePairAuto);
            JSONArray contacts   = null;

            try {
                contacts = json.getJSONArray("features");

                for(int i = 0; i < contacts.length(); i++){

                    JSONObject c    = contacts.getJSONObject(i);

                    JSONObject o    = c.getJSONObject("properties");
                    String nom      = o.getString("nom");
                    String nbeEmpl  = o.getString("nbemplacements");
                    JSONObject g    = c.getJSONObject("geometry");
                    String type     = o.getString("typeautopartage");

                    MarkerOptions markerOptions = new MarkerOptions();

                    if (type.equalsIgnoreCase("Citiz LPA")) {
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_autolib));
                    }else if (type.equalsIgnoreCase("SunMoov")) {
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_sunmoov));
                    }else if (type.equalsIgnoreCase("Wattmobile")) {
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_watt));
                    }else if (type.equalsIgnoreCase("Bluely")) {
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_bluely));
                    }

//                    Log.d("myTag", type + nbeEmpl + " emplacements");

                    markerOptions.position(new LatLng(g.getJSONArray("coordinates").getDouble(1), g.getJSONArray("coordinates").getDouble(0)));

                    markerOptions.title(type);
                    markerOptions.snippet(nbeEmpl + " emplacements");

                    mMap.addMarker(markerOptions);

                }
            } catch (JSONException e) {
                Log.d("myTag", "mon erreur : " + e.getMessage());
                e.printStackTrace();
            }


            // PARKINGS
            jParser  = new JSONParser();
            json     = jParser.getJSONFromUrl(urlParking, nameValuePairParking);
            contacts = null;

            try {
                contacts = json.getJSONArray("features");

                for(int i = 0; i < contacts.length(); i++){

                    JSONObject c    = contacts.getJSONObject(i);

                    JSONObject o    = c.getJSONObject("properties");
                    String nom      = o.getString("nom");
                    String etat  = o.getString("etat");
                    String nbeEmpl  = o.getString("capacitevoiture");
                    JSONObject g    = c.getJSONObject("geometry");

                    MarkerOptions markerOptions = new MarkerOptions();

                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_parking));


                    markerOptions.position(new LatLng(g.getJSONArray("coordinates").getDouble(1), g.getJSONArray("coordinates").getDouble(0)));

                    markerOptions.title(nom);
                    markerOptions.snippet(etat + " sur " + nbeEmpl + " emplacements");
  //                  Log.d("myTag", nom + etat + "sur" + nbeEmpl + " emplacements");

                    mMap.addMarker(markerOptions);

                }
            } catch (JSONException e) {
                Log.d("myTag", "mon erreur : " + e.getMessage());
                e.printStackTrace();
            }


            // PMR
            jParser  = new JSONParser();
            json     = jParser.getJSONFromUrl(urlPmr, nameValuePairPmr);
            contacts = null;

            try {
                contacts = json.getJSONArray("features");

                for(int i = 0; i < contacts.length(); i++){

                    JSONObject c    = contacts.getJSONObject(i);

                    JSONObject o    = c.getJSONObject("properties");
                    String nom      = o.getString("nom");
                    String nbeEmpl  = o.getString("nb_places");
                    JSONObject g    = c.getJSONObject("geometry");

                    MarkerOptions markerOptions = new MarkerOptions();

                    markerOptions.position(new LatLng(g.getJSONArray("coordinates").getDouble(1), g.getJSONArray("coordinates").getDouble(0)));
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_pmr));
                    markerOptions.title("Stationnement PMR");
                    markerOptions.snippet(nbeEmpl + " emplacements");

     //               Log.d("myTag", "Stationnement PMR" + nbeEmpl + " emplacements");

                    mMap.addMarker(markerOptions);
                }

            } catch (JSONException e) {
                Log.d("myTag", "mon erreur : " + e.getMessage());
                e.printStackTrace();
            }


        } catch (Exception e) {
            Log.d("myTag", "mon Exception : " + e.getMessage());
            //	Toast.makeText(Config.myResVehicule,"erreur : " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }


    private void showMapPied() {
        Cursor c = myDbHelper.loadPied();

        try {
            if (c != null) {
                while (c.moveToNext()) {
                    MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_metro));
                    markerOptions.position(new LatLng(c.getFloat(2), c.getFloat(3)));
                    markerOptions.title("Ligne " + c.getString(0));
                    markerOptions.snippet(c.getString(1));
                    mMap.addMarker(markerOptions);
                }
            }
        }catch(SQLException sqle){
            throw sqle;
        }

        c = myDbHelper.loadTaxi();

        try {
            if (c != null) {
                while (c.moveToNext()) {
                    MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_taxi));
                    markerOptions.position(new LatLng(c.getFloat(0), c.getFloat(1)));
                    markerOptions.title("Station de taxi");
                    mMap.addMarker(markerOptions);
                }
            }
        }catch(SQLException sqle){
            throw sqle;
        }

        c = myDbHelper.loadTram();

        try {
            if (c != null) {
                while (c.moveToNext()) {
                    MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_tramway));
                    markerOptions.position(new LatLng(c.getFloat(0), c.getFloat(1)));
                    markerOptions.title("Ligne " + c.getString(2));
                    markerOptions.snippet(c.getString(3));
                    mMap.addMarker(markerOptions);
                }
            }
        }catch(SQLException sqle){
            throw sqle;
        }

        c = myDbHelper.loadGare();

        try {
            if (c != null) {
                while (c.moveToNext()) {
                    MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_train));
                    markerOptions.position(new LatLng(c.getFloat(0), c.getFloat(1)));
                    markerOptions.title(c.getString(2));
                    mMap.addMarker(markerOptions);
                }
            }
        }catch(SQLException sqle){
            throw sqle;
        }

    }

    private void setUpMap() {
        mMap.setMyLocationEnabled(true);

        flagMap = 1;

        myAsyncTask2 myWebFetch = new myAsyncTask2();
        myWebFetch.flag = 1;
        myWebFetch.execute();
    }

    class myAsyncTask2 extends AsyncTask<Void, Void, Void> {
        TextView tv;
        int flag;

        myAsyncTask2()    {
            flag = 0;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (flag==1) {
                showMapPied();
            }else if (flag==2) {
                showMapVelo();
            }else if (flag==3) {
                showMapVoiture();
            }

            hideLoading();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("myTag", "doInBackground");
            return null;
        }
    }
}
