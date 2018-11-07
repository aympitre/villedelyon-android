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

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentDetailBalade extends Fragment {
    private DataBaseHelper myDbHelper;
    public ArrayList<HashMap<String, Object>> listItems;
    public View rootView;

    public FragmentDetailBalade() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView            = inflater.inflate(R.layout.fragment_detail_balade, container, false);
        Typeface myTypeface = Typeface.createFromAsset(Config.myHome.getAssets(), "Oswald-Regular.ttf");

        Config.myDetailBalade = this;

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

        return rootView;
    }

}
