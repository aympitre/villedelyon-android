package air.com.c2is.villedelyon;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DialogAlerte extends Dialog {

    public Activity c;
    public Dialog d;
    public Button yes, no;
    public LinearLayout myLay;

    public DialogAlerte(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_alerte);

        ((TextView) findViewById(R.id.lblTitre)).setText("pouet");
    }
}
