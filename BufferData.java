package com.chocolateradio.Custom;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.widget.SeekBar;

public class BufferData {

    private static BufferData ourInstance = null;
    private SeekBar sb_progress;
    private SeekBar sb_progress_single;
    private ProgressDialog universalProgressLoader;
    private MediaPlayer audioplayer;

    public static BufferData getInstance() {
        if (ourInstance == null)
            ourInstance = new BufferData();
        return ourInstance;
    }

    public ProgressDialog getUniversalProgressLoader() {
        return universalProgressLoader;
    }

    public void setUniversalProgressLoader(ProgressDialog universalProgressLoader) {
        this.universalProgressLoader = universalProgressLoader;
    }


    public SeekBar getSb_progress_single() {
        return sb_progress_single;
    }

    public void setSb_progress_single(SeekBar sb_progress_single) {
        this.sb_progress_single = sb_progress_single;
    }

    public SeekBar getSb_progress() {
        return sb_progress;
    }

    public void setSb_progress(SeekBar sb_progress) {
        this.sb_progress = sb_progress;
    }

    public MediaPlayer getAudioplayer() {
        return audioplayer;
    }

    public void setAudioplayer(MediaPlayer audioplayer) {
        this.audioplayer = audioplayer;
    }
}
