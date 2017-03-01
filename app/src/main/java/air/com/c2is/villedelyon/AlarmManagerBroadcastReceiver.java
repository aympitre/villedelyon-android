package air.com.c2is.villedelyon;

import android.media.AudioManager;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.SystemClock;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

	final public static String ONE_TIME = "onetime";
    public MediaPlayer mp;
    public int flagFirst = 0;

	@Override
	public void onReceive(Context context, Intent intent) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
            //Acquire the lock
            wl.acquire();

            //You can do the processing here update the widget/remote views.
            Bundle extras = intent.getExtras();
            StringBuilder msgStr = new StringBuilder();

            Config.flag_is_playing = 1;

            SharedPreferences sharedPref  = context.getSharedPreferences("vdl", Context.MODE_WORLD_WRITEABLE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("alert_vdl", "aymeric");
            editor.commit();

            try { Config.myReveil.goReveilOn();     } catch (Exception e) { e.printStackTrace(); }
            try { Config.myFragment.goReveilOn();   } catch (Exception e) { e.printStackTrace(); }
            try { Config.myActualite.goReveilOn();  } catch (Exception e) { e.printStackTrace(); }
            try { Config.myHome.goReveilOn();       } catch (Exception e) { e.printStackTrace(); }
            try { Config.myParametre.goReveilOn();  } catch (Exception e) { e.printStackTrace(); }
            try { Config.mySeDeplacer.goReveilOn(); } catch (Exception e) { e.printStackTrace(); }
            try { Config.myFavoris.goReveilOn();    } catch (Exception e) { e.printStackTrace(); }
            try { Config.myFragCarte.goReveilOn();  } catch (Exception e) { e.printStackTrace(); }
            try { Config.myDetailEquip.goReveilOn();  } catch (Exception e) { e.printStackTrace(); }
            try { Config.myCarteBalade.goReveilOn();  } catch (Exception e) { e.printStackTrace(); }
            try { Config.myDetailEvt.goReveilOn();      } catch (Exception e) { e.printStackTrace(); }
            try { Config.myDetailIncontournable.goReveilOn();  } catch (Exception e) { e.printStackTrace(); }
            try { Config.mySaviezDetail.goReveilOn();  } catch (Exception e) { e.printStackTrace(); }
            try { Config.mySaviezVous.goReveilOn();  } catch (Exception e) { e.printStackTrace(); }

            try {
                Config.mp = new MediaPlayer();
                int resId = -1;

                AssetFileDescriptor descriptor = context.getAssets().openFd("reveil.mp3");

                Config.mp.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                descriptor.close();

                Config.mp.prepare();
                Config.mp.setVolume(8, 8);
//                Config.mp.setVolume(1, 1);

                Config.mp.setLooping(true);
                Config.mp.start();

            } catch (Exception e) {
                e.printStackTrace();
            }

            notifyUser(context, intent);

             Log.d("myTag", "FIRE FIRE");

            wl.release();

            CancelAlarm(context);
            relanceCompteur(context);
	}

    public void stopReveil() {
        try {
            mp.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void relanceCompteur(Context context) {
        Config.flagRelanceCompteur = 1;

        SharedPreferences sharedPref = context.getSharedPreferences("vdl", Context.MODE_WORLD_WRITEABLE);
        SetAlarmRelance(context);
    }

    public void notifyUser(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent2 = new Intent(context, MainActivity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent2, 0); //PendingIntent.FLAG_UPDATE_CURRENT

        Config.flagOffReveil = 1;

        Notification.Builder notification = new Notification.Builder(context)
                .setContentTitle("Ville de lyon")
                .setContentText("Réveil")
                .setSmallIcon(R.drawable.ic_notif)
                .setAutoCancel(true)
                .setContentIntent(contentIntent);

        notificationManager.notify(11, notification.build());

    }

    public void SetAlarmRelance(Context context) {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.FALSE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);

//      Relance au lendemain
        //am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 86400000, pi);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 86400000, pi);

        Log.d("myTag", "Relance au lendemain");

        Config.flagAlarm = 1;
        Config.flagFirst = 1;
    }

	public void SetAlarm(Context context)
    {
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.FALSE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.cancel(pi);

        SharedPreferences sharedPref = context.getSharedPreferences("vdl", Context.MODE_WORLD_WRITEABLE);

        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + Config.reveilDiff, pi);

        Log.d("myTag", "je set l'alarme");

        Config.flagFirst = 1;
    }

    public void CancelAlarm(Context context)
    {
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
    public void setOnetimeTimer(Context context){
    	AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.TRUE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);

        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi);

//        Toast.makeText(context, "pouet", Toast.LENGTH_LONG).show();
    }
}
