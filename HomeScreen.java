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
        if (googleServicesAvailable()) {
            setContentView(R.layout.activity_home_screen);

            initMap();
            filename = Prefs.getPrefInstance().getValue(HomeScreen.this, Const.PREFS_FILENAME, "");
            RelativeLayout toolbar = (RelativeLayout) findViewById(R.id.toolbar);
            TextView tv = (TextView) toolbar.findViewById(R.id.tlbrtxt);
            ImageView upbtn = (ImageView) toolbar.findViewById(R.id.upbtn);
            maptype = (ImageView) toolbar.findViewById(R.id.selectMapType);
            optionslist = (ImageView) toolbar.findViewById(R.id.optionslist);
            maptype.setVisibility(View.VISIBLE);
            optionslist.setVisibility(View.VISIBLE);
            upbtn.setVisibility(View.VISIBLE);
            upbtn.setImageResource(R.drawable.world);
            tv.setText("Maps & Location");
            maptype.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    registerForContextMenu(maptype);
                    openContextMenu(maptype);
                }
            });
            optionslist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    registerForContextMenu(optionslist);
                    openContextMenu(optionslist);
                }
            });


//            EditText wanttogo = (EditText) findViewById(R.id.wanttogo);
//            final String locationToGO = wanttogo.getText().toString();
//            Button searchbtn = (Button) findViewById(R.id.searchbtn);
//            searchbtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    try {
//                        Geocoder geocoder = new Geocoder(getApplicationContext());
//                        List<Address> searchList = geocoder.getFromLocationName(locationToGO, 1);
//                        Address address = searchList.get(0);
//                        Log.d("addressline", address.getAddressLine(0));
//                        Log.d("adminarea", address.getAdminArea());
//                        Log.d("subadminarea", address.getSubAdminArea());
//                        Log.d("locality", address.getLocality());
//                        Log.d("featurename", address.getFeatureName());
//                        double lat = address.getLatitude();
//                        double lng = address.getLongitude();
//                        String addressline = address.getAddressLine(0);
//                        gotoLocationZoom(lat, lng, 15,addressline);
//                        searchList.remove(0);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });

            locationdetails = (Button) findViewById(R.id.locationdetails);
            if(markerclickcheck == 0){
                locationdetails.setVisibility(View.GONE);
            }
            else{
                locationdetails.setVisibility(View.VISIBLE);
            }
            locationdetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HomeScreen.this,Details.class);
                    Bundle bundle = new Bundle();
                    bundle.putDouble("Currentlat",currlat);
                    Log.d("current lat", String.valueOf(currlat));
                    bundle.putDouble("currentlong",currlong);
                    Log.d("currentlong", String.valueOf(currlong));
                    bundle.putParcelable("DetailLatLng",detailLatLng);
                    Log.d("detailLatLong", String.valueOf(detailLatLng));
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        } else {
            //No Google Maps Layout
            finish();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu,menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    public boolean onOptionsItemSelected(MenuItem item){
//        switch (item.getItemId()){
//            case R.id.mapTypeNone:
//                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
//                break;
//            case R.id.mapTypeNormal:
//                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//                break;
//            case R.id.mapTypeTerrain:
//                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
//                break;
//            case R.id.mapTypeSatellite:
//                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//                break;
//            case R.id.mapTypeHybrid:
//                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//                break;
//            default:
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onCreateContextMenu(ContextMenu menu,View view,ContextMenu.ContextMenuInfo menuInfo){
        switch (view.getId()){
            case R.id.selectMapType:
                menu.setHeaderTitle("SELECT MAP TYPE");
                menu.setHeaderIcon(R.drawable.map);
                menu.add(Menu.NONE,1,Menu.NONE,"None");
                menu.add(Menu.NONE,2,Menu.NONE,"Normal");
                menu.add(Menu.NONE,3,Menu.NONE,"Terrain");
                menu.add(Menu.NONE,4,Menu.NONE,"Satellite");
                menu.add(Menu.NONE,5,Menu.NONE,"Hybrid");
                menu.add(Menu.NONE,6,Menu.NONE,"Styled Map");
                break;
            case R.id.optionslist:
                menu.setHeaderTitle("OPTIONS");
                menu.add(Menu.NONE,7,Menu.NONE,"Clear");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case 1:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
            case 2:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case 3:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case 4:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case 5:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case 6:
                mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.style_json));
                break;
            case 7:
                mGoogleMap.clear();
                break;
            default:
                break;

        }
        return  super.onContextItemSelected(item);
    }

    public void geoLocate(View view) throws IOException {
        if(locationdetails.getVisibility() == View.VISIBLE){
            locationdetails.setVisibility(View.GONE);
        }
        EditText wanttogo = (EditText) findViewById(R.id.wanttogo);
        String locationToGO = wanttogo.getText().toString();

        Geocoder geocoder = new Geocoder(this);
        List<Address> searchList = geocoder.getFromLocationName(locationToGO, 5);

        Address address = searchList.get(0);
        double lat = address.getLatitude();
        double lng = address.getLongitude();
        String addressline = address.getAddressLine(0);
        gotoLocationZoom(lat, lng, 15,addressline);
        locationToGO="";
        searchList.remove(0);
    }



    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    public boolean checkLocationPermission() {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Can't Connect To Play Services", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        //gotoLocationZoom(39.008224, -76.8984527, 15);

//
//        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
//            buildAlertMessageNoGps();
//        }
        googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        googleApiClient.connect();
        Log.d("onmapready", "onmapready");
    }
//
//    private void buildAlertMessageNoGps() {
//        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
//                .setCancelable(false)
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
//                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                    }
//                })
//                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
//                        dialog.cancel();
//                    }
//                });
//        final AlertDialog alert = builder.create();
//        alert.show();
//    }

    private void gotoLocationZoom(double lat, double lng, float zoom,String title) {
        if(searchmarker != null){
            searchmarker.remove();
        }
        LatLng latLng = new LatLng(lat, lng);
        searchmarker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).draggable(true).title(title));
        mGoogleMap.setOnMarkerDragListener(this);
        mGoogleMap.setOnMarkerClickListener(this);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mGoogleMap.moveCamera(update);
    }

    private void gotoLocation(double lat, double lng) {
        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLng(latLng);
        mGoogleMap.moveCamera(update);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initMap();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
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


            boolean connected = false;
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                //we are connected to a network
                connected = true;
            }
            else
                connected = false;

            if(connected) {
                    APIService apiService = APIUtils.getAPIService();
                    Call<PointResponse> call = apiService.postData(location.getLatitude(), location.getLongitude());
//                    Call<PointResponse> call = apiService.postData();
                    call.enqueue(new Callback<PointResponse>() {
                        @Override
                        public void onResponse(Call<PointResponse> call, Response<PointResponse> response) {
                            String status = response.body().getStatus();
                            if (Objects.equals(status, "1")) {
                                pointData = response.body().getData();

                                int totalentries = pointData.size();
                                Prefs.getPrefInstance().setValue(getApplicationContext(), Const.TOTALENTRIES, String.valueOf(totalentries));
                                Log.d("totalentries", String.valueOf(totalentries));
                                for (int i = 0; i < totalentries; i++) {
                                    Prefs.getPrefInstance().setValue(getApplicationContext(), Const.PREFS_FILENAME, "locationmapdone");
                                    Prefs.getPrefInstance().setValue(getApplicationContext(),"Name" + i, pointData.get(i).getHospitalName());
                                    Log.d("name api",pointData.get(i).getHospitalName());
                                    Prefs.getPrefInstance().setValue(getApplicationContext(), "Address" + i, pointData.get(i).getHospitalLocation());
//                                    Log.d("address api",pointData.get(i).getHospitalLocation());
                                    Prefs.getPrefInstance().setValue(getApplicationContext(), "Latitude" + i, pointData.get(i).getHospitalLatitude().toString());
                                    Log.d("lat api",pointData.get(i).getHospitalLatitude().toString());
                                    Prefs.getPrefInstance().setValue(getApplicationContext(), "Longitude" + i, pointData.get(i).getHospitalLongitude().toString());
                                    Log.d("long api",pointData.get(i).getHospitalLongitude().toString());
                                    Prefs.getPrefInstance().setValue(getApplicationContext(), "Country" + i, pointData.get(i).getHospitalDescription());
//                                    Log.d("country api",pointData.get(i).getHospitalDescription());
//                                    Prefs.getPrefInstance().setValue(getApplicationContext(), "State" + i, pointData.get(i).getDistance().toString());
//                                    Log.d("state api",pointData.get(i).getDistance().toString());
                                    mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(pointData.get(i).getHospitalLatitude(), pointData.get(i).getHospitalLongitude())).title(pointData.get(i).getHospitalName()));
                                    setvalue = 1;
                                }
                                mGoogleMap.setOnMarkerClickListener(HomeScreen.this);
                            }
                        }

                        @Override
                        public void onFailure(Call<PointResponse> call, Throwable t) {

                        }
                    });
                }
            else {
                if (Objects.equals(filename, "locationmapdone")) {
                    String totalentries = Prefs.getPrefInstance().getValue(getApplicationContext(), Const.TOTALENTRIES, "");
                    Log.d("totalentries", String.valueOf(totalentries));
                    for (int i = 0; i < Integer.parseInt(totalentries); i++) {
                        Log.d("name show",Prefs.getPrefInstance().getValue(getApplicationContext(),"Name" + i,""));
                        double latt = Double.parseDouble(Prefs.getPrefInstance().getValue(getApplicationContext(), "Latitude" + i, ""));
                        Log.d("lat offline", String.valueOf(latt));
                        double longg = Double.parseDouble(Prefs.getPrefInstance().getValue(getApplicationContext(), "Longitude" + i, ""));
                        Log.d("long offline", String.valueOf(longg));
                        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(latt, longg)).title(Prefs.getPrefInstance().getValue(getApplicationContext(), "Address" + i, "")));
                        Log.d("address offline",Prefs.getPrefInstance().getValue(getApplicationContext(), "Address" + i, ""));
                    }
                    mGoogleMap.setOnMarkerClickListener(HomeScreen.this);
                }
                else{
                    Toast.makeText(this, "No Internet", Toast.LENGTH_LONG).show();
                    finish();
                    setvalue = 0;
                }
            }
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
            mGoogleMap.setOnMyLocationClickListener(this);
            mGoogleMap.setOnMyLocationChangeListener(this);
            mGoogleMap.setOnMapClickListener(this);
//            mGoogleMap.clear();
//            mGoogleMap.addMarker(new MarkerOptions().position(latLng1).title("You're Here"));
            Log.d("lat", String.valueOf(location.getLatitude()));

        }
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        if(mymarker !=null){
            mymarker.remove();
        }

        LatLng latLng1 = new LatLng(location.getLatitude(), location.getLongitude());
        mymarker = mGoogleMap.addMarker(new MarkerOptions()
                .position(latLng1)
                .title("You're Here"));
    }

    @Override
    public void onMyLocationChange(Location location) {
        if(mymarker != null) {
            mymarker.remove();}
            LatLng latLng1 = new LatLng(location.getLatitude(), location.getLongitude());
            mymarker =  mGoogleMap.addMarker(new MarkerOptions().position(latLng1).title("You're Here"));
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        LatLng latLng =  marker.getPosition();
        try {
            List<Address> newaddress = new Geocoder(this).getFromLocation(latLng.latitude,latLng.longitude,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(!Objects.equals(marker.getTitle(), "You're Here")){
    markerclickcheck = 1;
    locationdetails.setVisibility(View.VISIBLE);
    detailLatLng =  marker.getPosition();
    Log.d("detaillatlong","Details are : "  + detailLatLng);
        }
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if(locationdetails.getVisibility()==View.VISIBLE){
            locationdetails.setVisibility(View.GONE);
        }
    }
}


//
//    public void geoLocate(View view) throws IOException {
//        EditText wanttogo = (EditText) findViewById(R.id.wanttogo);
//        String locationToGO = wanttogo.getText().toString();
//
//        Geocoder geocoder = new Geocoder(this);
//        List<Address> searchList = geocoder.getFromLocationName(locationToGO, 1);
//
//        Address address = searchList.get(0);
//        Log.d("addressline", address.getAddressLine(0));
//        Log.d("adminarea", address.getAdminArea());
//        Log.d("subadminarea", address.getSubAdminArea());
//        Log.d("locality", address.getLocality());
//        Log.d("featurename", address.getFeatureName());
//        double lat = address.getLatitude();
//        double lng = address.getLongitude();
//        String addressline = address.getAddressLine(0);
//        gotoLocationZoom(lat, lng, 15,addressline);
//        locationToGO="";
//        searchList.remove(0);
//    }

