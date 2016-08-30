package air.com.c2is.villedelyon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.media.MediaPlayer;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

public class Config {
	public static int 	 VERSION_API			= 4;
	public static int 	 LIMIT_EVENT			= 50;
	public static int 	 LIMIT_ACTU				= 50;
	public static int 	 CODE_DE_MON_ACTIVITE 	= 1;
	public static int 	 MENU_ACTIVITE 			= 1;
	public static String NBE_ANNONCE 			= "";
	public static int 	 FLAG_REVEIL_SAVOIR 	= 0;

	public static Activity		myActu;
	public static MainActivity 	myHome;
	public static ListType 		myFragment;
	public static favoris 		myFavoris;
	public static Parametre 	myParam;
	public static String tabImage[];
	public static String sql_type;
	public static String sql_sous_type = "";
	public static String wait_sous_type = "";
	public static String xml_id = "";
	public static int flagRetourRecherche = 0;
	public static int flagFragment 		= 0;
	public static int flagShowCarto 	= 0;
	public static int flagDirectMarche 	= 0;
	public static int flagDirectSavoir 	= 0;
	public static int flag_tri_geoloc	= 0;
	public static long reveilDiff		= 0;
	public static int flag_is_playing	= 0;

	public static String visuel_pub;
	public static String url_pub;
	public static String id_pub ="";
	public static int    time_pub;

	public static int flagRelanceCompteur = 0;

	public static int str_marche_arrondissement 	= 0;
	public static String str_marche_theme 			= "";
	public static String str_marche_jour 			= "";

	public static String msg_notification			= "";

	public static MediaPlayer mp;

	public static Reveil myReveil;
	public static Actualite myActualite;
	public static Parametre myParametre;
	public static ResMarche myResMarche;
	public static SeDeplacer mySeDeplacer;

	public static String titreActu = "";
	public static String urlActu   = "";

	public static FragmentCarte 				myFragCarte;
	public static FragmentDetailEquipement 		myDetailEquip;
	public static FragmentCarteBalade 			myCarteBalade;
	public static FragmentDetailEvt 			myDetailEvt;
	public static FragmentDetailIncontournable 	myDetailIncontournable;
	public static LeSaviezVousDetail 			mySaviezDetail;
	public static LeSaviezVous 					mySaviezVous;
	public static FragmentDetailBalade 			myDetailBalade;

	public static String fragToReload 	= "";
	public static ArrayList<HashMap<String, Object>> pointCarto;

	public static HashMap<String, Object> myContentValue;

	public static String urlMeteo = "http://www.meteorologic.net/webmaster/xml/xml_file_27595.xml";

	public static  int flagOffReveil		= 0;
	public static  int flagFromRecherche	= 0;
	public static  int flagAlarm 			= 0;
	public static  int flagFirst 			= 0;
	public static  int flagBddExist 		= 0;
	public static  int flagDemarche 		= 0;
	public static  int flagForceRetour 		= 0;
	public static  int flagActivePreprod	= 1;
	public static  String str_demarche 		= "";

	public static  int 		codeInterne		 = 1;
	public static  int 		flagRechercheEvt = 0;
	public static  int 		flagBisRetour	 = 0;
	public static  String 	str_titre_evt 	 = "";
	public static  Date 	date_evt;
	public static  String 	str_date_evt 	 = "";

	public static String actu_titre;
	public static String actu_telephone;
	public static String titre_savoir;
	public static String texte_savoir;

	public static String myLabel 		 = "";
	public static String myId 			 = "";
	public static String myType 		 = "";

	public static String form_civilite	= "";
	public static String form_nom		= "";
	public static String form_prenom	= "";
	public static String form_tel		= "";
	public static String form_email		= "";
	public static String form_message	= "";
	public static String form_image		= "";
	public static String form_loc		= "";

	public static String myDemarcheTitre = "";
	public static String myDemarcheDesc  = "";

	public static int flagContentEquip 	= 0;
	public static int flagEvtFromFav	= 0;

	public static ContentValues myContentEquip;
	public static HashMap<String, Object> myCourant;

	public static void resetFragment() {
		Config.flagFragment = 0;
	}

	public static void majNbeFav(TextView p_param, Context myContext) {
		try {
			DataBaseHelper myDbHelperTemp = new DataBaseHelper(myContext);

			try {
				myDbHelperTemp.createDataBase();
			} catch (IOException ioe) {
				Log.d("myTag", "creation bdd favoris  KO");
			}
			try {
				myDbHelperTemp.openDataBase();
			}catch(SQLException sqle){
				Log.d("myTag", "ouverture bdd favoris KO");
				throw sqle;
			}

			int nbe = myDbHelperTemp.getNumFavoris();
			if (nbe==0) {
				p_param.setText("");
			}else {
				p_param.setText(String.valueOf(nbe));
			}
			// ici pour virer le favoris numero dans le header
			p_param.setText("");

			myDbHelperTemp.close();
		}catch(Exception e){
		}
	}

	public static Spanned formatLastWord(String p_param) {
		String first    = p_param;
		String next     = "";

		String tab[] =  p_param.split(" ");
		if (tab.length>1) {
			next  = tab[tab.length-1];
			first = first.replace(next,"");
		}

		Log.d("myTag", "victory : " + next);
		return Html.fromHtml(first.toUpperCase() + " <font color='#027BDA'>" + next.toUpperCase() + "</font>");
	}

	public static void showAlertNotif(Activity p_param) {
		if (Config.msg_notification.length()>0) {
			new AlertDialog.Builder(p_param)
					.setIcon(R.drawable.ic_notif)
					.setTitle("Notification Ville de Lyon")
					.setMessage(Config.msg_notification)
					.show();
			Config.msg_notification = "";
		}
	}

	public static String killHtml(String p_param) {
		String retour = p_param.replace("<br />","");
		retour = retour.replace("<br />","");
		retour = retour.replace("<strong>","");
		retour = retour.replace("</strong>","");
		retour = retour.replace("<b>","");
		retour = retour.replace("</b>","");
		retour = retour.replace("<i>","");
		retour = retour.replace("</i>","");
		retour = retour.replace("<p>","");
		retour = retour.replace("</p>","");

		return retour;
	}

	public static boolean isInteger(String s) {
		return isInteger(s,10);
	}

	public static boolean isInteger(String s, int radix) {
		if(s.isEmpty()) return false;

		for(int i = 0; i < s.length(); i++) {
			if(i == 0 && s.charAt(i) == '-') {
				if(s.length() == 1) return false;
				else continue;
			}
			if(Character.digit(s.charAt(i),radix) < 0) return false;
		}
		return true;
	}

	public static int checkDevice() {
		DisplayMetrics metrics = new DisplayMetrics();
		Config.myHome.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		float widthDpi = metrics.xdpi;
		float heightDpi = metrics.ydpi;
		int widthPixels = metrics.widthPixels;
		int heightPixels = metrics.heightPixels;
		float widthInches = widthPixels / widthDpi;
		float heightInches = heightPixels / heightDpi;
		double diagonalInches = Math.sqrt(
				(widthInches * widthInches)
						+ (heightInches * heightInches));


		if (diagonalInches >= 9) {
			//Device is a 10" tablet
			return 3;
		}
		else if (diagonalInches >= 5) {
			//Device is a 7" tablet
			return 2;
		}else{
			return 1;
		}
	}
}
