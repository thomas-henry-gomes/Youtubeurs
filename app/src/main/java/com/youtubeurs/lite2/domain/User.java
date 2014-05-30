package com.youtubeurs.lite2.domain;

/**
 * This is a representation of a user
 */
public class User {
    private String username;
    private String name;
    private String channelImageUrl;
    private String initial;

    public User(String username, String name, String channelImageUrl, String initial){
        this.username = username.trim();
        this.name = name.trim();
        this.channelImageUrl = channelImageUrl.trim();
        this.initial = initial.trim();
    }

    public User(){
        username = "";
        name = "";
        channelImageUrl = "";
        initial = "";
    }

    /**
     * Tell if the user is an initial application user
     * @return True if the user is a initial user or false if not
     */
    public Boolean isInitial(){
        if("TRUE".equals(initial))
            return true;
        else
            return false;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChannelImageUrl() {
        return channelImageUrl;
    }

    public void setChannelImageUrl(String channelImageUrl) {
        this.channelImageUrl = channelImageUrl;
    }

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }
}
