package com.example.summitexplorer

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.summitexplorer.database.AppDatabase
import com.example.summitexplorer.database.dao.PointDao
import com.example.summitexplorer.database.dao.RouteDao
import com.example.summitexplorer.database.dao.UserDao
import com.example.summitexplorer.database.model.Point
import com.example.summitexplorer.database.model.Route
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListFragment : Fragment() {

    private lateinit var routeDao: RouteDao
    private lateinit var pointDao: PointDao
    private lateinit var userDao: UserDao
    private var userId: Int = 0

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RouteAdapter
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        routeDao = MyApp.database.routeDao()
        pointDao = MyApp.database.pointDao()
        userDao = MyApp.database.userDao()
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)


        val userEmail = sharedPreferences.getString("userEmail", "Anónimo") ?: "Anónimo"
        lifecycleScope.launch {
            userId = withContext(Dispatchers.IO) {
                userDao.getUserIdByEmail(userEmail)
            }
            val userRoutes = withContext(Dispatchers.IO) {
                routeDao.getRoutesForUser(userId)
            }
            val routesWithPoints = mutableListOf<Pair<Route, List<Point>>>()
            userRoutes?.forEach { route ->
                val routePoints = withContext(Dispatchers.IO) {
                    pointDao.getPointsByRouteId(route.id)
                }
                routesWithPoints.add(Pair(route, routePoints ?: listOf()))
            }
            recyclerView = view.findViewById(R.id.recyclerView)
            adapter = RouteAdapter(routesWithPoints)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
        }

        return view
    }
}
