package com.example.summitexplorer.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.summitexplorer.database.model.Route

@Dao
interface RouteDao {
    @Insert
    suspend fun insertRoute(route: Route)

    @Transaction
    @Query("SELECT * FROM route")
    suspend fun getRoutesWithPoints(): List<Route>

    @Transaction
    @Query("SELECT * FROM route WHERE userId = :userId")
    suspend fun getRoutesForUser(userId: Int): List<Route>
}