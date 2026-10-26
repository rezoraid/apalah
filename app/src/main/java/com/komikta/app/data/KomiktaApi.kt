package com.komikta.app.data

import com.komikta.app.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface KomiktaApi {
    @GET("health") suspend fun health(): retrofit2.Response<okhttp3.ResponseBody>
    @GET("api/home") suspend fun home(@Query("source") source: String = "all"): ApiEnvelope<ComicListData>
    @GET("api/list") suspend fun latest(@Query("source") source: String = "all", @Query("page") page: Int = 1): ApiEnvelope<ComicListData>
    @GET("api/search") suspend fun search(@Query("q") query: String, @Query("source") source: String = "all", @Query("page") page: Int = 1): ApiEnvelope<ComicListData>
    @GET("api/genres") suspend fun genres(): ApiEnvelope<List<String>>
    @GET("api/genre/{genre}") suspend fun byGenre(@Path("genre") genre: String, @Query("source") source: String = "all", @Query("page") page: Int = 1): ApiEnvelope<ComicListData>
    @GET("api/type/{type}") suspend fun byType(@Path("type") type: String, @Query("source") source: String = "all", @Query("page") page: Int = 1): ApiEnvelope<ComicListData>
    @GET("api/comic/{source}/{id}") suspend fun detail(@Path("source") source: String, @Path("id", encoded = true) id: String): ApiEnvelope<DetailData>
    @GET("api/comic/{source}/{id}/chapters") suspend fun chapters(@Path("source") source: String, @Path("id", encoded = true) id: String): ApiEnvelope<Map<String, List<Chapter>>>
    @GET("api/comic/{source}/{id}/chapter/{chapter}") suspend fun chapter(@Path("source") source: String, @Path("id", encoded = true) id: String, @Path("chapter", encoded = true) chapter: String): ApiEnvelope<ReaderData>
}

object ApiClient {
    private val logging = HttpLoggingInterceptor().apply {
        level = if (com.komikta.app.BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
    }
    private val auth = Interceptor { chain ->
        val request = chain.request().newBuilder().apply {
            if (BuildConfig.API_KEY.isNotBlank()) header("x-api-key", BuildConfig.API_KEY.trim())
        }.build()
        chain.proceed(request)
    }
    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(35, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .addInterceptor(auth)
        .addInterceptor(logging)
        .build()

    val service: KomiktaApi = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(KomiktaApi::class.java)
}
