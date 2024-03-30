package com.example.summitexplorer.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.summitexplorer.database.model.Route

@Dao
interface RouteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRoute(route: Route): Long

    @Query("SELECT * FROM route WHERE userId = :userId")
    suspend fun getRoutesForUser(userId: Int): List<Route>

}