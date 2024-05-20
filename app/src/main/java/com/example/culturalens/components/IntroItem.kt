package com.example.culturalens.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IntroItem(navController: NavController, text: AnnotatedString, img_id: Int, route: String, pagerState: PagerState, idx: Int) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxHeight()
            .graphicsLayer {
                val pageOffset = (
                        (pagerState.currentPage - idx) + pagerState
                            .currentPageOffsetFraction
                        ).absoluteValue

                // alpha: 30% -> 100%
                alpha = lerp(
                    start = 0.2f,
                    stop = 1f,
                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                )
            },
    ) {
        Text(
            text = text,
            style = TextStyle(
                textAlign = TextAlign.Justify,
                fontSize = 16.sp,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
                .padding(PaddingValues(horizontal = 20.dp))
        )
        Image(
            bitmap = ImageBitmap.imageResource(img_id),
            "Intro Image",
            modifier = Modifier
                .width(250.dp)
                .height(300.dp)
                .padding(PaddingValues(top = 0.dp, bottom = 0.dp))
        )
        Button(
            onClick = {
                navController.navigate(route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
            elevation = ButtonDefaults.elevation(defaultElevation = 4.dp, pressedElevation = 2.dp),
            modifier = Modifier.padding(PaddingValues(bottom = 4.dp))
        ) {
            Text(
                text = "Thử ngay", fontSize = 16.sp,
                modifier = Modifier.padding(8.dp),
            )
        }
        Row(
            Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .zIndex(1f)
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(pagerState.pageCount) { idx ->
                val color = if (pagerState.currentPage == idx) MaterialTheme.colors.primaryVariant
                else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .height(20.dp)
                        .width(20.dp)
                        .clickable {
                            coroutineScope.launch {
                                pagerState.stopScroll()
                                pagerState.animateScrollToPage(idx)
                            }
                        }
                )
            }
        }
    }
}