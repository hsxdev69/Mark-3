package com.movievortex.android.model

import java.io.Serializable

enum class MediaType {
    Movie,
    TvSeries,
    Anime,
    LiveStream,
    Torrent,
    Unknown
}

enum class MediaDownloadState {
    None,
    Queued,
    Downloading,
    Paused,
    Completed,
    Failed,
    Cancelled
}

data class MediaCast(
    val name: String,
    val role: String? = null,
    val avatarUrl: String? = null
) : Serializable

data class MediaSeason(
    val seasonNumber: Int,
    val name: String = "Season $seasonNumber",
    val episodeCount: Int = 0
) : Serializable

data class MediaEpisode(
    val id: String,
    val title: String,
    val episodeNumber: Int,
    val seasonNumber: Int = 1,
    val poster: String? = null,
    val synopsis: String? = null,
    val runtime: Int? = null, // in minutes
    val releaseDate: String? = null,
    val dataUrl: String,
    val watchProgress: Float = 0f, // 0.0 to 1.0
    val isWatched: Boolean = false,
    val downloadState: MediaDownloadState = MediaDownloadState.None
) : Serializable

data class StreamSource(
    val url: String,
    val quality: String, // e.g. "4K", "1080p", "720p", "Auto"
    val headers: Map<String, String> = emptyMap(),
    val isM3u8: Boolean = false,
    val isDASH: Boolean = false,
    val isTorrent: Boolean = false,
    val extractorName: String = ""
) : Serializable

data class SubtitleTrack(
    val language: String,
    val url: String,
    val isDefault: Boolean = false,
    val format: String = "vtt" // srt, vtt
) : Serializable

data class MovieVortexMedia(
    val id: String,
    val title: String,
    val originalTitle: String? = null,
    val poster: String? = null,
    val backdrop: String? = null,
    val type: MediaType = MediaType.Movie,
    val year: Int? = null,
    val rating: Float? = null, // 0.0 to 10.0
    val runtime: Int? = null, // in minutes
    val synopsis: String? = null,
    val genres: List<String> = emptyList(),
    val cast: List<MediaCast> = emptyList(),
    val production: String? = null,
    val trailerUrl: String? = null,
    val seasons: List<MediaSeason> = emptyList(),
    val episodes: List<MediaEpisode> = emptyList(),
    val recommendations: List<MovieVortexMedia> = emptyList(),
    val availableQualities: List<String> = emptyList(),
    val subtitles: List<SubtitleTrack> = emptyList(),
    val downloadState: MediaDownloadState = MediaDownloadState.None,
    val watchProgress: Float = 0f, // 0.0 to 1.0
    val isInMyList: Boolean = false,
    val rawUrl: String = "",
    val providerApiName: String = ""
) : Serializable

data class StreamingPlatform(
    val id: String,
    val name: String,
    val logoResName: String,
    val primaryColor: Long, // ARGB
    val description: String
)
