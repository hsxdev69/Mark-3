package com.movievortex.android.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lagradost.cloudstream3.syncproviders.SyncAPI
import com.lagradost.cloudstream3.utils.DataStoreHelper
import com.movievortex.android.core.CoreAdapter
import com.movievortex.android.model.MovieVortexMedia
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MyListUiState(
    val items: List<MovieVortexMedia> = emptyList(),
    val isLoading: Boolean = false
)

class LibraryController : ViewModel() {

    private val _uiState = MutableStateFlow(MyListUiState())
    val uiState: StateFlow<MyListUiState> = _uiState.asStateFlow()

    init {
        loadMyList()
    }

    fun loadMyList() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val bookmarks = DataStoreHelper.getAllBookmarkedData()
            val mediaItems = bookmarks.map { bookmark ->
                MovieVortexMedia(
                    id = bookmark.url,
                    title = bookmark.name,
                    poster = bookmark.posterUrl,
                    backdrop = bookmark.posterUrl,
                    isInMyList = true,
                    rawUrl = bookmark.url,
                    providerApiName = bookmark.apiName
                )
            }

            _uiState.value = MyListUiState(
                items = mediaItems,
                isLoading = false
            )
        }
    }

    fun removeItem(media: MovieVortexMedia) {
        DataStoreHelper.deleteBookmarkedData(media.rawUrl.hashCode())
        _uiState.value = _uiState.value.copy(
            items = _uiState.value.items.filter { it.id != media.id }
        )
    }
}
