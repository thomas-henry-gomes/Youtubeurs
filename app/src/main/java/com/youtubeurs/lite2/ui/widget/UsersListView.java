package com.youtubeurs.lite2.ui.widget;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.youtubeurs.lite2.domain.User;
import com.youtubeurs.lite2.ui.adapter.UsersAdapter;

/**
 * A custom ListView that takes a list of users to display</br>
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
public class UsersListView extends ListView {

    public UsersListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public UsersListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UsersListView(Context context) {
        super(context);
    }

    public void setUsers(List<User> users, Context applicationContext) {
        if(getAdapter() == null) {
            UsersAdapter adapter = new UsersAdapter(getContext(), users, applicationContext);
            setAdapter(adapter);
        }
        else {
            UsersAdapter adapter = (UsersAdapter) getAdapter();
            adapter.setUsers(users);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
    }

}
