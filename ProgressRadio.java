package com.chocolateradio.Custom;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.WindowManager;

import com.chocolateradio.R;

import java.io.IOException;
import java.io.InputStream;

public class ProgressRadio {
//
    static ProgressDialog radiodialog;
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
        if(radiodialog!=null)
            radiodialog.dismiss();
            radiodialog=new ProgressDialog(context, ProgressDialog.THEME_HOLO_LIGHT);//ProgressDialog.THEME_HOLO_LIGHT //R.style.CustomDialogTheme
        try
        {
            radiodialog.show();
        }
        catch(WindowManager.BadTokenException e)
        {

        }

        radiodialog.setIndeterminate(true);
        radiodialog.setContentView(R.layout.custom_loading_dialog);
//        GifImageView iv_gif= (GifImageView) radiodialog.findViewById(R.id.loader);
//        iv_gif.setBytes(bytes);
//        iv_gif.startAnimation();
        radiodialog.setCancelable(cancelable);

    }

    public static void stop()
    {
        if(radiodialog!=null){
            radiodialog.dismiss();
        }
    }
}
