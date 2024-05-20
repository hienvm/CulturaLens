package com.example.culturalens
import com.example.culturalens.pages.GalleryPage
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.Share
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.culturalens.pages.ProjectorPage
import com.example.culturalens.pages.AugmentedImagePage
import com.example.culturalens.ui.theme.CulturaLensTheme

@Composable
fun NavigationHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = NavItem.Home.route) {
        composable(NavItem.Home.route) {
            HomePage(navController)
        }
        composable(NavItem.Projector.route) {
            ProjectorPage(navController)
        }
        composable(NavItem.AugmentedImage.route) {
            AugmentedImagePage(navController)
        }
        composable(NavItem.Gallery.route) {
            GalleryPage(navController)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    CulturaLensTheme {
        BottomNavigation(
            backgroundColor = MaterialTheme.colors.primaryVariant,
            contentColor = MaterialTheme.colors.onPrimary
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            navItems.forEach { item ->
                BottomNavigationItem(
                    selected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    label = { Text(item.label) }
                )
            }
        }
    }
}

val navItems = listOf(
    NavItem.Home,
    NavItem.Projector,
    NavItem.AugmentedImage,
    NavItem.Gallery,
)

sealed class NavItem(val route: String, val icon: ImageVector, val label: String) {
    data object Home : NavItem("home", Icons.Default.Home, "Home")
    data object Projector : NavItem("projector", Icons.Default.Place, "Projector")
    data object AugmentedImage : NavItem("augmented_image", Icons.Default.Share, "AR Image")
    data object Gallery : NavItem("gallery", Icons.AutoMirrored.Default.List, "Gallery")
}

