package cn.yajienet.huanaer.ui.components.neubru

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class NeubruElevation(
    val borderWidth: Dp = 2.dp,
    val shadowOffsetX: Dp = 4.dp,
    val shadowOffsetY: Dp = 4.dp,
    val cornerRadius: Dp = 16.dp
)

val LocalNeubruElevation = androidx.compose.runtime.compositionLocalOf { NeubruElevation() }

@Composable
@ReadOnlyComposable
fun neubruElevation(): NeubruElevation = LocalNeubruElevation.current