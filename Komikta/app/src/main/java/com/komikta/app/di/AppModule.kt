package com.komikta.app.di

import androidx.room.Room
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.komikta.app.BuildConfig
import com.komikta.app.data.local.database.AppDatabase
import com.komikta.app.data.remote.api.ManhwaApi
import com.komikta.app.data.repository.ManhwaRepositoryImpl
import com.komikta.app.domain.repository.ManhwaRepository
import com.komikta.app.ui.screens.detail.DetailViewModel
import com.komikta.app.ui.screens.explore.ExploreViewModel
import com.komikta.app.ui.screens.home.HomeViewModel
import com.komikta.app.ui.screens.library.LibraryViewModel
import com.komikta.app.ui.screens.reader.ReaderViewModel
import com.komikta.app.ui.screens.search.SearchViewModel
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

val appModule = module {

    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
        }
    }

    single {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()
    }

    single {
        val json: Json = get()
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(get())
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ManhwaApi::class.java)
    }

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "komikta.db"
        ).fallbackToDestructiveMigration().build()
    }

    single { get<AppDatabase>().bookmarkDao() }
    single { get<AppDatabase>().readingHistoryDao() }
    single { get<AppDatabase>().recentSearchDao() }

    single<ManhwaRepository> {
        ManhwaRepositoryImpl(get(), get(), get(), get())
    }

    viewModel { HomeViewModel(get()) }
    viewModel { ExploreViewModel(get()) }
    viewModel { SearchViewModel(get()) }
    viewModel { LibraryViewModel(get()) }
    viewModel { (source: String, sourceId: String) ->
        DetailViewModel(source, sourceId, get())
    }
    viewModel { (source: String, sourceId: String, chapter: String) ->
        ReaderViewModel(source, sourceId, chapter, get())
    }
}
