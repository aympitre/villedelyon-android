package air.com.c2is.villedelyon;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;


public class ListType extends android.support.v4.app.FragmentActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    protected GoogleMap mMap;
    public String tempMarker;
    public LinearLayout layCarto;
    public View layFragment;
    public Button myMenu1;
    public Button myMenu2;
    public Button myMenu3;
    public int flagCarto = 0;
    public RelativeLayout layBtCarto;
    public ImageButton btCarto;
    public ImageButton btListe;
    public int flagNoResume = 0;
    private static final int MY_LOCATION_REQUEST_CODE = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        }
    }

    public void changeBtCarto(int p_param) {
        if (p_param == 0) {
            btCarto.setVisibility(View.GONE);
            btListe.setVisibility(View.VISIBLE);
        } else {
            btCarto.setVisibility(View.VISIBLE);
            btListe.setVisibility(View.GONE);
        }
    }

    public void offAllMenu() {
        myMenu2.setTextColor(getResources().getColor(R.color.rouge));
        myMenu2.setBackground(getResources().getDrawable(R.drawable.menu));
        myMenu3.setTextColor(getResources().getColor(R.color.rouge));
        myMenu3.setBackground(getResources().getDrawable(R.drawable.menu));
        myMenu1.setTextColor(getResources().getColor(R.color.rouge));
        myMenu1.setBackground(getResources().getDrawable(R.drawable.menu));
    }

    protected void createActionMenu() {
        LinearLayout laySousMenu = (LinearLayout) findViewById(R.id.laySousMenu);
        if (Config.flagDirectDemarche == 1) {
            laySousMenu.setVisibility(View.GONE);
        } else {
            laySousMenu.setVisibility(View.VISIBLE);
        }

        Typeface myTypeface = Typeface.createFromAsset(Config.myHome.getAssets(), "Oswald-Regular.ttf");

        myMenu1 = (Button) findViewById(R.id.bt_menu1);
        myMenu2 = (Button) findViewById(R.id.bt_menu2);
        myMenu3 = (Button) findViewById(R.id.bt_menu3);


        if (Config.CODE_DE_MON_ACTIVITE == 1) {
            myMenu1.setText(getResources().getString(R.string.libMenu1_1));
            myMenu2.setText(getResources().getString(R.string.libMenu1_2));
            myMenu3.setText(getResources().getString(R.string.libMenu1_3));

        } else if (Config.CODE_DE_MON_ACTIVITE == 3) {
            myMenu1.setText(getResources().getString(R.string.libMenu3_1));
            myMenu2.setText(getResources().getString(R.string.libMenu3_2));
            myMenu3.setText(getResources().getString(R.string.libMenu3_3));

        } else if (Config.CODE_DE_MON_ACTIVITE == 4) {
            myMenu1.setText(getResources().getString(R.string.libMenu4_1));
            myMenu2.setText(getResources().getString(R.string.libMenu4_2));
            myMenu3.setText(getResources().getString(R.string.libMenu4_3));

        } else if (Config.CODE_DE_MON_ACTIVITE == 5) {
            myMenu1.setText(getResources().getString(R.string.libMenu5_1));
            myMenu2.setText(getResources().getString(R.string.libMenu5_2));
            myMenu3.setText(getResources().getString(R.string.libMenu5_3));

        } else if (Config.CODE_DE_MON_ACTIVITE == 6) {
            myMenu1.setText(getResources().getString(R.string.libMenu6_1));
            myMenu2.setText(getResources().getString(R.string.libMenu6_2));
            myMenu3.setText(getResources().getString(R.string.libMenu6_3));
        }

        if (Config.MENU_ACTIVITE == 2) {
            myMenu2.setTextColor(getResources().getColor(R.color.blanc));
            myMenu2.setBackground(getResources().getDrawable(R.drawable.menu_actif));
        } else if (Config.MENU_ACTIVITE == 3) {
            myMenu3.setTextColor(getResources().getColor(R.color.blanc));
            myMenu3.setBackground(getResources().getDrawable(R.drawable.menu_actif));
        } else {
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
                        if (Config.MENU_ACTIVITE != 1) {
                            hideCarto();
                            offAllMenu();
                            try {
                                Config.myWebFetch.cancel(true);
                            } catch (Exception e) {
                            }

                            myMenu1.setTextColor(getResources().getColor(R.color.blanc));
                            myMenu1.setBackground(getResources().getDrawable(R.drawable.menu_actif));

                            Config.resetVarNavigation();

                            Config.MENU_ACTIVITE = 1;
                            flagCarto = 0;
                            if (Config.CODE_DE_MON_ACTIVITE == 1) {
                                Config.fragToReload = getResources().getString(R.string.sqlType1_1);
                                Config.myFragment.loadFragment(getResources().getString(R.string.sqlType1_1));
                            } else if (Config.CODE_DE_MON_ACTIVITE == 3) {
                                Config.fragToReload = getResources().getString(R.string.sqlType3_1);
                                flagCarto = 1;
                                Config.myFragment.loadFragment(getResources().getString(R.string.sqlType3_1));
                            } else if (Config.CODE_DE_MON_ACTIVITE == 5) {
                                Config.myFragment.loadEvenement();
                            } else if (Config.CODE_DE_MON_ACTIVITE == 6) {
                                Config.sql_sous_type = Config.fragToReload = getResources().getString(R.string.sqlType6_1);
                                Config.myFragment.loadFragment(getResources().getString(R.string.sqlType6_1));
                            }

                            majEtatBtCarto();
                            Config.resetFragment();
                        }
                    }
                }
        );
        myMenu2.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (Config.MENU_ACTIVITE != 2) {
                            hideCarto();
                            offAllMenu();
                            try {
                                Config.myWebFetch.cancel(true);
                            } catch (Exception e) {
                            }


                            myMenu2.setTextColor(getResources().getColor(R.color.blanc));
                            myMenu2.setBackground(getResources().getDrawable(R.drawable.menu_actif));

                            Config.resetVarNavigation();
                            flagCarto = 0;
                            Config.MENU_ACTIVITE = 2;
                            if (Config.CODE_DE_MON_ACTIVITE == 1) {
                                Config.fragToReload = getResources().getString(R.string.sqlType1_2);
                                Config.myFragment.loadFragment(getResources().getString(R.string.sqlType1_2));

                            } else if (Config.CODE_DE_MON_ACTIVITE == 3) {
                                Config.fragToReload = getResources().getString(R.string.sqlType3_2);
                                Config.myFragment.loadFragment(getResources().getString(R.string.sqlType3_2));

                            } else if (Config.CODE_DE_MON_ACTIVITE == 5) {
                                Config.flagRetourRecherche = 1;
                                Config.myFragment.loadRechercheEvenement();

                            } else if (Config.CODE_DE_MON_ACTIVITE == 6) {
                                Config.sql_sous_type = Config.fragToReload = getResources().getString(R.string.sqlType6_2);
                                Config.myFragment.loadFragment(getResources().getString(R.string.sqlType6_2));
                            }
                            majEtatBtCarto();
                            Config.resetFragment();
                        }
                    }
                }
        );

        myMenu3.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (Config.MENU_ACTIVITE != 3) {
                            Config.MENU_ACTIVITE = 3;
                            hideCarto();
                            offAllMenu();
                            flagCarto = 0;
                            try {
                                Config.myWebFetch.cancel(true);
                            } catch (Exception e) {
                            }


                            myMenu3.setTextColor(getResources().getColor(R.color.blanc));
                            myMenu3.setBackground(getResources().getDrawable(R.drawable.menu_actif));

                            Config.resetVarNavigation();

                            if (Config.CODE_DE_MON_ACTIVITE == 1) {
                                Config.fragToReload = getResources().getString(R.string.sqlType1_3);
                                Config.myFragment.loadFragment(getResources().getString(R.string.sqlType1_3));
                            } else if (Config.CODE_DE_MON_ACTIVITE == 3) {
                                Config.fragToReload = getResources().getString(R.string.sqlType3_3);
                                Config.myFragment.loadFragment(getResources().getString(R.string.sqlType3_3));
                            } else if (Config.CODE_DE_MON_ACTIVITE == 5) {
                                Config.fragToReload = getResources().getString(R.string.sqlIncontournable);
                                Config.myFragment.loadFragment(getResources().getString(R.string.sqlIncontournable));
                                Config.flagRetourRecherche = 0;
                            } else if (Config.CODE_DE_MON_ACTIVITE == 6) {
                                Config.sql_sous_type = Config.fragToReload = getResources().getString(R.string.sqlType6_3);
                                Config.myFragment.loadFragment(getResources().getString(R.string.sqlType6_3));
                            }
                            majEtatBtCarto();
                            Config.resetFragment();
                        }
                    }
                }
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_type);

        //flagNoResume = 1;

        FacebookSdk.sdkInitialize(getApplicationContext());

        Log.d("myTag", "CODE_DE_MON_ACTIVITE : " + Config.CODE_DE_MON_ACTIVITE);

        Config.myActu = this;
        Config.myFragment = this;
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "Oswald-Regular.ttf");
        TextView myTitre = (TextView) findViewById(R.id.titre);
        layCarto = (LinearLayout) findViewById(R.id.layCarto);
        layFragment = (View) findViewById(R.id.fragment);

        if ((Config.MENU_ACTIVITE == 1) && (Config.CODE_DE_MON_ACTIVITE == 3)) {  // Cas particulier des parcs et jardin on force la carto
            flagCarto = 1;
        }

        createActionMenu();

        layBtCarto = (RelativeLayout) findViewById(R.id.layBtCarto);
        btCarto = (ImageButton) findViewById(R.id.btCarto);
        btCarto.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Config.myFragment.loadCarto();
                        changeBtCarto(0);
                    }
                }
        );
        btListe = (ImageButton) findViewById(R.id.btListe);
        btListe.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        hideCarto();
                        changeBtCarto(1);
                    }
                }
        );

        //TextView myChargementText   = (TextView) findViewById(R.id.myChargementText);
        //myChargementText.setTypeface(myTypeface);

        String myChaine = "";

        if (Config.flagDirectDemarche == 1) {

            loadDemarche(Config.str_demarche);

        } else {
            if (Config.CODE_DE_MON_ACTIVITE == 1) {
                myChaine = getResources().getString(R.string.libHomeBt1);
                if (Config.codeInterne == 1) {
                    loadFragment(getResources().getString(R.string.sqlType1_1));
                    Config.sql_sous_type = "";
                    Config.sql_type = getResources().getString(R.string.sqlType1_1);
                } else if (Config.codeInterne == 3) {
                    loadFragment(getResources().getString(R.string.sqlType1_3));
                    Config.sql_sous_type = "";
                    Config.sql_type = getResources().getString(R.string.sqlType1_3);
                } else if (Config.codeInterne == 4) {
                    Config.sql_type = "marches";
                } else if (Config.MENU_ACTIVITE == 2) {
                    Config.fragToReload = getResources().getString(R.string.sqlType1_2);
                    loadFragment(getResources().getString(R.string.sqlType1_2));
                }

                Config.codeInterne = 1;

            } else if (Config.CODE_DE_MON_ACTIVITE == 3) {
                myChaine = getResources().getString(R.string.libHomeBt3);

                if (Config.MENU_ACTIVITE == 1) {
                    flagCarto = 1;

                    Config.fragToReload = getResources().getString(R.string.sqlType3_1);
                    Config.myFragment.loadFragment(getResources().getString(R.string.sqlType3_1));

                } else if (Config.MENU_ACTIVITE == 2) {
                    Config.fragToReload = getResources().getString(R.string.sqlType3_2);
                    Config.myFragment.loadFragment(getResources().getString(R.string.sqlType3_2));
                } else if (Config.MENU_ACTIVITE == 3) {
                    Config.fragToReload = getResources().getString(R.string.sqlBalade);
                    Config.myFragment.loadFragment(getResources().getString(R.string.sqlBalade));
                }

                majEtatBtCarto();

            } else if (Config.CODE_DE_MON_ACTIVITE == 5) {
                myChaine = getResources().getString(R.string.libHomeBt5);

                if (Config.codeInterne == 2) {
                    loadRechercheEvenement();
                } else if (Config.codeInterne == 3) {
                    Config.fragToReload = getResources().getString(R.string.sqlIncontournable);
                    Config.myFragment.loadFragment(getResources().getString(R.string.sqlIncontournable));
                } else {
                    Config.flagFromRecherche = 0;
                    loadEvenement();
                }

                Config.codeInterne = 1;


            } else if (Config.CODE_DE_MON_ACTIVITE == 6) {
                myChaine = getResources().getString(R.string.libHomeBt6);

                Config.sql_sous_type = Config.fragToReload = getResources().getString(R.string.sqlType6_1);
                Config.myFragment.loadFragment(getResources().getString(R.string.sqlType6_1));

            }
        }

        if (Config.flagDirectDemarche == 1) {
            myTitre.setText("FAVORIS");
        } else {
            myTitre.setText(myChaine);
        }
        myTitre.setTypeface(myTypeface);

        // Bouton retour home
        ImageView myLogo = (ImageView) findViewById(R.id.logo);
        // ACTION DES BOUTONS DE LA HOME
        myLogo.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Config.flagRetourRecherche = 0;
                        startActivity(new Intent(ListType.this, MainActivity.class));
                    }
                }
        );

        ImageButton myBtMenu = (ImageButton) findViewById(R.id.bt_menu);
        myBtMenu.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Config.flagRetourRecherche = 0;
                        startActivity(new Intent(ListType.this, MainActivity.class));
                    }
                }
        );

        ImageButton myBtParam = (ImageButton) findViewById(R.id.bt_param);
        ImageButton myBtFavoris = (ImageButton) findViewById(R.id.bt_favoris);
        myBtParam.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Config.flagRetourRecherche = 0;
                        Intent intent = new Intent(ListType.this, Parametre.class);
                        startActivityForResult(intent, 2);
                    }
                }
        );
        myBtFavoris.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Config.flagRetourRecherche = 0;
                        Intent intent = new Intent(ListType.this, favoris.class);
                        startActivityForResult(intent, 2);

                    }
                }
        );

        Config.majNbeFav((TextView) findViewById(R.id.txt_nbe_favoris), this.getBaseContext());
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

                        Intent intent = new Intent(ListType.this, Reveil.class);
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

    public void showBtCarto(int p_param) {
        layBtCarto.setVisibility(View.VISIBLE);
        changeBtCarto(p_param);
    }

    public void hideBtCarto() {
        layBtCarto.setVisibility(View.GONE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUpMap();
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            public void onMapLoaded() {
                zoomTheMap();
            }
        });
    }

    private void setUpMapIfNeeded() {

        if (mMap == null) {
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMapAsync(this);
        }
    }

    public void showCarto() {
        setUpMap();
        layCarto.setVisibility(View.VISIBLE);
        layFragment.setVisibility(View.GONE);
        showBtCarto(0);
    }

    public void hideCarto() {
        layCarto.setVisibility(View.GONE);
        layFragment.setVisibility(View.VISIBLE);
    }


    public void setUpMap() {

        mMap.clear();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            PermissionUtils.requestPermission(this, MY_LOCATION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }

        for (int i = 0; i < Config.pointCarto.size(); i++) {
            String temp = "" + Config.pointCarto.get(i).get("latitude");
            String temp2 = "" + Config.pointCarto.get(i).get("longitude");
            String tmpPicto = "" + (String) Config.pointCarto.get(i).get("picto");

            if ((temp.length() > 0) && (temp2.length() > 0) && (!temp.equals("null"))) {
                if (!temp.equalsIgnoreCase("0.0")) {

                    MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_map));
                    if (Config.list_picto.contains(tmpPicto)) {
                        markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromAsset("picto_carto/" + tmpPicto + ".png"));
                    }

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

        zoomTheMap();
    }

    public void gotoEquipement(String p_titre) {
        for (int i = 0; i < Config.pointCarto.size(); i++) {

            String temp = "" + Config.pointCarto.get(i).get("titre");

            if (temp.equals(p_titre)) {
                String temp2 = "" + Config.pointCarto.get(i).get("id_equipement").toString();

                if (temp2.length() > 0) {
                    Config.flagContentEquip = 0;
                } else {
                    Config.flagContentEquip = 1;
                }
                Config.myContentValue = Config.pointCarto.get(i);
                Config.sql_type = temp2;
                Config.xml_id = "" + Config.pointCarto.get(i).get("xml_id").toString();

                Intent intent = new Intent(ListType.this, FragmentDetailEquipement.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, 0);
            }
        }
    }

    public void zoomTheMap() {
        if (Config.sql_type.equals(Config.slugTeteOr)) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Config.gpsTeteOr, 15));
        }else if (Config.sql_type.equals(Config.slugBlandan)) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Config.gpsBlandan, 15));
        }else if (Config.sql_type.equals(Config.slugGerland)) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Config.gpsGerland, 15));
        }else {
            try {
                Location location = mMap.getMyLocation();

                if (location != null) {
                    if (location.getLatitude() != 0) {
                        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
                    }
                } else {
                    if (Config.pointCarto.size() > 0) {
                        String temp = "" + Config.pointCarto.get(0).get("latitude");
                        String temp2 = "" + Config.pointCarto.get(0).get("longitude");

                        if ((temp.length() > 0) && (temp2.length() > 0) && (!temp.equals("null"))) {
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
            } catch (Exception e) {
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        AppEventsLogger.activateApp(this);
        Config.myActu = this;
        Config.majNbeFav((TextView) findViewById(R.id.txt_nbe_favoris), this.getBaseContext());
        Config.showAlertNotif(this);
        setUpMapIfNeeded();

        if (flagNoResume == 1) {
            flagNoResume = 0;
        } else {
            majEtatBtCarto();
        }
    }

    public void majEtatBtCarto() {
        if (flagCarto == 1) {
            showBtCarto(0);
        } else {
            hideBtCarto();
        }
    }

    public void loadDemarche(String p_param) {
        Config.flagDemarche = 1;
        Config.str_demarche = p_param;

        ListTypeFragmentDemarche fragment2 = new ListTypeFragmentDemarche();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment2).commit();

    }

    public void loadFormAlert() {
        Intent intent = new Intent(ListType.this, FormAlerte.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, 0);
    }

    public void loadFragment(String p_param) {
        Log.wtf("myTag", "loadFragment : " + p_param);

        Config.flagFragment = 0;

        try {
            if (p_param.length() > 0) {
                Config.sql_type = p_param;
                Config.flagFragment = 1;
            } else {
                if (Config.fragToReload.length() > 0) {
                    Config.sql_type = Config.fragToReload;
                }
            }

            if (p_param.equals("marches") && (Config.flagDirectMarche == 0)) {
                Intent intent = new Intent(ListType.this, RechercheMarcheFragment.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, 0);

            } else if (p_param.equals("label-lyon-ville-equitable-et-durable-carto") && (Config.flagDirectMarche == 0)) {
                Intent intent = new Intent(ListType.this, RechercheVieFragment.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, 0);

            } else {
                Config.flagDirectMarche = 0;
                ListTypeFragment fragment2 = new ListTypeFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment2).commit();
            }
        } catch (Exception e) {
        }
        //      }

    }

    public void setModeListe(String p_param) {
        if (p_param.equals("1")) {
            flagCarto = 1;
        } else {
            flagCarto = 0;
        }
    }

    public void loadBalade() {
        Config.flagFragment = 1;
        Config.fragToReload = getResources().getString(R.string.sqlBalade);
        FragmentDetailBalade fragment2 = new FragmentDetailBalade();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment2).commit();
    }

    public void loadIncontrounable() {
        Config.flagFragment = 2;
        Intent intent = new Intent(ListType.this, FragmentDetailIncontournable.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, 0);
        hideBtCarto();
    }

    public void loadEquipement() {
        Config.flagFragment = 1;

        Config.flagFromFavoris = 0;
        Intent intent = new Intent(ListType.this, FragmentDetailEquipement.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, 0);
        hideBtCarto();
    }

    public void loadEvenement() {
        Config.flagFragment = 3;

        ListTypeFragmentEvt fragment2 = new ListTypeFragmentEvt();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment2).commit();

        hideBtCarto();
    }

    public void loadDetailEvenement() {
        Config.flagFragment = 4;
        Intent intent = new Intent(ListType.this, FragmentDetailEvt.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, 0);
        hideBtCarto();
    }

    public void loadRechercheEvenement() {
        Config.flagFragment = 5;
        RechercheFragment fragment2 = new RechercheFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment2).commit();
    }

    public void loadCarto() {
        showCarto();
    }

    public void loadCartoBalade() {
        Config.flagFragment = 6;

        Intent intent = new Intent(ListType.this, FragmentCarteBalade.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, 0);
    }


    @Override
    public void onBackPressed() {
        Config.myFragment.setModeListe("0");
        Config.myFragment.majEtatBtCarto();

        if (Config.str_demarche.length() > 0) {
            Config.flagRetourRecherche = 0;

            if (Config.flagForceRetour == 1) {
                if (Config.MENU_ACTIVITE == 1) {
                    Config.sql_sous_type = Config.fragToReload = getResources().getString(R.string.sqlType6_1);
                    Config.myFragment.loadFragment(getResources().getString(R.string.sqlType6_1));
                } else if (Config.MENU_ACTIVITE == 2) {
                    Config.sql_sous_type = Config.fragToReload = getResources().getString(R.string.sqlType6_2);
                    Config.myFragment.loadFragment(getResources().getString(R.string.sqlType6_2));

                } else {
                    Config.sql_sous_type = Config.fragToReload = getResources().getString(R.string.sqlType6_3);
                    Config.myFragment.loadFragment(getResources().getString(R.string.sqlType6_3));
                    Config.str_demarche = "";
                }
                Config.flagForceRetour = 0;
            } else {
                Config.sql_sous_type = "";
                Config.fragToReload = "";
                Config.flagForceRetour = 0;
                finish();
            }

        } else if (Config.fragToReload.length() > 0) {
            Config.flagRetourRecherche = 0;

            Log.wtf("myTag", "fragToReload");

            if ((Config.fragToReload.equals(getResources().getString(R.string.sqlType3_2))) || (Config.fragToReload.equals(getResources().getString(R.string.sqlType3_3))) || (Config.fragToReload.equals(getResources().getString(R.string.sqlType1_2))) || (Config.fragToReload.equals(getResources().getString(R.string.sqlType1_3)))) {
                if (Config.flagForceRetour == 1) {
                    Config.myFragment.loadFragment(Config.fragToReload);
                    hideCarto();
                    Config.fragToReload = "";
                    Config.flagForceRetour = 0;
                } else {
                    Config.fragToReload = "";
                    Config.flagForceRetour = 0;
                    finish();
                }

            } else {
                finish();
            }
        } else {
            Log.wtf("myTag", "le else");

            if ((Config.flagBisRetour == 1) || (Config.flagBisRetour == 2)) {
                Config.flagBisRetour = 0;

                finish();
                Config.fragToReload = "";
                Config.flagForceRetour = 0;
                Config.flagRetourRecherche = 0;

            } else if (Config.flagBisRetour == 3) {
                Config.flagBisRetour = 0;

                Config.myFragment.loadFragment(getResources().getString(R.string.sqlType3_2));
                Config.fragToReload = "";
                Config.flagForceRetour = 0;
                Config.flagRetourRecherche = 0;

            } else if (Config.flagRetourRecherche == 1) {
                Config.flagRetourRecherche = 0;
                loadRechercheEvenement();
            } else {
                Config.flagRetourRecherche = 0;
                finish();
            }
        }
    }
}
