package tw.edu.pu.csim.tcyang.s1131225
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import androidx.compose.ui.unit.Density

data class Rect(val left: Float, val top: Float, val right: Float, val bottom: Float)

fun checkCollision(falling: Rect, target: Rect): Boolean {
    return falling.left < target.right &&
            falling.right > target.left &&
            falling.top < target.bottom &&
            falling.bottom > target.top
}

@Composable
fun ExamScreen(modifier: Modifier = Modifier, viewModel: ExamViewModel = viewModel()) {

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val currentDensity = density.density
    val screenWidthPx = (configuration.screenWidthDp * currentDensity).toInt()
    val screenHeightPx = (configuration.screenHeightDp * currentDensity).toInt()

    LaunchedEffect(screenWidthPx, screenHeightPx) {
        viewModel.updateScreenSize(screenWidthPx, screenHeightPx, currentDensity)
    }

    val iconYPos by viewModel.iconYPositionPx.collectAsState()
    val iconXOffset by viewModel.iconXOffsetDp.collectAsState()
    val fallingIcon by viewModel.serviceIconResId.collectAsState()
    val currentScore by viewModel.score.collectAsState()

    val iconSizePx = 300f
    val iconSizeDp = with(density) { iconSizePx.toDp() }
    val halfScreenDp = with(density) { (screenHeightPx / 2f).toDp() }

    // 自動掉落 coroutine (碰撞邏輯)
    LaunchedEffect(Unit) {
        while(true) {
            delay(50)
            val currentTop = iconYPos
            val currentIconXOffsetPx = iconXOffset * currentDensity

            // 服務圖示邊界
            val currentTopLeftXPx = (screenWidthPx / 2f) + currentIconXOffsetPx - (iconSizePx / 2f)
            val fallingRect = Rect(
                left = currentTopLeftXPx,
                top = currentTop,
                right = currentTopLeftXPx + iconSizePx,
                bottom = currentTop + iconSizePx
            )

            // 固定角色邊界 (根據最新的圖示位置計算)
            val verticalOffsetTopPx = (screenHeightPx/2f) - iconSizePx

            val roles = listOf(
                "嬰幼兒圖示" to Rect(0f, verticalOffsetTopPx, iconSizePx, verticalOffsetTopPx + iconSizePx),
                "兒童圖示" to Rect(screenWidthPx - iconSizePx, verticalOffsetTopPx, screenWidthPx.toFloat(), verticalOffsetTopPx + iconSizePx),
                "成人圖示" to Rect(0f, screenHeightPx - iconSizePx, iconSizePx, screenHeightPx.toFloat()),
                "一般民眾圖示" to Rect(screenWidthPx - iconSizePx, screenHeightPx - iconSizePx, screenWidthPx.toFloat(), screenHeightPx.toFloat())
            )

            var collided = false
            for ((name, rect) in roles) {
                if(checkCollision(fallingRect, rect)) {
                    viewModel.addScore()
                    viewModel.resetIconPosition()
                    collided = true
                    break
                }
            }

            // 掉到底部
            if(!collided && fallingRect.bottom >= screenHeightPx) {
                viewModel.subtractScore()
                viewModel.resetIconPosition()
                collided = true
            }

            if(!collided) {
                viewModel.dropIcon()
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFFFFF00)
    ) { innerPadding ->

        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {

            // 服務圖示
            Image(
                painter = painterResource(id = fallingIcon),
                contentDescription = "服務圖示",
                modifier = Modifier
                    .size(iconSizeDp)
                    .align(Alignment.TopCenter)
                    .offset(x = iconXOffset.dp, y = with(density){iconYPos.toDp()})
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            viewModel.updateXOffset(dragAmount.x, currentDensity, screenWidthPx)
                        }
                    }
            )

            // 角色圖示

            // 嬰幼兒（左上）
            Image(
                painter = painterResource(id = R.drawable.role0),
                contentDescription = "嬰幼兒圖示",
                modifier = Modifier
                    .size(iconSizeDp)
                    .align(Alignment.TopStart)
                    .offset(y = halfScreenDp - iconSizeDp)
            )

            // 兒童（右上）
            Image(
                painter = painterResource(id = R.drawable.role1),
                contentDescription = "兒童圖示",
                modifier = Modifier
                    .size(iconSizeDp)
                    .align(Alignment.TopEnd)
                    .offset(y = halfScreenDp - iconSizeDp)
            )

            // 成人（左下）
            Image(
                painter = painterResource(id = R.drawable.role2),
                contentDescription = "成人圖示",
                modifier = Modifier
                    .size(iconSizeDp)
                    .align(Alignment.BottomStart)
            )

            // 一般民眾（右下）
            Image(
                painter = painterResource(id = R.drawable.role3),
                contentDescription = "一般民眾圖示",
                modifier = Modifier
                    .size(iconSizeDp)
                    .align(Alignment.BottomEnd)
            )

            // 中央 Happy 圖示與文字 (修正 Image 區塊)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(id = R.drawable.happy),
                    contentDescription = "快樂圖示", // *** 修正點：加入 contentDescription ***
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("瑞科亞東會展服務大樓", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("作者：黃三B 楊子慶", fontSize = 18.sp)
                    Text("螢幕大小：${screenWidthPx} * ${screenHeightPx}", fontSize = 16.sp)

                    // 顯示動態分數
                    Text(
                        "成績：${currentScore}分",
                        fontSize = 16.sp,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 底部尺寸資訊
            Text(
                text = "寬度：${screenWidthPx} px\n高度：${screenHeightPx} px",
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
            )
        }
    }
}