package com.youtubeurs.lite2.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.youtubeurs.lite2.MyApplication;
import com.youtubeurs.lite2.util.imageutils.ImageLoader;

public class UrlImageView extends LinearLayout {

	private Context mContext;
	// private Drawable mDrawable;
	// private ProgressBar mSpinner;
	private ImageView mImage;

	// private boolean loaded = false;

	public UrlImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public UrlImageView(Context context) {
		super(context);
		init(context);
	}

	/**
	 * First time loading of the LoaderImageView
	 * Sets up the LayoutParams of the view, you can change these to
	 * get the required effects you want
	 */
	@SuppressWarnings("deprecation")
	private void init(final Context context) {
		mContext = context;

		mImage = new ImageView(mContext);
		mImage.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		mImage.setVisibility(View.GONE);
        mImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

		// mSpinner = new ProgressBar(mContext);
		// mSpinner.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		// mSpinner.setIndeterminate(true);

		// addView(mSpinner);
		addView(mImage);
	}

	/**
	 * Set's the view's drawable, this uses the internet to retrieve the image
	 * don't forget to add the correct permissions to your manifest
	 * 
	 * @param imageUrl
	 *            the url of the image you wish to load
	 */
	public void setImageDrawable(final String imageUrl) {
        MyApplication myApp = (MyApplication) mContext.getApplicationContext();
        if (myApp.getImgLoader() == null)
            myApp.setImgLoader(new ImageLoader(mContext));
        myApp.getImgLoader().DisplayImage(imageUrl, mImage);
        mImage.setVisibility(View.VISIBLE);
	}

}
