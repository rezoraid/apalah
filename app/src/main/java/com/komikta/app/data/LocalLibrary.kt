package com.komikta.app.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object LocalLibrary {
    private const val PREFS = "komikta_library"
    private const val BOOKMARKS = "bookmarks"
    private val gson = Gson()

    fun getBookmarks(context: Context): List<Comic> {
        val json = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(BOOKMARKS, "[]") ?: "[]"
        return runCatching {
            gson.fromJson<List<Comic>>(json, object : TypeToken<List<Comic>>() {}.type) ?: emptyList()
        }.getOrDefault(emptyList())
    }

    fun isBookmarked(context: Context, comicId: String): Boolean =
        getBookmarks(context).any { it.id == comicId }

    fun toggleBookmark(context: Context, comic: Comic): Boolean {
        val current = getBookmarks(context).toMutableList()
        val exists = current.any { it.id == comic.id }
        if (exists) current.removeAll { it.id == comic.id } else current.add(0, comic)
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(BOOKMARKS, gson.toJson(current)).apply()
        return !exists
    }
}
