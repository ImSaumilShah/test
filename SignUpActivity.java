package com.socialout.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.socialout.API.APIClient;
import com.socialout.API.APIInterface;
import com.socialout.Custom.Const;
import com.socialout.Custom.Prefs;
import com.socialout.Custom.Progress;
import com.socialout.Custom.Utils;
import com.socialout.Model.CountryData;
import com.socialout.Model.CountryResponse;
import com.socialout.Model.ForgetPassResponse;
import com.socialout.Model.SignUpResponse;
import com.socialout.Model.SigninBg;
import com.socialout.Model.TermsResponse;
import com.socialout.R;
import com.socialout.Socialout;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignUpActivity extends AppCompatActivity {

    Button signupbtn;
    CheckBox checkBox;
    EditText username,email,password,country;
    TextView error,terms,tv_terms;
    ImageView signupimage;
    String bg ;
    String tandc;
    LinearLayout signup;
    Spinner spcountry;
    List<CountryData> countryList;
    TextView spinnerlistitem;
    String chkemail;
    List<String> spinnerList = new ArrayList<>();
    String id = "", deeplink = "";
    private String token;

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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
//        FirebaseApp.initializeApp(this);
        token = FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic("socialoutnotifyall");
        Log.d("mytag", "Refreshed token: " + token);
        
        id = "empty";
        deeplink = "empty";

        if(getIntent().getData() != null){
            id = getIntent().getData().getQueryParameter("id");
            deeplink = getIntent().getData().getQueryParameter("deeplink");}

        id = getIntent().getStringExtra("id");
        Log.d("mytag","HOME ID " + id);
        deeplink=getIntent().getStringExtra("deeplink");
        Log.d("mytag"," HOME DEEPLINK " + deeplink);

        signup=(LinearLayout)findViewById(R.id.signup);
        signupbtn=(Button)findViewById(R.id.signupbtn);
        username=(EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.password);
        spinnerList.add("Country");

        email=(EditText)findViewById(R.id.emailadd);
        signupimage=(ImageView)findViewById(R.id.signupimage);
        checkBox=(CheckBox)findViewById(R.id.termcheckBox);
        error=(TextView) findViewById(R.id.error);
        spcountry=(Spinner)findViewById(R.id.spcountry);
        terms = (TextView)findViewById(R.id.terms);

        ScrollView scrollView = (ScrollView)findViewById(R.id.scrollsignup);
        scrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocusFromTouch();
                ((InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((SignUpActivity.this.getWindow().getDecorView().getApplicationWindowToken()), 0);
                return false;
            }
        });

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
//                if(!email.isFocused()){  email.setError("ckfiejvij");}
//                      else if(email.getText().toString().trim().length() == 0){email.setError("Field can not be empty."); email.requestFocus();}
//                else if(!email.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")){email.requestFocus();email.setError("Invalid Email Address.");}
//

                if(!email.isFocused()) {

                    if (Utils.getInstance().isConnectivity(SignUpActivity.this)) {
                        APIInterface cemail = APIClient.getClient().create(APIInterface.class);
                        final Call<ForgetPassResponse> checkEmail = cemail.checkEmail(Prefs.getPrefInstance().getValue(SignUpActivity.this, Const.ACCESS_TOKEN, ""), email.getText().toString());

                        checkEmail.enqueue(new Callback<ForgetPassResponse>() {
                            @Override
                            public void onResponse(Call<ForgetPassResponse> call, Response<ForgetPassResponse> response) {
                                if (response.isSuccessful()) {
                                    int status = response.body().getStatus();
                                    if (status == 0) {
                                        chkemail = response.body().getMsg();
                                        email.setError(chkemail);
                                        Toast.makeText(SignUpActivity.this, chkemail, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ForgetPassResponse> call, Throwable t) {

                            }
                        });
                    }
                }

                    else {
                        Toast.makeText(SignUpActivity.this , "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
            }
        });

        APIInterface apiInterface2 = APIClient.getClient().create(APIInterface.class);
        Call<TermsResponse> termscall = apiInterface2.getTerms(Prefs.getPrefInstance().getValue(SignUpActivity.this,Const.ACCESS_TOKEN,""));

        termscall.enqueue(new Callback<TermsResponse>() {
            @Override
            public void onResponse(Call<TermsResponse> call, Response<TermsResponse> response) {

                int status = response.body().getStatus();
                if(status ==1){
                    tandc=response.body().getData();
                    Log.d("mytag","t&c is :: " + tandc);
                }
            }

            @Override
            public void onFailure(Call<TermsResponse> call, Throwable t) {
                Progress.stop();
            }
        });


        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(SignUpActivity.this).inflate(R.layout.termsdialog, null);
                final AppCompatDialog dialog = new AppCompatDialog(SignUpActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(view);
                tv_terms = view.findViewById(R.id.tv_terms);
                tv_terms.setText(Html.fromHtml(tandc));
                ImageView close = view.findViewById(R.id.close);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(checkBox.isChecked()){
                    error.setVisibility(View.GONE);
                }else{
                    Log.d("mytag","not checked");
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("deeplink",deeplink);
                startActivity(intent);
                finish();
            }
        });

        Progress.start(SignUpActivity.this,false);

        APIInterface apiInterface1 = APIClient.getClient().create(APIInterface.class);
        Call<CountryResponse> countryResponseCall = apiInterface1.getcountry(Prefs.getPrefInstance().getValue(SignUpActivity.this,Const.ACCESS_TOKEN,""));
        countryResponseCall.enqueue(new Callback<CountryResponse>() {
            @Override
            public void onResponse(Call<CountryResponse> call, Response<CountryResponse> response) {
                int status = response.body().getStatus();
                if(status==1) {
                    Log.d("mytag"," in country");
                    countryList = response.body().getData();
                    for (int i = 0; i < countryList.size(); i++) {

                        spinnerList.add(countryList.get(i).getCountryName());
                    }

                    Progress.stop();

//                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SignUpActivity.this, R.layout.spinneritem, R.id.spinnerlistitem, spinnerList);

                    spcountry.setAdapter(new MyCustomAdapter(SignUpActivity.this, R.layout.spinneritem, spinnerList));
//                    spcountry.setSelection(100);
                }
            }

            @Override
            public void onFailure(Call<CountryResponse> call, Throwable t) {
                Progress.stop();
            }
        });

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<SigninBg> signinBgCall = apiInterface.signupimage(Prefs.getPrefInstance().getValue(SignUpActivity.this,Const.ACCESS_TOKEN,""));

        signinBgCall.enqueue(new Callback<SigninBg>() {
            @Override
            public void onResponse(Call<SigninBg> call, Response<SigninBg> response) {
                int status = response.body().getStatus();
                if(status==1)
                {
                    bg=response.body().getData();
                    Picasso.with(SignUpActivity.this).load(bg).placeholder(R.drawable.backsignin).into(signupimage);
                }else{
                    Picasso.with(SignUpActivity.this).load(R.drawable.backsignin).into(signupimage);
                }
            }

            @Override
            public void onFailure(Call<SigninBg> call, Throwable t) {
                Picasso.with(SignUpActivity.this).load(R.drawable.backsignin).into(signupimage);
            }
        });

        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.getText().toString().trim().length() == 0){username.setError("Field can not be empty."); username.requestFocus();}
                else if(email.getText().toString().trim().length() == 0){email.setError("Field can not be empty."); email.requestFocus();}
                else if(!email.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")){email.requestFocus();email.setError("Invalid Email Address.");}
//                else if(country.getText().toString().trim().length()==0){country.requestFocus();country.setError("Field can not be empty.");}
                else if(password.getText().toString().trim().length()==0){password.requestFocus();password.setError("Field can not be empty.");}
                else if(!checkBox.isChecked()){
                    error.setVisibility(View.VISIBLE);
                    error.setText("Please check Terms & Conditions in order to sign up.");
                }
                else {
                    Progress.start(SignUpActivity.this,false);
                    APIInterface apiService = APIClient.getClient().create(APIInterface.class);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("name", username.getText().toString());
                        jsonObject.put("email", email.getText().toString());
                        jsonObject.put("country", spcountry.getSelectedItemPosition());
                        jsonObject.put("password", password.getText().toString());
                        jsonObject.put("fcm_id",token);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String allvalues = jsonObject.toString();
                    final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), allvalues);

                    Call<SignUpResponse> signUpResponseCall = apiService.SignupDetails(Prefs.getPrefInstance().getValue(SignUpActivity.this, Const.ACCESS_TOKEN,""),requestBody);
                    signUpResponseCall.enqueue(new Callback<SignUpResponse>() {
                        @Override
                        public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                            if (response.isSuccessful()) {
                                int status = response.body().getStatus();
                                if (status == 1) {
                                    Progress.stop();
                                    Toast.makeText(SignUpActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                                    Prefs.getPrefInstance().setValue(SignUpActivity.this,Const.NAME,username.getText().toString());
                                    Prefs.getPrefInstance().setValue(SignUpActivity.this,Const.EMAIL,email.getText().toString());
                                    Prefs.getPrefInstance().setValue(SignUpActivity.this,Const.PASSWORD,password.getText().toString());
                                    Prefs.getPrefInstance().setValue(SignUpActivity.this,Const.USER_ID,response.body().getData().getUserId().toString());
                                    Prefs.getPrefInstance().setValue(SignUpActivity.this,Const.COUNTRY,String.valueOf(spcountry.getSelectedItemPosition()));

                                    Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("id",id);
                                    intent.putExtra("deeplink",deeplink);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    error.setVisibility(View.VISIBLE);
                                    String msg=response.body().getMsg();
                                    error.setText(msg);
                                    if (msg.trim().toLowerCase().equals("username is in use!")){
                                        username.setText("");
                                        username.requestFocus();
                                    }
                                    if (msg.trim().toLowerCase().equals("email already exists. please use another email id.")){
                                        email.setText("");
                                        email.requestFocus();
                                    }
                                    Progress.stop();
                                }
                            }else{
                                Progress.stop();
                            }
                        }

                        @Override
                        public void onFailure(Call<SignUpResponse> call, Throwable t) {
                            Progress.stop();
                        }
                    });
                }
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


    public class MyCustomAdapter extends ArrayAdapter<String> {

        public MyCustomAdapter(Context context, int textViewResourceId,
                               List<String> objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater=getLayoutInflater();
            View row=inflater.inflate(R.layout.spinneritem, parent, false);
            TextView label=(TextView)row.findViewById(R.id.spinnerlistitem);
            label.setText(spinnerList.get(position));

            if(position == 0){
                label.setTextColor(getResources().getColor(R.color.textbg));
            }else{
                label.setTextColor(getResources().getColor(R.color.textclr));
            }

            return row;
        }
    }
}
