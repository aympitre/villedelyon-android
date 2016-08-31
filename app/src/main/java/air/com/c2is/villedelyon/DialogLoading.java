package air.com.c2is.villedelyon;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class DialogLoading extends Dialog {

    public Activity c;
    public Dialog d;
    public Button yes, no;
    public LinearLayout myLay;
    public int flagType;
    public HashMap<String, Object> mapping2;
    public ArrayList<HashMap<String, Object>> listItems;
    public TextView lblTitre;

    public DialogLoading(Activity a) {
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
        setContentView(R.layout.dialog_loading);

        myLay    = (LinearLayout) findViewById(R.id.myLay);

        Typeface myTypeface = Typeface.createFromAsset(Config.myHome.getAssets(), "Oswald-Regular.ttf");

        lblTitre   = (TextView) findViewById(R.id.lblTitre);
        lblTitre.setTypeface(myTypeface);
    }



}
