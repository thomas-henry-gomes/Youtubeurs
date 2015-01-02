package com.youtubeurs.lite2;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.*;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.youtubeurs.lite2.domain.Library;
import com.youtubeurs.lite2.domain.User;
import com.youtubeurs.lite2.domain.Video;
import com.youtubeurs.lite2.service.task.GetYouTubeUserVideosTask;
import com.youtubeurs.lite2.ui.widget.VideosListView;
import com.youtubeurs.lite2.util.StreamUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;

public class VideosActivity extends ActionBarActivity implements OnRefreshListener {

	private AdView adView;
	private InterstitialAd interstitial;

    private boolean clickPub = false;

	private static VideosListView listView;

	private MySQLite database;
	private Menu menu;
	private boolean videosToRefresh = false;

	final String VIDEO_URL = "video_url";
	final String VIDEO_TITLE = "video_title";

	final String VIDEO_AUTHOR = "video_author";
	String videoAuthor = "";

	final String INTERSTITIAL_ADS = "interstitial_ads";
	public String interstitialAds = "";

	// This is the handler that receives the response when the YouTube task has finished
	static Handler responseHandler = null;

    private PullToRefreshLayout mPullToRefreshLayout;

    private int preLast = 0;
    private int preFirst = 1;

    private User user = null;
    private String googleAnalyticsLabel = "";

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_level);

		Intent intent = getIntent();
		if (intent != null) {
			interstitialAds = intent.getStringExtra(INTERSTITIAL_ADS);
			videoAuthor = intent.getStringExtra(VIDEO_AUTHOR);

            database = new MySQLite(getApplicationContext());
            database.openDatabase();
            user = database.getUserFromUsername(videoAuthor);
            if (user != null) {
                googleAnalyticsLabel = "Vidéos du YouTubeur " + user.getName();
                MyApplication.getGaTracker().set(Fields.SCREEN_NAME, googleAnalyticsLabel);
            }
            database.closeDatabase();
		}
		else {
            this.finish();
            overridePendingTransition(R.anim.fadeout, R.anim.fadein);
		}

        // Now find the PullToRefreshLayout to setup
        mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);

        // Now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(this)
                // Mark All Children as pullable
                .allChildrenArePullable()
                // Set the OnRefreshListener
                .listener(this)
                // Finally commit the setup to our PullToRefreshLayout
                .setup(mPullToRefreshLayout);

		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        listView = (VideosListView) findViewById(R.id.videosListView);

        preLast = 0;
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // Gestion des vidéos
                final int lastItem = firstVisibleItem + visibleItemCount;
                if(lastItem == totalItemCount) {
                    if(preLast != lastItem){
                        //to avoid multiple calls for last item
                        preLast = lastItem;
                        database.openDatabase();
                        listView.setVideosNext(database.getVideos(videoAuthor), getApplicationContext());
                        database.closeDatabase();
                    }
                }

                // Gestion de la barre d'action
                if (getSupportActionBar() != null) {
                    if(firstVisibleItem > (preFirst + 1)) {
                        preFirst = firstVisibleItem;
                        getSupportActionBar().hide();
                    }
                    else {
                        if(firstVisibleItem < (preFirst - 1)) {
                            preFirst = firstVisibleItem;
                            getSupportActionBar().show();
                        }
                    }
                }
            }
        });

		responseHandler = new Handler() {
			public void handleMessage(Message msg) {
				populateListWithVideosMsg(msg);
			};
		};

		// Lecture de la base pour afficher les videos
		database.openDatabase();

		List<Video> listVideos = database.getVideos(videoAuthor);
		if ((listVideos == null) || (listVideos.size() == 0)) {
			// Pas de videos en base
			videosToRefresh = true;

			populateListWithVideosList(listVideos);
		}
		else {
			// Affichage des videos
			populateListWithVideosList(listVideos);
		}

		database.closeDatabase();

		// Set up the action bar
		String complement = "";
		if (listVideos != null)
			complement = complement.concat("" + listVideos.size());
        if (user != null)
		    Tools.setupActionBar(this, getSupportActionBar(), complement, user.getName());
        else
            Tools.setupActionBar(this, getSupportActionBar(), complement, videoAuthor);

		// Ajout de la PUB
		Tools.setupAds(getApplicationContext(), this, adView);

        interstitialAds = "false";
	}

	/**
	 * This method retrieves the Library of videos from the task and passes them to our ListView
	 * 
	 * @param msg
	 */
	private void populateListWithVideosMsg(Message msg) {
		String v = "";
		v = (String) msg.getData().get("ERROR");

		if ("true".equals(v)) {
            mPullToRefreshLayout.setRefreshComplete();

			Tools.showToast(getApplicationContext(), "Erreur lors de la récupération, vérifier votre connexion internet !!!", Toast.LENGTH_LONG);
			return;
		}

		// Retreive the videos are task found from the data bundle sent back
		Library lib = (Library) msg.getData().get(GetYouTubeUserVideosTask.LIBRARY);

		// MAJ database
		database.openDatabase();

        user = database.getUserFromUsername(videoAuthor);

        if (user != null) {
            Tracker tracker = GoogleAnalytics.getInstance(this).getTracker("UA-49895675-6");

            HashMap<String, String> hitParameters = new HashMap<String, String>();
            hitParameters.put(Fields.HIT_TYPE, "appview");
            hitParameters.put(Fields.SCREEN_NAME, "Vidéos du YouTubeur " + user.getName());

            tracker.send(hitParameters);
        }

        // Insertion du user dans la table USERS
        if ((lib.getVideos().size() > 0) && (user == null)) {
            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                // Récupération de l'ID de la chaîne du user
                HttpClient client = new DefaultHttpClient();
                HttpUriRequest request = new HttpGet("https://www.googleapis.com/youtube/v3/channels?part=brandingSettings&forUsername=" + videoAuthor + "&key=" + DeveloperKey.DEVELOPER_KEY);
                HttpResponse response = null;
                response = client.execute(request);
                String jsonString = StreamUtils.convertToString(response.getEntity().getContent());

                JSONObject json = new JSONObject(jsonString);
                String id = json.getJSONArray("items").getJSONObject(0).getString("id");

                // Récupération de l'image de la chaîne
                request = new HttpGet("https://www.googleapis.com/youtube/v3/channels?part=brandingSettings&id=" + id + "&key=" + DeveloperKey.DEVELOPER_KEY);
                response = null;
                response = client.execute(request);
                jsonString = StreamUtils.convertToString(response.getEntity().getContent());

                json = new JSONObject(jsonString);
                String title = json.getJSONArray("items").getJSONObject(0).getJSONObject("brandingSettings").getJSONObject("channel").getString("title");
                String url;
                try {
                    url = json.getJSONArray("items").getJSONObject(0).getJSONObject("brandingSettings").getJSONObject("image").getString("bannerMobileHdImageUrl");
                } catch (JSONException e) {
                    try {
                        url = json.getJSONArray("items").getJSONObject(0).getJSONObject("brandingSettings").getJSONObject("image").getString("bannerTabletLowImageUrl");
                    } catch (JSONException e1) {
                        url = "";
                    }
                }

                database.insertUser(videoAuthor, title, url, "FALSE");
            } catch (IOException e) {
                Tools.showToast(getApplicationContext(), "Erreur lors de la récupération, vérifier votre connexion internet !!!", Toast.LENGTH_LONG);
                return;
            } catch (JSONException e) {
                Tools.showToast(getApplicationContext(), "Erreur lors de la récupération, vérifier votre connexion internet !!!", Toast.LENGTH_LONG);
                return;
            }
        }

		Video videoTmp = null;
		for (int i = 0; i < lib.getVideos().size(); i++) {
			videoTmp = lib.getVideos().get(i);

			if (database.getVideoWithURL(videoTmp.getUrl()) == null) {
				// Video absente de la base : donc ajout
				database.insertVideo(videoTmp);
			}
		}

		// Set up the action bar
		String complement = "";
		int videosNumber = database.getVideos(videoAuthor).size();
		if (videosNumber > 0)
			complement = complement.concat("" + videosNumber);

        user = database.getUserFromUsername(videoAuthor);
        if (user != null)
            Tools.setupActionBar(this, getSupportActionBar(), complement, user.getName());
        else
            Tools.setupActionBar(this, getSupportActionBar(), complement, videoAuthor);

		// Because we have created a custom ListView we don't have to worry about setting the adapter in the activity
		// we can just call our custom method with the list of items we want to display
		listView.setVideosFirst(database.getVideos(videoAuthor), getApplicationContext());

		database.closeDatabase();

        // Notify PullToRefreshLayout that the refresh has finished
        mPullToRefreshLayout.setRefreshComplete();

		if (videosNumber > 0)
			Tools.hideToast();
		else
			Tools.showToast(getApplicationContext(), "Aucune vidéo trouvée pour le YouTubeur " + videoAuthor + ".\nAttention à bien renseigner un user valide !!!", Toast.LENGTH_LONG);
	}

	/**
	 * This method retrieves the Library of videos from the task and passes them to our ListView
	 */
	private void populateListWithVideosList(List<Video> videosList) {
		listView.setVideosFirst(videosList, getApplicationContext());
	}

	@Override
	public void onLowMemory() {
		this.finish();
		super.onLowMemory();
	}

	@SuppressLint("HandlerLeak")
	@Override
	protected void onRestart() {
		if (responseHandler == null) {
			responseHandler = new Handler() {
				public void handleMessage(Message msg) {
					populateListWithVideosMsg(msg);
				};
			};
		}
		super.onRestart();

        MyApplication.getGaTracker().set(Fields.SCREEN_NAME, googleAnalyticsLabel);
	}

	@Override
	public void onDestroy() {
		if (adView != null)
			adView.destroy();
		super.onDestroy();
	}

    @Override
    public void onPause() {
        if (adView != null)
            adView.pause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adView != null)
            adView.resume();

        if ("true".equals(interstitialAds)) {
            database = new MySQLite(getApplicationContext());
            database.openDatabase();
            String nopub = database.getParam("NOPUB");

            boolean noPubTmp = false;
            if(database.getParam("NOPUB-TIME") != null){
                long timePrev = Long.parseLong(database.getParam("NOPUB-TIME"));
                long timeNext = new Date().getTime();
                long timeDiff = timeNext - timePrev;

                if(timeDiff <= Tools.getLapsOfNoPub()){
                    noPubTmp = true;
                }
            }

            database.closeDatabase();

            if ((nopub == null) && (!noPubTmp)) {
                Random random = new Random();
                // Chiffre aléatoire entre 0 et 100 compris
                int valeur = random.nextInt(101);
                if (valeur <= 30) {
                    interstitial = new InterstitialAd(this);
                    interstitial.setAdUnitId(Tools.getInterstitialAdsId(videoAuthor));
                    AdRequest adRequest = new AdRequest.Builder().build();
                    interstitial.setAdListener(new AdListener() {
                        /**
                         * Called when an Activity is created in front of your app,
                         * presenting the user with a full-screen ad UI in response to their touching ad.
                         */
                        public void onAdOpened() {
                            interstitialAds = "false";
                            //Tools.showToast(getApplicationContext(), "onPresentScreen", Toast.LENGTH_LONG);
                            Tools.showToast(getApplicationContext(), "Vous pouvez désactiver temporairement les publicités\nen cliquant sur celle qui vient de s'ouvrir !!!", Toast.LENGTH_LONG);
                        }

                        /**
                         * Called when the full-screen Activity presented with onPresentScreen has been dismissed
                         * and control is returning to your app.
                         */
                        public void onAdClosed() {
                            interstitialAds = "false";
                            //Tools.showToast(getApplicationContext(), "onDismissScreen", Toast.LENGTH_LONG);
                            if (clickPub){
                                clickPub = false;
                                database.openDatabase();
                                if(database.getParam("NOPUB-TIME") == null)
                                    database.insertParam("NOPUB-TIME", "" + (new Date().getTime()));
                                else
                                    database.updateParam("NOPUB-TIME", "" + (new Date().getTime()));
                                database.closeDatabase();
                                Tools.showToast(getApplicationContext(), "Merci pour votre soutien, les publicités sont maintenant désactivées temporairement.", Toast.LENGTH_LONG);
                                //this.recreate();
                            }
                        }

                        /**
                         * Called when an Ad touch will launch a new application.
                         */
                        public void onAdLeftApplication() {
                            interstitialAds = "false";
                            //Tools.showToast(getApplicationContext(), "onLeaveApplication", Toast.LENGTH_LONG);
                            clickPub = true;
                        }

                        /**
                         * Sent when AdView.loadAd has succeeded
                         */
                        public void onAdLoaded() {
                            interstitial.show();
                        }

                        /**
                         * Sent when loadAd has failed,
                         * typically because of network failure, an application configuration error, or a lack of ad inventory.
                         */
                        public void onAdFailedToLoad(int errorCode) {
                            //Tools.showToast(getApplicationContext(), "onFailedToReceiveAd Code:" + arg1, Toast.LENGTH_LONG);
                        }
                    });
                    interstitial.loadAd(adRequest);
                }
            }

            interstitialAds = "false";
        }
        else {
            interstitialAds = "false";
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// INFLATE THE MENU; THIS ADDS ITEMS TO THE ACTION BAR IF IT IS PRESENT.
		getMenuInflater().inflate(R.menu.level, menu);
		this.menu = menu;

		// Si besoin de rafraichir les videos
		if (videosToRefresh) {
			Tools.showToast(getApplicationContext(), "Récuperation des vidéos en cours ...", Toast.LENGTH_LONG);

			// We start a new task that does its work on its own thread
			// We pass in a handler that will be called when the task has finished
			// We also pass in the name of the user we are searching YouTube for
			new Thread(new GetYouTubeUserVideosTask(responseHandler, videoAuthor)).start();
			videosToRefresh = false;
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
            this.finish();
            overridePendingTransition(R.anim.fadeout, R.anim.fadein);

            return true;
		case R.id.menu_refresh:
            Tools.showToast(getApplicationContext(), "Actualisation en cours ...", Toast.LENGTH_LONG);

			// We start a new task that does its work on its own thread
			// We pass in a handler that will be called when the task has finished
			// We also pass in the name of the user we are searching YouTube for
			new Thread(new GetYouTubeUserVideosTask(responseHandler, videoAuthor)).start();

            return true;
        case R.id.menu_delete:
            database.openDatabase();
            User u = database.getUserFromUsername(videoAuthor);
            database.closeDatabase();

            if (u == null)
                return true;

            AlertDialog.Builder adb = new AlertDialog.Builder(this);

            TextView title = new TextView(this);
            title.setText("--- ATTENTION ---");
            title.setPadding(15, 15, 15, 15);
            title.setGravity(Gravity.CENTER);
            title.setTextColor(getResources().getColor(R.color.material_textColorPrimary));
            title.setTextSize(19);
            adb.setCustomTitle(title);

            final Activity activity = this;

            adb.setMessage("Voulez-vous supprimer le YouTubeur " + u.getName() + " de l'application ?\n(Il vous sera toujours possible de le rajouter à partir de son user : " + videoAuthor + ")");
            adb.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    MySQLite database = new MySQLite(getApplicationContext());
                    database.openDatabase();
                    database.removeVideosFromUsername(videoAuthor);
                    database.removeUserFromUsername(videoAuthor);
                    database.closeDatabase();

                    activity.finish();
                    overridePendingTransition(R.anim.fadeout, R.anim.fadein);
                }
            });
            adb.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog ad = adb.show();
            TextView messageText = (TextView) ad.findViewById(android.R.id.message);
            messageText.setPadding(15, 15, 15, 15);
            messageText.setGravity(Gravity.CENTER);
            messageText.setTextColor(getResources().getColor(R.color.material_textColorPrimary));
            messageText.setTextSize(18);
            ad.show();

            return true;
        case R.id.menu_share:
            interstitialAds = "false";

            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_TEXT, "Suit le Youtubeur " + videoAuthor + " avec l'application Android Youtubeurs : https://play.google.com/store/apps/details?id=com.youtubeurs.lite2 .");
            startActivity(Intent.createChooser(i, "Partager avec ..."));
            overridePendingTransition(R.anim.fadeout, R.anim.fadein);

            return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		this.finish();
        overridePendingTransition(R.anim.fadeout, R.anim.fadein);
	}

    @Override
    public void onRefreshStarted(View view) {
        // We start a new task that does its work on its own thread
        // We pass in a handler that will be called when the task has finished
        // We also pass in the name of the user we are searching YouTube for
        new Thread(new GetYouTubeUserVideosTask(responseHandler, videoAuthor)).start();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Send a screen view when the Activity is displayed to the user.
        MyApplication.getGaTracker().send(MapBuilder.createAppView().build());
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Make sure we null our handler when the activity has stopped
        // because who cares if we get a callback once the activity has stopped? not me!
        responseHandler = null;
    }

}
