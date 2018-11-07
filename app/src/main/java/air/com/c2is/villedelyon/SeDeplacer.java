package air.com.c2is.villedelyon;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.ViewGroup;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.nio.charset.Charset;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;

public class SeDeplacer extends FragmentActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    public GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private DataBaseHelper myDbHelper;

    public Button myMenu1;
    public Button myMenu2;
    public Button myMenu3;

    public ImageButton btMenuLayer;
    public ImageButton btMenuLayerPetit;

    public ImageButton btPicto1;
    public ImageButton btPicto2;
    public ImageButton btPicto3;
    public ImageButton btPicto4;
    public RelativeLayout layMenuPicto;
    public LinearLayout layFondMenu;
    MarkerOptions markerActu;

    public int flagPicto1;
    public int flagPicto2;
    public int flagPicto3;
    public int flagPicto4;
    public int flagOpenMenu;

    public int flagLegende = 0;

    public RelativeLayout layLegende;
    public WebView description;
    public ImageButton btLegende;
    public int flagMap = 0;

    public String urlAuto = "https://download.data.grandlyon.com/wfs/grandlyon?SERVICE=WFS&VERSION=2.0.0&outputformat=GEOJSON&maxfeatures=30&request=GetFeature&typename=pvo_patrimoine_voirie.pvostationautopartage";
    public String urlParking = "http://appvilledelyon.c2is.fr/opendata.php?flag=1";
    public String urlPmr = "https://download.data.grandlyon.com/wfs/grandlyon?SERVICE=WFS&VERSION=2.0.0&outputformat=GEOJSON&maxfeatures=2000&request=GetFeature&typename=vdl_deplacements.emplacement_pmr";
    public String urlVelov = "http://appvilledelyon.c2is.fr/opendata.php?flag=0";
    public String urlParcVelo = "https://download.data.grandlyon.com/wfs/grandlyon?SERVICE=WFS&VERSION=2.0.0&outputformat=GEOJSON&maxfeatures=300&request=GetFeature&typename=pvo_patrimoine_voirie.pvostationnementvelo";

    public JSONArray contactVelov = null;
    public JSONArray contactParcVelo = null;
    public JSONArray contactsPartage = null;
    public JSONArray contactsParking = null;
    public JSONArray contactsPmr = null;

    public DialogLoading myDialLoading;
    private static final int MY_LOCATION_REQUEST_CODE = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
             //   mMap.setMyLocationEnabled(true);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        myDialLoading = new DialogLoading(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_se_deplacer);

        Config.mySeDeplacer = this;

        FacebookSdk.sdkInitialize(getApplicationContext());
        Config.myActu = this;
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "Oswald-Regular.ttf");
        TextView myTitre = (TextView) findViewById(R.id.titre);

        layFondMenu = (LinearLayout) findViewById(R.id.layFondMenu);
        layMenuPicto = (RelativeLayout) findViewById(R.id.layMenuPicto);

        myTitre.setTypeface(myTypeface);

        description = (WebView) findViewById(R.id.description);
        WebSettings settings = description.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        description.setBackgroundColor(Color.TRANSPARENT);

        layLegende = (RelativeLayout) findViewById(R.id.layLegende);
        btPicto1 = (ImageButton) findViewById(R.id.btPicto1);
        btPicto2 = (ImageButton) findViewById(R.id.btPicto2);
        btPicto3 = (ImageButton) findViewById(R.id.btPicto3);
        btPicto4 = (ImageButton) findViewById(R.id.btPicto4);
        btMenuLayer = (ImageButton) findViewById(R.id.btMenuLayer);
        btMenuLayerPetit = (ImageButton) findViewById(R.id.btMenuLayerPetit);
        setMenuLayerAction();

        ImageButton myBtMenu = (ImageButton) findViewById(R.id.bt_menu);

        ImageView myLogo = (ImageView) findViewById(R.id.logo);
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

        ImageButton myBtParam = (ImageButton) findViewById(R.id.bt_param);
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

        btLegende = (ImageButton) findViewById(R.id.btLegende);
        btLegende.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (flagLegende == 1) {
                            closeLegende();
                        } else {
                            openLegende();
                        }
                    }
                }
        );

        myMenu1 = (Button) findViewById(R.id.bt_menu1);
        myMenu2 = (Button) findViewById(R.id.bt_menu2);
        myMenu3 = (Button) findViewById(R.id.bt_menu3);

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
                        initFlagPicto();
                        Config.MENU_ACTIVITE = 1;
                        setRollMenu();
                        closeLegende();

                        showLoading();

                        mMap.clear();
                        if (flagMap != 1) {
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
                        initFlagPicto();
                        Config.MENU_ACTIVITE = 2;
                        setRollMenu();
                        closeLegende();

                        showLoading();

                        final Handler handler = new Handler();

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mMap.clear();
                                if (flagMap != 2) {
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
                        initFlagPicto();
                        Config.MENU_ACTIVITE = 3;
                        setRollMenu();
                        closeLegende();

                        showLoading();

                        final Handler handler = new Handler();

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mMap.clear();
                                if (flagMap != 3) {
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
        } catch (SQLException sqle) {
            Log.d("myTag", "ouverture bdd KO");
            throw sqle;
        }

        Config.majNbeFav((TextView) findViewById(R.id.txt_nbe_favoris), this.getBaseContext());
        setUpMapIfNeeded();
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

    public void initBtMenu() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layFondMenu.getLayoutParams();

        if (Config.MENU_ACTIVITE == 2) {
            params.setMargins(0, 200, 0, 0);
            layFondMenu.setLayoutParams(params);

            if (Config.checkDevice() >= 2) {
                btPicto1.setImageResource(R.drawable.picto_velov_grand_on);
                btPicto2.setImageResource(R.drawable.picto_velo_grand_off);
            } else {
                btPicto1.setImageResource(R.drawable.picto_velov_on);
                btPicto2.setImageResource(R.drawable.picto_velo_off);
            }
            btPicto1.setAlpha(255);
            btPicto2.setAlpha(128);
            btPicto1.setVisibility(View.VISIBLE);
            btPicto2.setVisibility(View.VISIBLE);
            btPicto3.setVisibility(View.INVISIBLE);
            btPicto4.setVisibility(View.INVISIBLE);
        } else if (Config.MENU_ACTIVITE == 3) {
            params.setMargins(0, 0, 0, 0);
            layFondMenu.setLayoutParams(params);

            if (Config.checkDevice() >= 2) {
                btPicto1.setImageResource(R.drawable.picto_parking_grand_on);
                btPicto2.setImageResource(R.drawable.picto_pmr_grand_off);
                btPicto3.setImageResource(R.drawable.picto_autolib_grand_off);
                btPicto4.setImageResource(R.drawable.picto_bluely_grand_off);
            } else {
                btPicto1.setImageResource(R.drawable.picto_parking_on);
                btPicto2.setImageResource(R.drawable.picto_pmr_off);
                btPicto3.setImageResource(R.drawable.picto_autolib_off);
                btPicto4.setImageResource(R.drawable.picto_bluely_off);
            }

            btPicto1.setAlpha(255);
            btPicto2.setAlpha(128);
            btPicto3.setAlpha(128);
            btPicto1.setVisibility(View.VISIBLE);
            btPicto2.setVisibility(View.VISIBLE);
            btPicto3.setVisibility(View.VISIBLE);
            btPicto4.setVisibility(View.VISIBLE);
        } else {
            params.setMargins(0, 0, 0, 0);
            layFondMenu.setLayoutParams(params);

            if (Config.checkDevice() >= 2) {
                btPicto1.setImageResource(R.drawable.picto_tramway_grand_on);
                btPicto2.setImageResource(R.drawable.picto_metro_grand_off);
                btPicto3.setImageResource(R.drawable.picto_taxi_grand_off);
                btPicto4.setImageResource(R.drawable.picto_train_grand_off);
            } else {
                btPicto1.setImageResource(R.drawable.picto_tramway_on);
                btPicto2.setImageResource(R.drawable.picto_metro_off);
                btPicto3.setImageResource(R.drawable.picto_taxi_off);
                btPicto4.setImageResource(R.drawable.picto_train_off);
            }

            btPicto1.setAlpha(255);
            btPicto2.setAlpha(128);
            btPicto3.setAlpha(128);
            btPicto4.setAlpha(128);
            btPicto1.setVisibility(View.VISIBLE);
            btPicto2.setVisibility(View.VISIBLE);
            btPicto3.setVisibility(View.VISIBLE);
            btPicto4.setVisibility(View.VISIBLE);
        }
    }

    public void setMenuLayerAction() {
        btMenuLayer.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (flagOpenMenu == 1) {
                            layMenuPicto.animate().setDuration(100);
                            layMenuPicto.animate().translationX(-250);  //-200
                            layMenuPicto.animate().start();
                            flagOpenMenu = 0;
                            btMenuLayerPetit.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );
        btMenuLayerPetit.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        btMenuLayerPetit.setVisibility(View.GONE);
                        layMenuPicto.animate().setDuration(100);
                        layMenuPicto.animate().translationX(0);
                        layMenuPicto.animate().start();
                        flagOpenMenu = 1;
                    }
                }
        );
        flagOpenMenu = 1;

        //btPicto1.setAlpha(128);
        btPicto2.setAlpha(128);
        btPicto3.setAlpha(128);
        btPicto4.setAlpha(128);

        initFlagPicto();

        btPicto1.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (flagPicto1 == 0) {
                            btPicto1.setAlpha(255);

                            if (Config.checkDevice() >= 2) {
                                if (Config.MENU_ACTIVITE == 2) {
                                    btPicto1.setImageResource(R.drawable.picto_velov_grand_on);
                                } else if (Config.MENU_ACTIVITE == 3) {
                                    btPicto1.setImageResource(R.drawable.picto_parking_grand_on);
                                } else {
                                    btPicto1.setImageResource(R.drawable.picto_tramway_grand_on);
                                }
                            } else {
                                if (Config.MENU_ACTIVITE == 2) {
                                    btPicto1.setImageResource(R.drawable.picto_velov_on);
                                } else if (Config.MENU_ACTIVITE == 3) {
                                    btPicto1.setImageResource(R.drawable.picto_parking_on);
                                } else {
                                    btPicto1.setImageResource(R.drawable.picto_tramway_on);
                                }
                            }

                            flagPicto1 = 1;
                        } else {
                            btPicto1.setAlpha(128);
                            if (Config.checkDevice() >= 2) {
                                if (Config.MENU_ACTIVITE == 2) {
                                    btPicto1.setImageResource(R.drawable.picto_velov_grand_off);
                                } else if (Config.MENU_ACTIVITE == 3) {
                                    btPicto1.setImageResource(R.drawable.picto_parking_grand_off);
                                } else {
                                    btPicto1.setImageResource(R.drawable.picto_tramway_grand_off);
                                }
                            } else {
                                if (Config.MENU_ACTIVITE == 2) {
                                    btPicto1.setImageResource(R.drawable.picto_velov_off);
                                } else if (Config.MENU_ACTIVITE == 3) {
                                    btPicto1.setImageResource(R.drawable.picto_parking_off);
                                } else {
                                    btPicto1.setImageResource(R.drawable.picto_tramway_off);
                                }
                            }
                            flagPicto1 = 0;
                        }

                        if (Config.MENU_ACTIVITE == 1) {
                            showMapPied();
                        } else if (Config.MENU_ACTIVITE == 2) {
                            showMapVeloFromPicto();
                        } else if (Config.MENU_ACTIVITE == 3) {
                            showMapVoitureFromPicto();
                        }
                    }
                }
        );
        btPicto2.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (flagPicto2 == 0) {
                            btPicto2.setAlpha(255);

                            if (Config.checkDevice() >= 2) {
                                if (Config.MENU_ACTIVITE == 2) {
                                    btPicto2.setImageResource(R.drawable.picto_velo_grand_on);
                                } else if (Config.MENU_ACTIVITE == 3) {
                                    btPicto2.setImageResource(R.drawable.picto_pmr_grand_on);
                                } else {
                                    btPicto2.setImageResource(R.drawable.picto_metro_grand_on);
                                }
                            } else {
                                if (Config.MENU_ACTIVITE == 2) {
                                    btPicto2.setImageResource(R.drawable.picto_velo_on);
                                } else if (Config.MENU_ACTIVITE == 3) {
                                    btPicto2.setImageResource(R.drawable.picto_pmr_on);
                                } else {
                                    btPicto2.setImageResource(R.drawable.picto_metro_on);
                                }
                            }

                            flagPicto2 = 1;
                        } else {
                            btPicto2.setAlpha(128);

                            if (Config.checkDevice() >= 2) {
                                if (Config.MENU_ACTIVITE == 2) {
                                    btPicto2.setImageResource(R.drawable.picto_velo_grand_off);
                                } else if (Config.MENU_ACTIVITE == 3) {
                                    btPicto2.setImageResource(R.drawable.picto_pmr_grand_off);
                                } else {
                                    btPicto2.setImageResource(R.drawable.picto_metro_grand_off);
                                }
                            } else {
                                if (Config.MENU_ACTIVITE == 2) {
                                    btPicto2.setImageResource(R.drawable.picto_velo_off);
                                } else if (Config.MENU_ACTIVITE == 3) {
                                    btPicto2.setImageResource(R.drawable.picto_pmr_off);
                                } else {
                                    btPicto2.setImageResource(R.drawable.picto_metro_off);
                                }
                            }

                            flagPicto2 = 0;
                        }

                        if (Config.MENU_ACTIVITE == 1) {
                            showMapPied();
                        } else if (Config.MENU_ACTIVITE == 2) {
                            showMapVeloFromPicto();

                        } else if (Config.MENU_ACTIVITE == 3) {
                            showMapVoitureFromPicto();
                        }

                    }
                }
        );
        btPicto3.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (flagPicto3 == 0) {
                            btPicto3.setAlpha(255);

                            if (Config.checkDevice() >= 2) {
                                if (Config.MENU_ACTIVITE == 3) {
                                    btPicto3.setImageResource(R.drawable.picto_autolib_grand_on);
                                } else {
                                    btPicto3.setImageResource(R.drawable.picto_taxi_grand_on);
                                }

                            } else {
                                if (Config.MENU_ACTIVITE == 3) {
                                    btPicto3.setImageResource(R.drawable.picto_autolib_on);
                                } else {
                                    btPicto3.setImageResource(R.drawable.picto_taxi_on);
                                }
                            }
                            flagPicto3 = 1;
                        } else {
                            btPicto3.setAlpha(128);
                            if (Config.checkDevice() >= 2) {
                                if (Config.MENU_ACTIVITE == 3) {
                                    btPicto3.setImageResource(R.drawable.picto_autolib_grand_off);
                                } else {
                                    btPicto3.setImageResource(R.drawable.picto_taxi_grand_off);
                                }
                            } else {
                                if (Config.MENU_ACTIVITE == 3) {
                                    btPicto3.setImageResource(R.drawable.picto_autolib_off);
                                } else {
                                    btPicto3.setImageResource(R.drawable.picto_taxi_off);
                                }
                            }
                            flagPicto3 = 0;
                        }
                        if (Config.MENU_ACTIVITE == 1) {
                            showMapPied();
                        } else if (Config.MENU_ACTIVITE == 3) {
                            showMapVoitureFromPicto();
                        }
                    }
                }
        );
        btPicto4.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (flagPicto4 == 0) {
                            btPicto4.setAlpha(255);

                            if (Config.MENU_ACTIVITE == 3) {
                                if (Config.checkDevice() >= 2) {
                                    btPicto4.setImageResource(R.drawable.picto_bluely_grand_on);
                                } else {
                                    btPicto4.setImageResource(R.drawable.picto_bluely_on);
                                }
                            } else {
                                if (Config.checkDevice() >= 2) {
                                    btPicto4.setImageResource(R.drawable.picto_train_grand_on);
                                } else {
                                    btPicto4.setImageResource(R.drawable.picto_train_on);
                                }
                            }
                            flagPicto4 = 1;
                        } else {
                            btPicto4.setAlpha(128);
                            if (Config.MENU_ACTIVITE == 3) {
                                if (Config.checkDevice() >= 2) {
                                    btPicto4.setImageResource(R.drawable.picto_bluely_grand_off);
                                } else {
                                    btPicto4.setImageResource(R.drawable.picto_bluely_off);
                                }
                            } else {
                                if (Config.checkDevice() >= 2) {
                                    btPicto4.setImageResource(R.drawable.picto_train_grand_off);
                                } else {
                                    btPicto4.setImageResource(R.drawable.picto_train_off);
                                }
                            }
                            flagPicto4 = 0;
                        }
                        if (Config.MENU_ACTIVITE == 1) {
                            showMapPied();
                        } else {
                            showMapVoitureFromPicto();
                        }
                    }
                }
        );
    }

    public void initFlagPicto() {
        flagPicto2 = flagPicto3 = flagPicto4 = 0;
        flagPicto1 = 1;
    }

    public void loadLegende(int p_param) {
        if (p_param == 3) {
            description.loadUrl("file:///android_asset/legende3.html");
        } else if (p_param == 2) {
            description.loadUrl("file:///android_asset/legende2.html");
        } else {
            description.loadUrl("file:///android_asset/legende1.html");
        }

    }

    public void showLoading() {
        initBtMenu();
        myDialLoading.show();
    }

    public void hideLoading() {
        myDialLoading.hide();
    }

    public void openLegende() {
        layMenuPicto.setVisibility(View.GONE);
        layLegende.setVisibility(View.VISIBLE);
        loadLegende(Config.MENU_ACTIVITE);
        flagLegende = 1;
    }

    public void closeLegende() {
        layMenuPicto.setVisibility(View.VISIBLE);
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
                        Config.killLocalNotification(getBaseContext());

                        Config.flagDirectSavoir = 1;

                        Intent intent = new Intent(SeDeplacer.this, Reveil.class);
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
        Config.myActu = this;
        setUpMapIfNeeded();
        Config.majNbeFav((TextView) findViewById(R.id.txt_nbe_favoris), this.getBaseContext());
        Config.showAlertNotif(this);
    }

    private void setUpMapIfNeeded() {
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
                    markerActu = markerOptions;
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
                    markerActu = markerOptions;
                }
            }
        } catch (SQLException sqle) {
            throw sqle;
        }

        loadPmr();
    }

    public void loadPmr() {
        if (flagMap == 3) {
            Cursor c = myDbHelper.loadPmr();

            try {
                if (c != null) {
                    while (c.moveToNext()) {
                        MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_pmr));
                        markerOptions.position(new LatLng(c.getFloat(0), c.getFloat(1)));
                        markerOptions.title("Stationnement PMR");
                        markerOptions.snippet(c.getString(2) + " " + c.getString(3));

                        mMap.addMarker(markerOptions);
                        markerActu = markerOptions;
                    }
                }
            } catch (SQLException sqle) {
                throw sqle;
            }
        }
    }

    public void zoomTheMap() {
        if (markerActu != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerActu.getPosition(), 15));
        }
    }

    public void killPmr() {
        if (flagMap == 3) {

        }
    }

    public void showMapVeloFromPicto() {
        if (mMap == null) {
//            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
  //                  .getMap();
        }
        mMap.clear();

        try {
            if (flagPicto1 == 1) {
                for (int i = 0; i < contactVelov.length(); i++) {

                    JSONObject c = contactVelov.getJSONObject(i);

                    JSONObject o = c.getJSONObject("properties");
                    int nbeEmpl = o.getInt("available_bikes");
                    int nbeEmplRestant = o.getInt("available_bike_stands");


                    MarkerOptions markerOptions = new MarkerOptions();

                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_velib));

                    markerOptions.position(new LatLng(o.getDouble("lat"), o.getDouble("lng")));

                    markerOptions.title("Station Velo'v");
                    markerOptions.snippet(nbeEmpl + "/" + (nbeEmpl + nbeEmplRestant) + " vélos disponibles");
                    mMap.addMarker(markerOptions);
                    markerActu = markerOptions;
                }
            }
        } catch (Exception e) {
            Log.d("myTag", "mon erreur : " + e.getMessage());


        }

        try {
            if (flagPicto2 == 1) {
                for (int i = 0; i < contactParcVelo.length(); i++) {
                    JSONObject c = contactParcVelo.getJSONObject(i);

                    JSONObject o = c.getJSONObject("properties");
                    String adresse = o.getString("adresse");
                    String commune = o.getString("commune");
                    JSONObject g = c.getJSONObject("geometry");

                    MarkerOptions markerOptions = new MarkerOptions();

                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_velo));
                    markerOptions.position(new LatLng(g.getJSONArray("coordinates").getDouble(1), g.getJSONArray("coordinates").getDouble(0)));

                    markerOptions.title("Parc à vélo");

                    String s = new String(adresse.getBytes(), "UTF-8");

                    markerOptions.snippet(Config.formatCharFlux(s));
                    mMap.addMarker(markerOptions);
                    markerActu = markerOptions;
                }
            }
        } catch (Exception e) {
            Log.wtf("myTag", "mon erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void showMapVoitureFromPicto() {
        if (mMap == null) {
/*            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
                    */
        }
        mMap.clear();

        Log.wtf("myTag", "flagPicto1 : " + flagPicto1);
        Log.wtf("myTag", "flagPicto2 : " + flagPicto2);
        Log.wtf("myTag", "flagPicto3 : " + flagPicto3);
        Log.wtf("myTag", "flagPicto4 : " + flagPicto4);


        // AUTO-PARTAGE
        if ((flagPicto3 == 1) || (flagPicto4 == 1)) {
            try {
                for (int i = 0; i < contactsPartage.length(); i++) {
                    JSONObject c = contactsPartage.getJSONObject(i);
                    JSONObject o = c.getJSONObject("properties");
                    String nom = o.getString("nom");
                    String nbeEmpl = o.getString("nbemplacements");
                    JSONObject g = c.getJSONObject("geometry");
                    String type = o.getString("typeautopartage");

                    MarkerOptions markerOptions = new MarkerOptions();

                    int flag = 0;
                    if (type.equalsIgnoreCase("Citiz LPA")) {
                        if (flagPicto3 == 1) {
                            flag = 1;
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_autolib));
                        }
                    } else if (type.equalsIgnoreCase("SunMoov")) {
                        if (flagPicto4 == 1) {
                            flag = 1;
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_sunmoov));
                        }
                    } else if (type.equalsIgnoreCase("Wattmobile")) {
                        if (flagPicto4 == 1) {
                            flag = 1;
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_watt));
                        }
                    } else if (type.equalsIgnoreCase("Bluely")) {
                        if (flagPicto4 == 1) {
                            flag = 1;
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_bluely));
                        }
                    }

                    if (flag == 1) {
                        markerOptions.position(new LatLng(g.getJSONArray("coordinates").getDouble(1), g.getJSONArray("coordinates").getDouble(0)));

                        markerOptions.title(type);
                        markerOptions.snippet(nbeEmpl + " emplacement(s)");

                        mMap.addMarker(markerOptions);
                        markerActu = markerOptions;
                    }

                }
            } catch (Exception e) {
                Log.wtf("myTag", "mon erreur AUTO-PARTAGE : " + e.getMessage());
                e.printStackTrace();
            }
        }

        // PARKINGS
        if (flagPicto1 == 1) {
            try {
                Log.wtf("myTag", "mon taille parking : " + contactsParking.length());
                for (int i = 0; i < contactsParking.length(); i++) {

                    JSONObject c = contactsParking.getJSONObject(i);
                    JSONObject o = c.getJSONObject("properties");

                    String nom = o.getString("nom");
                    String etat = o.getString("etat");
                    String nbeEmpl = o.getString("capacitevoiture");
                    JSONObject g = c.getJSONObject("geometry");

                    MarkerOptions markerOptions = new MarkerOptions();

                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_parking));

                    markerOptions.position(new LatLng(g.getJSONArray("coordinates").getDouble(1), g.getJSONArray("coordinates").getDouble(0)));

                    markerOptions.title(Config.formatCharFlux(nom));

                    markerOptions.snippet(etat + " sur " + nbeEmpl + " emplacements");

                    mMap.addMarker(markerOptions);
                    markerActu = markerOptions;
                }
            } catch (Exception e) {
                Log.wtf("myTag", "mon erreur parking : " + e.getMessage());
                e.printStackTrace();
            }
        }

        // PMR
        if (flagPicto2 == 1) {
            try {
                for (int i = 0; i < contactsPmr.length(); i++) {

                    JSONObject c = contactsPmr.getJSONObject(i);
                    JSONObject o = c.getJSONObject("properties");
                    String nom = o.getString("nom");
                    String nbeEmpl = o.getString("nb_places");
                    JSONObject g = c.getJSONObject("geometry");

                    MarkerOptions markerOptions = new MarkerOptions();

                    markerOptions.position(new LatLng(g.getJSONArray("coordinates").getDouble(1), g.getJSONArray("coordinates").getDouble(0)));
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_pmr));
                    markerOptions.title("Stationnement PMR");
                    markerOptions.snippet(nbeEmpl + " emplacement");

                    mMap.addMarker(markerOptions);
                    markerActu = markerOptions;
                }

            } catch (Exception e) {
                Log.wtf("myTag", "mon erreur pmr : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    private void showMapVelo() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        try {
            // VELO'V
            JSONParser jParser = new JSONParser();
            JSONObject json = jParser.getJSONFromUrl(urlVelov);
            contactVelov = null;
            contactVelov = json.getJSONArray("features");

            if (flagPicto1 == 1) {
                for (int i = 0; i < contactVelov.length(); i++) {

                    JSONObject c = contactVelov.getJSONObject(i);

                    JSONObject o = c.getJSONObject("properties");
                    int nbeEmpl = o.getInt("available_bikes");
                    int nbeEmplRestant = o.getInt("available_bike_stands");


                    MarkerOptions markerOptions = new MarkerOptions();

                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_velib));

                    markerOptions.position(new LatLng(o.getDouble("lat"), o.getDouble("lng")));

                    markerOptions.title("Station Velo'v");
                    markerOptions.snippet(nbeEmpl + "/" + (nbeEmpl + nbeEmplRestant) + " vélos disponibles");
                    mMap.addMarker(markerOptions);
                    markerActu = markerOptions;
                }
            }
        } catch (Exception e) {
            Log.d("myTag", "mon erreur velov : " + e.getMessage());
            e.printStackTrace();
        }


        try {
            // PARC A VELO
            JSONParser jParser = new JSONParser();
            JSONObject json = jParser.getJSONFromUrl(urlParcVelo);
            contactParcVelo = null;
            contactParcVelo = json.getJSONArray("features");

            if (flagPicto2 == 1) {
                for (int i = 0; i < contactParcVelo.length(); i++) {
                    JSONObject c = contactParcVelo.getJSONObject(i);
                    JSONObject o = c.getJSONObject("properties");
                    String adresse = o.getString("adresse");
                    String commune = o.getString("commune");
                    JSONObject g = c.getJSONObject("geometry");

                    MarkerOptions markerOptions = new MarkerOptions();

                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_velo));
                    markerOptions.position(new LatLng(g.getJSONArray("coordinates").getDouble(1), g.getJSONArray("coordinates").getDouble(0)));

                    markerOptions.title("Parc à vélo");
                    markerOptions.snippet(new String(adresse.getBytes("ISO-8859-1")));

                    mMap.addMarker(markerOptions);
                    markerActu = markerOptions;
                }
            }
        } catch (Exception e) {
            Log.wtf("myTag", "mon erreur parc : " + e.getMessage());
            e.printStackTrace();
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
                    markerActu = markerOptions;
                }
            }
        } catch (SQLException sqle) {
            throw sqle;
        }


    }

    private void showMapVoiture() {
        Log.wtf("myTag", "show map voiture");

        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            // AUTO-PARTAGE

            JSONParser jParser = new JSONParser();
            JSONObject json = jParser.getJSONFromUrl(urlAuto);
            contactsPartage = null;

            try {
                contactsPartage = json.getJSONArray("features");

                if (flagPicto3 == 1) {
                    for (int i = 0; i < contactsPartage.length(); i++) {

                        JSONObject c = contactsPartage.getJSONObject(i);

                        Log.wtf("myTag", "obj : " + c.toString());

                        JSONObject o = c.getJSONObject("properties");
                        String nom = o.getString("nom");
                        String nbeEmpl = o.getString("nbemplacements");
                        JSONObject g = c.getJSONObject("geometry");
                        String type = o.getString("typeautopartage");

                        MarkerOptions markerOptions = new MarkerOptions();

                        if (type.equalsIgnoreCase("Citiz LPA")) {
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_autolib));
                        } else if (type.equalsIgnoreCase("SunMoov")) {
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_sunmoov));
                        } else if (type.equalsIgnoreCase("Wattmobile")) {
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_watt));
                        } else if (type.equalsIgnoreCase("Bluely")) {
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_bluely));
                        }

                        //                    Log.d("myTag", type + nbeEmpl + " emplacements");

                        markerOptions.position(new LatLng(g.getJSONArray("coordinates").getDouble(1), g.getJSONArray("coordinates").getDouble(0)));

                        markerOptions.title(type);
                        markerOptions.snippet(nbeEmpl + " emplacement(s)");

                        mMap.addMarker(markerOptions);
                        markerActu = markerOptions;
                    }
                }
            } catch (Exception e) {
                Log.wtf("myTag", "mon erreur partage : " + e.getMessage());
                e.printStackTrace();
            }


            // PARKINGS

            jParser = new JSONParser();
            json = jParser.getJSONFromUrl(urlParking);
            contactsParking = null;

            try {
                contactsParking = json.getJSONArray("features");
                if (flagPicto1 == 1) {
                    for (int i = 0; i < contactsParking.length(); i++) {

                        JSONObject c = contactsParking.getJSONObject(i);

                        JSONObject o = c.getJSONObject("properties");
                        String nom = o.getString("nom");
                        String etat = o.getString("etat");
                        String nbeEmpl = o.getString("capacitevoiture");
                        JSONObject g = c.getJSONObject("geometry");

                        MarkerOptions markerOptions = new MarkerOptions();

                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_parking));

                        markerOptions.position(new LatLng(g.getJSONArray("coordinates").getDouble(1), g.getJSONArray("coordinates").getDouble(0)));
                        markerOptions.title(Config.formatCharFlux(nom));

                        markerOptions.snippet(etat + " sur " + nbeEmpl + " emplacements");

                        Log.wtf("myTag", ">>" + g);

                        mMap.addMarker(markerOptions);
                        markerActu = markerOptions;
                    }
                }
            } catch (Exception e) {
                Log.wtf("myTag", "mon erreur parking : " + e.getMessage());
                e.printStackTrace();
            }


            // PMR

            jParser = new JSONParser();
            json = jParser.getJSONFromUrl(urlPmr);
            contactsPmr = null;

            try {
                contactsPmr = json.getJSONArray("features");


                if (flagPicto2 == 1) {
                    for (int i = 0; i < contactsPmr.length(); i++) {

                        JSONObject c = contactsPmr.getJSONObject(i);

                        JSONObject o = c.getJSONObject("properties");
                        String nom = o.getString("nom");
                        String nbeEmpl = o.getString("nb_places");
                        JSONObject g = c.getJSONObject("geometry");

                        MarkerOptions markerOptions = new MarkerOptions();

                        markerOptions.position(new LatLng(g.getJSONArray("coordinates").getDouble(1), g.getJSONArray("coordinates").getDouble(0)));
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_pmr));
                        markerOptions.title("Stationnement PMR");
                        markerOptions.snippet(nbeEmpl + " emplacement");

                        //               Log.d("myTag", "Stationnement PMR" + nbeEmpl + " emplacements");

                        mMap.addMarker(markerOptions);
                        markerActu = markerOptions;
                    }
                }

            } catch (Exception e) {
                Log.wtf("myTag", "mon erreur pmr : " + e.getMessage());
                e.printStackTrace();
            }


        } catch (Exception e) {
            Log.wtf("myTag", "mon Exception : " + e.getMessage());
            //	Toast.makeText(Config.myResVehicule,"erreur : " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }


    private void showMapPied() {
        mMap.clear();

        Cursor c = myDbHelper.loadPied();

        if (flagPicto2 == 1) {
            try {
                if (c != null) {
                    while (c.moveToNext()) {
                        MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_metro));
                        markerOptions.position(new LatLng(c.getFloat(2), c.getFloat(3)));
                        markerOptions.title("Ligne " + c.getString(0));
                        Log.d("myTag", "ligne");
                        markerOptions.snippet(c.getString(1));
                        mMap.addMarker(markerOptions);
                        markerActu = markerOptions;
                    }
                }
            } catch (SQLException sqle) {
                throw sqle;
            }
        }

        if (flagPicto3 == 1) {
            c = myDbHelper.loadTaxi();

            try {
                if (c != null) {
                    while (c.moveToNext()) {
                        MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_taxi));
                        markerOptions.position(new LatLng(c.getFloat(0), c.getFloat(1)));
                        markerOptions.title("Station de taxi");

                        mMap.addMarker(markerOptions);
                        markerActu = markerOptions;
                    }
                }
            } catch (SQLException sqle) {
                throw sqle;
            }
        }

        if (flagPicto1 == 1) {
            c = myDbHelper.loadTram();

            try {
                if (c != null) {
                    while (c.moveToNext()) {
                        MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_tramway));
                        markerOptions.position(new LatLng(c.getFloat(0), c.getFloat(1)));
                        markerOptions.title("Ligne " + c.getString(2));
                        markerOptions.snippet(c.getString(3));
                        mMap.addMarker(markerOptions);
                        markerActu = markerOptions;
                    }
                }
            } catch (SQLException sqle) {
                throw sqle;
            }
        }

        if (flagPicto4 == 1) {
            c = myDbHelper.loadGare();

            try {
                if (c != null) {
                    while (c.moveToNext()) {
                        MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_train));
                        markerOptions.position(new LatLng(c.getFloat(0), c.getFloat(1)));
                        markerOptions.title(c.getString(2));
                        mMap.addMarker(markerOptions);
                        markerActu = markerOptions;
                    }
                }
            } catch (SQLException sqle) {
                throw sqle;
            }
        }
    }

    private void setUpMap() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Permission to access the location is missing.

            PermissionUtils.requestPermission(this, MY_LOCATION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }

        flagMap = 1;

        myAsyncTask2 myWebFetch = new myAsyncTask2();
        myWebFetch.flag = 1;
        myWebFetch.execute();
    }

    class myAsyncTask2 extends AsyncTask<Void, Void, Void> {
        TextView tv;
        int flag;

        myAsyncTask2() {
            flag = 0;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (flag == 1) {
                showMapPied();
            } else if (flag == 2) {
                showMapVelo();
            } else if (flag == 3) {
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
