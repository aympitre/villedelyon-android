package air.com.c2is.villedelyon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import javax.xml.parsers.DocumentBuilderFactory;

public class DataBaseHelper extends SQLiteOpenHelper{
	 
    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/air.com.c2is.villedelyon/databases/";
    private static String DB_NAME = "lyon.sqlite";

    private SQLiteDatabase myDataBase; 
 
    private final Context myContext;
 
    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DataBaseHelper(Context context) {
    	//context.getFilesDir().getPath()

    	super(context, DB_NAME, null, 1);
        this.myContext = context;
    }	
 
  /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException{
 
    	boolean dbExist = checkDataBase();

		copyToSD();

    	if(dbExist){
			Config.flagBddExist = 1;
    		//do nothing - database already exist
//    		Toast.makeText(myContext,"La base de donn�e existe ", Toast.LENGTH_LONG).show();
    	}else{
			Config.flagBddExist = 0;
    		//By calling this method and empty database will be created into the default system path
               //of your application so we are gonna be able to overwrite that database with our database.
        	this.getReadableDatabase();
 
        	try {
 
    			copyDataBase();
 
    		} catch (IOException e) {
//    			Toast.makeText(myContext,"erreur copie bdd : ", Toast.LENGTH_LONG).show();

				Log.d("myTag", "Error copying database");
        		throw new Error("Error copying database");
 
        	}
    	}
 
    }
 
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){
 
    	SQLiteDatabase checkDB = null;
 
    	try{
    		String myPath = DB_PATH + DB_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);

    	}catch(SQLiteException e){
//			Toast.makeText(myContext,"bdd non existante ", Toast.LENGTH_LONG).show();
    		//database does't exist yet.
    		
    	}
 
    	if(checkDB != null){
    		checkDB.close();
 
    	}
 
    	return checkDB != null ? true : false;
    }
 
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{
 
    	//Open your local db as the input stream
    	InputStream myInput = myContext.getAssets().open(DB_NAME);


    	// Path to the just created empty db
    	String outFileName = DB_PATH + DB_NAME;
 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
 
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
 
    }
 
    public void openDataBase() throws SQLException{
 
    	//Open the database
        String myPath = DB_PATH + DB_NAME;
    	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);

//    	Toast.makeText(myContext,"ouverture : " + myPath, Toast.LENGTH_LONG).show();
    }
 
    @Override
	public synchronized void close() {
 
    	    if(myDataBase != null)
    		    myDataBase.close();
 
    	    super.close();
 
	}
 
	@Override
	public void onCreate(SQLiteDatabase db) {
 
	}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
 
	}

	public void majParametre(int p_type, int p_valeur) {
		if (p_type==1) {
			myDataBase.rawQuery("UPDATE Parametre SET flag_geoloc=" + p_valeur, null);
		}else{
			myDataBase.rawQuery("UPDATE Parametre SET flag_mode_emploi=" + p_valeur, null);
		}
	}

	public Cursor loadParametre() {
		Cursor c = myDataBase.rawQuery("SELECT flag_geoloc, flag_mode_emploi FROM Parametre", null);

		return c;
	}

	// *** LES INSERT SQL ***
	public void insertType(ContentValues p_param) {
		myDataBase.insert("soustype", null, p_param);
	}

	public void supprimer() {

	}

	public void insertFavoris(ContentValues p_param) {
		Cursor c = myDataBase.rawQuery("SELECT count(*) as nbe FROM Favoris WHERE id_equipement=" + p_param.get("id_equipement"), null);

		Log.d("myTag", "sql : " + "SELECT count(*) as nbe FROM Favoris WHERE id_equipement=" + p_param.get("id_equipement"));

		if ((p_param.get("xml_equipement").toString().length()>0)&&(p_param.get("id_equipement")==0)) {
			c = myDataBase.rawQuery("SELECT count(*) as nbe FROM Favoris WHERE xml_equipement=" + p_param.get("xml_equipement"), null);
		}

		if (c.moveToFirst()) {
			if (c.getInt(0) == 0) {
				myDataBase.insert("Favoris", null, p_param);
			}
		}
	}

	public void insertActualite(ContentValues p_param) {
		myDataBase.insert("actualite", null, p_param);
	}

	public void insertBalade(ContentValues p_param) {
		myDataBase.insert("balade", null, p_param);
	}

	public void insertUrgence(ContentValues p_param) {
		myDataBase.insert("equipement", null, p_param);
	}
	public void updateUrgence(ContentValues p_param) {

		if (p_param.get("xml_id").toString()!="null") {
			if (p_param.get("fermeture_exceptionnelle").toString()!="") {
				myDataBase.rawQuery("UPDATE equipement SET fermeture_exceptionnelle='" + p_param.get("fermeture_exceptionnelle") + "' WHERE xml_id='" + p_param.get("xml_id") + "'", null);
			}
		}

	}

	public void insertIncontournable(ContentValues p_param) {
		myDataBase.insert("incontournable", null, p_param);
	}

	// *** LES DELETE SQL ***
	public void deleteFavoris(String p_id) {
		myDataBase.delete("Favoris", "id_equipement=" + p_id, null);
	}
	public void deleteFavorisActu(int p_id) {
		myDataBase.delete("Favoris", "id_favoris=" + p_id, null);
	}

	public void deleteFavorisXml(String p_id) {
		myDataBase.delete("Favoris", "xml_equipement='" + p_id + "'", null);
	}

	public void deleteSousType() { myDataBase.delete("soustype","",null); }
	public void deleteBalade() {
		myDataBase.delete("balade","",null);
	}
	public void deleteUrgence() {
		myDataBase.delete("equipement","type='urgence'",null);
	}
	public void deleteIncontrounables() {
		myDataBase.delete("incontournable","",null);
	}
	public void deleteDemarche() {
		myDataBase.delete("equipement","type='et-si'",null);
		myDataBase.delete("equipement","type='par-organisme'",null);
		myDataBase.delete("equipement","type='par-demarche'",null);
	}


	public void copyToSD() throws IOException {
			File sd = Environment.getExternalStorageDirectory();

			if (sd.canWrite()) {
				String currentDBPath = DB_NAME;
				String backupDBPath = "backupname.sqlite";
				File currentDB = new File(DB_PATH, currentDBPath);
				File backupDB = new File(sd, backupDBPath);

				if (currentDB.exists()) {
					FileChannel src = new FileInputStream(currentDB).getChannel();
					FileChannel dst = new FileOutputStream(backupDB).getChannel();
					dst.transferFrom(src, 0, src.size());
					src.close();
					dst.close();
				}
			}

	}

	public void updateUtilisateur(ContentValues p_param) {
		myDataBase.insert("Utilisateur", null, p_param);
	}

	public void majUtilisateur(String p_nom, String p_prenom, String p_email, String p_telephone) {
		myDataBase.rawQuery("UPDATE Utilisateur SET nom='" + p_nom + "', prenom='"+p_prenom+"', email='"+p_email+"', telephone='"+p_telephone+"'", null);
	}

	// *** LES LOAD SQL ***
	public Cursor loadUtilisateur() {
		Cursor c;

		c = myDataBase.rawQuery("SELECT nom, prenom, email, telephone FROM Utilisateur", null);

		return c;
	}

	public Cursor loadActualite() {
		Cursor c;

		c = myDataBase.rawQuery("SELECT titre,texte,image,xml_id FROM actualite limit 0,20", null);

		return c;
	}

	public Cursor loadType(String p_type) {
		Cursor c;

		c = myDataBase.rawQuery("SELECT libelle, slug FROM soustype WHERE type='"+p_type+"' ORDER by ordre ASC", null);

		return c;
	}

	public Cursor loadAllType() {
		Cursor c;

		c = myDataBase.rawQuery("SELECT slug FROM soustype", null);

		return c;
	}

	public Cursor loadEquipementFromType(String p_param) {
		Cursor c;

		c = myDataBase.rawQuery("SELECT titre, longitude, latitude, id_equipement, arrondissement, jour, type_associe, complement_info  FROM equipement WHERE type='"+p_param+"' ORDER BY arrondissement, titre", null);

		//Log.d("myTag", "SELECT titre, longitude, latitude, id_equipement, arrondissement, jour, type_associe, complement_info  FROM equipement WHERE type='"+p_param+"' ORDER BY arrondissement, titre");

		return c;

	}


	public Cursor loadEquipementFromDemarche(String p_param) {
		Cursor c;

		c = myDataBase.rawQuery("SELECT titre, longitude, latitude, id_equipement, arrondissement, jour, type_associe, complement_info  FROM equipement WHERE xml_id='"+p_param+"' ORDER BY arrondissement", null);

	//	Log.d("myTag","SELECT titre, longitude, latitude, id_equipement, arrondissement, jour, type_associe, complement_info  FROM equipement WHERE xml_id='"+p_param+"' ORDER BY arrondissement");

		return c;

	}

	public int checkFavoris(int p_id) {
		Cursor c;

		c = myDataBase.rawQuery("SELECT libelle FROM Favoris WHERE id_equipement="+p_id, null);

		if (c.moveToFirst()) {
			return 1;
		}

		return 0;
	}

	public int test() {
		return 0;
	}

	public int checkFavorisXml() {
		Cursor c;

		c = myDataBase.rawQuery("SELECT libelle FROM Favoris WHERE xml_equipement='"+Config.xml_id+"'", null);

		if (c.moveToFirst()) {
			return 1;
		}

		return 0;
	}


	public int getNumFavoris() {
		Cursor c = myDataBase.rawQuery("SELECT count(*) as nbe FROM Favoris", null);

		if (c.moveToFirst()) {
			return c.getInt(0);
		}
		return 0;
	}

	public Cursor loadFavoris() {
		Cursor c;

		c = myDataBase.rawQuery("SELECT libelle, id_equipement, xml_equipement FROM Favoris", null);

		return c;
	}
	public int checkFavorisActu(String p_url) {
		Cursor c;

		c = myDataBase.rawQuery("SELECT id_favoris FROM Favoris WHERE url='"+p_url+"'", null);

		try {
			if (c != null) {
				if (c.moveToNext()) {
					return c.getInt(0);
				}
			}
		}catch(SQLException sqle){
			throw sqle;
		}

		return 0;
	}
	public int checkFavorisEvt(String p_param) {
		Cursor c;

		c = myDataBase.rawQuery("SELECT id_favoris FROM Favoris WHERE xml_id='"+p_param+"'", null);

		try {
			if (c != null) {
				if (c.moveToNext()) {
					return c.getInt(0);
				}
			}
		}catch(SQLException sqle){
			throw sqle;
		}

		return 0;
	}

	public Cursor loadTitreEquipementFromXML(String p_xml_id) {
		Cursor c;

		c = myDataBase.rawQuery("SELECT titre FROM equipement WHERE xml_id='"+p_xml_id+"'", null);

		return c;
	}

	public Cursor loadEquipementFromXML(String p_xml_id) {
		Cursor c;

		c = myDataBase.rawQuery("SELECT titre, adresse, code_postal, ville, horaires, fermeture_exceptionnelle, site_web, email, telephone, afficher_complement, complement_info, longitude, latitude, id_equipement, type_associe, xml_id  FROM equipement WHERE xml_id='"+p_xml_id+"' ORDER BY arrondissement", null);


		return c;
	}

	public Cursor loadEquipement(String p_id) {
		Cursor c;

		c = myDataBase.rawQuery("SELECT titre, adresse, code_postal, ville, horaires, fermeture_exceptionnelle, site_web, email, telephone, afficher_complement, complement_info, longitude, latitude, id_equipement, type_associe, xml_id  FROM equipement WHERE id_equipement="+p_id, null);

		//Log.d("myTag", "SELECT titre, adresse, code_postal, ville, horaires, fermeture_exceptionnelle, site_web, email, telephone, afficher_complement, complement_info, longitude, latitude, id_equipement, type_associe  FROM equipement WHERE id_equipement="+p_id);


		return c;
	}

	public Cursor loadBalade() {
		Cursor c;

		c = myDataBase.rawQuery("SELECT titre, content, visuel, tetiaire, tetiaire_hd, points FROM balade", null);

		return c;

	}

	public Cursor loadUrgence() {
		Cursor c;

		c = myDataBase.rawQuery("SELECT titre, mots_clefs FROM equipement WHERE type='urgence' ORDER BY arrondissement", null);

		return c;

	}

	public Cursor loadIncontournable() {
		Cursor c;

		c = myDataBase.rawQuery("SELECT titre, visuel_wp, accroche, description, visuel, site_web, visuel_hd, tetiaire, tetiaire_hd, type_principal FROM incontournable", null);

		return c;

	}

	public Cursor loadPied() {
		Cursor c;

		c = myDataBase.rawQuery("SELECT ligne, nom, latitude, longitude FROM Metro", null);

		return c;
	}

	public Cursor loadTaxi() {
		Cursor c;

		c = myDataBase.rawQuery("SELECT latitude, longitude FROM Taxi", null);

		return c;
	}

	public Cursor loadTram() {
		Cursor c;

		c = myDataBase.rawQuery("SELECT latitude, longitude, ligne, nom FROM Tram", null);

		return c;
	}

	public Cursor loadGare() {
		Cursor c;

		c = myDataBase.rawQuery("SELECT latitude, longitude, libelle FROM Gare", null);

		return c;
	}

	public Cursor loadVelo() {
		Cursor c;

		c = myDataBase.rawQuery("SELECT latitude, longitude FROM Velov", null);

		return c;
	}

	public Cursor loadParking() {
		Cursor c;

		c = myDataBase.rawQuery("SELECT latitude, longitude, libelle FROM Parking", null);

		return c;
	}
	public Cursor loadAutolib() {
		Cursor c;

		c = myDataBase.rawQuery("SELECT latitude, longitude FROM Autolib", null);

		return c;
	}
	public Cursor loadPmr() {
		Cursor c;

		c = myDataBase.rawQuery("SELECT latitude, longitude, libelle, rue FROM Pmr", null);

		return c;
	}


}