# Verified API Endpoints

Base: `https://tets-server-production.up.railway.app`

All responses wrap as:
```json
{ "success": true|false, "data": ..., "error": { "code", "message" } }
```

## Health / Meta
- `GET /` or `GET /health` → status + sources
- `GET /api` → full endpoint list

## Catalog
- `GET /api/home?source=all&refresh=false`  
  → `{ popular: Comic[], latest: Comic[], new: Comic[], scrapedAt }`
- `GET /api/list?source=all&page=1` → `{ items: Comic[] }`
- `GET /api/search?q=judul&source=all&page=1` → `{ items: Comic[] }`
- `GET /api/genres` → `Genre[]`
- `GET /api/genre/{genre}?source=all&page=1` → `{ items: Comic[] }`
- `GET /api/type/{type}?source=all&page=1` → `{ items: Comic[] }`

## Detail & Reader
- `GET /api/comic/{source}/{id}` → `{ comic: Comic, chapters: Chapter[] }`
- `GET /api/comic/{source}/{id}/chapter/{chapter}` → `{ comic, chapter: { images: [{index, image, rawUrl}] } }`
- `GET /api/image-proxy?url=...&source=...` → image bytes (may redirect to http)

## Comic fields (observed)
`id`, `source`, `sourceId`, `slug`, `title`, `alternativeTitles`, `type`, `status`, `genres`, `author`, `artist`, `cover`, `rawCover`, `synopsis`, `latestChapter`, `rating: {value, count}`, `updatedAt`

## Not available on backend
- Login / user accounts
- Server-side bookmarks / reading history
- Push notifications
- Downloads

These are implemented locally with Room.
