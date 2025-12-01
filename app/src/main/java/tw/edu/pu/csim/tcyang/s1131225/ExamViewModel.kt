package tw.edu.pu.csim.tcyang.s1131225

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class ExamViewModel : ViewModel() {

    // --- 既存螢幕尺寸狀態 ---
    private val _screenWidth = MutableStateFlow(0)
    val screenWidth: StateFlow<Int> = _screenWidth.asStateFlow()

    private val _screenHeight = MutableStateFlow(0)
    val screenHeight: StateFlow<Int> = _screenHeight.asStateFlow()

    // --- 服務圖示狀態 ---
    private val _serviceIconResId = MutableStateFlow(R.drawable.service0)
    val serviceIconResId: StateFlow<Int> = _serviceIconResId.asStateFlow()

    private val _iconYPositionPx = MutableStateFlow(0f)
    val iconYPositionPx: StateFlow<Float> = _iconYPositionPx.asStateFlow()

    private val _iconXOffsetDp = MutableStateFlow(0f)
    val iconXOffsetDp: StateFlow<Float> = _iconXOffsetDp.asStateFlow()

    // 服務圖示的常數
    val FALL_SPEED_PX = 20f

    // 隨機圖示清單
    private val serviceIcons = listOf(
        R.drawable.service0,
        R.drawable.service1,
        R.drawable.service2,
        R.drawable.service3
    )

    init {
        generateNewIcon()
    }

    // --- 尺寸更新邏輯 ---
    fun updateScreenSize(width: Int, height: Int) {
        if (width > 0 && height > 0 && (_screenWidth.value != width || _screenHeight.value != height)) {
            _screenWidth.value = width
            _screenHeight.value = height

            resetIconPosition()
        }
    }

    // --- 服務圖示邏輯 ---
    private fun generateNewIcon() {
        val newIcon = serviceIcons.random()
        _serviceIconResId.value = newIcon
    }

    fun resetIconPosition() {
        generateNewIcon()
        _iconYPositionPx.value = 0f
        _iconXOffsetDp.value = 0f
    }


    fun dropIcon() {
        if (_screenHeight.value == 0) return

        _iconYPositionPx.value += FALL_SPEED_PX

        // 碰撞偵測 (圖示邊緣碰撞螢幕底部)
        if (_iconYPositionPx.value >= _screenHeight.value) {
            resetIconPosition()
        }
    }

    fun updateXOffset(deltaPx: Float, density: Float, screenWidthPx: Int) {
        val deltaDp = deltaPx / density
        val newOffsetDp = _iconXOffsetDp.value + deltaDp

        // 300 Px 轉 Dp
        val iconSizeDp = 300f / density
        // 最大偏移量 = (螢幕寬度 - 圖示尺寸) / 2
        val screenWidthDp = screenWidthPx / density
        val maxOffsetDp = (screenWidthDp - iconSizeDp) / 2

        _iconXOffsetDp.value = newOffsetDp.coerceIn(-maxOffsetDp, maxOffsetDp)
    }
}