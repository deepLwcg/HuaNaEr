package cn.yajienet.huanaer

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cn.yajienet.huanaer.data.datastore.ThemeMode
import cn.yajienet.huanaer.data.datastore.ThemeStyle
import cn.yajienet.huanaer.ui.navigation.HuaNaErNavigation
import cn.yajienet.huanaer.ui.navigation.Screen
import cn.yajienet.huanaer.ui.screens.splash.SplashScreen
import cn.yajienet.huanaer.ui.theme.HuaNaErTheme

class MainActivity : ComponentActivity() {

    companion object {
        const val ACTION_ADD_TRANSACTION = "cn.yajienet.huanaer.ADD_TRANSACTION"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as HuaNaErApplication
        val isAddTransactionAction = intent?.action == ACTION_ADD_TRANSACTION

        setContent {
            val themeMode by app.settingsRepository.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
            val themeStyle by app.settingsRepository.themeStyle.collectAsState(initial = ThemeStyle.MINT_BREEZE)
            val dynamicColor by app.settingsRepository.dynamicColor.collectAsState(initial = true)
            var showSplash by remember { mutableStateOf(!isAddTransactionAction) }

            val darkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            HuaNaErTheme(
                darkTheme = darkTheme,
                dynamicColor = dynamicColor,
                themeStyle = themeStyle
            ) {
                if (showSplash) {
                    SplashScreen(onSplashComplete = { showSplash = false })
                } else {
                    HuaNaErNavigation(
                        initialRoute = if (isAddTransactionAction) Screen.AddTransaction.route else null
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}