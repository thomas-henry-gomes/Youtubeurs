package com.youtubeurs.lite2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.AdView;
import com.google.analytics.tracking.android.EasyTracker;
import com.youtubeurs.lite2.service.task.AutoRefresh;
import com.youtubeurs.lite2.util.imageutils.ImageLoader;

public class SettingsActivity extends PreferenceActivity {
	private AdView adView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

        // Demande d'initialisation de l'application
        findPreference("prefReset").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                initData();
                return true;
            }
        });

        // Demande de vidage de cache
        findPreference("prefCleanCache").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ImageLoader il = new ImageLoader(getApplicationContext());
                il.clearCache();
                Tools.showToast(getApplicationContext(), "Cache vidé", Toast.LENGTH_LONG);
                return true;
            }
        });

        // Demande d'actualisation
        findPreference("prefUpdate").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                stopService(new Intent(SettingsActivity.this, AutoRefresh.class));
                Intent i = new Intent(SettingsActivity.this, AutoRefresh.class);
                i.putExtra("prefUpdate", "true");
                startService(i);
                Tools.showToast(getApplicationContext(), "Actualisation en cours ...", Toast.LENGTH_LONG);
                return true;
            }
        });

        // Demande d'activation ou de désactivation du rafraichissement automatique
        findPreference("prefAutoRefresh").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                Boolean value = (Boolean) o;

                if (value == true) {
                    Intent i = new Intent(SettingsActivity.this, AutoRefresh.class);
                    startService(i);
                    return true;
                }
                else {
                    Intent i = new Intent(SettingsActivity.this, AutoRefresh.class);
                    stopService(i);
                    return true;
                }
            }
        });

        // Modification de la durée de rafraichissement automatique
        findPreference("prefAutoRefreshDelay").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                //String value = (String) o;

                stopService(new Intent(SettingsActivity.this, AutoRefresh.class));
                startService(new Intent(SettingsActivity.this, AutoRefresh.class));
                return true;
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	public void initData() {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);

		TextView title = new TextView(this);
		title.setText("--- ATTENTION ---");
		title.setPadding(15, 15, 15, 15);
		title.setGravity(Gravity.CENTER);
		title.setTextColor(Color.BLACK);
		title.setTextSize(19);
		adb.setCustomTitle(title);

		adb.setMessage("Voulez-vous réinitialiser l'application ?\nElle sera telle qu'après sa première installation.");
		adb.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				MySQLite database = new MySQLite(getApplicationContext());
				database.openDatabase();
				database.removeVideos();
				database.removeParams();
                database.removeUsersInitial();
                database.removeUsersNotInitial();
                Tools.initUsers(database);
				database.closeDatabase();
				Tools.showToast(getApplicationContext(), "Application réinitialisée", Toast.LENGTH_LONG);
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
		messageText.setTextColor(Color.BLACK);
		messageText.setTextSize(18);
		ad.show();
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
