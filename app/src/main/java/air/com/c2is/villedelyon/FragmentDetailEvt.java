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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentDetailEvt extends android.support.v4.app.FragmentActivity {
    private DataBaseHelper myDbHelper;
    public ArrayList<HashMap<String, Object>> listItems;
    public String idToLoad;
    public Button btEquipement;
    public static GoogleAnalytics analytics;
    public static Tracker tracker;
    public ImageButton myAddFavoris;
    public int id_favoris;

    public FragmentDetailEvt() {
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

                        Intent intent = new Intent(FragmentDetailEvt.this, Reveil.class);
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

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
        Config.myActu = this;
        Config.majNbeFav((TextView) findViewById(R.id.txt_nbe_favoris), this.getBaseContext());
        Config.showAlertNotif(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        analytics = GoogleAnalytics.getInstance(Config.myHome.getBaseContext());
        analytics.setLocalDispatchPeriod(1800);
        tracker = analytics.newTracker(getResources().getString(R.string.google_analytics_id));
        tracker.setScreenName("/Fiche detail evenement : " + Config.myContentValue.get("titre").toString());
        tracker.send(new HitBuilders.ScreenViewBuilder().build());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_detail_evt);
        Config.myDetailEvt = this;

        Typeface myTypeface = Typeface.createFromAsset(Config.myHome.getAssets(), "Oswald-Regular.ttf");

        TextView titre = (TextView) findViewById(R.id.titre);
        titre.setTypeface(myTypeface);
        titre.setText(getResources().getString(R.string.libHomeBt5));

        ShareButton shareButton = (ShareButton) findViewById(R.id.share_button);
        if (Config.flagEvtFromFav==1) {
            LinearLayout laySousMenu = (LinearLayout) findViewById(R.id.laySousMenu);
            laySousMenu.setVisibility(View.GONE);
            titre.setText(getResources().getString(R.string.titreFavoris));

            shareButton.setVisibility(View.GONE);
        }else {
            FacebookSdk.sdkInitialize(Config.myHome.getApplicationContext());

            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(Config.myContentValue.get("evenement_url").toString()))
                    .build();
            shareButton.setShareContent(content);
        }

        Button myMenu1    = (Button) findViewById(R.id.bt_menu1);
        Button myMenu2    = (Button) findViewById(R.id.bt_menu2);
        Button myMenu3    = (Button) findViewById(R.id.bt_menu3);

        myMenu1.setText(getResources().getString(R.string.libMenu5_1));
        myMenu2.setText(getResources().getString(R.string.libMenu5_2));
        myMenu3.setText(getResources().getString(R.string.libMenu5_3));

        if (Config.flagFromRecherche==1) {
            myMenu2.setTextColor(getResources().getColor(R.color.blanc));
            myMenu2.setBackground(getResources().getDrawable(R.drawable.menu_actif));
        }else{
            myMenu1.setTextColor(getResources().getColor(R.color.blanc));
            myMenu1.setBackground(getResources().getDrawable(R.drawable.menu_actif));
        }
        myMenu1.setTypeface(myTypeface);
        myMenu2.setTypeface(myTypeface);
        myMenu3.setTypeface(myTypeface);

        ImageButton myBtMenu = (ImageButton) findViewById(R.id.bt_menu);
        ImageView myLogo     = (ImageView) findViewById(R.id.logo);
        // ACTION DES BOUTONS DE LA HOME
        myLogo.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        startActivity(new Intent(FragmentDetailEvt.this, MainActivity.class));
                    }
                }
        );
        // ACTION DES BOUTONS DE LA HOME
        myBtMenu.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        startActivity(new Intent(FragmentDetailEvt.this, MainActivity.class));
                    }
                }
        );

        TextView myTitre     = (TextView) findViewById(R.id.titreDetailEvt);
        TextView myTitre2    = (TextView) findViewById(R.id.typeEvt);
        TextView myTitre3    = (TextView) findViewById(R.id.dateDetailEvt);
        myTitre.setTypeface (myTypeface);
        myTitre2.setTypeface(myTypeface);
        myTitre3.setTypeface(myTypeface);
        myTitre.setText     (Config.myContentValue.get("titre").toString());
        myTitre2.setText    (Config.myContentValue.get("type_principal").toString());
        myTitre3.setText    (Config.myContentValue.get("accroche").toString());

        ImageView imgTetiaire     = (ImageView) findViewById(R.id.imgVisuelEvt);
        BitmapDownloaderTask task = new BitmapDownloaderTask(imgTetiaire);

        if (Config.myContentValue.get("visuel").toString().length()>0)
        {
            task.execute(Config.myContentValue.get("visuel").toString());
        }

        Config.xml_id = Config.myContentValue.get("equipement").toString();

        WebView myTexte2     = (WebView) findViewById(R.id.descriptionEvt);
        WebSettings settings = myTexte2.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        myTexte2.setBackgroundColor(Color.TRANSPARENT);

        try {
            myTexte2.loadDataWithBaseURL(null, Config.myContentValue.get("description").toString(), "text/html", "UTF-8", null);
        }catch(SQLException sqle){
            throw sqle;
        }

        myDbHelper = new DataBaseHelper(Config.myHome.getBaseContext());
        try {
            myDbHelper.openDataBase();
        }catch(SQLException sqle){
            throw sqle;
        }

        myAddFavoris  = (ImageButton) findViewById(R.id.btFavoris);

        myAddFavoris.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (id_favoris==0)
                        {
                            myAddFavoris.setImageDrawable(getResources().getDrawable(R.drawable.bt_favoris_on));
                            addToFavoris();
                        }
                        else
                        {
                            myAddFavoris.setImageDrawable(getResources().getDrawable(R.drawable.bt_favoris_off));
                            myDbHelper.deleteFavorisActu(id_favoris);
                            id_favoris = 0;
                        }
                    }
                }
        );

        if (Config.flagEvtFromFav==1) {
            id_favoris = (int) Config.myContentValue.get("id_favoris");
        }else {
            id_favoris = myDbHelper.checkFavorisEvt(Config.myContentValue.get("xml_id").toString());
        }

        if (id_favoris!=0) {
            myAddFavoris.setImageDrawable(getResources().getDrawable(R.drawable.bt_favoris_on));
        }


        btEquipement    = (Button) findViewById(R.id.btEqt);

        if (Config.myContentValue.get("equipement").toString().length()>0) {

            Cursor c = myDbHelper.loadTitreEquipementFromXML(Config.myContentValue.get("equipement").toString());
            int flag_ok = 0;
            try {
                if (c != null) {
                    while (c.moveToNext()) {
                        btEquipement.setText(c.getString(0));
                        flag_ok = 1;
                    }
                }
            }catch(SQLException sqle){
                throw sqle;
            }
            //myDbHelper.close();

            if (flag_ok==0) {
                myAsyncTask2 myWebFetch = new myAsyncTask2();
                myWebFetch.execute();
            }

        }else{
            btEquipement.setVisibility(View.GONE);
        }

        if (Config.flagEvtFromFav==1) {
            Config.flagEvtFromFav = 0;

            btEquipement.setOnClickListener(
                    new View.OnClickListener() {
                        public void onClick(View v) {
                            Config.flagContentEquip = 0;

                            Intent intent = new Intent(FragmentDetailEvt.this, FragmentDetailEquipement.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivityForResult(intent, 0);
                        }
                    }
            );
        }else {
            btEquipement.setOnClickListener(
                    new View.OnClickListener() {
                        public void onClick(View v) {
                            Config.flagContentEquip = 0;
                            Config.myFragment.loadEquipement();
                        }
                    }
            );
        }

        // *** Bouton du menu
        myMenu1.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {

                    }
                }
        );
        myMenu2.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Config.resetVarNavigation();
                        Config.codeInterne          = 2;
                        Config.MENU_ACTIVITE        = 1;
                        Config.CODE_DE_MON_ACTIVITE = 5;
                        Intent intent = new Intent(FragmentDetailEvt.this, ListType.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent, 0);
                    }
                }
        );
        myMenu3.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Config.resetVarNavigation();
                        Config.codeInterne          = 3;
                        Config.MENU_ACTIVITE        = 1;
                        Config.CODE_DE_MON_ACTIVITE = 5;
                        Intent intent = new Intent(FragmentDetailEvt.this, ListType.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent, 0);
                    }
                }
        );


        ImageButton myBtParam = (ImageButton) findViewById(R.id.bt_param);
        ImageButton myBtFavoris = (ImageButton) findViewById(R.id.bt_favoris);
        myBtParam.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(FragmentDetailEvt.this, Parametre.class);
                        startActivityForResult(intent, 2);
                    }
                }
        );
        myBtFavoris.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(FragmentDetailEvt.this, favoris.class);
                        startActivityForResult(intent, 2);

                    }
                }
        );


        Config.majNbeFav((TextView) findViewById(R.id.txt_nbe_favoris), this.getBaseContext());


    }

    public void addToFavoris() {
        ContentValues myValue = new ContentValues();

        myValue.put("libelle"           , Config.myContentValue.get("titre").toString());
        myValue.put("type_principal"    , Config.myContentValue.get("type_principal").toString());
        myValue.put("accroche"          , Config.myContentValue.get("accroche").toString());
        if (Config.myContentValue.get("visuel").toString().length()>0) {
            myValue.put("visuel", Config.myContentValue.get("visuel").toString());
        }
        myValue.put("description"       , Config.myContentValue.get("description").toString());
        myValue.put("xml_equipement"    , Config.xml_id);

        myValue.put("type"    , 2);

        id_favoris = (int) myDbHelper.insertFavorisActu(myValue);
    }

    private Document parseXML(InputStream stream)
            throws Exception
    {
        DocumentBuilderFactory objDocumentBuilderFactory = null;
        DocumentBuilder objDocumentBuilder               = null;
        Document doc                                     = null;
        try
        {
            objDocumentBuilderFactory   = DocumentBuilderFactory.newInstance();
            objDocumentBuilder          = objDocumentBuilderFactory.newDocumentBuilder();

            doc = objDocumentBuilder.parse(stream);
        }
        catch(Exception ex)
        {
            throw ex;
        }

        return doc;
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

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                    try {
                        URL url = new URL("http://appvilledelyon.c2is.fr/equipements.php?version=3&ids="+Config.xml_id);

                        //Log.d("myTag", "http://appvilledelyon.c2is.fr/equipements.php?version=3&ids="+Config.xml_id);

                        URLConnection connection = url.openConnection();

                        Document doc = parseXML(connection.getInputStream());

                        NodeList descNodes = doc.getElementsByTagName("equipement");
                        NodeList listChamps;

                        ContentValues myValue = new ContentValues();

                        for(int i=0; i<descNodes.getLength();i++)
                        {
                            Node courant        = descNodes.item(i);
                            NodeList listNode   = courant.getChildNodes();

                            for(int j=0; j<listNode.getLength();j++) {
                                try {
                                    if(listNode.item(j).getNodeName().equals("titre")){
                                        myValue.put("titre", listNode.item(j).getTextContent());
                                        btEquipement.setText(listNode.item(j).getTextContent());

                                    } else if (listNode.item(j).getNodeName().equals("xml_id")) {
                                        myValue.put("xml_id", listNode.item(j).getTextContent());
                                    } else if (listNode.item(j).getNodeName().equals("mots_clefs")) {
                                        myValue.put("mots_clefs", listNode.item(j).getTextContent());
                                    } else if (listNode.item(j).getNodeName().equals("adresse")) {
                                        myValue.put("adresse", listNode.item(j).getTextContent());
                                    } else if (listNode.item(j).getNodeName().equals("code_postal")) {
                                        myValue.put("code_postal", listNode.item(j).getTextContent());
                                    } else if (listNode.item(j).getNodeName().equals("ville")) {
                                        myValue.put("ville", listNode.item(j).getTextContent());
                                    } else if (listNode.item(j).getNodeName().equals("complement_info")) {
                                        myValue.put("complement_info", listNode.item(j).getTextContent());
                                    } else if (listNode.item(j).getNodeName().equals("site_web")) {
                                        myValue.put("site_web", listNode.item(j).getTextContent());
                                    } else if (listNode.item(j).getNodeName().equals("email")) {
                                        myValue.put("email", listNode.item(j).getTextContent());
                                    } else if (listNode.item(j).getNodeName().equals("telephone")) {
                                        myValue.put("telephone", listNode.item(j).getTextContent());
                                    } else if (listNode.item(j).getNodeName().equals("afficher_complement_info")) {
                                        myValue.put("afficher_complement", listNode.item(j).getTextContent());
                                    } else if (listNode.item(j).getNodeName().equals("longitude")) {
                                        myValue.put("longitude", listNode.item(j).getTextContent());
                                    } else if (listNode.item(j).getNodeName().equals("latitude")) {
                                        myValue.put("latitude", listNode.item(j).getTextContent());
                                    } else if (listNode.item(j).getNodeName().equals("horaires")) {
                                        myValue.put("horaires", listNode.item(j).getTextContent());
                                    } else if (listNode.item(j).getNodeName().equals("jour")) {
                                        myValue.put("jour", listNode.item(j).getTextContent());
                                    } else if (listNode.item(j).getNodeName().equals("arrondissement")) {
                                        myValue.put("arrondissement", listNode.item(j).getTextContent());
                                    } else if (listNode.item(j).getNodeName().equals("type_associe")) {
                                        myValue.put("type_associe", listNode.item(j).getTextContent());
                                    }

                                } catch (Exception e) {
                                    Log.d("myTag", "Exception apres");
                                }
                            }
                        }

                        Config.myContentEquip = myValue;

                        Log.d("myTag", "xml : " + Config.myContentEquip.getAsString("titre"));

                    } catch (MalformedURLException e) {
                        Log.d("myTag", "This is my MalformedURLException");
                    } catch (Exception e) {
                        Log.d("myTag", "toto");
                    }

            } catch (Exception e) {
                Log.d("myTag", "catché : " + e.toString());

            }

            return null;
        }
    }

}
