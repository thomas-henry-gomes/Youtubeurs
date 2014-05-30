package com.youtubeurs.lite2.ui.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.youtubeurs.lite2.MyApplication;
import com.youtubeurs.lite2.MySQLite;
import com.youtubeurs.lite2.R;
import com.youtubeurs.lite2.VisualisationActivity;
import com.youtubeurs.lite2.domain.Video;
import com.youtubeurs.lite2.ui.widget.UrlImageView;

/**
 * This adapter is used to show our Video objects in a ListView
 * It hasn't got many memory optimisations, if your list is getting bigger or more complex
 * you may want to look at better using your view resources: http://developer.android.com/resources/samples/ApiDemos/src/com/example/android/apis/view/List14.html
 * 
 * @author paul.blundell
 */
public class VideosAdapter extends BaseAdapter {
	final String VIDEO_URL = "video_url";
	final String VIDEO_TITLE = "video_title";
	final String VIDEO_AUTHOR = "video_author";

	private Context applicationContext = null;

	// The list of videos to display
	List<Video> videos;

	// An inflator to use when creating rows
	private LayoutInflater mInflater;

	/**
	 * @param context
	 *            this is the context that the list will be shown in - used to create new list rows
	 * @param videos
	 *            this is a list of videos to display
	 */
	public VideosAdapter(Context context, List<Video> videos, Context applicationContext) {
		this.videos = videos;
		this.applicationContext = applicationContext;
		this.mInflater = LayoutInflater.from(context);
	}

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    @Override
	public int getCount() {
		return videos.size();
	}

	@Override
	public Object getItem(int position) {
		return videos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// This is the layout we are using for each row in our list
		// anything you declare in this layout can then be referenced below
		convertView = mInflater.inflate(R.layout.list_item_video, null);

		// We are using a custom imageview so that we can load images using urls
		// For further explanation see: http://blog.blundell-apps.com/imageview-with-loading-spinner/
		UrlImageView thumb = (UrlImageView) convertView.findViewById(R.id.userVideoThumbImageView);

        TextView title = (TextView) convertView.findViewById(R.id.userVideoTitleTextView);
        TextView subTitleLeft = (TextView) convertView.findViewById(R.id.userVideoSubTitleLeftTextView);
        TextView SubTitleRight = (TextView) convertView.findViewById(R.id.userVideoSubTitleRightTextView);


		// Get a single video from our list
		Video video = videos.get(position);
		// Set the image for the list item
		try {
			thumb.setImageDrawable(video.getThumbUrl());
		} catch (NullPointerException npe) {
		}

        Long time = Long.valueOf(video.getUploaded());
        Date uploadedDate = new Date(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

		// Set the title for the list item
		// String vue = "vue";
		// if (video.getCount() > 1)
		// vue = vue.concat("s");
		title.setText(video.getTitle());
        SubTitleRight.setText("Tu as visionné cette vidéo " + video.getCount() + " fois");
        subTitleLeft.setText(dateFormat.format(uploadedDate));

		convertView.setTag(new String[]{video.getTitle(), video.getUrl(), video.getUsername()});

		convertView.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				MyApplication myApp = (MyApplication) applicationContext;
				if (myApp == null) {
					System.runFinalizersOnExit(true);
					System.exit(0);
				}

				if (v == null) {
					System.runFinalizersOnExit(true);
					System.exit(0);
				}
				if ((v.getParent() == null) || (v.getContext() == null) || (v.getTag() == null)) {
					System.runFinalizersOnExit(true);
					System.exit(0);
				}

				Intent intent = new Intent(v.getContext(), VisualisationActivity.class);
				intent.putExtra(VIDEO_URL, ((String[]) v.getTag())[1]);
				intent.putExtra(VIDEO_TITLE, ((String[]) v.getTag())[0]);
				intent.putExtra(VIDEO_AUTHOR, ((String[]) v.getTag())[2]);

				MySQLite database = new MySQLite(v.getContext());
				database.openDatabase();
				Video video = database.getVideoWithURL(((String[]) v.getTag())[1]);
				int count = video.getCount() + 1;
				video.setCount(count);
				database.updateVideo(video);
				database.closeDatabase();

                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(v.getContext());
                if (sharedPrefs.getBoolean("prefExtPlay", false)) {
                    v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(((String[]) v.getTag())[1])));
                }
                else {
                    v.getContext().startActivity(intent);
                }
			}
		});

		return convertView;
	}
}
