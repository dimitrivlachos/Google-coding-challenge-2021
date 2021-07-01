package com.google;

import java.util.ArrayList;
import java.util.List;

/*
This class allows us to store the proper case sensitive name of the playlist along with it's contents
and easily manage and manipulate them, keeping all the playlist related methods contained, such as
clearing the playlist, allowing this to be done with one simple command in the higher methods.
 */

public class Playlist {
    private final String playlistName;
    private List<String> videoIDs;

    public Playlist(String playlistName) {
        this.playlistName = playlistName;
        this.videoIDs = new ArrayList<>();
    }

    public void addVideoToPlaylist(String videoID) {
        videoIDs.add(videoID);
    }

    public void removeVideoFromPlaylist(String videoID) {
        videoIDs.remove(videoID);
    }

    public void clearPlaylist () {
        videoIDs = new ArrayList<>();
    }

    public List<String> getVideos() {
        /*
        We need to make sure that an empty array returns null and not just an empty array,
        this way we know when the array is in fact empty.
         */
        if (videoIDs.size() == 0)
            return null;
        return videoIDs;
    }

    public boolean contains (String videoID) {
        return videoIDs.contains(videoID);
    }

    public String getPlaylistName() {
        return playlistName;
    }
}
