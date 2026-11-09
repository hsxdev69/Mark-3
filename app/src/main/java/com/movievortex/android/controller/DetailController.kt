package com.movievortex.android.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lagradost.cloudstream3.APIHolder
import com.lagradost.cloudstream3.AnimeLoadResponse
import com.lagradost.cloudstream3.MovieLoadResponse
import com.lagradost.cloudstream3.SubtitleFile
import com.lagradost.cloudstream3.TvSeriesLoadResponse
import com.lagradost.cloudstream3.mvvm.Resource
import com.lagradost.cloudstream3.syncproviders.SyncAPI
import com.lagradost.cloudstream3.ui.APIRepository
import com.lagradost.cloudstream3.utils.DataStoreHelper
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.movievortex.android.core.CoreAdapter
import com.movievortex.android.core.ServerManager
import com.movievortex.android.core.TmdbRepository
import com.movievortex.android.model.MediaEpisode
import com.movievortex.android.model.MediaSeason
import com.movievortex.android.model.MovieVortexMedia
import com.movievortex.android.model.StreamSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

data class DetailUiState(
    val isLoading: Boolean = true,
    val media: MovieVortexMedia? = null,
    val selectedSeason: Int = 1,
    val currentSeasonEpisodes: List<MediaEpisode> = emptyList(),
    val isTrailerPlaying: Boolean = false,
    val isInMyList: Boolean = false,
    val errorMessage: String? = null,
    // Inline Player & 4-Server Streaming Architecture with Auto-Fallback
    val selectedServer: String = ServerManager.DEFAULT_SERVER,
    val isResolvingStream: Boolean = false,
    val activeStreamUrl: String? = null,
    val activeStreamSource: StreamSource? = null,
    val streamSources: List<StreamSource> = emptyList(),
    val fallbackStatusMessage: String? = null,
    val streamError: String? = null,
    val currentPlayingEpisode: MediaEpisode? = null
)

class DetailController : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private var activeDataUrl: String = ""
    private var activeApiName: String = ""

    fun loadMediaDetail(rawUrl: String, apiName: String, initialMedia: MovieVortexMedia? = null) {
        activeDataUrl = rawUrl
        activeApiName = apiName

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                media = initialMedia,
                errorMessage = null,
                streamError = null,
                fallbackStatusMessage = null
            )

            try {
                val api = APIHolder.getApiFromNameNull(apiName)
                    ?: APIHolder.apis.firstOrNull { rawUrl.startsWith(it.mainUrl) }
                    ?: APIHolder.apis.firstOrNull()

                if (api == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Content provider is currently unavailable."
                    )
                    return@launch
                }

                val repo = APIRepository(api)
                val response = withContext(Dispatchers.IO) {
                    repo.load(rawUrl)
                }

                when (response) {
                    is Resource.Success -> {
                        val fullMedia = CoreAdapter.fromLoadResponse(response.value, api.name)
                        val initialSeason = fullMedia.seasons.firstOrNull()?.seasonNumber ?: 1
                        val seasonEps = fullMedia.episodes.filter { it.seasonNumber == initialSeason }

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            media = fullMedia,
                            selectedSeason = initialSeason,
                            currentSeasonEpisodes = seasonEps,
                            isInMyList = fullMedia.isInMyList,
                            currentPlayingEpisode = seasonEps.firstOrNull()
                        )

                        // Immediately resolve inline video stream for default server (Mark 1)
                        loadStreamsForServer(ServerManager.DEFAULT_SERVER, seasonEps.firstOrNull())

                        // Enrich with actual TMDB backdrop, cast, overview, real recommendations
                        try {
                            val enriched = TmdbRepository.enrichMediaWithTmdb(fullMedia)
                            _uiState.value = _uiState.value.copy(
                                media = enriched,
                                isInMyList = enriched.isInMyList
                            )
                        } catch (_: Exception) {}
                    }
                    is Resource.Failure -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "This content is currently unavailable. Please try again."
                        )
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "This content is currently unavailable. Please try again."
                )
            }
        }
    }

    /**
     * Resolves streams for a specified Mark server (Mark 1, Mark 2, Mark 3, Mark 4).
     * If the server fails or returns 0 links, automatically falls back to the next server.
     */
    fun loadStreamsForServer(serverName: String, episode: MediaEpisode? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isResolvingStream = true,
                selectedServer = serverName,
                streamError = null
            )

            val providerName = ServerManager.toProviderName(serverName)
            val media = _uiState.value.media
            val targetEpisode = episode ?: _uiState.value.currentPlayingEpisode
            val dataUrl = targetEpisode?.dataUrl ?: media?.rawUrl ?: activeDataUrl

            val collectedSources = mutableListOf<StreamSource>()

            try {
                // Find target API provider or fallback to available APIs
                val api = APIHolder.getApiFromNameNull(providerName)
                    ?: APIHolder.apis.firstOrNull { it.name.contains(providerName, ignoreCase = true) }
                    ?: APIHolder.apis.firstOrNull()

                if (api != null && dataUrl.isNotBlank()) {
                    val repo = APIRepository(api)
                    var streamDataUrl = dataUrl

                    // If it is a page URL, resolve underlying stream data URL
                    if (!streamDataUrl.startsWith("{") && (streamDataUrl.startsWith("http://") || streamDataUrl.startsWith("https://"))) {
                        try {
                            val loadRes = withContext(Dispatchers.IO) { repo.load(streamDataUrl) }
                            if (loadRes is Resource.Success) {
                                when (val v = loadRes.value) {
                                    is MovieLoadResponse -> if (v.dataUrl.isNotBlank()) streamDataUrl = v.dataUrl
                                    is TvSeriesLoadResponse -> v.episodes.firstOrNull()?.data?.let { if (it.isNotBlank()) streamDataUrl = it }
                                    is AnimeLoadResponse -> v.episodes.values.flatten().firstOrNull()?.data?.let { if (it.isNotBlank()) streamDataUrl = it }
                                    else -> {}
                                }
                            }
                        } catch (_: Throwable) {}
                    }

                    withContext(Dispatchers.IO) {
                        try {
                            repo.loadLinks(
                                data = streamDataUrl,
                                isCasting = false,
                                subtitleCallback = {},
                                callback = { link: ExtractorLink ->
                                    collectedSources.add(CoreAdapter.fromExtractorLink(link))
                                }
                            )
                        } catch (_: Throwable) {}
                    }
                }
            } catch (_: Throwable) {}

            if (collectedSources.isNotEmpty()) {
                val best = collectedSources.first()
                _uiState.value = _uiState.value.copy(
                    isResolvingStream = false,
                    activeStreamUrl = best.url,
                    activeStreamSource = best,
                    streamSources = collectedSources,
                    currentPlayingEpisode = targetEpisode,
                    fallbackStatusMessage = null,
                    streamError = null
                )
            } else {
                // Auto-fallback mechanism: Attempt next server in line
                val allServers = ServerManager.ALL_SERVERS
                val currentIndex = allServers.indexOf(serverName)
                if (currentIndex >= 0 && currentIndex < allServers.size - 1) {
                    val nextServer = allServers[currentIndex + 1]
                    _uiState.value = _uiState.value.copy(
                        fallbackStatusMessage = "Server $serverName offline. Auto-fallback to $nextServer..."
                    )
                    loadStreamsForServer(nextServer, targetEpisode)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isResolvingStream = false,
                        streamError = "Streams unavailable across all servers. Tap any server to retry.",
                        fallbackStatusMessage = null
                    )
                }
            }
        }
    }

    fun switchServer(serverName: String) {
        if (_uiState.value.selectedServer != serverName || _uiState.value.activeStreamUrl == null) {
            loadStreamsForServer(serverName, _uiState.value.currentPlayingEpisode)
        }
    }

    fun playEpisode(episode: MediaEpisode) {
        _uiState.value = _uiState.value.copy(currentPlayingEpisode = episode)
        loadStreamsForServer(_uiState.value.selectedServer, episode)
    }

    fun selectSeason(seasonNumber: Int) {
        val currentMedia = _uiState.value.media ?: return
        val filtered = currentMedia.episodes.filter { it.seasonNumber == seasonNumber }
        _uiState.value = _uiState.value.copy(
            selectedSeason = seasonNumber,
            currentSeasonEpisodes = filtered
        )
    }

    fun toggleMyList() {
        val current = _uiState.value.media ?: return
        val newState = !_uiState.value.isInMyList

        // Persist bookmark via DataStoreHelper
        if (newState) {
            val bookmark = DataStoreHelper.BookmarkedData(
                bookmarkedTime = APIHolder.unixTimeMS,
                id = current.rawUrl.hashCode(),
                latestUpdatedTime = APIHolder.unixTimeMS,
                name = current.title,
                url = current.rawUrl,
                apiName = current.providerApiName,
                type = null,
                posterUrl = current.poster,
                year = current.year
            )
            DataStoreHelper.setBookmarkedData(current.rawUrl.hashCode(), bookmark)
        } else {
            DataStoreHelper.deleteBookmarkedData(current.rawUrl.hashCode())
        }

        _uiState.value = _uiState.value.copy(
            isInMyList = newState,
            media = current.copy(isInMyList = newState)
        )
    }

    fun toggleTrailer() {
        _uiState.value = _uiState.value.copy(
            isTrailerPlaying = !_uiState.value.isTrailerPlaying
        )
    }
}
