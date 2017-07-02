package air.com.c2is.villedelyon;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.content.Intent;
import android.util.Log;
import android.widget.SimpleAdapter;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class SplashScreen extends Activity {

    private static String TAG       = SplashScreen.class.getName();
    private static long SLEEP_TIME  = 1;    // Sleep for some time
    public SharedPreferences sharedPref;
    public myAsyncTask2 myWebFetch;

    public String   visuel_pub;
    public int      nbe_pub = 0;
    public String   url_pub;
    public int      time_pub;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        sharedPref      = this.getPreferences(Context.MODE_PRIVATE);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);    // Removes title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,     WindowManager.LayoutParams.FLAG_FULLSCREEN);    // Removes notification bar

        setContentView(R.layout.activity_splash_screen);

        myWebFetch = new myAsyncTask2();
        myWebFetch.execute();
    }

    private class IntentLauncher extends Thread {
        @Override
        /**
         * Sleep for some time and than start new activity.
         */
        public void run() {
            try {
                // Sleeping
                Thread.sleep(SLEEP_TIME*1000);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }

            int num_alert = sharedPref.getInt("num_alert" + Config.id_pub, 0);

//            Log.d("myTag", "ici pref : " + num_alert);

            if (num_alert<nbe_pub) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("num_alert" + Config.id_pub, num_alert+1);
                editor.commit();

                Intent intent = new Intent(SplashScreen.this, PubScreen.class);
                SplashScreen.this.startActivity(intent);

            }else {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                SplashScreen.this.startActivity(intent);
            }
            SplashScreen.this.finish();
        }
    }

    private Document parseXML(InputStream stream)
            throws Exception
    {
        DocumentBuilderFactory objDocumentBuilderFactory = null;
        DocumentBuilder objDocumentBuilder               = null;
        Document doc                                     = null;
        try
        {
            objDocumentBuilderFactory   = DocumentBuilderFactory.newInstance();
            objDocumentBuilder          = objDocumentBuilderFactory.newDocumentBuilder();

            doc = objDocumentBuilder.parse(stream);
        }
        catch(Exception ex)
        {
            throw ex;
        }

        return doc;
    }

    class myAsyncTask2 extends AsyncTask<Void, Void, Void> {

        myAsyncTask2()    {

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            IntentLauncher launcher = new IntentLauncher();
            launcher.start();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                try {
                    URL url = new URL(Config.urlDomaine+"splashscreen.php");
                    URLConnection connection = url.openConnection();

                    Document doc = parseXML(connection.getInputStream());

                    NodeList descNodes = doc.getElementsByTagName("splash_screen");
                    NodeList listChamps;

                    ContentValues myValue = new ContentValues();

                    for(int i=0; i<descNodes.getLength();i++)
                    {
                        HashMap<String, Object> mapping = new HashMap<String, Object>();
                        Node courant = descNodes.item(i);
                        NodeList listNode = courant.getChildNodes();
                        for(int j=0; j<listNode.getLength();j++) {
                            if(listNode.item(j).getNodeName().equals("titre")){
                                //Log.d("myTag", "titre : " + listNode.item(j).getTextContent());

                            }else if(listNode.item(j).getNodeName().equals("visuel_wp")){
                                Config.visuel_pub = listNode.item(j).getTextContent();
                            }else if(listNode.item(j).getNodeName().equals("nb_occurences")){
                                nbe_pub = Integer.parseInt(listNode.item(j).getTextContent());
                            }else if(listNode.item(j).getNodeName().equals("url")){
                                Config.url_pub = listNode.item(j).getTextContent();
                            }else if(listNode.item(j).getNodeName().equals("display_time")){
                                Config.time_pub = Integer.parseInt(listNode.item(j).getTextContent());
                            }else if(listNode.item(j).getNodeName().equals("id")){
                                Config.id_pub = listNode.item(j).getTextContent();
                            }
                        }
                    }

                } catch (MalformedURLException e) {
                    Log.d("myTag", "This is my MalformedURLException");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            } catch (Exception e) {
                //	Toast.makeText(Config.myResVehicule,"erreur : " + e.toString(), Toast.LENGTH_LONG).show();
            }

            return null;
        }
    }
}
