package com.example.summitexplorer.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "Point",
    foreignKeys = [
        ForeignKey(entity = Route::class, parentColumns = ["id"], childColumns = ["routeId"])
    ])
data class Point(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "routeId")
    var routeId: Long, // Clave foránea para la relación con la tabla de rutas

    @ColumnInfo(name = "latitude")
    val latitude: Double, // Latitud del punto

    @ColumnInfo(name = "longitude")
    val longitude: Double, // Longitud del punto

    @ColumnInfo(name = "name")
    val name: String // Nombre del punto
)
