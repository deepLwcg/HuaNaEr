package cn.yajienet.huanaer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cn.yajienet.huanaer.data.datastore.ThemeMode
import cn.yajienet.huanaer.ui.navigation.HuaNaErNavigation
import cn.yajienet.huanaer.ui.screens.splash.SplashScreen
import cn.yajienet.huanaer.ui.theme.HuaNaErTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as HuaNaErApplication

        setContent {
            val themeMode by app.settingsRepository.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
            val dynamicColor by app.settingsRepository.dynamicColor.collectAsState(initial = true)
            var showSplash by remember { mutableStateOf(true) }

            HuaNaErTheme(
                darkTheme = when (themeMode) {
                    ThemeMode.LIGHT -> false
                    ThemeMode.DARK -> true
                    ThemeMode.SYSTEM -> isSystemInDarkTheme()
                },
                dynamicColor = dynamicColor
            ) {
                if (showSplash) {
                    SplashScreen(
                        onSplashComplete = { showSplash = false }
                    )
                } else {
                    HuaNaErNavigation()
                }
            }
        }
    }
}