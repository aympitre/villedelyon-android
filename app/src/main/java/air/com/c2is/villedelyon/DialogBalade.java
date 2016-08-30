package air.com.c2is.villedelyon;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import android.view.Window;
import android.app.Dialog;
import android.widget.ImageButton;

public class DialogBalade extends Dialog {

    public Activity c;
    public Dialog d;
    public Button yes, no;
    public LinearLayout myLay;
    public int flagType;
    public HashMap<String, Object> mapping2;
    public ArrayList<HashMap<String, Object>> listItems;
    public TextView lblTitre;
    public WebView  webTexte;

    public DialogBalade(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;

        flagType = 0;
        listItems = new ArrayList<HashMap<String, Object>>();
        mapping2 = new HashMap<String, Object>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_balade);

        myLay    = (LinearLayout) findViewById(R.id.myLay);

        Typeface myTypeface = Typeface.createFromAsset(Config.myHome.getAssets(), "Oswald-Regular.ttf");

        lblTitre   = (TextView) findViewById(R.id.lblTitre);
        lblTitre.setTypeface(myTypeface);

        webTexte  = (WebView) findViewById(R.id.description);
        WebSettings settings = webTexte.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        webTexte.setBackgroundColor(Color.TRANSPARENT);


    }

    public void setValeur(String p_titre, String p_desc) {
        lblTitre.setText(p_titre);
        webTexte.loadDataWithBaseURL(null, p_desc, "text/html", "UTF-8", null);
    }

}
