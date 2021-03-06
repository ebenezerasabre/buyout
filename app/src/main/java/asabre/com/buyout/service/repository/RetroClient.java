package asabre.com.buyout.service.repository;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroClient {
    private static final String TAG = RetroClient.class.getSimpleName();
    public static final String BASE_URL = "https://www.quest.ewquest.com/buyout-0.0.1-SNAPSHOT/";
    private static Retrofit mRetrofit = null;

    static Retrofit getInstance(){
        Log.d(TAG, "getInstance: called");

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        if(mRetrofit == null){
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return mRetrofit;
    }

}




