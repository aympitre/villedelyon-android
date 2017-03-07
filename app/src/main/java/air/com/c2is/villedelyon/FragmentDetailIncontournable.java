package air.com.c2is.villedelyon;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentDetailIncontournable extends android.support.v4.app.FragmentActivity {
    private DataBaseHelper myDbHelper;
    public ArrayList<HashMap<String, Object>> listItems;
    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    public FragmentDetailIncontournable() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
        Config.myActu = this;
        Config.majNbeFav((TextView) findViewById(R.id.txt_nbe_favoris), this.getBaseContext());
        Config.showAlertNotif(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        analytics = GoogleAnalytics.getInstance(Config.myHome.getBaseContext());
        analytics.setLocalDispatchPeriod(1800);
        tracker = analytics.newTracker(getResources().getString(R.string.google_analytics_id));
        tracker.setScreenName("/Fiche detail incontournable : " + Config.myContentValue.get("titre").toString());
        tracker.send(new HitBuilders.ScreenViewBuilder().build());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_detail_inc);
        Config.myDetailIncontournable = this;

        Typeface myTypeface = Typeface.createFromAsset(Config.myHome.getAssets(), "Oswald-Regular.ttf");

        TextView titre = (TextView) findViewById(R.id.titre);
        titre.setTypeface(myTypeface);
        titre.setText(getResources().getString(R.string.libHomeBt3));

        Button myMenu1    = (Button) findViewById(R.id.bt_menu1);
        Button myMenu2    = (Button) findViewById(R.id.bt_menu2);
        Button myMenu3    = (Button) findViewById(R.id.bt_menu3);

        myMenu1.setText(getResources().getString(R.string.libMenu5_1));
        myMenu2.setText(getResources().getString(R.string.libMenu5_2));
        myMenu3.setText(getResources().getString(R.string.libMenu5_3));
        myMenu3.setTextColor(getResources().getColor(R.color.blanc));
        myMenu3.setBackground(getResources().getDrawable(R.drawable.menu_actif));
        myMenu1.setTypeface(myTypeface);
        myMenu2.setTypeface(myTypeface);
        myMenu3.setTypeface(myTypeface);

        ImageButton myBtMenu = (ImageButton) findViewById(R.id.bt_menu);
        ImageView myLogo     = (ImageView) findViewById(R.id.logo);
        // ACTION DES BOUTONS DE LA HOME
        myLogo.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        startActivity(new Intent(FragmentDetailIncontournable.this, MainActivity.class));
                    }
                }
        );
        // ACTION DES BOUTONS DE LA HOME
        myBtMenu.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        startActivity(new Intent(FragmentDetailIncontournable.this, MainActivity.class));
                    }
                }
        );

        TextView myTitre    = (TextView) findViewById(R.id.titrebalade);
        myTitre.setTypeface(myTypeface);

        TextView myTitre2        = (TextView) findViewById(R.id.titrebalade2);
        myTitre2.setTypeface(myTypeface);
        TextView myTitreDuree    = (TextView) findViewById(R.id.titrebaladeduree);
        myTitreDuree.setTypeface(myTypeface);

        myTitre.setText     (Config.myContentValue.get("type_principal").toString());
        myTitre2.setText    (Config.myContentValue.get("titre").toString());
        myTitreDuree.setText(Config.myContentValue.get("accroche").toString());

        ImageView imgTetiaire = (ImageView) findViewById(R.id.imgVisuel);
        BitmapDownloaderTask task = new BitmapDownloaderTask(imgTetiaire);

        if (Config.myContentValue.get("tetiaire_hd").toString().length()>0) {
            task.execute(Config.myContentValue.get("tetiaire_hd").toString());
        }else{
            task.execute(Config.myContentValue.get("tetiaire").toString());
        }



//       Config.myContentValue.get("tetiaire")
//      Config.myContentValue.get("tetiaire_hd")
//        Config.myContentValue.get("points")


        WebView myTexte  = (WebView) findViewById(R.id.description);
        WebSettings settings = myTexte.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        myTexte.setBackgroundColor(Color.TRANSPARENT);

        myTexte.loadDataWithBaseURL(null, Config.myContentValue.get("description").toString(), "text/html", "UTF-8", null);

// *** Bouton du menu
        myMenu1.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Config.resetVarNavigation();
                        Config.codeInterne          = 1;
                        Config.MENU_ACTIVITE        = 1;
                        Config.CODE_DE_MON_ACTIVITE = 5;
                        Intent intent = new Intent(FragmentDetailIncontournable.this, ListType.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent, 0);                    }
                }
        );
        myMenu2.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Config.resetVarNavigation();
                        Config.codeInterne          = 2;
                        Config.MENU_ACTIVITE        = 1;
                        Config.CODE_DE_MON_ACTIVITE = 5;
                        Intent intent = new Intent(FragmentDetailIncontournable.this, ListType.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent, 0);
                    }
                }
        );
        myMenu3.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {

                    }
                }
        );

        ImageButton myBtParam = (ImageButton) findViewById(R.id.bt_param);
        ImageButton myBtFavoris = (ImageButton) findViewById(R.id.bt_favoris);
        myBtParam.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(FragmentDetailIncontournable.this, Parametre.class);
                        startActivityForResult(intent, 2);
                    }
                }
        );
        myBtFavoris.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(FragmentDetailIncontournable.this, favoris.class);
                        startActivityForResult(intent, 2);

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

                        Intent intent = new Intent(FragmentDetailIncontournable.this, Reveil.class);
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
