package air.com.c2is.villedelyon;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentDetailEquipement extends android.support.v4.app.FragmentActivity {
    private DataBaseHelper myDbHelper;
    public RelativeLayout layBtFermerCarte;
    public ListView mylistview;
    public ArrayList<HashMap<String, Object>> listItems;
    public TextView myTitreEquipement;
    public String libEquip;
    public String contenu;
    public int idEquip = 0;
    public int flagIsFavoris = 0;
    public int flagCartoOpen = 0;
    public ImageButton btAddFavoris;
    public ImageButton btOpenCarte;
    public View myCarte;
    public int depHaut;
    public Fragment map;
    public float latitude;
    public float longitude;
    public Timer myTimer;
    public GoogleMap mMap;
    public static GoogleAnalytics analytics;
    public static Tracker tracker;
    public String myAdresse;
    public String myVille;
    public String myCp;
    public String myPicto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        analytics = GoogleAnalytics.getInstance(Config.myHome.getBaseContext());
        analytics.setLocalDispatchPeriod(1800);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_detail_equipement);

        Config.myActu = this;
        Config.myDetailEquip = this;
        FacebookSdk.sdkInitialize(getApplicationContext());

        Typeface myTypeface = Typeface.createFromAsset(Config.myHome.getAssets(), "Oswald-Regular.ttf");

        Button myMenu1 = (Button) findViewById(R.id.bt_menu1);
        Button myMenu2 = (Button) findViewById(R.id.bt_menu2);
        Button myMenu3 = (Button) findViewById(R.id.bt_menu3);

        if (Config.flagFromFavoris == 1) {
            LinearLayout layMenu = (LinearLayout) findViewById(R.id.layMenu);
            layMenu.setVisibility(View.GONE);
        }

        layBtFermerCarte = (RelativeLayout) findViewById(R.id.layBtFermerCarte);
        myTitreEquipement = (TextView) findViewById(R.id.titreEquipement);

        btAddFavoris = (ImageButton) findViewById(R.id.btAddFavoris);
        btOpenCarte = (ImageButton) findViewById(R.id.btOpenCarte);
        ImageButton btFermer = (ImageButton) findViewById(R.id.btFermer);

        btFermer.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (flagCartoOpen == 1) {
                            closeCarte();
                        }
                    }
                }
        );
        btOpenCarte.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (flagCartoOpen == 1) {
                            closeCarte();
                        } else {
                            openCarte();
                        }
                    }
                }
        );

        ImageButton myBtMenu = (ImageButton) findViewById(R.id.bt_menu);
        ImageView myLogo = (ImageView) findViewById(R.id.logo);
        // ACTION DES BOUTONS DE LA HOME
        myLogo.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        startActivity(new Intent(FragmentDetailEquipement.this, MainActivity.class));
                    }
                }
        );
        // ACTION DES BOUTONS DE LA HOME
        myBtMenu.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        startActivity(new Intent(FragmentDetailEquipement.this, MainActivity.class));
                    }
                }
        );

        myCarte = (View) findViewById(R.id.map);

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

        myTitreEquipement.setTypeface(myTypeface);
        myMenu1.setTypeface(myTypeface);
        myMenu2.setTypeface(myTypeface);
        myMenu3.setTypeface(myTypeface);

        TextView titre = (TextView) findViewById(R.id.titre);
        titre.setTypeface(myTypeface);
        majBigTitre(titre);

        // *** Bouton du menu
        myMenu1.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (Config.MENU_ACTIVITE != 1) {
                            Config.MENU_ACTIVITE = 1;
                            Config.resetVarNavigation();

                            if (Config.CODE_DE_MON_ACTIVITE == 1) {
                                Config.sql_type = Config.sql_sous_type = Config.fragToReload = getResources().getString(R.string.sqlType1_1);
                            } else if (Config.CODE_DE_MON_ACTIVITE == 3) {
                                Config.sql_type = Config.sql_sous_type = Config.fragToReload = getResources().getString(R.string.sqlType3_1);
                            } else if (Config.CODE_DE_MON_ACTIVITE == 6) {
                                Config.sql_type = Config.sql_sous_type = Config.fragToReload = getResources().getString(R.string.sqlType6_1);
                            }
                            Config.resetFragment();

                            Intent intent = new Intent(FragmentDetailEquipement.this, ListType.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivityForResult(intent, 0);
                        }
                    }
                }
        );
        myMenu2.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (Config.MENU_ACTIVITE != 2) {
                            Config.MENU_ACTIVITE = 2;

                            Config.resetVarNavigation();

                            if (Config.CODE_DE_MON_ACTIVITE == 1) {
                                Config.sql_sous_type = Config.fragToReload = getResources().getString(R.string.sqlType1_2);
                                Config.codeInterne = 5;
                            } else if (Config.CODE_DE_MON_ACTIVITE == 3) {
                                Config.fragToReload = getResources().getString(R.string.sqlType3_2);

                            } else if (Config.CODE_DE_MON_ACTIVITE == 6) {
                                Config.sql_sous_type = Config.fragToReload = getResources().getString(R.string.sqlType6_2);
                            }
                            Config.resetFragment();

                            Intent intent = new Intent(FragmentDetailEquipement.this, ListType.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivityForResult(intent, 0);
                        }
                    }
                }
        );
        myMenu3.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (Config.MENU_ACTIVITE != 3) {
                            Config.MENU_ACTIVITE = 3;

                            Config.resetVarNavigation();

                            if (Config.CODE_DE_MON_ACTIVITE == 1) {
                                Config.codeInterne = 3;
                                Config.sql_sous_type = Config.fragToReload = getResources().getString(R.string.sqlType1_3);

                            } else if (Config.CODE_DE_MON_ACTIVITE == 3) {
                                Config.sql_sous_type = Config.fragToReload = getResources().getString(R.string.sqlBalade);

                            } else if (Config.CODE_DE_MON_ACTIVITE == 5) {
                                Config.sql_sous_type = Config.fragToReload = getResources().getString(R.string.sqlIncontournable);

                            } else if (Config.CODE_DE_MON_ACTIVITE == 6) {
                                Config.sql_sous_type = Config.fragToReload = getResources().getString(R.string.sqlType6_3);
                            }
                            Config.resetFragment();

                            Intent intent = new Intent(FragmentDetailEquipement.this, ListType.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivityForResult(intent, 0);
                        }
                    }
                }
        );

        btAddFavoris.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (flagIsFavoris == 1) {
                            deleteFavoris();
                        } else {
                            addFavoris();
                        }
                    }
                }
        );

        myDbHelper = new DataBaseHelper(Config.myHome.getBaseContext());
        try {
            myDbHelper.openDataBase();
        } catch (SQLException sqle) {
            Log.wtf("myTag", "ouverture bdd KO");
            throw sqle;
        }

        ImageButton myBtParam = (ImageButton) findViewById(R.id.bt_param);
        ImageButton myBtFavoris = (ImageButton) findViewById(R.id.bt_favoris);
        myBtParam.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(FragmentDetailEquipement.this, Parametre.class);
                        startActivityForResult(intent, 2);
                    }
                }
        );
        myBtFavoris.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(FragmentDetailEquipement.this, favoris.class);
                        startActivityForResult(intent, 2);
                    }
                }
        );

        Config.majNbeFav((TextView) findViewById(R.id.txt_nbe_favoris), this.getBaseContext());

        myAsyncTask2 myWebFetch = new myAsyncTask2();
        myWebFetch.execute();
    }

    public void majBigTitre(TextView p_titre) {
        if (Config.flagFromFavoris == 1) {
            p_titre.setText(getResources().getString(R.string.titreFavoris));
        } else {
            if (Config.CODE_DE_MON_ACTIVITE == 1) {
                p_titre.setText(getResources().getString(R.string.libHomeBt1));
            } else if (Config.CODE_DE_MON_ACTIVITE == 3) {
                p_titre.setText(getResources().getString(R.string.libHomeBt3));
            } else if (Config.CODE_DE_MON_ACTIVITE == 5) {
                p_titre.setText(getResources().getString(R.string.libHomeBt5));
            } else if (Config.CODE_DE_MON_ACTIVITE == 6) {
                p_titre.setText(getResources().getString(R.string.libHomeBt6));
            }
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

                        Intent intent = new Intent(FragmentDetailEquipement.this, Reveil.class);
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

    public void closeCarte() {
        flagCartoOpen = 0;
        android.view.ViewGroup.LayoutParams params = myCarte.getLayoutParams();

        params.height = depHaut;
        btOpenCarte.setImageResource(R.drawable.fleche_bas);

        myCarte.setLayoutParams(params);
        layBtFermerCarte.setVisibility(View.GONE);
    }

    public void openCarte() {
        flagCartoOpen = 1;

        android.view.ViewGroup.LayoutParams params = myCarte.getLayoutParams();
        depHaut = params.height;

        Display display = getWindowManager().getDefaultDisplay();

        int height = display.getHeight();  // deprecated
        params.height = (height - (height / 3));

        btOpenCarte.setImageResource(R.drawable.fleche_haut);

        myCarte.setLayoutParams(params);

        layBtFermerCarte.setVisibility(View.VISIBLE);
    }

    public void checkGeocodeur() {
        Geocoder geocoder = new Geocoder(getBaseContext());
        if (geocoder.isPresent()) {
            List<Address> addresses;

            try {
                addresses = geocoder.getFromLocationName(myAdresse + " " + myCp + " " + myVille, 1);
                if (addresses.size() > 0) {
                    latitude = (float) addresses.get(0).getLatitude();
                    longitude = (float) addresses.get(0).getLongitude();
                }
            } catch (Exception ex) {
            }

        }
    }

    public void centerTheMap() {
        if (latitude != 0) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

            MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.picto_map));

            if (myPicto!=null) {
                if (myPicto.length()>0) {
                    if (Config.list_picto.contains(myPicto)) {
                        markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromAsset("picto_carto/" + myPicto + ".png"));
                    }
                }
            }

            markerOptions.position(new LatLng(latitude, longitude));
            markerOptions.title(libEquip);
            mMap.addMarker(markerOptions);
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                public void onMapLoaded() {
                    zoomTheMap();
                }
            });

            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
            mMap.moveCamera(center);
        }
    }

    public void zoomTheMap() {
        LatLng myLocation = new LatLng(latitude, longitude);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
    }


    public void majImgFavoris() {
        if (flagIsFavoris == 1) {
            btAddFavoris.setImageResource(R.drawable.bt_favoris_on);
        } else {
            btAddFavoris.setImageResource(R.drawable.bt_favoris_off);
        }
    }

    public void checkFavoris() {
        flagIsFavoris = 0;
        btAddFavoris.setVisibility(View.VISIBLE);
        if (idEquip != 0) {
            if (myDbHelper.checkFavoris(idEquip) == 1) {
                flagIsFavoris = 1;
            } else if (myDbHelper.checkFavorisXml() == 1) {
                flagIsFavoris = 1;
            }
        } else {
            if (Config.xml_id.length()==0) {
                btAddFavoris.setVisibility(View.GONE);
            }else {
                if (myDbHelper.checkFavorisXml() == 1) {
                    flagIsFavoris = 1;
                }
            }
        }

        majImgFavoris();
    }

    public void loadMap() {
        if (latitude == 0) {
            btOpenCarte.setVisibility(View.INVISIBLE);
            myCarte.setVisibility(View.GONE);
        } else {
            btOpenCarte.setVisibility(View.VISIBLE);
            myCarte.setVisibility(View.VISIBLE);
        }

        centerTheMap();
    }

    public void addFavoris() {
        ContentValues myValue = new ContentValues();

        myValue.put("libelle", libEquip);
        myValue.put("id_equipement", idEquip);
        myValue.put("xml_equipement", Config.xml_id);

        Log.d("myTag", "libEquip : " + libEquip);
        Log.d("myTag", "idEquip : " + idEquip);
        Log.d("myTag", "xml_equipement : " + Config.xml_id);

        myDbHelper.insertFavoris(myValue);
        flagIsFavoris = 1;
        majImgFavoris();

        Config.majNbeFav((TextView) findViewById(R.id.txt_nbe_favoris), this.getBaseContext());
    }

    public void deleteFavoris() {

        Log.d("myTag", "xml id : " + Config.xml_id);

//        if (idEquip==0) {
        myDbHelper.deleteFavorisXml(Config.xml_id);
  /*      }else{
            myDbHelper.deleteFavoris(String.valueOf(idEquip));
        }
        */

        flagIsFavoris = 0;
        majImgFavoris();

        Config.majNbeFav((TextView) findViewById(R.id.txt_nbe_favoris), this.getBaseContext());
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
        Config.majNbeFav((TextView) findViewById(R.id.txt_nbe_favoris), this.getBaseContext());
        Config.showAlertNotif(this);
    }

    private Document parseXML(InputStream stream)
            throws Exception {
        DocumentBuilderFactory objDocumentBuilderFactory = null;
        DocumentBuilder objDocumentBuilder = null;
        Document doc = null;
        try {
            objDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
            objDocumentBuilder = objDocumentBuilderFactory.newDocumentBuilder();

            doc = objDocumentBuilder.parse(stream);
        } catch (Exception ex) {
            throw ex;
        }

        return doc;
    }

    public void loadEquipFromXml(String p_id) {

        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try {
                URL url;

                url = new URL(Config.urlDomaine + "equipements.php?version=" + Config.VERSION_API + "&ids=" + Config.xml_id);

                URLConnection connection = url.openConnection();

                Document doc = parseXML(connection.getInputStream());

                NodeList descNodes = doc.getElementsByTagName("equipement");
                NodeList listChamps;

                ContentValues myValue = new ContentValues();

                contenu = "";
                String complement = "";
                int flag_complement = 0;

                for (int i = 0; i < descNodes.getLength(); i++) {
                    Node courant = descNodes.item(i);
                    NodeList listNode = courant.getChildNodes();

                    for (int j = 0; j < listNode.getLength(); j++) {
                        if (listNode.item(j).getNodeName().equals("titre")) {
                            libEquip = listNode.item(j).getTextContent();

                        } else if (listNode.item(j).getNodeName().equals("adresse")) {
                            myAdresse = listNode.item(j).getTextContent();
                            contenu += listNode.item(j).getTextContent() + "<br>";

                        } else if (listNode.item(j).getNodeName().equals("code_postal")) {
                            myCp = listNode.item(j).getTextContent();
                            contenu += listNode.item(j).getTextContent() + "<br>";

                        } else if (listNode.item(j).getNodeName().equals("ville")) {
                            myVille = listNode.item(j).getTextContent();
                            contenu += listNode.item(j).getTextContent() + "<br>";

                        } else if (listNode.item(j).getNodeName().equals("site_web")) {
                            if (listNode.item(j).getTextContent().length() > 0) {
                                contenu += "Site web : <a href='" + listNode.item(j).getTextContent() + "'>" + listNode.item(j).getTextContent() + "</a><br>";
                            }
                        } else if (listNode.item(j).getNodeName().equals("email")) {
                            if (listNode.item(j).getTextContent().length() > 0) {
                                contenu += "Email : <a href='mailto:" + listNode.item(j).getTextContent() + "'>" + listNode.item(j).getTextContent() + "</a><br>";
                            }
                        } else if (listNode.item(j).getNodeName().equals("telephone")) {
                            if (listNode.item(j).getTextContent().length() > 0) {
                                contenu += "Tel : <a href='tel:" + listNode.item(j).getTextContent() + "'>" + listNode.item(j).getTextContent() + "</a><br>";
                            }
                        } else if (listNode.item(j).getNodeName().equals("longitude")) {
                            if (listNode.item(j).getTextContent().length() > 0) {
                                longitude = Float.valueOf(listNode.item(j).getTextContent());
                            }
                        } else if (listNode.item(j).getNodeName().equals("latitude")) {
                            if (listNode.item(j).getTextContent().length() > 0) {
                                latitude = Float.valueOf(listNode.item(j).getTextContent());
                            }
                        } else if (listNode.item(j).getNodeName().equals("horaires")) {
                            contenu += listNode.item(j).getTextContent() + "<br>";

                        } else if (listNode.item(j).getNodeName().equals("fermeture_exceptionnelle")) {
                            contenu += listNode.item(j).getTextContent() + "<br>";

                        } else if (listNode.item(j).getNodeName().equals("afficher_complement_info")) {
                            if (listNode.item(j).getTextContent().length() > 0) {
                                flag_complement = 1;
                            } else {
                                flag_complement = 0;
                            }

                        } else if (listNode.item(j).getNodeName().equals("complement_info")) {
                            complement = "<br>" + listNode.item(j).getTextContent() + "<br>";
                        }
                    }
                }

                if (flag_complement == 1) {
                    contenu += complement;
                }


                if (latitude == 0) {
                    checkGeocodeur();
                }

            } catch (MalformedURLException e) {
                Log.d("myTag", "This is my MalformedURLException");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("myTag", "Exception Exception");
                e.printStackTrace();
            }

        } catch (Exception e) {
            //	Toast.makeText(Config.myResVehicule,"erreur : " + e.toString(), Toast.LENGTH_LONG).show();
        }

    }

    class myAsyncTask2 extends AsyncTask<Void, Void, Void> {
        TextView tv;

        myAsyncTask2() {
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            tracker = analytics.newTracker(getResources().getString(R.string.google_analytics_id));
            tracker.setScreenName("/Fiche detail equipement : " + libEquip);
            tracker.send(new HitBuilders.ScreenViewBuilder().build());

            myTitreEquipement.setText(Config.formatLastWord(libEquip));

            WebView myTexte = (WebView) findViewById(R.id.description);
            WebSettings settings = myTexte.getSettings();
            settings.setDefaultTextEncodingName("utf-8");

            myTexte.setBackgroundColor(Color.TRANSPARENT);

            myTexte.loadDataWithBaseURL(null, contenu, "text/html", "UTF-8", null);

            checkFavoris();

            Config.flagContentEquip = 0;

            loadMap();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (Config.flagContentEquip == 1) {

                idEquip = 0;

                if (Config.myContentValue.get("picto")!=null) {
                    try {
                        myPicto = Config.myContentValue.get("picto").toString();
                    } catch (Exception e) {
                        myPicto = "";
                    }
                }else{
                    myPicto = "";
                }
                try {
                    libEquip = Config.myContentValue.get("titre").toString();
                } catch (Exception e) {}
                try {
                    contenu = Config.myContentValue.get("adresse").toString() + "<br>";
                } catch (Exception e) {}
                try {
                    myAdresse = Config.myContentValue.get("adresse").toString();
                } catch (Exception e) {}

                try {
                    contenu += Config.myContentValue.get("code_postal").toString() + " " + Config.myContentValue.get("ville").toString() + "<br>";
                    myCp = Config.myContentValue.get("code_postal").toString();
                    myVille = Config.myContentValue.get("ville").toString();

                } catch (Exception e) {
                }
                try {
                    if (Config.myContentValue.get("telephone").toString().length() > 0) {
                        contenu += "Tel : <a href='tel:" + Config.myContentValue.get("telephone").toString() + "'>" + Config.myContentValue.get("telephone").toString() + "</a><br>";
                    }
                } catch (Exception e) {
                }
                try {
                    if (Config.myContentValue.get("email").toString().length() > 0) {
                        contenu += "Email : <a href='mailto:" + Config.myContentValue.get("email").toString() + "'>" + Config.myContentValue.get("email").toString() + "</a><br>";
                    }
                } catch (Exception e) {
                }
                try {
                    if (Config.myContentValue.get("site_web").toString().length() > 0) {
                        contenu += "Site web : <a href='" + Config.myContentValue.get("site_web").toString() + "'>" + Config.myContentValue.get("site_web").toString() + "</a><br>";
                    }
                } catch (Exception e) {
                }
                try {
                    if (Config.myContentValue.get("horaires").toString().length() > 0) {
                        contenu += Config.myContentValue.get("horaires").toString() + "<br>";
                    }
                } catch (Exception e) {
                }

                try {
                    if (Config.myContentValue.get("fermeture_exceptionnelle").toString().length() > 0) {
                        contenu += Config.myContentValue.get("fermeture_exceptionnelle").toString() + "<br>";
                    }
                } catch (Exception e) {
                }

                try {
                    if (Config.myContentValue.get("afficher_complement_info").toString().length() > 0) {
                        if (Config.myContentValue.get("complement_info").toString().length() > 0) {
                            contenu += Config.myContentValue.get("complement_info").toString() + "<br>";
                        }
                    }
                } catch (Exception e) {
                }

                try {
                    longitude = Float.parseFloat((String) Config.myContentValue.get("longitude"));
                } catch (Exception e) {
                }
                try {
                    latitude = Float.parseFloat((String) Config.myContentValue.get("latitude"));
                } catch (Exception e) {
                }

                try {
                    Config.xml_id = Config.myContentValue.get("xml_id").toString();
                } catch (Exception e) {
                }

                return null;


            } else {
                Cursor c;
                int flagTemp = 0;
                idEquip = 0;


                if (Config.xml_id.length() > 0) {
                    c = myDbHelper.loadEquipementFromXML(Config.xml_id);
                } else {
                    c = myDbHelper.loadEquipement(Config.sql_type);
                }

                try {
                    if (c != null) {
                        while (c.moveToNext()) {
                            flagTemp = 1;
                            try {
                                idEquip = c.getInt(13);
                            } catch (Exception e) {
                            }
                            try {
                                libEquip = c.getString(0);
                            } catch (Exception e) {
                            }

                            try {
                                contenu = c.getString(1) + "<br>";
                                myAdresse = c.getString(1);
                            } catch (Exception e) {
                            }
                            try {
                                contenu += c.getString(2) + " " + c.getString(3) + "<br>";
                                myCp = c.getString(2);
                                myVille = c.getString(3);
                            } catch (Exception e) {
                            }
                            try {
                                if (c.getString(8).length() > 0) {
                                    contenu += "Tel : <a href='tel:" + c.getString(8) + "'>" + c.getString(8) + "</a><br>";
                                }

                            } catch (Exception e) {
                            }
                            try {
                                if (c.getString(7).length() > 0) {
                                    contenu += "Email : <a href='mailto:" + c.getString(7) + "'>" + c.getString(7) + "</a><br>";
                                }
                            } catch (Exception e) {
                            }
                            try {
                                if (c.getString(6).length() > 0) {
                                    contenu += "Site web : <a href='" + c.getString(6) + "'>" + c.getString(6) + "</a><br>";
                                }
                            } catch (Exception e) {
                            }
                            try {
                                if (c.getString(4).length() > 0) {
                                    contenu += c.getString(4) + "<br>";
                                }
                            } catch (Exception e) {
                            }

                            try {
                                Config.xml_id = c.getString(15);


                            } catch (Exception e) {
                            }


                            try {
                                if (c.getString(5).length() > 0) {
                                    contenu += c.getString(5) + "<br>";
                                }
                            } catch (Exception e) {
                            }

                            try {
                                if (c.getString(10).length() > 0) {
                                    contenu += c.getString(10) + "<br>";
                                }
                            } catch (Exception e) {
                            }

                            try {
                                longitude = c.getFloat(11);
                            } catch (Exception e) {
                            }
                            try {
                                latitude = c.getFloat(12);
                            } catch (Exception e) {
                            }

                        }

                        if (latitude == 0) {
                            checkGeocodeur();
                        }

                        if (flagTemp == 0) {
                            loadEquipFromXml(Config.xml_id);
                        }
                    }
                } catch (SQLException sqle) {
                    Log.d("myTag", "SQLException : " + sqle.toString());
                    throw sqle;
                }


                return null;

            }

        }

    }

}
