package com.movievortex.android.controller

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.lagradost.cloudstream3.APIHolder
import com.lagradost.cloudstream3.AnimeLoadResponse
import com.lagradost.cloudstream3.MovieLoadResponse
import com.lagradost.cloudstream3.SubtitleFile
import com.lagradost.cloudstream3.TvSeriesLoadResponse
import com.lagradost.cloudstream3.mvvm.Resource
import com.lagradost.cloudstream3.ui.APIRepository
import com.lagradost.cloudstream3.utils.DataStoreHelper
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.movievortex.android.core.CoreAdapter
import com.movievortex.android.model.MediaEpisode
import com.movievortex.android.model.MovieVortexMedia
import com.movievortex.android.model.StreamSource
import com.movievortex.android.model.SubtitleTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class PlayerStatus {
    Idle,
    ResolvingStreams,
    Ready,
    Playing,
    Paused,
    Buffering,
    Error
}

data class PlayerUiState(
    val status: PlayerStatus = PlayerStatus.Idle,
    val mediaTitle: String = "",
    val mediaSubtitle: String = "", // e.g. "Season 1 • Episode 4"
    val streamSources: List<StreamSource> = emptyList(),
    val currentSource: StreamSource? = null,
    val subtitleTracks: List<SubtitleTrack> = emptyList(),
    val currentSubtitle: SubtitleTrack? = null,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val isControlsVisible: Boolean = true,
    val isOfflinePlayback: Boolean = false,
    val statusMessage: String = "Connecting to stream...",
    val errorMessage: String? = null
)

class PlayerController : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private var activeDataUrl: String = ""
    private var activeApiName: String = ""

    fun initializePlayback(
        media: MovieVortexMedia,
        episode: MediaEpisode? = null,
        offlineFilePath: String? = null
    ) {
        val title = media.title
        val subTitle = if (episode != null) {
            "Season ${episode.seasonNumber} • Episode ${episode.episodeNumber}: ${episode.title}"
        } else {
            media.year?.let { "$it" } ?: ""
        }

        activeDataUrl = episode?.dataUrl ?: media.rawUrl
        activeApiName = media.providerApiName

        // Check if playing an offline file
        if (offlineFilePath != null) {
            val offlineSource = StreamSource(
                url = offlineFilePath,
                quality = "Offline (Original)",
                isTorrent = false
            )
            _uiState.value = PlayerUiState(
                status = PlayerStatus.Ready,
                mediaTitle = title,
                mediaSubtitle = subTitle,
                streamSources = listOf(offlineSource),
                currentSource = offlineSource,
                isOfflinePlayback = true
            )
            return
        }

        // Check mobile data restriction and resume preferences
        val ctx = com.lagradost.cloudstream3.CommonActivity.activity?.applicationContext
        val prefs = ctx?.let { androidx.preference.PreferenceManager.getDefaultSharedPreferences(it) }
        val mobileAllowed = prefs?.getBoolean("pref_mobile_data_allowed", true) ?: true
        if (!mobileAllowed && offlineFilePath.isNullOrBlank() && ctx != null) {
            val cm = ctx.getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as? android.net.ConnectivityManager
            val isCellular = cm?.getNetworkCapabilities(cm.activeNetwork)?.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR) == true
            if (isCellular) {
                _uiState.value = PlayerUiState(
                    status = PlayerStatus.Error,
                    mediaTitle = title,
                    mediaSubtitle = subTitle,
                    errorMessage = "Mobile data streaming is disabled in Settings. Switch to Wi-Fi or enable Mobile Data in Settings."
                )
                return
            }
        }

        // Resume saved position only if Resume Playback is enabled in Settings
        val shouldResume = prefs?.getBoolean("pref_resume_playback", true) ?: true
        val lastPos = if (shouldResume) DataStoreHelper.getViewPos(activeDataUrl.hashCode()) else null
        val resumeMs = lastPos?.position ?: 0L

        _uiState.value = PlayerUiState(
            status = PlayerStatus.ResolvingStreams,
            mediaTitle = title,
            mediaSubtitle = subTitle,
            currentPositionMs = resumeMs,
            statusMessage = "Resolving high-quality streams..."
        )

        resolveStreamLinks()
    }

    fun retryPlayback() {
        if (activeDataUrl.isNotBlank()) {
            _uiState.value = _uiState.value.copy(
                status = PlayerStatus.ResolvingStreams,
                statusMessage = "Re-connecting to media stream...",
                errorMessage = null
            )
            resolveStreamLinks()
        }
    }

    private fun resolveStreamLinks() {
        viewModelScope.launch {
            try {
                val api = APIHolder.getApiFromNameNull(activeApiName)
                    ?: APIHolder.apis.firstOrNull { activeDataUrl.startsWith(it.mainUrl) }
                    ?: APIHolder.apis.firstOrNull()

                if (api == null) {
                    _uiState.value = _uiState.value.copy(
                        status = PlayerStatus.Error,
                        errorMessage = "This content is currently unavailable. Please try again."
                    )
                    return@launch
                }

                val repo = APIRepository(api)
                val collectedSources = mutableListOf<StreamSource>()
                val collectedSubs = mutableListOf<SubtitleTrack>()

                // Pre-load content page if activeDataUrl is a page URL rather than extractor data
                var streamDataUrl = activeDataUrl
                if (!streamDataUrl.startsWith("{") && (streamDataUrl.startsWith("http://") || streamDataUrl.startsWith("https://"))) {
                    try {
                        val loadRes = withContext(Dispatchers.IO) { repo.load(streamDataUrl) }
                        if (loadRes is Resource.Success) {
                            when (val v = loadRes.value) {
                                is MovieLoadResponse -> {
                                    if (v.dataUrl.isNotBlank()) streamDataUrl = v.dataUrl
                                }
                                is TvSeriesLoadResponse -> {
                                    v.episodes.firstOrNull()?.data?.let {
                                        if (it.isNotBlank()) streamDataUrl = it
                                    }
                                }
                                is AnimeLoadResponse -> {
                                    v.episodes.values.flatten().firstOrNull()?.data?.let {
                                        if (it.isNotBlank()) streamDataUrl = it
                                    }
                                }
                                else -> {}
                            }
                        }
                    } catch (t: Throwable) {
                        Log.w("PlayerController", "Pre-load check: ${t.message}")
                    }
                }

                withContext(Dispatchers.IO) {
                    try {
                        repo.loadLinks(
                            data = streamDataUrl,
                            isCasting = false,
                            subtitleCallback = { subFile: SubtitleFile ->
                                collectedSubs.add(CoreAdapter.fromSubtitleFile(subFile))
                            },
                            callback = { link: ExtractorLink ->
                                collectedSources.add(CoreAdapter.fromExtractorLink(link))
                            }
                        )
                    } catch (t: Throwable) {
                        Log.e("PlayerController", "loadLinks exception: ${t.message}")
                    }
                }

                if (collectedSources.isNotEmpty()) {
                    // Pick the highest resolution link by default
                    val selected = collectedSources.first()
                    _uiState.value = _uiState.value.copy(
                        status = PlayerStatus.Ready,
                        streamSources = collectedSources,
                        currentSource = selected,
                        subtitleTracks = collectedSubs,
                        currentSubtitle = collectedSubs.firstOrNull { it.isDefault } ?: collectedSubs.firstOrNull(),
                        statusMessage = ""
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        status = PlayerStatus.Error,
                        errorMessage = "This content is currently unavailable. Please try again."
                    )
                }
            } catch (t: Throwable) {
                _uiState.value = _uiState.value.copy(
                    status = PlayerStatus.Error,
                    errorMessage = "This content is currently unavailable. Please try again."
                )
            }
        }
    }

    fun selectSource(source: StreamSource) {
        _uiState.value = _uiState.value.copy(currentSource = source)
    }

    fun selectSubtitle(subtitle: SubtitleTrack?) {
        _uiState.value = _uiState.value.copy(currentSubtitle = subtitle)
    }

    fun updateProgress(posMs: Long, durMs: Long) {
        _uiState.value = _uiState.value.copy(
            currentPositionMs = posMs,
            durationMs = durMs
        )
        // Persist view position to DataStoreHelper
        if (activeDataUrl.isNotBlank() && durMs > 0) {
            DataStoreHelper.setViewPos(activeDataUrl.hashCode(), posMs, durMs)
        }
    }

    fun toggleControlsVisibility() {
        _uiState.value = _uiState.value.copy(
            isControlsVisible = !_uiState.value.isControlsVisible
        )
    }
}
