package com.youtubeurs.lite2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.google.analytics.tracking.android.EasyTracker;
import com.youtubeurs.lite2.service.task.AutoRefresh;

public class LaunchActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        if((!Tools.isMyServiceRunning(this)) && (sharedPrefs.getBoolean("prefAutoRefresh", false))) {
            Intent i = new Intent(LaunchActivity.this, AutoRefresh.class);
            startService(i);
        }

        MySQLite database = new MySQLite(getApplicationContext());
        database.openDatabase();

        if (database.getUsersCount() == 0){
            Tools.initUsers(database);
        }

        database.closeDatabase();

		Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.fadeout, R.anim.fadein);
	}

	@Override
	public void onBackPressed() {
	}

    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }
}
