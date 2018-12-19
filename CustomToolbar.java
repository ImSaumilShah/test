package com.chocolateradio.Custom;

import android.app.Activity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chocolateradio.Activities.HomeScreen;
import com.chocolateradio.R;

/**
 * Created by Saumil on 1/2/2018.
 */

public class CustomToolbar {

    public static void setToolbar(final Activity activity, LinearLayout toolbarLayout, final String Title) {

        toolbarLayout = (LinearLayout) activity.findViewById(R.id.toolbar);
        TextView title = (TextView) toolbarLayout.findViewById(R.id.tlbrtxt);
        LinearLayout upbtn = (LinearLayout) toolbarLayout.findViewById(R.id.upbtn);
        ImageView option = (ImageView) toolbarLayout.findViewById(R.id.option);

        toolbarLayout.setTag("Outer");
        title.setText(Title);
        title.setSelected(true);
        HomeScreen.SetScreen("HomeFragment");
//        upbtnicon.getLayoutParams().width = activity.getResources().getDimensionPixelSize(R.dimen._25sdp);
//        upbtnicon.getLayoutParams().height = activity.getResources().getDimensionPixelSize(R.dimen._20sdp);

        upbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onBackPressed();
            }
        });

        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeScreen.SetScreen("Home");
            }
        });
    }

    public static void setInnerToolbar(final Activity activity, LinearLayout toolbarLayout, final String Title, String value) {

        toolbarLayout = (LinearLayout) activity.findViewById(R.id.toolbar);
        TextView title = (TextView) toolbarLayout.findViewById(R.id.tlbrtxt);
        LinearLayout upbtn = (LinearLayout) toolbarLayout.findViewById(R.id.upbtn);
        ImageView option = (ImageView) toolbarLayout.findViewById(R.id.option);

        if(Title.equals("More")){
            toolbarLayout.setTag("Outer");
        }else{
            toolbarLayout.setTag("Inner");
        }

        if(value.equals("SongFragment")){
            HomeScreen.SetScreen("SongFragment");
        }else{
            HomeScreen.SetScreen("InnerFragment");
        }
        title.setText(Title);
        title.setSelected(true);

        upbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onBackPressed();
            }
        });

        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeScreen.SetScreen("Home");
            }
        });
    }
}
