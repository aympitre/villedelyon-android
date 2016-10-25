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

public class DialogOk extends Dialog {

    public Activity c;
    public Dialog d;
    public Button yes, no;
    public LinearLayout myLay;
    public int flagType;
    public HashMap<String, Object> mapping2;
    public ArrayList<HashMap<String, Object>> listItems;
    public TextView lblTitre;

    public DialogOk(Activity a) {
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
        setContentView(R.layout.dialog_ok);

        Typeface myTypeface = Typeface.createFromAsset(Config.myHome.getAssets(), "Oswald-Regular.ttf");

        TextView titreOk = (TextView) findViewById(R.id.titreConfOk);
        titreOk.setTypeface(myTypeface);
    }



}
