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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.unit.Density

// --- 輔助 Data Class (不變) ---
data class Rect(val left: Float, val top: Float, val right: Float, val bottom: Float)

data class RoleBoundsPx(
    val name: String,
    val rect: Rect
)

fun checkCollision(falling: Rect, target: Rect): Boolean {
    return falling.left < target.right &&
            falling.right > target.left &&
            falling.top < target.bottom &&
            falling.bottom > target.top
}
// ------------------------------------------

@Composable
fun ExamScreen(modifier: Modifier = Modifier, viewModel: ExamViewModel = viewModel()) {

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val currentDensity = density.density // <-- 獲取原始密度值

    val currentWidthPx = (configuration.screenWidthDp * currentDensity).toInt()
    val currentHeightPx = (configuration.screenHeightDp * currentDensity).toInt()

    // ... (其他狀態讀取) ...
    val screenWidthPxState = viewModel.screenWidth.collectAsState().value
    val screenHeightPxState = viewModel.screenHeight.collectAsState().value

    val iconYPositionPx = viewModel.iconYPositionPx.collectAsState().value
    val iconXOffsetDp = viewModel.iconXOffsetDp.collectAsState().value
    val fallingIconResId = viewModel.serviceIconResId.collectAsState().value
    val collisionMessageState = viewModel.collisionMessage.collectAsState().value

    // *** 修正點：傳遞 density 參數 ***
    LaunchedEffect(currentWidthPx, currentHeightPx) {
        viewModel.updateScreenSize(currentWidthPx, currentHeightPx, currentDensity)
    }

    val iconSizePx = 300f
    val iconSizeDp = with(density) { iconSizePx.toDp() }
    val screenHeightDp = configuration.screenHeightDp.dp
    val verticalOffsetDp = (screenHeightDp / 2) - (iconSizeDp / 2)
    val iconYPositionDp = with(density) { iconYPositionPx.toDp() }


    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFFFFF00)
    ) { innerPadding ->

        // --- 服務圖示動畫計時器與碰撞檢查 (邏輯不變) ---
        LaunchedEffect(Unit) {
            val bottomPaddingPx = with(density) { innerPadding.calculateBottomPadding().toPx() }

            while (true) {
                kotlinx.coroutines.delay(100)

                val currentWidth = viewModel.screenWidth.value
                val currentHeight = viewModel.screenHeight.value

                if (currentHeight <= 0 || currentWidth <= 0) {
                    viewModel.dropIcon()
                    continue
                }

                // Px 值計算
                val currentIconXOffsetPx = iconXOffsetDp * currentDensity
                val currentIconYPositionPx = viewModel.iconYPositionPx.value
                val verticalOffsetPx = (currentHeight / 2f) - (iconSizePx / 2f)

                // 服務圖示邊界
                val currentTopLeftXPx = (currentWidth / 2f) + currentIconXOffsetPx - (iconSizePx / 2f)
                val currentFallingBounds = Rect(
                    left = currentTopLeftXPx,
                    top = currentIconYPositionPx,
                    right = currentTopLeftXPx + iconSizePx,
                    bottom = currentIconYPositionPx + iconSizePx
                )

                // 固定圖示邊界
                val roleIconsBounds = listOf(
                    RoleBoundsPx("嬰幼兒圖示", Rect(0f, verticalOffsetPx, iconSizePx, verticalOffsetPx + iconSizePx)),
                    RoleBoundsPx("兒童圖示", Rect(currentWidth - iconSizePx, verticalOffsetPx, currentWidth.toFloat(), verticalOffsetPx + iconSizePx)),
                    RoleBoundsPx("成人圖示", Rect(0f, currentHeight - bottomPaddingPx - iconSizePx, iconSizePx, currentHeight - bottomPaddingPx)),
                    RoleBoundsPx("一般民眾圖示", Rect(currentWidth - iconSizePx, currentHeight - bottomPaddingPx - iconSizePx, currentWidth.toFloat(), currentHeight - bottomPaddingPx))
                )

                var collisionDetected = false

                for (role in roleIconsBounds) {
                    if (checkCollision(currentFallingBounds, role.rect)) {
                        viewModel.setCollisionMessage("碰撞${role.name}")
                        viewModel.resetIconPosition()
                        collisionDetected = true
                        break
                    }
                }

                val bottomCollisionBoundaryPx = currentHeight - bottomPaddingPx
                if (!collisionDetected && (currentFallingBounds.bottom) >= bottomCollisionBoundaryPx) {
                    viewModel.setCollisionMessage("(掉到最下方)")
                    viewModel.resetIconPosition()
                    collisionDetected = true
                }

                if (!collisionDetected) {
                    viewModel.dropIcon()
                }
            }
        }

        // 6. UI 佈局 (不變)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // ======== 隨機服務圖示 (掉落與拖曳) ========
            Image(
                painter = painterResource(id = fallingIconResId),
                contentDescription = "隨機服務圖示",
                modifier = Modifier
                    .size(iconSizeDp)
                    .align(Alignment.TopCenter) // 錨點在上方中央
                    .offset(
                        x = iconXOffsetDp.dp, // 這裡會使用隨機的起始 Offset
                        y = iconYPositionDp
                    )
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                viewModel.updateXOffset(
                                    dragAmount.x,
                                    currentDensity,
                                    currentWidthPx
                                )
                            }
                        )
                    }
            )

            // ... (角色圖示不變) ...
            Image(painter = painterResource(id = R.drawable.role0), contentDescription = "嬰幼兒圖示", modifier = Modifier.size(iconSizeDp).align(Alignment.TopStart).offset(y = verticalOffsetDp))
            Image(painter = painterResource(id = R.drawable.role1), contentDescription = "兒童圖示", modifier = Modifier.size(iconSizeDp).align(Alignment.TopEnd).offset(y = verticalOffsetDp))
            Image(painter = painterResource(id = R.drawable.role2), contentDescription = "成人圖示", modifier = Modifier.size(iconSizeDp).align(Alignment.BottomStart))
            Image(painter = painterResource(id = R.drawable.role3), contentDescription = "一般民眾圖示", modifier = Modifier.size(iconSizeDp).align(Alignment.BottomEnd))


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

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "成績：100分",
                            fontSize = 16.sp,
                            color = Color.Red,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            " $collisionMessageState",
                            fontSize = 16.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}