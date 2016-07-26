package air.com.c2is.villedelyon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class Reveil extends Activity {
    public ArrayList<HashMap<String, Object>> listItems;

    public String strHeure;
    public SharedPreferences sharedPref;
    public TextView  myTitreReveil;
    public TextView  myMeteo;
    public TextView  myTemperature;
    public ImageView myImgMeteo;
    public WebView  mySavoir;
    public LinearLayout myLayTime;
    public LinearLayout myLayReveil;
    public LinearLayout myLaySavoir;
    public Button       myBtValider;
    public TimePicker   myTime;
    public Button       btHorloge;
    public Button       btAlarme;
    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);
        tracker = analytics.newTracker(getResources().getString(R.string.google_analytics_id));
        tracker.setScreenName("/Reveil");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());

        Config.myReveil = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reveil);
        FacebookSdk.sdkInitialize(getApplicationContext());
        listItems   = new ArrayList<HashMap<String, Object>>();

        Typeface myTypeface     = Typeface.createFromAsset(getAssets(), "Oswald-Regular.ttf");
        final TextView myTitre  = (TextView) findViewById(R.id.titreDate);
        myTitre.setTypeface(myTypeface);

        btAlarme     = (Button) findViewById(R.id.btAlarme);
        btAlarme.setTypeface(myTypeface);


        btAlarme.setBackgroundResource(R.drawable.clock_bouton_stop);
        btAlarme.setText(getResources().getString(R.string.libBtAlarmeOff));



        btHorloge     = (Button) findViewById(R.id.btHorloge);
        btHorloge.setTypeface(myTypeface);

        myBtValider = (Button) findViewById(R.id.btValider);
        myBtValider.setTypeface(myTypeface);

        myTime        = (TimePicker) findViewById(R.id.dpAlaram);
        myTime.setBackgroundColor(Color.WHITE);
        myTime.setIs24HourView(true);

        Calendar actuTemp  = Calendar.getInstance();
        myTime.setCurrentHour(actuTemp.get(Calendar.HOUR_OF_DAY));
        myTime.setCurrentMinute(actuTemp.get(Calendar.MINUTE));

        myImgMeteo     = (ImageView) findViewById(R.id.imgMeteo);

        myMeteo        = (TextView) findViewById(R.id.libelleMeteo);
        myTemperature  = (TextView) findViewById(R.id.libelleTemperature);
        myTemperature.setTypeface(myTypeface);
        myMeteo.setTypeface(myTypeface);

        myTitreReveil   = (TextView) findViewById(R.id.titreSavoir);
        myTitreReveil.setTypeface(myTypeface);

        mySavoir        = (WebView) findViewById(R.id.texteSavoir);
        myLayTime       = (LinearLayout) findViewById(R.id.LayTime);
        myLayReveil     = (LinearLayout) findViewById(R.id.LayReveil);
        myLaySavoir     = (LinearLayout) findViewById(R.id.LaySavoirReveil);

        WebSettings settings = mySavoir.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        mySavoir.setBackgroundColor(getResources().getColor(R.color.transparent));

        ImageButton btFermer        = (ImageButton) findViewById(R.id.btFermer);
        btFermer.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        showReveil();
                    }
                }
        );
        ImageButton btFermer2        = (ImageButton) findViewById(R.id.btFermer2);
        btFermer2.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        showReveil();
                    }
                }
        );

        myBtValider.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Calendar c = Calendar.getInstance();

                        int mYear = c.get(Calendar.YEAR);
                        int mMonth = c.get(Calendar.MONTH);
                        int mDay = c.get(Calendar.DAY_OF_MONTH);

                        c = new GregorianCalendar();  // This creates a Calendar instance with the current time

                        c.set(Calendar.HOUR, myTime.getCurrentHour());
                        c.set(Calendar.MINUTE, myTime.getCurrentMinute());
                        c.getTimeInMillis();

                        SimpleDateFormat format1 = new SimpleDateFormat("hh   :   mm");
                        String formatted = format1.format(c.getTime());
                        formatted = myTime.getCurrentHour() + " : ";
                        if (myTime.getCurrentMinute() < 10) {
                            formatted = formatted + "0" + myTime.getCurrentMinute();
                        } else {
                            formatted = formatted + myTime.getCurrentMinute();
                        }

                        if (myTime.getCurrentHour() < 10) {
                            formatted = "0" + formatted;
                        }

                        Calendar actu = Calendar.getInstance();
                        //Log.d("myTag", "pulco : " + actu.getTimeInMillis());

                        actu.set(Calendar.MILLISECOND, 0);

                        Calendar actu2 = Calendar.getInstance();

                        actu2.set(Calendar.YEAR, mYear);
                        actu2.set(Calendar.MONTH, mMonth);
                        actu2.set(Calendar.DAY_OF_MONTH, mDay);
                        actu2.set(Calendar.HOUR_OF_DAY, myTime.getCurrentHour());
                        actu2.set(Calendar.MINUTE, myTime.getCurrentMinute());
                        actu2.set(Calendar.MILLISECOND, 0);

                        Config.reveilDiff = actu2.getTimeInMillis() - actu.getTimeInMillis();
                        Config.reveilDiff = Math.abs(Config.reveilDiff);

//                        Config.reveilDiff = 1000;

                        btHorloge.setText(formatted);
                        strHeure = formatted;

                        // Save de l'heure de reveil
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putLong("heure_reveil", actu2.getTimeInMillis() - actu.getTimeInMillis());
                        editor.commit();

                        showReveil();

                        if (Config.flagAlarm == 1) {
                            Config.myHome.goAlarme();
                            saveHeure();
                        }
                    }
                }
        );

        btHorloge.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        showTime();
                    }
                }
        );

        btAlarme.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (Config.flagAlarm == 0) {
                            if (Config.reveilDiff!=0) {
                                ((Button) v).setText(getResources().getString(R.string.libBtAlarmeOff));

                                if (Config.checkDevice() > 1) {
                                    ((Button) v).setBackgroundResource(R.drawable.clock_bouton_stop2);
                                } else {
                                    ((Button) v).setBackgroundResource(R.drawable.clock_bouton_stop);
                                }

                                Config.flagAlarm = 1;
                                Config.myHome.goAlarme();
                                saveHeure();
                            }

                        } else {
                            ((Button) v).setText(getResources().getString(R.string.libBtAlarmeOn));
                            if (Config.checkDevice()>1) {
                                ((Button) v).setBackgroundResource(R.drawable.clock_bouton_alarme2);
                            }else {
                                ((Button) v).setBackgroundResource(R.drawable.clock_bouton_alarme);
                            }
                            Config.flagAlarm = 0;

                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putLong("flag_alarm", 0);
                            editor.commit();

                            Config.myHome.killAlarme();
                        }
                    }
                }
        );

        // ACTION DES BOUTONS DU HEADER
        ImageView myLogo = (ImageView) findViewById(R.id.logo);

        myLogo.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        startActivity(new Intent(Reveil.this, MainActivity.class));
                    }
                }
        );
        ImageButton myBtFavoris = (ImageButton) findViewById(R.id.bt_favoris);
        myBtFavoris.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(Reveil.this, favoris.class);
                        startActivityForResult(intent, 2);
                    }
                }
        );
        ImageButton myBtParam = (ImageButton) findViewById(R.id.bt_param);
        myBtParam.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(Reveil.this, Parametre.class);
                        startActivityForResult(intent, 2);
                    }
                }
        );

        if (Config.flagDirectSavoir==1) {
            Config.flagDirectSavoir = 0;
            showSavoir();
        }

        Calendar c           = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String strDate       = sdf.format(c.getTime());
        myTitre.setText(strDate);

        // lancement du chargement HTTP
        myAsyncTask2 myWebFetch = new myAsyncTask2();
        myWebFetch.execute();

        if (Config.FLAG_REVEIL_SAVOIR==1) {
            Config.FLAG_REVEIL_SAVOIR = 0;
            Config.myHome.stopReveil();
            showSavoir();
        }

        Config.majNbeFav((TextView) findViewById(R.id.txt_nbe_favoris), this.getBaseContext());

        sharedPref = this.getSharedPreferences("vdl", Context.MODE_WORLD_WRITEABLE);
        long flag_actif = sharedPref.getLong("flag_alarm", 0);

        if (flag_actif==1) {
            Config.flagAlarm = 1;
            btAlarme.setText(getResources().getString(R.string.libBtAlarmeOff));
            if (Config.checkDevice() > 1) {
                btAlarme.setBackgroundResource(R.drawable.clock_bouton_stop2);
            } else {
                btAlarme.setBackgroundResource(R.drawable.clock_bouton_stop);
            }
        }else{
            Config.flagAlarm = 0;
            btAlarme.setText(getResources().getString(R.string.libBtAlarmeOn));
            if (Config.checkDevice()>1) {
                btAlarme.setBackgroundResource(R.drawable.clock_bouton_alarme2);
            }else {
                btAlarme.setBackgroundResource(R.drawable.clock_bouton_alarme);
            }
        }

        String actuHeure = sharedPref.getString("alarm_value", "00  :  00");
        strHeure         = actuHeure;

        btHorloge.setText(strHeure);

        if (Config.flagAlarm==1) {
            btAlarme.setText(getResources().getString(R.string.libBtAlarmeOff));
            if (Config.checkDevice() > 1) {
                btAlarme.setBackgroundResource(R.drawable.clock_bouton_stop2);
            } else {
                btAlarme.setBackgroundResource(R.drawable.clock_bouton_stop);
            }
        }

        // QUAND ON VIENT DE LA NOTIFICATION
        if (Config.flagOffReveil==1) {
            Config.flagOffReveil = 0;
            btAlarme.setText(getResources().getString(R.string.libBtAlarmeOn));
            if (Config.checkDevice()>1) {
                btAlarme.setBackgroundResource(R.drawable.clock_bouton_alarme2);
            }else {
                btAlarme.setBackgroundResource(R.drawable.clock_bouton_alarme);
            }
            Config.flagAlarm = 0;
        }

        testBtValider();
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

                        Intent intent = new Intent(Reveil.this, Reveil.class);
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


    public void saveHeure() {
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("alarm_value", strHeure);
        editor.putLong("flag_alarm", 1);
        editor.commit();
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
        Config.majNbeFav((TextView) findViewById(R.id.txt_nbe_favoris), this.getBaseContext());
        Config.showAlertNotif(this);

        testBtValider();
    }

    public void testBtValider() {
        if (Config.flagRelanceCompteur==1) {
            Config.flagAlarm = 1;
            btAlarme.setBackgroundResource(R.drawable.clock_bouton_stop);
            btAlarme.setText(getResources().getString(R.string.libBtAlarmeOff));

        }
    }

    public void loadSavoir() {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try {
                URL url = new URL("http://appvilledelyon.c2is.fr/savoir.php?limit=1&version="+Config.VERSION_API);
                URLConnection connection = url.openConnection();

                Document doc        = parseXML(connection.getInputStream());

                NodeList descNodes  = doc.getElementsByTagName("savoir");
                NodeList listChamps;

                ContentValues myValue = new ContentValues();

                for(int i=0; i<descNodes.getLength();i++)
                {
                    HashMap<String, Object> mapping = new HashMap<String, Object>();
                    Node courant = descNodes.item(i);
                    NodeList listNode = courant.getChildNodes();
                    for(int j=0; j<listNode.getLength();j++) {
                        if(listNode.item(j).getNodeName().equals("title")){
                            myTitreReveil.setText(listNode.item(j).getTextContent());

                        }else if(listNode.item(j).getNodeName().equals("content")){
                            mySavoir.loadDataWithBaseURL(null,"<font color='white'>"+listNode.item(j).getTextContent().toString(), "text/html", "UTF-8", null);
                        }
                    }
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
    }

    public void showSavoir() {
        myLayTime.setVisibility(View.GONE);
        myLayReveil.setVisibility(View.GONE);
        myLaySavoir.setVisibility(View.VISIBLE);

        loadSavoir();
    }

    public void showTime() {
        myLayTime.setVisibility(View.VISIBLE);
        myLayReveil.setVisibility(View.GONE);
        myLaySavoir.setVisibility(View.GONE);
    }
    public void showReveil() {
        myLayTime.setVisibility(View.GONE);
        myLayReveil.setVisibility(View.VISIBLE);
        myLaySavoir.setVisibility(View.GONE);
    }


    public void getPictoFromFlux(String p_param){
        //Log.d("myTag", "getPictoFromFlux : " + p_param);

        if (p_param.equals("pluiefaible")) {
            myMeteo.setText("PLUVIEUX");
            myImgMeteo.setImageResource(R.drawable.meteo_nuageux_pluvieux);
        }else if (p_param.equals("nuageux")) {
            myMeteo.setText("NUAGEUX");
            myImgMeteo.setImageResource(R.drawable.meteo_nuageux);
        }else if (p_param.equals("couvert")) {
            myMeteo.setText("NUAGEUX");
            myImgMeteo.setImageResource(R.drawable.meteo_nuageux);
        }else if (p_param.equals("soleil")) {
            myMeteo.setText("SOLEIL");
            myImgMeteo.setImageResource(R.drawable.meteo_soleil);
        }else if (p_param.equals("voile")) {
            myMeteo.setText("SOLEIL NUAGEUX");
            myImgMeteo.setImageResource(R.drawable.meteo_soleil_nuageux);
        }else if (p_param.equals("neifefaible")) {
            myMeteo.setText("NEIGE");
            myImgMeteo.setImageResource(R.drawable.meteo_neige);
        }else if (p_param.equals("averse")) {
            myMeteo.setText("AVERSE");
            myImgMeteo.setImageResource(R.drawable.meteo_pluvieux);
        }else if (p_param.equals("orage")) {
            myMeteo.setText("ORAGEUX");
            myImgMeteo.setImageResource(R.drawable.meteo_orageux);
        }else if (p_param.equals("pluiemoderer")) {
            myMeteo.setText("PLUIE");
            myImgMeteo.setImageResource(R.drawable.meteo_pluvieux);
        }else if (p_param.equals("pluie")) {
            myMeteo.setText("PLUIE");
            myImgMeteo.setImageResource(R.drawable.meteo_pluvieux);
        }else if (p_param.equals("brouillardgivrant")) {
            myMeteo.setText("BROUILLARD");
            myImgMeteo.setImageResource(R.drawable.meteo_brouillard);
        }else if (p_param.equals("brouillard")) {
            myMeteo.setText("BROUILLARD");
            myImgMeteo.setImageResource(R.drawable.meteo_brouillard);
        }else if (p_param.equals("neige")) {
            myMeteo.setText("NEIGE");
            myImgMeteo.setImageResource(R.drawable.meteo_neige);
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

            if (listItems.size()>0) {
                String myPicto = listItems.get(0).get("pictos_midi").toString();
                String strTemperature = listItems.get(0).get("tempe_midi").toString() + " °";

                getPictoFromFlux(myPicto);
                myTemperature.setText(strTemperature);
            }
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
                    URL url = new URL(Config.urlMeteo);
                    URLConnection connection = url.openConnection();

                    Document doc = parseXML(connection.getInputStream());

                    NodeList descNodes = doc.getElementsByTagName("item");
                    NodeList listChamps;

                    ContentValues myValue = new ContentValues();

                    for(int i=0; i<descNodes.getLength();i++)
                    {
                        HashMap<String, Object> mapping = new HashMap<String, Object>();
                        Node courant = descNodes.item(i);
                        NodeList listNode = courant.getChildNodes();
                        for(int j=0; j<listNode.getLength();j++) {
                            if(listNode.item(j).getNodeName().equals("pictos_matin")){
                                mapping.put("pictos_matin", listNode.item(j).getTextContent());

                            }else if(listNode.item(j).getNodeName().equals("tempe_matin")){
                                mapping.put("tempe_matin", listNode.item(j).getTextContent());

                            }else if(listNode.item(j).getNodeName().equals("pictos_midi")){
                                mapping.put("pictos_midi", listNode.item(j).getTextContent());

                            }else if(listNode.item(j).getNodeName().equals("tempe_midi")){
                                mapping.put("tempe_midi", listNode.item(j).getTextContent());

                            }else if(listNode.item(j).getNodeName().equals("pictos_apmidi")){
                                mapping.put("pictos_apmidi", listNode.item(j).getTextContent());

                            }else if(listNode.item(j).getNodeName().equals("tempe_apmidi")){
                                mapping.put("tempe_apmidi", listNode.item(j).getTextContent());

                            }else if(listNode.item(j).getNodeName().equals("pictos_soir")){
                                mapping.put("pictos_soir", listNode.item(j).getTextContent());

                            }else if(listNode.item(j).getNodeName().equals("tempe_soir")){
                                mapping.put("tempe_soir", listNode.item(j).getTextContent());
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
}
