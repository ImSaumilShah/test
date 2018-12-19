package com.saumil.locationmapdemo.Activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.saumil.locationmapdemo.Custom.Const;
import com.saumil.locationmapdemo.Custom.Prefs;
import com.saumil.locationmapdemo.Data.Model.PointResponse;
import com.saumil.locationmapdemo.Data.Model.PointData;
import com.saumil.locationmapdemo.Data.Remote.APIService;
import com.saumil.locationmapdemo.Data.Remote.APIUtils;
import com.saumil.locationmapdemo.R;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomeScreen extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationChangeListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    GoogleMap mGoogleMap;
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    Marker mymarker,searchmarker;
    int markercheck = 0;
    private int searchcheck = 0;
    Toolbar toolbar1;
    Button locationdetails;
    private int markerclickcheck = 0;
    LatLng detailLatLng;
    List<PointData> pointData;
    ImageView maptype,optionslist;
    double currlat,currlong;
    String filename;
    private int setvalue;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      
            setContentView(R.layout.activity_home_screen);

            initMap();
            
    }


    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }
    
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        googleApiClient.connect();
        Log.d("onmapready", "onmapready");
    }

 
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Log.d("onconnect", "onconnect");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("inpermission", "inpermission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        Log.d("onconnectafterls", "onconnectafterls");
    }


    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection Suspended!!!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Failed!!!", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            Toast.makeText(this, "Can't Get Current Location!!!", Toast.LENGTH_SHORT).show();
        } else {
            LatLng latLng1 = new LatLng(location.getLatitude(), location.getLongitude());
            currlat = location.getLatitude();
            currlong = location.getLongitude();
            CameraUpdate update1 = CameraUpdateFactory.newLatLngZoom(latLng1,15);
            mGoogleMap.animateCamera(update1);


            
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mGoogleMap.setMyLocationEnabled(true);
            //mGoogleMap.setOnMyLocationClickListener(this);
           // mGoogleMap.setOnMyLocationChangeListener(this);
            //mGoogleMap.setOnMapClickListener(this);
//            mGoogleMap.clear();
//            mGoogleMap.addMarker(new MarkerOptions().position(latLng1).title("You're Here"));
            Log.d("lat", String.valueOf(location.getLatitude()));

        }
    }

//     @Override
//     public void onMyLocationClick(@NonNull Location location) {
//         if(mymarker !=null){
//             mymarker.remove();
//         }

//         LatLng latLng1 = new LatLng(location.getLatitude(), location.getLongitude());
//         mymarker = mGoogleMap.addMarker(new MarkerOptions()
//                 .position(latLng1)
//                 .title("You're Here"));
//     }

//     @Override
//     public void onMyLocationChange(Location location) {
//         if(mymarker != null) {
//             mymarker.remove();}
//             LatLng latLng1 = new LatLng(location.getLatitude(), location.getLongitude());
//             mymarker =  mGoogleMap.addMarker(new MarkerOptions().position(latLng1).title("You're Here"));
//     }

  
}


