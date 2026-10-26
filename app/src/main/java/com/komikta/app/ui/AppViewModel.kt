package com.komikta.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komikta.app.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeState(
    val loading: Boolean = true,
    val popular: List<Comic> = emptyList(),
    val latest: List<Comic> = emptyList(),
    val fresh: List<Comic> = emptyList(),
    val error: String? = null
)

class AppViewModel : ViewModel() {
    private val repository = KomiktaRepository()
    private val _home = MutableStateFlow(HomeState())
    val home: StateFlow<HomeState> = _home.asStateFlow()
    private val _catalog = MutableStateFlow<List<Comic>>(emptyList())
    val catalog: StateFlow<List<Comic>> = _catalog.asStateFlow()
    private val _busy = MutableStateFlow(false)
    val busy: StateFlow<Boolean> = _busy.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init { loadHome() }

    fun loadHome() {
        viewModelScope.launch {
            _home.value = _home.value.copy(loading = true, error = null)
            repository.home().fold(
                onSuccess = { data ->
                    _home.value = HomeState(false, data.popular, data.latest, data.new)
                },
                onFailure = { err ->
                    _home.value = _home.value.copy(loading = false, error = friendly(err))
                }
            )
        }
    }
    fun loadLatest() {
        viewModelScope.launch {
            _busy.value = true; _error.value = null
            repository.latest().fold({ _catalog.value = it.items }, { _error.value = friendly(it) })
            _busy.value = false
        }
    }
    fun search(query: String) {
        viewModelScope.launch {
            _busy.value = true; _error.value = null
            repository.search(query).fold({ _catalog.value = it.items }, { _error.value = friendly(it) })
            _busy.value = false
        }
    }
    fun loadType(type: String) {
        viewModelScope.launch {
            _busy.value = true; _error.value = null
            repository.byType(type).fold({ _catalog.value = it.items }, { _error.value = friendly(it) })
            _busy.value = false
        }
    }
    private fun friendly(t: Throwable): String =
        if (t is java.net.UnknownHostException) "Tidak dapat terhubung ke server. Periksa koneksi internet."
        else t.message?.takeIf { it.isNotBlank() } ?: "Terjadi kesalahan. Coba lagi."
}
