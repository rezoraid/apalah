# API route reference extracted from `manhwa-api.zip`

Base URL configured in the app:
`https://tets-server-production.up.railway.app/`

All JSON routes (except `/health` and image proxy) return `{ success: true, data: ... }` on success.

- `GET /health`
- `GET /api`
- `GET /api/sources`
- `GET /api/home?source=all&refresh=false`
- `GET /api/list?source=all&page=1`
- `GET /api/search?q=title&source=all&page=1`
- `GET /api/genres`
- `GET /api/genre/:genre?source=all&page=1`
- `GET /api/type/:type?source=all&page=1`
- `GET /api/comic/:source/:id`
- `GET /api/comic/:source/:id/chapters`
- `GET /api/comic/:source/:id/chapter/:chapter`
- `GET /api/image-proxy?url=...&source=...`

Source types listed in API README: `softkomik`, `komiku`, `luvyaa`, `noveldex`, `manta`, `lumos`, `shinigami`, and `all` for combined list operations. `id` is the item's `sourceId`; the chapter key may be a source-specific identifier. Fields not supplied by a source may be null or empty.
