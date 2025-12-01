// MainActivity.kt （保持乾淨，只放啟動畫面）
package tw.edu.pu.csim.tcyang.s1131225

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import tw.edu.pu.csim.tcyang.s1131225.ui.theme.S1131225Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            S1131225Theme {
                ExamApp()
            }
        }
    }
}

@Composable
fun ExamApp() {
    ExamScreen()
}

@Preview(showBackground = true)
@Composable
fun ExamPreview() {
    S1131225Theme {
        ExamApp()
    }
}