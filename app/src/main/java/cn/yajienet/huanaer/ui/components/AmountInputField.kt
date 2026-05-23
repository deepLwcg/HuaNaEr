package cn.yajienet.huanaer.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cn.yajienet.huanaer.util.CurrencyFormat

@Composable
fun AmountInputField(
    amount: String,
    onAmountChange: (String) -> Unit,
    accentColor: Color,
    modifier: Modifier = Modifier,
    label: String = "金额",
    focusRequester: FocusRequester = remember { FocusRequester() },
    compact: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.headlineMedium.copy(
        fontWeight = FontWeight.Bold
    )
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val fieldTextStyle = textStyle.copy(color = accentColor)
    val placeholderStyle = fieldTextStyle.copy(
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
    )

    Column(modifier = modifier.fillMaxWidth()) {
        if (!compact) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
        }
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(if (compact) 12.dp else 16.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            border = BorderStroke(
                width = 1.dp,
                color = if (isFocused) accentColor
                else MaterialTheme.colorScheme.outlineVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = if (compact) 12.dp else 16.dp,
                        vertical = if (compact) 8.dp else 12.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = CurrencyFormat.SYMBOL,
                    style = fieldTextStyle,
                    modifier = Modifier.padding(end = 2.dp)
                )
                BasicTextField(
                    value = amount,
                    onValueChange = onAmountChange,
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    textStyle = fieldTextStyle,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    cursorBrush = SolidColor(accentColor),
                    interactionSource = interactionSource,
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (amount.isEmpty()) {
                                Text(text = "0.00", style = placeholderStyle)
                            }
                            innerTextField()
                        }
                    }
                )
            }
        }
    }
}
