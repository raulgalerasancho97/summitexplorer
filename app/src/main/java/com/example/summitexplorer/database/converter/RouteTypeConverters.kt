package com.example.summitexplorer.database.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.osmdroid.util.GeoPoint

class RouteTypeConverters {
    @TypeConverter
    fun fromRoutePoints(routePoints: MutableList<Pair<GeoPoint, String>>): String {
        return Gson().toJson(routePoints)
    }

    @TypeConverter
    fun toRoutePoints(routePointsString: String): MutableList<Pair<GeoPoint, String>> {
        val listType = object : TypeToken<MutableList<Pair<GeoPoint, String>>>() {}.type
        return Gson().fromJson(routePointsString, listType)
    }
}