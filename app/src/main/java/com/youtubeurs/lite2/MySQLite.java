package com.youtubeurs.lite2;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.youtubeurs.lite2.domain.User;
import com.youtubeurs.lite2.domain.Video;

public class MySQLite {
	private SQLiteDatabase bdd;
	private MySQLiteOpenHelper MySQLiteOpenHelper;

	public MySQLite(Context context) {
		// On crée la BDD et sa table
		MySQLiteOpenHelper = new MySQLiteOpenHelper(context);
	}

	public void openDatabase() {
		bdd = MySQLiteOpenHelper.getWritableDatabase();
	}

	public void closeDatabase() {
		bdd.close();
	}

	public SQLiteDatabase getDatabase() {
		return bdd;
	}

    /*---------------------------------------------------- USERS TABLE ----------------------------------------------------*/
    /**
     * Insert a user into the USERS table
     *
     * @param username
     *          The username of the user to insert
     * @param name
     *          The name of the user to insert
     * @param channelImageUrl
     *          The channel image url of the user to insert
     * @param initial
     *          Is this user an initial user of the application ?
     *
     * @return The row ID of the newly inserted row, or -1 if an error occurred
     */
    public long insertUser(String username, String name, String channelImageUrl, String initial) {
        ContentValues values = new ContentValues();

        values.put(com.youtubeurs.lite2.MySQLiteOpenHelper.USER_USERNAME, username);
        values.put(com.youtubeurs.lite2.MySQLiteOpenHelper.USER_NAME, name);
        values.put(com.youtubeurs.lite2.MySQLiteOpenHelper.USER_CHANNEL_IMAGE_URL, channelImageUrl);
        values.put(com.youtubeurs.lite2.MySQLiteOpenHelper.USER_INITIAL, initial);

        return bdd.insert(com.youtubeurs.lite2.MySQLiteOpenHelper.USERS_TABLE_NAME, null, values);
    }

    /**
     * Get a user
     *
     * @param username
     *            The username to return
     * @return The user or null if the request is empty
     */
    public User getUserFromUsername(String username) {
        Cursor c = bdd.query(com.youtubeurs.lite2.MySQLiteOpenHelper.USERS_TABLE_NAME, new String[] { com.youtubeurs.lite2.MySQLiteOpenHelper.USER_USERNAME, com.youtubeurs.lite2.MySQLiteOpenHelper.USER_NAME, com.youtubeurs.lite2.MySQLiteOpenHelper.USER_CHANNEL_IMAGE_URL, com.youtubeurs.lite2.MySQLiteOpenHelper.USER_INITIAL }, com.youtubeurs.lite2.MySQLiteOpenHelper.USER_USERNAME + " LIKE \"" + username + "\"", null, null, null, null);
        return cursorToUser(c);
    }

    /**
     * Convert a cursor into a user
     *
     * @param c
     *            A cursor
     * @return The first user of the cursor or null if the cursor is empty
     */
    private User cursorToUser(Cursor c) {
        if (c.getCount() == 0)
            return null;

        c.moveToFirst();

        String username = c.getString(com.youtubeurs.lite2.MySQLiteOpenHelper.USER_USERNAME_NUM);
        String name = c.getString(com.youtubeurs.lite2.MySQLiteOpenHelper.USER_NAME_NUM);
        String channelImageUrl = c.getString(com.youtubeurs.lite2.MySQLiteOpenHelper.USER_CHANNEL_IMAGE_URL_NUM);
        String initial = c.getString(com.youtubeurs.lite2.MySQLiteOpenHelper.USER_INITIAL_NUM);

        User user = new User(username, name, channelImageUrl, initial);
        c.close();

        return user;
    }

    /**
     * Get users
     *
     * @return The users or null if the request is empty
     */
    public List<User> getUsers() {
        String orderBy =  MySQLiteOpenHelper.USER_NAME + " COLLATE NOCASE ASC";
        Cursor c = bdd.query(true, com.youtubeurs.lite2.MySQLiteOpenHelper.USERS_TABLE_NAME, new String[] { com.youtubeurs.lite2.MySQLiteOpenHelper.USER_USERNAME, com.youtubeurs.lite2.MySQLiteOpenHelper.USER_NAME, com.youtubeurs.lite2.MySQLiteOpenHelper.USER_CHANNEL_IMAGE_URL, com.youtubeurs.lite2.MySQLiteOpenHelper.USER_INITIAL }, null, null, null, null, orderBy, null);
        return cursorToUsers(c);
    }

    /**
     * Convert a cursor into users
     *
     * @param c
     *            A cursor
     * @return users of the cursor or null if the cursor is empty
     */
    private List<User> cursorToUsers(Cursor c) {
        if (c.getCount() == 0)
            return new ArrayList<User>();

        List<User> list = new ArrayList<User>();

        c.moveToFirst();

        do {
            String username = c.getString(com.youtubeurs.lite2.MySQLiteOpenHelper.USER_USERNAME_NUM);
            String name = c.getString(com.youtubeurs.lite2.MySQLiteOpenHelper.USER_NAME_NUM);
            String channelImageUrl = c.getString(com.youtubeurs.lite2.MySQLiteOpenHelper.USER_CHANNEL_IMAGE_URL_NUM);
            String initial = c.getString(com.youtubeurs.lite2.MySQLiteOpenHelper.USER_INITIAL_NUM);

            User u = new User(username, name, channelImageUrl, initial);
            list.add(u);
        } while (c.moveToNext());

        c.close();

        return list;
    }

    /**
     * Remove all users no initial of the USERS table
     *
     * @return The Number of deleted rows
     */
    public int removeUsersNotInitial() {
        return bdd.delete(com.youtubeurs.lite2.MySQLiteOpenHelper.USERS_TABLE_NAME, com.youtubeurs.lite2.MySQLiteOpenHelper.USER_INITIAL + "=?", new String[] {String.valueOf("FALSE")});
    }

    /**
     * Remove all users initial of the USERS table
     *
     * @return The Number of deleted rows
     */
    public int removeUsersInitial() {
        return bdd.delete(com.youtubeurs.lite2.MySQLiteOpenHelper.USERS_TABLE_NAME, com.youtubeurs.lite2.MySQLiteOpenHelper.USER_INITIAL + "=?", new String[] {String.valueOf("TRUE")});
    }

    /**
     * Remove user of the USERS table
     * @param username
     *          The username to delete
     * @return The Number of deleted rows
     */
    public int removeUserFromUsername(String username) {
        return bdd.delete(com.youtubeurs.lite2.MySQLiteOpenHelper.USERS_TABLE_NAME, com.youtubeurs.lite2.MySQLiteOpenHelper.USER_USERNAME + "=?", new String[] {username});
    }

    /**
     * Get users count
     *
     * @return The number of users
     */
    public int getUsersCount() {
        Cursor c = bdd.query(com.youtubeurs.lite2.MySQLiteOpenHelper.USERS_TABLE_NAME, new String[] { com.youtubeurs.lite2.MySQLiteOpenHelper.USER_USERNAME, com.youtubeurs.lite2.MySQLiteOpenHelper.USER_NAME, com.youtubeurs.lite2.MySQLiteOpenHelper.USER_CHANNEL_IMAGE_URL, com.youtubeurs.lite2.MySQLiteOpenHelper.USER_INITIAL }, null, null, null, null, null);
        return c.getCount();
    }

	/*---------------------------------------------------- PARAMS TABLE ---------------------------------------------------*/
	/**
	 * Insert a parameter into the PARAMS table
	 * 
	 * @param param
	 *            The parameter to insert
	 * @param value
	 *            The value of the parameter
	 * @return The row ID of the newly inserted row, or -1 if an error occurred
	 */
	public long insertParam(String param, String value) {
		ContentValues values = new ContentValues();

		values.put(com.youtubeurs.lite2.MySQLiteOpenHelper.PARAMS_NAME, param);
		values.put(com.youtubeurs.lite2.MySQLiteOpenHelper.PARAMS_VALUE, value);

		return bdd.insert(com.youtubeurs.lite2.MySQLiteOpenHelper.PARAMS_TABLE_NAME, null, values);
	}

	/**
	 * Update a parameter into the PARAMS table
	 * 
	 * @param param
	 *            The parameter to update
	 * @param value
	 *            The value of the parameter
	 * @return the number of rows affected by the update
	 */
	public int updateParam(String param, String value) {
		ContentValues values = new ContentValues();

		values.put(com.youtubeurs.lite2.MySQLiteOpenHelper.PARAMS_VALUE, value);

		return bdd.update(com.youtubeurs.lite2.MySQLiteOpenHelper.PARAMS_TABLE_NAME, values, com.youtubeurs.lite2.MySQLiteOpenHelper.PARAMS_NAME + "=?", new String[] { param });
	}

	/**
	 * Get a parameter
	 * 
	 * @param param
	 *            The parameter to return
	 * @return The parameter value or null if the request is empty
	 */
	public String getParam(String param) {
		Cursor c = bdd.query(com.youtubeurs.lite2.MySQLiteOpenHelper.PARAMS_TABLE_NAME, new String[] { com.youtubeurs.lite2.MySQLiteOpenHelper.PARAMS_NAME,
				com.youtubeurs.lite2.MySQLiteOpenHelper.PARAMS_VALUE }, com.youtubeurs.lite2.MySQLiteOpenHelper.PARAMS_NAME + " LIKE \"" + param + "\"", null, null, null, null);
		return cursorToParam(c);
	}

	/**
	 * Convert a cursor into a parameter
	 * 
	 * @param c
	 *            A cursor
	 * @return The first parameter of the cursor or null if the cursor is empty
	 */
	private String cursorToParam(Cursor c) {
		if (c.getCount() == 0)
			return null;

		c.moveToFirst();

		String value = c.getString(com.youtubeurs.lite2.MySQLiteOpenHelper.PARAMS_VALUE_NUM);

		c.close();

		return value;
	}

	/**
	 * Remove all rows of the PARAM table
	 * 
	 * @return The Number of deleted rows
	 */
	public int removeParams() {
		return bdd.delete(com.youtubeurs.lite2.MySQLiteOpenHelper.PARAMS_TABLE_NAME, "1", null);
	}

	/*---------------------------------------------------- VIDEOS TABLE ---------------------------------------------------*/
	/**
	 * Insert a video into the VIDEOS table
	 * 
	 * @param video
	 *            The video to insert
	 * @return The row ID of the newly inserted row, or -1 if an error occurred
	 */
	public long insertVideo(Video video) {
		ContentValues values = new ContentValues();

		values.put(com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEO_URL, video.getUrl());
		values.put(com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEO_USERNAME, video.getUsername());
		values.put(com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEO_TITLE, video.getTitle());
		values.put(com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEO_THUMB_URL, video.getThumbUrl());
		values.put(com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEO_COUNT, video.getCount());
        values.put(com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEO_DATE, video.getUploaded());

		return bdd.insert(com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEOS_TABLE_NAME, null, values);
	}

	/**
	 * Update a video into the VIDEOS table
	 * 
	 * @param video
	 *            The video to update
	 * @return the number of rows affected by the update
	 */
	public int updateVideo(Video video) {
		ContentValues values = new ContentValues();

		values.put(com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEO_TITLE, video.getTitle());
		values.put(com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEO_THUMB_URL, video.getThumbUrl());
		values.put(com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEO_COUNT, video.getCount());

		return bdd.update(com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEOS_TABLE_NAME, values, com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEO_URL + "=?", new String[] { video.getUrl() });
	}

	/**
	 * Remove the video of the given URL
	 * 
	 * @param url
	 *            The URL of the video to remove
	 * @return The number of deleted rows
	 */
	public int removeVideoWithURL(String url) {
		return bdd.delete(com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEOS_TABLE_NAME, com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEO_URL + " = " + url, null);
	}

	/**
	 * Remove all rows of the VIDEOS table
	 * 
	 * @return The Number of deleted rows
	 */
	public int removeVideos() {
		return bdd.delete(com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEOS_TABLE_NAME, "1", null);
	}

    /**
     * Remove all rows of the VIDEOS table from username
     * @param username
     *          The user to delete
     * @return The Number of deleted rows
     */
    public int removeVideosFromUsername(String username) {
        return bdd.delete(com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEOS_TABLE_NAME, com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEO_USERNAME + "=?", new String[] {username});
    }

	/**
	 * Get a video
	 * 
	 * @param url
	 *            The url of the video to return
	 * @return The video or null if the request is empty
	 */
	public Video getVideoWithURL(String url) {
		Cursor c = bdd.query(com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEOS_TABLE_NAME, new String[] { com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEO_URL,
				com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEO_USERNAME, com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEO_TITLE, com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEO_THUMB_URL,
				com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEO_COUNT }, com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEO_URL + " LIKE \"" + url + "\"", null, null, null, null);
		return cursorToVideo(c);
	}

	/**
	 * Convert a cursor into a video
	 * 
	 * @param c
	 *            A cursor
	 * @return The first video of the cursor or null if the cursor is empty
	 */
	private Video cursorToVideo(Cursor c) {
		if (c.getCount() == 0)
			return null;

		c.moveToFirst();

		Video video = new Video();
		video.setUrl(c.getString(com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEO_URL_NUM));
		video.setTitle(c.getString(com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEO_TITLE_NUM));
		video.setThumbUrl(c.getString(com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEO_THUMB_URL_NUM));
		video.setCount(c.getInt(com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEO_COUNT_NUM));

		c.close();

		return video;
	}

	/**
	 * Get videos
	 * 
	 * @return The videos or null if the request is empty
	 */
	public List<Video> getVideos(String username) {
        String orderBy =  MySQLiteOpenHelper.VIDEO_DATE + " DESC";
		Cursor c = bdd.query(com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEOS_TABLE_NAME, new String[] { com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEO_URL,
				com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEO_USERNAME, com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEO_TITLE, com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEO_THUMB_URL,
				com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEO_COUNT, com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEO_DATE }, com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEO_USERNAME + " LIKE ?", new String[] { username }, null, null, orderBy);
		return cursorToVideos(c);
	}

	/**
	 * Convert a cursor into videos
	 * 
	 * @param c
	 *            A cursor
	 * @return Videos of the cursor or null if the cursor is empty
	 */
	private List<Video> cursorToVideos(Cursor c) {
		if (c.getCount() == 0)
			return new ArrayList<Video>();

		List<Video> list = new ArrayList<Video>();

		c.moveToFirst();

		do {
			Video video = new Video();
			video.setUrl(c.getString(com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEO_URL_NUM));
			video.setUsername(c.getString(com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEO_USERNAME_NUM));
			video.setTitle(c.getString(com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEO_TITLE_NUM));
			video.setThumbUrl(c.getString(com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEO_THUMB_URL_NUM));
			video.setCount(c.getInt(com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEO_COUNT_NUM));
            video.setUploaded(c.getString(com.youtubeurs.lite2.MySQLiteOpenHelper.VIDEO_DATE_NUM));
			list.add(video);
		} while (c.moveToNext());

		c.close();

		return list;
	}
}
