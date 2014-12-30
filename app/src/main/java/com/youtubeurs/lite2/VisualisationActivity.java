package com.youtubeurs.lite2;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.youtubeurs.lite2.domain.User;

public class VisualisationActivity extends YouTubeFailureRecoveryActivity {
	private YouTubePlayerView playerView;

	final String INTERSTITIAL_ADS = "interstitial_ads";
	final String VIDEO_URL = "video_url";
	final String VIDEO_TITLE = "video_title";
	String interstitialAds = "";
	String videoUrl = "";
	String videoTitle = "";

	final String VIDEO_AUTHOR = "video_author";
	String videoAuthor = "";

    //TODO ActionBar ab = null;

    private MySQLite database;

    SharedPreferences sharedPrefs = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_response);

		Intent intent = getIntent();
		if (intent != null) {
			videoUrl = intent.getStringExtra(VIDEO_URL);
			videoTitle = intent.getStringExtra(VIDEO_TITLE);
			videoAuthor = intent.getStringExtra(VIDEO_AUTHOR);

            database = new MySQLite(getApplicationContext());
            database.openDatabase();
            User user = database.getUserFromUsername(videoAuthor);
            if (user != null) {
                MyApplication.getGaTracker().set(Fields.SCREEN_NAME, "Vidéo du YouTubeur " + user.getName() + " : " + videoTitle);
            }
            database.closeDatabase();
		}
		else {
            this.finish();
            overridePendingTransition(R.anim.fadeout, R.anim.fadein);
		}

		// Set up the action bar
		//TODO Tools.setupActionBar(this, getSupportActionBar(), videoTitle, "");

		playerView = (YouTubePlayerView) findViewById(R.id.player);
		playerView.initialize(DeveloperKey.DEVELOPER_KEY, this);

		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

		LinearLayout.LayoutParams playerParams = (LinearLayout.LayoutParams) playerView.getLayoutParams();
		playerParams.width = LayoutParams.MATCH_PARENT;
		playerParams.height = LayoutParams.MATCH_PARENT;

        // TODO ab = getActionBar();
        // TODO ab.hide();

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// INFLATE THE MENU; THIS ADDS ITEMS TO THE ACTION BAR IF IT IS PRESENT.
		getMenuInflater().inflate(R.menu.response, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
            this.finish();
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
	public void onLowMemory() {
		this.finish();
		super.onLowMemory();
	}

	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        final YouTubePlayer player1 = player;

		player.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
			@Override
			public void onVideoStarted() {
				// ab.hide();
			}

			@Override
			public void onVideoEnded() {
                // TODO ab.show();
			}

			@Override
			public void onLoading() {

			}

			@Override
			public void onLoaded(String arg0) {
                // TODO ab.hide();

                if (sharedPrefs.getBoolean("prefAutoPlay", false))
                    player1.play();
			}

			@Override
			public void onError(ErrorReason arg0) {
                // TODO ab.show();
			}

			@Override
			public void onAdStarted() {

			}
		});

		player.setPlaybackEventListener(new YouTubePlayer.PlaybackEventListener() {
			@Override
			public void onStopped() {
                // TODO ab.show();
			}

			@Override
			public void onSeekTo(int arg0) {

			}

			@Override
			public void onPlaying() {
                // TODO ab.hide();
			}

			@Override
			public void onPaused() {
                // TODO ab.show();
			}

			@Override
			public void onBuffering(boolean arg0) {

			}
		});

		player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
		//player.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
		if (!wasRestored) {
			player.cueVideo(Tools.getQueryParam(videoUrl, "v"));
		}
	}

	@Override
	protected YouTubePlayer.Provider getYouTubePlayerProvider() {
		return playerView;
	}

    @Override
    protected void onStart() {
        super.onStart();

        // Send a screen view when the Activity is displayed to the user.
        MyApplication.getGaTracker().send(MapBuilder.createAppView().build());
    }
}
