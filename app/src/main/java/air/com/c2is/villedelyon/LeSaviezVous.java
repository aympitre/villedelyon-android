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


public class LeSaviezVous extends Activity {
    public ListView mylistview;
    public LinearLayout myChargement;
    public ArrayList<HashMap<String, Object>> listItems;
    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);
        tracker = analytics.newTracker(getResources().getString(R.string.google_analytics_id));
        tracker.setScreenName("/Le saviez vous");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_le_saviez_vous);

        Config.mySaviezVous = this;

        FacebookSdk.sdkInitialize(getApplicationContext());
        Config.myActu = this;

        listItems   = new ArrayList<HashMap<String, Object>>();

        myChargement=  (LinearLayout) this.findViewById(R.id.myChargement);
        mylistview 	=  (ListView) this.findViewById(R.id.mylistview);

        Typeface myTypeface         = Typeface.createFromAsset(getAssets(), "Oswald-Regular.ttf");
        TextView myTitre            = (TextView) findViewById(R.id.titre);

        TextView myChargementText   = (TextView) findViewById(R.id.myChargementText);
        myChargementText.setTypeface(myTypeface);

        myTitre.setTypeface(myTypeface);

        TextView lbl_bt_savoir1 = (TextView) findViewById(R.id.lbl_bt_savoir1);
        lbl_bt_savoir1.setTypeface(myTypeface);
        TextView lbl_bt_savoir2 = (TextView) findViewById(R.id.lbl_bt_savoir2);
        lbl_bt_savoir2.setTypeface(myTypeface);

        LinearLayout myBtGuichet = (LinearLayout) findViewById(R.id.bt_guichet);

        myBtGuichet.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.guichetdusavoir.org"));
                        startActivity(browserIntent);
                    }
                }
        );

        // ACTION DES BOUTONS DU HEADER
        ImageView myLogo = (ImageView) findViewById(R.id.logo);

        myLogo.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        startActivity(new Intent(LeSaviezVous.this, MainActivity.class));
                    }
                }
        );
        ImageButton myBtFavoris = (ImageButton) findViewById(R.id.bt_favoris);
        myBtFavoris.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(LeSaviezVous.this, favoris.class);
                        startActivityForResult(intent, 2);
                    }
                }
        );
        ImageButton myBtParam = (ImageButton) findViewById(R.id.bt_param);
        myBtParam.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(LeSaviezVous.this, Parametre.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent, 0);

                    }
                }
        );
        ImageButton myBtMenu = (ImageButton) findViewById(R.id.bt_menu);
        myBtMenu.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        startActivity(new Intent(LeSaviezVous.this, MainActivity.class));
                    }
                }
        );

        // lancement du chargement HTTP
        myAsyncTask2 myWebFetch = new myAsyncTask2();
        myWebFetch.execute();

        mylistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Config.titre_savoir = listItems.get(position).get("titre").toString();
                Config.texte_savoir = listItems.get(position).get("texte").toString();

                Intent intent = new Intent(LeSaviezVous.this, LeSaviezVousDetail.class);
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

        myAsyncTask2()    {

        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            SimpleAdapter mSchedule = new SimpleAdapter (Config.myHome.getBaseContext(), listItems, R.layout.itemdefault,
                    new String[] {"titre"},
                    new int[] {R.id.titre});

            mSchedule.setViewBinder(new MyViewBinderDefault());

            mylistview.setAdapter(mSchedule);
            myChargement.setVisibility(View.GONE);
            mylistview.setVisibility(View.VISIBLE);
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
                    URL url = new URL("http://appvilledelyon.c2is.fr/savoir.php?limit=-1&version="+Config.VERSION_API);
                    URLConnection connection = url.openConnection();

                    Document doc = parseXML(connection.getInputStream());

                    NodeList descNodes = doc.getElementsByTagName("savoir");
                    NodeList listChamps;

                    ContentValues myValue = new ContentValues();

                    for(int i=0; i<descNodes.getLength();i++)
                    {
                        HashMap<String, Object> mapping = new HashMap<String, Object>();
                        Node courant = descNodes.item(i);
                        NodeList listNode = courant.getChildNodes();
                        for(int j=0; j<listNode.getLength();j++) {
                            if(listNode.item(j).getNodeName().equals("title")){
                                mapping.put("titre", listNode.item(j).getTextContent());

                            }else if(listNode.item(j).getNodeName().equals("content")){
                                mapping.put("texte", listNode.item(j).getTextContent());
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

                        Intent intent = new Intent(LeSaviezVous.this, Reveil.class);
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
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        AppEventsLogger.activateApp(this);
        Config.myActu  =this;
        Config.majNbeFav((TextView) findViewById(R.id.txt_nbe_favoris), this.getBaseContext());
        Config.showAlertNotif(this);
    }
}
