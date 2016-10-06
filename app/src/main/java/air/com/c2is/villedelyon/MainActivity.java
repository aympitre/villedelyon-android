package air.com.c2is.villedelyon;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.os.SystemClock;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.PowerManager;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class MainActivity extends Activity {
    public LinearLayout layChargement;
    public LinearLayout layContenu;
    private DataBaseHelper myDbHelper;
    public int flagChargement;
    public MediaPlayer mp;
    public SharedPreferences sharedPref;
    public Button myBt1;
    PendingIntent       pi;
    BroadcastReceiver   br;
    AlarmManager        am;
    public int flagNotif;

    public static GoogleAnalytics   analytics;
    public static Tracker           tracker;

    final static private long ONE_SECOND = 1000;
    final static private long TPSALARM = ONE_SECOND * 10;
    public GoogleCloudMessaging gcm;
    private AlarmManagerBroadcastReceiver alarm;

    /** * Cette méthode permet l'enregistrement du terminal */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(Config.myActu.getApplicationContext());
                    }
                    String regId = gcm.register("228960025800");
                    msg = "Terminal enregistré, register ID=" + regId;

                    Log.d("myTag", msg);

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("notif_vdl", regId);
                    editor.commit();

                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    URL url = new URL("http://appvilledelyon.c2is.fr/register_app.php?token="+regId);
                    URLConnection connection2 = url.openConnection();


                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.d("myTag", msg);
                }
                return msg;
            }
        }.execute(null, null, null);
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getSharedPreferences(
                MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        String registrationId = prefs.getString("notif_vdl", "");
        if (registrationId.isEmpty()) {
            return "";
        }
        return registrationId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);
        tracker = analytics.newTracker(getResources().getString(R.string.google_analytics_id));
        tracker.setScreenName("/Accueil");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());

        alarm = new AlarmManagerBroadcastReceiver();

        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_main);

        final SharedPreferences prefs = getSharedPreferences("vdl", Context.MODE_PRIVATE);
        String strAlert = prefs.getString("alert_vdl", "");

        if(strAlert.equalsIgnoreCase("aymeric")) {

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

                            Intent intent = new Intent(MainActivity.this, Reveil.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivityForResult(intent, 0);

                            finish();
                        }

                    })
                    .show();


            SharedPreferences sharedPref  = getSharedPreferences("vdl", Context.MODE_WORLD_WRITEABLE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("alert_vdl", "");
            editor.commit();

        }

        Config.myActu = this;

        String regId = getRegistrationId(getApplicationContext());
        if (TextUtils.isEmpty(regId)) {
            registerInBackground();
        }

        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "Oswald-Regular.ttf");

        layChargement   = (LinearLayout) findViewById(R.id.layChargement);
        layContenu      = (LinearLayout) findViewById(R.id.layContenu);

        TextView myChargement = (TextView) findViewById(R.id.libChargement);
        myChargement.setTypeface(myTypeface);
        TextView libChargement1 = (TextView) findViewById(R.id.libChargement1);
        libChargement1.setTypeface(myTypeface);
        showChargement();

        myBt1 = (Button) findViewById(R.id.bt_infos_utiles);
        Button myBt2 = (Button) findViewById(R.id.bt_actualites);
        Button myBt3 = (Button) findViewById(R.id.bt_detendre);
        Button myBt4 = (Button) findViewById(R.id.bt_se_deplacer);
        Button myBt5 = (Button) findViewById(R.id.bt_agenda);
        Button myBt6 = (Button) findViewById(R.id.bt_demarche);

        Button myBt9 = (Button) findViewById(R.id.bt_favoris_home);
        Button myBt10 = (Button) findViewById(R.id.bt_quiz);

        myBt1.setTypeface(myTypeface);
        myBt2.setTypeface(myTypeface);
        myBt3.setTypeface(myTypeface);
        myBt4.setTypeface(myTypeface);
        myBt5.setTypeface(myTypeface);
        myBt6.setTypeface(myTypeface);
        myBt9.setTypeface(myTypeface);
        myBt10.setTypeface(myTypeface);

        Button myBt7 = (Button) findViewById(R.id.bt_saviez);
        Button myBt8 = (Button) findViewById(R.id.bt_reveil);

        myBt7.setTypeface(myTypeface);
        myBt8.setTypeface(myTypeface);

        ImageButton myBtParam   = (ImageButton) findViewById(R.id.bt_param);

        ImageButton myBtFavoris = (ImageButton) findViewById(R.id.bt_favoris);
        myBtParam.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, Parametre.class);
                        startActivityForResult(intent, 2);
                    }
                }
        );
        myBt9.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, favoris.class);
                        startActivityForResult(intent, 2);

                    }
                }
        );

        // ACTION DES BOUTONS DE LA HOME
        myBt1.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        goInfo();
                    }
                }
        );
        myBt2.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        goActualite();
                    }
                }
        );
        myBt3.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        goDetendre();
                    }
                }
        );
        myBt4.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        goDeplacer();
                    }
                }
        );
        myBt5.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        goAgenda();
                    }
                }
        );
        myBt6.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        goDemarche();
                    }
                }
        );
        myBt7.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        goSaviez();
                    }
                }
        );
        myBt8.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        goReveil();
                    }
                }
        );

        Config.myHome = this;
        setup();

        myDbHelper = new DataBaseHelper(this.getBaseContext());

        try {
            myDbHelper.createDataBase();
            Log.d("myTag", "creation bdd ok");
        } catch (IOException ioe) {
            Log.d("myTag", "creation bdd KO");
            throw new Error("Unable to create database");
        }

       // hideChargement();

        checkMajBdd();


        try {
            myDbHelper.openDataBase();
            Log.d("myTag", "ouverture bdd ok");
        }catch(SQLException sqle){
            Log.d("myTag", "ouverture bdd KO");
            throw sqle;
        }

        Config.majNbeFav((TextView) findViewById(R.id.txt_nbe_favoris), this.getBaseContext());

        sharedPref             = this.getSharedPreferences("vdl", Context.MODE_WORLD_WRITEABLE);
        Config.flag_tri_geoloc = sharedPref.getInt("flag_geoloc", 1);

        resetRetour();
    }

    public void resetRetour() {
        Config.flagDemarche     = 0;
        Config.flagForceRetour  = 0;
        Config.flagBisRetour    = 0;
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

                        Intent intent = new Intent(MainActivity.this, Reveil.class);
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

    public void checkMajBdd() {
        final SharedPreferences prefs = getSharedPreferences(
                MainActivity.class.getSimpleName(), Context.MODE_WORLD_WRITEABLE);
        String tempo = prefs.getString("time_maj_vdl", "");

        Calendar actu   = Calendar.getInstance();
        long actu_long  = actu.getTimeInMillis();
        String str_temp = "" + actu_long;

        if (tempo.isEmpty()) {
            goMajDonnee(str_temp, prefs);

        }else{
            /*
            long time = Long.valueOf(tempo).longValue() ;

            int semaine = (7*24*60*60*1000);
            if ((actu_long - time)>semaine) {
            */
                goMajDonnee(str_temp, prefs);
            /*}else{
                hideChargement();
            }*/
        }

        hideChargement();
    }

    public void goMajDonnee(String p_time, SharedPreferences p_pref) {
        SharedPreferences.Editor editor = p_pref.edit();
        editor.putString("time_maj_vdl", p_time);
        editor.commit();

        myAsyncTask2 myWebFetch = new myAsyncTask2();
        myWebFetch.flag = 2;
        myWebFetch.execute();
    }


    public void goAlarme() {
        Context context = this.getApplicationContext();
        if(alarm != null){
           // alarm.setOnetimeTimer(context);
            alarm.SetAlarm(context);

        }else{
            Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
        }

    }

    public void killAlarme() {
        alarm.CancelAlarm(MainActivity.this);
    }

    public void stopReveil() {
        try {
            alarm.stopReveil();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playReveil() {
        flagNotif = 1;

        try {
/*
            mp = new MediaPlayer();
            int resId = -1;

            AssetFileDescriptor descriptor = getAssets().openFd("reveil.mp3");
            mp.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            mp.prepare();
            mp.setVolume(1f, 1f);
            mp.setLooping(true);
            mp.start();
*/

            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_notif)
                    .setTitle("Réveil")
                    .setMessage("Appuyer pour couper la sonnerie.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            stopReveil();

                            Intent intent = new Intent(MainActivity.this, Reveil.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivityForResult(intent, 0);

                            finish();
                        }

                    })
                    .show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setup() {
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent i) {
                notifyUser();
                playReveil();
                Config.FLAG_REVEIL_SAVOIR = 1;

                goReveil();
            }
        };
        registerReceiver(br, new IntentFilter("com.authorwjf.wakeywakey"));
        /*
        pi = PendingIntent.getBroadcast( this, 0, new Intent("com.authorwjf.wakeywakey"),
                0 );
        am = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));
        */
    }


    public void notifyUser() {
        NotificationManager notificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(MainActivity.this, Reveil.class);
        Bundle bundle = new Bundle();
        bundle.putString("buzz", "buzz");
        intent.putExtras(bundle);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification(R.drawable.ic_notif, "Ville de Lyon : Réveil", System.currentTimeMillis());

        notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND;

        notification.setLatestEventInfo(this, "Ville de Lyon", "Réveil", contentIntent);
        //10 is a random number I chose to act as the id for this notification
        notification.number += 1;
        notificationManager.notify(10, notification);




    }

    public void goInfo() {
        if (flagChargement==0) {
            Config.flagDirectMarche     = 0;
            Config.flagForceRetour      = 0;
            Config.str_demarche         = "";
            Config.MENU_ACTIVITE        = 1;
            Config.CODE_DE_MON_ACTIVITE = 1;
            Intent intent = new Intent(MainActivity.this, ListType.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivityForResult(intent, 0);
            //overridePendingTransition(0,0); //0 for no animation
        }
    }
    public void goVie() {
        if (flagChargement==0) {
            Config.flagDirectMarche     = 0;
            Config.flagForceRetour      = 0;
            Config.str_demarche         = "";
            Config.MENU_ACTIVITE        = 2;
            Config.CODE_DE_MON_ACTIVITE = 1;
            Intent intent = new Intent(MainActivity.this, ListType.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivityForResult(intent, 0);
            //overridePendingTransition(0,0); //0 for no animation
        }
    }
    public void goLoisir() {
        if (flagChargement==0) {
            Config.codeInterne          = 3;
            Config.flagDirectMarche     = 0;
            Config.flagForceRetour      = 0;
            Config.str_demarche         = "";
            Config.MENU_ACTIVITE        = 3;
            Config.CODE_DE_MON_ACTIVITE = 1;

            Intent intent = new Intent(MainActivity.this, ListType.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivityForResult(intent, 0);
            //overridePendingTransition(0,0); //0 for no animation
        }
    }
    public void goActualite() {
        if (flagChargement==0) {
            Config.str_demarche         = "";
            Config.flagForceRetour      = 0;
            Config.MENU_ACTIVITE        = 1;
            Config.CODE_DE_MON_ACTIVITE = 2;
            Intent intent = new Intent(MainActivity.this, Actualite.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivityForResult(intent, 0);
        }
    }
    public void goDetendre() {
        if (flagChargement==0) {
            Config.str_demarche         = "";
            Config.flagForceRetour      = 0;
            Config.MENU_ACTIVITE        = 1;
            Config.CODE_DE_MON_ACTIVITE = 3;
            Intent intent = new Intent(MainActivity.this, ListType.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivityForResult(intent, 0);

        }
    }
    public void goDeplacer() {
        if (flagChargement==0) {
            Config.str_demarche         = "";
            Config.flagForceRetour      = 0;
            Config.MENU_ACTIVITE        = 1;
            Config.CODE_DE_MON_ACTIVITE = 4;
            Intent intent = new Intent(MainActivity.this, SeDeplacer.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivityForResult(intent, 0);

        }
    }
    public void goAgenda() {
        if (flagChargement==0) {
            Config.str_demarche         = "";
            Config.flagForceRetour      = 0;
            Config.MENU_ACTIVITE        = 1;
            Config.CODE_DE_MON_ACTIVITE = 5;
            Intent intent = new Intent(MainActivity.this, ListType.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivityForResult(intent, 0);
        }
    }
    public void goDemarche() {
        if (flagChargement==0) {
            Config.MENU_ACTIVITE        = 1;
            Config.flagForceRetour      = 0;
            Config.CODE_DE_MON_ACTIVITE = 6;
            Config.sql_sous_type        = getResources().getString(R.string.sqlType6_1);

            Intent intent = new Intent(MainActivity.this, ListType.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivityForResult(intent, 0);

        }
    }
    public void goSaviez() {
        if (flagChargement==0) {
            Config.CODE_DE_MON_ACTIVITE = 7;
            Config.flagForceRetour      = 0;
            Intent intent = new Intent(MainActivity.this, LeSaviezVous.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivityForResult(intent, 0);

        }
    }
    public void goReveil() {
        if (flagChargement==0) {
            Config.CODE_DE_MON_ACTIVITE = 8;
            Config.flagForceRetour      = 0;
            Intent intent = new Intent(MainActivity.this, Reveil.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivityForResult(intent, 0);

        }
    }

    public void hideChargement() {
        flagChargement = 0;
        layChargement.setVisibility(View.GONE);
        layContenu.setVisibility(View.VISIBLE);
    }
    public void showChargement () {
        flagChargement = 1;
        layChargement.setVisibility(View.VISIBLE);
        layContenu.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_notif)
                .setTitle("Fermeture application")
                .setMessage("Etes vous-sur de vouloir quitter l'application Ville de Lyon ?")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("Non", null)
                .show();
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

            if (this.flag<6) { //9
                myAsyncTask2 myWebFetch = new myAsyncTask2();
                myWebFetch.flag = this.flag+1;
                myWebFetch.execute();
            }else{
                hideChargement();
                myDbHelper.close();
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

                // *** Les sous types
                if (this.flag==2) {
                    try {

                        myDbHelper.deleteSousType();

                        String tmp_type = getResources().getString(R.string.sqlType1_2);
                        URL url = new URL("http://appvilledelyon.c2is.fr/soustypes.php?type="+tmp_type+"&version="+Config.VERSION_API);
                        URLConnection connection = url.openConnection();

                        Document doc = parseXML(connection.getInputStream());

                        NodeList descNodes = doc.getElementsByTagName("soustype");
                        NodeList listChamps;

                        ContentValues myValue = new ContentValues();

                        for(int i=0; i<descNodes.getLength();i++)
                        {
                            Node courant        = descNodes.item(i);
                            NodeList listNode   = courant.getChildNodes();
                            for(int j=0; j<listNode.getLength();j++) {
                                if(listNode.item(j).getNodeName().equals("slug")){
                                    myValue.put("slug", listNode.item(j).getTextContent());
                                }else if(listNode.item(j).getNodeName().equals("name")){
                                    myValue.put("libelle", listNode.item(j).getTextContent());
                                }
                            }
                            myValue.put("ordre",i);
                            myValue.put("type", tmp_type);

                            myDbHelper.insertType(myValue);
                        }

                    } catch (MalformedURLException e) {
                        Log.d("myTag", "This is my MalformedURLException");
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        String temp = e.toString();

                    }
                }else if (this.flag==3) {
                    try {
                        String tmp_type = getResources().getString(R.string.sqlType1_3);
                        URL url = new URL("http://appvilledelyon.c2is.fr/soustypes.php?type="+tmp_type+"&version="+Config.VERSION_API);
                        URLConnection connection = url.openConnection();

                        Document doc = parseXML(connection.getInputStream());

                        NodeList descNodes = doc.getElementsByTagName("soustype");
                        NodeList listChamps;

                        ContentValues myValue = new ContentValues();

                        for(int i=0; i<descNodes.getLength();i++)
                        {
                            Node courant = descNodes.item(i);
                            NodeList listNode = courant.getChildNodes();
                            for(int j=0; j<listNode.getLength();j++) {
                                if(listNode.item(j).getNodeName().equals("slug")){
                                    myValue.put("slug", listNode.item(j).getTextContent());
                                }else if(listNode.item(j).getNodeName().equals("name")){
                                    myValue.put("libelle", listNode.item(j).getTextContent());
                                }
                            }
                            myValue.put("ordre",i);
                            myValue.put("type", tmp_type);

                            myDbHelper.insertType(myValue);
                        }

                    } catch (MalformedURLException e) {
                        Log.d("myTag", "This is my MalformedURLException");
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        String temp = e.toString();

                    }
                    // les balades
                }else if (this.flag==4) {

                    try {
                        URL url = new URL("http://appvilledelyon.c2is.fr/balades.php?version="+Config.VERSION_API);

                        if (Config.flagActivePreprod==1) {
                            url = new URL("http://c2is:c2is@prep.c2is.fr/appvilledelyon/current/balades.php?version="+Config.VERSION_API);
                        }

                        URLConnection connection = url.openConnection();

                        Document doc = parseXML(connection.getInputStream());

                        NodeList descNodes = doc.getElementsByTagName("balade");
                        NodeList listChamps;

                        ContentValues myValue = new ContentValues();

                        if (descNodes.getLength()>0) {
                            myDbHelper.deleteBalade();
                        }

                        for(int i=0; i<descNodes.getLength();i++)
                        {
                            Node courant = descNodes.item(i);
                            NodeList listNode = courant.getChildNodes();
                            for(int j=0; j<listNode.getLength();j++) {
                                if(listNode.item(j).getNodeName().equals("title")){
                                    myValue.put("titre", listNode.item(j).getTextContent());
                                }else if(listNode.item(j).getNodeName().equals("content")){
                                    myValue.put("content", listNode.item(j).getTextContent());
                                }else if(listNode.item(j).getNodeName().equals("visuel")){
                                    myValue.put("visuel", listNode.item(j).getTextContent());
                                }else if(listNode.item(j).getNodeName().equals("tetiaire")){
                                    myValue.put("tetiaire", listNode.item(j).getTextContent());
                                }else if(listNode.item(j).getNodeName().equals("tetiaire_hd")){
                                    myValue.put("tetiaire_hd", listNode.item(j).getTextContent());
                                }else if(listNode.item(j).getNodeName().equals("points")){
                                    myValue.put("points", listNode.item(j).getTextContent());
                                }
                            }

                            myDbHelper.insertBalade(myValue);
                        }

                    } catch (MalformedURLException e) {
                        Log.d("myTag", "This is my MalformedURLException");
                    } catch (Exception e) {
                        Log.d("myTag", "toto");
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        String temp = e.toString();

                    }



                    // les urgences
                }else if (this.flag==77) {
                    try {
                        URL url = new URL("http://appvilledelyon.c2is.fr/equipements.php?type=urgence&version="+Config.VERSION_API);
                        URLConnection connection = url.openConnection();

                        Document doc = parseXML(connection.getInputStream());

                        NodeList descNodes = doc.getElementsByTagName("equipement");
                        NodeList listChamps;

                        ContentValues myValue = new ContentValues();

                        if (descNodes.getLength()>0) {
                            myDbHelper.deleteUrgence();
                        }

                        for(int i=0; i<descNodes.getLength();i++)
                        {
                            Node courant = descNodes.item(i);
                            NodeList listNode = courant.getChildNodes();
                            for(int j=0; j<listNode.getLength();j++) {
                                if(listNode.item(j).getNodeName().equals("titre")){
                                    myValue.put("titre", listNode.item(j).getTextContent());
                                }else if(listNode.item(j).getNodeName().equals("mots_clefs")){
                                    myValue.put("mots_clefs", listNode.item(j).getTextContent());
                                }
                            }
                            myValue.put("type", "urgence");

                            myDbHelper.insertUrgence(myValue);
                        }

                    } catch (MalformedURLException e) {
                        Log.d("myTag", "This is my MalformedURLException");
                    } catch (Exception e) {
                        Log.d("myTag", "Exception");
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        String temp = e.toString();

                    }
                    // les evenements incontournables
                }else if (this.flag==6) {

                    try {

                        URL url = new URL("http://appvilledelyon.c2is.fr/evenements.php?inc=1&version="+Config.VERSION_API);
                        URLConnection connection = url.openConnection();

                        Document doc = parseXML(connection.getInputStream());

                        NodeList descNodes = doc.getElementsByTagName("evenement");
                        NodeList listChamps;

                        ContentValues myValue = new ContentValues();

                        if (descNodes.getLength()>0) {
                            myDbHelper.deleteIncontrounables();
                        }

                        for(int i=0; i<descNodes.getLength();i++)
                        {
                            Node courant = descNodes.item(i);
                            NodeList listNode = courant.getChildNodes();

                            for(int j=0; j<listNode.getLength();j++) {
                                if(listNode.item(j).getNodeName().equals("titre")){
                                    myValue.put("titre", listNode.item(j).getTextContent());
                                }else if(listNode.item(j).getNodeName().equals("visuel_wp")){
                                    myValue.put("visuel_wp", listNode.item(j).getTextContent());

                                }else if(listNode.item(j).getNodeName().equals("type_principal")){
                                    myValue.put("type_principal", listNode.item(j).getTextContent());

                                }else if(listNode.item(j).getNodeName().equals("accroche_detaillee")){
                                    myValue.put("description", listNode.item(j).getTextContent());

                                }else if(listNode.item(j).getNodeName().equals("visuel")){
                                    myValue.put("visuel", listNode.item(j).getTextContent());
                                }else if(listNode.item(j).getNodeName().equals("site_web")){
                                    myValue.put("site_web", listNode.item(j).getTextContent());
                                }else if(listNode.item(j).getNodeName().equals("visuel_hd")){
                                    myValue.put("visuel_hd", listNode.item(j).getTextContent());
                                }else if(listNode.item(j).getNodeName().equals("tetiaire")){
                                    myValue.put("tetiaire", listNode.item(j).getTextContent());
                                }else if(listNode.item(j).getNodeName().equals("tetiaire_hd")){
                                    myValue.put("tetiaire_hd", listNode.item(j).getTextContent());
                                }else if(listNode.item(j).getNodeName().equals("accroche_date_lieu")){
                                    myValue.put("accroche", listNode.item(j).getTextContent());
                                }

                            }

                            myDbHelper.insertIncontournable(myValue);
                        }

                    } catch (MalformedURLException e) {
                        Log.d("myTag", "This is my MalformedURLException");
                    } catch (Exception e) {
                        Log.d("myTag", "Exception");
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        String temp = e.toString();

                    }
                }else if (this.flag==5) {
                    try {
                        String tmp_type = getResources().getString(R.string.sqlType3_2);


//                        URL url = new URL("http://c2is:c2is@prep.c2is.fr/appvilledelyon/current//soustypes.php?type="+tmp_type+"&version="+Config.VERSION_API);
                        URL url = new URL("http://appvilledelyon.c2is.fr/soustypes.php?type="+tmp_type+"&version="+Config.VERSION_API);
                        URLConnection connection = url.openConnection();

                        Document doc = parseXML(connection.getInputStream());

                        NodeList descNodes = doc.getElementsByTagName("soustype");
                        NodeList listChamps;

                        ContentValues myValue = new ContentValues();

                        for(int i=0; i<descNodes.getLength();i++)
                        {
                            Node courant = descNodes.item(i);
                            NodeList listNode = courant.getChildNodes();
                            for(int j=0; j<listNode.getLength();j++) {
                                if(listNode.item(j).getNodeName().equals("slug")){
                                    myValue.put("slug", listNode.item(j).getTextContent());
                                }else if(listNode.item(j).getNodeName().equals("name")){
                                    myValue.put("libelle", listNode.item(j).getTextContent());
                                }
                            }
                            myValue.put("ordre",i);
                            myValue.put("type", tmp_type);

                            myDbHelper.insertType(myValue);
                        }

                    } catch (MalformedURLException e) {
                        Log.d("myTag", "This is my MalformedURLException");
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        String temp = e.toString();

                    }
                    // Tous les equipements
                }else if (this.flag==8) {
                    try {
                        String str_type = getResources().getString(R.string.sqlType3_1);

//                        URL url = new URL("http://c2is:c2is@prep.c2is.fr/appvilledelyon/current/equipements.php?type="+str_type+"&version="+Config.VERSION_API);

                        URL url = new URL("http://appvilledelyon.c2is.fr/equipements.php?type="+str_type+"&version="+Config.VERSION_API);
                        URLConnection connection = url.openConnection();

                        Document doc = parseXML(connection.getInputStream());

                        NodeList descNodes = doc.getElementsByTagName("equipement");
                        NodeList listChamps;

                        ContentValues myValue = new ContentValues();

                        for (int i = 0; i < descNodes.getLength(); i++) {
                            Node courant = descNodes.item(i);
                            NodeList listNode = courant.getChildNodes();
                            for (int j = 0; j < listNode.getLength(); j++) {
                                if (listNode.item(j).getNodeName().equals("titre")) {
                                    myValue.put("titre", listNode.item(j).getTextContent());
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
                                } else if (listNode.item(j).getNodeName().equals("horaires")) {
                                    myValue.put("horaires", listNode.item(j).getTextContent());
                                } else if (listNode.item(j).getNodeName().equals("latitude")) {
                                    myValue.put("latitude", listNode.item(j).getTextContent());
                                } else if (listNode.item(j).getNodeName().equals("jour")) {
                                    myValue.put("jour", listNode.item(j).getTextContent());
                                } else if (listNode.item(j).getNodeName().equals("arrondissement")) {
                                    myValue.put("arrondissement", listNode.item(j).getTextContent());
                                } else if (listNode.item(j).getNodeName().equals("fermeture_exceptionnelle")) {
                                    myValue.put("fermeture_exceptionnelle", listNode.item(j).getTextContent());
                                }
                            }
                            myValue.put("type", str_type);

                            //myDbHelper.insertUrgence(myValue);
                        }

                    } catch (MalformedURLException e) {
                        Log.d("myTag", "This is my MalformedURLException");
                    } catch (Exception e) {
                        Log.d("myTag", "Exception");
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        String temp = e.toString();
                        Log.d("myTag", temp);

                    }
                    // Equipements dans liste
                }else if (this.flag==9) {
                    myDbHelper.close();

                    try {
                        myDbHelper.openDataBase();
                        Log.d("myTag", "ouverture 3 bdd ok");
                    }catch(SQLException sqle){
                        Log.d("myTag", "ouverture 3 bdd KO");
                        throw sqle;
                    }

                    Cursor c2 = myDbHelper.loadAllType();

                        if (c2 != null) {
                            while (c2.moveToNext()) {
                                //Log.d("myTag", "Sous type : " +  c2.getString(0));
                                String str_type = c2.getString(0);

                                try {

//                                    URL url = new URL("http://c2is:c2is@prep.c2is.fr/appvilledelyon/current/equipements.php?type="+str_type+"&version="+Config.VERSION_API);

                                    URL url = new URL("http://appvilledelyon.c2is.fr/equipements.php?type="+str_type+"&version="+Config.VERSION_API);
                                    URLConnection connection = url.openConnection();

                                    Document doc = parseXML(connection.getInputStream());

                                    NodeList descNodes = doc.getElementsByTagName("equipement");
                                    NodeList listChamps;

                                    ContentValues myValue = new ContentValues();

                                    for (int i = 0; i < descNodes.getLength(); i++) {
                                        Node courant = descNodes.item(i);
                                        NodeList listNode = courant.getChildNodes();
                                        for (int j = 0; j < listNode.getLength(); j++) {
                                            if (listNode.item(j).getNodeName().equals("titre")) {
                                                myValue.put("titre", listNode.item(j).getTextContent());
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
                                            } else if (listNode.item(j).getNodeName().equals("fermeture_exceptionnelle")) {
                                                myValue.put("fermeture_exceptionnelle", listNode.item(j).getTextContent());
                                            }
                                        }
                                        myValue.put("type", str_type);

                                        //myDbHelper.updateUrgence(myValue);
                                    }

                                } catch (MalformedURLException e) {
                                    Log.d("myTag", "This is my MalformedURLException");
                                } catch (Exception e) {
                                    Log.d("myTag", "Exception");
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                    String temp = e.toString();
                                    Log.d("myTag", temp);

                                }


                            }
                        }
                    // les demarches
                }else if (this.flag==7) {

                    String[] tabType = {"et-si","par-organisme","par-demarche"};
                    String str_type = "";

                    myDbHelper.deleteDemarche();

                    for (int k=0;k<tabType.length;k++) {
                        str_type = tabType[k];

                        try {

                            URL url = new URL("http://appvilledelyon.c2is.fr/equipements.php?type=" + str_type + "&version="+Config.VERSION_API);
                            //URL url = new URL("http://c2is:c2is@prep.c2is.fr/appvilledelyon/current/equipements.php?type=" + str_type + "&version="+Config.VERSION_API);
                            URLConnection connection = url.openConnection();

                            Document doc = parseXML(connection.getInputStream());

                            NodeList descNodes = doc.getElementsByTagName("equipement");
                            NodeList listChamps;

                            ContentValues myValue = new ContentValues();

                            for (int i = 0; i < descNodes.getLength(); i++) {
                                Node courant = descNodes.item(i);
                                NodeList listNode = courant.getChildNodes();
                                for (int j = 0; j < listNode.getLength(); j++) {
                                    if (listNode.item(j).getNodeName().equals("titre")) {
                                        myValue.put("titre", listNode.item(j).getTextContent());

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
                                    } else if (listNode.item(j).getNodeName().equals("horaires")) {
                                        myValue.put("horaires", listNode.item(j).getTextContent());
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
                                    } else if (listNode.item(j).getNodeName().equals("jour")) {
                                        myValue.put("jour", listNode.item(j).getTextContent());
                                    } else if (listNode.item(j).getNodeName().equals("arrondissement")) {
                                        myValue.put("arrondissement", listNode.item(j).getTextContent());
                                    } else if (listNode.item(j).getNodeName().equals("type_associe")) {
                                        myValue.put("type_associe", listNode.item(j).getTextContent());
                                    } else if (listNode.item(j).getNodeName().equals("fermeture_exceptionnelle")) {
                                        myValue.put("fermeture_exceptionnelle", listNode.item(j).getTextContent());
                                    }

                                }
                                myValue.put("type", str_type);

                                myDbHelper.insertUrgence(myValue);
                            }

                        } catch (MalformedURLException e) {
                            Log.d("myTag", "This is my MalformedURLException");
                        } catch (Exception e) {
                            Log.d("myTag", "Exception");
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            String temp = e.toString();
                            Log.d("myTag", temp);

                        }
                    }
                }



            } catch (Exception e) {
                //	Toast.makeText(Config.myResVehicule,"erreur : " + e.toString(), Toast.LENGTH_LONG).show();
                Log.d("myTag", "catché : " + e.toString());

            }

            return null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        Log.d("myTag", "onResume onResume : / " + Config.flag_is_playing);

        if (Config.flag_is_playing==1) {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_notif)
                    .setTitle("Réveil")
                    .setMessage("Appuyer pour couper la sonnerie.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Config.mp.stop();
                            Config.flag_is_playing = 0;
                            Config.flagDirectSavoir = 1;

                            Intent intent = new Intent(MainActivity.this, Reveil.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivityForResult(intent, 0);

                            finish();
                        }

                    })
                    .show();


            SharedPreferences sharedPref  = getSharedPreferences("vdl", Context.MODE_WORLD_WRITEABLE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("alert_vdl", "");
            editor.commit();
        }else{

            final SharedPreferences prefs = getSharedPreferences("vdl", Context.MODE_PRIVATE);
            String strAlert = prefs.getString("alert_vdl", "");

            if(strAlert.equalsIgnoreCase("aymeric")) {

                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_notif)
                        .setTitle("Réveil")
                        .setMessage("Appuyer pour couper la sonnerie.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Config.mp.stop();
                                Config.flagDirectSavoir = 1;

                                Intent intent = new Intent(MainActivity.this, Reveil.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivityForResult(intent, 0);

                                finish();
                            }

                        })
                        .show();


                SharedPreferences sharedPref  = getSharedPreferences("vdl", Context.MODE_WORLD_WRITEABLE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("alert_vdl", "");
                editor.commit();

            }

        }

        AppEventsLogger.activateApp(this);
        Config.myActu = this;
        Config.majNbeFav((TextView) findViewById(R.id.txt_nbe_favoris), this.getBaseContext());
        Config.showAlertNotif(this);



    }
    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

}
