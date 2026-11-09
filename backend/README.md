# MovieVortex Modular Backend Core Architecture

This directory contains the modular streaming and download execution components for MovieVortex.

---

## 📁 Architecture Overview

```
backend/
├── README.md                 # Backend documentation
├── library/                  # MovieVortex Multiplatform Library (Kotlin Multiplatform)
│   ├── src/
│   │   ├── commonMain/       # MainAPI, APIRepository, ExtractorLink, Extractors, SearchResponse
│   │   ├── androidMain/      # Android-specific bindings & context helpers
│   │   └── jvmMain/          # JVM runtime bindings
│   └── build.gradle.kts      # Library build configuration
└── cloudstream-core/         # Native Streaming & Player Subsystems
    ├── network/              # DNS over HTTPS, OkHttp clients, SSL handlers
    ├── plugins/              # Dynamic plugin loading & provider registry
    ├── services/             # DownloadQueueService, Background Download Workers
    ├── utils/                # VideoDownloadManager, DataStoreHelper, Coroutines
    ├── player/               # Native Hardware Player Engine (DownloadedPlayerActivity, GeneratorPlayer)
    ├── syncproviders/        # Media progress tracking APIs
    ├── subtitles/            # Subtitle decoders, parsers, offset management
    └── mvvm/                 # Resource wrappers and safe coroutine helpers
```

---

## ⚙️ Core Subsystems

### 1. Provider & Extractor Engine (`library/` & `cloudstream-core/plugins/`)
* **`MainAPI` / `APIRepository`**: Interfaces implemented by video sources with safe coroutine execution.
* **`PluginManager` / `RepositoryManager`**: Manages online provider repository caching and loading.

### 2. Video Player Core (`cloudstream-core/player/`)
* **`DownloadedPlayerActivity`**: Handles inline and full-screen video playback, audio track switching, aspect ratios, and subtitle synchronization.
* **`GeneratorPlayer`**: Handles link generator dispatches and offline playback.

### 3. In-App Download Manager (`cloudstream-core/utils/` & `services/`)
* **`VideoDownloadManager`**: Background downloading, byte tracking, pause/resume, and multi-part download pipelines.
* **`DownloadQueueManager`**: Persistent FIFO queue ensuring reliable background downloads.
