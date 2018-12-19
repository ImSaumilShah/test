package com.saumil.locationmapdemo.Activity;

import android.content.ComponentName;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.saumil.locationmapdemo.R;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;

public class Details extends AppCompatActivity {

    LatLng latLng;
    List<Address> addresslist;
    double currlat,currlong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        RelativeLayout toolbar = (RelativeLayout) findViewById(R.id.toolbar);
        TextView tv = (TextView) toolbar.findViewById(R.id.tlbrtxt);
        ImageView upbtn = (ImageView) toolbar.findViewById(R.id.upbtn);
        ImageView maptype = (ImageView) toolbar.findViewById(R.id.selectMapType);
        ImageView optionslist = (ImageView) toolbar.findViewById(R.id.optionslist);
        maptype.setVisibility(View.GONE);
        optionslist.setVisibility(View.GONE);
        upbtn.setVisibility(View.VISIBLE);
        upbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tv.setText("Details");
        Intent intent = getIntent();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            latLng = intent.getParcelableExtra("DetailLatLng");
            currlat = intent.getDoubleExtra("Currentlat",currlat);
            currlong = intent.getDoubleExtra("currentlong",currlong);
            Log.d("currentlat", String.valueOf(currlat));
            Log.d("currentlong", String.valueOf(currlong));
            Log.d("detail", String.valueOf(latLng));
        }

        try {
            addresslist = new Geocoder(this).getFromLocation(latLng.latitude,latLng.longitude,1);
            Log.d("addresslist", String.valueOf(addresslist));
            Log.d("addresslist size", String.valueOf(addresslist.size()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        final TextView name,address,city,state,lat,lng;
        Button getdirection;

        name = (TextView) findViewById(R.id.locationname);
        address = (TextView) findViewById(R.id.locationaddress);
        city = (TextView) findViewById(R.id.locationcity);
        state = (TextView) findViewById(R.id.locationstate);
        lat = (TextView) findViewById(R.id.locationlatitude);
        lng = (TextView) findViewById(R.id.locationlongitude);

        getdirection = (Button) findViewById(R.id.locationdirection);

//        name.setText(addresslist.get(0).getFeatureName());
        address.setText(addresslist.get(0).getAddressLine(0));
        city.setText(addresslist.get(0).getSubAdminArea());
        state.setText(addresslist.get(0).getAdminArea());
        final double latt = addresslist.get(0).getLatitude();
        final double lngg = addresslist.get(0).getLongitude();

        lat.setText(String.valueOf(latt));
        lng.setText(String.valueOf(lngg));

        getdirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(Intent.ACTION_VIEW,  Uri
                        .parse("http://maps.google.com/maps?saddr="
                                + currlat + ","
                                + currlong + "&daddr="
//                                + latLng.latitude + "," + latLng.longitude));
                                + latt + "," + lngg));
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                final ComponentName cn = new ComponentName("com.google.android.apps.maps",  "com.google.android.maps.MapsActivity");
                intent.setComponent(cn);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity( intent);
            }
        });

    }
}
