package tw.edu.pu.csim.tcyang.s1131225

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ExamViewModel : ViewModel() {

    private val _screenWidth = MutableStateFlow(0)
    val screenWidth: StateFlow<Int> = _screenWidth

    private val _screenHeight = MutableStateFlow(0)
    val screenHeight: StateFlow<Int> = _screenHeight

    fun updateScreenSize(width: Int, height: Int) {
        _screenWidth.value = width
        _screenHeight.value = height
    }
}
