package com.example.summitexplorer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

class MapFragment : Fragment() {
    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val context = requireContext()
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = view.findViewById(R.id.mapView)
        setupMapView()
        return view
    }

    private fun setupMapView() {
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(14.0)
        val startPoint = GeoPoint(40.4168, -3.7038)
        mapView.controller.setCenter(startPoint)
    }
}
