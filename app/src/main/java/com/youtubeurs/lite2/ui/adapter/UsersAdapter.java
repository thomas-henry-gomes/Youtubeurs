package com.youtubeurs.lite2.ui.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.youtubeurs.lite2.MyApplication;
import com.youtubeurs.lite2.MySQLite;
import com.youtubeurs.lite2.R;
import com.youtubeurs.lite2.VideosActivity;
import com.youtubeurs.lite2.domain.User;
import com.youtubeurs.lite2.domain.Video;
import com.youtubeurs.lite2.ui.widget.UrlImageView;

/**
 * This adapter is used to show our User objects in a ListView
 * It hasn't got many memory optimisations, if your list is getting bigger or more complex
 * you may want to look at better using your view resources: http://developer.android.com/resources/samples/ApiDemos/src/com/example/android/apis/view/List14.html
 *
 * @author paul.blundell
 */
public class UsersAdapter extends BaseAdapter {
    final String VIDEO_AUTHOR = "video_author";
    final String INTERSTITIAL_ADS = "interstitial_ads";

    private Context applicationContext = null;

    // The list of users to display
    List<User> users;

    // An inflator to use when creating rows
    private LayoutInflater mInflater;

    /**
     * @param context
     *            this is the context that the list will be shown in - used to create new list rows
     * @param users
     *            this is a list of users to display
     */
    public UsersAdapter(Context context, List<User> users, Context applicationContext) {
        this.users = users;
        this.applicationContext = applicationContext;
        this.mInflater = LayoutInflater.from(context);
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // This is the layout we are using for each row in our list
        // anything you declare in this layout can then be referenced below
        convertView = mInflater.inflate(R.layout.list_item_user, null);

        // We are using a custom imageview so that we can load images using urls
        // For further explanation see: http://blog.blundell-apps.com/imageview-with-loading-spinner/
        UrlImageView thumb = (UrlImageView) convertView.findViewById(R.id.userVideoThumbImageView);

        TextView title = (TextView) convertView.findViewById(R.id.userVideoTitleTextView);
        TextView subTitle = (TextView) convertView.findViewById(R.id.userVideoSubTitleTextView);

        // Get a single user from our list
        User user = users.get(position);
        // Set the image for the list item
        try {
            if (!"".equals(user.getChannelImageUrl())) {
                thumb.setImageDrawable(user.getChannelImageUrl());
            }
            else {
                thumb.setVisibility(View.GONE);
            }
        } catch (NullPointerException npe) {
        }

        // Set the title for the list item
        // String vue = "vue";
        // if (video.getCount() > 1)
        // vue = vue.concat("s");
        MySQLite database = new MySQLite(convertView.getContext());
        database.openDatabase();

        String titleEnd = "";
        List<Video> videos = database.getVideos(user.getUsername());
        if((videos.size() == 0) || (videos.size() < 0)) {
            titleEnd = "Pas encore actualisé";
        }
        else {
            titleEnd = "" + database.getVideos(user.getUsername()).size() + " vidéo";
            if (videos.size() > 1)
                titleEnd = titleEnd + "s";
        }
        title.setText(user.getName());
        subTitle.setText(titleEnd);

        database.closeDatabase();

        convertView.setTag(new String[]{user.getUsername()});

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

                Intent intent = new Intent(v.getContext(), VideosActivity.class);
                intent.putExtra(VIDEO_AUTHOR, ((String[]) v.getTag())[0]);
                intent.putExtra(INTERSTITIAL_ADS, "false");
                v.getContext().startActivity(intent);
                ((Activity) v.getContext()).overridePendingTransition(R.anim.fadeout, R.anim.fadein);
            }
        });

        return convertView;
    }
}