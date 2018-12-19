package com.chocolateradio.Data.Remote;


import com.chocolateradio.Custom.Const;

/**
 * Created by Saumil on 1/11/2018.
 */

public class APIUtils {

    public static final String BASE_URL = Const.HOST_CHOCOLATERADIO;

    public static APIService getAPIService() {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

}
