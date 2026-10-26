package com.komikta.app.data.mapper

import com.komikta.app.data.remote.dto.ChapterContentDto
import com.komikta.app.data.remote.dto.ChapterDto
import com.komikta.app.data.remote.dto.ComicDto
import com.komikta.app.data.remote.dto.GenreDto
import com.komikta.app.domain.model.Chapter
import com.komikta.app.domain.model.ChapterContent
import com.komikta.app.domain.model.Comic
import com.komikta.app.domain.model.Genre

fun ComicDto.toDomain(): Comic = Comic(
    id = id,
    source = source,
    sourceId = sourceId,
    title = title,
    alternativeTitles = alternativeTitles,
    type = type,
    status = status,
    genres = genres,
    author = author,
    artist = artist,
    coverUrl = cover ?: rawCover,
    synopsis = synopsis,
    latestChapter = latestChapter,
    rating = rating?.value,
    updatedAt = updatedAt
)

fun ChapterDto.toDomain(): Chapter = Chapter(
    id = id,
    comicId = comicId ?: "",
    source = source ?: "",
    sourceId = sourceId ?: "",
    number = chapter,
    title = title,
    releaseDate = releaseDate
)

fun ChapterContentDto.toDomain(comicId: String): ChapterContent = ChapterContent(
    chapterId = id,
    comicId = comicId,
    title = title,
    images = images.sortedBy { it.index }.map { it.image }
)

fun GenreDto.toDomain(): Genre = Genre(
    name = name,
    slug = slug,
    category = category,
    countHint = countHint
)
