package air.com.c2is.villedelyon;
import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class RechercheVieFragment extends android.support.v4.app.FragmentActivity {
    private DataBaseHelper myDbHelper;
    public LinearLayout myChargement;
    public TextView titreFrom1;
    public TextView titreFrom2;
    public TextView myChargementText;
    public ArrayList<HashMap<String, Object>> listItems;
    public int flagTypeToEquip;
    public int flagDirectEquipement;
    public Spinner spinnerJour;
    public Spinner spinnerTheme;
    public static GoogleAnalytics analytics;
    public static Tracker tracker;
    public ArrayList<HashMap<String, Object>> tabCategorie;
    public String[] arraySpinner;


    public List<String> list;


    public RechercheVieFragment() {
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
        tracker.setScreenName("/Recherche ville equitable");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_recherche_vie);

        Typeface myTypeface = Typeface.createFromAsset(Config.myHome.getAssets(), "Oswald-Regular.ttf");

        TextView titre = (TextView) findViewById(R.id.titre);
        titre.setTypeface(myTypeface);

        TextView txtIntro = (TextView) findViewById(R.id.txtIntro);
        txtIntro.setTypeface(myTypeface);

        Button myMenu1    = (Button) findViewById(R.id.bt_menu1);
        Button myMenu2    = (Button) findViewById(R.id.bt_menu2);
        Button myMenu3    = (Button) findViewById(R.id.bt_menu3);

        spinnerJour       = (Spinner) findViewById(R.id.spinnerJour);
        spinnerTheme      = (Spinner) findViewById(R.id.spinnerTheme);

        //spinnerTheme.setAdapter();

        ImageView myLogo = (ImageView) findViewById(R.id.logo);
        // ACTION DES BOUTONS DE LA HOME
        myLogo.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        startActivity(new Intent(RechercheVieFragment.this, MainActivity.class));
                    }
                }
        );

        TextView titreFrom1 = (TextView) findViewById(R.id.titreFrom1);
        TextView titreFrom2 = (TextView) findViewById(R.id.titreFrom2);

        myMenu1.setText(getResources().getString(R.string.libMenu1_1));
        myMenu2.setText(getResources().getString(R.string.libMenu1_2));
        myMenu3.setText(getResources().getString(R.string.libMenu1_3));

        myMenu2.setTextColor(getResources().getColor(R.color.blanc));
        myMenu2.setBackground(getResources().getDrawable(R.drawable.menu_actif));

        myMenu1.setTypeface(myTypeface);
        myMenu2.setTypeface(myTypeface);
        myMenu3.setTypeface(myTypeface);

        titreFrom1.setTypeface(myTypeface);
        titreFrom2.setTypeface(myTypeface);


        // *** Bouton du menu
        myMenu1.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Config.codeInterne          = 1;
                        Config.MENU_ACTIVITE        = 1;
                        Config.CODE_DE_MON_ACTIVITE = 1;
                        Intent intent = new Intent(RechercheVieFragment.this, ListType.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent, 0);
                    }
                }
        );
        myMenu2.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        finish();
                    }
                }
        );
        myMenu3.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Config.codeInterne          = 3;
                        Config.MENU_ACTIVITE        = 3;
                        Config.CODE_DE_MON_ACTIVITE = 1;
                        Intent intent = new Intent(RechercheVieFragment.this, ListType.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent, 0);
                    }
                }
        );

        Button myValider    = (Button) findViewById(R.id.btValider);
        myValider.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Config.flagDirectMarche = 1;

                        if (spinnerJour.getSelectedItemPosition()!=0) {
                            Config.str_marche_jour = spinnerJour.getSelectedItem().toString();
                        }else{
                            Config.str_marche_jour = "";
                        }
                        if (spinnerTheme.getSelectedItemPosition()!=0) {
                            Config.str_marche_theme = getSqlMarche(spinnerTheme.getSelectedItemPosition());
                        }else{
                            Config.str_marche_theme = "";
                        }

                        Config.codeInterne          = 4;
                        Config.MENU_ACTIVITE        = 2;
                        Config.CODE_DE_MON_ACTIVITE = 1;

                        Intent intent = new Intent(RechercheVieFragment.this, ResMarche.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent, 5);
                    }
                }
        );
        myValider.setTypeface(myTypeface);

        myDbHelper = new DataBaseHelper(Config.myHome.getBaseContext());
        try {
            myDbHelper.openDataBase();
           // Log.d("myTag", "ouverture bdd ok");
        }catch(SQLException sqle){
           // Log.d("myTag", "ouverture bdd KO");
            throw sqle;
        }

        ImageButton myBtParam = (ImageButton) findViewById(R.id.bt_param);
        ImageButton myBtFavoris = (ImageButton) findViewById(R.id.bt_favoris);
        myBtParam.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(RechercheVieFragment.this, Parametre.class);
                        startActivityForResult(intent, 2);
                    }
                }
        );
        myBtFavoris.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(RechercheVieFragment.this, favoris.class);
                        startActivityForResult(intent, 2);

                    }
                }
        );


        Config.majNbeFav((TextView) findViewById(R.id.txt_nbe_favoris), this.getBaseContext());


        list = new ArrayList<String>();
        list.add("Par thème");


        // lancement du chargement HTTP
        myAsyncTask2 myWebFetch = new myAsyncTask2();
        myWebFetch.execute();


    }

    public void majTheme() {
        ArrayAdapter<String> adp2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,list);
        adp2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTheme.setAdapter(adp2);
    }

    public int checkInList(String p_param){
        for (int i=0;i<list.size();i++) {
            if (list.get(i).equals(p_param)) {
                return 1;
            }
        }
        return 0;
    }

    public String getSqlMarche(int p_index) {
        switch (p_index) {
            case 0:
                return "";
            case 1:
                return "alimentaires";
            case 2:
                return "biologique";
            case 3:
                return "loisirs";
            case 4:
                return "divers";
            case 5:
                return "soir";
            case 6:
                return "manufactur";
            default:
                return "";
        }

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

        myAsyncTask2()    {

        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            for(int j=0; j<tabCategorie.size();j++) {
                Log.d("myTag", tabCategorie.get(j).get("categorie").toString());

            }

            majTheme();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            tabCategorie = new ArrayList<HashMap<String, Object>>();

            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                try {
                    URL url = new URL("http://appvilledelyon.c2is.fr/equipements.php?version="+Config.VERSION_API+"&type=label-lyon-ville-equitable-et-durable-vie-quotidienne");

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
                            if(listNode.item(j).getNodeName().equals("categorie")){
                                String[] parts = listNode.item(j).getTextContent().split(";");

                                for(int k=0;k<parts.length;k++) {
                                    if (checkInList(parts[k])==0) {
                                        list.add(parts[k]);
                                    }
                                }

                            }
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

}