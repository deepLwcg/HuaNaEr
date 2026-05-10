package cn.yajienet.huanaer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cn.yajienet.huanaer.data.datastore.ThemeMode
import cn.yajienet.huanaer.ui.navigation.HuaNaErNavigation
import cn.yajienet.huanaer.ui.theme.HuaNaErTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as HuaNaErApplication

        setContent {
            val themeMode by app.settingsRepository.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
            val dynamicColor by app.settingsRepository.dynamicColor.collectAsState(initial = true)

            HuaNaErTheme(
                darkTheme = when (themeMode) {
                    ThemeMode.LIGHT -> false
                    ThemeMode.DARK -> true
                    ThemeMode.SYSTEM -> isSystemInDarkTheme()
                },
                dynamicColor = dynamicColor
            ) {
                HuaNaErNavigation()
            }
        }
    }
}