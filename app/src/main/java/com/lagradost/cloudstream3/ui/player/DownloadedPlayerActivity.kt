package com.lagradost.cloudstream3.ui.player

import android.content.Intent
import android.net.Uri
import java.io.File
import androidx.navigation.fragment.NavHostFragment
import com.lagradost.safefile.SafeFile
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavOptions
import com.lagradost.cloudstream3.ui.settings.Globals.EMULATOR
import com.lagradost.cloudstream3.ui.settings.Globals.TV
import com.lagradost.cloudstream3.ui.settings.Globals.isLayout
import com.lagradost.cloudstream3.APIHolder
import com.lagradost.cloudstream3.AnimeLoadResponse
import com.lagradost.cloudstream3.CommonActivity
import com.lagradost.cloudstream3.MainActivity
import com.lagradost.cloudstream3.MovieLoadResponse
import com.lagradost.cloudstream3.R
import com.lagradost.cloudstream3.TvSeriesLoadResponse
import com.lagradost.cloudstream3.TvType
import com.lagradost.cloudstream3.mvvm.Resource
import com.lagradost.cloudstream3.mvvm.logError
import com.lagradost.cloudstream3.mvvm.safe
import com.lagradost.cloudstream3.ui.APIRepository
import com.lagradost.cloudstream3.ui.player.OfflinePlaybackHelper.playLink
import com.lagradost.cloudstream3.ui.player.OfflinePlaybackHelper.playUri
import com.lagradost.cloudstream3.ui.result.ResultEpisode
import com.lagradost.cloudstream3.ui.result.VideoWatchState
import com.lagradost.cloudstream3.utils.BackPressedCallbackHelper.attachBackPressedCallback
import com.lagradost.cloudstream3.utils.Coroutines.ioSafe
import com.lagradost.cloudstream3.utils.Coroutines.main
import com.lagradost.cloudstream3.utils.UIHelper.enableEdgeToEdgeCompat
import com.lagradost.cloudstream3.utils.UIHelper.navigate
import com.lagradost.cloudstream3.utils.UIHelper.hideSystemUI
import com.lagradost.cloudstream3.utils.UIHelper.showSystemUI
import android.content.res.Configuration
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.movievortex.android.core.ServerManager
import com.lagradost.cloudstream3.Actor
import com.lagradost.cloudstream3.ActorData
import com.lagradost.cloudstream3.SearchResponse
import com.movievortex.android.model.MovieVortexMedia
import com.lagradost.cloudstream3.utils.downloader.DownloadQueueManager
import com.lagradost.cloudstream3.utils.downloader.DownloadObjects
import android.os.Environment
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.ExtractorLinkType
import com.lagradost.cloudstream3.utils.Qualities
import com.lagradost.cloudstream3.utils.SingleSelectionHelper
import com.lagradost.cloudstream3.utils.DataStore.setKey
import com.lagradost.cloudstream3.utils.DataStore.getFolderName
import com.lagradost.cloudstream3.utils.DOWNLOAD_HEADER_CACHE
import com.lagradost.cloudstream3.utils.DOWNLOAD_EPISODE_CACHE
import com.movievortex.android.controller.DownloadUiItem
import com.movievortex.android.model.MediaDownloadState
import com.movievortex.android.model.MediaType

class DownloadedPlayerActivity : AppCompatActivity() {
    companion object {
        const val TAG = "DownloadedPlayerActivity"
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean =
        CommonActivity.dispatchKeyEvent(this, event) ?: super.dispatchKeyEvent(event)

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean =
        CommonActivity.onKeyDown(this, keyCode, event) ?: super.onKeyDown(keyCode, event)

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        CommonActivity.onUserLeaveHint(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (isSameIntent(intent)) return
        setIntent(intent)
        Log.i(TAG, "onNewIntent")
        handleIntent(intent)
    }

    private fun isSameIntent(newIntent: Intent): Boolean {
        if (newIntent.getBooleanExtra("IS_NOTIFICATION_CLICK", false)) return true
        val old = intent ?: return false
        val oldContent = old.getStringExtra("CONTENT_URL") ?: old.getStringExtra("URL")
        val newContent = newIntent.getStringExtra("CONTENT_URL") ?: newIntent.getStringExtra("URL")
        if (oldContent != null && oldContent == newContent) return true

        val oldUri = old.data ?: old.clipData?.getItemAt(0)?.uri
        val newUri = newIntent.data ?: newIntent.clipData?.getItemAt(0)?.uri
        if (oldUri != null && oldUri == newUri) return true

        val oldText = safe { old.getStringExtra(Intent.EXTRA_TEXT) }
        val newText = safe { newIntent.getStringExtra(Intent.EXTRA_TEXT) }
        return oldText != null && oldText == newText
    }

    private var currentMarkServer: String = ServerManager.MARK_1
    private var currentMediaTitle: String? = null
    private var currentContentUrl: String? = null
    private var currentAllEpisodes: List<ResultEpisode> = emptyList()
    private var currentPlayingEpisodeIndex: Int = 0
    private var currentSelectedSeason: Int = 1
    private var currentLoadResponse: com.lagradost.cloudstream3.LoadResponse? = null
    private val triedServers: MutableSet<String> = mutableSetOf()
    private var isOfflineMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        val autoFullScreen = prefs.getBoolean("pref_auto_fullscreen", false)
        if (autoFullScreen) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        } else if (!isLayout(TV or EMULATOR)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        }
        CommonActivity.loadThemes(this)
        CommonActivity.init(this)
        enableEdgeToEdgeCompat()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val lp = window.attributes
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = lp
        }
        setContentView(R.layout.empty_layout)
        Log.i(TAG, "onCreate")

        setupWindowInsets()
        setupServerTabs()
        setupDownloadButton()
        updateLayoutForOrientation(resources.configuration.orientation)
        handleIntent(intent)

        attachBackPressedCallback("DownloadedPlayerActivity") {
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE && !isLayout(TV or EMULATOR)) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else {
                finish()
            }
        }
    }

    private fun setupServerTabs() {
        val mark1Tab = findViewById<TextView>(R.id.server_tab_mark_1)
        val mark2Tab = findViewById<TextView>(R.id.server_tab_mark_2)
        val mark3Tab = findViewById<TextView>(R.id.server_tab_mark_3)
        val mark4Tab = findViewById<TextView>(R.id.server_tab_mark_4)

        // Only keep AniVortex active, hide all other removed server tabs
        mark2Tab?.visibility = View.GONE
        mark3Tab?.visibility = View.GONE
        mark4Tab?.visibility = View.GONE

        mark1Tab?.text = "AniVortex"
        updateServerTabsUI(ServerManager.MARK_1)
    }

    private fun updateServerTabsUI(selectedMark: String) {
        currentMarkServer = ServerManager.MARK_1
        val mark1Tab = findViewById<TextView>(R.id.server_tab_mark_1)
        val mark2Tab = findViewById<TextView>(R.id.server_tab_mark_2)
        val mark3Tab = findViewById<TextView>(R.id.server_tab_mark_3)
        val mark4Tab = findViewById<TextView>(R.id.server_tab_mark_4)

        mark2Tab?.visibility = View.GONE
        mark3Tab?.visibility = View.GONE
        mark4Tab?.visibility = View.GONE

        if (mark1Tab != null) {
            val accentColor = com.movievortex.android.ui.theme.VortexColors.getPlayerAccentColorInt(this)
            val drawable = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                cornerRadius = 18f * resources.displayMetrics.density
                setColor(accentColor)
            }
            mark1Tab.background = drawable
            mark1Tab.text = "AniVortex"
            val r = Color.red(accentColor)
            val g = Color.green(accentColor)
            val b = Color.blue(accentColor)
            val isBright = (0.299 * r + 0.587 * g + 0.114 * b) / 255.0 > 0.6
            mark1Tab.setTextColor(if (isBright) Color.BLACK else Color.WHITE)
            mark1Tab.setTypeface(null, Typeface.BOLD)
        }
    }

    fun showUnavailableOverlay(message: String = "The Movie is not available this server plese switch another") {
        main {
            showContentHideSkeleton()
            findViewById<View>(R.id.player_unavailable_overlay)?.visibility = View.VISIBLE
            findViewById<TextView>(R.id.player_unavailable_message)?.text = message
        }
    }

    fun hideUnavailableOverlay() {
        main {
            findViewById<View>(R.id.player_unavailable_overlay)?.visibility = View.GONE
        }
    }

    private fun triggerAutoServerFallback(reason: String) {
        main {
            showUnavailableOverlay("Stream unavailable on AniVortex. Please check your network or try again.")
        }
    }

    private fun onServerSwitched(markName: String) {
        hideUnavailableOverlay()
        triedServers.add(markName)
        val providerName = ServerManager.toProviderName(markName)
        val title = currentMediaTitle ?: intent.getStringExtra("TITLE")
        if (title.isNullOrBlank()) {
            android.widget.Toast.makeText(this, "Switched to $markName", android.widget.Toast.LENGTH_SHORT).show()
            return
        }

        ioSafe {
            try {
                var api = APIHolder.getApiFromNameNull(providerName)
                    ?: APIHolder.apis.firstOrNull { it.name.equals(providerName, ignoreCase = true) }

                if (api == null) {
                    try {
                        com.movievortex.android.core.VortexPluginBootstrap.bootstrap(this@DownloadedPlayerActivity)
                    } catch (_: Exception) {}
                    api = APIHolder.getApiFromNameNull(providerName)
                        ?: APIHolder.apis.firstOrNull { it.name.equals(providerName, ignoreCase = true) }
                }

                if (api != null) {
                    val searchRes = api.search(title)
                    val match = searchRes?.firstOrNull()
                    if (match != null) {
                        main {
                            playProviderContent(providerName, match.url, intent)
                        }
                        return@ioSafe
                    }
                }

                // Fallback: try loading with the new provider and fallback url
                val fallbackUrl = currentContentUrl
                if (fallbackUrl != null && api != null) {
                    main {
                        playProviderContent(providerName, fallbackUrl, intent)
                    }
                    return@ioSafe
                }

                triggerAutoServerFallback("Stream unavailable on $markName.")
            } catch (e: Exception) {
                triggerAutoServerFallback("Error connecting to $markName.")
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateLayoutForOrientation(newConfig.orientation)
        setupWindowInsets()
    }

    private fun setupWindowInsets() {
        val root = findViewById<View>(R.id.player_root_layout) ?: return
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val statusBarInset = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navBarInset = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT && !isLayout(TV or EMULATOR)) {
                view.setPadding(0, statusBarInset.top, 0, navBarInset.bottom)
            } else {
                view.setPadding(0, 0, 0, 0)
            }
            insets
        }
    }

    private fun showSkeletonLoading() {
        val skeleton = findViewById<com.facebook.shimmer.ShimmerFrameLayout>(R.id.player_skeleton_loading)
        val details = findViewById<View>(R.id.player_details_scroll)
        if (resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE && !isLayout(TV or EMULATOR)) {
            skeleton?.visibility = View.VISIBLE
            skeleton?.startShimmer()
            details?.visibility = View.GONE
        }
    }

    private fun showContentHideSkeleton() {
        val skeleton = findViewById<com.facebook.shimmer.ShimmerFrameLayout>(R.id.player_skeleton_loading)
        val details = findViewById<View>(R.id.player_details_scroll)
        skeleton?.stopShimmer()
        skeleton?.visibility = View.GONE
        if (resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE && !isLayout(TV or EMULATOR)) {
            details?.visibility = View.VISIBLE
            details?.alpha = 0f
            details?.animate()?.alpha(1f)?.setDuration(220)?.start()
        }
    }

    private fun updateLayoutForOrientation(orientation: Int) {
        val root = findViewById<ConstraintLayout>(R.id.player_root_layout) ?: return
        val navHost = findViewById<View>(R.id.nav_host_fragment) ?: return
        val detailsScroll = findViewById<View>(R.id.player_details_scroll) ?: return
        val skeleton = findViewById<com.facebook.shimmer.ShimmerFrameLayout>(R.id.player_skeleton_loading)

        val set = ConstraintSet()
        set.clone(root)

        if (orientation == Configuration.ORIENTATION_LANDSCAPE || isLayout(TV or EMULATOR)) {
            // Fullscreen landscape
            detailsScroll.visibility = View.GONE
            skeleton?.visibility = View.GONE
            set.clear(R.id.nav_host_fragment, ConstraintSet.BOTTOM)
            set.connect(R.id.nav_host_fragment, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            set.connect(R.id.nav_host_fragment, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
            set.connect(R.id.nav_host_fragment, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            set.connect(R.id.nav_host_fragment, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
            set.setDimensionRatio(R.id.nav_host_fragment, null)
            set.constrainHeight(R.id.nav_host_fragment, 0)
            set.constrainWidth(R.id.nav_host_fragment, 0)
            hideSystemUI()
        } else {
            // Portrait inline: 16:9 at top, details & episodes below
            if (isOfflineMode) {
                skeleton?.stopShimmer()
                skeleton?.visibility = View.GONE
                detailsScroll.visibility = View.VISIBLE
            } else if (currentLoadResponse == null) {
                skeleton?.visibility = View.VISIBLE
                skeleton?.startShimmer()
                detailsScroll.visibility = View.GONE
            } else {
                skeleton?.stopShimmer()
                skeleton?.visibility = View.GONE
                detailsScroll.visibility = View.VISIBLE
            }

            set.clear(R.id.nav_host_fragment, ConstraintSet.BOTTOM)
            set.connect(R.id.nav_host_fragment, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            set.connect(R.id.nav_host_fragment, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            set.connect(R.id.nav_host_fragment, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
            set.setDimensionRatio(R.id.nav_host_fragment, "16:9")
            set.constrainHeight(R.id.nav_host_fragment, 0)
            set.constrainWidth(R.id.nav_host_fragment, 0)

            set.connect(R.id.player_skeleton_loading, ConstraintSet.TOP, R.id.nav_host_fragment, ConstraintSet.BOTTOM)
            set.connect(R.id.player_skeleton_loading, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
            set.connect(R.id.player_skeleton_loading, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            set.connect(R.id.player_skeleton_loading, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
            set.constrainHeight(R.id.player_skeleton_loading, 0)
            set.constrainWidth(R.id.player_skeleton_loading, 0)

            set.connect(R.id.player_details_scroll, ConstraintSet.TOP, R.id.nav_host_fragment, ConstraintSet.BOTTOM)
            set.connect(R.id.player_details_scroll, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
            set.connect(R.id.player_details_scroll, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            set.connect(R.id.player_details_scroll, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
            set.constrainHeight(R.id.player_details_scroll, 0)
            set.constrainWidth(R.id.player_details_scroll, 0)
            showSystemUI()
        }
        set.applyTo(root)
    }

    private fun handleIntent(intent: Intent) {
        val directUrl = intent.getStringExtra("DIRECT_URL") ?: intent.getStringExtra("LIVE_URL")
        val directTitle = intent.getStringExtra("TITLE")
        currentMediaTitle = directTitle
        if (!directUrl.isNullOrBlank()) {
            playDirectStream(directUrl, directTitle)
            return
        }

        val apiName = intent.getStringExtra(MainActivity.API_NAME_EXTRA_KEY)
        val contentUrl = intent.getStringExtra("CONTENT_URL") ?: intent.getStringExtra("URL")
        currentContentUrl = contentUrl
        if (apiName != null && contentUrl != null) {
            currentMarkServer = ServerManager.toDisplayName(apiName)
            triedServers.clear()
            triedServers.add(currentMarkServer)
            updateServerTabsUI(currentMarkServer)
            playProviderContent(apiName, contentUrl, intent)
            return
        }

        val offlinePath = intent.getStringExtra("OFFLINE_PATH")
        val offlineTitle = intent.getStringExtra("TITLE")
        if (!offlinePath.isNullOrBlank()) {
            playOfflineDirect(offlinePath, offlineTitle)
            return
        }

        val data = intent.data
        if (OfflinePlaybackHelper.playIntent(activity = this, intent = intent)) {
            return
        }

        if (
            intent.action == Intent.ACTION_SEND ||
            intent.action == Intent.ACTION_OPEN_DOCUMENT ||
            intent.action == Intent.ACTION_VIEW
        ) {
            val extraText = safe { intent.getStringExtra(Intent.EXTRA_TEXT) }
            val cd = intent.clipData
            val item = if (cd != null && cd.itemCount > 0) cd.getItemAt(0) else null
            val url = item?.text?.toString()
            when {
                item?.uri != null -> playUriSafely(item.uri)
                url != null -> playLink(this, url)
                data != null -> playUriSafely(data)
                extraText != null -> playLink(this, extraText)
                else -> finish()
            }
        } else if (data?.scheme == "content") {
            playUriSafely(data)
        } else finish()
    }

    private fun playOfflineDirect(path: String, title: String?) {
        isOfflineMode = true
        hideUnavailableOverlay()
        val skeleton = findViewById<com.facebook.shimmer.ShimmerFrameLayout>(R.id.player_skeleton_loading)
        val detailsScroll = findViewById<View>(R.id.player_details_scroll)
        skeleton?.stopShimmer()
        skeleton?.visibility = View.GONE
        detailsScroll?.visibility = View.VISIBLE

        val resolvedUri: Uri = try {
            if (path.startsWith("content://")) {
                Uri.parse(path)
            } else {
                val f = File(path)
                if (f.exists()) {
                    Uri.fromFile(f)
                } else {
                    val appDirs = listOfNotNull(
                        getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                        filesDir,
                        File("/storage/emulated/0/Download/MovieVortex")
                    )
                    val clean = title?.replace(Regex("[^a-zA-Z0-9]"), "")?.lowercase() ?: ""
                    var found: File? = null
                    for (dir in appDirs) {
                        if (dir.exists()) {
                            found = dir.walkTopDown().firstOrNull { file ->
                                file.isFile && (file.extension.equals("mp4", true) || file.extension.equals("mkv", true) || file.extension.equals("webm", true)) &&
                                        (clean.isNotBlank() && file.nameWithoutExtension.replace(Regex("[^a-zA-Z0-9]"), "").lowercase().contains(clean))
                            }
                            if (found != null) break
                        }
                    }
                    if (found != null) Uri.fromFile(found) else Uri.fromFile(f)
                }
            }
        } catch (_: Exception) {
            intent.data ?: Uri.parse(path)
        }

        val displayName = title ?: (if (!path.startsWith("content://")) File(path).nameWithoutExtension.replace("_", " ") else "Downloaded Movie")
        val finalUri = if (intent.data != null && intent.data?.scheme == "content") intent.data!! else resolvedUri
        val extractorUri = ExtractorUri(
            uri = finalUri,
            name = displayName,
            id = path.hashCode()
        )
        val args = GeneratorPlayer.newInstance(
            DownloadFileGenerator(listOf(extractorUri)),
            0
        )
        runOnUiThread {
            findViewById<TextView>(R.id.inline_player_title)?.text = displayName
            findViewById<TextView>(R.id.inline_player_meta)?.text = "Offline Download"
            findViewById<TextView>(R.id.inline_player_overview)?.text = displayName
            findViewById<View>(R.id.server_tabs_container)?.visibility = View.GONE
            findViewById<View>(R.id.inline_episodes_header)?.visibility = View.GONE
            findViewById<View>(R.id.inline_episodes_recycler)?.visibility = View.GONE
        }
        navigateToPlayer(args)
    }

    private fun playDirectStream(url: String, title: String?) {
        val displayName = title ?: "Live TV"
        val args = GeneratorPlayer.newInstance(
            LinkGenerator(
                listOf(BasicLink(url, displayName)),
                extract = false,
                id = url.hashCode()
            ),
            0
        )
        runOnUiThread {
            findViewById<TextView>(R.id.inline_player_title)?.text = displayName
            findViewById<TextView>(R.id.inline_player_meta)?.text = "Live Stream"
            findViewById<TextView>(R.id.inline_player_overview)?.text = "Playing live stream"
        }
        navigateToPlayer(args)
    }

    private fun playUriSafely(uri: Uri) {
        val name = SafeFile.fromUri(this, uri)?.name() ?: uri.lastPathSegment?.substringAfterLast('/')?.substringBeforeLast('.') ?: "Downloaded Video"
        val extractorUri = ExtractorUri(
            uri = uri,
            name = name,
            id = uri.hashCode()
        )
        val args = GeneratorPlayer.newInstance(
            DownloadFileGenerator(listOf(extractorUri)),
            0
        )
        navigateToPlayer(args)
    }

    private fun navigateToPlayer(args: Bundle) {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.navigation_player, inclusive = true, saveState = false)
            .build()
        main {
            try {
                val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
                val navController = navHost?.navController
                if (navController != null) {
                    if (navController.currentDestination?.id == R.id.navigation_player) {
                        navController.navigate(R.id.navigation_player, args, navOptions)
                    } else {
                        val graph = navController.navInflater.inflate(R.navigation.mobile_navigation)
                        graph.setStartDestination(R.id.navigation_player)
                        navController.setGraph(graph, args)
                    }
                } else {
                    this@DownloadedPlayerActivity.navigate(R.id.global_to_navigation_player, args, navOptions)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error navigating to player fragment", e)
                try {
                    this@DownloadedPlayerActivity.navigate(R.id.global_to_navigation_player, args)
                } catch (t: Throwable) {
                    Log.e(TAG, "Fallback navigation failed", t)
                }
            }
        }
    }

    private fun playProviderContent(apiName: String, url: String, intent: Intent) {
        hideUnavailableOverlay()
        showSkeletonLoading()
        val media = intent.getSafeSerializable<MovieVortexMedia>("MEDIA_OBJECT")

        if (media != null) {
            val fallbackTitle = media.title
            val fallbackYear = media.year?.toString()
            val fallbackScore = media.rating?.let { "★ $it" }
            val fallbackPlot = media.synopsis ?: ""

            currentMediaTitle = fallbackTitle
            findViewById<TextView>(R.id.inline_player_title)?.text = fallbackTitle
            val meta = buildString {
                fallbackYear?.let { append("$it  •  ") }
                if (media.type == MediaType.TvSeries) append("TV Series") else append("Movie")
                fallbackScore?.let { append("  •  $it") }
            }
            findViewById<TextView>(R.id.inline_player_meta)?.text = meta
            findViewById<TextView>(R.id.inline_player_overview)?.text = fallbackPlot
            findViewById<TextView>(R.id.inline_download_text)?.text = if (media.type == MediaType.TvSeries) "Download Episode" else "Download Movie"

            if (media.cast.isNotEmpty()) {
                val actors = media.cast.map { ActorData(Actor(it.name, it.avatarUrl), roleString = it.role) }
                setupCast(actors)
            }
        }

        ioSafe {
            try {
                var api = APIHolder.getApiFromNameNull(apiName)
                    ?: APIHolder.apis.firstOrNull { url.startsWith(it.mainUrl) }
                    ?: APIHolder.apis.firstOrNull()

                if (api == null) {
                    try {
                        com.movievortex.android.core.VortexPluginBootstrap.bootstrap(this@DownloadedPlayerActivity)
                    } catch (_: Exception) {}
                    api = APIHolder.getApiFromNameNull(apiName)
                        ?: APIHolder.apis.firstOrNull { url.startsWith(it.mainUrl) }
                        ?: APIHolder.apis.firstOrNull()
                }

                if (api == null) {
                    triggerAutoServerFallback("Provider $currentMarkServer not available.")
                    return@ioSafe
                }

                val repo = APIRepository(api)
                val res = repo.load(url)
                if (res is Resource.Success) {
                    val loadResponse = res.value
                    val episodes: List<ResultEpisode> = when (loadResponse) {
                        is MovieLoadResponse -> {
                            listOf(
                                ResultEpisode(
                                    headerName = loadResponse.name,
                                    name = loadResponse.name,
                                    poster = loadResponse.posterUrl,
                                    episode = 1,
                                    seasonIndex = null,
                                    season = null,
                                    data = loadResponse.dataUrl.ifBlank { url },
                                    apiName = api.name,
                                    id = (api.name + url).hashCode(),
                                    index = 0,
                                    position = 0L,
                                    duration = 0L,
                                    score = loadResponse.score,
                                    description = loadResponse.plot,
                                    isFiller = false,
                                    tvType = TvType.Movie,
                                    parentId = url.hashCode(),
                                    videoWatchState = VideoWatchState.None
                                )
                            )
                        }
                        is TvSeriesLoadResponse -> {
                            loadResponse.episodes.mapIndexed { idx, ep ->
                                ResultEpisode(
                                    headerName = ep.name ?: "Episode ${ep.episode ?: (idx + 1)}",
                                    name = ep.name,
                                    poster = ep.posterUrl,
                                    episode = ep.episode ?: (idx + 1),
                                    seasonIndex = ep.season,
                                    season = ep.season,
                                    data = ep.data,
                                    apiName = api.name,
                                    id = ep.data.hashCode(),
                                    index = idx,
                                    position = 0L,
                                    duration = 0L,
                                    score = ep.score,
                                    description = ep.description,
                                    isFiller = false,
                                    tvType = TvType.TvSeries,
                                    parentId = url.hashCode(),
                                    videoWatchState = VideoWatchState.None
                                )
                            }
                        }
                        is AnimeLoadResponse -> {
                            loadResponse.episodes.values.flatten().mapIndexed { idx, ep ->
                                ResultEpisode(
                                    headerName = ep.name ?: "Episode ${ep.episode ?: (idx + 1)}",
                                    name = ep.name,
                                    poster = ep.posterUrl,
                                    episode = ep.episode ?: (idx + 1),
                                    seasonIndex = ep.season,
                                    season = ep.season,
                                    data = ep.data,
                                    apiName = api.name,
                                    id = ep.data.hashCode(),
                                    index = idx,
                                    position = 0L,
                                    duration = 0L,
                                    score = ep.score,
                                    description = ep.description,
                                    isFiller = false,
                                    tvType = TvType.Anime,
                                    parentId = url.hashCode(),
                                    videoWatchState = VideoWatchState.None
                                )
                            }
                        }
                        else -> emptyList()
                    }

                    if (episodes.isEmpty()) {
                        triggerAutoServerFallback("No streaming links found on $currentMarkServer.")
                        return@ioSafe
                    }

                    val targetEpNumber = intent.getIntExtra("EPISODE", -1)
                    val targetEpData = intent.getStringExtra("EPISODE_DATA")
                    val startIndex = when {
                        targetEpData != null -> episodes.indexOfFirst { it.data == targetEpData }.coerceAtLeast(0)
                        targetEpNumber != -1 -> episodes.indexOfFirst { it.episode == targetEpNumber }.coerceAtLeast(0)
                        else -> 0
                    }

                    val generator = RepoLinkGenerator(episodes, page = loadResponse)

                    main {
                        showContentHideSkeleton()
                        currentLoadResponse = loadResponse
                        currentMediaTitle = loadResponse.name
                        currentAllEpisodes = episodes

                        findViewById<TextView>(R.id.inline_player_title)?.text = loadResponse.name
                        val meta = buildString {
                            loadResponse.year?.let { append("$it  •  ") }
                            if (loadResponse is TvSeriesLoadResponse) append("TV Series") else append("Movie")
                            loadResponse.score?.let { append("  •  ★ $it") }
                        }
                        findViewById<TextView>(R.id.inline_player_meta)?.text = meta
                        findViewById<TextView>(R.id.inline_player_overview)?.text = loadResponse.plot ?: ""
                        findViewById<TextView>(R.id.inline_download_text)?.text = if (loadResponse is MovieLoadResponse) "Download Movie" else "Download Episode"

                        // Star Cast Details
                        if (!loadResponse.actors.isNullOrEmpty()) {
                            setupCast(loadResponse.actors)
                        }

                        // Related Movies Recommendations
                        setupRelatedMovies(loadResponse.recommendations)

                        if (episodes.isNotEmpty() && (loadResponse is TvSeriesLoadResponse || loadResponse is AnimeLoadResponse || episodes.size > 1)) {
                            val seasons = episodes.mapNotNull { it.season }.distinct().sorted()
                            val availableSeasons = if (seasons.isEmpty()) listOf(1) else seasons
                            setupSeasonTabs(availableSeasons, episodes, loadResponse, startIndex)
                            navigateToPlayer(GeneratorPlayer.newInstance(generator, startIndex))
                        } else {
                            findViewById<View>(R.id.inline_seasons_section)?.visibility = View.GONE
                            navigateToPlayer(GeneratorPlayer.newInstance(generator, startIndex))
                        }
                    }
                } else {
                    triggerAutoServerFallback("Failed to resolve stream on $currentMarkServer.")
                }
            } catch (e: Exception) {
                logError(e)
                triggerAutoServerFallback("Error loading $currentMarkServer: ${e.message}")
            }
        }
    }

    private fun setupDownloadButton() {
        val downloadBtn = findViewById<View>(R.id.inline_download_button) ?: return
        downloadBtn.setOnClickListener {
            handleDownloadClick()
        }
    }

    private fun handleDownloadClick() {
        val resp = currentLoadResponse
        val ep = currentAllEpisodes.getOrNull(currentPlayingEpisodeIndex) ?: currentAllEpisodes.firstOrNull()
        if (resp == null || ep == null) {
            android.widget.Toast.makeText(this, "Stream details loading...", android.widget.Toast.LENGTH_SHORT).show()
            return
        }

        val downloadText = findViewById<TextView>(R.id.inline_download_text)
        downloadText?.text = "Resolving qualities..."
        android.widget.Toast.makeText(this, "Checking available qualities...", android.widget.Toast.LENGTH_SHORT).show()

        val generator = RepoLinkGenerator(listOf(ep), page = resp)
        val extractedLinks = mutableListOf<ExtractorLink>()
        val extractedSubs = mutableListOf<com.lagradost.cloudstream3.ui.player.SubtitleData>()

        ioSafe {
            try {
                generator.generateLinks(
                    clearCache = false,
                    sourceTypes = setOf(ExtractorLinkType.VIDEO, ExtractorLinkType.M3U8),
                    callback = { (link, _) ->
                        if (link != null) {
                            synchronized(extractedLinks) {
                                if (extractedLinks.none { it.url == link.url }) {
                                    extractedLinks.add(link)
                                }
                            }
                        }
                    },
                    subtitleCallback = { sub ->
                        synchronized(extractedSubs) {
                            extractedSubs.add(sub)
                        }
                    },
                    offset = 0,
                    isCasting = false
                )
            } catch (_: Exception) {}

            main {
                downloadText?.text = if (resp is MovieLoadResponse) "Download Movie" else "Download Episode"
                if (extractedLinks.isEmpty()) {
                    // Fallback to direct auto download if links couldn't be pre-extracted
                    startDirectDownload(ep, resp, null, emptyList())
                } else {
                    showQualitySelectionDialog(ep, resp, extractedLinks, extractedSubs)
                }
            }
        }
    }

    private fun showQualitySelectionDialog(
        ep: ResultEpisode,
        resp: com.lagradost.cloudstream3.LoadResponse,
        links: List<ExtractorLink>,
        subs: List<com.lagradost.cloudstream3.ui.player.SubtitleData>
    ) {
        val sortedLinks = links.sortedByDescending { it.quality }
        val displayItems = sortedLinks.map { link ->
            val qualityStr = Qualities.getStringByInt(link.quality)
            val name = link.name
            if (qualityStr.isNotBlank() && !qualityStr.equals("Unknown", ignoreCase = true)) {
                "$qualityStr • $name"
            } else {
                name
            }
        }

        val title = ep.name ?: resp.name
        SingleSelectionHelper.apply {
            showBottomDialogInstant(
                displayItems,
                "Select Quality - $title",
                dismissCallback = {}
            ) { selectedIndex ->
                val chosenLink = sortedLinks.getOrNull(selectedIndex)
                startDirectDownload(ep, resp, chosenLink, subs)
            }
        }
    }

    private fun startDirectDownload(
        ep: ResultEpisode,
        resp: com.lagradost.cloudstream3.LoadResponse,
        selectedLink: ExtractorLink?,
        subs: List<com.lagradost.cloudstream3.ui.player.SubtitleData>
    ) {
        val isMovie = resp is MovieLoadResponse
        val linksList = if (selectedLink != null) listOf(selectedLink) else null
        val title = ep.name ?: resp.name
        val qualityStr = selectedLink?.let { Qualities.getStringByInt(it.quality) } ?: "1080p"

        try {
            // Save cache keys so MovieVortex and CloudStream download screens discover this immediately
            val parentId = (resp.apiName + resp.url).hashCode()
            val posterUrl = ep.poster ?: resp.posterUrl
            val norm = com.movievortex.android.controller.normalizeTitle(title)
            val cleanName = title.replace(Regex("[^a-zA-Z0-9.-]"), "_")

            try {
                setKey("vortex_download_posters", ep.id.toString(), posterUrl)
                setKey("vortex_download_posters", norm, posterUrl)
                setKey("vortex_download_posters", cleanName, posterUrl)
                setKey("vortex_download_posters", title, posterUrl)
            } catch (_: Exception) {}

            val epCached = DownloadObjects.DownloadEpisodeCached(
                name = title,
                poster = posterUrl,
                episode = ep.episode,
                season = ep.season,
                id = ep.id,
                parentId = parentId,
                score = ep.score,
                description = ep.description,
                cacheTime = System.currentTimeMillis()
            )

            setKey(
                DOWNLOAD_HEADER_CACHE,
                parentId.toString(),
                DownloadObjects.DownloadHeaderCached(
                    apiName = resp.apiName,
                    url = resp.url,
                    type = resp.type,
                    name = resp.name,
                    poster = resp.posterUrl,
                    id = parentId,
                    cacheTime = System.currentTimeMillis()
                )
            )
            setKey(DOWNLOAD_EPISODE_CACHE, ep.id.toString(), epCached)
            setKey(getFolderName(DOWNLOAD_EPISODE_CACHE, parentId.toString()), ep.id.toString(), epCached)
        } catch (_: Exception) {}

        try {
            DownloadQueueManager.addToQueue(
                DownloadObjects.DownloadQueueItem(
                    episode = ep,
                    isMovie = isMovie,
                    resultName = resp.name,
                    resultType = resp.type,
                    resultPoster = resp.posterUrl,
                    apiName = resp.apiName,
                    resultId = (resp.apiName + resp.url).hashCode(),
                    resultUrl = resp.url,
                    links = linksList,
                    subs = subs.ifEmpty { null }
                ).toWrapper()
            )
        } catch (e: Exception) {
            logError(e)
        }

        try {
            // Store strictly inside app-private internal directory (never in public external folders)
            val appDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: filesDir
            val cleanName = title.replace(Regex("[^a-zA-Z0-9.-]"), "_")
            val localPath = File(appDir, "$cleanName.mp4").absolutePath

            val uiItem = DownloadUiItem(
                id = ep.id.toString(),
                title = title,
                mediaType = if (isMovie) MediaType.Movie else MediaType.TvSeries,
                poster = ep.poster ?: resp.posterUrl,
                quality = qualityStr,
                fileSizeBytes = 0L,
                downloadedBytes = 0L,
                progress = 0.05f,
                state = MediaDownloadState.Downloading,
                localFilePath = localPath
            )
            com.movievortex.android.controller.DownloadController.registerExternalDownload(uiItem)
        } catch (_: Exception) {}

        android.widget.Toast.makeText(
            this,
            "Download queued: $title ($qualityStr)",
            android.widget.Toast.LENGTH_SHORT
        ).show()
        findViewById<TextView>(R.id.inline_download_text)?.text = "Downloading ($qualityStr)"
    }

    private fun setupCast(actors: List<ActorData>?) {
        val castSection = findViewById<View>(R.id.inline_cast_section) ?: return
        val castRecycler = findViewById<RecyclerView>(R.id.inline_cast_recycler) ?: return
        if (actors.isNullOrEmpty()) {
            castSection.visibility = View.GONE
            return
        }
        castSection.visibility = View.VISIBLE
        castRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        castRecycler.adapter = InlineCastAdapter(actors)
    }

    private fun setupRelatedMovies(recommendations: List<SearchResponse>?) {
        val relatedSection = findViewById<View>(R.id.inline_related_section) ?: return
        val relatedRecycler = findViewById<RecyclerView>(R.id.inline_related_recycler) ?: return
        if (recommendations.isNullOrEmpty()) {
            relatedSection.visibility = View.GONE
            return
        }
        relatedSection.visibility = View.VISIBLE
        relatedRecycler.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 2)
        relatedRecycler.isNestedScrollingEnabled = false
        relatedRecycler.adapter = InlineRelatedAdapter(recommendations) { item ->
            currentContentUrl = item.url
            currentMediaTitle = item.name
            currentMarkServer = ServerManager.toDisplayName(item.apiName)
            updateServerTabsUI(currentMarkServer)
            triedServers.clear()
            triedServers.add(currentMarkServer)
            val clickedIntent = Intent(intent).apply {
                removeExtra("MEDIA_OBJECT")
                putExtra("TITLE", item.name)
                putExtra("CONTENT_URL", item.url)
                putExtra(MainActivity.API_NAME_EXTRA_KEY, item.apiName)
            }
            showSkeletonLoading()
            playProviderContent(item.apiName, item.url, clickedIntent)
        }
    }

    private fun setupSeasonTabs(
        seasons: List<Int>,
        allEpisodes: List<ResultEpisode>,
        loadResponse: com.lagradost.cloudstream3.LoadResponse,
        initialPlayingIndex: Int
    ) {
        val seasonsSection = findViewById<View>(R.id.inline_seasons_section) ?: return
        val seasonsContainer = findViewById<LinearLayout>(R.id.inline_seasons_container) ?: return
        val seasonsScroll = findViewById<View>(R.id.inline_seasons_scroll) ?: return
        val recycler = findViewById<RecyclerView>(R.id.inline_episodes_recycler) ?: return

        seasonsSection.visibility = View.VISIBLE
        seasonsContainer.removeAllViews()

        if (seasons.size <= 1) {
            seasonsScroll.visibility = View.GONE
        } else {
            seasonsScroll.visibility = View.VISIBLE
        }

        currentPlayingEpisodeIndex = initialPlayingIndex
        val playingEp = allEpisodes.getOrNull(initialPlayingIndex)
        val initialSeason = playingEp?.season ?: seasons.firstOrNull() ?: 1

        fun loadSeasonEpisodes(seasonNum: Int) {
            currentSelectedSeason = seasonNum
            val filtered = allEpisodes.filter { (it.season ?: 1) == seasonNum }
            val displayEpisodes = if (filtered.isNotEmpty()) filtered else allEpisodes

            recycler.layoutManager = LinearLayoutManager(this@DownloadedPlayerActivity)
            recycler.adapter = InlineEpisodeAdapter(displayEpisodes, currentPlayingEpisodeIndex) { clickedEp ->
                currentPlayingEpisodeIndex = clickedEp.index
                val gen = RepoLinkGenerator(allEpisodes, page = loadResponse)
                navigateToPlayer(GeneratorPlayer.newInstance(gen, clickedEp.index))
            }

            for (i in 0 until seasonsContainer.childCount) {
                val tabView = seasonsContainer.getChildAt(i) as? TextView ?: continue
                val tagSeason = tabView.tag as? Int ?: (i + 1)
                if (tagSeason == seasonNum) {
                    tabView.setBackgroundResource(R.drawable.bg_season_tab_active)
                    tabView.setTextColor(Color.WHITE)
                    tabView.setTypeface(null, Typeface.BOLD)
                } else {
                    tabView.setBackgroundResource(R.drawable.bg_season_tab_inactive)
                    tabView.setTextColor(Color.parseColor("#B0B0C0"))
                    tabView.setTypeface(null, Typeface.NORMAL)
                }
            }
        }

        for (s in seasons) {
            val tab = TextView(this).apply {
                text = "Season $s"
                textSize = 13f
                tag = s
                val padH = (16 * resources.displayMetrics.density).toInt()
                val padV = (8 * resources.displayMetrics.density).toInt()
                setPadding(padH, padV, padH, padV)
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = (8 * resources.displayMetrics.density).toInt()
                }
                layoutParams = params
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    loadSeasonEpisodes(s)
                }
            }
            seasonsContainer.addView(tab)
        }

        loadSeasonEpisodes(initialSeason)
    }

    override fun onResume() {
        super.onResume()
        CommonActivity.setActivityInstance(this)
    }
}

class InlineEpisodeAdapter(
    private val episodes: List<ResultEpisode>,
    private var currentPlayingIndex: Int,
    private val onEpisodeClick: (ResultEpisode) -> Unit
) : RecyclerView.Adapter<InlineEpisodeAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root: View = view.findViewById(R.id.episode_item_root)
        val thumbnail: ImageView = view.findViewById(R.id.ep_thumbnail)
        val title: TextView = view.findViewById(R.id.ep_title)
        val desc: TextView = view.findViewById(R.id.ep_desc)
        val playIcon: ImageView = view.findViewById(R.id.ep_play_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_inline_episode, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ep = episodes[position]
        val epNum = ep.episode ?: (position + 1)
        val epTitleText = if (!ep.name.isNullOrBlank() && ep.name != "Episode $epNum") {
            "$epNum. ${ep.name}"
        } else {
            "Episode $epNum"
        }
        holder.title.text = epTitleText
        holder.desc.text = ep.description ?: ""
        holder.desc.visibility = if (ep.description.isNullOrBlank()) View.GONE else View.VISIBLE

        if (!ep.poster.isNullOrBlank()) {
            holder.thumbnail.load(ep.poster)
        } else {
            holder.thumbnail.setImageResource(R.drawable.example_poster)
        }

        val isSelected = ep.index == currentPlayingIndex
        if (isSelected) {
            holder.root.setBackgroundResource(R.drawable.bg_episode_item_active)
            holder.playIcon.imageTintList = ColorStateList.valueOf(Color.WHITE)
        } else {
            holder.root.setBackgroundResource(R.drawable.bg_episode_item_inactive)
            holder.playIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#80FFFFFF"))
        }

        holder.root.setOnClickListener {
            val prev = currentPlayingIndex
            currentPlayingIndex = ep.index
            notifyDataSetChanged()
            onEpisodeClick(ep)
        }
    }

    override fun getItemCount(): Int = episodes.size
}

class InlineCastAdapter(
    private val actors: List<ActorData>
) : RecyclerView.Adapter<InlineCastAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.cast_image)
        val name: TextView = view.findViewById(R.id.cast_name)
        val role: TextView = view.findViewById(R.id.cast_role)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_inline_cast, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = actors[position]
        holder.name.text = item.actor.name
        val roleStr = item.roleString ?: item.role?.name ?: ""
        holder.role.text = roleStr
        holder.role.visibility = if (roleStr.isBlank()) View.GONE else View.VISIBLE

        val imgUrl = item.actor.image
        if (!imgUrl.isNullOrBlank()) {
            holder.image.load(imgUrl)
        } else {
            holder.image.setImageResource(R.drawable.example_poster)
        }
    }

    override fun getItemCount(): Int = actors.size
}

class InlineRelatedAdapter(
    private val items: List<SearchResponse>,
    private val onItemClick: (SearchResponse) -> Unit
) : RecyclerView.Adapter<InlineRelatedAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root: View = view.findViewById(R.id.related_item_root)
        val poster: ImageView = view.findViewById(R.id.related_poster)
        val title: TextView = view.findViewById(R.id.related_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_inline_related, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.name
        val posterUrl = item.posterUrl
        if (!posterUrl.isNullOrBlank()) {
            holder.poster.load(posterUrl)
        } else {
            holder.poster.setImageResource(R.drawable.example_poster)
        }
        holder.root.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = items.size
}

@Suppress("DEPRECATION")
private inline fun <reified T : java.io.Serializable> Intent.getSafeSerializable(key: String): T? =
    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.TIRAMISU) {
        getSerializableExtra(key) as? T
    } else {
        getSerializableExtra(key, T::class.java)
    }
