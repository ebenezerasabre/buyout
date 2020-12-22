package asabre.com.buyout.service.repository;

import android.content.Context;

import androidx.room.Room;
import asabre.com.buyout.view.ui.MainActivity;

public class DatabaseClient {
    private Context mContext;
    private static DatabaseClient mInstance;

    //our app database object
    private AppDatabase mAppDatabase;


    private DatabaseClient(Context context){
        mContext = context;
        // creating the app database with Room database builder
        // Quest is the name of the database
        mAppDatabase = Room.databaseBuilder(mContext, AppDatabase.class, "Quest").build();
    }


    public static synchronized DatabaseClient getInstance(Context context){
        if(mInstance == null){
            mInstance = new DatabaseClient(context);
        }
        return mInstance;
    }

    public AppDatabase getAppDatabase(){
        return mAppDatabase;
    }


}
