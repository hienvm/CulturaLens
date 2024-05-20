package com.example.culturalens.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IntroPager(navController: NavController) {
    val pagerState = rememberPagerState(pageCount = { pages.size })

    HorizontalPager(
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 50.dp),
        pageSpacing = 0.dp,
        modifier = Modifier
//        pageSize = introPagerSize,
    ) {idx ->
        IntroItem(
            navController = navController,
            text = pages[idx].text,
            img_id = pages[idx].img_id,
            route = pages[idx].route,
            pagerState = pagerState,
            idx = idx,
        )
    }

}


@OptIn(ExperimentalFoundationApi::class)
val introPagerSize = object : PageSize {
    override fun Density.calculateMainAxisPageSize(
        availableSpace: Int,
        pageSpacing: Int
    ): Int {
        return (availableSpace - 2 * pageSpacing) / 3
    }
}


val pages: List<IntroInfo> = listOf(
    IntroInfo.Projector,
    IntroInfo.AugmentedImage,
    IntroInfo.Gallery,
)

