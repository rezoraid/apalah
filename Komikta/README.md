# Komikta — Native Android Manhwa Reader

Aplikasi native Android (Kotlin + Jetpack Compose) untuk membaca manhwa, manhua, dan manga.

**Nama:** Komikta  
**Tagline:** Baca cerita, temukan dunia.

## Fitur

- Home: hero promo, rekomendasi (popular), update terbaru
- Explore: filter genre + grid katalog
- Search: debounce + recent searches (local)
- Detail: synopsis, daftar chapter, bookmark
- Reader: vertical scroll long-strip + save reading progress
- Library: bookmark & riwayat baca (Room)
- Profile: info aplikasi
- Dark cinematic UI sesuai referensi desain

## Tech Stack

- Kotlin, Jetpack Compose, Material 3 (customized)
- MVVM + Repository
- Retrofit + OkHttp + Kotlin Serialization
- Coil (image loading)
- Room + DataStore-ready
- Navigation Compose
- Koin DI
- Coroutines + Flow

## API

Base URL: `https://tets-server-production.up.railway.app/`

### Verified endpoints

| Method | Route | Deskripsi |
|--------|-------|-----------|
| GET | `/api` | Daftar endpoint |
| GET | `/api/home?source=all` | popular, latest, new |
| GET | `/api/list?source=all&page=1` | Update terbaru |
| GET | `/api/search?q=...&source=all&page=1` | Pencarian |
| GET | `/api/genres` | Daftar genre |
| GET | `/api/genre/{genre}` | Filter by genre |
| GET | `/api/type/{type}` | Filter by type |
| GET | `/api/comic/{source}/{id}` | Detail + chapters |
| GET | `/api/comic/{source}/{id}/chapter/{chapter}` | Halaman chapter |
| GET | `/api/image-proxy?url=...&source=...` | Proxy gambar |

Tidak ada endpoint auth / notifikasi / download server-side. Bookmark & history disimpan lokal.

## Setup

1. Buka folder `Komikta` di **Android Studio** (Ladybug / 2024.2+ recommended).
2. Sync Gradle.
3. Run pada emulator / device (min SDK 26).

```bash
./gradlew :app:assembleDebug
```

APK debug: `app/build/outputs/apk/debug/app-debug.apk`

## Struktur package

```
com.komikta.app
├── data/
│   ├── remote/ (api, dto)
│   ├── local/  (Room)
│   ├── mapper/
│   └── repository/
├── domain/
│   ├── model/
│   └── repository/
├── ui/
│   ├── theme/
│   ├── components/
│   ├── navigation/
│   └── screens/
├── di/
└── util/
```

## Catatan

- Cleartext traffic diizinkan hanya untuk host API (image-proxy kadang mengembalikan `http://`).
- Logo: `res/drawable/ic_logo_k.png` dan `logo_komikta.png`.
- Build APK tidak dijalankan di environment ini karena Android SDK tidak tersedia; project siap di-sync & build di Android Studio.
