package com.socialout.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.socialout.API.APIClient;
import com.socialout.API.APIInterface;
import com.socialout.Custom.CheckPermission;
import com.socialout.Custom.Const;
import com.socialout.Custom.Prefs;
import com.socialout.Custom.Progress;
import com.socialout.Custom.Utils;
import com.socialout.Model.LoginData;
import com.socialout.Model.LoginResponse;
import com.socialout.Model.SigninBg;
import com.socialout.R;
import com.socialout.Socialout;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class
SignInActivity extends AppCompatActivity {

    Button signin;
    TextView forgetpass,terms;
    ImageView arr;
    ImageView bgimage;
    EditText username,password;
    String list, id = "", deeplink = "";
    CheckBox keepsignin;
    LinearLayout sign;
    private String token;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Socialout.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Socialout.activityPaused();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
//        FirebaseApp.initializeApp(this);
        token = FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic("socialoutnotifyall");
        Log.d("mytag", "Refreshed token: " + token);
        
        Prefs.getPrefInstance().setValue(SignInActivity.this,Const.ACCESS_TOKEN,"Xs8Q6eZKglCaPJA5lce9KjLVpOMAxziQ");
        id = "empty";
        deeplink = "empty";

        if(getIntent().getData() != null){
        id = getIntent().getData().getQueryParameter("id");
        deeplink = getIntent().getData().getQueryParameter("deeplink");}
        Log.d("mytag","group id is :: " + id + " deep link is : " + deeplink) ;

//        int PERMISSION_ALL = 1;
//        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,Manifest.permission.READ_CONTACTS};
//
//        if(!CheckPermission.hasPermissions(SignInActivity.this, PERMISSIONS)){
//            ActivityCompat.requestPermissions(SignInActivity.this, PERMISSIONS, PERMISSION_ALL);
//        }

        if(Prefs.getPrefInstance().getValue(SignInActivity.this,Const.LOGIN_ACCESS,"").equals("Logged_In")){
            Intent in = new Intent(SignInActivity.this,HomeActivity.class);
            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                in.putExtra("id",id);
                in.putExtra("deeplink",deeplink);
            Log.d("mytag","signin to home"+"group id is :: " + id + " deep link is : " + deeplink) ;
            startActivity(in);
            finish();
        }
        keepsignin=findViewById(R.id.keepsignin);
        bgimage= findViewById(R.id.bgimage);
        signin=findViewById(R.id.signin);

        username= findViewById(R.id.username);
        password= findViewById(R.id.password);
        sign = findViewById(R.id.sign);
        terms=findViewById(R.id.terms);

        ScrollView scrollView = findViewById(R.id.signinscroll);
        scrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocusFromTouch();
                ((InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((SignInActivity.this.getWindow().getDecorView().getApplicationWindowToken()), 0);
                return false;
            }
        });

        forgetpass=findViewById(R.id.forgetpass);

        if (Utils.getInstance().isConnectivity(SignInActivity.this)) {
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            Call<SigninBg> signinBgCall = apiInterface.Bgimage(Prefs.getPrefInstance().getValue(SignInActivity.this, Const.ACCESS_TOKEN, ""));

            signinBgCall.enqueue(new Callback<SigninBg>() {
                @Override
                public void onResponse(Call<SigninBg> call, Response<SigninBg> response) {
                    int status = response.body().getStatus();
                    if (status == 1) {
                        list = response.body().getData();
                        Picasso.with(SignInActivity.this).load(list).placeholder(R.drawable.backsignin).into(bgimage);
                    } else {
                        Picasso.with(SignInActivity.this).load(R.drawable.backsignin).into(bgimage);
                    }
                }

                @Override
                public void onFailure(Call<SigninBg> call, Throwable t) {
                    Picasso.with(SignInActivity.this).load(R.drawable.backsignin).into(bgimage);
                }
            });
        }

        else {
            Toast.makeText(SignInActivity.this , "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

        if (Prefs.getPrefInstance().getValue(SignInActivity.this, Const.KEEPSIGNEDIN, "").equals("true")) {
            username.setText(Prefs.getPrefInstance().getValue(SignInActivity.this,Const.EMAIL,""));
            password.setText(Prefs.getPrefInstance().getValue(SignInActivity.this,Const.PASSWORD,""));
            keepsignin.setChecked(true);
        }
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("deeplink",deeplink);
                Log.d("mytag","signin to signup"+"group id is :: " + id + " deep link is : " + deeplink) ;
                startActivity(intent);
                finish();
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(username.getText().toString().trim().length() == 0){username.setError("Field can not be empty."); username.requestFocus();}
                else if(!username.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")){username.requestFocus();username.setError("Invalid Email Address.");}
                else if(password.getText().toString().trim().length()==0){password.requestFocus();password.setError("Field can not be empty.");}
                else {
                    signin.setEnabled(false);
                    Progress.start(SignInActivity.this,false);
                    APIInterface apiService = APIClient.getClient().create(APIInterface.class);
                    Call<LoginResponse> loginResponseCall = apiService.loginDetails(Prefs.getPrefInstance().getValue(SignInActivity.this,Const.ACCESS_TOKEN,""), username.getText().toString(), password.getText().toString(),token);
                    loginResponseCall.enqueue(new Callback<LoginResponse>() {
                        @Override
                        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                            if (response.isSuccessful()) {
                                int status = response.body().getStatus();
                                if (status == 1) {
                                    LoginData loginData = response.body().getData();
                                    Prefs.getPrefInstance().setValue(SignInActivity.this, Const.USER_ID, loginData.getUserId().toString());
                                    Prefs.getPrefInstance().setValue(SignInActivity.this, Const.EMAIL, loginData.getEmail());
                                    Prefs.getPrefInstance().setValue(SignInActivity.this, Const.NAME, loginData.getName());
                                    Prefs.getPrefInstance().setValue(SignInActivity.this, Const.PASSWORD, password.getText().toString());
                                    Prefs.getPrefInstance().setValue(SignInActivity.this, Const.PROFILE_IMAGE,loginData.getImage());
                                    Prefs.getPrefInstance().setValue(SignInActivity.this, Const.LOGIN_ACCESS, "Logged_In");

                                    if(keepsignin.isChecked()){
                                        Prefs.getPrefInstance().setValue(SignInActivity.this,Const.KEEPSIGNEDIN,"true");
                                    }else{
                                        Prefs.getPrefInstance().setValue(SignInActivity.this,Const.KEEPSIGNEDIN,"false");
//                                        keepsignin.setSelected(false);
                                    }

                                    Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    signin.setEnabled(true);
                                    Progress.stop();
                                    finish();
                                } else {
                                    Progress.stop();
                                    signin.setEnabled(true);
                                    Toast.makeText(SignInActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Progress.stop();
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginResponse> call, Throwable t) {
                            Progress.stop();
                            signin.setEnabled(true);
                        }
                    });
                }
            }
        });


        forgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(SignInActivity.this,ForgetPassActivity.class);
                startActivity(in);
                finish();
            }
        });
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
