package com.example.summitexplorer.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.summitexplorer.database.dao.PointDao
import com.example.summitexplorer.database.dao.RouteDao
import com.example.summitexplorer.database.dao.UserDao
import com.example.summitexplorer.database.model.Point
import com.example.summitexplorer.database.model.Route
import com.example.summitexplorer.database.model.User

@Database(entities = [User::class, Route::class, Point::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun routeDao(): RouteDao
    abstract fun pointDao(): PointDao
}
