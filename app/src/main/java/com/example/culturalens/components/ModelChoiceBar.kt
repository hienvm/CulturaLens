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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import com.example.culturalens.ModelWrapper
import com.example.culturalens.models
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

val OFFSET = 40.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ModelChoiceBar(showChoices: Boolean, getCurModel: () -> ModelWrapper?, setCurModel: (ModelWrapper) -> Unit) {
    val pagerState = rememberPagerState(pageCount = {
        models.size
    })
    val coroutineScope = rememberCoroutineScope()

    if (showChoices) {
        Column{
            Divider(color = MaterialTheme.colors.primaryVariant, thickness = 2.dp)
            Box(
                Modifier.height(150.dp)
            ) {
                Row(
                    Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .zIndex(1f),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    repeat(pagerState.pageCount) {idx ->
                        val color =
                            if (getCurModel() != null && models.indexOf(getCurModel()) == idx) MaterialTheme.colors.primaryVariant
                            else if (pagerState.currentPage == idx) Color.DarkGray
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
                HorizontalPager(
                    state = pagerState,
                    Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = OFFSET),
                ) {idx ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(
                            16.dp,
                            Alignment.CenterHorizontally
                        ),
                        modifier = Modifier.graphicsLayer {
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
                        Image(
                            painter = painterResource(id = models[idx].imgID),
                            contentDescription = models[idx].modelID.toString(),
                            Modifier
                                .clickable(enabled = true) {
                                    setCurModel(models[idx])
                                }
                        )
                        models[idx].About()
                    }
                }
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.stopScroll()
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        },
                        Modifier.fillMaxHeight().width(OFFSET),
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            "Left",
                            tint = if (pagerState.settledPage > 0) Color.Black else Color.Transparent,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }

                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.stopScroll()
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                        Modifier.fillMaxHeight().width(OFFSET)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            "Right",
                            tint = if (pagerState.currentPage < pagerState.pageCount - 1) Color.Black else Color.Transparent,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }
        }
    }
}