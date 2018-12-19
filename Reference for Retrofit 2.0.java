
/**
 * Created by Saumil on 1/11/2018.
 */

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////// Gradle Dependencies ////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

implementation 'com.squareup.okhttp3:logging-interceptor:3.9.1'
implementation 'com.squareup.retrofit2:converter-gson:2.3.0'
implementation 'com.squareup.retrofit2:retrofit:2.3.0'
implementation 'com.google.code.gson:gson:2.8.2'
implementation 'com.squareup.okhttp3:okhttp:3.10.0'

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////// Retrofit Connection ////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class RetrofitClient {

    private static Retrofit retrofit = null;

    static Retrofit getClient(String baseUrl) {
        if (retrofit==null) {

            HttpLoggingInterceptor Logging=new HttpLoggingInterceptor();  // For Log
            Logging.setLevel(HttpLoggingInterceptor.Level.BODY);  // BODY level only for log
            OkHttpClient.Builder http=new OkHttpClient.Builder();
            http.connectTimeout(1, TimeUnit.MINUTES);  // connection timeout time
            http .readTimeout(1, TimeUnit.MINUTES);   // read data timeout time
            http.writeTimeout(1, TimeUnit.MINUTES);   // write data timeout time
            http.addInterceptor(Logging);

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(http.build())
                    .build();
        }
        return retrofit;
    }

}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////  Util for Retrofit Conncection and API Interface  //////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class APIUtils {

    public static final String BASE_URL = Const.HOST;

    public static APIService getAPIService() {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////// REFERENCE EXAMPLE of API Interface of Retrofit 2.0 (Updated) ////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public interface APIService {

//---------------------------- WITHOUT HEADER --------------------------------------------//
//    @GET(Const.)
//    Call<Response> getData();

//    @GET(Const.)
//    Call<Response> getData(@Path("id") int id);

//    @POST(Const.)
//    @FormUrlEncoded
//    Call<Response> postData(@Field("") String, @Field("") String);

//    @POST(Const.)
//    Call<Response> postData(@Body RequestBody requestBody);

//    @POST(Const.)
//    Call<Response> postData(@Path("id") int id, @Body RequestBody requestBody);

//    @Multipart
//    @POST(Const.)
//    Call<Response> postData(@Part MultipartBody.Part Image);

//    @Multipart
//    @POST(Const.)
//    Call<Response> postData(@Path("id") int id, @Part MultipartBody.Part Image);

//    @Multipart
//    @POST(Const.)
//    Call<Response> postData(@Part MultipartBody.Part Image, @Part("") String);

//    @Multipart
//    @POST(Const.)
//    Call<Response> postData(@Path("id") int id, @Part MultipartBody.Part Image, @Part("") RequestBody);


//-------------------------------- WITH HEADER --------------------------------------------//


//    @GET(Const.)
//    Call<Response> getData(@Header("Authorization") String authorization);

//    @GET(Const.)
//    Call<Response> getData(@Header("Authorization") String authorization, @Path("id") int id);

//    @POST(Const.)
//    @FormUrlEncoded
//    Call<Response> postData(@Header("Authorization") String authorization, @Field("") String, @Field("") String);

//    @POST(Const.)
//    Call<Response> postData(@Header("Authorization") String authorization, @Body RequestBody requestBody);

//    @POST(Const.)
//    Call<Response> postData(@Header("Authorization") String authorization, @Path("id") int id, @Body RequestBody requestBody);

//    @Multipart
//    @POST(Const.)
//    Call<Response> postData(@Header("Authorization") String authorization, @Part MultipartBody.Part Image);

//    @Multipart
//    @POST(Const.)
//    Call<Response> postData(@Header("Authorization") String authorization, @Path("id") int id, @Part MultipartBody.Part Image);

//    @Multipart
//    @POST(Const.)
//    Call<Response> postData(@Header("Authorization") String authorization, @Part MultipartBody.Part Image, @Part("") String);

//    @Multipart
//    @POST(Const.)
//    Call<Response> postData(@Header("Authorization") String authorization, @Path("id") int id, @Part MultipartBody.Part Image, @Part("") RequestBody);

}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////// API CALL ////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// Prefs.getPrefInstance().getValue(activity, Const.ACCESS_TOKEN, "")

if (Utils.getInstance().isConnectivity(activity)) {
        Progress.start(activity, false);
        Call<responseclass> responseCall = APIUtils.getAPIService().getdetails();
            responseCall.enqueue(new Callback<responseclass>() {
                @Override
                public void onResponse(Call<responseclass> call, Response<responseclass> response) {
                    if (response.isSuccessful()) {
                        int status = response.body().getStatus();
                        if (status == 1) {
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
                public void onFailure(Call<responseclass> call, Throwable t) {
                    Progress.stop();
                    t.printStackTrace();
                }
            });
        } else {
            Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////  END  ///////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////