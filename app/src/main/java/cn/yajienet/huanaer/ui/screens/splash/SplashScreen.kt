package cn.yajienet.huanaer.ui.screens.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2000) // 显示2秒
        onSplashComplete()
    }

    // 渐变背景
    val gradientColors = listOf(
        Color(0xFF667EEA),
        Color(0xFF5A67D8),
        Color(0xFF4C51BF)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = gradientColors,
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo 圆形背景动画
            AnimatedVisibility(
                visible = startAnimation,
                enter = scaleIn(
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    ),
                    initialScale = 0.3f
                ) + fadeIn(
                    animationSpec = tween(600, easing = LinearEasing)
                )
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    // ¥ 符号
                    Text(
                        text = "¥",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4C51BF)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // 应用名称动画
            AnimatedVisibility(
                visible = startAnimation,
                enter = slideInVertically(
                    animationSpec = tween(
                        durationMillis = 800,
                        easing = FastOutSlowInEasing
                    ),
                    initialOffsetY = { it / 2 }
                ) + fadeIn(
                    animationSpec = tween(800, easing = LinearEasing)
                )
            ) {
                Text(
                    text = "花哪儿",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(8.dp))

            // 副标题动画
            AnimatedVisibility(
                visible = startAnimation,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = 1000,
                        delayMillis = 400,
                        easing = LinearEasing
                    )
                )
            ) {
                Text(
                    text = "轻松记账，掌控生活",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        // 底部版权信息
        AnimatedVisibility(
            visible = startAnimation,
            enter = fadeIn(
                animationSpec = tween(
                    durationMillis = 1200,
                    delayMillis = 800,
                    easing = LinearEasing
                )
            ),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text(
                text = "© YajieNet",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}