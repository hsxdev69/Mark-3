package com.movievortex.android.core

import android.app.Activity
import android.util.Log
import com.lagradost.cloudstream3.APIHolder
import com.lagradost.cloudstream3.MainActivity
import com.lagradost.cloudstream3.plugins.PluginManager
import com.lagradost.cloudstream3.plugins.RepositoryManager
import com.lagradost.cloudstream3.ui.settings.extensions.RepositoryData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

/**
 * Automates the background setup of the Phisher repository and ensures that
 * the three essential streaming plugins (AniVortex, IStreamFlare, and MovieBoxProvider)
 * are installed and active on first launch and across sessions.
 */
object VortexPluginBootstrap {
    private const val TAG = "VortexPluginBootstrap"

    const val PHISHER_REPO_NAME = "Phisher Repo"
    const val PHISHER_REPO_URL =
        "https://raw.githubusercontent.com/phisher98/cloudstream-extensions-phisher/refs/heads/builds/repo.json"

    data class TargetPlugin(
        val name: String,
        val internalName: String,
        val fallbackUrl: String
    )

    val TARGET_PLUGINS = listOf(
        TargetPlugin(
            name = "AniVortex",
            internalName = "AniVortex",
            fallbackUrl = "https://raw.githubusercontent.com/phisher98/cloudstream-extensions-phisher/builds/AniVortex.cs3"
        ),
        TargetPlugin(
            name = "IStreamFlare",
            internalName = "IStreamFlare",
            fallbackUrl = "https://raw.githubusercontent.com/phisher98/cloudstream-extensions-phisher/builds/IStreamFlare.cs3"
        ),
        TargetPlugin(
            name = "MovieBoxProvider",
            internalName = "MovieBoxProvider",
            fallbackUrl = "https://raw.githubusercontent.com/phisher98/cloudstream-extensions-phisher/builds/MovieBoxProvider.cs3"
        ),
        TargetPlugin(
            name = "SuperStream",
            internalName = "SuperStream",
            fallbackUrl = "https://raw.githubusercontent.com/phisher98/cloudstream-extensions-phisher/builds/SuperStream.cs3"
        )
    )

    private val _isBootstrapping = MutableStateFlow(false)
    val isBootstrapping: StateFlow<Boolean> = _isBootstrapping.asStateFlow()

    private val _bootstrapComplete = MutableStateFlow(false)
    val bootstrapComplete: StateFlow<Boolean> = _bootstrapComplete.asStateFlow()

    suspend fun bootstrap(activity: Activity) = withContext(Dispatchers.IO) {
        if (_isBootstrapping.value) return@withContext
        _isBootstrapping.value = true

        try {
            Log.i(TAG, "Starting VortexPluginBootstrap with Phisher Repo & Bundled Assets...")

            // 1. Ensure Phisher Repo is added to repositories
            val repoData = RepositoryData(
                iconUrl = null,
                name = PHISHER_REPO_NAME,
                url = PHISHER_REPO_URL
            )
            RepositoryManager.addRepository(repoData)

            // 2. Load any existing plugins currently on disk
            try {
                PluginManager.___DO_NOT_CALL_FROM_A_PLUGIN_loadAllOnlinePlugins(activity)
                PluginManager.___DO_NOT_CALL_FROM_A_PLUGIN_loadAllLocalPlugins(activity, false)
            } catch (e: Exception) {
                Log.w(TAG, "Initial plugin load warning: ${e.message}")
            }

            // 3. Ensure each target plugin is loaded in memory
            for (target in TARGET_PLUGINS) {
                try {
                    val isAlreadyLoaded = APIHolder.allProviders.any { it.name.equals(target.internalName, ignoreCase = true) } ||
                            APIHolder.apis.any { it.name.equals(target.internalName, ignoreCase = true) }

                    if (!isAlreadyLoaded) {
                        val file = PluginManager.getPluginPath(activity, target.internalName, PHISHER_REPO_URL)
                        if (file.exists() && file.length() > 0) {
                            try {
                                val tempFile = java.io.File(activity.cacheDir, "${target.internalName}.reload.tmp.cs3")
                                file.copyTo(tempFile, overwrite = true)
                                val ok = PluginManager.installPluginFromFile(
                                    context = activity,
                                    sourceFile = tempFile,
                                    internalName = target.internalName,
                                    repositoryUrl = PHISHER_REPO_URL
                                )
                                tempFile.delete()
                                Log.i(TAG, "Loaded disk plugin ${target.name}: success=$ok")
                            } catch (e: Exception) {
                                Log.w(TAG, "Failed loading disk plugin ${target.name}: ${e.message}")
                            }
                        } else {
                            // Extract from bundled assets
                            try {
                                val assetPath = "plugins/${target.internalName}.cs3"
                                activity.assets.open(assetPath).use { input ->
                                    val tempFile = java.io.File(activity.cacheDir, "${target.internalName}.tmp.cs3")
                                    tempFile.outputStream().use { output -> input.copyTo(output) }
                                    val ok = PluginManager.installPluginFromFile(
                                        context = activity,
                                        sourceFile = tempFile,
                                        internalName = target.internalName,
                                        repositoryUrl = PHISHER_REPO_URL
                                    )
                                    tempFile.delete()
                                    Log.i(TAG, "Installed bundled plugin ${target.name}: success=$ok")
                                }
                            } catch (e: Exception) {
                                Log.d(TAG, "Bundled asset not found or failed for ${target.name}: ${e.message}")
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Plugin ${target.name} target check error: ${e.message}")
                }
            }

            // 4. Query online repository manifest to fetch missing plugins if still not loaded
            val repoPlugins = try {
                RepositoryManager.getRepoPlugins(repoData)
            } catch (e: Exception) {
                Log.w(TAG, "Could not fetch online repo metadata: ${e.message}")
                null
            }

            for (target in TARGET_PLUGINS) {
                try {
                    val isLoaded = APIHolder.allProviders.any { it.name.equals(target.internalName, ignoreCase = true) } ||
                            APIHolder.apis.any { it.name.equals(target.internalName, ignoreCase = true) }

                    if (!isLoaded) {
                        Log.i(TAG, "Auto-installing target plugin from online repo: ${target.name}...")
                        val matchingPlugin = repoPlugins?.firstOrNull {
                            it.plugin.internalName.equals(target.internalName, ignoreCase = true) ||
                            it.plugin.name.equals(target.name, ignoreCase = true)
                        }

                        val downloadUrl = matchingPlugin?.plugin?.url ?: target.fallbackUrl
                        val downloadHash = matchingPlugin?.plugin?.fileHash

                        val success = PluginManager.downloadPlugin(
                            activity = activity,
                            pluginUrl = downloadUrl,
                            pluginHash = downloadHash,
                            internalName = target.internalName,
                            repositoryUrl = PHISHER_REPO_URL,
                            loadPlugin = true
                        )

                        if (success) {
                            Log.i(TAG, "Successfully installed from web: ${target.name}")
                        } else {
                            // Retry with fallback URL without hash restriction
                            Log.w(TAG, "Direct fallback retry for: ${target.name}...")
                            PluginManager.downloadPlugin(
                                activity = activity,
                                pluginUrl = target.fallbackUrl,
                                pluginHash = null,
                                internalName = target.internalName,
                                repositoryUrl = PHISHER_REPO_URL,
                                loadPlugin = true
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Online fetch error for ${target.name}: ${e.message}")
                }
            }

            // Ensure APIHolder.apis has all providers registered
            try {
                APIHolder.allProviders.withLock {
                    if (APIHolder.allProviders.isNotEmpty()) {
                        APIHolder.apis = APIHolder.allProviders.distinctBy { it.name }
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "API sync warning: ${e.message}")
            }

            _bootstrapComplete.value = true
            val loadedNames = APIHolder.apis.map { it.name }
            Log.i(TAG, "VortexPluginBootstrap finished. Active providers: $loadedNames")

            // Broadcast plugin load events to trigger UI reload
            try {
                withContext(Dispatchers.Main) {
                    MainActivity.mainPluginsLoadedEvent.invoke(true)
                    MainActivity.afterPluginsLoadedEvent.invoke(true)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Plugin event broadcast warning: ${e.message}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in VortexPluginBootstrap", e)
        } finally {
            _isBootstrapping.value = false
        }
    }
}
