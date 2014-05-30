package com.youtubeurs.lite2.domain;

import java.io.Serializable;
import java.util.List;

/**
 * This is the 'library' of all the users videos
 * 
 * @author paul.blundell
 */
public class Library implements Serializable {

	private static final long serialVersionUID = 684272754074651104L;

	// The username of the owner of the library
	private String user;
	// A list of videos that the user owns
	private List<Video> videos;

	public Library(String user, List<Video> videos) {
		this.user = user;
		this.videos = videos;
	}

	/**
	 * @return the user name
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @return the videos
	 */
	public List<Video> getVideos() {
		return videos;
	}
}