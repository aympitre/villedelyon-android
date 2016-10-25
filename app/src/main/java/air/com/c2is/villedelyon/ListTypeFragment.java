package air.com.c2is.villedelyon;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/**
 * A placeholder fragment containing a simple view.
 */
public class ListTypeFragment extends Fragment {
    protected DataBaseHelper    myDbHelper;
    public LinearLayout         myChargement;
    public RelativeLayout       layBtCarto;
    public ImageButton          btCarto;
    public TextView             myChargementText;
    public ListView             mylistview;
    public ArrayList<HashMap<String, Object>> listItems;
    public int flagTypeToEquip;
    public int flagDirectEquipement;
    public myAsyncTask2 myWebFetch;
    public GPSTracker gps;
    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    public ListTypeFragment() {
    }

    public void setGoogleAnalytics() {
        analytics = GoogleAnalytics.getInstance(Config.myHome.getBaseContext());
        analytics.setLocalDispatchPeriod(1800);

        String str_tag = "";
        if (Config.MENU_ACTIVITE == 1) {
            if (Config.CODE_DE_MON_ACTIVITE == 3) {
                str_tag = getResources().getString(R.string.libHomeBt1) + "/" + getResources().getString(R.string.libMenu1_3);
            }else if (Config.CODE_DE_MON_ACTIVITE == 2) {
                str_tag = getResources().getString(R.string.libHomeBt1) + "/" + getResources().getString(R.string.libMenu1_2);
            }else {
                str_tag = getResources().getString(R.string.libHomeBt1) + "/" + getResources().getString(R.string.libMenu1_1);
            }
        }else if (Config.MENU_ACTIVITE == 3) {
            if (Config.CODE_DE_MON_ACTIVITE == 3) {
                str_tag = getResources().getString(R.string.libHomeBt3) + "/" + getResources().getString(R.string.libMenu3_3);
            }else if (Config.CODE_DE_MON_ACTIVITE == 2) {
                str_tag = getResources().getString(R.string.libHomeBt3) + "/" + getResources().getString(R.string.libMenu3_2);
            }else {
                str_tag = getResources().getString(R.string.libHomeBt3) + "/" + getResources().getString(R.string.libMenu3_1);
            }

        }else if (Config.MENU_ACTIVITE == 6) {
            if (Config.CODE_DE_MON_ACTIVITE == 3) {
                str_tag = getResources().getString(R.string.libHomeBt6) + "/" + getResources().getString(R.string.libMenu6_3);
            }else if (Config.CODE_DE_MON_ACTIVITE == 2) {
                str_tag = getResources().getString(R.string.libHomeBt6) + "/" + getResources().getString(R.string.libMenu6_2);
            }else {
                str_tag = getResources().getString(R.string.libHomeBt6) + "/" + getResources().getString(R.string.libMenu6_1);
            }
        }

        tracker = analytics.newTracker(getResources().getString(R.string.google_analytics_id));
        tracker.setScreenName("/Liste : " + str_tag);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        setGoogleAnalytics();

        Config.pointCarto   = new ArrayList<HashMap<String, Object>>();
        flagTypeToEquip     = 0;
        flagDirectEquipement= 0;
        View rootView       = inflater.inflate(R.layout.fragment_list_type, container, false);
        Typeface myTypeface = Typeface.createFromAsset(Config.myHome.getAssets(), "Oswald-Regular.ttf");

        myChargement        =  (LinearLayout) rootView.findViewById(R.id.myChargement);
        myChargementText    =  (TextView) rootView.findViewById(R.id.myChargementText);
        Button myMenu1      = (Button) rootView.findViewById(R.id.bt_menu1);
        Button myMenu2      = (Button) rootView.findViewById(R.id.bt_menu2);
        Button myMenu3      = (Button) rootView.findViewById(R.id.bt_menu3);

        layBtCarto          =  (RelativeLayout) rootView.findViewById(R.id.layBtCarto);
        btCarto             =  (ImageButton) rootView.findViewById(R.id.btCarto);

        LinearLayout layLyonDirect = (LinearLayout) rootView.findViewById(R.id.layLyonDirect);

        if (Config.CODE_DE_MON_ACTIVITE==1) {
            myMenu1.setText(getResources().getString(R.string.libMenu1_1));
            myMenu2.setText(getResources().getString(R.string.libMenu1_2));
            myMenu3.setText(getResources().getString(R.string.libMenu1_3));

        }else if (Config.CODE_DE_MON_ACTIVITE==3) {
            myMenu1.setText(getResources().getString(R.string.libMenu3_1));
            myMenu2.setText(getResources().getString(R.string.libMenu3_2));
            myMenu3.setText(getResources().getString(R.string.libMenu3_3));

        }else if (Config.CODE_DE_MON_ACTIVITE==4) {
            myMenu1.setText(getResources().getString(R.string.libMenu4_1));
            myMenu2.setText(getResources().getString(R.string.libMenu4_2));
            myMenu3.setText(getResources().getString(R.string.libMenu4_3));

        }else if (Config.CODE_DE_MON_ACTIVITE==5) {
            myMenu1.setText(getResources().getString(R.string.libMenu5_1));
            myMenu2.setText(getResources().getString(R.string.libMenu5_2));
            myMenu3.setText(getResources().getString(R.string.libMenu5_3));

        }else if (Config.CODE_DE_MON_ACTIVITE==6) {
            myMenu1.setText(getResources().getString(R.string.libMenu6_1));
            myMenu2.setText(getResources().getString(R.string.libMenu6_2));
            myMenu3.setText(getResources().getString(R.string.libMenu6_3));

            if (Config.MENU_ACTIVITE==1) {
                Button btLyonDirect = (Button) rootView.findViewById(R.id.btLyonDirect);
                btLyonDirect.setTypeface(myTypeface);

                btLyonDirect.setOnClickListener(
                        new View.OnClickListener() {
                            public void onClick(View v) {
                                goTelDirect();
                            }
                        }
                );

                layLyonDirect.setVisibility(View.VISIBLE);
            }
        }

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

        myChargementText.setTypeface(myTypeface);
        myMenu1.setTypeface(myTypeface);
        myMenu2.setTypeface(myTypeface);
        myMenu3.setTypeface(myTypeface);

        if (Config.flagShowCarto==1) {
            showBtCarto();
            Config.flagShowCarto = 0;
        }else {
            hideBtCarto();
        }

        btCarto.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Config.myFragment.loadCarto();
                    }
                }
        );

        // *** Bouton du menu
        myMenu1.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        myWebFetch.cancel(true);

                        Config.str_demarche         = "";
                        Config.flagForceRetour      = 0;

                        Config.MENU_ACTIVITE = 1;
                        if (Config.CODE_DE_MON_ACTIVITE == 1) {
                            Config.fragToReload = getResources().getString(R.string.sqlType1_1);
                            Config.myFragment.loadFragment(getResources().getString(R.string.sqlType1_1));
                        } else if (Config.CODE_DE_MON_ACTIVITE == 3) {
                            Config.fragToReload  = getResources().getString(R.string.sqlType3_1);
                            Config.flagShowCarto = 1;
                            Config.myFragment.loadFragment(getResources().getString(R.string.sqlType3_1));
                        } else if (Config.CODE_DE_MON_ACTIVITE == 5) {
                            Config.myFragment.loadEvenement();
                        } else if (Config.CODE_DE_MON_ACTIVITE == 6) {
                            Config.sql_sous_type = Config.fragToReload = getResources().getString(R.string.sqlType6_1);
                            Config.myFragment.loadFragment(getResources().getString(R.string.sqlType6_1));
                        }
                        Config.resetFragment();
                    }
                }
        );
        myMenu2.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        myWebFetch.cancel(true);

                        Config.str_demarche         = "";
                        Config.flagForceRetour      = 0;

                        Config.MENU_ACTIVITE = 2;
                        if (Config.CODE_DE_MON_ACTIVITE == 1) {
                            Config.fragToReload = getResources().getString(R.string.sqlType1_2);
                            Config.myFragment.loadFragment(getResources().getString(R.string.sqlType1_2));

                        } else if (Config.CODE_DE_MON_ACTIVITE == 3) {
                            Config.fragToReload = getResources().getString(R.string.sqlType3_2);
                            Config.myFragment.loadFragment(getResources().getString(R.string.sqlType3_2));

                        } else if (Config.CODE_DE_MON_ACTIVITE == 5) {
                            Config.myFragment.loadRechercheEvenement();

                        } else if (Config.CODE_DE_MON_ACTIVITE == 6) {
                            Config.sql_sous_type = Config.fragToReload = getResources().getString(R.string.sqlType6_2);
                            Config.myFragment.loadFragment(getResources().getString(R.string.sqlType6_2));
                        }
                        Config.resetFragment();
                    }
                }
        );

        myMenu3.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        myWebFetch.cancel(true);
                        Config.MENU_ACTIVITE = 3;

                        Config.str_demarche         = "";
                        Config.flagForceRetour      = 0;

                        if (Config.CODE_DE_MON_ACTIVITE==1) {
                            Config.fragToReload = getResources().getString(R.string.sqlType1_3);
                            Config.myFragment.loadFragment(getResources().getString(R.string.sqlType1_3));
                        }else if (Config.CODE_DE_MON_ACTIVITE==3) {
                            Config.fragToReload = getResources().getString(R.string.sqlBalade);
                            Config.myFragment.loadFragment(getResources().getString(R.string.sqlBalade));
                        }else if (Config.CODE_DE_MON_ACTIVITE==5) {
                            Config.fragToReload = getResources().getString(R.string.sqlIncontournable);
                            Config.myFragment.loadFragment(getResources().getString(R.string.sqlIncontournable));
                        } else if (Config.CODE_DE_MON_ACTIVITE == 6) {
                            Config.sql_sous_type = Config.fragToReload = getResources().getString(R.string.sqlType6_3);
                            Config.myFragment.loadFragment(getResources().getString(R.string.sqlType6_3));
                        }
                        Config.resetFragment();
                    }
                }
        );

        mylistview = (ListView) rootView.findViewById(R.id.mylistview);

        mylistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                if (flagTypeToEquip==1) {
                    Config.flagShowCarto = 1;
                    Config.sql_sous_type = listItems.get(position).get("slug").toString();
                    Config.myFragment.loadFragment(listItems.get(position).get("slug").toString());

                    Config.flagForceRetour      = 1;


                    if (Config.flagBisRetour==1) {
                        Config.fragToReload = getResources().getString(R.string.sqlType1_2);
                    }else if (Config.flagBisRetour==2) {
                        Config.fragToReload = getResources().getString(R.string.sqlType1_3);
                    }

                    if (Config.fragToReload==getResources().getString(R.string.sqlType1_2)) {
                        Config.flagBisRetour = 1;
                    }else if (Config.fragToReload==getResources().getString(R.string.sqlType1_3)) {
                        Config.flagBisRetour = 2;
                    }else if (Config.fragToReload==getResources().getString(R.string.sqlType3_2)) {
                        Config.flagBisRetour = 3;

                    }

                }else {
                    Log.d("myTag", "code activite : " + Config.CODE_DE_MON_ACTIVITE + " menu : " +Config.MENU_ACTIVITE);

                    if ((Config.CODE_DE_MON_ACTIVITE == 3) && (Config.MENU_ACTIVITE == 3)) {
                        Config.myContentValue = listItems.get(position);
                        Config.flagForceRetour = 1;
                        Config.myFragment.loadBalade();

                    } else if ((Config.CODE_DE_MON_ACTIVITE == 1) && (Config.MENU_ACTIVITE == 1)) {
                        Config.actu_titre       = listItems.get(position).get("titre").toString();
                        Config.actu_telephone   = listItems.get(position).get("mots_clefs").toString();
                        goTel();

                    } else if ((Config.CODE_DE_MON_ACTIVITE == 5) && (Config.MENU_ACTIVITE == 3)) {
                        Config.myContentValue = listItems.get(position);
                        Config.myFragment.loadIncontrounable();

                    } else if ((Config.CODE_DE_MON_ACTIVITE == 6) && (flagDirectEquipement==1)) {

                        Config.flagForceRetour = 1;
                        Config.myDemarcheTitre = listItems.get(position).get("titre").toString();
                        Config.myDemarcheDesc  = listItems.get(position).get("complement_info").toString();

                        try {
                            Config.myLabel  = listItems.get(position).get("label").toString();
                            Config.myId     = listItems.get(position).get("id").toString();
                            Config.myType   = listItems.get(position).get("type").toString();

                            Config.form_civilite	= listItems.get(position).get("form_civilite").toString();
                            Config.form_nom		    = listItems.get(position).get("form_nom").toString();
                            Config.form_prenom	    = listItems.get(position).get("form_prenom").toString();
                            Config.form_tel		    = listItems.get(position).get("form_tel").toString();
                            Config.form_email		= listItems.get(position).get("form_email").toString();
                            Config.form_message	    = listItems.get(position).get("form_message").toString();
                            Config.form_image		= listItems.get(position).get("form_image").toString();
                            Config.form_loc         = listItems.get(position).get("form_loc").toString();


                        } catch (Exception e) {
                        }

                        if (listItems.get(position).get("type_associe").toString().length()>0) {
                            Config.myFragment.loadDemarche(listItems.get(position).get("type_associe").toString());
                        }else{
                            Config.myFragment.loadDemarche("");
                        }

                    } else if (((Config.CODE_DE_MON_ACTIVITE == 3) && (Config.MENU_ACTIVITE == 1))||(flagDirectEquipement==1)) {
                        if (flagDirectEquipement==1) {
                            Config.flagContentEquip = 1;
                        }
                        Config.myContentValue   = listItems.get(position);
                        Config.sql_type         = listItems.get(position).get("id_equipement").toString();
                        Config.xml_id           = "";

                        Config.myFragment.loadEquipement();
                    }

                }

            }
        });

        try {
            gps = new GPSTracker(Config.myHome);
        } catch (Exception e) {
        }

        myDbHelper = new DataBaseHelper(Config.myHome.getBaseContext());
        try {
            myDbHelper.openDataBase();
            // Log.d("myTag", "ouverture bdd ok");
        }catch(SQLException sqle){
            // Log.d("myTag", "ouverture bdd KO");
            throw sqle;
        }

/*  exemple pour copier la BDD sur le téléphone
        try {
            myDbHelper.copyToSD();
        }catch(IOException sqle){
        }
        */

        Log.d("myTag", "list type fragment");

        myWebFetch = new myAsyncTask2();
        myWebFetch.execute();

        return rootView;
    }

    public void showBtCarto() {
        layBtCarto.setVisibility(View.VISIBLE);
    }
    public void hideBtCarto() {
        layBtCarto.setVisibility(View.GONE);
    }

    public void showNoResult() {
       // Log.d("myTag", "showNoResult : " + Config.fragToReload + "/" + Config.sql_sous_type +"/"+ Config.wait_sous_type);

        myChargementText.setText(getResources().getString(R.string.libVide));
        myChargement.setVisibility(View.VISIBLE);
        mylistview.setVisibility(View.INVISIBLE);
        hideBtCarto();
    }
    public void showChargement () {
        myChargementText.setText(getResources().getString(R.string.libChargement2));
        myChargement.setVisibility(View.VISIBLE);
        mylistview.setVisibility(View.INVISIBLE);

    }
   public void hideChargement() {
        myChargement.setVisibility(View.GONE);
        mylistview.setVisibility(View.VISIBLE);
    }

    public void goTel() {
        new AlertDialog.Builder(Config.myFragment)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(Config.actu_titre)
                .setMessage("Souhaitez vous appeler le : " + Config.actu_telephone + " ?")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which) {
                        Uri smsUri = Uri.parse("tel:"+Config.actu_telephone);
                        Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
                        startActivityForResult(intent, 15);
                    }

                })
                .setNegativeButton("Non", null)
                .show();
    }

    public void goTelDirect() {
        new AlertDialog.Builder(Config.myFragment)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Lyon en direct")
                .setMessage("Souhaitez vous appeler le 04 72 10 30 30 ?")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which) {
                        Uri smsUri = Uri.parse("tel:0472103030");
                        Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
                        startActivityForResult(intent, 15);
                    }

                })
                .setNegativeButton("Non", null)
                .show();
    }

    public int checkRechercheMarche() {
        if (Config.str_marche_arrondissement != 0) {
            return 1;
        }
        if (Config.str_marche_jour.length()>0) {
            return 1;
        }
        if (Config.str_marche_theme.length()>0) {
            return 1;
        }

        return 0;
    }

    public void resetRechercheMarche() {
        Config.str_marche_arrondissement = 0;
        Config.str_marche_jour           = "";
        Config.str_marche_theme          = "";

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
        int kill3G;
        SimpleAdapter mSchedule;

        myAsyncTask2()    {
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            mylistview.setAdapter(null);
            mylistview.setAdapter(mSchedule);

            if (kill3G==0) {
                if (listItems.size() == 0) {
                    myWebFetch.cancel(true);
                    Config.MENU_ACTIVITE = 1;

                    if (Config.CODE_DE_MON_ACTIVITE != 5) {
                        if ((Config.MENU_ACTIVITE == 1) || (Config.MENU_ACTIVITE == 3) || (Config.MENU_ACTIVITE == 6)) {
                            Config.fragToReload = Config.wait_sous_type;
                            Config.sql_sous_type = Config.wait_sous_type;
                            Config.myFragment.loadFragment(Config.sql_sous_type);
                            Config.resetFragment();
                        }
                    }

                    showNoResult();

                } else {
                    hideChargement();
                }

                resetRechercheMarche();

                Log.d("myTag", "victory list : " + listItems.size() + "/" + flag);
                flag++;
            }
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            flag    = 0;
            kill3G  = 0;
        }


        @Override
        protected Void doInBackground(Void... params) {
            listItems = new ArrayList<HashMap<String, Object>>();

            try {
                HashMap<String, Object> item;

                Cursor c;
                int flagBalade      = 0;
                int flagUrgence     = 0;
                int flagInc         = 0;
                int flagEq          = 0;
                int flagDemarcheXML = 0;
                int flagTri         = 0;

                if (Config.sql_sous_type.length()>0) {
                    Config.wait_sous_type = Config.sql_sous_type;
                }

                // >> numéro d'urgence
                if ((Config.CODE_DE_MON_ACTIVITE==1)&&(Config.MENU_ACTIVITE==1)) {
                    c           = myDbHelper.loadUrgence();
                    flagUrgence = 1;

                }else {
                    if (Config.sql_type.equals(getResources().getString(R.string.sqlBalade))) {
                        flagBalade = 1;
                        c = myDbHelper.loadBalade();

                    } else if (Config.sql_type.equals(getResources().getString(R.string.sqlIncontournable))) {
                        flagInc = 1;
                        c = myDbHelper.loadIncontournable();

                    } else if (Config.sql_type.equals(getResources().getString(R.string.sqlType3_1))) {
                        flagEq = 1;
                        c = myDbHelper.loadEquipementFromType(Config.sql_type);

                    }else if (Config.sql_sous_type.length()>0) {
                        flagEq = 1;
                        c = myDbHelper.loadEquipementFromType(Config.sql_sous_type);
                        Config.sql_sous_type = "";
                        flagDirectEquipement = 1;

                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);

                        try {
//                            String str_url = "http://c2is:c2is@prep.c2is.fr/appvilledelyon/current/equipements.php?version="+Config.VERSION_API+"&type="+Config.sql_type;

                            String str_url = "http://appvilledelyon.c2is.fr/equipements.php?version="+Config.VERSION_API+"&type="+Config.sql_type;

                            URL url = new URL(str_url);
                            URLConnection connection = url.openConnection();

                            Document doc = parseXML(connection.getInputStream());

                            NodeList descNodes = doc.getElementsByTagName("equipement");
                            NodeList listChamps;

                            for(int i=0; i<descNodes.getLength();i++) {
                                HashMap<String, Object> mapping = new HashMap<String, Object>();
                                Node courant        = descNodes.item(i);
                                NodeList listNode   = courant.getChildNodes();
                                int flagOk = 1;

                                String tempLat  = "";
                                String tempLong = "";

                                for (int j = 0; j < listNode.getLength(); j++) {
                                    if (listNode.item(j).getNodeName().equals("titre")) {
                                        mapping.put("titre", listNode.item(j).getTextContent());
                                    } else if (listNode.item(j).getNodeName().equals("longitude")) {
                                        mapping.put("longitude", listNode.item(j).getTextContent());
                                        tempLong = listNode.item(j).getTextContent();
                                    } else if (listNode.item(j).getNodeName().equals("latitude")) {
                                        mapping.put("latitude", listNode.item(j).getTextContent());
                                        tempLat = listNode.item(j).getTextContent();

                                    }else if (listNode.item(j).getNodeName().equals("form_titre")) {

                                        NodeList temp        = listNode.item(j).getChildNodes();
                                        for (int k = 0; k < temp.getLength(); k++) {

                                            if (temp.item(k).getNodeName().equals("label")) {
                                                mapping.put("label", temp.item(k).getTextContent());
                                            } else if (temp.item(k).getNodeName().equals("id")) {
                                                mapping.put("id", temp.item(k).getTextContent());
                                            } else if (temp.item(k).getNodeName().equals("type")) {
                                                mapping.put("type", temp.item(k).getTextContent());
                                            }

                                        }

                                    } else if (listNode.item(j).getNodeName().equals("form_civilite")) {
                                        mapping.put("form_civilite", listNode.item(j).getTextContent());
                                    } else if (listNode.item(j).getNodeName().equals("form_nom")) {
                                        mapping.put("form_nom", listNode.item(j).getTextContent());
                                    } else if (listNode.item(j).getNodeName().equals("form_prenom")) {
                                        mapping.put("form_prenom", listNode.item(j).getTextContent());
                                    } else if (listNode.item(j).getNodeName().equals("form_tel")) {
                                        mapping.put("form_tel", listNode.item(j).getTextContent());
                                    } else if (listNode.item(j).getNodeName().equals("form_email")) {
                                        mapping.put("form_email", listNode.item(j).getTextContent());
                                    } else if (listNode.item(j).getNodeName().equals("form_message")) {
                                        mapping.put("form_message", listNode.item(j).getTextContent());
                                    } else if (listNode.item(j).getNodeName().equals("form_image")) {
                                        mapping.put("form_image", listNode.item(j).getTextContent());
                                    } else if (listNode.item(j).getNodeName().equals("form_loc")) {
                                        mapping.put("form_loc", listNode.item(j).getTextContent());


                                    } else if (listNode.item(j).getNodeName().equals("code_postal")) {
                                        mapping.put("code_postal", listNode.item(j).getTextContent());
                                    } else if (listNode.item(j).getNodeName().equals("ville")) {
                                        mapping.put("ville", listNode.item(j).getTextContent());
                                    } else if (listNode.item(j).getNodeName().equals("email")) {
                                        mapping.put("email", listNode.item(j).getTextContent());

                                    } else if (listNode.item(j).getNodeName().equals("site_web")) {
                                        mapping.put("site_web", listNode.item(j).getTextContent());

                                    } else if (listNode.item(j).getNodeName().equals("horaires")) {
                                        mapping.put("horaires", listNode.item(j).getTextContent());

                                    } else if (listNode.item(j).getNodeName().equals("fermeture_exceptionnelle")) {
                                        mapping.put("fermeture_exceptionnelle", listNode.item(j).getTextContent());

                                    } else if (listNode.item(j).getNodeName().equals("afficher_complement_info")) {
                                        mapping.put("afficher_complement_info", listNode.item(j).getTextContent());
                                    } else if (listNode.item(j).getNodeName().equals("adresse")) {
                                        mapping.put("adresse", listNode.item(j).getTextContent());
                                    } else if (listNode.item(j).getNodeName().equals("id_equipement")) {
                                        mapping.put("id_equipement", listNode.item(j).getTextContent());
                                    } else if (listNode.item(j).getNodeName().equals("arrondissement")) {
                                        mapping.put("arrondissement", listNode.item(j).getTextContent());
                                    } else if (listNode.item(j).getNodeName().equals("jour")) {
                                        mapping.put("jour", listNode.item(j).getTextContent());
                                    } else if (listNode.item(j).getNodeName().equals("type_associe")) {
                                        mapping.put("type_associe", listNode.item(j).getTextContent());
                                    } else if (listNode.item(j).getNodeName().equals("complement_info")) {
                                        mapping.put("complement_info", listNode.item(j).getTextContent());
                                    } else if (listNode.item(j).getNodeName().equals("xml_id")) {
                                        mapping.put("xml_equipement", listNode.item(j).getTextContent());
                                        mapping.put("xml_id", listNode.item(j).getTextContent());
                                    }
                                }

                                mapping.put("id_equipement", "");

                                if (gps.canGetLocation()) {
                                    Location location = new Location("");

                                    if (tempLat.length()>0) {
                                        location.setLatitude(Double.parseDouble(tempLat) );
                                        location.setLongitude(Double.parseDouble(tempLong));
                                    }

                                    flagTri = 1;
                                    try {
                                        mapping.put("distance", gps.location.distanceTo(location));
                                    } catch (Exception e) {
                                        mapping.put("distance", 0);
                                    }
                                }else{
                                    gps.showSettingsAlert();
                                }

                                listItems.add(mapping);
                            }

                        } catch (MalformedURLException e) {
                        }

                    } else {
                        flagTypeToEquip = 1;
                        c = myDbHelper.loadType(Config.sql_type);
                    }
                }
                int flagKo = 0;
                if ((flagDemarcheXML==0)&&(flagDirectEquipement!=1)) {
                    try {
                        if (c != null) {

                            while (c.moveToNext()) {

                                item = new HashMap<String, Object>();
                                flagKo = 0;

                                if (flagBalade == 1) {
                                    item.put("titre"        , c.getString(0));
                                    item.put("content"      , c.getString(1));
                                    item.put("visuel"       , c.getString(2));
                                    item.put("tetiaire"     , c.getString(3));
                                    item.put("tetiaire_hd"  , c.getString(4));
                                    item.put("points"       , c.getString(5));

                                } else if (flagUrgence == 1) {
                                    item.put("titre"        , c.getString(0));
                                    item.put("mots_clefs"   , c.getString(1));
                                } else if (flagEq == 1) {

                                    if (Config.str_marche_theme.length() > 0) {
                                        try {
                                            if (c.getString(0).toLowerCase().indexOf(Config.str_marche_theme.toLowerCase()) == -1) {
                                                flagKo = 1;
                                            }
                                        } catch (Exception e) {
                                            flagKo = 1;
                                            Log.d("myTag", "mini Exception dans la liste");
                                        }
                                    }

                                    if (Config.str_marche_arrondissement != 0) {
                                        try {
                                            if (c.getString(4).equals(String.valueOf(Config.str_marche_arrondissement))) {
                                                //                                                Log.d("myTag", "t " + c.getString(4) + "/" + String.valueOf(Config.str_marche_arrondissement));
                                            } else {
                                                flagKo = 1;
                                            }
                                        } catch (Exception e) {
                                            flagKo = 1;
                                            Log.d("myTag", "mini Exception dans la liste");
                                        }
                                    }

                                    if (Config.str_marche_jour.length() > 0) {
                                        try {
                                            if (c.getString(5).toLowerCase().indexOf(Config.str_marche_jour.toLowerCase()) == -1) {
                                                flagKo = 1;
                                            }
                                        } catch (Exception e) {
                                            flagKo = 1;
                                            Log.d("myTag", "mini Exception dans la liste");
                                        }
                                    }

                                    try {
                                        item.put("titre", c.getString(0));
                                    } catch (Exception e) {
                                        flagKo = 1;
                                    }
                                    try {
                                        item.put("longitude", c.getDouble(1));
                                    } catch (Exception e) {
                                        flagKo = 1;
                                    }
                                    try {
                                        item.put("latitude", c.getDouble(2));
                                    } catch (Exception e) {
                                        flagKo = 1;
                                    }

                                    if (Config.flag_tri_geoloc==1) {
                                        if (gps.canGetLocation()) {
                                            Location location = new Location("");

                                            location.setLatitude(c.getDouble(2));
                                            location.setLongitude(c.getDouble(1));

                                            flagTri = 1;
                                            try {
                                                item.put("distance", gps.location.distanceTo(location));
                                            } catch (Exception e) {
                                                item.put("distance", 0);
                                            }
                                        }else{
                                            gps.showSettingsAlert();
                                        }
                                    }else{
                                        item.put("distance", 0);
                                    }


                                    try {
                                        item.put("id_equipement", c.getString(3));
                                    } catch (Exception e) {
                                        flagKo = 1;
                                    }
                                    try {
                                        item.put("arrondissement", c.getString(4));
                                    } catch (Exception e) {
                                        flagKo = 1;
                                    }
                                    try {
                                        item.put("jour", c.getString(5));
                                    } catch (Exception e) {
                                        flagKo = 1;
                                    }
                                    try {
                                        item.put("type_associe", c.getString(6));
                                    } catch (Exception e) {
                                        Log.d("myTag", "erreur type_associe");

                                    }
                                    try {
                                        item.put("complement_info", c.getString(7));
                                    } catch (Exception e) {
                                        Log.d("myTag", "erreur complement_info");
                                    }

                                } else if (flagInc == 1) {
                                    item.put("titre", c.getString(0));
                                    item.put("visuel", c.getString(1));
                                    item.put("description", c.getString(3));
                                    item.put("tetiaire", c.getString(7));
                                    item.put("tetiaire_hd", c.getString(8));
                                    item.put("accroche", c.getString(2));
                                    item.put("type_principal", c.getString(9));

                                } else {
                                    item.put("libelle", c.getString(0));
                                    item.put("slug", c.getString(1));
                                }

                                if (flagKo == 0) {
                                    listItems.add(item);
                                }

                            }
                        }
                    } catch (SQLException sqle) {
                        Log.d("myTag", "SQLException dans la liste");
                        throw sqle;
                    }
                }

                // Tri de la liste en fonction de la géolocalisation
                if (flagTri==1) {
                    if (Config.flag_tri_geoloc == 1) {
                        Collections.sort(listItems, new CustomComparator1());
                    }
                }

                Config.pointCarto = listItems;

                if (flagBalade==1) {
                    mSchedule = new SimpleAdapter(Config.myHome.getBaseContext(), listItems, R.layout.itembalade,
                            new String[]{"titre", "visuel", "content"},
                            new int[]{R.id.titre, R.id.imgBalade, R.id.description});

                    mSchedule.setViewBinder(new MyViewBinderBalade());
                }else if (flagUrgence==1) {
                    mSchedule = new SimpleAdapter(Config.myHome.getBaseContext(), listItems, R.layout.itemurgence,
                            new String[]{"titre", "mots_clefs"},
                            new int[]{R.id.titre, R.id.numero});

                    mSchedule.setViewBinder(new MyViewBinderUrgence());
                }else if (flagEq==1) {
                    mSchedule = new SimpleAdapter(Config.myHome.getBaseContext(), listItems, R.layout.itemdefault,
                            new String[]{"titre"},
                            new int[]{R.id.titre});

                    mSchedule.setViewBinder(new MyViewBinderDefault());

                }else if (flagInc==1) {
                    mSchedule = new SimpleAdapter(Config.myHome.getBaseContext(), listItems, R.layout.itemevt,
                            new String[]{"titre",  "accroche", "type_principal", "visuel"},
                            new int[]{R.id.titreEvtListe,  R.id.accroche, R.id.type, R.id.imgactualite});

                    mSchedule.setViewBinder(new MyViewBinderIncontournable());
                }else {
                    mSchedule = new SimpleAdapter(Config.myHome.getBaseContext(), listItems, R.layout.itemdefault,
                            new String[]{"libelle", "slug"},
                            new int[]{R.id.titre, R.id.texte});

                    mSchedule.setViewBinder(new MyViewBinderDefault());
                }


                resetRechercheMarche();
                return null;


            } catch (Exception e) {
                resetRechercheMarche();
                kill3G = 1;
                Log.d("myTag", "grosse Exception dans la liste" + e.toString());
                //	Toast.makeText(Config.myResVehicule,"erreur : " + e.toString(), Toast.LENGTH_LONG).show();
            }

            resetRechercheMarche();
            return null;
        }
    }


    public class CustomComparator1 implements Comparator<HashMap> {
        public int compare(HashMap arg0, HashMap arg1) {

            float val1 = 0;
            float val2 = 1;

            try {
                val1 = Float.parseFloat(arg0.get("distance").toString());
                val2 = Float.parseFloat(arg1.get("distance").toString());
            } catch (Exception e) {
            }

            if (val1==val2) {
                return 0;

            }else if (val1<val2) {
                return -1;
            }else{
                return 1;

            }
        }
    }
}
