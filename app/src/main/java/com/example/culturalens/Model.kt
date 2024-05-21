package com.example.culturalens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation

class ModelWrapper(
    val modelID: Int,
    val imgID: Int,
    val scaleToUnits: Float = 1f,
    val rotation: Rotation = Rotation(),
    val name: String,
    val text: String,
    val descrImgID: Int,
    val position: Position = Position()
) {
    @Composable
    fun About() {
        About(name = name, text = text)
    }
}

val models = listOfNotNull(
    ModelWrapper(
        modelID = R.raw.trong_dong_4k,
        imgID = R.drawable.trong_dong_4k,
        scaleToUnits = 0.7f,
        name = "Trống đồng Đông Sơn",
        text = "Một loại trống đồng tiêu biểu cho Văn hóa Đông Sơn (thế kỷ 7 TCN - thế kỷ 6 CN) của người Việt cổ.",
        descrImgID = R.drawable.trong_dong_descr
    ),
    ModelWrapper(
        modelID = R.raw.dong_son_sword,
        imgID = R.drawable.dong_son_sword,
        scaleToUnits = 1f,
        name = "Kiếm đồng Đông Sơn",
        text = "Kiếm đồng thời Đông Sơn (thế kỷ 7 TCN - thế kỷ 6 CN), đại biểu cho kỹ nghệ chế tác đồng và sức mạnh dân tộc, quyết tâm giữ nước của người Việt xưa.",
        descrImgID = R.drawable.kiem_dong_son_descr
    ),
    ModelWrapper(
        modelID = R.raw.chuong,
        imgID = R.drawable.chuong,
        scaleToUnits = 0.7f,
        name = "Chuông đồng",
        text = "Chuông đồng là khí cụ nổi tiếng, thường được dùng trong các chùa, miếu, đình,...",
        descrImgID = R.drawable.trong_dong_descr
    ),
    ModelWrapper(
        modelID = R.raw.vietnamese___non_la,
        imgID = R.drawable.non_la,
        scaleToUnits = 0.4f,
        name = "Nón lá",
        text = "Nón, nón tơi hoặc nón lá là một vật dụng dùng để che nắng, che mưa, là một biểu tượng của Việt Nam, có từ thế kỉ thứ XIII, thời nhà Trần.",
        descrImgID = R.drawable.non_la_descr
    ),
    ModelWrapper(
        modelID = R.raw.ghe_go,
        imgID = R.drawable.ghe_go,
        scaleToUnits = 0.5f,
        name = "Ghế gỗ Việt Nam",
        text = "Loại ghế gỗ với nét chạm khắc tinh xảo, vẫn còn được sử dụng phổ biến trong các hộ gia đình Việt Nam cho tới ngày nay.",
        descrImgID = R.drawable.ghe_go
    ),
    ModelWrapper(
        modelID = R.raw.tu_go,
        imgID = R.drawable.tu_go,
        scaleToUnits = 6f,
        name = "Tủ gỗ thời nhà Nguyễn",
        text = "Tủ gỗ thời nhà Nguyễn, với vẻ ngoài và cơ chế độc đáo.",
        descrImgID = R.drawable.tu_go,
        position = Position(x = -0.5f)
    ),
    ModelWrapper(
        modelID = R.raw.ghe_go,
        imgID = R.drawable.dong_son_sword,
        scaleToUnits = 0.1f,
        name = "Kiếm đồng Đông Sơn",
        text = "Kiếm đồng thời Đông Sơn (thế kỷ 7 TCN - thế kỷ 6 CN), đại biểu cho kỹ nghệ chế tác đồng và sức mạnh dân tộc, quyết tâm giữ nước của người Việt xưa.",
        descrImgID = R.drawable.kiem_dong_son_descr
    ),
)

@Composable
fun About(name: String, text: String) {
    Column (verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            text = name,
            textAlign = TextAlign.Justify
            )
        Text(
            fontStyle = FontStyle.Italic,
            fontSize = 12.sp,
            text = text,
            textAlign = TextAlign.Justify
            )
    }
}