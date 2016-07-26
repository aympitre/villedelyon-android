package air.com.c2is.villedelyon;
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

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentDetailBalade extends Fragment {
    private DataBaseHelper myDbHelper;
    public ArrayList<HashMap<String, Object>> listItems;
    public View rootView;
    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    public FragmentDetailBalade() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        analytics = GoogleAnalytics.getInstance(Config.myHome.getBaseContext());
        analytics.setLocalDispatchPeriod(1800);
        tracker = analytics.newTracker(getResources().getString(R.string.google_analytics_id));
        tracker.setScreenName("/Fiche detail balade : " + Config.myContentValue.get("titre").toString());
        tracker.send(new HitBuilders.ScreenViewBuilder().build());

        rootView            = inflater.inflate(R.layout.fragment_detail_balade, container, false);
        Typeface myTypeface = Typeface.createFromAsset(Config.myHome.getAssets(), "Oswald-Regular.ttf");

        Config.myDetailBalade = this;

        Button myMenu1    = (Button) rootView.findViewById(R.id.bt_menu1);
        Button myMenu2    = (Button) rootView.findViewById(R.id.bt_menu2);
        Button myMenu3    = (Button) rootView.findViewById(R.id.bt_menu3);

        myMenu1.setText(getResources().getString(R.string.libMenu3_1));
        myMenu2.setText(getResources().getString(R.string.libMenu3_2));
        myMenu3.setText(getResources().getString(R.string.libMenu3_3));
        myMenu3.setTextColor(getResources().getColor(R.color.blanc));
        myMenu3.setBackground(getResources().getDrawable(R.drawable.menu_actif));

        myMenu1.setTypeface(myTypeface);
        myMenu2.setTypeface(myTypeface);
        myMenu3.setTypeface(myTypeface);

        TextView myTitre    = (TextView) rootView.findViewById(R.id.titrebalade);
        myTitre.setTypeface(myTypeface);
        myTitre.setText(Config.formatLastWord(Config.myContentValue.get("titre").toString()));

        ImageView imgTetiaire = (ImageView) rootView.findViewById(R.id.imgVisuel);
        BitmapDownloaderTask task = new BitmapDownloaderTask(imgTetiaire);

        if (Config.myContentValue.get("tetiaire_hd").toString().length()>0) {
            task.execute(Config.myContentValue.get("tetiaire_hd").toString());
        }else{
            task.execute(Config.myContentValue.get("tetiaire").toString());
        }

        ImageButton myBtCarto = (ImageButton) rootView.findViewById(R.id.bt_carto_balade);
        String str_point = Config.myContentValue.get("points").toString();
        if (str_point.length()>0) {
            myBtCarto.setVisibility(View.VISIBLE);
            myBtCarto.setOnClickListener(
                    new View.OnClickListener() {
                        public void onClick(View v) {
                            Config.myFragment.loadCartoBalade();
                        }
                    }
            );
        }else{
            myBtCarto.setVisibility(View.GONE);
        }

        WebView myTexte  = (WebView) rootView.findViewById(R.id.description);
        WebSettings settings = myTexte.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        myTexte.setBackgroundColor(Color.TRANSPARENT);

        myTexte.loadDataWithBaseURL(null, Config.myContentValue.get("content").toString(), "text/html", "UTF-8", null);

        // *** Bouton du menu
        myMenu1.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Config.str_demarche     = "";
                        Config.flagForceRetour  = 0;
                        Config.MENU_ACTIVITE    = 1;
                        Config.flagShowCarto    = 1;
                        Config.fragToReload     = getResources().getString(R.string.sqlType3_1);
                        Config.myFragment.loadFragment(getResources().getString(R.string.sqlType3_1));
                    }
                }
        );
        myMenu2.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Config.str_demarche     = "";
                        Config.flagForceRetour  = 0;
                        Config.MENU_ACTIVITE    = 2;
                        Config.fragToReload     = getResources().getString(R.string.sqlType3_2);
                        Config.myFragment.loadFragment(getResources().getString(R.string.sqlType3_2));
                    }
                }
        );


        return rootView;
    }

}
