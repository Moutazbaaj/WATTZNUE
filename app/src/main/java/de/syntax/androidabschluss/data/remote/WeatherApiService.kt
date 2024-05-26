package de.syntax.androidabschluss.data.remote

import WeatherResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


// Base URL for the API
const val BASE_URL_Weather = "https://api.openweathermap.org/data/2.5/"

// Logger instance for debugging API responses
private val logger: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}


private val httpClient: OkHttpClient = OkHttpClient.Builder()
    .addInterceptor(logger)
    .build()


// Moshi instance for JSON parsing
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()


// Retrofit instance for making network requests
private val retrofitWeather = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL_Weather)
    .client(httpClient)
    .build()


// Retrofit API service interface
interface WeatherApiService {
    // Endpoint for getting the current weather
    @GET("weather")
    fun getCurrentWeather(
        //@Query("q") city: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("appid") apiKey: String,

        ): Call<WeatherResponse>
}


// Singleton object for accessing Retrofit API service
object WeatherApi {
    val retrofitService: WeatherApiService by lazy { retrofitWeather.create(WeatherApiService::class.java) }
}


