package air.com.c2is.villedelyon;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


public class LeSaviezVousDetail extends Activity {
    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);
        tracker = analytics.newTracker(getResources().getString(R.string.google_analytics_id));
        tracker.setScreenName("/Le saviez vous detail : " + Config.titre_savoir);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_le_saviez_vous_detail);

        Config.mySaviezDetail = this;

        FacebookSdk.sdkInitialize(getApplicationContext());
        Config.myActu  =this;

        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "Oswald-Regular.ttf");
        TextView myTitre    = (TextView) findViewById(R.id.titre);

        myTitre.setTypeface(myTypeface);

        WebView myTexte  = (WebView) findViewById(R.id.texteSavoir);
        myTexte.loadDataWithBaseURL(null, Config.texte_savoir, "text/html", "UTF-8", null);
        myTexte.setBackgroundColor(Color.TRANSPARENT);

        TextView myTitreDetail    = (TextView) findViewById(R.id.titreSavoir);
        myTitreDetail.setTypeface(myTypeface);
        myTitreDetail.getPaint().setAntiAlias(true);

        myTitreDetail.setText(Config.titre_savoir);

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
                        startActivity(new Intent(LeSaviezVousDetail.this, MainActivity.class));
                    }
                }
        );
        ImageButton myBtFavoris = (ImageButton) findViewById(R.id.bt_favoris);
        myBtFavoris.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(LeSaviezVousDetail.this, favoris.class);
                        startActivityForResult(intent, 2);
                    }
                }
        );
        ImageButton myBtParam = (ImageButton) findViewById(R.id.bt_param);
        myBtParam.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(LeSaviezVousDetail.this, Parametre.class);
                        startActivityForResult(intent, 2);
                    }
                }
        );
        ImageButton myBtMenu = (ImageButton) findViewById(R.id.bt_menu);
        myBtMenu.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        startActivity(new Intent(LeSaviezVousDetail.this, MainActivity.class));
                    }
                }
        );

        Config.majNbeFav((TextView) findViewById(R.id.txt_nbe_favoris), this.getBaseContext());
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

                        Intent intent = new Intent(LeSaviezVousDetail.this, Reveil.class);
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
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
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
