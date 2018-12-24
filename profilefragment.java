package com.socialout.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.socialout.API.APIClient;
import com.socialout.API.APIInterface;
import com.socialout.Activity.HomeActivity;
import com.socialout.Adapter.GallaryAdapter;
import com.socialout.Adapter.ProfileGallaryAdapter;
import com.socialout.Custom.Const;
import com.socialout.Custom.Prefs;
import com.socialout.Custom.Progress;
import com.socialout.Custom.Toolbar;
import com.socialout.Model.ProfileDetailsData;
import com.socialout.Model.ProfileDetailsResponse;
import com.socialout.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Naisargi on 09-May-18.
 */


public class ProfileFragment extends Fragment {

    static FragmentManager fragmentManager;
    static Activity activity;
    static DrawerLayout drawerLayout;
    static LinearLayout toolbar;
    static String profile;
    TextView username;
    TextView userbio,ccs;
    CircleImageView profilepic;
    ProfileDetailsData data;
    LinearLayout ll_profile,mypost,mylike;
    TextView group_count,event_count,mypost_number,mylike_number;
    LinearLayout groups,events;
    String title;

    public ProfileFragment() {

        Log.d("mytag","in profile fragment:");
    }

    public ProfileFragment(Activity activity, DrawerLayout mDrawerLayout, LinearLayout toolbar, FragmentManager fragmentManager, String profile) {
        this.fragmentManager = fragmentManager;
        this.activity = activity;
        this.drawerLayout = mDrawerLayout;
        this.toolbar = toolbar;
        this.profile=profile;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_fragment,container,false);
        Toolbar.setToolbar(activity,drawerLayout,toolbar,"Profile");
        username=v.findViewById(R.id.usernameprofile);
        username.setText(Prefs.getPrefInstance().getValue(activity,Const.NAME,""));
        profilepic = v.findViewById(R.id.profilepic);
        userbio = v.findViewById(R.id.userbio);
        ccs=v.findViewById(R.id.ccs);
        ll_profile=v.findViewById(R.id.ll_profile);
        group_count=v.findViewById(R.id.group_number);
        event_count=v.findViewById(R.id.events_number);
        mypost_number=v.findViewById(R.id.mypost_number);
        mylike_number=v.findViewById(R.id.mylike_number);
        groups=v.findViewById(R.id.groups);
        events=v.findViewById(R.id.events);
        mypost=v.findViewById(R.id.mypost);
        mylike=v.findViewById(R.id.mylike);

        String url=Prefs.getPrefInstance().getValue(activity,Const.PROFILE_IMAGE,"");

        HomeActivity.home.setSelected(false);
        HomeActivity.user.setSelected(true);
        HomeActivity.notification.setSelected(false);
        HomeActivity.chat.setSelected(false);

        mypost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment frag = new MyPostFragment(activity, drawerLayout, toolbar, fragmentManager);
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.content_frame, frag).addToBackStack("Inner").commit();
            }
        });

        groups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("mytag","in  grops:::");
//                TabFragment.setitem(1);
                Prefs.getPrefInstance().setValue(activity,Const.MYGROUP,"true");
                Fragment frag = new TabFragment(activity, drawerLayout, toolbar, fragmentManager);
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.content_frame, frag).addToBackStack("Inner").commit();
            }
        });

        mylike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment frag = new MylikePostFragment(activity,drawerLayout,toolbar,fragmentManager);
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.content_frame, frag).addToBackStack("Inner").commit();
            }
        });

        events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment frag = new MyUpcomingEventsFragment(activity, drawerLayout, toolbar, fragmentManager,title);
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.content_frame, frag).addToBackStack("Inner").commit();
            }
        });

        ll_profile.setVisibility(View.GONE);
        int img=R.drawable.user2;

        if(url.equals("")){
            profilepic.setImageResource(R.drawable.user2);
        } else {
            Picasso.with(getContext()).load(url).placeholder(img).error(img).into(profilepic);
        }

        Progress.start(activity,false);

        APIInterface apiInterface2 = APIClient.getClient().create(APIInterface.class);
        Call<ProfileDetailsResponse> profileDetailsResponseCall = apiInterface2.getProfileDetails(Prefs.getPrefInstance().getValue(activity, Const.ACCESS_TOKEN,""),Prefs.getPrefInstance().getValue(activity,Const.USER_ID,""));

        profileDetailsResponseCall.enqueue(new Callback<ProfileDetailsResponse>() {
            @Override
            public void onResponse(Call<ProfileDetailsResponse> call, Response<ProfileDetailsResponse> response) {
                if(response.isSuccessful()){
                    int status=response.body().getStatus();
                    if(status==1){
                        Progress.stop();
                        data = response.body().getData();
                        event_count.setText(data.getEvent_count());
                        group_count.setText(data.getGroup_count());
                        mypost_number.setText(data.getPost_count());
                        mylike_number.setText(data.getLike_count());

                        if(data.getBio().equals("N/A")){
                            userbio.setText("Bio");
                        }
                        else {
                        userbio.setText(data.getBio());}

                        if(data.getCountry_name().equals("")) {
                            if (data.getState_name().equals("")) {
                                if (data.getCity_name().equals("")) {
                                    ccs.setText("Country");
                                } else {
                                    ccs.setText(data.getCity_name());
                                }
                            } else {
                                if (data.getCity_name().equals("")) {
                                    ccs.setText(data.getState_name());
                                } else {
                                ccs.setText(data.getState_name()+", "+data.getCity_name());
                                }
                            }
                        }
                        else{
                            if (data.getState_name().equals("")) {
                                if (data.getCity_name().equals("")) {
                                    ccs.setText(data.getCountry_name());
                                } else {
                                    ccs.setText(data.getCountry_name()+", "+data.getCity_name());
                                }
                            } else {
                                if (data.getCity_name().equals("")) {
                                    ccs.setText(data.getCountry_name()+", "+data.getState_name());
                                } else {
                                    ccs.setText(data.getCountry_name()+", "+data.getState_name()+", " + data.getCity_name());
                                }
                            }
                        }
                        Log.d("mytag","city is :::  " + data.getCity() + data.getState());
                        Picasso.with(activity).load(data.getImage()).placeholder(R.drawable.user2).into(profilepic);
                        ll_profile.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ProfileDetailsResponse> call, Throwable t) {
                Progress.stop();
            }
        });
        return v;
    }

    public static void PushFragment(){
        Fragment fragment = new EditProfileFragment(activity,drawerLayout, toolbar , fragmentManager, profile);
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, fragment).addToBackStack("Inner").commit();
    }
}
