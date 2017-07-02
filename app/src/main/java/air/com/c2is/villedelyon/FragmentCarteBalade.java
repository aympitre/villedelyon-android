package air.com.c2is.villedelyon;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import android.widget.Button;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;


/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentCarteBalade extends FragmentActivity {
    protected GoogleMap mMap;
    public static GoogleAnalytics analytics;
    public static Tracker tracker;
    public String tempMarker;
    public LatLng firstLocation = null;
    public LatLng oldLocation;
    public int flagFirstLocation = 0;
    public String[] myTab;

    public DialogBalade myDialBalade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);
        tracker = analytics.newTracker(getResources().getString(R.string.google_analytics_id));
        tracker.setScreenName("/Carto balade");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_carte);

        myDialBalade         = new DialogBalade(this);
        Config.myCarteBalade = this;

        FacebookSdk.sdkInitialize(getApplicationContext());

        Config.myActu = this;

        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "Oswald-Regular.ttf");

        ImageButton myBtMenu = (ImageButton) findViewById(R.id.bt_menu);
        ImageView myLogo     = (ImageView) findViewById(R.id.logo);
        // ACTION DES BOUTONS DE LA HOME
        myLogo.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        startActivity(new Intent(FragmentCarteBalade.this, MainActivity.class));
                    }
                }
        );
        // ACTION DES BOUTONS DE LA HOME
        myBtMenu.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        startActivity(new Intent(FragmentCarteBalade.this, MainActivity.class));
                    }
                }
        );

        ImageButton myBtParam = (ImageButton) findViewById(R.id.bt_param);
        ImageButton myBtFavoris = (ImageButton) findViewById(R.id.bt_favoris);
        myBtParam.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(FragmentCarteBalade.this, Parametre.class);
                        startActivityForResult(intent, 2);
                    }
                }
        );
        myBtFavoris.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(FragmentCarteBalade.this, favoris.class);
                        startActivityForResult(intent, 2);

                    }
                }
        );


        TextView myTitre = (TextView) findViewById(R.id.titre);
        myTitre.setTypeface(myTypeface);

        myTitre.setText(getResources().getString(R.string.libHomeBt3));

        Config.majNbeFav((TextView) findViewById(R.id.txt_nbe_favoris), this.getBaseContext());
        setUpMapIfNeeded();

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
        Config.myActu = this;

        setUpMapIfNeeded();
        Config.majNbeFav((TextView) findViewById(R.id.txt_nbe_favoris), this.getBaseContext());
        Config.showAlertNotif(this);
    }

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

    public void goReveilOn() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_notif)
                .setTitle("Réveil")
                .setMessage("Appuyer pour couper la sonnerie.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Config.mp.stop();
                        Config.killLocalNotification(getBaseContext());

                        Config.flagDirectSavoir = 1;

                        Intent intent = new Intent(FragmentCarteBalade.this, Reveil.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent, 0);

                        finish();
                    }

                })
                .show();


        SharedPreferences sharedPref = getSharedPreferences("vdl", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("alert_vdl", "");
        editor.commit();
    }

    private void setUpMap() {
        mMap.setMyLocationEnabled(true);

        String str_point = Config.myContentValue.get("points").toString();

        if (str_point.length() > 0) {
            myTab = str_point.split(";");

            int nbe = 0;
            for (int i = 0; i < myTab.length; i++) {
                String temp     = myTab[i].toString();
                String[] myTab2 = temp.split("\\|");

                if (myTab2.length > 0) {

                    MarkerOptions markerOptions;
                    if (nbe==(myTab.length-1)) {
                        markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_arrivee));
                    }else {
                        markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_neutre));
                    }

                    if (String.valueOf(myTab2[4]).equalsIgnoreCase("2")) {
                        if (nbe==(myTab.length-1)) {
                            markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_arrivee));
                        }

                        if (flagFirstLocation==1) {
                            traceLaLigne(oldLocation, new LatLng(Double.valueOf(myTab2[3]), Double.valueOf(myTab2[2])));
                        }else{
                            markerOptions     = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_depart));
                            flagFirstLocation = 1;
                            firstLocation     = new LatLng(Double.valueOf(myTab2[3]), Double.valueOf(myTab2[2]));
                        }
                        oldLocation = new LatLng(Double.valueOf(myTab2[3]), Double.valueOf(myTab2[2]));
                    }

                    nbe++;

                    markerOptions.position(new LatLng(Double.valueOf(myTab2[3]), Double.valueOf(myTab2[2])));
                    markerOptions.title(Config.killHtml(String.valueOf(myTab2[0])));
                    markerOptions.snippet(String.valueOf(i));

                    if (String.valueOf(myTab2[0]).length()>0) {
                        mMap.addMarker(markerOptions);
                        oldLocation = new LatLng(Double.valueOf(myTab2[3]), Double.valueOf(myTab2[2]));
                    }
                }
            }
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {

            @Override
            public boolean onMarkerClick(Marker arg0) {
                String title = arg0.getTitle();
                String snip = arg0.getSnippet();

                if (snip.length()>0) {
                    String temp = myTab[Integer.valueOf(snip)].toString();
                    String[] myTab2 = temp.split("\\|");

                    if (myTab2.length > 0) {
                        myDialBalade.show();
                        myDialBalade.setValeur(Config.killHtml(String.valueOf(myTab2[0])), String.valueOf(myTab2[1]));
                    }
                }
                return true;
            }

        });

        zoomTheMap();
    }

    public void traceLaLigne(LatLng pt1, LatLng pt2) {
        PolylineOptions rectOptions = new PolylineOptions()
                .add(pt1)
                .add(pt2);

        // Get back the mutable Polyline
        Polyline polyline = mMap.addPolyline(rectOptions);
        polyline.setColor(Color.RED);
    }

    public void zoomTheMap() {
        if (firstLocation != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 15));
        }else if (oldLocation != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(oldLocation, 15));
        }else{
            Location location = mMap.getMyLocation();

            if (location != null) {
                LatLng myLocation = new LatLng(location.getLatitude(),
                        location.getLongitude());

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
            }
        }
    }
}
