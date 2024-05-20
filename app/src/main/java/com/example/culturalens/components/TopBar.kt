package com.example.culturalens.components

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.IconToggleButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.culturalens.NavItem
import com.example.culturalens.ui.theme.CulturaLensTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavController,
    title: String = "CulturaLens",
    backRoute: String? = NavItem.Home.route,
    actions: @Composable() () -> Unit = {
        IconToggleButton(checked = false, onCheckedChange = { } ) {
            Icon(
                Icons.Default.Settings,
                "Settings",
                modifier = Modifier.size(OFFSET),
            )
        }
    },
) {
    CulturaLensTheme {
        TopAppBar(
            title = { Text(text = title) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colors.primary,
                titleContentColor = MaterialTheme.colors.onPrimary,
            ),
            navigationIcon = {
                if (backRoute != null) {
                    IconButton(
                        onClick = {
                            navController.navigate(backRoute) {
                                popUpTo(navController.graph.startDestinationId)
//                        {
//                            saveState = true
//                        }
                                launchSingleTop = true
//                        restoreState = true
                            }
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Default.ArrowBack,
                            "Back",
                            modifier = Modifier.size(OFFSET),
                        )
                    }
                }
            },
            actions = { actions() }
        )
    }
}
