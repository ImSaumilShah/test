package com.sprinkler.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sprinkler.Custom.Const;
import com.sprinkler.Custom.Prefs;
import com.sprinkler.Custom.Progress;
import com.sprinkler.Custom.Utils;
import com.sprinkler.Data.Model.AppStatusResponse;
import com.sprinkler.Data.Model.LoginResponse;
import com.sprinkler.Data.Remote.APIUtils;
import com.sprinkler.R;
import com.sprinkler.Sprinkler;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Login extends AppCompatActivity {

    String token;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        token = FirebaseInstanceId.getInstance().getToken();
//        FirebaseMessaging.getInstance().subscribeToTopic("sprinkleruser");
        Log.d("mytag", "Refreshed token: " + token);
        Prefs.getPrefInstance().setValue(Login.this, Const.FCMID, token);

        final EditText username = findViewById(R.id.loginusername);
        final EditText password = findViewById(R.id.loginpassword);
        final TextInputLayout loginerror = findViewById(R.id.loginerror);
        TextView forgotpassword = findViewById(R.id.forgotpassword);
        LinearLayout login = findViewById(R.id.btnlogin);
        LinearLayout signup = findViewById(R.id.loginsignup);
        LinearLayout upbtn = findViewById(R.id.loginupbtn);
        ScrollView scrollView = (ScrollView) findViewById(R.id.loginscroll);
        scrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocusFromTouch();
                ((InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((Login.this.getWindow().getDecorView().getApplicationWindowToken()), 0);
                return false;
            }
        });

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ForgotPassword.class);
                startActivity(intent);
                finish();
            }
        });

        upbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Signup.class);
                startActivity(intent);
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginerror.setVisibility(View.GONE);
                if (username.getText().toString().length() == 0) {
                    username.requestFocus();
                    username.setError("Field can not be empty.");
                } else if (!username.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {
                    username.requestFocus();
                    username.setError("Invalid Email Address.");
                } else if (password.getText().toString().length() == 0) {
                    password.requestFocus();
                    password.setError("Field can not be empty.");
                } else {
                    if (Utils.getInstance().isConnectivity(Login.this)) {
                        Progress.start(Login.this, false);
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("email", username.getText().toString());
                            jsonObject.put("password", password.getText().toString());
                            jsonObject.put("fcm_id", token);
                            jsonObject.put("role", 2);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String allvalues = jsonObject.toString();
                        final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), allvalues);
                        Call<LoginResponse> loginResponseCall = APIUtils.getAPIService().loginuser(Prefs.getPrefInstance().getValue(Login.this, Const.ACCESS_TOKEN, ""), requestBody);
                        loginResponseCall.enqueue(new Callback<LoginResponse>() {
                            @Override
                            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                                if (response.isSuccessful()) {
                                    int status = response.body().getStatus();
                                    if (status == 1) {
                                        Prefs.getPrefInstance().setValue(Login.this, Const.USER_ID, response.body().getUserId().toString());
                                        Prefs.getPrefInstance().setValue(Login.this, Const.EMAIL, response.body().getEmail());
                                        Prefs.getPrefInstance().setValue(Login.this, Const.NAME, response.body().getUsername());
                                        Prefs.getPrefInstance().setValue(Login.this, Const.ROLE, response.body().getRole().toString());
                                        Prefs.getPrefInstance().setValue(Login.this, Const.PHONE_NUMBER, response.body().getPhone());
                                        Prefs.getPrefInstance().setValue(Login.this, Const.PROFILE_IMAGE, response.body().getProfileImage());
                                        Prefs.getPrefInstance().setValue(Login.this, Const.LOGIN_ACCESS, "Logged_In");
                                        Prefs.getPrefInstance().setValue(Login.this, Const.LOGIN_ACCOUNT, "email");
                                        Prefs.getPrefInstance().setValue(Login.this, Const.APPSTATUS, "Running");

                                        if (response.body().getUserProcess() == 0) {
                                            Intent intent = new Intent(Login.this, HomeScreen.class);
                                            Progress.stop();
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            getappstatus();
                                        }
                                    } else {
                                        Progress.stop();
                                        loginerror.setVisibility(View.VISIBLE);
                                        loginerror.setError(response.body().getMsg());
//                                        Toast.makeText(Login.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Progress.stop();
                                    Log.d("mytag", "response error.");
                                }
                            }

                            @Override
                            public void onFailure(Call<LoginResponse> call, Throwable t) {
                                Progress.stop();
                                t.printStackTrace();
                            }
                        });
                    } else {
                        Toast.makeText(Login.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void getappstatus() {
        if (Utils.getInstance().isConnectivity(Login.this)) {
            Call<AppStatusResponse> appStatusResponseCall = APIUtils.getAPIService().getappstatus(Prefs.getPrefInstance().getValue(Login.this, Const.ACCESS_TOKEN, ""), Prefs.getPrefInstance().getValue(Login.this, Const.USER_ID, ""), Prefs.getPrefInstance().getValue(Login.this, Const.ROLE, ""));
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
                            if(response.body().getAppstatus().equals("Cancelled")){
                                Prefs.getPrefInstance().setValue(Sprinkler.applicationContext, Const.APPSTATUS, "Running");
                            }else{
                                Prefs.getPrefInstance().setValue(Sprinkler.applicationContext, Const.APPSTATUS, response.body().getAppstatus());
                            }
                            Intent intent = new Intent(Login.this, HomeScreen.class);
                            Progress.stop();
                            startActivity(intent);
                            finish();
                        } else {
                            Progress.stop();
                            Toast.makeText(Login.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(Login.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Login.this, FirstScreen.class);
        startActivity(intent);
        finish();
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
}
