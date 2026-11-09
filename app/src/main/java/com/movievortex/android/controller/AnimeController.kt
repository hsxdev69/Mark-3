package com.movievortex.android.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lagradost.cloudstream3.APIHolder
import com.lagradost.cloudstream3.MainActivity
import com.lagradost.cloudstream3.TvType
import com.lagradost.cloudstream3.mvvm.Resource
import com.lagradost.cloudstream3.ui.APIRepository
import com.movievortex.android.core.CoreAdapter
import com.movievortex.android.model.MovieVortexMedia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class AnimeUiState(
    val isLoading: Boolean = true,
    val heroItems: List<MovieVortexMedia> = emptyList(),
    val rows: List<ContentRow> = emptyList(),
    val exploreGrid: List<MovieVortexMedia> = emptyList(),
    val isExploreLoading: Boolean = false,
    val errorMessage: String? = null
)

class AnimeController : ViewModel() {

    private val _uiState = MutableStateFlow(AnimeUiState())
    val uiState: StateFlow<AnimeUiState> = _uiState.asStateFlow()

    private var animePage = 1

    init {
        loadAnimeContent()
        MainActivity.mainPluginsLoadedEvent += ::onPluginsLoaded
        MainActivity.afterPluginsLoadedEvent += ::onPluginsLoaded
    }

    private fun onPluginsLoaded(success: Boolean) {
        loadAnimeContent()
    }

    fun loadAnimeContent() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                // Find providers supporting Anime
                val animeApis = APIHolder.apis.filter {
                    it.supportedTypes.contains(TvType.Anime) ||
                    it.supportedTypes.contains(TvType.AnimeMovie) ||
                    it.supportedTypes.contains(TvType.OVA)
                }

                val primaryApi = animeApis.firstOrNull { it.hasMainPage }
                    ?: animeApis.firstOrNull()
                    ?: APIHolder.apis.firstOrNull { it.hasMainPage }

                val rows = mutableListOf<ContentRow>()
                val heroList = mutableListOf<MovieVortexMedia>()
                val exploreList = mutableListOf<MovieVortexMedia>()

                if (primaryApi != null) {
                    val repo = APIRepository(primaryApi)
                    val result = withContext(Dispatchers.IO) {
                        repo.getMainPage(1)
                    }

                    if (result is Resource.Success) {
                        result.value.forEach { hp ->
                            hp?.items?.forEach { pageList ->
                                val items = pageList.list.map { CoreAdapter.fromSearchResponse(it) }
                                if (items.isNotEmpty()) {
                                    rows.add(ContentRow(title = pageList.name, items = items))
                                }
                            }
                        }
                    }
                }

                rows.firstOrNull()?.items?.take(5)?.let { heroList.addAll(it) }
                rows.getOrNull(1)?.items?.let { exploreList.addAll(it) }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    heroItems = heroList,
                    rows = rows,
                    exploreGrid = exploreList
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Unable to load anime catalog right now."
                )
            }
        }
    }

    fun loadMoreExplore() {
        if (_uiState.value.isExploreLoading) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExploreLoading = true)
            animePage++

            try {
                val animeApis = APIHolder.apis.filter {
                    it.supportedTypes.contains(TvType.Anime)
                }
                val primaryApi = animeApis.firstOrNull { it.hasMainPage } ?: animeApis.firstOrNull()
                if (primaryApi != null) {
                    val repo = APIRepository(primaryApi)
                    val result = withContext(Dispatchers.IO) {
                        repo.getMainPage(animePage)
                    }

                    if (result is Resource.Success) {
                        val newItems = result.value.flatMap { hp ->
                            hp?.items?.flatMap { it.list } ?: emptyList()
                        }.map { CoreAdapter.fromSearchResponse(it) }

                        _uiState.value = _uiState.value.copy(
                            exploreGrid = _uiState.value.exploreGrid + newItems,
                            isExploreLoading = false
                        )
                        return@launch
                    }
                }
            } catch (_: Exception) {}

            _uiState.value = _uiState.value.copy(isExploreLoading = false)
        }
    }

    override fun onCleared() {
        super.onCleared()
        MainActivity.mainPluginsLoadedEvent -= ::onPluginsLoaded
        MainActivity.afterPluginsLoadedEvent -= ::onPluginsLoaded
    }
}
