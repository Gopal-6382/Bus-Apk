package com.example.bus.ui.dbs;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserDao {

    @Insert
    void insertUser(HelperDB user);

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    HelperDB loginUser(String email, String password);
}
