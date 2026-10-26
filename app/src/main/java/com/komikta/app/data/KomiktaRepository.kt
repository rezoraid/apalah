package com.komikta.app.data

import java.io.IOException

class KomiktaRepository(private val api: KomiktaApi = ApiClient.service) {
    suspend fun home(): Result<ComicListData> = safe {
        val response = api.home()
        val data = response.data ?: throw IOException(response.error?.message ?: "Respons beranda kosong")
        ComicListData(popular = data.popular, latest = data.latest, new = data.new, items = data.items)
    }
    suspend fun latest(page: Int = 1): Result<ComicListData> = safe {
        api.latest(page = page).data ?: throw IOException("Data update tidak tersedia")
    }
    suspend fun search(q: String, page: Int = 1): Result<ComicListData> = safe {
        api.search(q, page = page).data ?: throw IOException("Hasil pencarian tidak tersedia")
    }
    suspend fun byType(type: String, page: Int = 1): Result<ComicListData> = safe {
        api.byType(type, page = page).data ?: throw IOException("Katalog tidak tersedia")
    }
    suspend fun detail(comic: Comic): Result<DetailData> = safe {
        api.detail(comic.source, comic.sourceId.ifBlank { comic.id.substringAfter(":") }).data
            ?: throw IOException("Detail komik tidak tersedia")
    }
    suspend fun chapter(comic: Comic, chapter: String): Result<ReaderData> = safe {
        api.chapter(comic.source, comic.sourceId.ifBlank { comic.id.substringAfter(":") }, chapter).data
            ?: throw IOException("Isi chapter tidak tersedia")
    }
    private suspend fun <T> safe(block: suspend () -> T): Result<T> = try {
        Result.success(block())
    } catch (t: Throwable) {
        Result.failure(t)
    }
}
