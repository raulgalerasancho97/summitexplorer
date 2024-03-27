package com.example.summitexplorer.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.osmdroid.util.GeoPoint

@Entity(tableName = "route")
data class Route(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Int, // Clave foránea para la relación con User
    var routePoints: MutableList<Pair<GeoPoint, String>> = mutableListOf()
){
    //constructor(userId: Int) : this(userId = userId)

    // Método para añadir un GeoPoint a la lista de GeoPoints
    fun addGeoPoint(geoPoint: Pair<GeoPoint, String>) {
        routePoints.add(geoPoint)
    }
    // Método para limpiar la lista de puntos
    fun clearPoints() {
        routePoints.clear()
    }
}

