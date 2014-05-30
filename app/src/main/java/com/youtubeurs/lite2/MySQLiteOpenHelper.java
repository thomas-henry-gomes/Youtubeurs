package com.youtubeurs.lite2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 3;
	public static final String DATABASE_NAME = "youtubeurs";

	public static final String VIDEOS_TABLE_NAME = "videos";
	public static final String VIDEO_URL = "url";
	public static final int VIDEO_URL_NUM = 0;
	public static final String VIDEO_USERNAME = "username";
	public static final int VIDEO_USERNAME_NUM = 1;
	public static final String VIDEO_TITLE = "title";
	public static final int VIDEO_TITLE_NUM = 2;
	public static final String VIDEO_THUMB_URL = "thumb_url";
	public static final int VIDEO_THUMB_URL_NUM = 3;
	public static final String VIDEO_COUNT = "count";
	public static final int VIDEO_COUNT_NUM = 4;
    public static final String VIDEO_DATE = "date";
    public static final int VIDEO_DATE_NUM = 5;

	public static final String PARAMS_TABLE_NAME = "params";
	public static final String PARAMS_NAME = "name";
	public static final int PARAMS_NAME_NUM = 0;
	public static final String PARAMS_VALUE = "value";
	public static final int PARAMS_VALUE_NUM = 1;

    public static final String USERS_TABLE_NAME = "users";
    public static final String USER_USERNAME = "username";
    public static final int USER_USERNAME_NUM = 0;
    public static final String USER_NAME = "name";
    public static final int USER_NAME_NUM = 1;
    public static final String USER_CHANNEL_IMAGE_URL = "channel_image_url";
    public static final int USER_CHANNEL_IMAGE_URL_NUM = 2;
    public static final String USER_INITIAL = "initial";
    public static final int USER_INITIAL_NUM = 3;


	private static final String VIDEOS_TABLE_CREATE = "CREATE TABLE " + VIDEOS_TABLE_NAME + " (" + VIDEO_URL + " TEXT NOT NULL PRIMARY KEY, " + VIDEO_USERNAME + " TEXT NOT NULL, " + VIDEO_TITLE + " TEXT NOT NULL, " + VIDEO_THUMB_URL + " TEXT NOT NULL, " + VIDEO_COUNT + " INTEGER, " + VIDEO_DATE + " TEXT NOT NULL);";
	private static final String PARAMS_TABLE_CREATE = "CREATE TABLE " + PARAMS_TABLE_NAME + " (" + PARAMS_NAME + " TEXT NOT NULL PRIMARY KEY, " + PARAMS_VALUE + " TEXT NOT NULL);";
    private static final String USERS_TABLE_CREATE  = "CREATE TABLE " + USERS_TABLE_NAME  + " (" + USER_USERNAME + " TEXT NOT NULL PRIMARY KEY, " + USER_NAME + " TEXT NOT NULL, " + USER_CHANNEL_IMAGE_URL + " TEXT NOT NULL, " + USER_INITIAL + " TEXT NOT NULL);";

	MySQLiteOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(VIDEOS_TABLE_CREATE);
		db.execSQL(PARAMS_TABLE_CREATE);
        db.execSQL(USERS_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + VIDEOS_TABLE_NAME);
        db.execSQL(VIDEOS_TABLE_CREATE);

        db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE_NAME);
        db.execSQL(USERS_TABLE_CREATE);
	}
}
