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

    // *** 新增：儲存螢幕密度 ***
    private val _currentDensity = MutableStateFlow(0f)

    // --- 服務圖示狀態 ---
    private val _serviceIconResId = MutableStateFlow(R.drawable.service0)
    val serviceIconResId: StateFlow<Int> = _serviceIconResId.asStateFlow()

    private val _iconYPositionPx = MutableStateFlow(0f)
    val iconYPositionPx: StateFlow<Float> = _iconYPositionPx.asStateFlow()

    private val _iconXOffsetDp = MutableStateFlow(0f)
    val iconXOffsetDp: StateFlow<Float> = _iconXOffsetDp.asStateFlow()

    // --- 碰撞訊息狀態 ---
    private val _collisionMessage = MutableStateFlow("")
    val collisionMessage: StateFlow<String> = _collisionMessage.asStateFlow()

    val FALL_SPEED_PX = 20f

    private val serviceIcons = listOf(
        R.drawable.service0,
        R.drawable.service1,
        R.drawable.service2,
        R.drawable.service3
    )

    init {
        generateNewIcon()
    }

    // --- 尺寸更新邏輯 (新增 density 參數) ---
    fun updateScreenSize(width: Int, height: Int, density: Float) {
        if (width > 0 && height > 0 && (_screenWidth.value != width || _screenHeight.value != height || _currentDensity.value != density)) {
            _screenWidth.value = width
            _screenHeight.value = height
            _currentDensity.value = density // 儲存密度
            resetIconPosition()
        }
    }

    // --- 服務圖示邏輯 ---
    private fun generateNewIcon() {
        val newIcon = serviceIcons.random()
        _serviceIconResId.value = newIcon
    }

    // *** 修正：重設位置時計算隨機水平位移 ***
    fun resetIconPosition() {
        generateNewIcon()
        _iconYPositionPx.value = 0f
        _collisionMessage.value = ""

        val density = _currentDensity.value
        val screenWidthPx = _screenWidth.value

        if (density > 0f && screenWidthPx > 0) {
            val iconSizeDp = 300f / density // 圖示尺寸 Dp
            val screenWidthDp = screenWidthPx / density

            // 最大偏移量：從螢幕中心到圖示邊緣對齊螢幕邊緣時的圖示中心點距離
            // 這與 updateXOffset 中的 maxOffsetDp 計算相同
            val maxOffsetDp = (screenWidthDp - iconSizeDp) / 2f

            // 定義三個起始位置：左 (-max), 中 (0), 右 (+max)
            val startOffsets = listOf(-maxOffsetDp, 0f, maxOffsetDp)

            // 隨機選擇起始位置
            _iconXOffsetDp.value = startOffsets.random()
        } else {
            // 如果尺寸或密度未初始化，預設為中央
            _iconXOffsetDp.value = 0f
        }
    }

    fun dropIcon() {
        if (_screenHeight.value == 0) return
        _iconYPositionPx.value += FALL_SPEED_PX
    }

    fun setCollisionMessage(message: String) {
        _collisionMessage.value = message
    }

    fun updateXOffset(deltaPx: Float, density: Float, screenWidthPx: Int) {
        val deltaDp = deltaPx / density
        val newOffsetDp = _iconXOffsetDp.value + deltaDp

        val iconSizeDp = 300f / density
        val screenWidthDp = screenWidthPx / density
        val maxOffsetDp = (screenWidthDp - iconSizeDp) / 2

        // 拖曳範圍限制
        _iconXOffsetDp.value = newOffsetDp.coerceIn(-maxOffsetDp, maxOffsetDp)
    }
}