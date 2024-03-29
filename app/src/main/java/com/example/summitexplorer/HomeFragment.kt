package com.example.summitexplorer

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class HomeFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences

    interface WeatherService {
        @GET("weather")
        fun getWeather(
            @Query("q") city: String,
            @Query("appid") apiKey: String,
            @Query("units") units: String,
            @Query("lang") lang: String
        ): Call<WeatherResponse>
    }

    data class WeatherResponse(
        @SerializedName("main") val main: Main,
        @SerializedName("weather") val weather: List<Weather>
    )

    data class Main(
        @SerializedName("temp") val temp: Double,
        @SerializedName("temp_max") val tempMax: Double,
        @SerializedName("temp_min") val tempMin: Double
    )

    data class Weather(
        @SerializedName("description") val description: String
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "Anónimo")
        val helloText = view.findViewById<TextView>(R.id.helloText)
        helloText.text = "Bienvenido de nuevo $userName"
        fetchTemperature(view)
        return view
    }

    private fun fetchTemperature(view: View) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(WeatherService::class.java)

        val apiKey = ApiKeys.OPENWEATHERMAP_API_KEY
        val city = "Madrid"
        val units = "metric"
        val lang = "es"

        val call = service.getWeather(city, apiKey, units, lang)

        call.enqueue(object : retrofit2.Callback<WeatherResponse> {
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: retrofit2.Response<WeatherResponse>
            ) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    weatherResponse?.let {
                        val temperature = weatherResponse.main.temp
                        val maxTemperature = weatherResponse.main.tempMax
                        val minTemperature = weatherResponse.main.tempMin
                        val description =
                            weatherResponse.weather.firstOrNull()?.description ?: "N/A"

                        val temperatureText = view.findViewById<TextView>(R.id.temperatureText)
                        temperatureText.text = "Temperatura en Madrid: $temperature °C\n" +
                                "Temperatura Máxima: $maxTemperature °C\n" +
                                "Temperatura Mínima: $minTemperature °C\n" +
                                "Descripción: $description"
                    }
                } else {
                    Log.e("miError", "Unsuccessful response: ${response.code()}")
                    // Handle error condition
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e("miError", "Error fetching weather", t)
                t.printStackTrace()
            }
        })
    }
}
