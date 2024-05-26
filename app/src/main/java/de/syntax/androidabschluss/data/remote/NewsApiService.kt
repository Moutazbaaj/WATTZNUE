package de.syntax.androidabschluss.data.remote


import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import de.syntax.androidabschluss.data.datamodels.newsmodels.NewsResponse
import de.syntax.androidabschluss.data.datamodels.newsmodels.SourcesResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


// Base URL for the API
const val BASE_URL_News = "https://newsapi.org/v2/"

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
private val retrofitNews = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL_News)
    .client(httpClient)
    .build()


// Retrofit API service interface
interface NewsApiService {

    @GET("everything")
    suspend fun getNews(
        @Query("q") query: String,
        @Query("apiKey") apiKey: String,
        @Query("sortBy") sortBy: String = "relevancy,popularity,publishedAt",
        @Query("language") language: String = "en",
        @Query("pageSize") pageSize: Int = 100,
        @Query("page") page: Int = 1
    ): NewsResponse


    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String,
        @Query("apiKey") apiKey: String,
        @Query("pageSize") pageSize: Int = 20,
    ): NewsResponse


    @GET("top-headlines/sources")
    suspend fun getNewsCategories(
        @Query("category") category: String,
        @Query("apiKey") apiKey: String,
        @Query("pageSize") pageSize: Int = 100,
        @Query("page") page: Int = 1
    ): SourcesResponse


}


// Singleton object for accessing Retrofit API service
object NewsApi {
    val retrofitService: NewsApiService by lazy { retrofitNews.create(NewsApiService::class.java) }
}