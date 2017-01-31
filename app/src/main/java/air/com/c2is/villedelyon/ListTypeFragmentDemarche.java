package air.com.c2is.villedelyon;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
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


/**
 * A placeholder fragment containing a simple view.
 */
public class ListTypeFragmentDemarche extends ListTypeFragment {
    public myAsyncTask2 myWebFetch;
    public ImageButton myAddFavoris;
    public int id_favoris;
    public int flagIsFavoris;
    public DialogOk myDialOk;
    public ListTypeFragmentDemarche() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        flagTypeToEquip     = 0;
        flagDirectEquipement= 0;
        View rootView       = inflater.inflate(R.layout.fragment_list_type_demarche, container, false);
        Typeface myTypeface = Typeface.createFromAsset(Config.myHome.getAssets(), "Oswald-Regular.ttf");

        Config.myFragDemarche = this;
        myDialOk              = new DialogOk(getActivity());

        TextView myTitreEquipement = (TextView) rootView.findViewById(R.id.titreEquipement);
        myTitreEquipement.setTypeface(myTypeface);

        myTitreEquipement.setText(Config.formatLastWord(Config.myDemarcheTitre));

        WebView myTexte      = (WebView) rootView.findViewById(R.id.description);
        WebSettings settings = myTexte.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        myTexte.setBackgroundColor(Color.TRANSPARENT);

        myTexte.loadDataWithBaseURL(null, Config.myDemarcheDesc, "text/html", "UTF-8", null);

        myChargement      = (LinearLayout) rootView.findViewById(R.id.myChargement);
        myChargementText  = (TextView) rootView.findViewById(R.id.myChargementText);
        Button myMenu1    = (Button) rootView.findViewById(R.id.bt_menu1);
        Button myMenu2    = (Button) rootView.findViewById(R.id.bt_menu2);
        Button myMenu3    = (Button) rootView.findViewById(R.id.bt_menu3);

        layBtCarto        =  (RelativeLayout) rootView.findViewById(R.id.layBtCarto);
        btCarto           =  (ImageButton) rootView.findViewById(R.id.btCarto);

        LinearLayout layLyonDirect = (LinearLayout) rootView.findViewById(R.id.layLyonDirect);
        LinearLayout layLyonAlerte = (LinearLayout) rootView.findViewById(R.id.layLyonAlerte);

        if (Config.myId.length()>0) {
            layLyonAlerte.setVisibility(View.VISIBLE);
        }else{
            layLyonAlerte.setVisibility(View.GONE);
        }

        Button btLyonTelephone  = (Button) rootView.findViewById(R.id.btLyonTelephone);
        Button btLyonMail       = (Button) rootView.findViewById(R.id.btLyonMail);

        myMenu1.setText(getResources().getString(R.string.libMenu6_1));
        myMenu2.setText(getResources().getString(R.string.libMenu6_2));
        myMenu3.setText(getResources().getString(R.string.libMenu6_3));

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

        hideBtCarto();

        // *** Bouton du menu
        myMenu1.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        myWebFetch.cancel(true);
                        Config.MENU_ACTIVITE = 1;
                        Config.sql_sous_type = Config.fragToReload = getResources().getString(R.string.sqlType6_1);
                        Config.myFragment.loadFragment(getResources().getString(R.string.sqlType6_1));
                        Config.resetFragment();
                    }
                }
        );
        myMenu2.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        myWebFetch.cancel(true);
                        Config.MENU_ACTIVITE = 2;
                        Config.sql_sous_type = Config.fragToReload = getResources().getString(R.string.sqlType6_2);
                        Config.myFragment.loadFragment(getResources().getString(R.string.sqlType6_2));
                        Config.resetFragment();
                    }
                }
        );

        myMenu3.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        myWebFetch.cancel(true);
                        Config.MENU_ACTIVITE = 3;
                        Config.sql_sous_type = Config.fragToReload = getResources().getString(R.string.sqlType6_3);
                        Config.myFragment.loadFragment(getResources().getString(R.string.sqlType6_3));
                        Config.resetFragment();
                    }
                }
        );

        mylistview = (ListView) rootView.findViewById(R.id.mylistview);

        mylistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Config.flagContentEquip = 1;
                Config.myContentValue   = listItems.get(position);

                Config.xml_id           = "";
                Config.myFragment.loadEquipement();
            }
        });

        myAddFavoris  = (ImageButton) rootView.findViewById(R.id.btFavoris);

        myAddFavoris.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (id_favoris==0) {
                            myAddFavoris.setImageDrawable(getResources().getDrawable(R.drawable.bt_favoris_on));
                            addToFavoris();

                        }else{
                            myAddFavoris.setImageDrawable(getResources().getDrawable(R.drawable.bt_favoris_off));
                            myDbHelper.deleteFavorisActu(id_favoris);
                            id_favoris = 0;
                        }
                    }
                }
        );

        myDbHelper = new DataBaseHelper(Config.myHome.getBaseContext());
        try {
            myDbHelper.openDataBase();
            // Log.d("myTag", "ouverture bdd ok");
        }catch(SQLException sqle){
            // Log.d("myTag", "ouverture bdd KO");
            throw sqle;
        }

        checkFavoris();

        myWebFetch = new myAsyncTask2();
        myWebFetch.execute();

        btLyonTelephone.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        goTelDirect();
                    }
                }
        );

        btLyonMail.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        goFormAlerte();
                    }
                }
        );

        Config.flagDirectDemarche = 0;

        return rootView;
    }

    public void majImgFavoris() {
        if (id_favoris == 0) {
            myAddFavoris.setImageDrawable(getResources().getDrawable(R.drawable.bt_favoris_off));
        } else {
            myAddFavoris.setImageDrawable(getResources().getDrawable(R.drawable.bt_favoris_on));
        }
    }
    public void checkFavoris() {

        if (Config.myDemarcheTitre.length()>0) {
            id_favoris = myDbHelper.checkFavorisDemarche(Config.myDemarcheTitre.replace("'","''"));
        }

        majImgFavoris();
    }

    public void addToFavoris() {
        Log.d("myTag", "addToFavoris");
        ContentValues myValue = new ContentValues();

        myValue.put("libelle"           , Config.myDemarcheTitre);
        myValue.put("type_principal"    , "");
        myValue.put("accroche"          , "");
        myValue.put("visuel"            , "");
        myValue.put("description"       , Config.myDemarcheDesc);
        myValue.put("xml_equipement"    , Config.str_demarche);
        myValue.put("type"              , 4);

        id_favoris = (int) myDbHelper.insertFavorisActu(myValue);
    }

    public void showPopUp() {
        myDialOk.show();
    }

    public void goFormAlerte() {
        Config.myFragment.loadFormAlert();
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



    public void majListSize(int p_num) {
        android.view.ViewGroup.LayoutParams lp = (android.view.ViewGroup.LayoutParams) mylistview.getLayoutParams();
        lp.height = (p_num*150);
        mylistview.setLayoutParams(lp);
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
        SimpleAdapter mSchedule;
        myAsyncTask2()    {
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            mylistview.setAdapter(null);
            mylistview.setAdapter(mSchedule);

            hideChargement();

            resetRechercheMarche();

            if (listItems.size()==0) {
                android.view.ViewGroup.LayoutParams lp = (android.view.ViewGroup.LayoutParams) mylistview.getLayoutParams();
                lp.height = 40;
                mylistview.setLayoutParams(lp);

            }else{
                majListSize(listItems.size());
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                listItems = new ArrayList<HashMap<String, Object>>();
                HashMap<String, Object> item;

                int flagBalade  = 0;
                int flagUrgence = 0;
                int flagInc     = 0;
                int flagEq      = 0;
                int flagDemarcheXML = 0;

                Config.flagDemarche = 0;
                flagEq              = 1;

                int flagInt = 1;

                Log.d("myTag", "str demarche : " + Config.str_demarche);

                try {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    try {
                        URL url;
                        if (Config.isInteger(Config.str_demarche)) {
                            url = new URL("http://appvilledelyon.c2is.fr/equipements.php?version=" + Config.VERSION_API + "&ids=" + Config.str_demarche);
//                            url = new URL("http://c2is:c2is@prep.c2is.fr/appvilledelyon/current/equipements.php?version=" + Config.VERSION_API + "&ids=" + Config.str_demarche);

                        }else {

//                            url = new URL("http://c2is:c2is@prep.c2is.fr/appvilledelyon/current/equipements.php?version=" + Config.VERSION_API + "&type=" + Config.str_demarche);
                            url = new URL("http://appvilledelyon.c2is.fr/equipements.php?version=" + Config.VERSION_API + "&type=" + Config.str_demarche);
                        }

                        URLConnection connection = url.openConnection();

                        Document doc = parseXML(connection.getInputStream());

                        NodeList descNodes = doc.getElementsByTagName("equipement");
                        NodeList listChamps;

                        ContentValues myValue = new ContentValues();

                        flagDemarcheXML = 1;

                        for(int i=0; i<descNodes.getLength();i++)
                        {
                            HashMap<String, Object> mapping = new HashMap<String, Object>();
                            Node courant = descNodes.item(i);
                            NodeList listNode = courant.getChildNodes();
                            for(int j=0; j<listNode.getLength();j++) {
                                if(listNode.item(j).getNodeName().equals("titre")){
                                    mapping.put("titre", listNode.item(j).getTextContent());
                                } else if (listNode.item(j).getNodeName().equals("xml_id")) {
                                    mapping.put("xml_id", listNode.item(j).getTextContent());
                                } else if (listNode.item(j).getNodeName().equals("mots_clefs")) {
                                    mapping.put("mots_clefs", listNode.item(j).getTextContent());
                                } else if (listNode.item(j).getNodeName().equals("adresse")) {
                                    mapping.put("adresse", listNode.item(j).getTextContent());
                                } else if (listNode.item(j).getNodeName().equals("code_postal")) {
                                    mapping.put("code_postal", listNode.item(j).getTextContent());
                                } else if (listNode.item(j).getNodeName().equals("ville")) {
                                    mapping.put("ville", listNode.item(j).getTextContent());
                                } else if (listNode.item(j).getNodeName().equals("complement_info")) {
                                    mapping.put("complement_info", listNode.item(j).getTextContent());
                                } else if (listNode.item(j).getNodeName().equals("site_web")) {
                                    mapping.put("site_web", listNode.item(j).getTextContent());
                                } else if (listNode.item(j).getNodeName().equals("email")) {
                                    mapping.put("email", listNode.item(j).getTextContent());
                                } else if (listNode.item(j).getNodeName().equals("telephone")) {
                                    mapping.put("telephone", listNode.item(j).getTextContent());
                                } else if (listNode.item(j).getNodeName().equals("afficher_complement_info")) {
                                    mapping.put("afficher_complement", listNode.item(j).getTextContent());
                                } else if (listNode.item(j).getNodeName().equals("longitude")) {
                                    mapping.put("longitude", listNode.item(j).getTextContent());
                                } else if (listNode.item(j).getNodeName().equals("latitude")) {
                                    mapping.put("latitude", listNode.item(j).getTextContent());
                                } else if (listNode.item(j).getNodeName().equals("jour")) {
                                    mapping.put("jour", listNode.item(j).getTextContent());
                                } else if (listNode.item(j).getNodeName().equals("arrondissement")) {
                                    mapping.put("arrondissement", listNode.item(j).getTextContent());
                                } else if (listNode.item(j).getNodeName().equals("type_associe")) {
                                    mapping.put("type_associe", listNode.item(j).getTextContent());
                                } else if (listNode.item(j).getNodeName().equals("horaires")) {
                                    mapping.put("horaires", listNode.item(j).getTextContent());
                                }
                            }
                            listItems.add(mapping);
                        }
                    } catch (MalformedURLException e) {
                        Log.d("myTag", "This is my MalformedURLException");
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                 } catch (Exception e) {
                    //	Toast.makeText(Config.myResVehicule,"erreur : " + e.toString(), Toast.LENGTH_LONG).show();
                 }

            } catch (SQLException sqle) {
                Log.d("myTag", "SQLException dans la liste");
                throw sqle;
            }

            mSchedule = new SimpleAdapter(Config.myHome.getBaseContext(), listItems, R.layout.itemdefault,
                    new String[]{"titre"},
                    new int[]{R.id.titre});

            mSchedule.setViewBinder(new MyViewBinderDefault());

            return null;
        }
    }
}
