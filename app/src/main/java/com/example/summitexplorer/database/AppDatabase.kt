package com.example.summitexplorer.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.summitexplorer.database.dao.UserDao
import com.example.summitexplorer.database.model.User

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
