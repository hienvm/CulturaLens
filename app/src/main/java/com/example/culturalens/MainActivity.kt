package com.example.culturalens

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.navigation.compose.rememberNavController
import com.example.culturalens.ui.theme.CulturaLensTheme

lateinit var activity: ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContent {
            CulturaLensTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { BottomNavigationBar(navController = navController) },
                ) {
                    Column(
                        modifier = Modifier
                            .padding(it)
                    ) {
                        NavigationHost(navController = navController)
                    }
                }
            }
        }
    }
}

@Preview(device = "id:small_phone", showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    wallpaper = Wallpapers.NONE
)
@Composable
fun Preview() {
//    Main()
}



