package com.example.summitexplorer.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.summitexplorer.database.model.Point

@Dao
interface PointDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoints(points: MutableList<Point>)

    @Query("SELECT * FROM Point WHERE routeId = :routeId")
    suspend fun getPointsByRouteId(routeId: Long): List<Point>
}
