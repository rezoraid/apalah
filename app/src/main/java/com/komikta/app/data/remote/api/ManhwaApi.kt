package com.komikta.app.data.remote.api

import com.komikta.app.data.remote.dto.ApiResponse
import com.komikta.app.data.remote.dto.ChapterContentData
import com.komikta.app.data.remote.dto.ComicDetailData
import com.komikta.app.data.remote.dto.GenreDto
import com.komikta.app.data.remote.dto.HomeData
import com.komikta.app.data.remote.dto.ListData
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ManhwaApi {

    @GET("api/home")
    suspend fun getHome(
        @Query("source") source: String = "all",
        @Query("refresh") refresh: Boolean = false
    ): ApiResponse<HomeData>

    @GET("api/list")
    suspend fun getList(
        @Query("source") source: String = "all",
        @Query("page") page: Int = 1
    ): ApiResponse<ListData>

    @GET("api/search")
    suspend fun search(
        @Query("q") query: String,
        @Query("source") source: String = "all",
        @Query("page") page: Int = 1
    ): ApiResponse<ListData>

    @GET("api/genres")
    suspend fun getGenres(): ApiResponse<List<GenreDto>>

    @GET("api/genre/{genre}")
    suspend fun getByGenre(
        @Path("genre") genre: String,
        @Query("source") source: String = "all",
        @Query("page") page: Int = 1
    ): ApiResponse<ListData>

    @GET("api/type/{type}")
    suspend fun getByType(
        @Path("type") type: String,
        @Query("source") source: String = "all",
        @Query("page") page: Int = 1
    ): ApiResponse<ListData>

    @GET("api/comic/{source}/{id}")
    suspend fun getComicDetail(
        @Path("source") source: String,
        @Path("id") id: String
    ): ApiResponse<ComicDetailData>

    @GET("api/comic/{source}/{id}/chapter/{chapter}")
    suspend fun getChapter(
        @Path("source") source: String,
        @Path("id") id: String,
        @Path("chapter") chapter: String
    ): ApiResponse<ChapterContentData>
}
