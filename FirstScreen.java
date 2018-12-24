package com.sprinkler.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sprinkler.Custom.CheckPermission;
import com.sprinkler.Custom.Const;
import com.sprinkler.Custom.Prefs;
import com.sprinkler.Custom.Progress;
import com.sprinkler.Custom.Utils;
import com.sprinkler.Data.Model.AppStatusResponse;
import com.sprinkler.Data.Model.LoginResponse;
import com.sprinkler.Data.Model.SocialLoginResponse;
import com.sprinkler.Data.Remote.APIUtils;
import com.sprinkler.R;
import com.sprinkler.Sprinkler;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FirstScreen extends AppCompatActivity {

    String token;
    GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 007;
    CallbackManager callbackManager;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager=CallbackManager.Factory.create();


        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.sprinkler",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        token = FirebaseInstanceId.getInstance().getToken();
//        FirebaseMessaging.getInstance().subscribeToTopic("sprinkleruser");
        Log.d("mytag", "Refreshed token: " + token);
        Prefs.getPrefInstance().setValue(FirstScreen.this, Const.FCMID,token);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 0);
            }
        }

//        int PERMISSION_ALL = 1;
//        String[] PERMISSIONS = new String[0];
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            PERMISSIONS = new String[]{Manifest.permission_group.STORAGE, Manifest.permission_group.LOCATION, Manifest.permission_group.CAMERA};
//        }else{
//            PERMISSIONS = new String[]{Manifest.permission_group.STORAGE, Manifest.permission_group.LOCATION, Manifest.permission.CAMERA};
//        }
//
//        if (!CheckPermission.hasPermissions(FirstScreen.this, PERMISSIONS)) {
//            ActivityCompat.requestPermissions(FirstScreen.this, PERMISSIONS, PERMISSION_ALL);
//        }
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

        if (!CheckPermission.hasPermissions(FirstScreen.this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(FirstScreen.this, PERMISSIONS, PERMISSION_ALL);
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account!=null){
            getGoogleAccDetails(account);
        }


        LinearLayout login = findViewById(R.id.fscreenlogin);
        LinearLayout signup = findViewById(R.id.fscreensignup);
        LinearLayout skip = findViewById(R.id.fscreenskip);
        LinearLayout facebook = findViewById(R.id.fscreenfacebook);
        LinearLayout googleplus = findViewById(R.id.fscreengoogle);


        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Prefs.getPrefInstance().setValue(FirstScreen.this, Const.APPSTATUS,"Skipped");
                Prefs.getPrefInstance().setValue(FirstScreen.this, Const.USER_ID, "");
                Prefs.getPrefInstance().setValue(FirstScreen.this, Const.ROLE, "");
                Prefs.getPrefInstance().setValue(FirstScreen.this, Const.LOGIN_ACCOUNT, "");
                Intent intent = new Intent(FirstScreen.this,HomeScreen.class);
                startActivity(intent);
                finish();
            }
        });

        callbackManager = CallbackManager.Factory.create();

        final LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);

        List< String > permissionNeeds = Arrays.asList("email", "public_profile");
        loginButton.setReadPermissions(permissionNeeds);
        loginButton.registerCallback(callbackManager,
                new FacebookCallback< LoginResult >() {@Override
                public void onSuccess(LoginResult loginResult) {

                    System.out.println("onSuccess");

//                    String accessToken = loginResult.getAccessToken()
//                            .getToken();
//                    Log.i("accessToken", accessToken);

                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {@Override
                            public void onCompleted(JSONObject object,
                                                    GraphResponse response) {

                                Log.i("LoginActivity",
                                        response.toString());
                                try {
                                    String id = object.getString("id");
                                    URL profile_pic = null;
                                    try {
                                         profile_pic = new URL(
                                                "http://graph.facebook.com/" + id + "/picture?type=large");
                                        Log.i("profile_pic",
                                                profile_pic + "");

                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    }
                                    String name = object.getString("name");
                                    String email = object.getString("email");
//                                    String gender = object.getString("gender");
//                                    String birthday = object.getString("birthday");

                                    callsocialloginapi(email, name, String.valueOf(profile_pic), "facebook");
                                } catch (JSONException e) {
                                    LoginManager.getInstance().logOut();
                                    Toast.makeText(FirstScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields",
                            "id,name,email");
                    request.setParameters(parameters);
                    request.executeAsync();
                }

                    @Override
                    public void onCancel() {
                        System.out.println("onCancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
//                        System.out.println("onError");
//                        Log.v("LoginActivity", exception.getCause().toString());
                    }
                });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FirstScreen.this,Login.class);
                startActivity(intent);
                finish();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FirstScreen.this,Signup.class);
                startActivity(intent);
                finish();
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButton.performClick();
            }
        });

        googleplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(result);
        }else{
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {

        Log.d("mytag", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {

                GoogleSignInAccount account = result.getSignInAccount();

                // Signed in successfully, show authenticated UI.
                getGoogleAccDetails(account);
        }else{
            Log.d("mytag","result is : " + result.getStatus() + " " + result.getStatus().getStatusMessage() + " " + result.getStatus().getResolution() + " " + result.getStatus().getStatusCode());
        }
    }

    private void getGoogleAccDetails(GoogleSignInAccount account) {
        if(account != null){
            String name = account.getDisplayName();
            String email = account.getEmail();
            Uri  image = account.getPhotoUrl();
            String displayname = account.getDisplayName();
            Long Expiry_time = account.getExpirationTimeSecs();

            if(email != null){
                callsocialloginapi(email, name, String.valueOf(image), "google");
            }else{
                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setTitle(R.string.app_name)
                        .setMessage("Something Went Wrong. Please Try Again With Different Account.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }

                        })
                        .show();
            }

            Log.d("mytag","Google SignIn Name : " + name + " Email : " + email + " image url : " + image + " display name : " + displayname + " Expiry time :: " + Expiry_time);
        }
    }

    private void callsocialloginapi(String email, String name, final String image, final String loginaccount) {
        if (Utils.getInstance().isConnectivity(FirstScreen.this)) {
            Progress.start(FirstScreen.this, false);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", email );
                jsonObject.put("name", name);
                jsonObject.put("fcm_id", token);
                jsonObject.put("role", 2);
                jsonObject.put("image", image);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String allvalues = jsonObject.toString();
            final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), allvalues);
            Call<SocialLoginResponse> socialLoginResponseCall = APIUtils.getAPIService().socialloginuser(Prefs.getPrefInstance().getValue(FirstScreen.this, Const.ACCESS_TOKEN, ""), requestBody);
            socialLoginResponseCall.enqueue(new Callback<SocialLoginResponse>() {
                @Override
                public void onResponse(Call<SocialLoginResponse> call, Response<SocialLoginResponse> response) {
                    if (response.isSuccessful()) {
                        int status = response.body().getStatus();
                        if (status == 1) {
                            Prefs.getPrefInstance().setValue(FirstScreen.this, Const.USER_ID, response.body().getUserId().toString());
                            Prefs.getPrefInstance().setValue(FirstScreen.this, Const.EMAIL, response.body().getEmail());
                            Prefs.getPrefInstance().setValue(FirstScreen.this, Const.NAME, response.body().getUsername());
                            Prefs.getPrefInstance().setValue(FirstScreen.this, Const.ROLE, response.body().getRole().toString());
                            Prefs.getPrefInstance().setValue(FirstScreen.this, Const.PHONE_NUMBER, "");
                            Prefs.getPrefInstance().setValue(FirstScreen.this, Const.PROFILE_IMAGE, response.body().getProfileImage());
                            Prefs.getPrefInstance().setValue(FirstScreen.this, Const.LOGIN_ACCESS, "Logged_In");
                            Prefs.getPrefInstance().setValue(FirstScreen.this, Const.LOGIN_ACCOUNT, loginaccount);
                            Prefs.getPrefInstance().setValue(FirstScreen.this, Const.APPSTATUS, "Running");

                            if(response.body().getUserProcess() == 0){
                                Intent intent = new Intent(FirstScreen.this,HomeScreen.class);
                                Progress.stop();
                                startActivity(intent);
                                finish();
                            }else{
                                getappstatus();
                            }
                        } else {
                            Progress.stop();
                            Toast.makeText(FirstScreen.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Progress.stop();
                        Log.d("mytag", "response error.");
                    }
                }

                @Override
                public void onFailure(Call<SocialLoginResponse> call, Throwable t) {
                    Progress.stop();
                    t.printStackTrace();
                }
            });
        }
    }

    private void getappstatus() {
        if (Utils.getInstance().isConnectivity(FirstScreen.this)) {
            Call<AppStatusResponse> appStatusResponseCall = APIUtils.getAPIService().getappstatus(Prefs.getPrefInstance().getValue(FirstScreen.this, Const.ACCESS_TOKEN, ""), Prefs.getPrefInstance().getValue(FirstScreen.this, Const.USER_ID, ""), Prefs.getPrefInstance().getValue(FirstScreen.this, Const.ROLE, ""));
            appStatusResponseCall.enqueue(new Callback<AppStatusResponse>() {
                @Override
                public void onResponse(Call<AppStatusResponse> call, Response<AppStatusResponse> response) {
                    if (response.isSuccessful()) {
                        int status = response.body().getStatus();
                        if (status == 1) {
                            Prefs.getPrefInstance().setValue(Sprinkler.applicationContext, Const.WASHERLATITUDE, response.body().getLatitude());
                            Prefs.getPrefInstance().setValue(Sprinkler.applicationContext, Const.WASHERNUMBER, response.body().getPhone());
                            Prefs.getPrefInstance().setValue(Sprinkler.applicationContext, Const.USERMESSAGE, "");
                            Prefs.getPrefInstance().setValue(Sprinkler.applicationContext, Const.WASHERNAME, response.body().getName());
                            Prefs.getPrefInstance().setValue(Sprinkler.applicationContext, Const.SELECTEDWASHPLAN, response.body().getPlan());
                            Prefs.getPrefInstance().setValue(Sprinkler.applicationContext, Const.SELECTEDCARTYPE, response.body().getCarType());
                            Prefs.getPrefInstance().setValue(Sprinkler.applicationContext, Const.WASHERLONGITUDE, response.body().getLongitude());
                            Prefs.getPrefInstance().setValue(Sprinkler.applicationContext, Const.USERNOTITYPE, response.body().getNotiType().toString());
                            Prefs.getPrefInstance().setValue(Sprinkler.applicationContext, Const.WASHERID, response.body().getUserId().toString());
                            Prefs.getPrefInstance().setValue(Sprinkler.applicationContext, Const.WASHERLOCATION, response.body().getLocation());
                            Prefs.getPrefInstance().setValue(Sprinkler.applicationContext, Const.WASHERIMAGE, response.body().getImage());
//                            Prefs.getPrefInstance().setValue(Sprinkler.applicationContext, Const.WASHERTIP, "0");
                            Prefs.getPrefInstance().setValue(Sprinkler.applicationContext, Const.WASHAMOUNT, response.body().getAmount());
                            Prefs.getPrefInstance().setValue(Sprinkler.applicationContext, Const.APPSTATUS, response.body().getAppstatus());
                            Intent intent = new Intent(FirstScreen.this, HomeScreen.class);
                            Progress.stop();
                            startActivity(intent);
                            finish();
                        } else {
                            Progress.stop();
                            Toast.makeText(FirstScreen.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Progress.stop();
                        Log.d("mytag", "response error.");
                    }
                }

                @Override
                public void onFailure(Call<AppStatusResponse> call, Throwable t) {
                    Progress.stop();
                    t.printStackTrace();
                }
            });
        } else {
            Toast.makeText(FirstScreen.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.app_name)
                .setMessage("Are you sure you want to Exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
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
                ((InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
}
