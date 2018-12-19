package com.chocolateradio.Data.Remote;

import com.chocolateradio.Custom.Const;
import com.chocolateradio.Data.Model.AddSongPlaylist;
import com.chocolateradio.Data.Model.AllSongsResponse;
import com.chocolateradio.Data.Model.ArtistResponse;
import com.chocolateradio.Data.Model.CreatePlaylistResponse;
import com.chocolateradio.Data.Model.CurrentSongDetailResponse;
import com.chocolateradio.Data.Model.EventsResponse;
import com.chocolateradio.Data.Model.FavoriteResponse;
import com.chocolateradio.Data.Model.GenresResponse;
import com.chocolateradio.Data.Model.GetPlayListResponse;
import com.chocolateradio.Data.Model.ImageAlbumResponse;
import com.chocolateradio.Data.Model.ImagesResponse;
import com.chocolateradio.Data.Model.InstagramResponse;
import com.chocolateradio.Data.Model.LikeResponse;
import com.chocolateradio.Data.Model.MixesResponse;
import com.chocolateradio.Data.Model.NotificationsResponse;
import com.chocolateradio.Data.Model.RadioResponse;
import com.chocolateradio.Data.Model.RecentSongsResponse;
import com.chocolateradio.Data.Model.RegisterUserResponse;
import com.chocolateradio.Data.Model.ShareResponse;
import com.chocolateradio.Data.Model.SimpleResponse;
import com.chocolateradio.Data.Model.SongShareResponse;
import com.chocolateradio.Data.Model.SongsResponse;
import com.chocolateradio.Data.Model.SponsorResponse;
import com.chocolateradio.Data.Model.TotalPlayedResponse;
import com.chocolateradio.Data.Model.VideosResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Saumil on 1/11/2018.
 */

public interface APIService {

    @POST(Const.REGISTER_USER)
    @FormUrlEncoded
    Call<RegisterUserResponse> registeruser(@Field("u_id") String user_id, @Field("fcm_id") String fcm_id);

    @POST(Const.SIGNUP_USER)
    @FormUrlEncoded
    Call<SimpleResponse> signupuser(@Field("signup_email") String email, @Field("signup_phone") String phone);

    @GET(Const.RADIO)
    Call<RadioResponse> getradiodetails();

    @GET(Const.ARTIST)
    Call<ArtistResponse> getartist();

    @POST(Const.ARTIST_SONGS)
    @FormUrlEncoded
    Call<SongsResponse> getartistsongs(@Field("u_id") String user_id, @Field("artist_id") String artist_id);

    @GET(Const.IMAGE_ALBUMS)
    Call<ImageAlbumResponse> getimagealbums();

    @POST(Const.IMAGES)
    @FormUrlEncoded
    Call<ImagesResponse> getimages(@Field("image_id") String image_id);

    @GET(Const.GENRES)
    Call<GenresResponse> getgenres();

    @POST(Const.GENRES_SONGS)
    @FormUrlEncoded
    Call<SongsResponse> getgenressongs(@Field("u_id") String user_id, @Field("genres_id") String genres_id);

    @GET(Const.MIXES)
    Call<MixesResponse> getmixes();

    @POST(Const.MIXES_SONGS)
    @FormUrlEncoded
    Call<SongsResponse> getmixesongs(@Field("u_id") String user_id, @Field("mixes_id") String mixes_id);

    @GET(Const.VIDEOS)
    Call<VideosResponse> getvideos();

    @GET(Const.SHARE_APP)
    Call<ShareResponse> getsharedetails();

    @GET(Const.SPONSORS)
    Call<SponsorResponse> getsponsordetails();

    @GET(Const.NOTIFICATION)
    Call<NotificationsResponse> getnotifications(@Path("user_id") String user_id);

    @GET(Const.SHARE_SONG)
    Call<SongShareResponse> getsongshare(@Path("song_id") String user_id);

    @GET(Const.TOTALPLAYED_SONG)
    Call<TotalPlayedResponse> settotalplayed(@Path("song_id") String song_id);

    @POST(Const.SONG_DETAILS)
    @FormUrlEncoded
    Call<CurrentSongDetailResponse> getcurrentsongdetails(@Field("song_id") String song_id, @Field("u_id") String user_id);

    @POST(Const.SEARCH_SONG)
    @FormUrlEncoded
    Call<SongsResponse> getsearchedsongdetails(@Field("keyword") String keyword, @Field("u_id") String user_id);

    @POST(Const.RECENT_SONGS)
    @FormUrlEncoded
    Call<RecentSongsResponse> getrecentsongs(@Field("u_id") String user_id);

    @POST(Const.REMOVE_SONG_PLAYLIST)
    @FormUrlEncoded
    Call<SimpleResponse> deletesong(@Field("sip_id") String sip_id);

    @POST(Const.REMOVE_PLAYLIST)
    @FormUrlEncoded
    Call<SimpleResponse> deleteplaylist(@Field("playlist_id") String playlist_id);

    @GET(Const.INSTAGRAM)
    Call<InstagramResponse> getinstagramdetails();

    @GET(Const.EVENTS)
    Call<EventsResponse> getevents();

    @POST(Const.ALL_SONGS)
    @FormUrlEncoded
    Call<AllSongsResponse> getallsongs(@Field("u_id") String user_id);

    @POST(Const.HIT_SINGLES)
    @FormUrlEncoded
    Call<SongsResponse> gethitsingles(@Field("u_id") String user_id);

    @POST(Const.FAVORITE_SONGS)
    @FormUrlEncoded
    Call<SongsResponse> getfavoritesongs(@Field("u_id") String user_id);

    @POST(Const.GET_PLAYLIST)
    @FormUrlEncoded
    Call<GetPlayListResponse> getplaylist(@Field("u_id") String user_id);

    @POST(Const.ADD_REMOVE_LIKE)
    @FormUrlEncoded
    Call<LikeResponse> addremovelike(@Field("u_id") String user_id, @Field("song_id") Integer song_id);

    @POST(Const.ADD_SONG_PLAYLIST)
    @FormUrlEncoded
    Call<AddSongPlaylist> addsongplaylist(@Field("song_id") Integer song_id, @Field("u_id") String user_id, @Field("playlist_id") Integer playlist_id);

    @POST(Const.CREATE_PLAYLIST)
    @FormUrlEncoded
    Call<CreatePlaylistResponse> createplaylist(@Field("u_id") String user_id, @Field("playlist_name") String playlist_name);

    @POST(Const.ADD_REMOVE_FAVORITE)
    @FormUrlEncoded
    Call<FavoriteResponse> addremovefavorite(@Field("u_id") String user_id, @Field("song_id") Integer song_id);

}