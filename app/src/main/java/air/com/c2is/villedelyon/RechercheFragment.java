package air.com.c2is.villedelyon;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.database.SQLException;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.appevents.AppEventsLogger;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


/**
 * A placeholder fragment containing a simple view.
 */
public class RechercheFragment extends Fragment {
    private DataBaseHelper myDbHelper;
    public LinearLayout myChargement;
    public LinearLayout myLayerDatePicker;
    public LinearLayout myLayerTitre;
    public DatePicker myDate;
    public TextView titreFrom1;
    public TextView titreFrom2;
    public EditText txtTitre;
    public TextView myChargementText;
    public ArrayList<HashMap<String, Object>> listItems;
    public int flagTypeToEquip;
    public int flagDirectEquipement;
    public Button btPicker;
    public Button btPickerOk;
    public Button btValider;
    public int flagDatePicker = 0;
    public int flagFirstDatePicker = 0;
    public ImageButton btFermerPicker;

    public RechercheFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        flagFirstDatePicker = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView       = inflater.inflate(R.layout.fragment_recherche_evt, container, false);

        Typeface myTypeface = Typeface.createFromAsset(Config.myHome.getAssets(), "Oswald-Regular.ttf");

        txtTitre          = (EditText) rootView.findViewById(R.id.txtTitre);
        myDate            = (DatePicker) rootView.findViewById(R.id.dpResult);

        myLayerTitre      = (LinearLayout) rootView.findViewById(R.id.titrePageRecherche);
        myLayerDatePicker = (LinearLayout) rootView.findViewById(R.id.layerDatePicker);

        btFermerPicker    = (ImageButton) rootView.findViewById(R.id.btFermerPicker);
        btFermerPicker.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        hideDatePicker();
                    }
                }
        );

        btValider         = (Button) rootView.findViewById(R.id.btValider);
        btValider.setTypeface(myTypeface);
        btValider.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        int temp = 0;
                        if (txtTitre.getText().toString().length()>0) {
                            temp = 1;
                            Config.str_titre_evt = txtTitre.getText().toString();

                            try {
                                Config.str_titre_evt = URLEncoder.encode(Config.str_titre_evt, "utf-8");
                            } catch (UnsupportedEncodingException e) {
                                // TODO Auto-generated catch block
                            }
                        }else{
                            Config.str_titre_evt = "";
                        }

                        if (flagFirstDatePicker==1) {
                            temp = 1;
                            getSelDate();
                        }else{
                            setDateDuJour();
                            temp = 1;
                        }

                        killClavier();

                        if (temp==1) {
                            Config.flagRechercheEvt     = 1;
                            Config.flagRetourRecherche  = 1;
                            Config.flagFromRecherche    = 1;
                            Config.myFragment.loadEvenement();
                        }else{
                            new AlertDialog.Builder(Config.myActu)
                                    .setIcon(R.drawable.ic_notif)
                                    .setTitle("Attention")
                                    .setMessage("Vous devez saisir soit un titre, soit une date avant de lancer une recherche")
                                    .show();
                        }
                    }
                }
        );

        TextView titreFrom1 = (TextView) rootView.findViewById(R.id.titreFrom1);
        TextView titreFrom2 = (TextView) rootView.findViewById(R.id.titreFrom2);


        titreFrom1.setTypeface(myTypeface);
        titreFrom2.setTypeface(myTypeface);

        btPickerOk= (Button) rootView.findViewById(R.id.btOk);
        btPickerOk.setTypeface(myTypeface);
        btPickerOk.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        getSelDate();
                        hideDatePicker();
                    }
                }
        );

        btPicker = (Button) rootView.findViewById(R.id.btPicker);
        btPicker.setTypeface(myTypeface);
        btPicker.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (flagDatePicker==1) {
                            hideDatePicker();
                        }else{
                            killClavier();
                            showDatePicker();
                        }
                    }
                }
        );


        myDbHelper = new DataBaseHelper(Config.myHome.getBaseContext());
        try {
            myDbHelper.openDataBase();
           // Log.d("myTag", "ouverture bdd ok");
        }catch(SQLException sqle){
           // Log.d("myTag", "ouverture bdd KO");
            throw sqle;
        }


        return rootView;
    }

    public void killClavier() {
        InputMethodManager imm = (InputMethodManager) Config.myHome.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txtTitre.getWindowToken(), 0);
    }

    public void setDateDuJour() {
        Calendar c  = Calendar.getInstance();
        Config.date_evt = c.getTime();

        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String formatted         = format1.format(c.getTime());

        Config.str_date_evt      = formatted;

    }

    public void getSelDate() {
        int day     = myDate.getDayOfMonth();
        int month   = myDate.getMonth();
        int year    = myDate.getYear();

        Calendar c  = Calendar.getInstance();

        c.set(year,month,day);
        Config.date_evt = c.getTime();

        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String formatted         = format1.format(c.getTime());

        Config.str_date_evt      = formatted;

        SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");
        String formatted2        = format2.format(c.getTime());

        btPicker.setText(formatted2);
    }

    public void showDatePicker() {
        flagFirstDatePicker = 1;
        flagDatePicker      = 1;

        myLayerTitre.setVisibility      (View.GONE);
        myLayerDatePicker.setVisibility (View.VISIBLE);
        btPicker.setVisibility          (View.GONE);
        txtTitre.setVisibility          (View.GONE);
        btValider.setVisibility         (View.GONE);
    }
    public void hideDatePicker() {
        flagDatePicker = 0;

        myLayerTitre.setVisibility      (View.VISIBLE);
        myLayerDatePicker.setVisibility (View.GONE);
        btPicker.setVisibility          (View.VISIBLE);
        txtTitre.setVisibility          (View.VISIBLE);
        btValider.setVisibility         (View.VISIBLE);
    }
}
