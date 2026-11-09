package com.movievortex.android.core

/**
 * Centralized Server Manager for MovieVortex
 * High-performance 4-server streaming architecture:
 * - Mark 1 -> AniVortex (Default High-Speed Stream)
 * - Mark 2 -> IStreamFlare (Western Movies & Shows)
 * - Mark 3 -> MovieBoxProvider (Blockbuster Movies)
 * - Mark 4 -> SuperStream (Ultra-Fast 4K/1080p Vault)
 */
object ServerManager {
    const val MARK_1 = "Mark 1"
    const val MARK_2 = "Mark 2"
    const val MARK_3 = "Mark 3"
    const val MARK_4 = "Mark 4"

    val ALL_SERVERS = listOf(MARK_1, MARK_2, MARK_3, MARK_4)

    const val DEFAULT_SERVER = MARK_1

    // Underlying backend provider internal names
    const val PROVIDER_MARK_1 = "AniVortex"
    const val PROVIDER_MARK_2 = "IStreamFlare"
    const val PROVIDER_MARK_3 = "MovieBoxProvider"
    const val PROVIDER_MARK_4 = "SuperStream"

    fun toDisplayName(rawName: String?): String {
        if (rawName.isNullOrBlank()) return MARK_1
        val lower = rawName.lowercase()
        return when {
            lower.contains("anivortex") || lower.contains("anvortex") || lower.contains("mark 1") || lower.contains("mark-1") || lower.contains("mark1") -> MARK_1
            lower.contains("streamflare") || lower.contains("istreamflare") || lower.contains("mark 2") || lower.contains("mark-2") || lower.contains("mark2") -> MARK_2
            lower.contains("moviebox") || lower.contains("rive") || lower.contains("mark 3") || lower.contains("mark-3") || lower.contains("mark3") -> MARK_3
            lower.contains("superstream") || lower.contains("spiral") || lower.contains("mark 4") || lower.contains("mark-4") || lower.contains("mark4") -> MARK_4
            else -> MARK_1
        }
    }

    fun toProviderName(markName: String): String {
        return when (markName) {
            MARK_1 -> PROVIDER_MARK_1
            MARK_2 -> PROVIDER_MARK_2
            MARK_3 -> PROVIDER_MARK_3
            MARK_4 -> PROVIDER_MARK_4
            else -> PROVIDER_MARK_1
        }
    }

    fun toDescription(markName: String): String {
        return when (markName) {
            MARK_1 -> "Direct High-Speed Core (Mark 1)"
            MARK_2 -> "Western Movies & Shows (Mark 2)"
            MARK_3 -> "Blockbuster Multi-Quality (Mark 3)"
            MARK_4 -> "Ultra-Fast 4K/1080p Vault (Mark 4)"
            else -> "High-Speed Streaming Server"
        }
    }
}
