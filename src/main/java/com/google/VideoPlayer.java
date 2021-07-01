package com.google;

import java.util.*;

public class VideoPlayer {

  private final VideoLibrary videoLibrary;
  private Video currentlyPlaying;
  private boolean paused;

  /*
  We're using a hashmap to match keys in a non case-sensitive manner by setting and comparing
  them all in lowercase. However, we're storing the name and the list of video IDs in a playlist
  object to allow better and cleaner access and manipulation of the data and to retain the case
  sensitive name that was originally input.
   */
  private HashMap<String, Playlist> playlists;

  public VideoPlayer() {
    this.videoLibrary = new VideoLibrary();
    this.currentlyPlaying = null;
    this.paused = false;
    this.playlists = new HashMap<>();
  }

  public void numberOfVideos() {
    System.out.printf("%s videos in the library%n", videoLibrary.getVideos().size());
  }

  public void showAllVideos() {
    System.out.println("Here's a list of all available videos:");

    List<Video> videoList = videoLibrary.getVideos();
    //This sorts the temporary list alphabetically, according to title
    Collections.sort(videoList, Comparator.comparing(Video::getTitle));

    for(Video currentVid : videoList) {
      System.out.printf("%s%n", currentVid.toString());
    }
  }

  public void playVideo(String videoId) {
    Video tempVideo = videoLibrary.getVideo(videoId);

    //Check if video exists
    if (tempVideo == null) {
      System.out.println("Cannot play video: Video does not exist");
    }
    //Check if the video is flagged
    else if (tempVideo.isFlagged()) {
      System.out.println("Cannot play video: Video is currently flagged (reason: " + tempVideo.getFlagReason() + ")");
    }
    //If not and no other vide is playing, then play this one
    else if (currentlyPlaying == null) {
      setCurrentlyPlaying(tempVideo);
    }
    //Otherwise first stop the currently playing video before playing the next
    else {
      stopVideo();
      setCurrentlyPlaying(tempVideo);
    }
  }

  private void setCurrentlyPlaying (Video video) {
    //This method allows this section of code to be re-used to eliminate redundancy
    //setting to a value of null would mean that no video is playing
    currentlyPlaying = video;
    System.out.println("Playing video: " + currentlyPlaying.getTitle());
    paused = false;
  }

  public void stopVideo() {
    //If there is a video playing, print that we're stopping it and then stop it
    if (currentlyPlaying != null) {
      System.out.println("Stopping video: " + currentlyPlaying.getTitle());
      currentlyPlaying = null;
      paused = false;
    }
    else {
      System.out.println("Cannot stop video: No video is currently playing");
    }
  }

  public void playRandomVideo() {
    //Create a list for all the videos. This is so we can sort through them before picking one randomly
    List<Video> videoList = new ArrayList<>();

    //Search through all videos and check if they have been flagged
    for(Video video:videoLibrary.getVideos()) {
      //Any unflagged videos are added to the list to be randomly chosen from
      if (!video.isFlagged()) {
        videoList.add(video);
      }
    }

    //If the list isn't empty, there are videos to choose from, so generate a random number
    //from 0 to the size of the list to pick one of the videos and then play it.
    if(!videoList.isEmpty()) {
      playVideo(videoList.get(new Random().nextInt(videoList.size())).getVideoId());
    }
    //If the list is empty, there were no unflagged videos, or maybe none at all
    else {
      System.out.println("No videos available");
    }
  }

  public void pauseVideo() {
    //First check if there is in fact a video playing
    if (currentlyPlaying == null) {
      System.out.println("Cannot pause video: No video is currently playing");
    }
    //If there is, check if it's already paused
    else if (paused) {
      System.out.println("Video already paused: " + currentlyPlaying.getTitle());
    }
    //And if it's not then pause it
    else {
      paused = true;
      System.out.println("Pausing video: " + currentlyPlaying.getTitle());
    }
  }

  public void continueVideo() {
    //First check if there is a video playing
    if (currentlyPlaying == null) {
      System.out.println("Cannot continue video: No video is currently playing");
    }
    //Then check if it is paused, if so, unpause it
    else if (paused) {
      System.out.println("Continuing video: " + currentlyPlaying.getTitle());
      paused = false;
    }
    //Otherwise the video isn't paused
    else {
      System.out.println("Cannot continue video: Video is not paused");
    }
  }

  public void showPlaying() {
    //Check if there is a video playing
    if (currentlyPlaying == null) {
      System.out.println("No video is currently playing");
    }
    //Check if it's paused
    else if (paused) {
      System.out.println("Currently playing: " + currentlyPlaying.toString() + " - PAUSED");
    }
    //If not, there must be one currently playing and unpaused, let it be known
    else {
      System.out.println("Currently playing: " + currentlyPlaying.toString());
    }
  }

  public void createPlaylist(String playlistName) {
    //We use this key string to remove redundant uses of this long phrase and make the code more readable
    String key = playlistName.toLowerCase(Locale.ROOT);

    //Check if this playlist exists before attempting to create it
    if (!playlists.containsKey(key)) {
      playlists.put(key, new Playlist(playlistName));
      System.out.println("Successfully created new playlist: " + playlistName);
    }
    //Let the user know if it exists
    else {
      System.out.println("Cannot create playlist: A playlist with the same name already exists");
    }
  }

  public void addVideoToPlaylist(String playlistName, String videoID) {
    //We use this key string to remove redundant uses of this long phrase and make the code more readable
    String key = playlistName.toLowerCase(Locale.ROOT);

    //First check if the playlist in question actually exists
    if (playlists.containsKey(key)) {
      //We use tempVideo here to reduce the amount of text
      Video tempVideo = videoLibrary.getVideo(videoID);

      //If we pulled null out from that call, then the video doesn't exist
      if (tempVideo == null) {
        System.out.println("Cannot add video to " + playlistName + ": Video does not exist");
      }
      //If we got a video, then check if it's flagged
      else if (tempVideo.isFlagged()) {
        System.out.println("Cannot add video to " + playlistName + ": Video is currently flagged (reason: " +
                tempVideo.getFlagReason() + ")");
      }
      //If it exists and is unflagged, then check if it's already in the playlist
      else if (playlists.get(key).contains(videoID)) {
        System.out.println("Cannot add video to " + playlistName + ": Video already added");
      }
      //Finally we can add the allowed video to the playlist
      else {
        System.out.println("Added video to " + playlistName + ": " + videoLibrary.getVideo(videoID).getTitle());
        playlists.get(key).addVideoToPlaylist(videoID);
      }
    }
    //If the playlist doesn't actually exist, let the user know
    else {
      System.out.println("Cannot add video to " + playlistName + ": Playlist does not exist");
    }
  }

  public void showAllPlaylists() {
    //First check if there are any playlists to show
    if (playlists.isEmpty()) {
      System.out.println("No playlists exist yet");
    }
    //If there are then show them
    else {
      System.out.println("Showing all playlists:");
      //Iterates through all the playlists, printing out each of their proper cased names
      for (Playlist playlist:playlists.values()) {
        System.out.println(playlist.getPlaylistName());
      }
    }
  }

  public void showPlaylist(String playlistName) {
    //We use this key string to remove redundant uses of this long phrase and make the code more readable
    String key = playlistName.toLowerCase(Locale.ROOT);

    //First check if the palylist exists
    if (!playlists.containsKey(key)) {
      System.out.println("Cannot show playlist " + playlistName + ": Playlist does not exist");
    }
    //Then check if it has any videos saved in it
    else if (playlists.get(key).getVideos() == null) {
      System.out.println("Showing playlist: " + playlistName + "\nNo videos here yet");
    }
    //If it does then show them
    else {
      //First print out the playlist's name
      System.out.println("Showing playlist: " + playlistName);

      //Then iterate through each video and print out their details
      for (String videoID:playlists.get(key).getVideos()) {
        System.out.println(videoLibrary.getVideo(videoID).toString());
      }
    }
  }

  public void removeFromPlaylist(String playlistName, String videoId) {
    //We use this key string to remove redundant uses of this long phrase and make the code more readable
    String key = playlistName.toLowerCase(Locale.ROOT);

    //First check if the playlist exists
    if (!playlists.containsKey(key)) {
      System.out.println("Cannot remove video from " + playlistName + ": Playlist does not exist");
    }
    //Then check if the video exists
    else if (videoLibrary.getVideo(videoId) == null) {
      System.out.println("Cannot remove video from " + playlistName + ": Video does not exist");
    }
    //Then check if the existing video exists in the existing playlist
    else if (!playlists.get(key).contains(videoId)) {
      System.out.println("Cannot remove video from " + playlistName + ": Video is not in playlist");
    }
    //Finally removing the video that exists fro the playlist that exists so that it no longer exists in the playlist
    else {
      playlists.get(key).removeVideoFromPlaylist(videoId);
      System.out.println("Removed video from " + playlistName + ": " + videoLibrary.getVideo(videoId).getTitle());
    }
  }

  public void clearPlaylist(String playlistName) {
    //We use this key string to remove redundant uses of this long phrase and make the code more readable
    String key = playlistName.toLowerCase(Locale.ROOT);

    //First check if the playlist exists
    if (!playlists.containsKey(key)) {
      System.out.println("Cannot clear playlist " + playlistName + ": Playlist does not exist");
    }
    //If it does, go ahead and clear it out
    else {
      playlists.get(key).clearPlaylist();
      System.out.println("Successfully removed all videos from " + playlistName);
    }
  }

  public void deletePlaylist(String playlistName) {
    //We use this key string to remove redundant uses of this long phrase and make the code more readable
    String key = playlistName.toLowerCase(Locale.ROOT);

    //First check if the playlist exists
    if (!playlists.containsKey(key)) {
      System.out.println("Cannot delete playlist " + playlistName + ": Playlist does not exist");
    }
    //If it does exist then... make it stop doing that
    else {
      playlists.remove(key);
      System.out.println("Deleted playlist: " + playlistName);
    }
  }

  public void searchVideos(String searchTerm) {
    //Get a local copy of all video objects to sort and search through
    //Then sort them alphabetically
    List<Video> videoList = videoLibrary.getVideos();
    Collections.sort(videoList, Comparator.comparing(Video::getTitle));
    //Initialise a list to store positive search results for use later
    List<Video> results = new ArrayList<Video>();

    //search through videos
    for (Video video:videoList) {
      //If the term matches in the title, collect the result
      if (video.getTitle().toLowerCase(Locale.ROOT).contains(searchTerm.toLowerCase(Locale.ROOT)) && !video.isFlagged()) {
        results.add(video);
      }
    }

    //If the results came up empty, let the user know
    if (results.isEmpty()) {
      System.out.println("No search results for " + searchTerm);
    }
    //If there are results to show, show them!
    else {
      System.out.println("Here are the results for " + searchTerm +":");

      //Print and number the search results
      int iteration = 1;
      for (Video video:results) {
        System.out.println(iteration + ") " + video.toString());
        iteration ++;
      }

      System.out.println("Would you like to play any of the above? If yes, specify the number of the video." +
              "\nIf your answer is not a valid number, we will assume it's a no.");

      //Take user input to the above question
      int index;
      var input = new Scanner(System.in).nextLine();
      try {
        //try and parse user input number into an indexable integer
        index = Integer.parseInt(input);

        //And if that was possible then we proceed to play the requested video
        //We subtract one to align the uer friendly 1-x numbering system to the
        //better 0-x numbering system our program uses
        playVideo(results.get(index - 1).getVideoId());
      }
      catch (Exception e) {
        //If an error is caught, we assume their response was a no,
        //clearly not an integer, and the program continues on
      }
    }
  }

  public void searchVideosWithTag(String videoTag) {
    //Firstly, if this is not a tag, then don't even bother with it!
    if ((videoTag.charAt(0) + "").equals("#")) {
      //Get a local copy of all video objects to sort and search through
      //Then sort them alphabetically
      List<Video> videoList = videoLibrary.getVideos();
      Collections.sort(videoList, Comparator.comparing(Video::getTitle));
      //Initialise a list to store positive search results for use later
      List<Video> results = new ArrayList<Video>();

      //search through videos
      for (Video video : videoList) {
        //If the term matches in the title, collect the result
        if (video.getTags().contains(videoTag) && !video.isFlagged()) {
          results.add(video);
        }
      }

      //If there are no results to show, let the user know
      if (results.isEmpty()) {
        System.out.println("No search results for " + videoTag);
      }
      //And if there are results then show them!
      else {
        System.out.println("Here are the results for " + videoTag + ":");

        //Print and number the search results
        int iteration = 1;
        for (Video video : results) {
          System.out.println(iteration + ") " + video.toString());
          iteration++;
        }

        System.out.println("Would you like to play any of the above? If yes, specify the number of the video." +
                "\nIf your answer is not a valid number, we will assume it's a no.");

        //Take user input to the above question
        int index;
        var input = new Scanner(System.in).nextLine();
        try {
          //try and parse user input number into an indexable integer
          index = Integer.parseInt(input);

          //And if that was possible then we proceed to play the requested video
          //We subtract one to align the uer friendly 1-x numbering system to the
          //better 0-x numbering system our program uses
          playVideo(results.get(index - 1).getVideoId());
        } catch (Exception e) {
          //If an error is caught, we assume their response was a no,
          //clearly not an integer, and the program continues on
        }
      }
    }
    else {
      System.out.println("No search results for " + videoTag);
    }
  }

  public void flagVideo(String videoId) {
    //Adds default reason in lieu of being given one, then refers to the overloading method
    //for error checking in order to avoid redundant coding
    flagVideo(videoId, "Not supplied");
  }

  public void flagVideo(String videoId, String reason) {
    //Check if video exists
    if (videoLibrary.getVideo(videoId) == null) {
      System.out.println("Cannot flag video: Video does not exist");
    }
    //Then check if the video is already flagged
    else if (videoLibrary.getVideo(videoId).isFlagged()) {
      System.out.println("Cannot flag video: Video is already flagged");
    }
    //Finally allow the flagging to occur
    else {
      //Check if there is a video currently playing and if it's the same one getting flagged
      //If so, stop it playing before flagging
      if (currentlyPlaying != null) {
        if (currentlyPlaying.getVideoId().equals(videoId)) {
          stopVideo();
        }
      }

      //Finally flag the video and set the reason
      videoLibrary.getVideo(videoId).setFlagged(true);
      videoLibrary.getVideo(videoId).setFlagReason(reason);
      //Then let the user know it is done
      System.out.println("Successfully flagged video: " + videoLibrary.getVideo(videoId).getTitle() +
              " (reason: " + videoLibrary.getVideo(videoId).getFlagReason() + ")");
    }
  }

  public void allowVideo(String videoId) {
    //Check if video exists
    if (videoLibrary.getVideo(videoId) == null) {
      System.out.println("Cannot remove flag from video: Video does not exist");
    }
    //Then check if the video is actually flagged
    else if (!videoLibrary.getVideo(videoId).isFlagged()) {
      System.out.println("Cannot remove flag from video: Video is not flagged");
    }
    //Finally allow it to be unflagged
    else {
      videoLibrary.getVideo(videoId).setFlagged(false);
      videoLibrary.getVideo(videoId).setFlagReason(null);
      //Then let the user know
      System.out.println("Successfully removed flag from video: " + videoLibrary.getVideo(videoId).getTitle());
    }
  }
}