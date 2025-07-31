package com.example.bus.ui.dbs;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {HelperDB.class}, version = 1)
public abstract class UserDatabase extends RoomDatabase {

    private static volatile UserDatabase instance;
    private static final int THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(THREADS);

    public abstract UserDao userDao();

    public static UserDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (UserDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    UserDatabase.class, "user_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}
