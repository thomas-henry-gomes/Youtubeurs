package com.youtubeurs.lite2.service.task;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.youtubeurs.lite2.MainActivity;
import com.youtubeurs.lite2.MySQLite;
import com.youtubeurs.lite2.R;
import com.youtubeurs.lite2.Tools;
import com.youtubeurs.lite2.VideosActivity;
import com.youtubeurs.lite2.domain.Library;
import com.youtubeurs.lite2.domain.User;
import com.youtubeurs.lite2.domain.Video;

import java.util.ArrayList;
import java.util.List;

public class AutoRefresh extends IntentService {
    private Boolean alreadyRunning = false;
    private int mNotificationId = 0;
    private MySQLite database = null;
    private SharedPreferences sharedPrefs = null;
    private String extra = null;
    private int nbUsers = 0;
    private int nbReturns = 0;
    private ArrayList<String> notificationUsers = null;

    private Handler handler;
    static Handler responseHandler = null;

    public AutoRefresh() {
        super("Youtubeurs");
        alreadyRunning = false;
        responseHandler = new Handler() {
            public void handleMessage(Message msg) {
                populateListWithVideosMsg(msg);
            }
        };
        database = new MySQLite(this);
        mNotificationId = 0;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!alreadyRunning) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle("YouTubeurs en cours d'exécution")
                    .setContentText("Appuyer ici pour en savoir plus ou arrêter l'application")
                    .setOngoing(false);
            startForeground(mNotificationId, mBuilder.build());

            while (true) {
                // Initialisation des variables
                alreadyRunning = true;

                if (sharedPrefs == null) {
                    sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
                }

                if (extra == null) {
                    try {
                        int prefAutoRefreshDelay = Integer.parseInt(sharedPrefs.getString("prefAutoRefreshDelay", "1440"));
                        long autoRefreshDelay = prefAutoRefreshDelay * 60 * 1000;
                        Thread.sleep(autoRefreshDelay);

                    } catch (InterruptedException e) {
                        return;
                    }
                }

                if (responseHandler == null) {
                    responseHandler = new Handler() {
                        public void handleMessage(Message msg) {
                            populateListWithVideosMsg(msg);
                        }
                    };
                }

                if (database == null)
                    database = new MySQLite(this);

                // Si l'actualisation automatique est désactivée, on quitte le service
                if ((!sharedPrefs.getBoolean("prefAutoRefresh", false)) && (extra == null)) {
                    return;
                }

                if (sharedPrefs.getBoolean("prefAutoRefreshWifi", true)) {
                    ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    if (extra == null) {
                        while (!mWifi.isConnected()) {
                            try {
                                Thread.sleep(600000);
                                mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                            } catch (InterruptedException e) {
                                return;
                            }
                        }
                    }
                    else {
                        if (!mWifi.isConnected()) {
                            showToast("Actualisation annulée." + System.getProperty("line.separator") + "Vérifier votre connexion internet (Wifi)");
                            extra = null;
                            continue;
                        }
                    }
                }

                database.openDatabase();

                List<User> users = database.getUsers();

                database.closeDatabase();

                nbUsers = users.size();
                notificationUsers = null;
                nbReturns = 0;

                for (User user : users) {
                    new Thread(new GetYouTubeUserVideosTask(responseHandler, user.getUsername())).start();
                }

                if (extra != null) {
                    extra = null;
                }
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        if (intent != null) {
            extra = intent.getStringExtra("prefUpdate");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler = new Handler();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        alreadyRunning = false;
    }

    /**
     * This method retrieves the Library of videos from the task and passes them to our ListView
     *
     * @param msg message
     */
    private void populateListWithVideosMsg(Message msg) {
        Boolean newVideos = false;
        nbReturns = nbReturns + 1;
        int newVideosNumber = 0;

        String v = "";
        v = (String) msg.getData().get("ERROR");

        if ("true".equals(v)) {
            return;
        }

        // Retreive the videos are task found from the data bundle sent back
        Library lib = (Library) msg.getData().get(GetYouTubeUserVideosTask.LIBRARY);

        // MAJ database
        database.openDatabase();
        Video videoTmp;
        for (int i = 0; i < lib.getVideos().size(); i++) {
            videoTmp = lib.getVideos().get(i);

            if (database.getVideoWithURL(videoTmp.getUrl()) == null) {
                // Video absente de la base : donc ajout
                newVideos = true;
                newVideosNumber++;
                database.insertVideo(videoTmp);
            }
        }
        database.closeDatabase();

        if(newVideos) {
            String title = "Nouvelle vidéo disponible";
            if(newVideosNumber > 1) {
                title = "Nouvelles vidéos disponibles";
            }
            Notify(title, "", lib.getVideos().get(0).getUsername());
        }
    }

    /**
     * Notify with title and text
     * @param title title of the notification
     * @param text text of the notification
     */
    private void Notify(String title, String text, String username) {
        database.openDatabase();
        User user = database.getUserFromUsername(username);
        text = user.getName();
        database.closeDatabase();

        if (notificationUsers == null)
            notificationUsers = new ArrayList<String>();

        notificationUsers.add(text);

        if ((nbReturns == nbUsers) && (nbUsers == notificationUsers.size())) {
            mNotificationId = mNotificationId + 1;

            text = "";
            for(int i = 0 ; i < notificationUsers.size() ; i++){
                if (i == 0)
                    text = text + notificationUsers.get(i);

                if ((i != 0) && (i != notificationUsers.size()))
                    text = text + ", " + notificationUsers.get(i);

                if (i == (notificationUsers.size() - 1))
                    text = text + " et " + notificationUsers.get(i) + ".";
            }

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentTitle(title)
                            .setContentText(text)
                            .setAutoCancel(true);

            if (sharedPrefs == null) {
                sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            }

            if (sharedPrefs.getBoolean("prefNotificationSound", true)) {
                mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            }
            if (sharedPrefs.getBoolean("prefNotificationVibration", true)) {
                mBuilder.setVibrate(new long[]{100, 200, 100, 500});
            }

            Intent resultIntent = new Intent(this, MainActivity.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this, mNotificationId, resultIntent, 0);
            mBuilder.setContentIntent(resultPendingIntent);

            // Gets an instance of the NotificationManager service
            NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // Builds the notification and issues it.
            mNotifyMgr.notify(mNotificationId, mBuilder.build());
        }
    }

    /**
     * Show toast message
     * @param text text to show
     */
    private void showToast (final String text) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Tools.showToast(getApplicationContext(), text, Toast.LENGTH_LONG);
            }
        });
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);
    }
}
