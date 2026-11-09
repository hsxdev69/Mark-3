package com.movievortex.android.core

import com.lagradost.cloudstream3.AnimeLoadResponse
import com.lagradost.cloudstream3.AnimeSearchResponse
import com.lagradost.cloudstream3.Episode
import com.lagradost.cloudstream3.LiveSearchResponse
import com.lagradost.cloudstream3.LiveStreamLoadResponse
import com.lagradost.cloudstream3.LoadResponse
import com.lagradost.cloudstream3.MovieLoadResponse
import com.lagradost.cloudstream3.MovieSearchResponse
import com.lagradost.cloudstream3.SearchResponse
import com.lagradost.cloudstream3.SubtitleFile
import com.lagradost.cloudstream3.TorrentLoadResponse
import com.lagradost.cloudstream3.TorrentSearchResponse
import com.lagradost.cloudstream3.TvSeriesLoadResponse
import com.lagradost.cloudstream3.TvSeriesSearchResponse
import com.lagradost.cloudstream3.TvType
import com.lagradost.cloudstream3.syncproviders.SyncAPI
import com.lagradost.cloudstream3.utils.DataStoreHelper
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.movievortex.android.model.MediaCast
import com.movievortex.android.model.MediaDownloadState
import com.movievortex.android.model.MediaEpisode
import com.movievortex.android.model.MediaSeason
import com.movievortex.android.model.MediaType
import com.movievortex.android.model.MovieVortexMedia
import com.movievortex.android.model.StreamSource
import com.movievortex.android.model.SubtitleTrack

object CoreAdapter {

    fun toMediaType(tvType: TvType?): MediaType {
        return when (tvType) {
            TvType.Movie -> MediaType.Movie
            TvType.TvSeries -> MediaType.TvSeries
            TvType.Anime, TvType.AnimeMovie, TvType.OVA -> MediaType.Anime
            TvType.Live -> MediaType.LiveStream
            TvType.Torrent -> MediaType.Torrent
            else -> MediaType.Movie
        }
    }

    /**
     * Converts a CloudStream SearchResponse into a normalized MovieVortexMedia object.
     */
    fun fromSearchResponse(response: SearchResponse): MovieVortexMedia {
        val mediaType = toMediaType(response.type)
        val id = "${response.apiName}_${response.url}"

        // Retrieve watch progress from DataStoreHelper if available
        val resume = DataStoreHelper.getViewPos(response.id ?: response.url.hashCode())
        val progress = if (resume != null && resume.duration > 0) {
            (resume.position.toFloat() / resume.duration.toFloat()).coerceIn(0f, 1f)
        } else 0f

        val isBookmarked = DataStoreHelper.getBookmarkedData(response.id ?: response.url.hashCode()) != null

        val year = (response as? MovieSearchResponse)?.year
            ?: (response as? AnimeSearchResponse)?.year
            ?: (response as? TvSeriesSearchResponse)?.year

        val rating = response.score?.toFloat(10) ?: 0f

        return MovieVortexMedia(
            id = id,
            title = response.name,
            poster = response.posterUrl,
            backdrop = response.posterUrl,
            type = mediaType,
            year = year,
            rating = rating,
            synopsis = null,
            watchProgress = progress,
            isInMyList = isBookmarked,
            rawUrl = response.url,
            providerApiName = response.apiName
        )
    }

    /**
     * Converts a CloudStream LoadResponse into a detailed MovieVortexMedia object.
     */
    fun fromLoadResponse(response: LoadResponse, providerApi: String): MovieVortexMedia {
        val mediaType = toMediaType(response.type)
        val id = "${providerApi}_${response.url}"

        val seasonsMap = mutableMapOf<Int, MutableList<MediaEpisode>>()

        var episodesList = emptyList<MediaEpisode>()

        when (response) {
            is TvSeriesLoadResponse -> {
                episodesList = response.episodes.map { ep ->
                    fromEpisode(ep, response.url)
                }
            }
            is AnimeLoadResponse -> {
                episodesList = response.episodes.values.flatten().map { ep ->
                    fromEpisode(ep, response.url)
                }
            }
            else -> {}
        }

        episodesList.forEach { ep ->
            seasonsMap.getOrPut(ep.seasonNumber) { mutableListOf() }.add(ep)
        }

        val seasons = seasonsMap.keys.sorted().map { sNum ->
            MediaSeason(
                seasonNumber = sNum,
                name = if (sNum == 0) "Specials" else "Season $sNum",
                episodeCount = seasonsMap[sNum]?.size ?: 0
            )
        }

        val resume = DataStoreHelper.getViewPos(response.url.hashCode())
        val progress = if (resume != null && resume.duration > 0) {
            (resume.position.toFloat() / resume.duration.toFloat()).coerceIn(0f, 1f)
        } else 0f

        val isBookmarked = DataStoreHelper.getBookmarkedData(response.url.hashCode()) != null

        val cast = response.actors?.map { actor ->
            MediaCast(
                name = actor.actor.name,
                role = actor.roleString,
                avatarUrl = actor.actor.image
            )
        } ?: emptyList()

        val recs = response.recommendations?.mapNotNull { recItem ->
            try {
                fromSearchResponse(recItem)
            } catch (_: Exception) {
                null
            }
        } ?: emptyList()

        return MovieVortexMedia(
            id = id,
            title = response.name,
            originalTitle = null,
            poster = response.posterUrl,
            backdrop = response.backgroundPosterUrl ?: response.posterUrl,
            type = mediaType,
            year = response.year,
            rating = response.score?.toFloat(10) ?: 0f,
            runtime = response.duration,
            synopsis = response.plot,
            genres = response.tags ?: emptyList(),
            cast = cast,
            production = null,
            trailerUrl = response.trailers.firstOrNull()?.extractorUrl,
            seasons = seasons,
            episodes = episodesList,
            recommendations = recs,
            watchProgress = progress,
            isInMyList = isBookmarked,
            rawUrl = response.url,
            providerApiName = providerApi
        )
    }

    fun fromEpisode(ep: Episode, parentUrl: String): MediaEpisode {
        val sNum = ep.season ?: 1
        val epNum = ep.episode ?: 1
        val epResume = DataStoreHelper.getViewPos(ep.data.hashCode())
        val epProgress = if (epResume != null && epResume.duration > 0) {
            (epResume.position.toFloat() / epResume.duration.toFloat()).coerceIn(0f, 1f)
        } else 0f

        return MediaEpisode(
            id = ep.data,
            title = ep.name ?: "Episode $epNum",
            episodeNumber = epNum,
            seasonNumber = sNum,
            poster = ep.posterUrl,
            synopsis = ep.description,
            runtime = null,
            releaseDate = null,
            dataUrl = ep.data,
            watchProgress = epProgress,
            isWatched = epProgress >= 0.9f
        )
    }

    fun fromExtractorLink(link: ExtractorLink): StreamSource {
        val qualityName = when (link.quality) {
            0 -> "4K UHD"
            1080 -> "1080p FHD"
            720 -> "720p HD"
            480 -> "480p"
            360 -> "360p"
            else -> if (link.quality > 0) "${link.quality}p" else "Auto"
        }

        return StreamSource(
            url = link.url,
            quality = qualityName,
            headers = link.headers,
            isM3u8 = link.isM3u8,
            isDASH = link.isDash,
            extractorName = link.name
        )
    }

    fun fromSubtitleFile(sub: SubtitleFile): SubtitleTrack {
        return SubtitleTrack(
            language = sub.lang,
            url = sub.url,
            format = if (sub.url.endsWith(".srt", ignoreCase = true)) "srt" else "vtt"
        )
    }
}
