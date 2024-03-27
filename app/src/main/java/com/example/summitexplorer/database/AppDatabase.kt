package com.example.summitexplorer.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.summitexplorer.database.converter.RouteTypeConverters
import com.example.summitexplorer.database.dao.RouteDao
import com.example.summitexplorer.database.dao.UserDao
import com.example.summitexplorer.database.model.Route
import com.example.summitexplorer.database.model.User

@Database(entities = [User::class, Route::class], version = 2)
@TypeConverters(RouteTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun routeDao(): RouteDao
}
