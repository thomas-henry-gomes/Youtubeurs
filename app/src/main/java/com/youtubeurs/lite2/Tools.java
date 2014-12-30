package com.youtubeurs.lite2;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;

import android.support.v7.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.*;
import com.youtubeurs.lite2.service.task.AutoRefresh;

public class Tools {

	/*--------------------------------------------------------------------------------------------------*/
	/* GESTION DES TOASTS */
	/*--------------------------------------------------------------------------------------------------*/
	public static Toast appToast;

	/**
	 * Show Toast in application
	 */
	public static void showToast(Context context, String message, int time) {
        if (context == null)
            return;

		if (appToast != null)
			appToast.cancel();

		appToast = Toast.makeText(context, message, time);
		appToast.show();
	}

	/**
	 * Hide Toast in application
	 */
	public static void hideToast() {
		if (appToast != null)
			appToast.cancel();
	}

    /*--------------------------------------------------------------------------------------------------*/
	/* GESTION DES YOUTUBEURS INITIAUX */
	/*--------------------------------------------------------------------------------------------------*/
    /**
     * Initialize application's users in database
     * @param database
     *          the database
     */
    public static void initUsers(MySQLite database){
        database.insertUser("Aziatomik", "Aziatomik", "http://i1.ytimg.com/u/Y4B1a66jfFH0p60smEu8eQ/channels4_tablet_banner_low.jpg?v=5315fe81", "TRUE");
        database.insertUser("MonsieurDream", "Cyprien", "http://i1.ytimg.com/u/yWqModMQlbIo8274Wh_ZsQ/channels4_tablet_banner_low.jpg?v=513b4dce", "TRUE");
        database.insertUser("CyprienGaming", "Cyprien Gaming", "http://i1.ytimg.com/u/WMYFDuCcvkmPiOf1RP_IKQ/channels4_tablet_banner_low.jpg?v=52761f8b", "TRUE");
        database.insertUser("GoldenMoustacheVideo", "Golden Moustache", "http://i1.ytimg.com/u/JruTcTs7Gn2Tk7YC-ENeHQ/channels4_tablet_banner_low.jpg?v=52fdfc98", "TRUE");
        database.insertUser("HugoToutSeul", "Hugo Tout Seul", "http://i1.ytimg.com/u/XapBbZyOgvYwOlqYzf8dhg/channels4_tablet_banner_low.jpg?v=5325eb2b", "TRUE");
        database.insertUser("JulienJosselin", "Julfou", "http://i1.ytimg.com/u/m7o3SiyBiq-beAi3oNu_Cg/channels4_tablet_banner_low.jpg?v=51b00779", "TRUE");
        database.insertUser("LeKemar", "Kemar", "http://i1.ytimg.com/u/hKMRHxLETrj_5JjiqExD1w/channels4_tablet_banner_low.jpg?v=51826057", "TRUE");
        database.insertUser("Iafermejerome", "La Ferme Jerome", "", "TRUE");
        database.insertUser("mistervofficial", "Mister V", "http://i1.ytimg.com/u/8Q0SLrZLiTj5s4qc9aad-w/channels4_tablet_banner_low.jpg?v=5276a701", "TRUE");
        database.insertUser("ptitenatou", "Natoo", "http://i1.ytimg.com/u/tihF1ZtlYVzoaj_bKLQZ-Q/channels4_tablet_banner_low.jpg?v=51a0ec4b", "TRUE");
        database.insertUser("NormanFaitDesVideos", "Norman Fait Des Vidéos", "http://i1.ytimg.com/u/ww2zZWg4Cf5xcRKG-ThmXQ/channels4_tablet_banner_low.jpg?v=5160140c", "TRUE");
        database.insertUser("nqtv", "Rémi Gaillard", "http://i1.ytimg.com/u/mPSwsooZq8an7xOLQQhAdw/channels4_tablet_banner_low.jpg?v=517b26c9", "TRUE");
        database.insertUser("aMOODIEsqueezie", "Squeezie", "http://i1.ytimg.com/u/Weg2Pkate69NFdBeuRFTAw/channels4_tablet_banner_low.jpg?v=514dd807", "TRUE");
        database.insertUser("TomliVlogs", "Thomas Gauthier", "http://i1.ytimg.com/u/x0oS6YmHSbOMnN3vQvTR0Q/channels4_tablet_banner_low.jpg?v=515b5ebc", "TRUE");
        database.insertUser("LeRiiiiiiiireJaune", "Le Rire Jaune", "http://yt3.ggpht.com/-A_V85qYncHY/UsQ0XiymYLI/AAAAAAAAAOU/JtN7t6O_rHA/w2120-fcrop64=1,00005a57ffffa5a8-nd/channels4_banner.jpg", "TRUE");
    }

	/*--------------------------------------------------------------------------------------------------*/
	/* GESTION DE LA BARRE D'ACTION */
	/*--------------------------------------------------------------------------------------------------*/
	/**
	 * Set up the {@link android.app.ActionBar}.
	 * 
	 * @param activity
	 *            The activity
	 * 
	 * @param actionBar
	 *            The actionBar to set up
	 */
	public static void setupActionBar(Activity activity, ActionBar actionBar, String complement, String complement2) {
		// On désactive le bouton home de l'activité principale
		if (activity.getClass().equals(MainActivity.class)) {
			actionBar.setDisplayHomeAsUpEnabled(false);
		}

		// On initialise les sous-titres
		if (activity.getClass().equals(VideosActivity.class)) {
            actionBar.setTitle(complement2);
			if ("".equals(complement))
				actionBar.setSubtitle("");
			else
				actionBar.setSubtitle(complement + " vidéos");
		}
		else if (activity.getClass().equals(SettingsActivity.class)) {
			actionBar.setSubtitle("Choisissez un paramètre");
		}
		else if (activity.getClass().equals(VisualisationActivity.class)) {
			actionBar.setTitle(complement);
		}
	}

	/*--------------------------------------------------------------------------------------------------*/
	/* GESTION DE LA PUB */
	/*--------------------------------------------------------------------------------------------------*/
    /**
     * Return The time's laps of no pub
     *
     * @return The time's laps of no pub in long
     */
    public static long getLapsOfNoPub() {
        return 1800000;
    }

	/**
	 * Set up the Ads.
	 * 
	 * @param activity
	 *            The activity
	 * 
	 * @param adView
	 *            The adView
	 */
	public static void setupAds(Context context, Activity activity, AdView adView) {

        final Context context1 = context;
        final Activity activity1 = activity;

		final MySQLite database = new MySQLite(activity.getApplicationContext());
		database.openDatabase();
		String nopub = database.getParam("NOPUB");

        boolean noPubTmp = false;
        if(database.getParam("NOPUB-TIME") != null){
            long timePrev = Long.parseLong(database.getParam("NOPUB-TIME"));
            long timeNext = new Date().getTime();
            long timeDiff = timeNext - timePrev;

            if(timeDiff <= getLapsOfNoPub()){
                noPubTmp = true;
            }
        }

		database.closeDatabase();
		if ((nopub != null) || (noPubTmp)) {
			return;
		}

		LinearLayout layoutMain = (LinearLayout) activity.findViewById(R.id.LinearLayoutAds);

		adView = new AdView(activity);
        adView.setAdUnitId("ca-app-pub-3881234064367598/5596527463");
        adView.setAdSize(AdSize.BANNER);
		//adView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);

        adView.setAdListener(new AdListener() {
            /**
             * Called when an Activity is created in front of your app,
             * presenting the user with a full-screen ad UI in response to their touching ad.
             */
            @Override
            public void onAdOpened() {
            }

            /**
             * Called when the full-screen Activity presented with onPresentScreen has been dismissed
             * and control is returning to your app.
             */
            @Override
            public void onAdClosed() {
            }

            /**
             * Called when an Ad touch will launch a new application.
             */
            @Override
            public void onAdLeftApplication() {
                database.openDatabase();
                if(database.getParam("NOPUB-TIME") == null)
                    database.insertParam("NOPUB-TIME", "" + (new Date().getTime()));
                else
                    database.updateParam("NOPUB-TIME", "" + (new Date().getTime()));
                database.closeDatabase();
                Tools.showToast(context1, "Merci pour votre soutien, les publicités sont maintenant désactivées temporairement.", Toast.LENGTH_LONG);
                activity1.recreate();
            }

            /**
             * Sent when AdView.loadAd has succeeded
             */
            @Override
            public void onAdLoaded() {
                int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context1.getResources().getDisplayMetrics());
                if (activity1.getClass().equals(VideosActivity.class)) {
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT);
                    lp.setMargins(0, 0, 0, px);
                    ((FrameLayout) activity1.findViewById(R.id.ptr_layout)).setLayoutParams(lp);
                }
                else if (activity1.getClass().equals(MainActivity.class)) {
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT);
                    lp.setMargins(0, 0, 0, px);
                    ((LinearLayout) activity1.findViewById(R.id.LinearLayout)).setLayoutParams(lp);
                }
            }

            /**
             * Sent when loadAd has failed,
             * typically because of network failure, an application configuration error, or a lack of ad inventory.
             */
            @Override
            public void onAdFailedToLoad(int errorCode) {
                //Tools.showToast(context1, "onAdFailedToLoad : " + errorCode, Toast.LENGTH_LONG);
            }
        });

		layoutMain.addView(adView);

		AdRequest adRequest = new AdRequest.Builder().build();
        //AdRequest adRequest = new AdRequest.Builder().addTestDevice("01ad0cf6cc11d80e").build(); // Pour les tests
		adView.loadAd(adRequest);
	}

	/**
	 * Return the Interstitial Ads Id
	 * 
	 * @return The interstitial Ads Id
	 */
	public static String getInterstitialAdsId(String username) {

		// 1 interstitiel par podcasteur
		if ("NormanFaitDesVideos".equals(username)) {
			return "ca-app-pub-3881234064367598/6794059063";
		}
		else if ("TomliVlogs".equals(username)) {
			return "ca-app-pub-3881234064367598/8270792260";
		}
		else if ("MonsieurDream".equals(username)) {
			return "ca-app-pub-3881234064367598/3980193466";
		}
        else if ("CyprienGaming".equals(username)) {
            return "ca-app-pub-3881234064367598/3980193466";
        }
		else if ("HugoToutSeul".equals(username)) {
			return "ca-app-pub-3881234064367598/5456926665";
		}
		else if ("mistervofficial".equals(username)) {
			return "ca-app-pub-3881234064367598/3840592660";
		}
		else if ("Iafermejerome".equals(username)) {
			return "ca-app-pub-3881234064367598/2363859469";
		}
		else if ("LeKemar".equals(username)) {
			return "ca-app-pub-3881234064367598/8410393065";
		}
		else if ("ptitenatou".equals(username)) {
			return "ca-app-pub-3881234064367598/5317325863";
		}
		else if ("JulienJosselin".equals(username)) {
			return "ca-app-pub-3881234064367598/6933659869";
		}
		else {
			return "ca-app-pub-3881234064367598/1026727062";
		}

	}

	/*--------------------------------------------------------------------------------------------------*/
	/* GESTION DES URL */
	/*--------------------------------------------------------------------------------------------------*/
	/**
	 * Get value of given parameter for an URL
	 * 
	 * @param url
	 *            The given URL to search in
	 * @param paramUrl
	 *            The parameter to search
	 * @return
	 */
	public static String getQueryParam(String url, String paramUrl) {
		try {
			String[] urlParts = url.split("\\?");
			if (urlParts.length > 1) {
				String query = urlParts[1];
				for (String param : query.split("&")) {
					String[] pair = param.split("=");
					String key = URLDecoder.decode(pair[0], "UTF-8");
					String value = "";
					if (pair.length > 1) {
						value = URLDecoder.decode(pair[1], "UTF-8");
					}

					if (paramUrl.equals(key)) {
						return value;
					}
				}
			}

			return url;
		} catch (UnsupportedEncodingException ex) {
			throw new AssertionError(ex);
		}
	}

    /*--------------------------------------------------------------------------------------------------*/
	/* GESTION DES SERVICES */
	/*--------------------------------------------------------------------------------------------------*/
    /**
     * Return if the service is running or not
     * @param activity
     * @return
     */
    public static boolean isMyServiceRunning(Activity activity) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (AutoRefresh.class.getName().equals(service.service.getClassName())) {
                Log.w("YouTubeurs", "isMyServiceRunning true");
                return true;
            }
        }
        Log.w("YouTubeurs", "isMyServiceRunning false");
        return false;
    }
}
