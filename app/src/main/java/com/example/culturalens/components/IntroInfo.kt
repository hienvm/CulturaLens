package com.example.culturalens.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.example.culturalens.NavItem
import com.example.culturalens.R
import com.example.culturalens.ui.theme.Purple40
import com.example.culturalens.ui.theme.Purple80
import com.example.culturalens.ui.theme.PurpleGrey80

sealed class IntroInfo(val text: AnnotatedString, val img_id: Int, val route: String) {
    data object Projector : IntroInfo(
        text = buildAnnotatedString {
            append("Bắt đầu ngay với ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Purple40)) {
                append("Trình chiếu AR")
            }
            appendLine(".")
            withStyle(style = SpanStyle(fontStyle = FontStyle.Italic, color = PurpleGrey80)) {
                append("Sống động cùng ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Purple80)) {
                    append("Di sản Văn hóa")
                }
                append(" ngay tại không gian của bạn.")
            }
        },
        img_id = R.drawable.projector,
        route = NavItem.Projector.route,
    )
    data object AugmentedImage : IntroInfo(
        text = buildAnnotatedString {
            append("Cùng trải nghiệm ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Purple40)) {
                append("Hình ảnh Tăng cường")
            }
            appendLine(".")
            withStyle(style = SpanStyle(fontStyle = FontStyle.Italic, color = PurpleGrey80)) {
                append("Khai phá thêm thông tin từ ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Purple80)) {
                    append("Hình ảnh")
                }
                append(".")
            }
        },
        img_id = R.drawable.ar_img,
        route = NavItem.AugmentedImage.route,
    )
    data object Gallery : IntroInfo(
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(fontStyle = FontStyle.Italic, color = PurpleGrey80)) {
                append("Điện thoại của bạn không hỗ trợ ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Purple80)) {
                    append("AR")
                    appendLine("?")
                }
            }
//            appendLine()
            append("Hãy chiêm ngưỡng ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Purple40)) {
                append("Phòng trưng bày 3D")
            }
            append(" của chúng tôi.")
        },
        img_id = R.drawable.gallery,
        route = NavItem.Gallery.route,
    )
}