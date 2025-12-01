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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGestures // 拖曳手勢

@Composable
fun ExamScreen(modifier: Modifier = Modifier, viewModel: ExamViewModel = viewModel()) {

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    // 1. 尺寸計算
    val currentWidthPx = (configuration.screenWidthDp * density.density).toInt()
    val currentHeightPx = (configuration.screenHeightDp * density.density).toInt()

    // 2. 讀取 ViewModel 狀態
    val screenWidthPxState = viewModel.screenWidth.collectAsState().value
    val screenHeightPxState = viewModel.screenHeight.collectAsState().value
    val iconYPositionPx = viewModel.iconYPositionPx.collectAsState().value
    val iconXOffsetDp = viewModel.iconXOffsetDp.collectAsState().value
    val fallingIconResId = viewModel.serviceIconResId.collectAsState().value

    // 3. 更新 ViewModel (尺寸)
    LaunchedEffect(currentWidthPx, currentHeightPx) {
        viewModel.updateScreenSize(currentWidthPx, currentHeightPx)
    }

    // 4. 服務圖示動畫計時器 (每 0.1 秒掉落)
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(100)
            viewModel.dropIcon()
        }
    }

    // 5. 圖示尺寸與垂直位移計算
    val iconSizePx = 300f
    val iconSizeDp = with(density) { iconSizePx.toDp() }
    val screenHeightDp = configuration.screenHeightDp.dp
    val verticalOffsetDp = (screenHeightDp / 2) - (iconSizeDp / 2)
    val iconYPositionDp = with(density) { iconYPositionPx.toDp() }


    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFFFFF00)
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // ======== 隨機服務圖示 (可拖曳、會掉落) ========
            Image(
                painter = painterResource(id = fallingIconResId),
                contentDescription = "隨機服務圖示",
                modifier = Modifier
                    .size(iconSizeDp)
                    .align(Alignment.TopCenter)
                    .offset(
                        x = iconXOffsetDp.dp, // 水平拖曳位移
                        y = iconYPositionDp // 垂直掉落位移
                    )
                    // 水平拖曳邏輯
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                viewModel.updateXOffset(
                                    dragAmount.x,
                                    density.density,
                                    currentWidthPx
                                )
                            }
                        )
                    }
            )
            // ======== 服務圖示完成 ========

            // ======== 角色圖示 (固定位置) ========

            // 嬰幼兒（左上）- 貼齊螢幕高 1/2
            Image(
                painter = painterResource(id = R.drawable.role0),
                contentDescription = "嬰幼兒圖示",
                modifier = Modifier
                    .size(iconSizeDp)
                    .align(Alignment.TopStart)
                    .offset(y = verticalOffsetDp)
            )

            // 兒童（右上）- 貼齊螢幕高 1/2
            Image(
                painter = painterResource(id = R.drawable.role1),
                contentDescription = "兒童圖示",
                modifier = Modifier
                    .size(iconSizeDp)
                    .align(Alignment.TopEnd)
                    .offset(y = verticalOffsetDp)
            )

            // 成人（左下）- 貼齊左下角
            Image(
                painter = painterResource(id = R.drawable.role2),
                contentDescription = "成人圖示",
                modifier = Modifier
                    .size(iconSizeDp)
                    .align(Alignment.BottomStart)
            )

            // 一般民眾（右下）- 貼齊右下角
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

                Image(
                    painter = painterResource(id = R.drawable.happy),
                    contentDescription = "快樂圖示",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(iconSizeDp)
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