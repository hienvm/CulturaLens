package com.example.culturalens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.culturalens.components.Header
import com.example.culturalens.components.IntroPager
import com.example.culturalens.components.TopBar

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomePage(navController: NavController) {
    Scaffold(
        topBar = { TopBar(navController = navController, title = "Hãy chọn một chức năng", backRoute = null,) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            Surface (color = Color.White, modifier = Modifier.fillMaxSize()) {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                ){
                    Header()
                    Divider(
                        color = MaterialTheme.colors.primaryVariant,
                        thickness = 1.dp,
                        modifier = Modifier.padding(PaddingValues(horizontal = 30.dp, vertical = 0.dp)),
                    )
                    Text(
                        text = "XIN CHÀO!",
                        color = Color.Black,
                        modifier = Modifier.padding(20.dp),
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                        )
                    )

                    IntroPager(navController = navController)
                }
            }
        }
    }
}