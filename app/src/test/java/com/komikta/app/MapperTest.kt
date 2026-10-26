package com.komikta.app

import com.komikta.app.data.mapper.toDomain
import com.komikta.app.data.remote.dto.ComicDto
import com.komikta.app.data.remote.dto.RatingDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MapperTest {

    @Test
    fun comicDto_maps_to_domain() {
        val dto = ComicDto(
            id = "luvyaa:test",
            source = "luvyaa",
            sourceId = "test",
            title = "Test Title",
            cover = "https://example.com/cover.jpg",
            latestChapter = "10",
            rating = RatingDto(value = 4.5, count = 100)
        )
        val domain = dto.toDomain()
        assertEquals("luvyaa:test", domain.id)
        assertEquals("Test Title", domain.title)
        assertEquals("https://example.com/cover.jpg", domain.coverUrl)
        assertEquals("10", domain.latestChapter)
        assertEquals(4.5, domain.rating)
    }

    @Test
    fun comicDto_null_rating() {
        val dto = ComicDto(
            id = "x:y",
            source = "x",
            sourceId = "y",
            title = "T"
        )
        val domain = dto.toDomain()
        assertNull(domain.rating)
        assertNull(domain.coverUrl)
    }
}
