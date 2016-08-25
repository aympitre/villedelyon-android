package air.com.c2is.villedelyon;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


public class ListType extends android.support.v4.app.FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_type);

        FacebookSdk.sdkInitialize(getApplicationContext());

        Log.d("myTag", "CODE_DE_MON_ACTIVITE : " + Config.CODE_DE_MON_ACTIVITE);

        Config.myActu  =this;
        Config.myFragment   = this;
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "Oswald-Regular.ttf");
        TextView myTitre    = (TextView) findViewById(R.id.titre);

        TextView myChargementText   = (TextView) findViewById(R.id.myChargementText);
        myChargementText.setTypeface(myTypeface);

        String myChaine = "";


        if (Config.CODE_DE_MON_ACTIVITE==1) {
            myChaine = getResources().getString(R.string.libHomeBt1);
            if (Config.codeInterne==1) {
                Log.d("myTag", "codeInterne 1 ");
                loadFragment(getResources().getString(R.string.sqlType1_1));
                Config.sql_type = getResources().getString(R.string.sqlType1_1);
            }else if (Config.codeInterne==3) {
                Log.d("myTag", "codeInterne 3 ");
                loadFragment(getResources().getString(R.string.sqlType1_3));
                Config.sql_type = getResources().getString(R.string.sqlType1_3);
            }else if (Config.codeInterne==4) {
                Log.d("myTag", "codeInterne 4 ");
                Config.sql_type = "marches";
            }else if (Config.MENU_ACTIVITE==2) {
                Log.d("myTag", "MENU_ACTIVITE 2 ");
                Config.fragToReload  = getResources().getString(R.string.sqlType1_2);
                loadFragment(getResources().getString(R.string.sqlType1_2));
            }



            Config.codeInterne = 1;

        }else if (Config.CODE_DE_MON_ACTIVITE==3) {
            myChaine = getResources().getString(R.string.libHomeBt3);

            if (Config.MENU_ACTIVITE==1) {
                Config.flagShowCarto = 1;
                Config.fragToReload  = getResources().getString(R.string.sqlType3_1);

                Config.myFragment.loadFragment(getResources().getString(R.string.sqlType3_1));


            }else if (Config.MENU_ACTIVITE==2) {
                Config.fragToReload  = getResources().getString(R.string.sqlType3_2);
                Config.myFragment.loadFragment(getResources().getString(R.string.sqlType3_2));
            }else if (Config.MENU_ACTIVITE==3) {
                Config.fragToReload  = getResources().getString(R.string.sqlBalade);
                Config.myFragment.loadFragment(getResources().getString(R.string.sqlBalade));
            }

        }else if (Config.CODE_DE_MON_ACTIVITE==5) {
            myChaine = getResources().getString(R.string.libHomeBt5);

            Log.d("myTag", "codeinterne : " + Config.codeInterne);

            if (Config.codeInterne==2) {
                loadRechercheEvenement();
            }else if (Config.codeInterne==3) {
                Config.fragToReload = getResources().getString(R.string.sqlIncontournable);
                Config.myFragment.loadFragment(getResources().getString(R.string.sqlIncontournable));
            }else {
                Config.flagFromRecherche = 0;
                loadEvenement();
            }

            Config.codeInterne = 1;


        }else if (Config.CODE_DE_MON_ACTIVITE==6) {
            myChaine = getResources().getString(R.string.libHomeBt6);

            Config.sql_sous_type = Config.fragToReload = getResources().getString(R.string.sqlType6_1);
            Config.myFragment.loadFragment(getResources().getString(R.string.sqlType6_1));

            //Config.resetFragment();

        }




        myTitre.setText(myChaine);
        myTitre.setTypeface(myTypeface);

        // Bouton retour home
        ImageView myLogo = (ImageView) findViewById(R.id.logo);
        // ACTION DES BOUTONS DE LA HOME
        myLogo.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Config.flagRetourRecherche  = 0;
                        startActivity(new Intent(ListType.this, MainActivity.class));
                    }
                }
        );

        ImageButton myBtMenu = (ImageButton) findViewById(R.id.bt_menu);
        myBtMenu.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Config.flagRetourRecherche  = 0;
                        startActivity(new Intent(ListType.this, MainActivity.class));
                    }
                }
        );

        ImageButton myBtParam   = (ImageButton) findViewById(R.id.bt_param);
        ImageButton myBtFavoris = (ImageButton) findViewById(R.id.bt_favoris);
        myBtParam.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Config.flagRetourRecherche  = 0;
                        Intent intent = new Intent(ListType.this, Parametre.class);
                        startActivityForResult(intent, 2);
                    }
                }
        );
        myBtFavoris.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Config.flagRetourRecherche  = 0;
                        Intent intent = new Intent(ListType.this, favoris.class);
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
                        //                            stopReveil();
                        //                          killAlarme();

                        Config.flagDirectSavoir = 1;

                        Intent intent = new Intent(ListType.this, Reveil.class);
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

    public void loadDemarche(String p_param) {
        Config.flagDemarche = 1;
        Config.str_demarche = p_param;

        ListTypeFragmentDemarche fragment2 = new ListTypeFragmentDemarche();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment2).commit();

    }

    public void loadFormAlert() {
        Intent intent = new Intent(ListType.this, FormAlerte.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, 0);
    }

    public void loadFragment(String p_param) {
        Log.d("myTag", "loadFragment : " + p_param);

        Config.flagFragment = 0;

        try {
            if (p_param.length()>0)
            {
                Config.sql_type = p_param;
                Config.flagFragment = 1;
            }else{
                if (Config.fragToReload.length()>0) {
                    Config.sql_type = Config.fragToReload;
                }
            }

            Log.d("myTag", "mon parametre : " + p_param);

            if (p_param.equals("marches")&&(Config.flagDirectMarche==0)) {
                Intent intent = new Intent(ListType.this, RechercheMarcheFragment.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, 0);
            }else if (p_param.equals("label-lyon-ville-equitable-et-durable-vie-quotidienne")&&(Config.flagDirectMarche==0)) {
                Intent intent = new Intent(ListType.this, RechercheVieFragment.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, 0);
            }else{
                Config.flagDirectMarche = 0;
                ListTypeFragment fragment2 = new ListTypeFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment2).commit();
            }
        } catch (Exception e) {
        }

    }

    public void loadBalade() {
        Config.flagFragment = 1;
        FragmentDetailBalade fragment2 = new FragmentDetailBalade();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment2).commit();
    }

    public void loadIncontrounable() {
        Config.flagFragment = 2;
        Intent intent = new Intent(ListType.this, FragmentDetailIncontournable.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, 0);
    }

    public void loadEquipement() {
        Config.flagFragment = 1;

        Intent intent = new Intent(ListType.this, FragmentDetailEquipement.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, 0);
    }

    public void loadEvenement() {
        Config.flagFragment = 3;
        ListTypeFragmentEvt fragment2 = new ListTypeFragmentEvt();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment2).commit();
    }

    public void loadDetailEvenement() {
        Config.flagFragment = 4;
        Intent intent = new Intent(ListType.this, FragmentDetailEvt.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, 0);
    }

    public void loadRechercheEvenement() {
        Config.flagFragment = 5;
        RechercheFragment fragment2 = new RechercheFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment2).commit();
    }

    public void loadCarto() {
        Config.flagFragment = 6;

        Intent intent = new Intent(ListType.this, FragmentCarte.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, 0);
    }

    public void loadCartoBalade() {
        Config.flagFragment = 6;

        Intent intent = new Intent(ListType.this, FragmentCarteBalade.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, 0);
    }


    @Override
    public void onBackPressed(){
        Log.d("myTag", "mon menu : " + Config.MENU_ACTIVITE);
        Log.d("myTag", "mon sous type : " + Config.sql_sous_type);
        Log.d("myTag", "mon flag to reload : " + Config.fragToReload);
        Log.d("myTag", "mon str_demarche : " + Config.str_demarche);
        Log.d("myTag", "mon flagRetourRecherche : " + Config.flagRetourRecherche);

        if (Config.str_demarche.length()>0) {
            Config.flagRetourRecherche = 0;

            Log.d("myTag", "CODE_DE_str_demarche : " + Config.str_demarche);
            if (Config.MENU_ACTIVITE == 1) {
                if (Config.flagForceRetour==1) {
                    Config.sql_sous_type = Config.fragToReload = getResources().getString(R.string.sqlType6_1);
                    Config.myFragment.loadFragment(getResources().getString(R.string.sqlType6_1));
                    Config.flagForceRetour = 0;
                }else{
                    Config.fragToReload = "";
                    Config.flagForceRetour = 0;
                    finish();
                }

            } else if (Config.MENU_ACTIVITE == 2) {
                if (Config.flagForceRetour==1) {
                    Config.sql_sous_type = Config.fragToReload = getResources().getString(R.string.sqlType6_2);
                    Config.myFragment.loadFragment(getResources().getString(R.string.sqlType6_2));
                    Config.flagForceRetour = 0;
                }else{
                    Config.fragToReload = "";
                    Config.flagForceRetour = 0;
                    finish();
                }
            } else {
                if (Config.flagForceRetour==1) {
                    Config.sql_sous_type = Config.fragToReload = getResources().getString(R.string.sqlType6_3);
                    Config.myFragment.loadFragment(getResources().getString(R.string.sqlType6_3));
                    Config.str_demarche = "";
                    Config.flagForceRetour = 0;
                }else{
                    Config.fragToReload = "";
                    Config.flagForceRetour = 0;
                    finish();
                }
            }
        }else if (Config.fragToReload.length()>0) {
            Log.d("myTag", "CODE_DE_fragToReload : " + Config.fragToReload);
            Config.flagRetourRecherche = 0;

            if (    (Config.fragToReload.equals(getResources().getString(R.string.sqlType3_2))) ||
                    (Config.fragToReload.equals(getResources().getString(R.string.sqlBalade))) ||
                    (Config.fragToReload.equals(getResources().getString(R.string.sqlType1_2))) ||
                    (Config.fragToReload.equals(getResources().getString(R.string.sqlType1_3)))
                    ) {

                if (Config.flagForceRetour==1) {
                    Config.myFragment.loadFragment(Config.fragToReload);
                    Config.fragToReload = "";
                    Config.flagForceRetour = 0;
                }else{
                    Config.fragToReload = "";
                    Config.flagForceRetour = 0;
                    finish();
                }

            }else{

                finish();
            }
        }else {

            Log.d("myTag", "CODE_DE_MON_RETOUR : " + Config.flagBisRetour);

            if (Config.flagBisRetour==1) {
                Config.flagBisRetour = 0;

                Config.myFragment.loadFragment(getResources().getString(R.string.sqlType1_2));
                Config.fragToReload         = "";
                Config.flagForceRetour      = 0;
                Config.flagRetourRecherche  = 0;

            }else if (Config.flagBisRetour==2) {
                Config.flagBisRetour=0;

                Config.myFragment.loadFragment(getResources().getString(R.string.sqlType1_3));
                Config.fragToReload         = "";
                Config.flagForceRetour      = 0;
                Config.flagRetourRecherche  = 0;

            }else if (Config.flagBisRetour==3) {
                Config.flagBisRetour=0;

                Config.myFragment.loadFragment(getResources().getString(R.string.sqlType3_2));
                Config.fragToReload         = "";
                Config.flagForceRetour      = 0;
                Config.flagRetourRecherche  = 0;

            }else if (Config.flagRetourRecherche==1) {
                Config.flagRetourRecherche = 0;
                loadRechercheEvenement();
            }else {
                Config.flagRetourRecherche = 0;
                finish();
            }
        }
    }
}
