package com.youtubeurs.lite2.domain;

import java.io.Serializable;

/**
 * This is a representation of a users video of YouTube
 */
public class Video implements Serializable {

	private static final long serialVersionUID = -428357553250429316L;

	private String title;
	private String url;
	private String username;
	private String thumbUrl;
	private int count;
    private String uploaded;

	public Video(String title, String url, String username, String thumbUrl, int count, String uploaded) {
		super();
		this.title = title;
		this.url = url;
		this.username = username;
		this.thumbUrl = thumbUrl;
		this.count = count;
        this.uploaded = uploaded;
	}

	public Video() {
		super();
		this.title = "";
		this.url = "";
		this.username = "";
		this.thumbUrl = "";
		this.count = 0;
        this.uploaded = "0";
	}

	/**
	 * @return the title of the video
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the url to this video on youtube
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return the thumbUrl of a still image representation of this video
	 */
	public String getThumbUrl() {
		return thumbUrl;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @param thumbUrl
	 *            the thumbUrl to set
	 */
	public void setThumbUrl(String thumbUrl) {
		this.thumbUrl = thumbUrl;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count
	 *            the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

    /**
     * @return the uploaded date
     */
    public String getUploaded() {
        return uploaded;
    }

    /**
     * @param uploaded
     *            the uploaded date
     */
    public void setUploaded(String uploaded) {
        this.uploaded = uploaded;
    }
}