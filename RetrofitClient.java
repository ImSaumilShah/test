package com.chocolateradio.Data.Remote;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Saumil on 1/11/2018.
 */

public class RetrofitClient {

    private static Retrofit retrofit = null;

    static Retrofit getClient(String baseUrl) {
        if (retrofit==null) {

            HttpLoggingInterceptor Logging=new HttpLoggingInterceptor();
            Logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder http=new OkHttpClient.Builder();
            http.connectTimeout(1, TimeUnit.MINUTES);
            http .readTimeout(1, TimeUnit.MINUTES);
            http.writeTimeout(1, TimeUnit.MINUTES);
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