package air.com.c2is.villedelyon;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import javax.xml.parsers.DocumentBuilderFactory;


/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentCarte extends FragmentActivity {
    protected GoogleMap mMap;
    public String tempMarker;
    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);
        tracker = analytics.newTracker(getResources().getString(R.string.google_analytics_id));
        tracker.setScreenName("/Liste cartographie");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_carte);

        Config.myFragCarte = this;

        FacebookSdk.sdkInitialize(getApplicationContext());

        Config.myActu = this;

        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "Oswald-Regular.ttf");

        ImageButton myBtMenu = (ImageButton) findViewById(R.id.bt_menu);

        ImageView myLogo     = (ImageView) findViewById(R.id.logo);
        // ACTION DES BOUTONS DE LA HOME
        myLogo.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        startActivity(new Intent(FragmentCarte.this, MainActivity.class));
                    }
                }
        );

        // ACTION DES BOUTONS DE LA HOME
        myBtMenu.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        startActivity(new Intent(FragmentCarte.this, MainActivity.class));
                    }
                }
        );

        ImageButton myBtParam   = (ImageButton) findViewById(R.id.bt_param);
        ImageButton myBtFavoris = (ImageButton) findViewById(R.id.bt_favoris);
        myBtParam.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(FragmentCarte.this, Parametre.class);
                        startActivityForResult(intent, 2);
                    }
                }
        );
        myBtFavoris.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(FragmentCarte.this, favoris.class);
                        startActivityForResult(intent, 2);

                    }
                }
        );

        TextView myTitre    = (TextView) findViewById(R.id.titre);
        myTitre.setTypeface(myTypeface);

        if (Config.CODE_DE_MON_ACTIVITE==1) {
            myTitre.setText(getResources().getString(R.string.libHomeBt1));
        }else if (Config.CODE_DE_MON_ACTIVITE==2) {
            myTitre.setText(getResources().getString(R.string.libHomeBt2));
        }else if (Config.CODE_DE_MON_ACTIVITE==3) {
            myTitre.setText(getResources().getString(R.string.libHomeBt3));
        }else if (Config.CODE_DE_MON_ACTIVITE==4) {
            myTitre.setText(getResources().getString(R.string.libHomeBt4));
        }else if (Config.CODE_DE_MON_ACTIVITE==5) {
            myTitre.setText(getResources().getString(R.string.libHomeBt5));
        }else if (Config.CODE_DE_MON_ACTIVITE==6) {
            myTitre.setText(getResources().getString(R.string.libHomeBt6));
        }

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
        Config.myActu  =this;
        setUpMapIfNeeded();
        Config.majNbeFav((TextView) findViewById(R.id.txt_nbe_favoris), this.getBaseContext());
        Config.showAlertNotif(this);
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

                        Intent intent = new Intent(FragmentCarte.this, Reveil.class);
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

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
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

    public void gotoEquipement(String p_titre) {
        for (int i=0;i<Config.pointCarto.size();i++) {

            String temp = "" + Config.pointCarto.get(i).get("titre");

            if (temp.equals(p_titre)) {
                String temp2 = "" + Config.pointCarto.get(i).get("id_equipement").toString();

                if (temp2.length()>0) {
                    Config.flagContentEquip = 0;
                }else {
                    Config.flagContentEquip = 1;
                }
                Config.myContentValue   = Config.pointCarto.get(i);
                Config.sql_type         = temp2.toString();
                Config.xml_id           = "";

                Intent intent = new Intent(FragmentCarte.this, FragmentDetailEquipement.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, 0);
            }
        }
    }


    private void setUpMap() {
        mMap.setMyLocationEnabled(true);

        for (int i=0;i<Config.pointCarto.size();i++) {
            String temp = "" + Config.pointCarto.get(i).get("latitude");
            String temp2 = "" + Config.pointCarto.get(i).get("longitude");

            if ((temp.length()>0)&&(temp2.length()>0)) {
                if (!temp.equalsIgnoreCase("0.0")) {
                    MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_map));

                    Double tempdbl1 = 0.0;
                    try {
                        tempdbl1 = Double.valueOf(temp);
                    } catch (Exception ex) {
                        throw ex;
                    }

                    Double tempdbl2 = 0.0;
                    try {
                        tempdbl2 = Double.valueOf(temp2);
                    } catch (Exception ex) {
                        throw ex;
                    }

                    markerOptions.position(new LatLng(tempdbl1, tempdbl2));

                    markerOptions.title(String.valueOf(Config.pointCarto.get(i).get("titre")));

                    mMap.addMarker(markerOptions);
                }
            }
        }

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            public void onInfoWindowClick(Marker marker) {
                gotoEquipement(tempMarker);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker arg0) {
                if (arg0.getTitle().equals(tempMarker)) {
                    gotoEquipement(tempMarker);
                    tempMarker = "";

                    return true;
                }

                tempMarker = arg0.getTitle();

                return false;

            }

        });

    }

    public void zoomTheMap() {
        Location location = mMap.getMyLocation();

        if (location != null) {
            if (location.getLatitude()!=0) {
                LatLng myLocation = new LatLng(location.getLatitude(),
                        location.getLongitude());

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
            }
        }else{
            if (Config.pointCarto.size()>0) {

                String temp  = "" + Config.pointCarto.get(0).get("latitude");
                String temp2 = "" + Config.pointCarto.get(0).get("longitude");

                if ((temp.length()>0)&&(temp2.length()>0)) {
                    if (!temp.equalsIgnoreCase("0.0")) {
                        Double tempdbl1 = 0.0;
                        try {
                            tempdbl1 = Double.valueOf(temp);
                        } catch (Exception ex) {
                            throw ex;
                        }

                        Double tempdbl2 = 0.0;
                        try {
                            tempdbl2 = Double.valueOf(temp2);
                        } catch (Exception ex) {
                            throw ex;
                        }

                        LatLng myLocation = new LatLng(tempdbl1, tempdbl2);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
                    }
                }
            }
        }

    }
}
