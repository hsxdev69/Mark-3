package com.movievortex.android.model

import java.io.Serializable

data class LiveChannel(
    val id: String,
    val name: String,
    val logoUrl: String,
    val streamUrl: String,
    val category: String,
    val resolution: String = "1080p FHD",
    val currentProgram: String = "Live Streaming 24/7",
    val isFeatured: Boolean = false,
    val country: String = "IN"
) : Serializable {
    fun toMovieVortexMedia(): MovieVortexMedia = MovieVortexMedia(
        id = "live_$id",
        title = name,
        poster = logoUrl,
        backdrop = logoUrl,
        type = MediaType.LiveStream,
        genres = listOf(category, "LIVE", resolution),
        rawUrl = streamUrl,
        providerApiName = "LiveTV"
    )
}

object LiveChannelsRepository {
    val Categories = listOf(
        "All Channels",
        "📺 Hindi Entertainment",
        "🎬 Hindi Movies",
        "📰 News",
        "🏏 Sports",
        "🎵 Music",
        "👶 Kids",
        "🌍 Documentary",
        "🕉️ Devotional",
        "🚩 Marathi",
        "🌴 Tamil",
        "🏛️ Telugu",
        "🌾 Kannada",
        "🎨 Bengali",
        "🥥 Malayalam",
        "🚜 Punjabi",
        "🌐 International"
    )

    val AllChannels: List<LiveChannel> = listOf(
        // Featured Flagships
        LiveChannel(
            id = "aaj_tak_hd",
            name = "Aaj Tak HD",
            logoUrl = "https://xstreamcp-assets-msp.streamready.in/assets/LIVETV/LIVECHANNEL/LIVETV_LIVETVCHANNEL_AAJ_TAK/images/LOGO_HD/image.png",
            streamUrl = "https://feeds.intoday.in/aajtak/api/aajtakhd/master.m3u8",
            category = "News",
            resolution = "1080p FHD",
            currentProgram = "Sabse Tez • 24x7 Breaking News & Primetime",
            isFeatured = true
        ),
        LiveChannel(
            id = "dd_sports_hd",
            name = "DD Sports HD",
            logoUrl = "https://dtil.tmsimg.com/assets/s158255_ld_h15_aa.png",
            streamUrl = "https://cdn.pishow.tv/ott/live/1161/master.m3u8",
            category = "Sports",
            resolution = "1080p FHD",
            currentProgram = "Cricket, National Games & Live Championships",
            isFeatured = true
        ),
        LiveChannel(
            id = "red_bull_tv",
            name = "Red Bull TV",
            logoUrl = "https://i.imgur.com/Qh15Eea.png",
            streamUrl = "https://rbmn-live.akamaized.net/hls/live/590964/BoRB-AT/master.m3u8",
            category = "Sports",
            resolution = "1080p FHD",
            currentProgram = "Motorsport, F1, Extreme Sports & Live Events",
            isFeatured = true
        ),
        LiveChannel(
            id = "50_cent_action",
            name = "50 Cent Action",
            logoUrl = "https://provider-static.plex.tv/epg/cms/production/bcfb9977-809f-49ae-acc7-430f2c6ffb26/50CentAction_Logo_1500x1000_DarkBG_-_Chris_Connors.png",
            streamUrl = "https://jmp2.uk/plu-68487fb3f212bedacf5a53e3.m3u8",
            category = "Hindi Movies",
            resolution = "1080p FHD",
            currentProgram = "High-Octane Action Blockbusters 24/7",
            isFeatured = true
        ),
        LiveChannel(
            id = "aniplus_asia",
            name = "Aniplus Asia",
            logoUrl = "https://i.imgur.com/Im3MePy.png",
            streamUrl = "https://amg18481-amg18481c1-amgplt0352.playout.now3.amagi.tv/playlist/amg18481-amg18481c1-amgplt0352/playlist.m3u8",
            category = "Kids",
            resolution = "720p HD",
            currentProgram = "Simulcast Anime Series & Manga Premieres",
            isFeatured = true
        ),
        LiveChannel(
            id = "bbc_news_hd",
            name = "BBC News HD",
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/62/BBC_News_2019.svg/1200px-BBC_News_2019.svg.png",
            streamUrl = "https://vs-hls-push-ww-live.akamaized.net/x=4/i=urn:bbc:pips:service:bbc_news_channel_hd/t=3840/v=pv14/b=5070016/main.m3u8",
            category = "International",
            resolution = "1080p FHD",
            currentProgram = "Global Headlines, Verified Analysis & In-Depth Reports",
            isFeatured = true,
            country = "GB"
        ),

        // 1. Hindi Entertainment
        LiveChannel(
            id = "dangal_tv_hd",
            name = "Dangal TV",
            logoUrl = "https://xstreamcp-assets-msp.streamready.in/assets/LIVETV/LIVECHANNEL/LIVETV_LIVETVCHANNEL_DANGAL/images/LOGO_HD/image.png",
            streamUrl = "https://live-dangal.akamaized.net/liveabr/playlist.m3u8",
            category = "Hindi Entertainment",
            resolution = "720p HD",
            currentProgram = "Family Dramas, Blockbuster Serials & Shows"
        ),
        LiveChannel(
            id = "dangal_2",
            name = "Dangal 2",
            logoUrl = "https://i.imgur.com/BEr1r7L.png",
            streamUrl = "https://live-dangal2.akamaized.net/liveabr/playlist.m3u8",
            category = "Hindi Entertainment",
            resolution = "720p HD",
            currentProgram = "Action Dramas, Classic Cinema & Crime Shows"
        ),
        LiveChannel(
            id = "manoranjan_grand",
            name = "Manoranjan TV",
            logoUrl = "https://cdn.pishow.tv/ott/live/1011/master.m3u8",
            streamUrl = "https://cdn.pishow.tv/ott/live/1011/master.m3u8",
            category = "Hindi Entertainment",
            resolution = "720p HD",
            currentProgram = "Popular Hindi Serials, Comedy & Entertainment"
        ),
        LiveChannel(
            id = "4_sides_tv",
            name = "4 SiDES TV",
            logoUrl = "https://jiotvimages.cdn.jio.com/dare_images/images/4sites.png",
            streamUrl = "https://stream.ottlive.co.in/4sidestv/index.m3u8",
            category = "Hindi Entertainment",
            resolution = "1080p FHD",
            currentProgram = "Entertainment Shows, Reality TV & Lifestyle"
        ),
        LiveChannel(
            id = "4u_tv",
            name = "4U TV",
            logoUrl = "https://hls.4utv.live/hls/stream.m3u8",
            streamUrl = "https://hls.4utv.live/hls/stream.m3u8",
            category = "Hindi Entertainment",
            resolution = "720p HD",
            currentProgram = "Comedy Shorts, Drama & Celebrity Gossip"
        ),

        // 2. Hindi Movies
        LiveChannel(
            id = "goldmines_tv",
            name = "Goldmines",
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e0/Goldmines_Telefilms_Logo.jpg/800px-Goldmines_Telefilms_Logo.jpg",
            streamUrl = "https://streams.tangotv.in/GOLDMINES/ORIGIN/index.m3u8",
            category = "Hindi Movies",
            resolution = "576p SD",
            currentProgram = "South Indian Hindi Dubbed Blockbuster Premieres"
        ),
        LiveChannel(
            id = "b4u_movies",
            name = "B4U Movies",
            logoUrl = "https://i.imgur.com/M9kMFJl.png",
            streamUrl = "https://streams.tangotv.in/B4UMOVIES/ORIGIN/index.m3u8",
            category = "Hindi Movies",
            resolution = "576p SD",
            currentProgram = "Superhit Bollywood Movies & Evergreen Classics"
        ),
        LiveChannel(
            id = "24h_movies",
            name = "24H Movie Central",
            logoUrl = "https://i.imgur.com/iSVnzR1.png",
            streamUrl = "https://d1j2u714xk898n.cloudfront.net/scheduler/scheduleMaster/145.m3u8",
            category = "Hindi Movies",
            resolution = "720p HD",
            currentProgram = "Non-stop Cinema Marathon & Indie Gems"
        ),
        LiveChannel(
            id = "80s_rewind",
            name = "80s Rewind Cinema",
            logoUrl = "https://i.imgur.com/nkEeYfI.png",
            streamUrl = "https://jmp2.uk/plu-5ca525b650be2571e3943c63.m3u8",
            category = "Hindi Movies",
            resolution = "1080p FHD",
            currentProgram = "Iconic 80s Thrillers, Sci-Fi & Cult Hits"
        ),
        LiveChannel(
            id = "90s_throwback",
            name = "90s Throwback",
            logoUrl = "https://i.imgur.com/KoGko6M.png",
            streamUrl = "https://jmp2.uk/plu-5f4d86f519358a00072b978e.m3u8",
            category = "Hindi Movies",
            resolution = "1080p FHD",
            currentProgram = "Golden 90s Blockbusters & Action Dramas"
        ),
        LiveChannel(
            id = "00s_replay",
            name = "00s Replay Movies",
            logoUrl = "https://images.pluto.tv/channels/62ba60f059624e000781c436/colorLogoPNG.png",
            streamUrl = "https://jmp2.uk/plu-62ba60f059624e000781c436.m3u8",
            category = "Hindi Movies",
            resolution = "1080p FHD",
            currentProgram = "Millennium Classics & High-Energy Movies"
        ),
        LiveChannel(
            id = "70s_cinema",
            name = "70s Cinema Club",
            logoUrl = "https://i.imgur.com/mgXeEE4.png",
            streamUrl = "https://jmp2.uk/plu-5f4d878d3d19b30007d2e782.m3u8",
            category = "Hindi Movies",
            resolution = "1080p FHD",
            currentProgram = "Vintage Crime, Neo-Noir & Suspense Masterpieces"
        ),

        // 3. News
        LiveChannel(
            id = "abp_news",
            name = "ABP News",
            logoUrl = "https://i.imgur.com/DKHUFVQ.png",
            streamUrl = "https://d2l4ar6y3mrs4k.cloudfront.net/live-streaming/abpnews-livetv/master.m3u8",
            category = "News",
            resolution = "1080p FHD",
            currentProgram = "Desh Ki Har Badi Khabar & 24x7 Updates"
        ),
        LiveChannel(
            id = "india_tv",
            name = "India TV",
            logoUrl = "https://xstreamcp-assets-msp.streamready.in/assets/LIVETV/LIVECHANNEL/LIVETV_LIVETVCHANNEL_INDIA_TV/images/LOGO_HD/image.png",
            streamUrl = "https://pl-indiatvnews.akamaized.net/out/v1/db79179b608641ceaa5a4d0dd0dca8da/index.m3u8",
            category = "News",
            resolution = "720p HD",
            currentProgram = "Aap Ki Adalat & Top National Bulletins"
        ),
        LiveChannel(
            id = "zee_news",
            name = "Zee News",
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1c/Zee_News_logo.svg/1200px-Zee_News_logo.svg.png",
            streamUrl = "https://dknttpxmr0dwf.cloudfront.net/index_57.m3u8",
            category = "News",
            resolution = "1080p FHD",
            currentProgram = "DNA: Daily News and Analysis & Primetime"
        ),
        LiveChannel(
            id = "ndtv_india",
            name = "NDTV India",
            logoUrl = "https://i.imgur.com/QjJYohG.png",
            streamUrl = "https://ndtvindiaelemarchana.akamaized.net/hls/live/2003679/ndtvindia/master.m3u8",
            category = "News",
            resolution = "1080p FHD",
            currentProgram = "Prime Time & Ground Realities with Ravish"
        ),
        LiveChannel(
            id = "tv9_bharatvarsh",
            name = "TV9 Bharatvarsh",
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/TV9_Bharatvarsh.png/800px-TV9_Bharatvarsh.png",
            streamUrl = "https://dyjmyiv3bp2ez.cloudfront.net/pub-iotv9hinjzgtpe/liveabr/playlist.m3u8",
            category = "News",
            resolution = "720p HD",
            currentProgram = "National Superfast Bulletins & War Room"
        ),
        LiveChannel(
            id = "republic_bharat",
            name = "Republic Bharat",
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/95/Republic_Bharat_Logo.png/800px-Republic_Bharat_Logo.png",
            streamUrl = "https://raw.githubusercontent.com/amazeyourself/adaptive-streams/refs/heads/main/streams/in/YuppTV/RepublicBharat.m3u8",
            category = "News",
            resolution = "1080p FHD",
            currentProgram = "Rashtriya Debate with Arnab Goswami"
        ),
        LiveChannel(
            id = "times_now_navbharat",
            name = "Times Now Navbharat",
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a2/Times_Now_Navbharat.svg/1200px-Times_Now_Navbharat.svg.png",
            streamUrl = "https://raw.githubusercontent.com/amazeyourself/adaptive-streams/refs/heads/main/streams/in/YuppTV/TimesNowNavbharat.m3u8",
            category = "News",
            resolution = "1080p FHD",
            currentProgram = "Sawaal Public Ka & Ground Level Reports"
        ),
        LiveChannel(
            id = "news18_india",
            name = "News18 India",
            logoUrl = "https://xstreamcp-assets-msp.streamready.in/assets/LIVETV/LIVECHANNEL/LIVETV_LIVETVCHANNEL_NEWS18_INDIA/images/LOGO_HD/image.png",
            streamUrl = "https://n18syndication.akamaized.net/bpk-tv/News18_India_NW18_MOB/output01/master.m3u8",
            category = "News",
            resolution = "1080p FHD",
            currentProgram = "Desh Ka Mudda & Aar Paar Debate"
        ),
        LiveChannel(
            id = "ndtv_profit",
            name = "NDTV Profit",
            logoUrl = "https://upload.wikimedia.org/wikipedia/en/thumb/1/18/NDTV_Profit.svg/1280px-NDTV_Profit.svg.png",
            streamUrl = "https://ndtvprofit.akamaized.net/hls/live/2107404/ndtvprofit/master_1.m3u8",
            category = "News",
            resolution = "1080p FHD",
            currentProgram = "Sensex, Nifty, Stock Market & Global Economy"
        ),

        // 4. Sports
        LiveChannel(
            id = "eurosport_hd",
            name = "Eurosport",
            logoUrl = "https://dtil.tmsimg.com/assets/GNLZZGG0001099.png",
            streamUrl = "https://drk6xq0vhn.gpcdn.net/live/euro_sports_hd_abr/index.m3u8",
            category = "Sports",
            resolution = "720p HD",
            currentProgram = "Grand Slam Tennis, Cycling & Live World Championships"
        ),
        LiveChannel(
            id = "acc_sports",
            name = "ACC Sports Live",
            logoUrl = "https://i.imgur.com/V6Kaqha.png",
            streamUrl = "https://raycom-accdn-firetv.amagi.tv/playlist.m3u8",
            category = "Sports",
            resolution = "1080p FHD",
            currentProgram = "Championship Football, Basketball & Match Highlights"
        ),
        LiveChannel(
            id = "aspor_hd",
            name = "A Spor HD",
            logoUrl = "https://i.imgur.com/ZhkZzLf.png",
            streamUrl = "https://rnttwmjcin.turknet.ercdn.net/lcpmvefbyo/aspor/aspor.m3u8",
            category = "Sports",
            resolution = "1080p HD",
            currentProgram = "European Football Tournaments & Studio Live"
        ),
        LiveChannel(
            id = "africa_24_sport",
            name = "Africa 24 Sport",
            logoUrl = "https://i0.wp.com/africa24tv.com/wp-content/uploads/2023/12/LOGO-AFRICASPORT-4-HD-sans-fond.png?fit=512%2C107&ssl=1",
            streamUrl = "https://africa24.vedge.infomaniak.com/livecast/ik:africa24sport/manifest.m3u8",
            category = "Sports",
            resolution = "1080p HD",
            currentProgram = "Global Athletics, Football Leagues & Combat Sports"
        ),
        LiveChannel(
            id = "golf_kingdom",
            name = "30A Golf Kingdom",
            logoUrl = "https://i.imgur.com/Lv53nh4.png",
            streamUrl = "https://30a-tv.com/feeds/vidaa/golf.m3u8",
            category = "Sports",
            resolution = "720p HD",
            currentProgram = "PGA Tour Insights & Golf Championship Replays"
        ),

        // 5. Music
        LiveChannel(
            id = "9x_jalwa",
            name = "9X Jalwa",
            logoUrl = "https://xstreamcp-assets-msp.streamready.in/assets/LIVETV/LIVECHANNEL/LIVETV_LIVETVCHANNEL_9X_JALWA/images/LOGO_HD/image.png",
            streamUrl = "https://wiselp.wiseplayout.com/9X_Jalwa/master.m3u8",
            category = "Music",
            resolution = "1080p FHD",
            currentProgram = "Evergreen Bollywood Hits & Nostalgic Melodies"
        ),
        LiveChannel(
            id = "9x_jhakaas",
            name = "9X Jhakaas",
            logoUrl = "https://dtil.tmsimg.com/assets/s90012_ld_h15_aa.png?lock=720x540",
            streamUrl = "https://amg01281-9xmediapvtltd-9xjhakaas-samsungin-ci2cs.amagi.tv/playlist/amg01281-9xmediapvtltd-9xjhakaas-samsungin/playlist.m3u8",
            category = "Music",
            resolution = "1080p FHD",
            currentProgram = "High-Energy Regional & Party Anthems"
        ),
        LiveChannel(
            id = "mastiii",
            name = "Mastiii",
            logoUrl = "https://xstreamcp-assets-msp.streamready.in/assets/LIVETV/LIVECHANNEL/LIVETV_LIVETVCHANNEL_MASTII/images/LOGO_HD/image.png",
            streamUrl = "https://mumbai-edge.smartplaytv.in/MastiMusic/index.m3u8",
            category = "Music",
            resolution = "576p SD",
            currentProgram = "Non-Stop Bollywood Chartbusters & Dance Beats"
        ),
        LiveChannel(
            id = "shemaroo_filmi_gaane",
            name = "Shemaroo Filmi Gaane",
            logoUrl = "https://i.imgur.com/iR3G6L6.png",
            streamUrl = "https://prod-runn.cdn.runn.tv/shemaroo/stream/smrfgn/playlist.m3u8",
            category = "Music",
            resolution = "1080p FHD",
            currentProgram = "Golden Era Hindi Film Songs & Melodies"
        ),
        LiveChannel(
            id = "7s_music",
            name = "7S Music",
            logoUrl = "https://i.imgur.com/zDiIhdN.png",
            streamUrl = "https://cdn.pishow.tv/ott/live/1257/master.m3u8",
            category = "Music",
            resolution = "576p SD",
            currentProgram = "Indie Beats, Pop Top 40 & DJ Sets"
        ),

        // 6. Kids
        LiveChannel(
            id = "anime_hidive",
            name = "Anime x HIDIVE",
            logoUrl = "https://i.imgur.com/v0BlxCa.png",
            streamUrl = "https://jmp2.uk/plu-6793eaa4bc03978b9bc63db1.m3u8",
            category = "Kids",
            resolution = "1080p FHD",
            currentProgram = "Exclusive Anime Premieres, Isekai & Shonen"
        ),
        LiveChannel(
            id = "cartoon_classics",
            name = "Cartoon Classics",
            logoUrl = "https://images-3.rakuten.tv/storage/global-live-channel/translation/artwork/3227c06e-333b-4b1b-b657-3e3ab99ebd06-width200-quality90.jpeg",
            streamUrl = "https://daiconnect.com/live/hls/tvup/rk-cartoonclassics/578f4b7eb725168349ec0af81b21d388/index.m3u8",
            category = "Kids",
            resolution = "720p HD",
            currentProgram = "Looney Tunes, Popeye, Betty Boop & Classic Toons"
        ),
        LiveChannel(
            id = "3abn_kids",
            name = "3ABN Kids TV",
            logoUrl = "https://i.imgur.com/z3npqO1.png",
            streamUrl = "https://3abn.bozztv.com/3abn2/Kids_live/smil:Kids_live.smil/playlist.m3u8",
            category = "Kids",
            resolution = "720p HD",
            currentProgram = "Creative Animation, Storytime & Kids Science"
        ),

        // 7. Documentary
        LiveChannel(
            id = "discovery_channel_hd",
            name = "Discovery Channel",
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/23/Discovery_Channel_2019.svg/1200px-Discovery_Channel_2019.svg.png",
            streamUrl = "https://drk6xq0vhn.gpcdn.net/live/discovery_hd_abr/index.m3u8",
            category = "Documentary",
            resolution = "720p HD",
            currentProgram = "Science, Survival, Wild Expedition & Engineering"
        ),
        LiveChannel(
            id = "animal_planet_hd",
            name = "Animal Planet",
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/20/Animal_Planet_2018.svg/1200px-Animal_Planet_2018.svg.png",
            streamUrl = "https://drk6xq0vhn.gpcdn.net/live/animal_planet_hd_abr/index.m3u8",
            category = "Documentary",
            resolution = "720p HD",
            currentProgram = "Wild Predators, Ocean Life & Wildlife Safaris"
        ),
        LiveChannel(
            id = "nasa_tv_hd",
            name = "NASA TV",
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e5/NASA_logo.svg/1200px-NASA_logo.svg.png",
            streamUrl = "https://ntv1.akamaized.net/hls/live/2014075/NASA-NTV1-HLS/master.m3u8",
            category = "Documentary",
            resolution = "1080p FHD",
            currentProgram = "Space Station Live Feeds, Artemis & Rocket Launches",
            country = "US"
        ),

        // 8. Devotional
        LiveChannel(
            id = "aastha_hd",
            name = "Aastha",
            logoUrl = "https://i.imgur.com/IqgrV92.png",
            streamUrl = "https://aasthaott.akamaized.net/110923/smil:aasthatv.smil/index.m3u8",
            category = "Devotional",
            resolution = "720p HD",
            currentProgram = "Morning Aarti, Vedic Chants & Pravachan"
        ),
        LiveChannel(
            id = "sanskar_tv",
            name = "Sanskar",
            logoUrl = "https://upload.wikimedia.org/wikipedia/en/thumb/5/52/Sanskar_TV_logo.png/800px-Sanskar_TV_logo.png",
            streamUrl = "https://d26idhjf0y1p2g.cloudfront.net/out/v1/cd66dd25b9774cb29943bab54bbf3e2f/index.m3u8",
            category = "Devotional",
            resolution = "1080p FHD",
            currentProgram = "Bhajan Sandhya, Ramayan Katha & Darshan"
        ),
        LiveChannel(
            id = "sadhna_tv",
            name = "Sadhna TV",
            logoUrl = "https://jiotvimages.cdn.jio.com/dare_images/images/Sadhna.png",
            streamUrl = "https://6n3yow8pl9ok-hls-live.5centscdn.com/sadhanalivetv/live.stream/playlist.m3u8",
            category = "Devotional",
            resolution = "720p HD",
            currentProgram = "Spiritual Discourses, Satsang & Kundalini Yoga"
        ),
        LiveChannel(
            id = "dd_bharati",
            name = "DD Bharati",
            logoUrl = "https://i.imgur.com/4tfUIEo.png",
            streamUrl = "https://cdn.pishow.tv/ott/live/10/master.m3u8",
            category = "Devotional",
            resolution = "720p HD",
            currentProgram = "Indian Heritage, Classical Dance, Epics & Music"
        ),
        LiveChannel(
            id = "mh_one_shraddha",
            name = "MH One Shraddha",
            logoUrl = "https://streams.tangotv.in/MHONEDILSE/ORIGIN/index.m3u8",
            streamUrl = "https://streams.tangotv.in/MHONEDILSE/ORIGIN/index.m3u8",
            category = "Devotional",
            resolution = "576p SD",
            currentProgram = "Gurbani, Kirtan & Devotional Bhajans"
        ),

        // 9. Marathi
        LiveChannel(
            id = "fakt_marathi",
            name = "Fakt Marathi",
            logoUrl = "https://mumt07.tangotv.in/zHjX9OFlFAKTMARATHI/index.m3u8",
            streamUrl = "https://mumt07.tangotv.in/zHjX9OFlFAKTMARATHI/index.m3u8",
            category = "Marathi",
            resolution = "576p SD",
            currentProgram = "Marathi Movies, Dramas & Cultural Shows"
        ),
        LiveChannel(
            id = "news18_lokmat",
            name = "News18 Lokmat",
            logoUrl = "https://xstreamcp-assets-msp.streamready.in/assets/LIVETV/LIVECHANNEL/LIVETV_LIVETVCHANNEL_NEWS18_LOKMAT/images/LOGO_HD/image.png",
            streamUrl = "https://n18syndication.akamaized.net/bpk-tv/News18_Lokmat_NW18_MOB/output01/master.m3u8",
            category = "Marathi",
            resolution = "1080p FHD",
            currentProgram = "Maharashtra Breaking News, Politics & Bulletins"
        ),

        // 10. Tamil
        LiveChannel(
            id = "news18_tamil_nadu",
            name = "News18 Tamil Nadu",
            logoUrl = "https://xstreamcp-assets-msp.streamready.in/assets/LIVETV/LIVECHANNEL/LIVETV_LIVETVCHANNEL_NEWS18_TAMIL_NADU/images/LOGO_HD/image.png",
            streamUrl = "https://n18syndication.akamaized.net/bpk-tv/News18_Tamil_Nadu_NW18_MOB/output01/master.m3u8",
            category = "Tamil",
            resolution = "1080p FHD",
            currentProgram = "Tamil Nadu Top Headlines, Debates & Bulletins"
        ),

        // 11. Telugu
        LiveChannel(
            id = "10_tv_telugu",
            name = "10 TV Telugu",
            logoUrl = "https://xstreamcp-assets-msp.streamready.in/assets/LIVETV/LIVECHANNEL/LIVETV_LIVETVCHANNEL_10TV/images/LOGO_HD/image.png",
            streamUrl = "https://mumbai-edge.smartplaytv.in/10TV/index.m3u8",
            category = "Telugu",
            resolution = "720p HD",
            currentProgram = "Andhra & Telangana Fast News & Political Debates"
        ),

        // 12. Kannada
        LiveChannel(
            id = "news18_kannada",
            name = "News18 Kannada",
            logoUrl = "https://i.imgur.com/3XVFxdO.png",
            streamUrl = "https://n18syndication.akamaized.net/bpk-tv/News18_Kannada_NW18_MOB/output01/master.m3u8",
            category = "Kannada",
            resolution = "1080p FHD",
            currentProgram = "Karnataka Regional Headlines & Prime Discussions"
        ),

        // 13. Bengali
        LiveChannel(
            id = "news18_bangla",
            name = "News18 Bangla",
            logoUrl = "https://xstreamcp-assets-msp.streamready.in/assets/LIVETV/LIVECHANNEL/LIVETV_LIVETVCHANNEL_NEWS18_BANGLA/images/LOGO_HD/image.png",
            streamUrl = "https://n18syndication.akamaized.net/bpk-tv/News18_Bangla_NW18_MOB/output01/master.m3u8",
            category = "Bengali",
            resolution = "1080p FHD",
            currentProgram = "Kolkata & West Bengal Top Stories & Analysis"
        ),

        // 14. Malayalam
        LiveChannel(
            id = "mazhavil_manorama_hd",
            name = "Mazhavil Manorama",
            logoUrl = "https://mumt03.tangotv.in/Dsly5z3HMAZHAVILMANORAMAINT/index.m3u8",
            streamUrl = "https://mmtv-vglivessai.akamaized.net/v1/master/673630b269b766886555eebfddd4f27f3de3ab50/f8a0827f-030f-4a0d-b7e5-338996c09a5b/index.m3u8",
            category = "Malayalam",
            resolution = "1080p FHD",
            currentProgram = "Malayalam Entertainment, Serials & Reality Shows"
        ),
        LiveChannel(
            id = "24_news_malayalam",
            name = "24 News",
            logoUrl = "https://sund-images.sunnxt.com/202222/300x300_24News_202222_d63feca0-79ae-47ea-b75a-66c17d456f4c.png",
            streamUrl = "https://mumt07.tangotv.in/zHjX9OFlTWENTYFOURNEWS/index.m3u8",
            category = "Malayalam",
            resolution = "576p SD",
            currentProgram = "24x7 Kerala Breaking News & Current Affairs"
        ),

        // 15. Punjabi
        LiveChannel(
            id = "ptc_punjabi",
            name = "PTC Punjabi",
            logoUrl = "https://streams.tangotv.in/PTCPUNJABI/ORIGIN/index.m3u8",
            streamUrl = "https://d3qs3d2rkhfqrt.cloudfront.net/out/v1/3e22a9c278db4e3eb779afd42e41b0a6/index.m3u8",
            category = "Punjabi",
            resolution = "1080p FHD",
            currentProgram = "Majhe Di Awaz, Punjabi Serials & Cultural Music"
        ),
        LiveChannel(
            id = "pitaara_tv",
            name = "Pitaara TV",
            logoUrl = "https://vg-pitaaratvlive.akamaized.net/v1/master/611d79b11b77e2f571934fd80ca1413453772ac7/vglive-sk-583798/playlist.m3u8",
            streamUrl = "https://vg-pitaaratvlive.akamaized.net/v1/master/611d79b11b77e2f571934fd80ca1413453772ac7/vglive-sk-583798/playlist.m3u8",
            category = "Punjabi",
            resolution = "720p HD",
            currentProgram = "Blockbuster Punjabi Cinema, Comedy & Songs"
        ),
        LiveChannel(
            id = "9x_tashan",
            name = "9X Tashan",
            logoUrl = "https://xstreamcp-assets-msp.streamready.in/assets/LIVETV/LIVECHANNEL/LIVETV_LIVETVCHANNEL_9X_TASHAN/images/LOGO_HD/image.png",
            streamUrl = "https://amg01281-9xmediapvtltd-9xtashan-samsungin-xz1sd.amagi.tv/playlist/amg01281-9xmediapvtltd-9xtashan-samsungin/playlist.m3u8",
            category = "Punjabi",
            resolution = "1080p FHD",
            currentProgram = "Punjabi Chartbusters & Swag Bangers"
        ),
        LiveChannel(
            id = "news18_punjab",
            name = "News18 Punjab",
            logoUrl = "https://xstreamcp-assets-msp.streamready.in/assets/LIVETV/LIVECHANNEL/LIVETV_LIVETVCHANNEL_NEWS18_PUNJAB_HARYANA/images/LOGO_HD/image.png",
            streamUrl = "https://n18syndication.akamaized.net/bpk-tv/News18_Punjab_Haryana_HP_NW18_MOB/output01/master.m3u8",
            category = "Punjabi",
            resolution = "1080p FHD",
            currentProgram = "Punjab, Haryana & Himachal Regional Bulletins"
        ),

        // 16. International
        LiveChannel(
            id = "cnn_news18",
            name = "CNN-News18",
            logoUrl = "https://dtil.tmsimg.com/assets/s142747_ld_h15_aa.png?lock=720x540",
            streamUrl = "https://n18syndication.akamaized.net/bpk-tv/CNN_News18_NW18_MOB/output01/master.m3u8",
            category = "International",
            resolution = "1080p FHD",
            currentProgram = "India & Global Primetime Analysis"
        ),
        LiveChannel(
            id = "wion_news",
            name = "WION",
            logoUrl = "https://i.imgur.com/Wc5Z3iS.png",
            streamUrl = "http://vg-zeefta.akamaized.net/ptnr-yupptv/title-wion/v1/master/611d79b11b77e2f571934fd80ca1413453772ac7/20c3c0d9-0256-43fe-bca6-70fdd490b957/main.m3u8",
            category = "International",
            resolution = "1080p FHD",
            currentProgram = "World Is One News • Global Perspectives"
        ),
        LiveChannel(
            id = "dw_english",
            name = "DW English",
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/75/Deutsche_Welle_symbol_2012.svg/1200px-Deutsche_Welle_symbol_2012.svg.png",
            streamUrl = "https://dwamdstream102.akamaized.net/hls/live/2015525/dwstream102/index.m3u8",
            category = "International",
            resolution = "1080p FHD",
            currentProgram = "European & World Investigative News",
            country = "DE"
        ),
        LiveChannel(
            id = "bloomberg_tv",
            name = "Bloomberg Television",
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/56/Bloomberg_Television_logo.svg/1200px-Bloomberg_Television_logo.svg.png",
            streamUrl = "https://www.bloomberg.com/media-manifest/streams/us.m3u8",
            category = "International",
            resolution = "1080p FHD",
            currentProgram = "Wall Street, Global Markets & Economics",
            country = "US"
        )
    )

    fun getChannelsByCategory(category: String): List<LiveChannel> {
        return when (category) {
            "All Channels" -> AllChannels
            "📺 Hindi Entertainment" -> AllChannels.filter { it.category == "Hindi Entertainment" || it.category == "Entertainment" }
            "🎬 Hindi Movies" -> AllChannels.filter { it.category == "Hindi Movies" || it.category == "Movies" }
            "📰 News" -> AllChannels.filter { it.category == "News" }
            "🏏 Sports" -> AllChannels.filter { it.category == "Sports" }
            "🎵 Music" -> AllChannels.filter { it.category == "Music" }
            "👶 Kids" -> AllChannels.filter { it.category == "Kids" || it.category == "Kids & Anime" }
            "🌍 Documentary" -> AllChannels.filter { it.category == "Documentary" }
            "🕉️ Devotional" -> AllChannels.filter { it.category == "Devotional" }
            "🚩 Marathi" -> AllChannels.filter { it.category == "Marathi" }
            "🌴 Tamil" -> AllChannels.filter { it.category == "Tamil" }
            "🏛️ Telugu" -> AllChannels.filter { it.category == "Telugu" }
            "🌾 Kannada" -> AllChannels.filter { it.category == "Kannada" }
            "🎨 Bengali" -> AllChannels.filter { it.category == "Bengali" }
            "🥥 Malayalam" -> AllChannels.filter { it.category == "Malayalam" }
            "🚜 Punjabi" -> AllChannels.filter { it.category == "Punjabi" }
            "🌐 International" -> AllChannels.filter { it.category == "International" }
            else -> AllChannels.filter { 
                val clean = category.replace(Regex("""[^\w\s&]"""), "").trim()
                it.category.contains(clean, ignoreCase = true) 
            }
        }
    }
}
