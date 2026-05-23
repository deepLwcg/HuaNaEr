package cn.yajienet.huanaer.ui.screens.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.yajienet.huanaer.ui.theme.EaseInOutCubic
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2000)
        onSplashComplete()
    }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            colorScheme.background,
            colorScheme.primary.copy(alpha = 0.12f),
            colorScheme.secondary.copy(alpha = 0.08f)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = startAnimation,
                enter = scaleIn(
                    animationSpec = tween(600, easing = EaseInOutCubic),
                    initialScale = 0.3f
                ) + fadeIn(tween(600, easing = LinearEasing))
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(colorScheme.primary, colorScheme.secondary)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "¥",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onPrimary
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            AnimatedVisibility(
                visible = startAnimation,
                enter = slideInVertically(
                    animationSpec = tween(800, easing = EaseInOutCubic),
                    initialOffsetY = { it / 2 }
                ) + fadeIn(tween(800, easing = LinearEasing))
            ) {
                Text(
                    text = "花哪儿",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onBackground
                )
            }

            Spacer(Modifier.height(8.dp))

            AnimatedVisibility(
                visible = startAnimation,
                enter = fadeIn(tween(1000, delayMillis = 400, easing = LinearEasing))
            ) {
                Text(
                    text = "轻松记账，每天都甜甜的",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }

        AnimatedVisibility(
            visible = startAnimation,
            enter = fadeIn(tween(1200, delayMillis = 800, easing = LinearEasing)),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text(
                text = "© YajieNet",
                fontSize = 12.sp,
                color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}
