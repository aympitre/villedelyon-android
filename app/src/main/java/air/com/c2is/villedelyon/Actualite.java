package air.com.c2is.villedelyon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ImageButton;

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
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class Actualite extends Activity {

    private DataBaseHelper myDbHelper;
    public  ListView       mylistview;
    public LinearLayout    myChargement;
    public ArrayList<HashMap<String, Object>> listItems;
    public ImageButton myPub;
    public String urlPub = "";
    public myAsyncTask2 myWebFetch;
    public int flag_load_fini = 0;

    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);
        tracker = analytics.newTracker(getResources().getString(R.string.google_analytics_id));
        tracker.setScreenName("/Actualite");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualite);

        FacebookSdk.sdkInitialize(getApplicationContext());

        Config.myActu      = this;
        Config.myActualite = this;
        mylistview 	= (ListView) this.findViewById(R.id.mylistview);
        myChargement= (LinearLayout) this.findViewById(R.id.myChargement);
        myPub       = (ImageButton) this.findViewById(R.id.myPub);


        flag_load_fini = 0;
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "Oswald-Regular.ttf");
        TextView myChargementText   = (TextView) findViewById(R.id.myChargementText);
        myChargementText.setTypeface(myTypeface);

        TextView myTitre    = (TextView) findViewById(R.id.titre);

        myTitre.setTypeface(myTypeface);

        ImageButton myBtMenu = (ImageButton) findViewById(R.id.bt_menu);

        ImageView myLogo     = (ImageView) findViewById(R.id.logo);
        // ACTION DES BOUTONS DE LA HOME
        myLogo.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (flag_load_fini==1) {
                            startActivity(new Intent(Actualite.this, MainActivity.class));
                        }
                    }
                }
        );

        // ACTION DES BOUTONS DE LA HOME
        myBtMenu.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (flag_load_fini == 1) {
                            startActivity(new Intent(Actualite.this, MainActivity.class));
                        }
                    }
                }
        );

        ImageButton myBtParam   = (ImageButton) findViewById(R.id.bt_param);
        ImageButton myBtFavoris = (ImageButton) findViewById(R.id.bt_favoris);
        myBtParam.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(Actualite.this, Parametre.class);
                        startActivityForResult(intent, 2);
                  }
                }
        );
        myBtFavoris.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(Actualite.this, favoris.class);
                        startActivityForResult(intent, 2);
                    }
                }
        );

        listItems = new ArrayList<HashMap<String, Object>>();

        mylistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Config.titreActu    = listItems.get(position).get("titreActu").toString();
                Config.urlActu      = listItems.get(position).get("url_actu").toString();

                Intent intent = new Intent(Actualite.this, FragmentDetailActualite.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, 0);
            }
        });

        myWebFetch = new myAsyncTask2();
        myWebFetch.execute();

        myPub.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (urlPub.length() > 0) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlPub));
                            startActivity(browserIntent);
                        }
                    }
                }
        );

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

    public void goReveilOn() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_notif)
                .setTitle("Réveil")
                .setMessage("Appuyer pour couper la sonnerie.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Config.mp.stop();
                        //                            stopReveil();
                        //                          killAlarme();

                        Config.flagDirectSavoir = 1;

                        Intent intent = new Intent(Actualite.this, Reveil.class);
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

            SimpleAdapter mSchedule = new SimpleAdapter (Config.myHome.getBaseContext(), listItems, R.layout.itemactualite,
                    new String[] {"titreActu","texte","image"},
                    new int[] {R.id.titreActualite, R.id.description, R.id.imgactualite});

            mSchedule.setViewBinder(new MyViewBinderActu());

            mylistview.setAdapter(mSchedule);

            myChargement.setVisibility(View.GONE);
            mylistview.setVisibility(View.VISIBLE);

            myPub.setVisibility(View.VISIBLE);
            flag_load_fini = 1;
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
                    URL url = new URL("http://appvilledelyon.c2is.fr/actualites.php?limit="+Config.LIMIT_ACTU+"&version="+Config.VERSION_API);
                    URLConnection connection = url.openConnection();

                    Document doc = parseXML(connection.getInputStream());

                    NodeList descNodes = doc.getElementsByTagName("actualite");
                    NodeList listChamps;

                    ContentValues myValue = new ContentValues();

                    for(int i=0; i<descNodes.getLength();i++)
                    {
                        HashMap<String, Object> mapping = new HashMap<String, Object>();
                        Node courant = descNodes.item(i);
                        NodeList listNode = courant.getChildNodes();
                        for(int j=0; j<listNode.getLength();j++) {
                            if(listNode.item(j).getNodeName().equals("titre")){
                                mapping.put("titreActu", listNode.item(j).getTextContent());

                            }else if(listNode.item(j).getNodeName().equals("accroche_detaillee")){
                                mapping.put("texte", listNode.item(j).getTextContent());

                            }else if(listNode.item(j).getNodeName().equals("visuel")){
                                mapping.put("image", listNode.item(j).getTextContent());

                            }else if(listNode.item(j).getNodeName().equals("xml_id")){
                                mapping.put("xml_id",listNode.item(j).getTextContent());

                            }else if(listNode.item(j).getNodeName().equals("url_actu")){
                                mapping.put("url_actu",listNode.item(j).getTextContent());

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


            try {
                URL url = new URL("http://appvilledelyon.c2is.fr/bannieres.php");
                URLConnection connection = url.openConnection();

                Document doc = parseXML(connection.getInputStream());

                NodeList descNodes = doc.getElementsByTagName("banniere");
                NodeList listChamps;

                ContentValues myValue = new ContentValues();

                for(int i=0; i<descNodes.getLength();i++)
                {
                    HashMap<String, Object> mapping = new HashMap<String, Object>();
                    Node courant = descNodes.item(i);
                    NodeList listNode = courant.getChildNodes();
                    for(int j=0; j<listNode.getLength();j++) {

                        if(listNode.item(j).getNodeName().equals("image")){
                            String urlImg = listNode.item(j).getTextContent();

                            BitmapDownloaderTask task = new BitmapDownloaderTask(myPub);
                            task.execute(urlImg);
                            myPub.setTag(urlImg);

                        }else if(listNode.item(j).getNodeName().equals("url")){
                            urlPub = listNode.item(j).getTextContent();

                        }
                    }
                }

            } catch (MalformedURLException e) {
                Log.d("myTag", "This is my MalformedURLException");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("myTag", "exep : "+e.toString());
                e.printStackTrace();
            }


            return null;
        }
    }

}

