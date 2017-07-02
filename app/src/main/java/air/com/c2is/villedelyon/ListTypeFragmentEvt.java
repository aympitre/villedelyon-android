package air.com.c2is.villedelyon;

import android.content.ContentValues;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AbsListView;

import com.facebook.appevents.AppEventsLogger;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/**
 * A placeholder fragment containing a simple view.
 */
public class ListTypeFragmentEvt extends Fragment {
    private DataBaseHelper myDbHelper;
    public LinearLayout     myChargement;
    public LinearLayout     layBtAvant;
    public LinearLayout     layBtSuite;
    public TextView         myChargementText;
    public ListView         mylistview;
    public ArrayList<HashMap<String, Object>> listItems;
    public int flagTypeToEquip;
    public int flagDirectEquipement;
    public int debutLimit;
    public SimpleAdapter mSchedule3;
    public myAsyncTask3 myWebFetch3;

    public ListTypeFragmentEvt() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        debutLimit          = 1;
        flagTypeToEquip     = 0;
        flagDirectEquipement= 0;
        View rootView       = inflater.inflate(R.layout.fragment_list_type, container, false);
        Typeface myTypeface = Typeface.createFromAsset(Config.myHome.getAssets(), "Oswald-Regular.ttf");

        layBtAvant        =  (LinearLayout) rootView.findViewById(R.id.layBtAvant);
        layBtAvant.setVisibility(View.GONE);
        Button btSuiteAvant = (Button) rootView.findViewById(R.id.btSuiteAvant);
        btSuiteAvant.setTypeface(myTypeface);

        btSuiteAvant.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        loadOldEvent();
                    }
                }
        );

        layBtSuite        =  (LinearLayout) rootView.findViewById(R.id.layBtSuite);
        layBtSuite.setVisibility(View.GONE);
        Button btSuiteEvt = (Button) rootView.findViewById(R.id.btSuiteEvt);
        btSuiteEvt.setTypeface(myTypeface);

        btSuiteEvt.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        loadNextEvent();
                    }
                }
        );

        myChargement      =  (LinearLayout) rootView.findViewById(R.id.myChargement);
        myChargementText  =  (TextView) rootView.findViewById(R.id.myChargementText);
        myChargementText.setTypeface(myTypeface);

        mylistview = (ListView) rootView.findViewById(R.id.mylistview);

        mylistview.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && (mylistview.getLastVisiblePosition() - mylistview.getHeaderViewsCount() -
                        mylistview.getFooterViewsCount()) >= (mSchedule3.getCount() - 1)) {

                    if (Config.flagFromRecherche==0) {
                        showBtSuite();
                    }
                }else{
                    //hideBtSuite();
                }

                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && (mylistview.getFirstVisiblePosition()==0)) {

                    if (Config.flagFromRecherche==0) {
                        showBtAvant();
                    }
                }else{
                    hideBtAvant();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        mylistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Config.myContentValue = listItems.get(position);
                Config.myFragment.loadDetailEvenement();
            }

        });

        listItems = new ArrayList<HashMap<String, Object>>();

        // lancement du chargement HTTP
        myWebFetch3 = new myAsyncTask3();
        myWebFetch3.execute();

        return rootView;
    }


    public void showBtSuite() {
        layBtSuite.setVisibility(View.VISIBLE);
    }
    public void hideBtSuite() {
        layBtSuite.setVisibility(View.GONE);
    }
    public void showBtAvant() {
        if (debutLimit>1) {
            layBtAvant.setVisibility(View.VISIBLE);
        }
    }
    public void hideBtAvant() {
        layBtAvant.setVisibility(View.GONE);
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

    public int checkModeRecherche() {
        if (Config.str_titre_evt.length()>0) {
            return 1;
        }
        if (Config.date_evt instanceof Date) {
            return 1;
        }
        return 0;
    }

    public void loadOldEvent() {
        hideBtAvant();

        listItems = new ArrayList<HashMap<String, Object>>();

        myChargement.setVisibility(View.VISIBLE);
        mylistview.setVisibility(View.GONE);

        debutLimit = debutLimit - 1;

        myWebFetch3.execute();
    }

    public void loadNextEvent() {
        hideBtSuite();

        listItems = new ArrayList<HashMap<String, Object>>();

        myChargement.setVisibility(View.VISIBLE);
        mylistview.setVisibility(View.GONE);

        debutLimit = debutLimit + 1;

        myWebFetch3.execute();
    }

    class myAsyncTask3 extends AsyncTask<Void, Void, Void> {

        myAsyncTask3()    {

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            mylistview.setAdapter(null);

            if (listItems.size()==0) {
                myChargementText.setText(R.string.libAucun);
            }else {
                mylistview.setAdapter(mSchedule3);
                myChargement.setVisibility(View.GONE);
                mylistview.setVisibility(View.VISIBLE);
            }

            myWebFetch3.cancel(true);
            Log.d("myTag", "onPostExecute evt");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("myTag", "onPreExecute evt");

        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("myTag", "doInBackground evt");
            listItems = new ArrayList<HashMap<String, Object>>();

            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                try {
                    String str_url = Config.urlDomaineEvt+"evenements.php?version="+Config.VERSION_API+"&limit="+Config.LIMIT_EVENT;
                    if (debutLimit!=0) {
                        str_url = Config.urlDomaineEvt+"evenements.php?version="+Config.VERSION_API+"&limit="+Config.LIMIT_EVENT+"&page="+debutLimit;
                    }

                    if (Config.flagRechercheEvt==1) {
                        str_url = Config.urlDomaineEvt+"evenements.php?version="+Config.VERSION_API;

                        if (Config.str_titre_evt.length()>0) {
                            str_url = str_url + "&title=" + Config.str_titre_evt;
                        }

                        str_url = str_url + "&date=" + Config.str_date_evt;

                        Config.flagRechercheEvt = 0;
                    }

                    Log.d("myTag", str_url);

                    URL url = new URL(str_url);
                    URLConnection connection = url.openConnection();

                    Document doc = parseXML(connection.getInputStream());

                    NodeList descNodes = doc.getElementsByTagName("evenement");
                    NodeList listChamps;

                    ContentValues myValue = new ContentValues();

                    //Config.flagRechercheEvt

                    for(int i=0; i<descNodes.getLength();i++)
                    {
                        HashMap<String, Object> mapping = new HashMap<String, Object>();
                        Node courant = descNodes.item(i);
                        NodeList listNode = courant.getChildNodes();
                        int flagOk = 1;

                        for(int j=0; j<listNode.getLength();j++) {

                            /*
                            if (checkModeRecherche()==1) {
                               if (listNode.item(j).getNodeName().equals("titre")) {
                                    if (listNode.item(j).getTextContent().contains(Config.str_titre_evt)) {
                                        flagOk = 1;
                                    } else {
                                        flagOk = 0;
                                    }
                                }
                            }
                            */

                           // if (flagOk==1) {
                                if (listNode.item(j).getNodeName().equals("titre")) {
                                    mapping.put("titre", listNode.item(j).getTextContent());
                                } else if (listNode.item(j).getNodeName().equals("visuel")) {
                                    mapping.put("visuel", listNode.item(j).getTextContent());
                                } else if (listNode.item(j).getNodeName().equals("accroche_detaillee")) {
                                    mapping.put("description", listNode.item(j).getTextContent());
                                } else if (listNode.item(j).getNodeName().equals("tetiaire")) {
                                    mapping.put("tetiaire", listNode.item(j).getTextContent());
                                } else if (listNode.item(j).getNodeName().equals("tetiaire_hd")) {
                                    mapping.put("tetiaire_hd", listNode.item(j).getTextContent());
                                } else if (listNode.item(j).getNodeName().equals("accroche_date_lieu")) {
                                    mapping.put("accroche", listNode.item(j).getTextContent());
                                } else if (listNode.item(j).getNodeName().equals("type_principal")) {
                                    mapping.put("type_principal", listNode.item(j).getTextContent());
                                } else if (listNode.item(j).getNodeName().equals("equipement")) {
                                    mapping.put("equipement", listNode.item(j).getTextContent());
                                } else if (listNode.item(j).getNodeName().equals("evenement_url")) {
                                    mapping.put("evenement_url", listNode.item(j).getTextContent());
                                } else if (listNode.item(j).getNodeName().equals("xml_id")) {
                                    mapping.put("xml_id", listNode.item(j).getTextContent());
                                }

                            //}
                        }
//                        if (flagOk==1) {
                            listItems.add(mapping);
  //                      }
                    }

                } catch (MalformedURLException e) {

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                Config.str_titre_evt = "";
                Config.date_evt      = null;


            } catch (Exception e) {
                //	Toast.makeText(Config.myResVehicule,"erreur : " + e.toString(), Toast.LENGTH_LONG).show();
            }

            Log.d("myTag", "doInBackground evt termine");

            mSchedule3 = new SimpleAdapter(Config.myHome.getBaseContext(), listItems, R.layout.itemevt,
                    new String[]{"titre",  "accroche", "type_principal", "visuel"},
                    new int[]{R.id.titreEvtListe,  R.id.accroche, R.id.type, R.id.imgactualite});

            mSchedule3.setViewBinder(new MyViewBinderActu());

            return null;

        }
    }
}
