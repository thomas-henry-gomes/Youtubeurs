package com.youtubeurs.lite2;

import android.app.Application;

import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.youtubeurs.lite2.util.imageutils.ImageLoader;

public class MyApplication extends Application {
	private ImageLoader imgLoader = null;

    private static GoogleAnalytics mGa;
    private static Tracker mTracker;

    // Google Analytics configuration values - Placeholder property ID.
    private static final String GA_PROPERTY_ID = "UA-49895675-6";

	public MyApplication() {
		super();
	}

    @Override
    public void onCreate() {
        super.onCreate();
        initializeGa();
    }


    //---------------------------------------- Image Loader ----------------------------------------

	/**
	 * 
	 * @return the image loader util
	 */
	public ImageLoader getImgLoader() {
		return imgLoader;
	}

	/**
	 * 
	 * @param imgLoader
	 *            the image loader to set
	 */
	public void setImgLoader(ImageLoader imgLoader) {
		this.imgLoader = imgLoader;
	}


    //-------------------------------------- Google Analytics --------------------------------------

    /**
     * Method to handle basic Google Analytics initialization. This call will not
     * block as all Google Analytics work occurs off the main thread.
     */
    private void initializeGa() {
        mGa = GoogleAnalytics.getInstance(this);
        mTracker = mGa.getTracker(GA_PROPERTY_ID);

        /*
        // Set the opt out flag when user updates a tracking preference.
        SharedPreferences userPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        userPrefs.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener () {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                                  String key) {
                if (key.equals(TRACKING_PREF_KEY)) {
                    GoogleAnalytics.getInstance(getApplicationContext()).setAppOptOut(sharedPreferences.getBoolean(key, false));
                }
            }
        });*/
    }

    /**
     * Returns the Google Analytics tracker.
     */
    public static Tracker getGaTracker() {
        return mTracker;
    }

    /**
     * Returns the Google Analytics instance.
     */
    public static GoogleAnalytics getGaInstance() {
        return mGa;
    }
}
