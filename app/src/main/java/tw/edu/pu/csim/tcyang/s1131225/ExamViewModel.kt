package tw.edu.pu.csim.tcyang.s1131225

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class ExamViewModel : ViewModel() {

    // --- 螢幕尺寸與密度狀態 ---
    private val _screenWidth = MutableStateFlow(0)
    val screenWidth: StateFlow<Int> = _screenWidth.asStateFlow()

    private val _screenHeight = MutableStateFlow(0)
    val screenHeight: StateFlow<Int> = _screenHeight.asStateFlow()

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

    // --- 分數狀態 ---
    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()

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

    // 更新螢幕尺寸和密度
    fun updateScreenSize(width: Int, height: Int, density: Float) {
        if (width > 0 && height > 0 && (_screenWidth.value != width || _screenHeight.value != height || _currentDensity.value != density)) {
            _screenWidth.value = width
            _screenHeight.value = height
            _currentDensity.value = density
            resetIconPosition()
        }
    }

    // 隨機產生新圖示
    private fun generateNewIcon() {
        val newIcon = serviceIcons.random()
        _serviceIconResId.value = newIcon
    }

    // 重設圖示到隨機的水平起始位置
    fun resetIconPosition() {
        generateNewIcon()
        _iconYPositionPx.value = 0f
        _collisionMessage.value = "" // *** 修正：重設時清空碰撞訊息 ***

        val density = _currentDensity.value
        val screenWidthPx = _screenWidth.value

        if (density > 0f && screenWidthPx > 0) {
            val iconSizeDp = 300f / density
            val screenWidthDp = screenWidthPx / density

            val maxOffsetDp = (screenWidthDp - iconSizeDp) / 2f
            val startOffsets = listOf(-maxOffsetDp, 0f, maxOffsetDp)

            _iconXOffsetDp.value = startOffsets.random()
        } else {
            _iconXOffsetDp.value = 0f
        }
    }

    // 讓圖示往下掉
    fun dropIcon() {
        if (_screenHeight.value == 0) return
        _iconYPositionPx.value += FALL_SPEED_PX
    }

    // 設定碰撞訊息
    fun setCollisionMessage(message: String) {
        _collisionMessage.value = message
    }

    // 加分函式 (碰撞加 10 分)
    fun addScore() {
        _score.value += 10
    }

    // 扣分函式 (掉到最下方扣 5 分)
    fun subtractScore() {
        _score.value -= 5
    }

    // 處理水平拖曳
    fun updateXOffset(deltaPx: Float, density: Float, screenWidthPx: Int) {
        val deltaDp = deltaPx / density
        val newOffsetDp = _iconXOffsetDp.value + deltaDp

        val iconSizeDp = 300f / density
        val screenWidthDp = screenWidthPx / density
        val maxOffsetDp = (screenWidthDp - iconSizeDp) / 2

        _iconXOffsetDp.value = newOffsetDp.coerceIn(-maxOffsetDp, maxOffsetDp)
    }
}