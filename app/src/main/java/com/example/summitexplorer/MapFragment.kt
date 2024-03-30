package com.example.summitexplorer

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.summitexplorer.database.dao.PointDao
import com.example.summitexplorer.database.dao.RouteDao
import com.example.summitexplorer.database.dao.UserDao
import com.example.summitexplorer.database.model.Point
import com.example.summitexplorer.database.model.Route
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import kotlin.properties.Delegates

class MapFragment : Fragment() {
    private lateinit var mapView: MapView
    private var isCreatingRoute = false
    private lateinit var routeCreationButton: Button
    private lateinit var cancelRouteButton: Button
    private lateinit var myLocationOverlay: MyLocationNewOverlay
    private lateinit var marker: Marker
    private var newRouteMarkers: MutableList<Marker> = mutableListOf()
    private lateinit var routeDao: RouteDao
    private lateinit var pointDao: PointDao
    private lateinit var userDao: UserDao
    private lateinit var newRoute: Route
    private lateinit var sharedPreferences: SharedPreferences
    private var userId by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val context = requireContext()
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        userId = 0
        userDao = MyApp.database.userDao()
        routeDao = MyApp.database.routeDao()
        pointDao = MyApp.database.pointDao()
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        val userEmail = sharedPreferences.getString("userEmail", "Anónimo") ?: "Anónimo"
        lifecycleScope.launch {
             userId = withContext(Dispatchers.IO) {
                userDao.getUserIdByEmail(userEmail)
            }
            newRoute = Route(userId = userId)
            showUserRoutes()
        }
        mapView = view.findViewById(R.id.mapView)
        routeCreationButton = view.findViewById(R.id.routeCreationButton)
        cancelRouteButton = view.findViewById(R.id.cancelRouteButton)

        setupMapView()
        setupButtons()

        return view
    }

    private fun setupMapView() {
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(14.0)
        val startPoint = GeoPoint(40.4168, -3.7038)
        mapView.controller.setCenter(startPoint)

        // Add marker overlay
        marker = Marker(mapView)
        mapView.overlays.add(marker)

        // Add location overlay
        myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), mapView)
        mapView.overlays.add(myLocationOverlay)
        myLocationOverlay.enableMyLocation()



        marker.setOnMarkerClickListener { marker, _ ->
            showNameInputDialog(marker.position)
            true
        }
    }

    private fun showUserRoutes() {
        lifecycleScope.launch {
            val routes = routeDao.getRoutesForUser(userId)
            routes?.forEach { route ->
                val routePoints = pointDao.getPointsByRouteId(route.id)
                var routeGeoPoints = mutableListOf<GeoPoint>()
                routePoints?.forEach { point ->
                    var geoPoint = GeoPoint(point.latitude,point.longitude)
                    addMarker(geoPoint, point.name)
                    routeGeoPoints.add(geoPoint)
                }
                addPolyline(routeGeoPoints)
            }
        }
    }

    private fun addPolyline(routePoints: List<GeoPoint>) {
        val polyline = Polyline()
        polyline.width = 5f
        polyline.color = Color.RED
        polyline.setPoints(routePoints)
        mapView.overlays.add(polyline)
        mapView.invalidate()
    }


    private fun setupButtons() {
        routeCreationButton.setOnClickListener {
            isCreatingRoute = !isCreatingRoute
            if (isCreatingRoute) {
                routeCreationButton.text = "Guardar ruta"
                cancelRouteButton.visibility = View.VISIBLE // Mostrar el botón de cancelar ruta
                setupCreationMode()
            } else {
                routeCreationButton.text = "Crear nueva ruta"
                cancelRouteButton.visibility = View.GONE // Ocultar el botón de cancelar ruta
                lifecycleScope.launch(Dispatchers.IO) {
                    var routeId = withContext(Dispatchers.IO) {
                        routeDao.insertRoute(newRoute)
                    }
                    newRoute.changePointsRouteId(routeId)
                    pointDao.insertPoints(newRoute.routePoints)
                    clearRoute(true)
                }
                Toast.makeText(requireContext(), "Ruta guardada", Toast.LENGTH_SHORT).show()
                mapView.setOnTouchListener(null)
            }
        }

        cancelRouteButton.setOnClickListener {
            isCreatingRoute = false
            routeCreationButton.text = "Crear nueva ruta"
            cancelRouteButton.visibility = View.GONE
            clearRoute(false) // Limpiar la ruta y los marcadores
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupCreationMode() {
        val tapTimeout = ViewConfiguration.getTapTimeout()

        mapView.setOnTouchListener { _, motionEvent ->
            when (motionEvent.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    // No hacer nada en ACTION_DOWN, simplemente permitir que el mapa maneje el evento
                    mapView.onTouchEvent(motionEvent)
                }

                MotionEvent.ACTION_UP -> {
                    // Si es un toque simple (es decir, no hubo movimiento significativo entre ACTION_DOWN y ACTION_UP)
                    if (motionEvent.eventTime - motionEvent.downTime < tapTimeout) {
                        // Mostrar el diálogo para ingresar el nombre del punto
                        showNameInputDialog(
                            mapView.projection.fromPixels(
                                motionEvent.x.toInt(),
                                motionEvent.y.toInt()
                            ) as GeoPoint
                        )
                        true // Indica que el evento ha sido manejado
                    } else {
                        false // Permitir que el mapa maneje el evento si es un gesto largo
                    }
                }

                else -> false
            }
        }
    }

    private fun showNameInputDialog(geoPoint: GeoPoint) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Ingrese un nombre para el punto de la ruta")

        val input = EditText(requireContext())
        builder.setView(input)

        builder.setPositiveButton("Guardar") { dialog, _ ->
            lifecycleScope.launch {
                val name = input.text.toString()
                newRoute.addGeoPoint(Pair(geoPoint, name))
                addMarker(geoPoint, name)
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun addMarker(position: GeoPoint, name: String) {
        val marker = Marker(mapView)
        marker.position = position
        marker.title = name
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView.overlays.add(marker)
        newRouteMarkers.add(marker)
    }

    private fun clearRoute(saved: Boolean) {
        // Limpiar la lista de puntos y los marcadores en el mapa
        newRoute.clearPoints()
        if(!saved) {
            newRouteMarkers.forEach { mapView.overlays.remove(it) }
        }
        newRouteMarkers.clear()
        mapView.invalidate()
    }
}
