package asabre.com.buyout.service.repository;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import asabre.com.buyout.service.model.UserDao;
import asabre.com.buyout.service.model.UserEntity;

@Database(entities = {UserEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao mUserDao();
}
