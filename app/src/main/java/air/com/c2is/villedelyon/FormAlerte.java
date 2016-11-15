package air.com.c2is.villedelyon;

import android.os.Handler;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.MediaStore.Images;
import java.io.ByteArrayOutputStream;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.BitmapFactory.Options;
import android.graphics.Typeface;
import android.location.Location;
import java.net.URLEncoder;
import android.opengl.Matrix;
import org.apache.http.NameValuePair;

import android.widget.Toast;
import java.io.FileInputStream;
import java.util.List;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import org.apache.http.HttpStatus;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.EditText;
import android.net.Uri;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.io.InputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;

import android.os.ParcelFileDescriptor;
import android.graphics.BitmapFactory;

import android.support.v4.graphics.BitmapCompat;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import android.widget.RadioButton;
import android.view.View.OnFocusChangeListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import android.location.Geocoder;
import android.location.Address;
import java.util.Locale;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FormAlerte extends Activity {
    public Switch myGeoloc;
    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    public ImageView imgPreview;
    public LinearLayout myLayConfKo;
    public LinearLayout myLayConfOk;
    public LinearLayout layRadio;
    public LinearLayout layImage;
    public ScrollView myScrollForm;

    public TextView layObligatoire;
    public TextView libLocalisation;
    public EditText chpNom;
    public EditText chpPrenom;
    public EditText chpEmail;
    public EditText chpMessage;
    public EditText chpNumero;
    public EditText chpRue;
    public EditText chpCp;
    public EditText chpVille;
    public EditText chpTelephone;


    public String monImage = "";
    public int    flagPhoto = 0;
    public int    flagCiv = 1;
    public String strNom;
    public String strPrenom;
    public String strEmail;
    public String strMessage;
    public String strNumero;
    public String strRue;
    public String strCp;
    public String strVille;
    public String strTelephone;

    public TextView btValiderChargement;
    public Button btValider;

    public Bitmap myBitmap;

    public double latitude;
    public double longitude;
    public Geocoder geocoder;
    public Bitmap actuBitmap;
    public SharedPreferences sharedPref;

    public Uri imageUri;
    public Bitmap takenPictureData;
    private DataBaseHelper myDbHelper;

    public DialogOk myDialOk;
    public DialogKo myDialKo;
    public DialogLoading myDialLoading;

    public String urlAlerte = "http://appvilledelyon.c2is.fr/contact.php?version=4";
//    public String urlAlerte = "http://c2is:c2is@prep.c2is.fr/appvilledelyon/current/contact.php?version=4";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);
        tracker = analytics.newTracker(getResources().getString(R.string.google_analytics_id));
        tracker.setScreenName("/Formulaire alerte");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());

        myDialLoading = new DialogLoading(this);
        myDialOk      = new DialogOk(this);
        myDialKo      = new DialogKo(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_alerte);
        FacebookSdk.sdkInitialize(getApplicationContext());

        Config.myActu = this;

        Typeface myTypeface = Typeface.createFromAsset(Config.myHome.getAssets(), "Oswald-Regular.ttf");
        TextView titre = (TextView) findViewById(R.id.titre);
        titre.setTypeface(myTypeface);

        titre.setText(Config.myLabel);

        layObligatoire = (TextView) findViewById(R.id.layObligatoire);
        TextView titreKo = (TextView) findViewById(R.id.titreConfKo);
        titreKo.setTypeface(myTypeface);

        RadioButton radio_monsieur  = (RadioButton) findViewById(R.id.radio_monsieur);
        radio_monsieur.setTypeface(myTypeface);
        RadioButton radio_madame    = (RadioButton) findViewById(R.id.radio_madame);
        radio_madame.setTypeface(myTypeface);

        chpNom      = (EditText) findViewById(R.id.chpNom);
        chpPrenom   = (EditText) findViewById(R.id.chpPrenom);
        chpEmail    = (EditText) findViewById(R.id.chpEmail);
        chpMessage  = (EditText) findViewById(R.id.chpMessage);
        chpNumero   = (EditText) findViewById(R.id.chpNumero);
        chpRue      = (EditText) findViewById(R.id.chpRue);
        chpCp       = (EditText) findViewById(R.id.chpCp);
        chpVille    = (EditText) findViewById(R.id.chpVille);
        chpTelephone= (EditText) findViewById(R.id.chpTelephone);
        layImage    = (LinearLayout) findViewById(R.id.layImage);
        libLocalisation = (TextView) findViewById(R.id.libLocalisation);

        chpNom.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) { onChampSelect(v); }
        });
        chpPrenom.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) { onChampSelect(v); }
        });
        chpEmail.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) { onChampSelect(v); }
        });
        chpMessage.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) { onChampSelect(v); }
        });
        chpNumero.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) { onChampSelect(v); }
        });
        chpRue.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) { onChampSelect(v); }
        });
        chpCp.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) { onChampSelect(v); }
        });
        chpVille.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) { onChampSelect(v); }
        });
        chpTelephone.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) { onChampSelect(v); }
        });

        layRadio    = (LinearLayout) findViewById(R.id.layRadio);
        checkChamps();

        libLocalisation.setTypeface(myTypeface);

        imgPreview = (ImageView) findViewById(R.id.imgPreview);

        ImageView myLogo = (ImageView) findViewById(R.id.logo);
        // ACTION DES BOUTONS DE LA HOME
        myLogo.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        startActivity(new Intent(FormAlerte.this, MainActivity.class));
                    }
                }
        );
        ImageButton myBtFavoris = (ImageButton) findViewById(R.id.bt_favoris);
        myBtFavoris.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(FormAlerte.this, favoris.class);
                        startActivityForResult(intent, 2);
                    }
                }
        );

        ImageButton myBtParam = (ImageButton) findViewById(R.id.bt_param);
        myBtParam.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(FormAlerte.this, Parametre.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent, 0);

                    }
                }
        );

        ImageButton btFermer = (ImageButton) findViewById(R.id.bt_fermer);
        btFermer.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        finish();
                    }
                }
        );

        btValiderChargement = (TextView) findViewById(R.id.btValiderChargement);
        btValiderChargement.setTypeface(myTypeface);

        btValider = (Button) findViewById(R.id.btValider);
        btValider.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        goValider();
                    }
                }
        );


        Button btPhoto1 = (Button) findViewById(R.id.btPhoto1);
        btPhoto1.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        dispatchTakePictureIntent();
                    }
                }
        );

        Button btPhoto2 = (Button) findViewById(R.id.btPhoto2);
        btPhoto2.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (Build.VERSION.SDK_INT <= 19) {
                            Intent i = new Intent();
                            i.setType("image/*");
                            i.setAction(Intent.ACTION_GET_CONTENT);
                            i.addCategory(Intent.CATEGORY_OPENABLE);
                            startActivityForResult(i, 10);

                        } else if (Build.VERSION.SDK_INT > 19) {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, 10);
                        }
                    }
                }
        );

        myLayConfKo     = (LinearLayout) findViewById(R.id.myLayConfKo);
        //myLayConfOk     = (LinearLayout) findViewById(R.id.myLayConfOk);
        myScrollForm    = (ScrollView) findViewById(R.id.myScrollForm);

        sharedPref      = this.getSharedPreferences("vdl", Context.MODE_WORLD_WRITEABLE);

        int flag_geoloc = sharedPref.getInt("flag_geoloc", 1);
        if (flag_geoloc==1) {
            geocoder = new Geocoder(this, Locale.getDefault());

            GPSTracker tracker = new GPSTracker(this);
            if (!tracker.canGetLocation()) {
                tracker.showSettingsAlert();
            } else {
                latitude = tracker.getLatitude();
                longitude = tracker.getLongitude();
            }

            myAsyncTask3 myWebFetch = new myAsyncTask3();
            myWebFetch.execute();
        }

        myDbHelper = new DataBaseHelper(Config.myHome.getBaseContext());
        try {
            myDbHelper.openDataBase();
        }catch(SQLException sqle){
            throw sqle;
        }

        Cursor c = myDbHelper.loadUtilisateur();

        try {
            if (c != null) {
                while (c.moveToNext()) {
                    chpNom.setText(c.getString(0));
                    chpPrenom.setText(c.getString(1));
                    chpEmail.setText(c.getString(2));
                    chpTelephone.setText(c.getString(3));
                }
            }
        }catch(SQLException sqle){
            throw sqle;
        }

    }

    public void checkChamps() {
        if (Config.form_civilite.equals("1")) {
            layRadio.setVisibility(View.VISIBLE);
        }else{
            layRadio.setVisibility(View.GONE);
        }
        if (Config.form_nom.equals("1")) {
            chpNom.setVisibility(View.VISIBLE);
        }else{
            chpNom.setVisibility(View.GONE);
        }
        if (Config.form_prenom.equals("1")) {
            chpPrenom.setVisibility(View.VISIBLE);
        }else{
            chpPrenom.setVisibility(View.GONE);
        }
        if (Config.form_tel.equals("1")) {
            chpTelephone.setVisibility(View.VISIBLE);
        }else{
            chpTelephone.setVisibility(View.GONE);
        }
        if (Config.form_email.equals("1")) {
            chpEmail.setVisibility(View.VISIBLE);
        }else{
            chpEmail.setVisibility(View.GONE);
        }
        if (Config.form_message.equals("1")) {
            chpMessage.setVisibility(View.VISIBLE);
        }else{
            chpMessage.setVisibility(View.GONE);
        }
        if (Config.form_image.equals("1")) {
            layImage.setVisibility(View.VISIBLE);
        }else{
            layImage.setVisibility(View.GONE);
        }

        if (Config.form_loc.equals("1")) {
            chpNumero.setVisibility(View.VISIBLE);
            chpRue.setVisibility(View.VISIBLE);
            chpCp.setVisibility(View.VISIBLE);
            chpVille.setVisibility(View.VISIBLE);
            libLocalisation.setVisibility(View.VISIBLE);
        }else{
            chpNumero.setVisibility(View.GONE);
            chpRue.setVisibility(View.GONE);
            chpCp.setVisibility(View.GONE);
            chpVille.setVisibility(View.GONE);
            libLocalisation.setVisibility(View.GONE);
        }

    }


    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    private Bitmap handleResultFromChooser(Intent data) {
        takenPictureData = null;

        Uri photoUri = data.getData();

        monImage = getRealPathFromUri(getApplicationContext(),photoUri);

        if (photoUri != null) {
            try {
              takenPictureData = getBitmapFromUri(photoUri);


            } catch (Exception e) {

            }
        }

        return takenPictureData;
    }

    public void onChampSelect(View view) {
        EditText myText = (EditText) view;
        myText.setBackgroundColor(getResources().getColor(R.color.gris));
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_monsieur:
                if (checked)
                    flagCiv = 1;
                    break;
            case R.id.radio_madame:
                if (checked)
                    flagCiv = 2;
                    break;
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {

        /*
        File file = new File(uri.getPath());

        if (file != null) {
            Log.d("myTag", "toto toto");
        }
        */


        ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();


        Bitmap image = null;
        if (fileDescriptor!=null) {
            if (fileDescriptor.valid()) {
                try {
                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;

                    image = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
                    return image;

                } catch (Exception e) {
                    return null;
                }
            } else {
                return null;
            }
        }

        parcelFileDescriptor.close();

        return image;
    }

    public void loadImageToGalerie() {
        try {
            Bitmap thumbnail = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

            imgPreview.setImageBitmap(thumbnail);
            takenPictureData = thumbnail;

            monImage = getRealPathFromUri(getApplicationContext(),imageUri);

            String filename = "alerte-ville-de-lyon.png";
            File sd = Environment.getExternalStorageDirectory();

            String extr = Environment.getExternalStorageDirectory().toString();
            extr = extr + "/Pictures/";

            File dest = new File(extr, filename);
            FileOutputStream out = new FileOutputStream(dest);
            thumbnail.compress(Bitmap.CompressFormat.PNG, 70, out);
            out.flush();
            out.close();
        } catch (Exception et) {
            Log.d("myTag", ">> je encore faux " + et.getMessage());
            et.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {


            try {
                takenPictureData = null;

                takenPictureData = handleResultFromChooser(data);
                imgPreview.setImageBitmap(takenPictureData);
                actuBitmap = takenPictureData;

            } catch (Exception e) {
                // AYMERIC
                try {
                    myAsyncTask4 myWebFetch4 = new myAsyncTask4();
                    myWebFetch4.execute();

                } catch (Exception et) {
                    et.printStackTrace();
                }

            }

            flagPhoto = 1;


        } else if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 10) {
                takenPictureData = null;
                takenPictureData = handleResultFromChooser(data);

                imageUri = data.getData();

                imgPreview.setImageBitmap(takenPictureData);
                actuBitmap = takenPictureData;
                flagPhoto = 1;
            }
        }

    }

    public String getRealPathFromURI(Uri uri) {
        if (uri == null) {
            return null;
        }
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }


    public void goValider() {
        goAlertePhp();
    }

    public void showOk() {
        Config.myFragDemarche.showPopUp();
        finish();

//        myDialOk.show();
  //      btValider.setVisibility(View.VISIBLE);
    }

    public void showKo() {
        myDialKo.show();
        btValider.setVisibility(View.VISIBLE);
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
/*
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
*/
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContentResolver().insert(Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);


    }

    public int checkFormatMail(String p_param) {
        int flag = 0;

        String[] myTab = p_param.split("@");

        if (myTab.length>1) {
            String[] myTab2 = myTab[1].split("\\.");

            if (myTab2.length>1) {
                if (myTab2[1].toString().length()>1) {
                    flag = 1;
                }
            }
        }

        return flag;
    }

    public void goAlertePhp() {
        strNom       = chpNom.getText().toString();
        strPrenom    = chpPrenom.getText().toString();
        strEmail     = chpEmail.getText().toString();
        strMessage   = chpMessage.getText().toString();
        strNumero    = chpNumero.getText().toString();
        strRue       = chpRue.getText().toString();
        strCp        = chpCp.getText().toString();
        strVille     = chpVille.getText().toString();
        strTelephone = chpTelephone.getText().toString();

        int flag = 1;

        if ((strNom.length()==0)&&(Config.form_nom.equals("1"))) {
            chpNom.setBackgroundColor(getResources().getColor(R.color.rouge));
            flag=0;
        }
        if ((strPrenom.length()==0)&&(Config.form_prenom.equals("1"))) {
            chpPrenom.setBackgroundColor(getResources().getColor(R.color.rouge));
            flag=0;
        }
        if ((strEmail.length()==0)&&(Config.form_email.equals("1"))) {
            chpEmail.setBackgroundColor(getResources().getColor(R.color.rouge));
            flag=0;
        }
        if ((strMessage.length()==0)&&(Config.form_message.equals("1"))) {
            chpMessage.setBackgroundColor(getResources().getColor(R.color.rouge));
            flag=0;
        }
        if (flagPhoto==0) {
            imgPreview.setBackgroundColor(getResources().getColor(R.color.rouge));
            flag=0;
        }


        String strEmail = chpEmail.getText().toString();

        final String EMAIL_PATTERN = "^[_'-A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@['-A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

//        if (!strEmail.matches(EMAIL_PATTERN)) {
        if (checkFormatMail(strEmail)==0) {
            flag=2;
            chpEmail.setBackgroundColor(getResources().getColor(R.color.rouge));
        }

        if (Config.form_loc.equals("1")) {
            if (strNumero.length() == 0) {
                chpNumero.setBackgroundColor(getResources().getColor(R.color.rouge));
                flag = 0;
            }
            if (strRue.length() == 0) {
                chpRue.setBackgroundColor(getResources().getColor(R.color.rouge));
                flag = 0;
            }
            if (strCp.length() == 0) {
                chpCp.setBackgroundColor(getResources().getColor(R.color.rouge));
                flag = 0;
            }
            if (strVille.length() == 0) {
                chpVille.setBackgroundColor(getResources().getColor(R.color.rouge));
                flag = 0;
            }
        }

        if ((strTelephone.length()==0)&&(Config.form_tel.equals("1"))) {
            chpTelephone.setBackgroundColor(getResources().getColor(R.color.rouge));
            flag=0;

        }

        if (flag==1) {
            if (flagPhoto==1) {
                int bitmapByteCount = 0;
                int bytesAvailable  = 0;

                try {
                    monImage = getRealPathFromUri(getApplicationContext(),imageUri);

                    FileInputStream fileInputStream = new FileInputStream(monImage);
                    bytesAvailable = fileInputStream.available();

                } catch (Exception e) {
                    Log.d("myTag", "exep : " + e.toString());
                    e.printStackTrace();
                }

                Log.d("myTag", ">> imageUri 2 : " + imageUri);
                Log.d("myTag", ">> StickerToSend 2 : " + bytesAvailable);


                //if (bytesAvailable > 2691000) {      // supérieur à 3 mega 3229200
                if (bytesAvailable >  ((2691000*10)/7)) {      // supérieur à 3 mega 3229200
                    new AlertDialog.Builder(this)
                            .setIcon(R.drawable.ic_notif)
                            .setTitle("Attention")
                            .setMessage("La taille des images est limitée à 2.5 Mo.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }

                            })
                            .show();

                } else {

                    myDialLoading.show();

                    btValider.setVisibility(View.GONE);

                    ContentValues myValue = new ContentValues();
                    myValue.put("nom",strNom);
                    myValue.put("prenom",strPrenom);
                    myValue.put("email",strEmail);
                    myValue.put("telephone", strTelephone);

                    myDbHelper.updateUtilisateur(myValue);

                    myAsyncTask2 myWebFetch = new myAsyncTask2();
                    myWebFetch.execute();
                }
            }else{
                myDialLoading.show();

                btValider.setVisibility(View.GONE);

                ContentValues myValue = new ContentValues();
                myValue.put("nom",strNom);
                myValue.put("prenom",strPrenom);
                myValue.put("email",strEmail);
                myValue.put("telephone", strTelephone);

                myDbHelper.updateUtilisateur(myValue);

                myAsyncTask2 myWebFetch = new myAsyncTask2();
                myWebFetch.execute();
            }
        }else{
            if (flag==2) {
                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_notif)
                        .setTitle("Attention")
                        .setMessage("Votre email comporte des erreurs.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }

                        })
                        .show();
            }else {
                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_notif)
                        .setTitle("Attention")
                        .setMessage("Tous les champs suivis d'une étoile sont obligatoires.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }

                        })
                        .show();
            }
        }
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
        Config.myActu = this;
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    public void majLocalisation() {
        chpNumero.setText   (strNumero);
        chpRue.setText      (strRue);
        chpCp.setText       (strCp);
        chpVille.setText    (strVille);
    }

    public String getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return path;
    }

    class myAsyncTask2 extends AsyncTask<List<NameValuePair>, Integer, String> {
        myAsyncTask2()    {

        }

        int flagOk;

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            myDialLoading.hide();

            if (flagOk==1) {
                showOk();
            }else{
                showKo();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            flagOk = 1;
        }


        @Override
        protected String doInBackground(List<NameValuePair>... nameValuePairs) {
            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                try {

                    String strCiv = "Monsieur";
                    if (flagCiv==2) {
                        strCiv = "Madame";
                    }

                    String myUrl = urlAlerte;

                    if (Config.form_civilite.equals("1")) {
                        myUrl = myUrl + "&civilite=" + strCiv;
                    }
                    if (Config.form_nom.equals("1")) {
                        myUrl = myUrl + "&nom=" + strNom;
                    }
                    if (Config.form_prenom.equals("1")) {
                        myUrl = myUrl + "&prenom=" + strPrenom;
                    }
                    if (Config.form_tel.equals("1")) {
                        myUrl = myUrl + "&mobile=" + strTelephone;
                    }
                    if (Config.form_email.equals("1")) {
                        myUrl = myUrl + "&email=" + strEmail;
                    }
                    if (Config.form_message.equals("1")) {
                        myUrl = myUrl + "&description=" + strMessage;
                    }
                    if (Config.form_loc.equals("1")) {
                        myUrl = myUrl + "&num_rue=" + strNumero;
                        myUrl = myUrl + "&rue=" + strRue;
                        myUrl = myUrl + "&code_postal=" + strCp;
                        myUrl = myUrl + "&ville=" + strVille;
                    }

                    myUrl = myUrl + "&id="          + Config.myId;
                    myUrl = myUrl + "&type="        + Config.myType;


                    DataOutputStream dos = null;
                    String lineEnd = "\r\n";
                    String twoHyphens = "--";
                    String boundary = "*****";
                    int bytesRead, bytesAvailable, bufferSize;
                    byte[] buffer;
                    int maxBufferSize = 1 * 1024 * 1024;


                        URL url = new URL(urlAlerte);

                        // Open a HTTP  connection to  the URL
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true); // Allow Inputs
                        conn.setDoOutput(true); // Allow Outputs
                        conn.setUseCaches(false); // Don't use a Cached Copy
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                        dos = new DataOutputStream(conn.getOutputStream());


                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                    if (Config.form_civilite.equals("1")) {
                        dos.writeBytes("Content-Disposition: form-data; name=\"civilite\"" + lineEnd);
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(strCiv);
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                    }

                    if (Config.form_nom.equals("1")) {
                        dos.writeBytes("Content-Disposition: form-data; name=\"nom\"" + lineEnd);
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(URLEncoder.encode(strNom, "utf-8"));
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                    }
                    if (Config.form_prenom.equals("1")) {
                        dos.writeBytes("Content-Disposition: form-data; name=\"prenom\"" + lineEnd);
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(URLEncoder.encode(strPrenom, "utf-8"));
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                    }
                    if (Config.form_tel.equals("1")) {
                        dos.writeBytes("Content-Disposition: form-data; name=\"mobile\"" + lineEnd);
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(strTelephone);
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                    }
                    if (Config.form_email.equals("1")) {
                        dos.writeBytes("Content-Disposition: form-data; name=\"email\"" + lineEnd);
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(strEmail);
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                    }
                    if (Config.form_message.equals("1")) {
                        dos.writeBytes("Content-Disposition: form-data; name=\"description\"" + lineEnd);
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(URLEncoder.encode(strMessage, "utf-8"));
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                    }

                    dos.writeBytes("Content-Disposition: form-data; name=\"id\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(Config.myId);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                    dos.writeBytes("Content-Disposition: form-data; name=\"type\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(Config.myType);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                    dos.writeBytes("Content-Disposition: form-data; name=\"flagAndroid\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes("1");
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);


                    if (Config.form_loc.equals("1")) {
                        dos.writeBytes("Content-Disposition: form-data; name=\"num_rue\"" + lineEnd);
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(URLEncoder.encode(strNumero, "utf-8"));
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"rue\"" + lineEnd);
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(URLEncoder.encode(strRue, "utf-8"));
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"code_postal\"" + lineEnd);
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(URLEncoder.encode(strCp, "utf-8"));
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"ville\"" + lineEnd);
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(URLEncoder.encode(strVille, "utf-8"));
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                    }

                    if (monImage.length()>0) {
                        Log.d("myTag", "upload de l'imae : " + monImage);

                        FileInputStream fileInputStream = new FileInputStream(monImage);


                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"userfile\";filename=\"image.jpeg\"" + lineEnd);
                        dos.writeBytes(lineEnd);

                        BitmapFactory.decodeStream(fileInputStream).compress(Bitmap.CompressFormat.JPEG, 70, dos);

                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        buffer = new byte[bufferSize];

                        // Read file
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);


                        while (bytesRead > 0) {
                            dos.write(buffer, 0, bufferSize);
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                        }

                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);


                        fileInputStream.close();
                    }

                    int serverResponseCode       = conn.getResponseCode();
                    String serverResponseMessage = conn.getResponseMessage();

                    if (serverResponseMessage.equalsIgnoreCase("OK")) {
                        flagOk = 1;
                    }else{
                        flagOk = 0;
                    }

                    Log.d("myTag", "serverResponseMessage : " + serverResponseMessage);

                    dos.flush();


                } catch (Exception e) {
                    flagOk = 0;
                    Log.d("myTag", "exep : " + e.toString());
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                return null;


            } catch (Exception e) {
                flagOk = 0;
                Log.d("myTag", "exep : " + e.toString());
                //	Toast.makeText(Config.myResVehicule,"erreur : " + e.toString(), Toast.LENGTH_LONG).show();
            }

            return "";
        }
    }


    class myAsyncTask3 extends AsyncTask<Void, Void, Void> {
        public ArrayList<String> addressFragments;

        myAsyncTask3()    {

        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            majLocalisation();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String errorMessage = "";

            List<Address> addresses = null;

            try {
                addresses = geocoder.getFromLocation(
                        latitude,
                        longitude,
                        1);
            } catch (IOException ioException) {
                // Catch network or other I/O problems.
                Log.d("myTag", "This is my ioException");

            } catch (IllegalArgumentException illegalArgumentException) {
                // Catch invalid latitude or longitude values.
                Log.d("myTag", "This is invalid latitude or longitude");
            }

            // Handle case where no address was found.
            if (addresses == null || addresses.size()  == 0) {
                if (errorMessage.isEmpty()) {
                    Log.d("myTag", "pas trouvé l'adresse");
                }
               //deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
            } else {
                Address address  = addresses.get(0);
                addressFragments = new ArrayList<String>();

                // Fetch the address lines using getAddressLine,
                // join them, and send them to the thread.
                for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    addressFragments.add(address.getAddressLine(i));

                    String[] separated = address.getAddressLine(i).split(" ");
                    if (i==0) {
                        strNumero = separated[0];
                        strRue    = address.getAddressLine(i).replace(separated[0], "");
                    }else{
                        strCp    = separated[0];
                        strVille = address.getAddressLine(i).replace(separated[0], "");
                    }


                }

            }

            return null;
        }
    }

    class myAsyncTask4 extends AsyncTask<Void, Void, Void> {
        public ArrayList<String> addressFragments;

        myAsyncTask4()    {

        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            loadImageToGalerie();
            return null;
        }
    }
}
