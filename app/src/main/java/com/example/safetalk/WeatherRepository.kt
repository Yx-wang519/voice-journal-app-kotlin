package com.example.safetalk

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Handles weather data fetching using OpenWeatherMap API.
 */
class WeatherRepository {

    private val apiKey = "7971bc05c3e26ba96dcd818a1987f0a9"

    private val api: WeatherApi by lazy {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(WeatherApi::class.java)
    }

    suspend fun getWeather(lat: Double, lon: Double): String {
        return try {
            val response = api.getWeather(lat, lon, apiKey, "metric")
            val temp = response.main.temp.toInt()
            val description = response.weather.firstOrNull()?.description ?: "Unknown"
            "$temp°C · $description"
        } catch (e: Exception) {
            "Weather unavailable"
        }
    }
}