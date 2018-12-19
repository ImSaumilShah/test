package com.chocolateradio.PhoneMedia;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.chocolateradio.ChocolateRadio;
import com.chocolateradio.Custom.BufferData;
import com.chocolateradio.Custom.Const;
import com.chocolateradio.Custom.Prefs;
import com.chocolateradio.Custom.Utils;
import com.chocolateradio.Manager.MediaController;


public class IncomingCallReceiver extends BroadcastReceiver {

//    public String check = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (TelephonyManager.EXTRA_STATE_RINGING.equals(intent.getStringExtra(TelephonyManager.EXTRA_STATE))) {
            Utils.getInstance().d("call ringing");
//            if (MediaController.getInstance().getPlayingSongDetail() != null) {
//                if (MediaController.getInstance().isAudioPaused()) {
//                    Log.d("mytag","already paused");
////                    check = "paused";
//                    Prefs.getPrefInstance().setValue(ChocolateRadio.applicationContext, Const.CHECK,"paused");
//                    Log.d("mytag","check is :: "+Prefs.getPrefInstance().getValue(ChocolateRadio.applicationContext, Const.CHECK,""));
//
//                }
//                else{
//                    Log.d("mytag","already playing");
//                    MediaController.getInstance().pauseAudio(MediaController.getInstance().getPlayingSongDetail());
//                }
//            }

            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
//            Toast.makeText(context, incomingNumber, Toast.LENGTH_LONG).show();
        }
        if (TelephonyManager.EXTRA_STATE_IDLE.equals(intent.getStringExtra(TelephonyManager.EXTRA_STATE))) {
            Utils.getInstance().d("call idle");
//            if (MediaController.getInstance().getPlayingSongDetail() != null) {
//                if (MediaController.getInstance().isAudioPaused()) {
//                    String checkplay = Prefs.getPrefInstance().getValue(ChocolateRadio.applicationContext, Const.CHECK,"");
//                    Log.d("mytag","check is :: "+Prefs.getPrefInstance().getValue(ChocolateRadio.applicationContext, Const.CHECK,""));
//                    if(checkplay == "paused"){
//                        Prefs.getPrefInstance().setValue(ChocolateRadio.applicationContext,Const.CHECK,"");
//                        Log.d("mytag","check is :: "+ Prefs.getPrefInstance().getValue(ChocolateRadio.applicationContext, Const.CHECK,""));
//                    }else{
////                    MsgData.getInstance().setIsPlaying("1");
//                    MediaController.getInstance().playAudio(MediaController.getInstance().getPlayingSongDetail());}
//                }
//            }
//
//            MediaPlayer audioplayer = BufferData.getInstance().getAudioplayer();
//            if (audioplayer != null)
//            {
//                if (audioplayer.isPlaying())
//                {
//                    audioplayer.start();
////                    audioplayer.stop();
////                    audioplayer.release();
////                    audioplayer = null;
//                    BufferData.getInstance().setAudioplayer(audioplayer);
//                }
//            }

        }


    }
}
