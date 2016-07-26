package air.com.c2is.villedelyon;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class PubScreen extends Activity {

    private static String TAG       = PubScreen.class.getName();
    private static long SLEEP_TIME  = 3;    // Sleep for some time
    public int flagNoSuite          = 0;
    public SharedPreferences sharedPref;
    public static GoogleAnalytics   analytics;
    public static Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);
        tracker = analytics.newTracker(getResources().getString(R.string.google_analytics_id));
        tracker.setScreenName("/Splashscreen pub");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());


        this.requestWindowFeature(Window.FEATURE_NO_TITLE);    // Removes title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);    // Removes notification bar

        setContentView(R.layout.activity_pub);

        ImageButton btMain      = (ImageButton) findViewById(R.id.btMain);
        ImageButton btFermer    = (ImageButton) findViewById(R.id.btFermer);

        btFermer.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        flagNoSuite = 1;
                        goToSuite();
                    }
                }
        );
        btMain.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        tracker.send(new HitBuilders.EventBuilder()
                                .setCategory("liens")
                                .setAction("click_out")
                                .setLabel("pub_splashcreen")
                                .build());

                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Config.url_pub));
                        startActivity(browserIntent);
                    }
                }
        );

        loadUrlScreen();

        // Start timer and launch main activity
        IntentLauncher launcher = new IntentLauncher();
        launcher.start();
    }

    public void loadUrlScreen() {
        ImageView iv = (ImageView) findViewById(R.id.btMain);

        BitmapDownloaderTask task = new BitmapDownloaderTask(iv);
        task.execute(Config.visuel_pub);
    }

    public void goToSuite() {
        // Start main activity
        Intent intent = new Intent(PubScreen.this, MainActivity.class);
        PubScreen.this.startActivity(intent);

        PubScreen.this.finish();
    }

    private class IntentLauncher extends Thread {
        @Override
        /**
         * Sleep for some time and than start new activity.
         */
        public void run() {
            try {
                // Sleeping
                Thread.sleep(Config.time_pub);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }

            if (flagNoSuite==0) {
                goToSuite();
            }
        }
    }
}
