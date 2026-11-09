package com.movievortex.android.core

import com.lagradost.cloudstream3.app
import com.movievortex.android.model.MediaCast
import com.movievortex.android.model.MediaType
import com.movievortex.android.model.MovieVortexMedia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URLEncoder

/**
 * Repository to enrich MovieVortex media with 100% real TMDB data:
 * - Real backdrop, poster, rating, release year, overview, genres
 * - Real cast members with character roles and actual profile images
 * - Real related movies & recommendations
 */
object TmdbRepository {
    private const val TMDB_API_KEY = "e6333b32409e02a4a6eba6fb7ff866bb"
    private const val BASE_URL = "https://api.themoviedb.org/3"

    suspend fun enrichMediaWithTmdb(media: MovieVortexMedia): MovieVortexMedia = withContext(Dispatchers.IO) {
        try {
            val cleanTitle = media.title
                .replace(Regex("\\[.*?\\]|\\(.*?\\)"), "")
                .replace(Regex("Season \\d+|Episode \\d+|Series|Movie", RegexOption.IGNORE_CASE), "")
                .trim()

            if (cleanTitle.isBlank()) return@withContext media

            val encodedTitle = URLEncoder.encode(cleanTitle, "UTF-8")
            val searchUrl = "$BASE_URL/search/multi?api_key=$TMDB_API_KEY&query=$encodedTitle&language=en-US"
            val searchRes = app.get(searchUrl, cacheTime = 60 * 24).text
            val searchJson = JSONObject(searchRes)
            val results = searchJson.optJSONArray("results") ?: return@withContext media
            if (results.length() == 0) return@withContext media

            var bestResult: JSONObject? = null
            for (i in 0 until results.length()) {
                val item = results.optJSONObject(i) ?: continue
                val mType = item.optString("media_type")
                if (mType == "movie" || mType == "tv") {
                    bestResult = item
                    break
                }
            }
            if (bestResult == null) return@withContext media

            val tmdbId = bestResult.optInt("id")
            val mediaTypeStr = bestResult.optString("media_type", "movie")
            if (tmdbId <= 0) return@withContext media

            // Fetch complete details with credits and recommendations in one call
            val detailUrl = "$BASE_URL/$mediaTypeStr/$tmdbId?api_key=$TMDB_API_KEY&append_to_response=credits,recommendations&language=en-US"
            val detailRes = app.get(detailUrl, cacheTime = 60 * 24).text
            val detailJson = JSONObject(detailRes)

            val overview = detailJson.optString("overview").takeIf { it.isNotBlank() } ?: media.synopsis
            val backdropPath = detailJson.optString("backdrop_path")
            val posterPath = detailJson.optString("poster_path")

            val fullBackdrop = if (backdropPath.isNotBlank() && backdropPath != "null") {
                "https://image.tmdb.org/t/p/w1280$backdropPath"
            } else media.backdrop

            val fullPoster = if (posterPath.isNotBlank() && posterPath != "null") {
                "https://image.tmdb.org/t/p/w780$posterPath"
            } else media.poster

            val voteAvg = detailJson.optDouble("vote_average", 0.0).toFloat().takeIf { it > 0 } ?: media.rating

            val releaseDateStr = detailJson.optString("release_date").ifBlank { detailJson.optString("first_air_date") }
            val year = releaseDateStr.take(4).toIntOrNull() ?: media.year

            val runtime = if (detailJson.has("runtime")) detailJson.optInt("runtime") else media.runtime

            // Real Genres
            val genresList = mutableListOf<String>()
            val genresArr = detailJson.optJSONArray("genres")
            if (genresArr != null) {
                for (i in 0 until genresArr.length()) {
                    val gObj = genresArr.optJSONObject(i)
                    val gName = gObj?.optString("name")
                    if (!gName.isNullOrBlank()) genresList.add(gName)
                }
            }
            val finalGenres = if (genresList.isNotEmpty()) genresList else media.genres

            // Real Cast with character roles & actual photos
            val castList = mutableListOf<MediaCast>()
            val creditsObj = detailJson.optJSONObject("credits")
            val castArr = creditsObj?.optJSONArray("cast")
            if (castArr != null) {
                for (i in 0 until castArr.length().coerceAtMost(30)) {
                    val cObj = castArr.optJSONObject(i) ?: continue
                    val name = cObj.optString("name")
                    val character = cObj.optString("character").takeIf { it.isNotBlank() } ?: "Cast"
                    val profilePath = cObj.optString("profile_path")
                    val avatarUrl = if (profilePath.isNotBlank() && profilePath != "null") {
                        "https://image.tmdb.org/t/p/w185$profilePath"
                    } else null

                    if (name.isNotBlank()) {
                        castList.add(MediaCast(name = name, role = character, avatarUrl = avatarUrl))
                    }
                }
            }
            val finalCast = if (castList.isNotEmpty()) castList else media.cast

            // Real Recommendations
            val recList = mutableListOf<MovieVortexMedia>()
            val recObj = detailJson.optJSONObject("recommendations")
            val recArr = recObj?.optJSONArray("results")
            if (recArr != null) {
                for (i in 0 until recArr.length().coerceAtMost(16)) {
                    val rObj = recArr.optJSONObject(i) ?: continue
                    val rTitle = rObj.optString("title").ifBlank { rObj.optString("name") }
                    val rPosterPath = rObj.optString("poster_path")
                    val rBackdropPath = rObj.optString("backdrop_path")
                    val rVote = rObj.optDouble("vote_average", 0.0).toFloat()
                    val rDate = rObj.optString("release_date").ifBlank { rObj.optString("first_air_date") }
                    val rYear = rDate.take(4).toIntOrNull()
                    val rType = if (rObj.optString("media_type") == "tv") MediaType.TvSeries else MediaType.Movie

                    if (rTitle.isNotBlank() && rPosterPath.isNotBlank() && rPosterPath != "null") {
                        recList.add(
                            MovieVortexMedia(
                                id = "tmdb_${rObj.optInt("id")}",
                                title = rTitle,
                                poster = "https://image.tmdb.org/t/p/w500$rPosterPath",
                                backdrop = if (rBackdropPath.isNotBlank() && rBackdropPath != "null") "https://image.tmdb.org/t/p/w780$rBackdropPath" else null,
                                type = rType,
                                year = rYear,
                                rating = rVote,
                                synopsis = rObj.optString("overview"),
                                rawUrl = media.rawUrl,
                                providerApiName = media.providerApiName
                            )
                        )
                    }
                }
            }
            val finalRecs = if (recList.isNotEmpty()) recList else media.recommendations

            media.copy(
                poster = fullPoster,
                backdrop = fullBackdrop,
                synopsis = overview,
                rating = voteAvg,
                year = year,
                runtime = runtime,
                genres = finalGenres,
                cast = finalCast,
                recommendations = finalRecs
            )
        } catch (_: Exception) {
            media
        }
    }
}
