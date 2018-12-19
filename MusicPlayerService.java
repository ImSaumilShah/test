/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.chocolateradio.Manager;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.RemoteControlClient;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.chocolateradio.Activities.HomeScreen;
import com.chocolateradio.ChocolateRadio;
import com.chocolateradio.Custom.BufferData;
import com.chocolateradio.Custom.Const;
import com.chocolateradio.Custom.Prefs;
import com.chocolateradio.Custom.Utils;
import com.chocolateradio.Data.Model.SongDetails;
import com.chocolateradio.PhoneMedia.DMPlayerUtility;
import com.chocolateradio.R;

import static com.chocolateradio.FCM.MyFirebaseMessagingService.NOTIFICATION_CHANNEL_ID;


public class MusicPlayerService extends Service implements AudioManager.OnAudioFocusChangeListener, NotificationManager.NotificationCenterDelegate {

    public static final String NOTIFY_PREVIOUS = "com.chocolateradio.previous";
    public static final String NOTIFY_CLOSE = "com.chocolateradio.close";
    public static final String NOTIFY_PAUSE = "com.chocolateradio.pause";
    public static final String NOTIFY_PLAY = "com.chocolateradio.play";
    public static final String NOTIFY_NEXT = "com.chocolateradio.next";
    Notification notification;
    android.app.NotificationManager mNotificationManager;

    private RemoteControlClient remoteControlClient;
    private AudioManager audioManager;
    private static boolean ignoreAudioFocus = false;
    private PhoneStateListener phoneStateListener;

    private static boolean supportBigNotifications = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    private static boolean supportLockScreenControls = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        NotificationManager.getInstance().addObserver(this, NotificationManager.audioProgressDidChanged);
        NotificationManager.getInstance().addObserver(this, NotificationManager.audioPlayStateChanged);
        try {
            phoneStateListener = new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    if (state == TelephonyManager.CALL_STATE_RINGING) {
                        Log.d("mytag","call state ringing");

                        if (MediaController.getInstance().getPlayingSongDetail() != null) {
                            if (MediaController.getInstance().isAudioPaused()) {
                                Log.d("mytag","already paused");
//                    check = "paused";
                                Prefs.getPrefInstance().setValue(ChocolateRadio.applicationContext, Const.CHECK,"paused");
                                Log.d("mytag","check is :: "+Prefs.getPrefInstance().getValue(ChocolateRadio.applicationContext, Const.CHECK,""));

                            }
                            else{
                                Log.d("mytag","already playing");
                                MediaController.getInstance().pauseAudio(MediaController.getInstance().getPlayingSongDetail());
                            }
                        }

//                        if (MediaController.getInstance().isPlayingAudio(MediaController.getInstance().getPlayingSongDetail())
//                                && !MediaController.getInstance().isAudioPaused()) {
//                            MediaController.getInstance().pauseAudio(MediaController.getInstance().getPlayingSongDetail());
//                        }
                    } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                        Log.d("mytag","Call state idle");
                        if (MediaController.getInstance().getPlayingSongDetail() != null) {
                            if (MediaController.getInstance().isAudioPaused()) {
                                String checkplay = Prefs.getPrefInstance().getValue(ChocolateRadio.applicationContext, Const.CHECK,"");
                                Log.d("mytag","check is :: "+Prefs.getPrefInstance().getValue(ChocolateRadio.applicationContext, Const.CHECK,""));
                                if(checkplay == "paused"){
                                    Prefs.getPrefInstance().setValue(ChocolateRadio.applicationContext,Const.CHECK,"");
                                    Log.d("mytag","check is :: "+ Prefs.getPrefInstance().getValue(ChocolateRadio.applicationContext, Const.CHECK,""));
                                }else{
//                    MsgData.getInstance().setIsPlaying("1");
                                    MediaController.getInstance().playAudio(MediaController.getInstance().getPlayingSongDetail());}
                            }
                        }

                        MediaPlayer audioplayer = BufferData.getInstance().getAudioplayer();
                        if (audioplayer != null)
                        {
                            if (audioplayer.isPlaying())
                            {
                                audioplayer.start();
//                    audioplayer.stop();
//                    audioplayer.release();
//                    audioplayer = null;
                                BufferData.getInstance().setAudioplayer(audioplayer);
                            }
                        }
                    } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                        Log.d("mytag","Call state off hook");
                    }
                    super.onCallStateChanged(state, incomingNumber);
                }
            };
            TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            if (mgr != null) {
                mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
            }
        } catch (Exception e) {
            Log.e("tmessages", e.toString());
        }
        super.onCreate();
    }

    @SuppressLint("NewApi")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            SongDetails messageObject = MediaController.getInstance().getPlayingSongDetail();
            if (messageObject == null) {
                DMPlayerUtility.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        stopSelf();
                    }
                });
                return START_STICKY;
            }
            if (supportLockScreenControls) {
                ComponentName remoteComponentName = new ComponentName(getApplicationContext(), MusicPlayerReceiver.class.getName());
                try {
                    if (remoteControlClient == null) {
                        audioManager.registerMediaButtonEventReceiver(remoteComponentName);
                        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                        mediaButtonIntent.setComponent(remoteComponentName);
                        String pkg = getPackageName();
                        PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(this, 100, mediaButtonIntent.setPackage(pkg), 0);
                        remoteControlClient = new RemoteControlClient(mediaPendingIntent);
                        audioManager.registerRemoteControlClient(remoteControlClient);
                    }
                    remoteControlClient.setTransportControlFlags(RemoteControlClient.FLAG_KEY_MEDIA_PLAY | RemoteControlClient.FLAG_KEY_MEDIA_PAUSE
                            | RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE | RemoteControlClient.FLAG_KEY_MEDIA_STOP
                            | RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS | RemoteControlClient.FLAG_KEY_MEDIA_NEXT);
                } catch (Exception e) {
                    Log.e("tmessages", e.toString());
                }
            }
            createNotification(messageObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    @SuppressLint("NewApi")
    private void createNotification(SongDetails mSongDetail) {
        try {
            String songName = mSongDetail.getSongName();
            String authorName = mSongDetail.getArtistName();
            SongDetails audioInfo = MediaController.getInstance().getPlayingSongDetail();
            RemoteViews simpleContentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.player_small_notification);
            RemoteViews expandedView = null;
            if (supportBigNotifications) {
                expandedView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.player_big_notification);
            }
            Intent intent = new Intent(ChocolateRadio.applicationContext, HomeScreen.class);
            intent.setAction("openplayer");
            intent.setFlags(32768);
            PendingIntent contentIntent = PendingIntent.getActivity(ChocolateRadio.applicationContext, 0, intent, 0);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int notifyID = 1;
//                String CHANNEL_ID = "my_channel_01";// The id of the channel.
                CharSequence name = getString(R.string.channel_name);// The user-visible name of the channel.
                int importance = android.app.NotificationManager.IMPORTANCE_LOW;
                NotificationChannel mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
                mChannel.enableVibration(false);
                mChannel.setSound(null, null);
                mChannel.setShowBadge(false);

                notification = new NotificationCompat.Builder(getApplicationContext()).setSmallIcon(R.drawable.appicon)
                        .setContentIntent(contentIntent).setContentTitle(songName).setChannelId(NOTIFICATION_CHANNEL_ID).setDefaults(Notification.DEFAULT_ALL).setSound(null).build();
                notification.defaults = 0;


                mNotificationManager =
                        (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                if (mNotificationManager != null && Prefs.getPrefInstance().getValue(ChocolateRadio.applicationContext, Const.NOTIFICATION_STATUS, "").equals("false")) {
                    mNotificationManager.createNotificationChannel(mChannel);
                    Prefs.getPrefInstance().setValue(ChocolateRadio.applicationContext, Const.NOTIFICATION_STATUS, "true");
                }
// Issue the notification.
//            mNotificationManager.notify(notifyID , notification);
            } else {
                notification = new NotificationCompat.Builder(getApplicationContext()).setSmallIcon(R.drawable.appicon)
                        .setContentIntent(contentIntent).setContentTitle(songName).build();
            }
            notification.contentView = simpleContentView;
            if (supportBigNotifications) {
                notification.bigContentView = expandedView;
            }
            setListeners(simpleContentView);
            if (supportBigNotifications) {
                setListeners(expandedView);
            }
//            Bitmap albumArt = audioInfo != null ? audioInfo.getSmallCover(ChocolateRadio.applicationContext) : null;
//            if (albumArt != null) {
//                notification.contentView.setImageViewBitmap(R.id.player_album_art, albumArt);
//                if (supportBigNotifications) {
//                    notification.bigContentView.setImageViewBitmap(R.id.player_album_art, albumArt);
//                }
//            } else {
                notification.contentView.setImageViewResource(R.id.player_album_art, R.drawable.appicon);
                if (supportBigNotifications) {
                    notification.bigContentView.setImageViewResource(R.id.player_album_art, R.drawable.appicon);
                }
//            }
            notification.contentView.setViewVisibility(R.id.player_progress_bar, View.GONE);
            notification.contentView.setViewVisibility(R.id.player_next, View.VISIBLE);
            notification.contentView.setViewVisibility(R.id.player_previous, View.VISIBLE);
            if (supportBigNotifications) {
                notification.bigContentView.setViewVisibility(R.id.player_next, View.VISIBLE);
                notification.bigContentView.setViewVisibility(R.id.player_previous, View.VISIBLE);
                notification.bigContentView.setViewVisibility(R.id.player_progress_bar, View.GONE);
            }
            if (MediaController.getInstance().isAudioPaused()) {
                notification.contentView.setViewVisibility(R.id.player_pause, View.GONE);
                notification.contentView.setViewVisibility(R.id.player_play, View.VISIBLE);
                if (supportBigNotifications) {
                    notification.bigContentView.setViewVisibility(R.id.player_pause, View.GONE);
                    notification.bigContentView.setViewVisibility(R.id.player_play, View.VISIBLE);
                }
            } else {
                notification.contentView.setViewVisibility(R.id.player_pause, View.VISIBLE);
                notification.contentView.setViewVisibility(R.id.player_play, View.GONE);
                if (supportBigNotifications) {
                    notification.bigContentView.setViewVisibility(R.id.player_pause, View.VISIBLE);
                    notification.bigContentView.setViewVisibility(R.id.player_play, View.GONE);
                }
            }
            notification.contentView.setTextViewText(R.id.player_song_name, songName);
            notification.contentView.setTextViewText(R.id.player_author_name, authorName);
            if (supportBigNotifications) {
                notification.bigContentView.setTextViewText(R.id.player_song_name, songName);
                notification.bigContentView.setTextViewText(R.id.player_author_name, authorName);
            }
            notification.flags |= Notification.FLAG_ONGOING_EVENT;
            startForeground(5, notification);
            if (remoteControlClient != null) {
                RemoteControlClient.MetadataEditor metadataEditor = remoteControlClient.editMetadata(true);
                metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, authorName);
                metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_TITLE, songName);
//                if (audioInfo != null && audioInfo.getCover(ChocolateRadio.applicationContext) != null) {
//                    metadataEditor.putBitmap(RemoteControlClient.MetadataEditor.BITMAP_KEY_ARTWORK,
//                            audioInfo.getCover(ChocolateRadio.applicationContext));
//                }
                metadataEditor.apply();
                audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setListeners(RemoteViews view) {
        try {
            String pkg = getPackageName();
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, new Intent(NOTIFY_PREVIOUS).setPackage(pkg),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.player_previous, pendingIntent);
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, new Intent(NOTIFY_CLOSE).setPackage(pkg),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.player_close, pendingIntent);
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, new Intent(NOTIFY_PAUSE).setPackage(pkg),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.player_pause, pendingIntent);
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, new Intent(NOTIFY_NEXT).setPackage(pkg),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.player_next, pendingIntent);
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, new Intent(NOTIFY_PLAY).setPackage(pkg),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.player_play, pendingIntent);

//            if(mNotificationManager != null)
//            mNotificationManager.cancelAll();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("mytag", "service destroy");
        if (remoteControlClient != null) {
            RemoteControlClient.MetadataEditor metadataEditor = remoteControlClient.editMetadata(true);
            metadataEditor.clear();
            metadataEditor.apply();
            audioManager.unregisterRemoteControlClient(remoteControlClient);
            audioManager.abandonAudioFocus(this);
        }
        try {
            TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            if (mgr != null) {
                mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
            }
        } catch (Exception e) {
            Log.e("tmessages", e.toString());
        }
        NotificationManager.getInstance().removeObserver(this, NotificationManager.audioProgressDidChanged);
        NotificationManager.getInstance().removeObserver(this, NotificationManager.audioPlayStateChanged);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if (ignoreAudioFocus) {
            ignoreAudioFocus = false;
            return;
        }

//        final int mode = audioManager.getMode();
//        if(AudioManager.MODE_IN_CALL == mode){
//            Log.d("mytag","Mode in Call");
//            // device is in a telephony call
//        } else if(AudioManager.MODE_IN_COMMUNICATION == mode){
//            Log.d("mytag","Mode in Communication");
//            // device is in communiation mode, i.e. in a VoIP or video call
//        } else if(AudioManager.MODE_RINGTONE == mode){
//            Log.d("mytag","Mode Ringtone");
//            // device is in ringing mode, some incoming is being signalled
//        } else {
//            Log.d("mytag","Mode Normal");
//            // device is in normal mode, no incoming and no audio being played
//        }


        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
//             Pause
            Utils.getInstance().d("AUDIOFOCUS_LOSS_TRANSIENT");

//            if(MediaController.getInstance().getPlayingSongDetail() != null){
//                if(MediaController.getInstance().isAudioPaused()){
//                    Log.d("mytag","already paused");
//                    Prefs.getPrefInstance().setValue(ChocolateRadio.applicationContext,Const.CHECK,"paused");
//                }else{
//                    Log.d("mytag","playing then paused");
//                    MediaController.getInstance().pauseAudio(MediaController.getInstance().getPlayingSongDetail());
//                }
//            }
//            Utils.getInstance().d("AUDIOFOCUS_LOSS_TRANSIENT");
            MediaController.getInstance().pauseAudio(MediaController.getInstance().getPlayingSongDetail());
//            ignoreAudioFocus = true;
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
            Utils.getInstance().d("AUDIOFOCUS_LOSS_TRANSIENT CAN DUCK");
            MediaController.setVolume("DUCK");
        }  else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            Utils.getInstance().d("AUDIOFOCUS_LOSS");
            if (MediaController.getInstance().getPlayingSongDetail() != null) {
                if (!MediaController.getInstance().isAudioPaused()) {
                    MediaController.getInstance().pauseAudio(MediaController.getInstance().getPlayingSongDetail());
                    ignoreAudioFocus = true;
                }
            }
        }else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            ignoreAudioFocus = false;
            Utils.getInstance().d("AUDIOFOCUS_GAIN");
            MediaController.setVolume("NORMAL");
//              MediaController.getInstance().playAudio(MediaController.getInstance().getPlayingSongDetail());
        }
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationManager.audioPlayStateChanged) {
            SongDetails mSongDetail = MediaController.getInstance().getPlayingSongDetail();
            if (mSongDetail != null) {
                createNotification(mSongDetail);
            } else {
                stopSelf();
            }
        }
    }

    @Override
    public void newSongLoaded(Object... args) {
    }

    public static void setIgnoreAudioFocus() {
        ignoreAudioFocus = true;
    }
}
