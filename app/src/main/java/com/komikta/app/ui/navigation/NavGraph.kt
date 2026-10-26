package com.komikta.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.komikta.app.ui.screens.detail.DetailScreen
import com.komikta.app.ui.screens.explore.ExploreScreen
import com.komikta.app.ui.screens.home.HomeScreen
import com.komikta.app.ui.screens.library.LibraryScreen
import com.komikta.app.ui.screens.profile.ProfileScreen
import com.komikta.app.ui.screens.reader.ReaderScreen
import com.komikta.app.ui.screens.search.SearchScreen
import com.komikta.app.ui.theme.Accent
import com.komikta.app.ui.theme.Background
import com.komikta.app.ui.theme.Surface
import com.komikta.app.ui.theme.TextMuted
import com.komikta.app.ui.theme.TextPrimary

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Explore : Screen("explore")
    data object Library : Screen("library")
    data object Profile : Screen("profile")
    data object Search : Screen("search")
    data object Detail : Screen("detail/{source}/{sourceId}") {
        fun create(source: String, sourceId: String) = "detail/$source/$sourceId"
    }
    data object Reader : Screen("reader/{source}/{sourceId}/{chapter}") {
        fun create(source: String, sourceId: String, chapter: String) =
            "reader/$source/$sourceId/$chapter"
    }
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, "Home", Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem(Screen.Explore, "Explore", Icons.Filled.Explore, Icons.Outlined.Explore),
    BottomNavItem(Screen.Library, "Library", Icons.Filled.LibraryBooks, Icons.Outlined.LibraryBooks),
    BottomNavItem(Screen.Profile, "Profile", Icons.Filled.Person, Icons.Outlined.Person)
)

@Composable
fun KomiktaNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = bottomNavItems.any { item ->
        currentDestination?.hierarchy?.any { it.route == item.screen.route } == true
    }

    Scaffold(
        containerColor = Background,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Surface,
                    tonalElevation = 0.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == item.screen.route
                        } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Accent,
                                selectedTextColor = Accent,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted,
                                indicatorColor = Surface
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onSearchClick = { navController.navigate(Screen.Search.route) },
                    onComicClick = { comic ->
                        navController.navigate(Screen.Detail.create(comic.source, comic.sourceId))
                    },
                    onSeeAllLatest = { navController.navigate(Screen.Explore.route) }
                )
            }
            composable(Screen.Explore.route) {
                ExploreScreen(
                    onComicClick = { comic ->
                        navController.navigate(Screen.Detail.create(comic.source, comic.sourceId))
                    },
                    onSearchClick = { navController.navigate(Screen.Search.route) }
                )
            }
            composable(Screen.Library.route) {
                LibraryScreen(
                    onComicClick = { comic ->
                        navController.navigate(Screen.Detail.create(comic.source, comic.sourceId))
                    },
                    onExploreClick = { navController.navigate(Screen.Explore.route) }
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen()
            }
            composable(Screen.Search.route) {
                SearchScreen(
                    onBack = { navController.popBackStack() },
                    onComicClick = { comic ->
                        navController.navigate(Screen.Detail.create(comic.source, comic.sourceId))
                    }
                )
            }
            composable(
                route = Screen.Detail.route,
                arguments = listOf(
                    navArgument("source") { type = NavType.StringType },
                    navArgument("sourceId") { type = NavType.StringType }
                )
            ) { entry ->
                val source = entry.arguments?.getString("source") ?: return@composable
                val sourceId = entry.arguments?.getString("sourceId") ?: return@composable
                DetailScreen(
                    source = source,
                    sourceId = sourceId,
                    onBack = { navController.popBackStack() },
                    onReadChapter = { chapter ->
                        navController.navigate(
                            Screen.Reader.create(source, sourceId, chapter.number)
                        )
                    }
                )
            }
            composable(
                route = Screen.Reader.route,
                arguments = listOf(
                    navArgument("source") { type = NavType.StringType },
                    navArgument("sourceId") { type = NavType.StringType },
                    navArgument("chapter") { type = NavType.StringType }
                )
            ) { entry ->
                val source = entry.arguments?.getString("source") ?: return@composable
                val sourceId = entry.arguments?.getString("sourceId") ?: return@composable
                val chapter = entry.arguments?.getString("chapter") ?: return@composable
                ReaderScreen(
                    source = source,
                    sourceId = sourceId,
                    chapter = chapter,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
