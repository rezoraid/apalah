package com.komikta.app.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.compose.ui.platform.LocalContext
import android.net.Uri
import com.komikta.app.data.LocalLibrary
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.komikta.app.data.Comic
import com.komikta.app.data.KomiktaRepository
import kotlinx.coroutines.launch

private val bg = Color(0xFF080808)
private val surface = Color(0xFF171719)
private val muted = Color(0xFFA1A1AA)
private val red = Color(0xFFE63846)

@Composable
fun KomiktaApp(vm: AppViewModel = viewModel()) {
    val nav = rememberNavController()
    var selected by remember { mutableStateOf("Beranda") }
    Scaffold(
        containerColor = bg,
        bottomBar = {
            NavigationBar(containerColor = Color(0xF20E0E10), contentColor = Color.White, tonalElevation = 0.dp) {
                val destinations = listOf(
                    Triple("Beranda", Icons.Default.Home, "home"),
                    Triple("Jelajahi", Icons.Default.Explore, "explore"),
                    Triple("Koleksi", Icons.Default.Bookmarks, "library"),
                    Triple("Profil", Icons.Default.PersonOutline, "profile")
                )
                destinations.forEach { (label, icon, route) ->
                    NavigationBarItem(
                        selected = selected == label,
                        onClick = {
                            selected = label
                            when (route) {
                                "home" -> nav.navigate("home") { launchSingleTop = true; popUpTo("home") }
                                "explore" -> nav.navigate("explore") { launchSingleTop = true }
                                "library" -> nav.navigate("library") { launchSingleTop = true }
                                else -> nav.navigate("profile") { launchSingleTop = true }
                            }
                        },
                        icon = { Icon(icon, null) },
                        label = { Text(label, fontSize = 11.sp, fontWeight = if (selected == label) FontWeight.SemiBold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.White, selectedTextColor = Color.White, indicatorColor = Color(0xFF303034), unselectedIconColor = muted, unselectedTextColor = muted)
                    )
                }
            }
        }
    ) { padding ->
        NavHost(navController = nav, startDestination = "home", modifier = Modifier.padding(padding)) {
            composable("home") {
                HomeScreen(vm, onOpen = { comic -> nav.navigate("detail/${enc(comic.source)}/${enc(comic.sourceId.ifBlank { comic.id.substringAfter(":") })}/${enc(comic.title)}") })
            }
            composable("explore") {
                ExploreScreen(vm, onOpen = { comic -> nav.navigate("detail/${enc(comic.source)}/${enc(comic.sourceId.ifBlank { comic.id.substringAfter(":") })}/${enc(comic.title)}") })
            }
            composable("library") {
                LibraryScreen(onOpen = { comic -> nav.navigate("detail/${enc(comic.source)}/${enc(comic.sourceId.ifBlank { comic.id.substringAfter(":") })}/${enc(comic.title)}") })
            }
            composable("profile") { SimpleScreen("Profil", "Komikta · versi 1.0.0") }
            composable("detail/{source}/{id}/{title}", arguments = listOf(
                navArgument("source") { type = NavType.StringType },
                navArgument("id") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType }
            )) { entry ->
                val source = entry.arguments?.getString("source").orEmpty()
                val id = entry.arguments?.getString("id").orEmpty()
                val title = entry.arguments?.getString("title").orEmpty()
                DetailScreen(source, id, title, onBack = { nav.popBackStack() }, onRead = { chapter ->
                    nav.navigate("reader/${enc(source)}/${enc(id)}/${enc(chapter)}/${enc(title)}")
                })
            }
            composable("reader/{source}/{id}/{chapter}/{title}", arguments = listOf(
                navArgument("source") { type = NavType.StringType },
                navArgument("id") { type = NavType.StringType },
                navArgument("chapter") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType }
            )) { entry ->
                ReaderScreen(
                    entry.arguments?.getString("source").orEmpty(),
                    entry.arguments?.getString("id").orEmpty(),
                    entry.arguments?.getString("chapter").orEmpty(),
                    entry.arguments?.getString("title").orEmpty(),
                    onBack = { nav.popBackStack() },
                    onChapterChange = { nextChapter ->
                        nav.navigate("reader/${enc(entry.arguments?.getString("source").orEmpty())}/${enc(entry.arguments?.getString("id").orEmpty())}/${enc(nextChapter)}/${enc(entry.arguments?.getString("title").orEmpty())}")
                    }
                )
            }
        }
    }
}

private fun enc(s: String) = Uri.encode(s)

@Composable
private fun HomeScreen(vm: AppViewModel, onOpen: (Comic) -> Unit) {
    val state by vm.home.collectAsState()
    val featured = state.popular.firstOrNull() ?: state.latest.firstOrNull()
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(bg),
        contentPadding = PaddingValues(bottom = 18.dp)
    ) {
        item { TopBar() }
        item {
            if (featured != null) HeroBanner(featured, onRead = { onOpen(featured) }, onDetail = { onOpen(featured) })
            else if (state.loading) Box(Modifier.fillMaxWidth().height(330.dp).background(Color(0xFF171217)), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = red, strokeWidth = 2.dp)
            } else ErrorHero(state.error ?: "Belum ada konten", onRetry = vm::loadHome)
        }
        if (state.error != null && featured == null) item { ErrorLine(state.error!!, vm::loadHome) }
        if (state.latest.isNotEmpty()) item { ComicSection("Update", state.latest, onOpen) }
        if (state.popular.isNotEmpty()) item { ComicSection("Populer", state.popular, onOpen) }
        if (state.fresh.isNotEmpty()) item { ComicSection("Baru ditambahkan", state.fresh, onOpen) }
        if (!state.loading && state.latest.isEmpty() && state.popular.isEmpty() && state.fresh.isEmpty()) {
            item { EmptyState("Belum ada komik", "Server belum mengembalikan katalog. Coba muat ulang.", vm::loadHome) }
        }
    }
}

@Composable
private fun TopBar() {
    Row(
        Modifier.fillMaxWidth().background(bg).statusBarsPadding().height(60.dp).padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(Modifier.size(34.dp).clip(RoundedCornerShape(9.dp)).background(red), contentAlignment = Alignment.Center) {
                Text("K", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color.White)
            }
            Spacer(Modifier.width(9.dp))
            Text("KOMIKTA", fontSize = 22.sp, fontWeight = FontWeight.Black, letterSpacing = (-0.8).sp, color = Color.White)
        }
        IconButton(onClick = {}) { Icon(Icons.Default.Search, "Cari", tint = Color.White, modifier = Modifier.size(25.dp)) }
        IconButton(onClick = {}) { Icon(Icons.Default.NotificationsNone, "Notifikasi", tint = Color.White, modifier = Modifier.size(24.dp)) }
    }
}

@Composable
private fun HeroBanner(comic: Comic, onRead: () -> Unit, onDetail: () -> Unit) {
    Box(Modifier.fillMaxWidth().height(360.dp)) {
        AsyncImage(
            model = comic.cover ?: comic.rawCover,
            contentDescription = comic.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(
            0f to Color(0x55000000), 0.28f to Color(0x33000000), 0.62f to Color(0xCC000000), 1f to bg
        )))
        Column(
            Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(start = 22.dp, end = 22.dp, bottom = 22.dp)
        ) {
            Text("PILIHAN KOMIK", color = Color(0xFFE5A6AA), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
            Spacer(Modifier.height(7.dp))
            Text(comic.title, color = Color.White, fontSize = 29.sp, lineHeight = 33.sp, fontWeight = FontWeight.ExtraBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(7.dp))
            Text(comic.genres.take(3).joinToString(", ").ifBlank { comic.type?.replaceFirstChar { it.uppercase() } ?: "Manhwa" }, color = Color(0xFFD4D4D8), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(15.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (comic.rating?.value != null) MetaText("★ ${comic.rating.value}")
                comic.latestChapter?.let { MetaText("Ch. $it") }
                MetaText(comic.source.uppercase())
            }
            Spacer(Modifier.height(17.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = onRead, shape = RoundedCornerShape(28.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black), contentPadding = PaddingValues(horizontal = 23.dp, vertical = 11.dp)) {
                    Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(19.dp)); Spacer(Modifier.width(5.dp)); Text("Baca", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(onClick = onDetail, shape = RoundedCornerShape(28.dp), border = BorderStroke(1.dp, Color(0xFF68686D)), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White), contentPadding = PaddingValues(horizontal = 23.dp, vertical = 11.dp)) {
                    Text("Detail", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable private fun MetaText(text: String) {
    Text(text, color = Color(0xFFE4E4E7), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
}

@Composable
private fun ComicSection(title: String, comics: List<Comic>, onOpen: (Comic) -> Unit) {
    Column(Modifier.fillMaxWidth().padding(top = 23.dp)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(title, fontSize = 21.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(1f))
            Text("Lihat semua", fontSize = 12.sp, color = muted)
        }
        Spacer(Modifier.height(13.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(13.dp)) {
            items(comics, key = { it.id.ifBlank { it.title } }) { comic ->
                ComicCard(comic, onClick = { onOpen(comic) })
            }
        }
    }
}

@Composable
private fun ComicCard(comic: Comic, onClick: () -> Unit) {
    Column(Modifier.width(148.dp).clickable(onClick = onClick)) {
        Box(Modifier.fillMaxWidth().height(212.dp).clip(RoundedCornerShape(10.dp)).background(surface)) {
            AsyncImage(
                model = comic.cover ?: comic.rawCover,
                contentDescription = comic.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            comic.latestChapter?.let {
                Text("Ch. $it", Modifier.align(Alignment.TopStart).padding(7.dp).clip(RoundedCornerShape(5.dp)).background(Color(0xD9101012)).padding(horizontal = 7.dp, vertical = 4.dp), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            comic.rating?.value?.let {
                Text("★ ${"%.1f".format(it)}", Modifier.align(Alignment.TopEnd).padding(7.dp).clip(RoundedCornerShape(5.dp)).background(Color(0xD9101012)).padding(horizontal = 6.dp, vertical = 4.dp), color = Color(0xFFFFD66B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(comic.title, fontSize = 13.sp, lineHeight = 17.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFF4F4F5), maxLines = 2, overflow = TextOverflow.Ellipsis, minLines = 2)
        Text(comic.type?.replaceFirstChar { it.uppercase() } ?: comic.source.replaceFirstChar { it.uppercase() }, fontSize = 11.sp, color = muted, maxLines = 1)
    }
}

@Composable
private fun ExploreScreen(vm: AppViewModel, onOpen: (Comic) -> Unit) {
    var query by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("manhwa") }
    val comics by vm.catalog.collectAsState()
    val busy by vm.busy.collectAsState()
    val error by vm.error.collectAsState()
    Column(Modifier.fillMaxSize().background(bg).statusBarsPadding()) {
        Text("Jelajahi komik", Modifier.padding(start = 20.dp, top = 16.dp, bottom = 15.dp), fontSize = 27.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Row(Modifier.weight(1f).height(48.dp).clip(RoundedCornerShape(12.dp)).background(surface).padding(horizontal = 13.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Search, null, tint = muted)
                Spacer(Modifier.width(8.dp))
                BasicTextField(value = query, onValueChange = { query = it }, singleLine = true, textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 14.sp), cursorBrush = androidx.compose.ui.graphics.SolidColor(red), modifier = Modifier.weight(1f), decorationBox = { inner ->
                    if (query.isEmpty()) Text("Cari judul manhwa...", color = muted, fontSize = 14.sp)
                    inner()
                })
                if (query.isNotEmpty()) IconButton(onClick = { query = "" }, modifier = Modifier.size(28.dp)) { Icon(Icons.Default.Close, null, tint = muted) }
            }
            Spacer(Modifier.width(8.dp))
            Button(onClick = { if (query.isNotBlank()) vm.search(query) }, shape = RoundedCornerShape(12.dp), contentPadding = PaddingValues(horizontal = 15.dp, vertical = 12.dp)) { Text("Cari") }
        }
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("manhwa", "manga", "manhua").forEach { type ->
                FilterChip(selected = selectedType == type, onClick = { selectedType = type; vm.loadType(type) }, label = { Text(type.replaceFirstChar { it.uppercase() }) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = red, selectedLabelColor = Color.White, labelColor = muted))
            }
        }
        when {
            busy -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = red) }
            error != null -> Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text(error!!, color = muted); TextButton(onClick = { if (query.isNotBlank()) vm.search(query) else vm.loadType(selectedType) }) { Text("Coba lagi") } }
            comics.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Cari judul atau pilih kategori untuk mulai.", color = muted) }
            else -> LazyVerticalGrid(columns = GridCells.Adaptive(145.dp), contentPadding = PaddingValues(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(items = comics, key = { comic -> comic.id.ifBlank { comic.title } }) { comic ->
                    ComicCard(comic, onClick = { onOpen(comic) })
                }
            }
        }
    }
}

@Composable
private fun DetailScreen(source: String, id: String, title: String, onBack: () -> Unit, onRead: (String) -> Unit) {
    var comic by remember { mutableStateOf<Comic?>(null) }
    var chapters by remember { mutableStateOf<List<com.komikta.app.data.Chapter>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(source, id) {
        loading = true
        KomiktaRepository().detail(Comic(source = source, sourceId = id, id = "$source:$id", title = title)).fold(
            { comic = it.comic; chapters = it.chapters; error = null },
            { error = it.message ?: "Detail gagal dimuat" }
        )
        loading = false
    }
    Column(Modifier.fillMaxSize().background(bg).statusBarsPadding()) {
        Row(Modifier.fillMaxWidth().height(52.dp).padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Kembali", tint = Color.White) }
            Text("Detail komik", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(1f))
            val context = LocalContext.current
            var saved by remember(source, id) { mutableStateOf(LocalLibrary.isBookmarked(context, "$source:$id")) }
            IconButton(onClick = {
                val current = comic ?: Comic(id = "$source:$id", source = source, sourceId = id, title = title)
                saved = LocalLibrary.toggleBookmark(context, current)
            }) {
                Icon(if (saved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder, "Simpan ke koleksi", tint = if (saved) red else Color.White)
            }
        }
        if (loading) Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = red) }
        else if (error != null) ErrorLine(error!!, { loading = true; error = null })
        else LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(bottom = 24.dp)) {
            item {
                Row(Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AsyncImage(model = comic?.cover ?: comic?.rawCover, contentDescription = title, contentScale = ContentScale.Crop, modifier = Modifier.width(132.dp).height(190.dp).clip(RoundedCornerShape(10.dp)).background(surface))
                    Column(Modifier.weight(1f)) {
                        Text(comic?.title ?: title, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        Spacer(Modifier.height(8.dp))
                        Text(listOfNotNull(comic?.type, comic?.status, comic?.author).joinToString(" · ").ifBlank { source }, color = muted, fontSize = 12.sp)
                        Spacer(Modifier.height(10.dp))
                        comic?.genres?.take(4)?.joinToString(" • ")?.let { Text(it, color = Color(0xFFE7A0A6), fontSize = 12.sp) }
                        Spacer(Modifier.height(14.dp))
                        Button(onClick = { chapters.firstOrNull()?.chapter?.let(onRead) }, enabled = chapters.isNotEmpty(), colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)) { Icon(Icons.Default.PlayArrow, null); Text(" Baca") }
                    }
                }
            }
            comic?.synopsis?.let { synopsis -> item { Text("Sinopsis", Modifier.padding(horizontal = 20.dp), color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold); Text(synopsis, Modifier.padding(horizontal = 20.dp, vertical = 8.dp), color = muted, fontSize = 14.sp, lineHeight = 21.sp) } }
            item { Text("Daftar chapter", Modifier.padding(start = 20.dp, top = 12.dp, bottom = 8.dp), color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) }
            items(chapters, key = { it.id.ifBlank { it.chapter } }) { chapter ->
                Row(Modifier.fillMaxWidth().clickable { onRead(chapter.chapter) }.padding(horizontal = 20.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(chapter.title.ifBlank { "Chapter ${chapter.chapter}" }, color = Color.White, fontWeight = FontWeight.Medium)
                        chapter.releaseDate?.let { Text(it, color = muted, fontSize = 11.sp) }
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = muted)
                }
                HorizontalDivider(color = Color(0xFF202024), modifier = Modifier.padding(horizontal = 20.dp))
            }
        }
    }
}

@Composable
private fun ReaderScreen(source: String, id: String, chapter: String, title: String, onBack: () -> Unit, onChapterChange: (String) -> Unit) {
    var reader by remember { mutableStateOf<com.komikta.app.data.ReaderData?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(source, id, chapter) {
        loading = true
        KomiktaRepository().chapter(Comic(source = source, sourceId = id, id = "$source:$id", title = title), chapter).fold(
            { reader = it; error = null },
            { error = it.message ?: "Chapter gagal dimuat" }
        )
        loading = false
    }
    Column(Modifier.fillMaxSize().background(Color.Black).statusBarsPadding()) {
        Row(Modifier.fillMaxWidth().height(52.dp).background(Color(0xFF101010)).padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Kembali", tint = Color.White) }
            Column(Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("Chapter $chapter", color = muted, fontSize = 11.sp)
            }
        }
        when {
            loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = red) }
            error != null -> ErrorLine(error!!, onRetry = { error = null; loading = true })
            reader?.chapter?.images?.isNotEmpty() == true -> LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 16.dp)) {
                items(reader!!.chapter.images, key = { it.index }) { page ->
                    AsyncImage(model = page.image, contentDescription = "Halaman ${page.index + 1}", contentScale = ContentScale.FillWidth, modifier = Modifier.fillMaxWidth().background(Color.Black))
                }
                item {
                    Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        TextButton(
                            enabled = reader?.chapter?.prevChapter != null,
                            onClick = { reader?.chapter?.prevChapter?.let(onChapterChange) }
                        ) { Text("Chapter sebelumnya") }
                        TextButton(
                            enabled = reader?.chapter?.nextChapter != null,
                            onClick = { reader?.chapter?.nextChapter?.let(onChapterChange) }
                        ) { Text("Chapter berikutnya") }
                    }
                }
            }
            reader?.chapter?.novelContent?.paragraphs?.isNotEmpty() == true -> LazyColumn(Modifier.fillMaxSize().padding(horizontal = 22.dp), contentPadding = PaddingValues(vertical = 20.dp)) {
                item { Text(reader!!.chapter.title, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold) }
                items(reader!!.chapter.novelContent!!.paragraphs) { paragraph -> Text(paragraph, color = Color(0xFFE4E4E7), fontSize = 16.sp, lineHeight = 27.sp, modifier = Modifier.padding(top = 16.dp)) }
            }
            else -> EmptyState("Chapter kosong", "Sumber tidak mengembalikan halaman untuk chapter ini.", onRetry = { loading = true })
        }
    }
}


@Composable
private fun LibraryScreen(onOpen: (Comic) -> Unit) {
    val context = LocalContext.current
    var bookmarks by remember { mutableStateOf(LocalLibrary.getBookmarks(context)) }
    Column(Modifier.fillMaxSize().background(bg).statusBarsPadding()) {
        Text("Koleksi kamu", Modifier.padding(start = 20.dp, top = 16.dp, bottom = 14.dp), fontSize = 27.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
        if (bookmarks.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(28.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.BookmarkBorder, null, tint = muted, modifier = Modifier.size(38.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("Belum ada koleksi", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text("Buka detail komik lalu tekan tombol simpan untuk menambahkannya di sini.", color = muted, fontSize = 13.sp)
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(145.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items = bookmarks, key = { comic -> comic.id }) { comic ->
                    Column {
                        ComicCard(comic, onClick = { onOpen(comic) })
                        TextButton(onClick = {
                            LocalLibrary.toggleBookmark(context, comic)
                            bookmarks = LocalLibrary.getBookmarks(context)
                        }) { Text("Hapus dari koleksi", color = muted, fontSize = 11.sp) }
                    }
                }
            }
        }
    }
}

@Composable private fun SimpleScreen(title: String, description: String) {
    Column(Modifier.fillMaxSize().background(bg).statusBarsPadding().padding(22.dp)) {
        Text(title, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
        Spacer(Modifier.height(12.dp))
        Text(description, color = muted, fontSize = 14.sp, lineHeight = 22.sp)
    }
}

@Composable private fun ErrorHero(message: String, onRetry: () -> Unit) {
    Column(Modifier.fillMaxWidth().height(300.dp).background(Color(0xFF171217)).padding(22.dp), verticalArrangement = Arrangement.Center) {
        Text("Komikta", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(8.dp)); Text(message, color = muted)
        TextButton(onClick = onRetry) { Text("Coba lagi") }
    }
}
@Composable private fun ErrorLine(message: String, onRetry: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.CloudOff, null, tint = muted, modifier = Modifier.size(30.dp))
        Spacer(Modifier.height(8.dp)); Text(message, color = muted, fontSize = 13.sp)
        TextButton(onClick = onRetry) { Text("Coba lagi", color = Color.White) }
    }
}
@Composable private fun EmptyState(title: String, description: String, onRetry: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(7.dp)); Text(description, color = muted, fontSize = 13.sp)
        TextButton(onClick = onRetry) { Text("Muat ulang") }
    }
}
