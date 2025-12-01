package tw.edu.pu.csim.tcyang.s1131225

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ExamScreen(modifier: Modifier = Modifier, viewModel: ExamViewModel = viewModel()) {

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    // 1. 螢幕尺寸計算 (DP 轉 Px)
    val currentWidthPx = (configuration.screenWidthDp * density.density).toInt()
    val currentHeightPx = (configuration.screenHeightDp * density.density).toInt()

    // 2. 從 ViewModel 讀取狀態
    val screenWidthPxState = viewModel.screenWidth.collectAsState().value
    val screenHeightPxState = viewModel.screenHeight.collectAsState().value

    // 3. 更新 ViewModel
    LaunchedEffect(currentWidthPx, currentHeightPx) {
        viewModel.updateScreenSize(currentWidthPx, currentHeightPx)
    }

    // 4. *** 核心修正點：圖示尺寸與垂直位移計算 ***
    // 將 300 Px 轉換為 Dp
    val iconSizePx = 300f
    val iconSizeDp = with(density) { iconSizePx.toDp() }

    // 獲取螢幕高度 Dp
    val screenHeightDp = configuration.screenHeightDp.dp

    // 計算將圖示中心對齊螢幕中線所需的垂直位移量：(螢幕高度 / 2) - (圖示尺寸 / 2)
    val verticalOffsetDp = (screenHeightDp / 2) - (iconSizeDp / 2)


    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFFFFF00) // 亮黃色
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // ======== 角色圖示 - 應用 300 Px (iconSizeDp) 尺寸和位移 ========

            // 嬰幼兒（左上）- 貼齊螢幕高 1/2
            Image(
                painter = painterResource(id = R.drawable.role0),
                contentDescription = "嬰幼兒圖示",
                modifier = Modifier
                    .size(iconSizeDp)
                    .align(Alignment.TopStart)
                    .offset(y = verticalOffsetDp) // 應用垂直置中位移
            )

            // 兒童（右上）- 貼齊螢幕高 1/2
            Image(
                painter = painterResource(id = R.drawable.role1),
                contentDescription = "兒童圖示",
                modifier = Modifier
                    .size(iconSizeDp)
                    .align(Alignment.TopEnd)
                    .offset(y = verticalOffsetDp) // 應用垂直置中位移
            )

            // 成人（左下）- 300 Px 尺寸，貼齊左下角
            Image(
                painter = painterResource(id = R.drawable.role2),
                contentDescription = "成人圖示",
                modifier = Modifier
                    .size(iconSizeDp)
                    .align(Alignment.BottomStart)
            )

            // 一般民眾（右下）- 300 Px 尺寸，貼齊右下角
            Image(
                painter = painterResource(id = R.drawable.role3),
                contentDescription = "一般民眾圖示",
                modifier = Modifier
                    .size(iconSizeDp)
                    .align(Alignment.BottomEnd)
            )
            // ======== 角色圖示完成 ========


            // ======== 畫面內容 (中央資訊) ========
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // 中央圖片 (假設也用 300 Px)
                Image(
                    painter = painterResource(id = R.drawable.happy),
                    contentDescription = "快樂圖示",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(iconSizeDp) // 中央圖也使用 300 Px 的尺寸
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("瑪利亞基金會大考驗", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("作者：資管2B 陳芯霈", fontSize = 18.sp)

                    Text("螢幕大小：${screenWidthPxState} * ${screenHeightPxState}", fontSize = 16.sp)

                    Text(
                        "成績：100分",
                        fontSize = 16.sp,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}