package com.youtubeurs.lite2.ui.widget;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.youtubeurs.lite2.domain.Video;
import com.youtubeurs.lite2.ui.adapter.VideosAdapter;

/**
 * A custom ListView that takes a list of videos to display</br>
 * As you can see you don't call setAdapter you should call setVideosFirst and the rest is done for you.</br>
 * </br>
 * Although this is a simple custom view it is good practice to always use custom views when you can
 * it allows you to encapsulate your work and keep your activity as a delegate whenever possible</br>
 * This list could be further customised without any hard graft, whereas if you had put this into the activity</br>
 * it would have been a real pain to pull out further down the road.</br>
 * </br>
 * One example is we could switch out the adapter we are using, to something that displays scrolling images or whatever,
 * and our activity never need know!</br>
 * 
 * @author paul.blundell
 */
public class VideosListView extends ListView {
    final int STEP_NUMBER_LOAD = 10;

	public VideosListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public VideosListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public VideosListView(Context context) {
		super(context);
	}

    /**
     * Set the first videos in list
     * @param videos
     * @param applicationContext
     */
	public void setVideosFirst(List<Video> videos, Context applicationContext) {
        if (videos.size() > STEP_NUMBER_LOAD)
            videos = videos.subList(0, STEP_NUMBER_LOAD);

		VideosAdapter adapter = new VideosAdapter(getContext(), videos, applicationContext);
		setAdapter(adapter);
	}

    /**
     * Set the next videos in list
     * @param videos
     * @param applicationContext
     */
    public void setVideosNext(List<Video> videos, Context applicationContext) {
        VideosAdapter adapter = (VideosAdapter) getAdapter();

        // Si on est déjà à la fin, on ne fait rien
        if(adapter.getVideos().size() == videos.size())
            return;

        int nextSize = adapter.getVideos().size();
        nextSize = nextSize + STEP_NUMBER_LOAD;
        if (nextSize > videos.size())
            nextSize = videos.size();

        adapter.setVideos(videos.subList(0, nextSize));
        adapter.notifyDataSetChanged();
    }

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
	}
}
