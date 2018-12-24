package com.sprinkler.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sprinkler.Custom.Const;
import com.sprinkler.Custom.Prefs;
import com.sprinkler.Custom.Progress;
import com.sprinkler.Custom.Utils;
import com.sprinkler.Data.Model.LegalTermsResponse;
import com.sprinkler.Data.Model.SignupResponse;
import com.sprinkler.Data.Model.TermsResponse;
import com.sprinkler.Data.Remote.APIUtils;
import com.sprinkler.R;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Signup extends AppCompatActivity {

    String token;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        token = FirebaseInstanceId.getInstance().getToken();
//        FirebaseMessaging.getInstance().subscribeToTopic("sprinkleruser");
        Log.d("mytag", "Refreshed token: " + token);
        Prefs.getPrefInstance().setValue(Signup.this,Const.FCMID,token);

        final EditText username = findViewById(R.id.signupusername);
        final EditText email = findViewById(R.id.signupemail);
//        final EditText repeatemail = findViewById(R.id.signuprepeatemail);
        final EditText password = findViewById(R.id.signuppassword);
//        final EditText repeatpassword = findViewById(R.id.signuprepeatpassword);
        final EditText phone = findViewById(R.id.signupphone);
        final CheckBox terms = findViewById(R.id.signupterms);
        final TextInputLayout error = findViewById(R.id.signuperror);
        final LinearLayout signup = findViewById(R.id.btnsignup);
        final LinearLayout upbtn = findViewById(R.id.signupupbtn);
        final LinearLayout termsofservices = findViewById(R.id.termsofservices);
        ScrollView scrollView = (ScrollView) findViewById(R.id.signupscroll);
        scrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocusFromTouch();
                ((InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((Signup.this.getWindow().getDecorView().getApplicationWindowToken()), 0);
                return false;
            }
        });

        termsofservices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View popupView = LayoutInflater.from(Signup.this).inflate(R.layout.textdialog, null);
                final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                ImageView closedialog = popupView.findViewById(R.id.dialogclose);
                TextView dialogheader = popupView.findViewById(R.id.dialogheader);
                final TextView dialogtext = popupView.findViewById(R.id.dialogtext);

                dialogheader.setText("Terms of Service");

                if (Utils.getInstance().isConnectivity(Signup.this)) {
                    Progress.start(Signup.this, false);
                    Call<LegalTermsResponse> responseCall = APIUtils.getAPIService().getterms(Prefs.getPrefInstance().getValue(Signup.this, Const.ACCESS_TOKEN, ""));

                    responseCall.enqueue(new Callback<LegalTermsResponse>() {
                        @Override
                        public void onResponse(Call<LegalTermsResponse> call, Response<LegalTermsResponse> response) {
                            if (response.isSuccessful()) {
                                int status = response.body().getStatus();
                                if (status == 1) {
                                    dialogtext.setText(Html.fromHtml(response.body().getDescription()));
                                    Progress.stop();
                                } else {
                                    Progress.stop();
                                }
                            } else {
                                Progress.stop();
                                Log.d("mytag", "response error.");
                            }
                        }

                        @Override
                        public void onFailure(Call<LegalTermsResponse> call, Throwable t) {
                            Progress.stop();
                            t.printStackTrace();
                        }
                    });
                } else {
                    Toast.makeText(Signup.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }

                closedialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindow.dismiss();
                    }
                });

                popupWindow.setFocusable(true);
                popupWindow.setBackgroundDrawable(new ColorDrawable());
                int location[] = new int[2];
                view.getLocationOnScreen(location);
                popupWindow.showAtLocation(view, Gravity.CENTER,
                        0, 0);
//                        location[0], location[1] + view.getHeight()+50);
            }
        });

        terms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (terms.isChecked()) {
                    error.setVisibility(View.GONE);
                }
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
                if (username.getText().toString().length() == 0) {username.requestFocus(); username.setError("Field can not be empty.");}
                else if (email.getText().toString().length() == 0) {email.requestFocus(); email.setError("Field can not be empty.");}
                else if (!email.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {email.requestFocus(); email.setError("Invalid Email Address.");}
//                else if(repeatemail.getText().toString().length() == 0){ repeatemail.requestFocus(); repeatemail.setError("Field can not be empty.");}
//                else if(!repeatemail.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")){repeatemail.requestFocus();repeatemail.setError("Invalid Email Address.");}
//                else if(email.getText().toString().length() != repeatemail.getText().toString().length()){ repeatemail.requestFocus(); repeatemail.setError("Email does not match.");}
//                else if(!email.getText().toString().equals(repeatemail.getText().toString())){ repeatemail.requestFocus(); repeatemail.setError("Email does not match.");}
                else if (password.getText().toString().length() == 0) {password.requestFocus(); password.setError("Field can not be empty.");}
//                else if(repeatpassword.getText().toString().length()==0){repeatpassword.requestFocus(); password.setError("Field can not be empty.");}
//                else if(password.getText().toString().length() != repeatpassword.getText().toString().length()){repeatpassword.requestFocus(); repeatpassword.setError("Password does not match.");}
//                else if(!password.getText().toString().equals(repeatpassword.getText().toString())){repeatpassword.requestFocus(); repeatpassword.setError("Password does not match.");}
                else if (phone.getText().toString().length() == 0) {phone.requestFocus(); phone.setError("Field can not be empty.");}
                else if (phone.getText().toString().length() != 10) {phone.requestFocus(); phone.setError("Phone Number Must be 10 digits."); }
                else if (!terms.isChecked()) { error.setVisibility(View.VISIBLE); error.setError("Please agree to Terms of Service."); }
                else {
                    if (Utils.getInstance().isConnectivity(Signup.this)) {
                        Progress.start(Signup.this, false);
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("fullname", username.getText().toString());
                            jsonObject.put("email", email.getText().toString());
                            jsonObject.put("password", password.getText().toString());
                            jsonObject.put("number", phone.getText().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String allvalues = jsonObject.toString();
                        final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), allvalues);
                        Call<SignupResponse> SignupResponseCall = APIUtils.getAPIService().signupuser(Prefs.getPrefInstance().getValue(Signup.this, Const.ACCESS_TOKEN, ""), requestBody);
                        SignupResponseCall.enqueue(new Callback<SignupResponse>() {
                            @Override
                            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                                if (response.isSuccessful()) {
                                    int status = response.body().getStatus();
                                    if (status == 1) {
                                        Progress.stop();
                                        new AlertDialog.Builder(Signup.this)
                                                .setCancelable(false)
                                                .setTitle(R.string.app_name)
                                                .setMessage(response.body().getMsg())
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent intent = new Intent(Signup.this, Login.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                })
                                                .show();
                                    } else {
                                        Progress.stop();
                                        error.setVisibility(View.VISIBLE);
                                        error.setError(response.body().getMsg());
//                                        Toast.makeText(Signup.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Progress.stop();
                                    Log.d("mytag", "response error");
                                }
                            }

                            @Override
                            public void onFailure(Call<SignupResponse> call, Throwable t) {
                                Progress.stop();
                                t.printStackTrace();
                            }
                        });
                    } else {
                        Toast.makeText(Signup.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Signup.this, FirstScreen.class);
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
