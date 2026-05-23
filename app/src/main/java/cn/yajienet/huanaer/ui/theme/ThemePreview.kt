package cn.yajienet.huanaer.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.yajienet.huanaer.data.datastore.ThemeStyle

@Preview(name = "草莓奶昔 浅色", showBackground = true)
@Composable
private fun PreviewStrawberryLight() {
    CandyThemePreview(style = ThemeStyle.STRAWBERRY_SHAKE, darkTheme = false)
}

@Preview(name = "草莓奶昔 深色", showBackground = true)
@Composable
private fun PreviewStrawberryDark() {
    CandyThemePreview(style = ThemeStyle.STRAWBERRY_SHAKE, darkTheme = true)
}

@Preview(name = "海盐汽水 浅色", showBackground = true)
@Composable
private fun PreviewSeaSaltLight() {
    CandyThemePreview(style = ThemeStyle.SEA_SALT_SODA, darkTheme = false)
}

@Preview(name = "葡萄泡泡 深色", showBackground = true)
@Composable
private fun PreviewGrapeDark() {
    CandyThemePreview(style = ThemeStyle.GRAPE_BUBBLE, darkTheme = true)
}

@Composable
private fun CandyThemePreview(style: ThemeStyle, darkTheme: Boolean) {
    HuaNaErTheme(themeStyle = style, darkTheme = darkTheme) {
        val colors = extendedColorScheme()
        val shapes = LocalAppShapes.current
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = style.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(shapes.cardMedium)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(shapes.cardMedium)
                        .background(colors.income)
                )
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(shapes.cardMedium)
                        .background(colors.expense)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shapes.cardExtraLarge)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .padding(16.dp)
            ) {
                Text(
                    text = "¥ 1,234.56",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
