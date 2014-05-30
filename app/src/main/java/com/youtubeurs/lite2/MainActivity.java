package com.youtubeurs.lite2;

import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.AdView;
import com.google.analytics.tracking.android.EasyTracker;
import com.youtubeurs.lite2.domain.User;
import com.youtubeurs.lite2.ui.widget.UsersListView;

public class MainActivity extends Activity {
	private AdView adView;

	final String VIDEO_AUTHOR = "video_author";

    private static UsersListView listView;

	int nbClicHome = 0;
	private MySQLite database;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar
		Tools.setupActionBar(this, getActionBar(), "", "");

		nbClicHome = 0;
		database = new MySQLite(getApplicationContext());

        database.openDatabase();

        List<User> users = database.getUsers();
        listView = (UsersListView) findViewById(R.id.usersListView);
        listView.setUsers(users, getApplicationContext());

        /*-------------------------------------------------- Ajout de la PUB --------------------------------------------------*/
		Tools.setupAds(getApplicationContext(), this, adView);

        /*-------------------------------------------------- Ajout des popus --------------------------------------------------*/
        if (database.getParam("POPUP-PUB01") == null){
            AlertDialog.Builder adb = new AlertDialog.Builder(this);

            TextView title = new TextView(this);
            title.setText("--- NOUVEAU ---");
            title.setPadding(15, 15, 15, 15);
            title.setGravity(Gravity.CENTER);
            title.setTextColor(Color.BLACK);
            title.setTextSize(19);
            adb.setCustomTitle(title);

            adb.setMessage("Vous pouvez maintenant nous soutenir en cliquant sur les publicités.\n\nVous bénéficierez par la même occasion de leur désactivation temporaire !!!");
            adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    MySQLite database = new MySQLite(getApplicationContext());
                    database.openDatabase();
                    database.insertParam("POPUP-PUB01", "TRUE");
                    database.closeDatabase();
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

        database.closeDatabase();
	}

	@Override
	public void onDestroy() {
		nbClicHome = 0;
		if (adView != null)
			adView.destroy();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	long time = 0;
	Toast quit = null;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// On supprime les pubs si 100 clics en 20 secondes
			if (Calendar.getInstance().getTimeInMillis() - time > 20000) {
				nbClicHome = 0;
				time = Calendar.getInstance().getTimeInMillis();
			}
			else {
				nbClicHome++;
				if (nbClicHome == 100) {
					if (database != null) {
						database.openDatabase();
						database.insertParam("NOPUB", "TRUE");
						database.closeDatabase();

                        if (getApplicationContext() != null) {
                            quit = Toast.makeText(getApplicationContext(), "Déblocage effectué", Toast.LENGTH_SHORT);
                            quit.show();
                        }

                        this.recreate();
						overridePendingTransition(R.anim.fadeout, R.anim.fadein);
					}
				}
			}

            return true;
        case R.id.menu_add:
            AlertDialog.Builder adb = new AlertDialog.Builder(this);

            TextView title = new TextView(this);
            title.setText("Ajouter un YouTubeur");
            title.setPadding(15, 15, 15, 15);
            title.setGravity(Gravity.CENTER);
            title.setTextColor(Color.BLACK);
            title.setTextSize(19);
            adb.setCustomTitle(title);

            adb.setMessage("Saisissez ci-dessous un YouTubeur valide (utilisez son user YouTube : http://www.youtube.com/user/xxx/ où xxx est le user à utiliser) :");
            final EditText input = new EditText(this);
            adb.setView(input);
            adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(MainActivity.this, VideosActivity.class);
                    intent.putExtra(VIDEO_AUTHOR, input.getText().toString());
                    startActivity(intent);
                    overridePendingTransition(R.anim.fadeout, R.anim.fadein);
                }
            });
            adb.setNegativeButton("Fermer", new DialogInterface.OnClickListener() {
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

            return true;
        case R.id.menu_share:
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.youtubeurs.lite2");
            startActivity(Intent.createChooser(i, "Partager avec ..."));

            return true;
        case R.id.menu_facebook:
            String url = "https://www.facebook.com/YouTubeursAndroid";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
            overridePendingTransition(R.anim.fadeout, R.anim.fadein);

            return true;
        case R.id.menu_param:
            Intent j = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(j);
            overridePendingTransition(R.anim.fadeout, R.anim.fadein);

            return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
        if (getApplicationContext() == null)
            return;

		// On quitte l'application si double click
		if (Calendar.getInstance().getTimeInMillis() - time > 3000) {
			quit = Toast.makeText(getApplicationContext(), "Appuyer encore sur RETOUR pour quitter", Toast.LENGTH_SHORT);
			quit.show();
			time = Calendar.getInstance().getTimeInMillis();
		}
		else {
			quit.cancel();
			moveTaskToBack(true);
		}
	}

    @Override
    protected void onRestart() {
        super.onRestart();

        database.openDatabase();
        listView.setUsers(database.getUsers(), getApplicationContext());
        database.closeDatabase();

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT);
        lp.setMargins(0, 0, 0, 0);
        ((LinearLayout) this.findViewById(R.id.LinearLayout)).setLayoutParams(lp);
        ((LinearLayout) this.findViewById(R.id.LinearLayoutAds)).removeAllViews();
        Tools.setupAds(getApplicationContext(), this, adView);
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
