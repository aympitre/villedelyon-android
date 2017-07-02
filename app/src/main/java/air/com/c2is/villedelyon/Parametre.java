package air.com.c2is.villedelyon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


public class Parametre extends Activity {
    public Switch myGeoloc;
    public SharedPreferences sharedPref;
    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);
        tracker = analytics.newTracker(getResources().getString(R.string.google_analytics_id));
        tracker.setScreenName("/Parametre");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());

        Config.myParametre = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametre);
        FacebookSdk.sdkInitialize(getApplicationContext());

        Config.myActu  =this;
        WebView myTexte  = (WebView) findViewById(R.id.texte);
        WebSettings settings = myTexte.getSettings();
        settings.setDefaultTextEncodingName("utf-8");

        myTexte.loadUrl("file:///android_asset/mentions.html");

        myGeoloc = (Switch) findViewById(R.id.switchGeoloc);

        TextView myTitreMention = (TextView) findViewById(R.id.titreMention);

        Typeface myTypeface = Typeface.createFromAsset(Config.myHome.getAssets(), "Oswald-Regular.ttf");
        myGeoloc.setTypeface(myTypeface);
        myGeoloc.getPaint().setAntiAlias(true);
        myTitreMention.setTypeface(myTypeface);
        myTitreMention.getPaint().setAntiAlias(true);

        ImageView myLogo = (ImageView) findViewById(R.id.logo);
        // ACTION DES BOUTONS DE LA HOME
        myLogo.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        startActivity(new Intent(Parametre.this, MainActivity.class));
                    }
                }
        );
        ImageButton myBtFavoris = (ImageButton) findViewById(R.id.bt_favoris);
        myBtFavoris.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(Parametre.this, favoris.class);
                        startActivityForResult(intent, 2);
                    }
                }
        );
        sharedPref = this.getSharedPreferences("vdl", Context.MODE_PRIVATE);

        int flag_geoloc = sharedPref.getInt("flag_geoloc", 1);

        Log.d("myTag", "SharedPreferences frag : " + sharedPref.getInt("flag_geoloc", 0));

        if (flag_geoloc==1) {
            myGeoloc.setChecked(true);
        }else{
            myGeoloc.setChecked(false);
        }

        myGeoloc.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPref.edit();
                if (isChecked) {
                    editor.putInt("flag_geoloc", 1);
                    Config.flag_tri_geoloc = 1;
                } else {
                    editor.putInt("flag_geoloc", 0);
                    Config.flag_tri_geoloc = 0;
                }
                editor.commit();
            }
        });

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
        Config.myActu  =this;
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
                        Config.killLocalNotification(getBaseContext());
                        Config.flagDirectSavoir = 1;

                        Intent intent = new Intent(Parametre.this, Reveil.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent, 0);

                        finish();
                    }

                })
                .show();


        SharedPreferences sharedPref = getSharedPreferences("vdl", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("alert_vdl", "");
        editor.commit();
    }
}
