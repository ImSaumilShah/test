package com.chocolateradio.Custom;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.WindowManager;

import com.chocolateradio.R;
import com.felipecsl.gifimageview.library.GifImageView;

import java.io.IOException;
import java.io.InputStream;

public class Progress {
//
    static ProgressDialog dialog;
    @SuppressLint("ResourceAsColor")
    public static void start(Context context, boolean cancelable){

        InputStream is = null;
        byte[] bytes = new byte[0];
        try {
            is = context.getAssets().open("loader.gif");
            bytes = new byte[is.available()];
            is.read(bytes);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(dialog!=null)
            dialog.dismiss();
            dialog=new ProgressDialog(context, ProgressDialog.THEME_HOLO_LIGHT);//ProgressDialog.THEME_HOLO_LIGHT //R.style.CustomDialogTheme
        try
        {
            dialog.show();
        }
        catch(WindowManager.BadTokenException e)
        {

        }

        dialog.setIndeterminate(true);
        dialog.setContentView(R.layout.custom_loading_dialog);
//        GifImageView iv_gif= (GifImageView) dialog.findViewById(R.id.loader);
//        iv_gif.setBytes(bytes);
//        iv_gif.startAnimation();
        dialog.setCancelable(cancelable);

    }

    public static void stop()
    {
        if(dialog!=null){
            dialog.dismiss();
        }
    }
}
