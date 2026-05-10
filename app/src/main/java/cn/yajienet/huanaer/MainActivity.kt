package cn.yajienet.huanaer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cn.yajienet.huanaer.ui.navigation.HuaNaErNavigation
import cn.yajienet.huanaer.ui.theme.HuaNaErTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HuaNaErTheme {
                HuaNaErNavigation()
            }
        }
    }
}