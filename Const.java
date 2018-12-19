package com.chocolateradio.Custom;

/**
 * Created by Saumil Shah on 1/18/2018.
 */

public class Const {

    /**
     * Preference file name where you can store data in device
     */
    public static final String PREFS_FILENAME = "ChocolateRadio";
    public static final String USER_ID = "user_id";
    public static final String NAME = "name";
    public static final String LOGIN_ACCESS = "login_access";
    public static final String CHECK = "check";
    public static final String FCMID = "fcmid";
    public static final String SHUFFLE = "shuffle";
    public static final String REPEAT = "repeat";
    public static final String NOW_PLAYING= "nowplaying";
    public static final String NOTIFICATION_STATUS= "notification_status";

//    Total API

    public static final String HOST_CHOCOLATERADIO= "http://durisimomobileapps.net/chocolateradio/api/";
    public static final String REGISTER_USER = HOST_CHOCOLATERADIO + "add_user";
    public static final String SIGNUP_USER = HOST_CHOCOLATERADIO + "signup";
    public static final String ALL_SONGS = HOST_CHOCOLATERADIO + "song_all";
    public static final String RECENT_SONGS = HOST_CHOCOLATERADIO + "recentupload";
    public static final String RADIO = HOST_CHOCOLATERADIO + "radio";
    public static final String MIXES= HOST_CHOCOLATERADIO + "mixes";
    public static final String MIXES_SONGS= HOST_CHOCOLATERADIO + "mixes_song";
    public static final String GENRES= HOST_CHOCOLATERADIO + "genres";
    public static final String GENRES_SONGS = HOST_CHOCOLATERADIO + "genres_song";
    public static final String ARTIST= HOST_CHOCOLATERADIO + "artist_albums";
    public static final String ARTIST_SONGS= HOST_CHOCOLATERADIO + "artist_song";
    public static final String IMAGE_ALBUMS = HOST_CHOCOLATERADIO + "image_folder";
    public static final String IMAGES = HOST_CHOCOLATERADIO + "gallery_folder";
    public static final String SPONSORS= HOST_CHOCOLATERADIO + "sponsor";
    public static final String VIDEOS = HOST_CHOCOLATERADIO + "video";
    public static final String EVENTS = HOST_CHOCOLATERADIO + "event";
    public static final String ADD_REMOVE_LIKE = HOST_CHOCOLATERADIO + "like_song";
    public static final String ADD_REMOVE_FAVORITE = HOST_CHOCOLATERADIO + "favorite";
    public static final String FAVORITE_SONGS = HOST_CHOCOLATERADIO + "getallfavorite";
    public static final String HIT_SINGLES = HOST_CHOCOLATERADIO + "hitsingles";
    public static final String CREATE_PLAYLIST = HOST_CHOCOLATERADIO + "newplaylist";
    public static final String GET_PLAYLIST = HOST_CHOCOLATERADIO + "getplaylist";
    public static final String ADD_SONG_PLAYLIST = HOST_CHOCOLATERADIO + "addsong";
    public static final String REMOVE_SONG_PLAYLIST = HOST_CHOCOLATERADIO + "removesongfromplaylist";
    public static final String REMOVE_PLAYLIST = HOST_CHOCOLATERADIO + "removeplaylist";
    public static final String SHARE_APP = HOST_CHOCOLATERADIO + "shareapp";
    public static final String SHARE_SONG = HOST_CHOCOLATERADIO + "sharesong/{song_id}";
    public static final String TOTALPLAYED_SONG = HOST_CHOCOLATERADIO + "totalplayed/{song_id}";
    public static final String SONG_DETAILS = HOST_CHOCOLATERADIO + "songdetails";
    public static final String SEARCH_SONG = HOST_CHOCOLATERADIO + "search";
    public static final String INSTAGRAM = HOST_CHOCOLATERADIO + "instagramlink";
    public static final String NOTIFICATION= HOST_CHOCOLATERADIO + "notification/{user_id}";
    //    public static final String REMOVE_FAVORITE = HOST_CHOCOLATERADIO + "removefavoritesong";
    //    public static final String GET_SONG_PLAYLIST = HOST_CHOCOLATERADIO + "getsongbyplaylist";

}