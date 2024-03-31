package com.example.summitexplorer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.summitexplorer.R
import com.example.summitexplorer.database.model.Point
import com.example.summitexplorer.database.model.Route

class RouteAdapter(private val routesWithPoints: List<Pair<Route, List<Point>>>) :
    RecyclerView.Adapter<RouteAdapter.RouteViewHolder>() {

    class RouteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val routeNameTextView: TextView = itemView.findViewById(R.id.routeNameTextView)
        val pointNameTextView: TextView = itemView.findViewById(R.id.pointNameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_route, parent, false)
        return RouteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        val (route, points) = routesWithPoints[position]
        //holder.routeNameTextView.text = route.name
        holder.routeNameTextView.text = "Puntos de la ruta"

        //val pointsText = points.joinToString(separator = ", ") { it.name }
        val pointsText = points.joinToString(separator = "\n") {
            val formattedLatitude = "%.3f".format(it.latitude)
            val formattedLongitude = "%.3f".format(it.longitude)
            "${it.name} ($formattedLatitude, $formattedLongitude)"
        }
        holder.pointNameTextView.text = pointsText
    }

    override fun getItemCount(): Int = routesWithPoints.size
}
