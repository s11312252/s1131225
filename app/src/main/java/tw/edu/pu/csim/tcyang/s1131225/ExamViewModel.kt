package tw.edu.pu.csim.tcyang.s1131225

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ExamViewModel : ViewModel() {

    // 寬高變數保持不變
    var screenWidthPx by mutableStateOf(0)
        private set
    var screenHeightPx by mutableStateOf(0)
        private set

    /**
     * 更新螢幕尺寸。
     * 該函數接收計算好的寬高（以 Int 傳入）。
     */
    fun updateScreenSize(width: Int, height: Int) {
        screenWidthPx = width
        screenHeightPx = height
    }
}
