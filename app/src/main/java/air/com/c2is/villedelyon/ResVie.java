package air.com.c2is.villedelyon;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

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


public class ResVie extends Activity {
    public ListView mylistview;
    public LinearLayout myChargement;
    public TextView myChargementText;
    public TextView myAucunText;
    public ArrayList<HashMap<String, Object>> listItems;
    public static GoogleAnalytics analytics;
    public static Tracker tracker;
    public int nbeResultat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        nbeResultat = 0;
        analytics   = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);
        tracker = analytics.newTracker(getResources().getString(R.string.google_analytics_id));
        tracker.setScreenName("/Resultat recherche lyon ville equitable");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_marche);

        FacebookSdk.sdkInitialize(getApplicationContext());
        Config.myActu = this;

        listItems   = new ArrayList<HashMap<String, Object>>();

        myChargement=  (LinearLayout) this.findViewById(R.id.myChargement);
        mylistview 	=  (ListView) this.findViewById(R.id.mylistview);

        Typeface myTypeface         = Typeface.createFromAsset(getAssets(), "Oswald-Regular.ttf");
        TextView myTitre            = (TextView) findViewById(R.id.titre);

        myChargementText   = (TextView) findViewById(R.id.myChargementText);
        myChargementText.setTypeface(myTypeface);
        myAucunText        = (TextView) findViewById(R.id.myAucunText);
        myAucunText.setTypeface(myTypeface);

        myTitre.setTypeface(myTypeface);

        Button myMenu1 = (Button) findViewById(R.id.bt_menu1);
        Button myMenu2 = (Button) findViewById(R.id.bt_menu2);
        Button myMenu3 = (Button) findViewById(R.id.bt_menu3);
        myMenu1.setText(getResources().getString(R.string.libMenu1_1));
        myMenu2.setText(getResources().getString(R.string.libMenu1_2));
        myMenu3.setText(getResources().getString(R.string.libMenu1_3));
        myMenu1.setTypeface(myTypeface);
        myMenu2.setTypeface(myTypeface);
        myMenu3.setTypeface(myTypeface);

        myMenu2.setTextColor(getResources().getColor(R.color.blanc));
        myMenu2.setBackground(getResources().getDrawable(R.drawable.menu_actif));

        // ACTION DES BOUTONS DU HEADER
        ImageView myLogo = (ImageView) findViewById(R.id.logo);

        myLogo.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        startActivity(new Intent(ResVie.this, MainActivity.class));
                    }
                }
        );
        ImageButton myBtFavoris = (ImageButton) findViewById(R.id.bt_favoris);
        myBtFavoris.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(ResVie.this, favoris.class);
                        startActivityForResult(intent, 2);
                    }
                }
        );
        ImageButton myBtParam = (ImageButton) findViewById(R.id.bt_param);
        myBtParam.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(ResVie.this, Parametre.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent, 0);

                    }
                }
        );
        ImageButton myBtMenu = (ImageButton) findViewById(R.id.bt_menu);
        myBtMenu.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        startActivity(new Intent(ResVie.this, MainActivity.class));
                    }
                }
        );


        // *** Bouton du menu
        myMenu1.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Config.resetVarNavigation();
                        Config.MENU_ACTIVITE = 1;
                        Config.myHome.goInfo();
                    }
                }
        );
        myMenu2.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {

                    }
                }
        );

        myMenu3.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Config.resetVarNavigation();
                        Config.MENU_ACTIVITE = 3;
                        Config.myHome.goLoisir();
                    }
                }
        );

        // lancement du chargement HTTP
        myAsyncTask2 myWebFetch = new myAsyncTask2();
        myWebFetch.execute();


        mylistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Config.myContentValue = listItems.get(position);
                Config.xml_id = "";
                Config.flagContentEquip = 1;

                Intent intent = new Intent(ResVie.this, FragmentDetailEquipement.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, 0);
            }
        });
        Config.majNbeFav((TextView) findViewById(R.id.txt_nbe_favoris), this.getBaseContext());

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
        SimpleAdapter mSchedule;

        myAsyncTask2()    {

        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

//            Log.d("myTag", "Taille : " + listItems.size());

            mSchedule = new SimpleAdapter(Config.myHome.getBaseContext(), listItems, R.layout.itemdefault,
                    new String[]{"titre"},
                    new int[]{R.id.titre});

            mSchedule.setViewBinder(new MyViewBinderDefault());

            mylistview.setAdapter(mSchedule);

            if (nbeResultat==0) {
                myChargement.setVisibility(View.VISIBLE);
                myAucunText.setVisibility(View.VISIBLE);
                myChargementText.setVisibility(View.GONE);
            }else {
                myChargement.setVisibility(View.GONE);
                mylistview.setVisibility(View.VISIBLE);
            }
       }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nbeResultat = 0;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                try {
                    URL url = new URL("http://prep.c2is.fr/appvilledelyon/current/equipements.php?version=5&type=label-lyon-ville-equitable-et-durable");
//                    URL url = new URL("http://appvilledelyon.c2is.fr/equipements.php?version=2&type=label-lyon-ville-equitable-et-durable-vie-quotidienne");
                    URLConnection connection = url.openConnection();

                    Document doc = parseXML(connection.getInputStream());

                    NodeList descNodes = doc.getElementsByTagName("equipement");
                    NodeList listChamps;

                    ContentValues myValue = new ContentValues();

                    for(int i=0; i<descNodes.getLength();i++)
                    {
                        HashMap<String, Object> mapping = new HashMap<String, Object>();
                        Node courant                    = descNodes.item(i);
                        NodeList listNode               = courant.getChildNodes();
                        int flagOk = 1;

                        for(int j=0; j<listNode.getLength();j++) {
                            if(listNode.item(j).getNodeName().equals("titre")){
                                mapping.put("titre", listNode.item(j).getTextContent());

                            }else if(listNode.item(j).getNodeName().equals("arrondissement")){

                                if ((Config.str_marche_arrondissement != 0)&&(Config.str_marche_arrondissement != 10)) {
                                    try {
                                        if (!listNode.item(j).getTextContent().equals(String.valueOf(Config.str_marche_arrondissement))) {
                                            flagOk = 0;
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                                mapping.put("arrondissement", listNode.item(j).getTextContent());

                            }else if(listNode.item(j).getNodeName().equals("adresse")){
                                mapping.put("adresse", listNode.item(j).getTextContent());
                            }else if(listNode.item(j).getNodeName().equals("code_postal")){
                                mapping.put("code_postal", listNode.item(j).getTextContent());
                            }else if(listNode.item(j).getNodeName().equals("ville")){
                                mapping.put("ville", listNode.item(j).getTextContent());
                            }else if(listNode.item(j).getNodeName().equals("telephone")){
                                mapping.put("telephone", listNode.item(j).getTextContent());
                            }else if(listNode.item(j).getNodeName().equals("email")){
                                mapping.put("email", listNode.item(j).getTextContent());
                            }else if(listNode.item(j).getNodeName().equals("site_web")){
                                mapping.put("site_web", listNode.item(j).getTextContent());
                            }else if(listNode.item(j).getNodeName().equals("horaires")){
                                if (Config.str_marche_jour.length() > 0) {
                                    try {
                                        if (listNode.item(j).getTextContent().toLowerCase().indexOf(Config.str_marche_jour.toLowerCase()) == -1) {
                                            flagOk = 0;
                                        }
                                    } catch (Exception e) {
                                    }
                                }

                                mapping.put("horaires", listNode.item(j).getTextContent());
                            }else if(listNode.item(j).getNodeName().equals("xml_id")){
                                mapping.put("xml_id", listNode.item(j).getTextContent());
                            }else if(listNode.item(j).getNodeName().equals("fermeture_exceptionnelle")){
                                mapping.put("fermeture_exceptionnelle", listNode.item(j).getTextContent());
                            }else if(listNode.item(j).getNodeName().equals("afficher_complement_info")){
                                mapping.put("afficher_complement_info", listNode.item(j).getTextContent());
                            }else if(listNode.item(j).getNodeName().equals("longitude")){
                                mapping.put("longitude", listNode.item(j).getTextContent());
                            }else if(listNode.item(j).getNodeName().equals("latitude")){
                                mapping.put("latitude", listNode.item(j).getTextContent());
                            }else if(listNode.item(j).getNodeName().equals("sous_type")){

                                //Log.d("myTag", listNode.item(j).getTextContent() + "/" + Config.str_marche_theme);

                                if (Config.str_marche_theme.length() > 0) {
                                    try {
                                        if (listNode.item(j).getTextContent().toLowerCase().indexOf(Config.str_marche_theme.toLowerCase()) == -1) {
                                            flagOk = 0;
                                        }
                                    } catch (Exception e) {
                                    }
                                }

                                mapping.put("sous_type", listNode.item(j).getTextContent());
                            }
                        }

                        if (flagOk==1) {
                            nbeResultat++;
                            listItems.add(mapping);
                        }
                    }

                } catch (MalformedURLException e) {
                    Log.d("myTag", "This is my MalformedURLException");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                return null;

            } catch (Exception e) {
                //	Toast.makeText(Config.myResVehicule,"erreur : " + e.toString(), Toast.LENGTH_LONG).show();
            }

            return null;
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
        Config.myActu  =this;
        Config.majNbeFav((TextView) findViewById(R.id.txt_nbe_favoris), this.getBaseContext());
        Config.showAlertNotif(this);
    }
}
