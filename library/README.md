# MovieVortex Core Extension Library

This module defines the core provider API interfaces, extractors, and response models used by MovieVortex.

### Architecture
- **`MainAPI` / `APIRepository`**: Interfaces and query pipelines for resolving media metadata, episodes, and stream links.
- **`ExtractorLink`**: Model representing direct video streams, HLS manifests, and subtitles.
- **`SearchResponse` & `LoadResponse`**: Data models for search results and rich media details.
