package air.com.c2is.villedelyon;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;



import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

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
public class FragmentDetailActualite extends android.support.v4.app.FragmentActivity {
    private DataBaseHelper myDbHelper;
    public ArrayList<HashMap<String, Object>> listItems;
    public static GoogleAnalytics analytics;
    public static Tracker tracker;
    public WebView myTexte;
    public ImageButton myAddFavoris;
    public int id_favoris;


    public FragmentDetailActualite() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
        Config.majNbeFav((TextView) findViewById(R.id.txt_nbe_favoris), this.getBaseContext());
        Config.showAlertNotif(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        analytics = GoogleAnalytics.getInstance(Config.myHome.getBaseContext());
        analytics.setLocalDispatchPeriod(1800);
        tracker = analytics.newTracker(getResources().getString(R.string.google_analytics_id));
        tracker.setScreenName("/Fiche detail actualite");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_detail_actualite);

        Typeface myTypeface = Typeface.createFromAsset(Config.myHome.getAssets(), "Oswald-Regular.ttf");

        TextView titre = (TextView) findViewById(R.id.titre);
        titre.setTypeface(myTypeface);
        if (Config.flagEvtFromFav==1) {
            titre.setText(getResources().getString(R.string.titreFavoris));
        }else {
            titre.setText(getResources().getString(R.string.libHomeBt2));
        }

        ImageButton myBtMenu = (ImageButton) findViewById(R.id.bt_menu);
        ImageView myLogo     = (ImageView) findViewById(R.id.logo);
        // ACTION DES BOUTONS DE LA HOME
        myLogo.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        startActivity(new Intent(FragmentDetailActualite.this, MainActivity.class));
                    }
                }
        );
        // ACTION DES BOUTONS DE LA HOME
        myBtMenu.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        startActivity(new Intent(FragmentDetailActualite.this, MainActivity.class));
                    }
                }
        );

        TextView myTitre    = (TextView) findViewById(R.id.titreActualite);
        myTitre.setTypeface(myTypeface);
        myTitre.setText(Config.titreActu);

        myTexte  = (WebView) findViewById(R.id.description);
        WebSettings settings = myTexte.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        myTexte.setBackgroundColor(Color.TRANSPARENT);

        // lancement du chargement HTTP
        myAsyncTask2 myWebFetch = new myAsyncTask2();
        myWebFetch.execute();


        myDbHelper = new DataBaseHelper(Config.myHome.getBaseContext());
        try {
            myDbHelper.openDataBase();
        }catch(SQLException sqle){
            throw sqle;
        }

        ImageButton myBtParam       = (ImageButton) findViewById(R.id.bt_param);
        ImageButton myBtFavoris     = (ImageButton) findViewById(R.id.bt_favoris);
        myAddFavoris                = (ImageButton) findViewById(R.id.btFavoris);

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



        id_favoris = myDbHelper.checkFavorisActu(Config.urlActu);

        if (id_favoris!=0) {
            myAddFavoris.setImageDrawable(getResources().getDrawable(R.drawable.bt_favoris_on));
        }

        myBtParam.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(FragmentDetailActualite.this, Parametre.class);
                        startActivityForResult(intent, 2);
                    }
                }
        );
        myBtFavoris.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(FragmentDetailActualite.this, favoris.class);
                        startActivityForResult(intent, 2);

                    }
                }
        );


        Config.majNbeFav((TextView) findViewById(R.id.txt_nbe_favoris), this.getBaseContext());
    }


    public void addToFavoris() {
        ContentValues myValue = new ContentValues();

        myValue.put("libelle"        , Config.titreActu);
        myValue.put("type"           , "1");
        myValue.put("url"            , Config.urlActu);
        myValue.put("xml_equipement" , "");


        id_favoris = (int) myDbHelper.insertFavorisActu(myValue);
    }


    class myAsyncTask2 extends AsyncTask<Void, Void, Void> {
        Document doc;

        myAsyncTask2()    {

        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            try {
                String retour   = doc.outerHtml().toString();
                int dep         = retour.indexOf("alaune_detail_chapo");
                int fin         = retour.indexOf("milieu_colonne_droite");

                retour = retour.substring(dep+21,fin);

                myTexte.loadDataWithBaseURL(null, "<head><base href='http://www.lyon.fr/' target='_blank'></head>"+retour.replace("Et aussi...","<!--"), "text/html", "UTF-8", null);
            } catch (Exception e) {
                myTexte.loadDataWithBaseURL(null, "<br>Problème de connexion à lyon.fr.", "text/html", "UTF-8", null);

            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("myTag", "je onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                URL url                  = new URL(Config.urlActu);
                URLConnection connection = url.openConnection();

                doc = Jsoup.connect(Config.urlActu).get();
                Elements newsHeadlines = doc.select("#mp-itn b a");

                return null;

            } catch (Exception e) {
                Log.d("myTag", "erreur:" + e.toString());
            }

            return null;
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
                        //                            stopReveil();
                        //                          killAlarme();

                        Config.flagDirectSavoir = 1;

                        Intent intent = new Intent(FragmentDetailActualite.this, Reveil.class);
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

}
