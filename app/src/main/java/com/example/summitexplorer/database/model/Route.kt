package com.example.summitexplorer.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import org.osmdroid.util.GeoPoint

@Entity(tableName = "route",
        foreignKeys = [
            ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["userId"])
        ])
data class Route(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0L,

    @ColumnInfo(name = "userId")
    var userId: Int, // Clave foránea para la relación con User

    @Ignore
    var routePoints: MutableList<Point> = mutableListOf()
){
    constructor() : this(0L, 0) // Constructor vacío
    // Método para añadir un GeoPoint a la lista de GeoPoints
    fun addGeoPoint(geoPoint: Pair<GeoPoint, String>) {
        val newPoint = Point(
            routeId = this.id,
            latitude = geoPoint.first.latitude,
            longitude = geoPoint.first.longitude,
            name = geoPoint.second
        )
        routePoints.add(newPoint)
    }
    // Método para limpiar la lista de puntos
    fun clearPoints() {
        routePoints.clear()
    }

    fun changePointsRouteId(newRouteId: Long) {
        routePoints.forEach{ point: Point ->
            point.routeId = newRouteId
        }
    }
}

