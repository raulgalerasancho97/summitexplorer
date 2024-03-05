package com.example.summitexplorer

import android.app.Application
import androidx.room.Room
import com.example.summitexplorer.database.AppDatabase

class MyApp : Application() {

    companion object {
        lateinit var database: AppDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        // Inicializa la base de datos aquí
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "app-database"
        ).build()
    }
}
