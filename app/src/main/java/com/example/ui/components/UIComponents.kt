package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Reusable primary button following the "Professional Polish" design with standard
 * touch target and M3 tokens.
 */
@Composable
fun PolishButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    containerColor: Color = Color(0xFF6750A4),
    contentColor: Color = Color.White
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .minimumInteractiveComponentSize()
            .heightIn(min = 44.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = Color(0xFFCAC4D0).copy(alpha = 0.38f),
            disabledContentColor = Color(0xFF1D1B20).copy(alpha = 0.38f)
        ),
        shape = RoundedCornerShape(100.dp),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Outlined styled button with persistent borders and high visual feedback.
 */
@Composable
fun PolishOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    borderColor: Color = Color(0xFF6750A4),
    contentColor: Color = Color(0xFF6750A4)
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .minimumInteractiveComponentSize()
            .heightIn(min = 44.dp),
        border = BorderStroke(1.5.dp, if (enabled) borderColor else Color(0xFFCAC4D0).copy(alpha = 0.5f)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = contentColor,
            disabledContentColor = Color(0xFF1D1B20).copy(alpha = 0.38f)
        ),
        shape = RoundedCornerShape(100.dp),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Reusable Outlined TextField styled with consistent borders and state indicators.
 */
@Composable
fun PolishTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    leadingIconComposable: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = placeholder?.let { { Text(it) } },
        leadingIcon = when {
            leadingIconComposable != null -> leadingIconComposable
            leadingIcon != null -> { { Icon(leadingIcon, contentDescription = null, tint = Color(0xFF6750A4)) } }
            else -> null
        },
        trailingIcon = trailingIcon,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        singleLine = singleLine,
        isError = isError,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color(0xFF1D1B20),
            unfocusedTextColor = Color(0xFF1D1B20),
            focusedBorderColor = Color(0xFF6750A4),
            unfocusedBorderColor = Color(0xFFCAC4D0),
            focusedLabelColor = Color(0xFF6750A4),
            unfocusedLabelColor = Color(0xFF49454F),
            errorBorderColor = MaterialTheme.colorScheme.error,
            errorLabelColor = MaterialTheme.colorScheme.error
        ),
        modifier = modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
    )
}

/**
 * Reusable styled card with custom borders and elevated depth structures.
 */
@Composable
fun PolishCard(
    modifier: Modifier = Modifier,
    containerColor: Color = Color.White,
    borderColor: Color = Color(0xFFCAC4D0),
    elevation: Dp = 1.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardModifier = if (onClick != null) {
        modifier
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    } else {
        modifier
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        shape = RoundedCornerShape(16.dp),
        modifier = cardModifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            content()
        }
    }
}

/**
 * Custom metrics/stat visualization card following the designated theme.
 */
@Composable
fun PolishStatsCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier,
    containerColor: Color = Color(0xFFF3EDF7),
    borderColor: Color = Color(0xFFCAC4D0)
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        modifier = modifier
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, color = Color(0xFF49454F), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(value, color = color, fontSize = 26.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(vertical = 4.dp))
            Text(subtitle, color = Color(0xFF757575), fontSize = 10.sp)
        }
    }
}

/**
 * Table Component Header
 */
@Composable
fun PolishTableHeader(
    headers: List<String>,
    weights: List<Float>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFEADDFF), RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .border(1.dp, Color(0xFFCAC4D0), RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .padding(vertical = 10.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        headers.forEachIndexed { index, header ->
            val weight = weights.getOrElse(index) { 1f }
            Text(
                text = header,
                modifier = Modifier.weight(weight),
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = Color(0xFF21005D),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Table Component Row for hosting complex custom cells.
 */
@Composable
fun PolishTableRow(
    cells: List<@Composable () -> Unit>,
    weights: List<Float>,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    onClick: (() -> Unit)? = null
) {
    val rowModifier = if (onClick != null) {
        modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .border(0.5.dp, Color(0xFFCAC4D0).copy(alpha = 0.5f))
            .padding(vertical = 8.dp, horizontal = 12.dp)
    } else {
        modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .border(0.5.dp, Color(0xFFCAC4D0).copy(alpha = 0.5f))
            .padding(vertical = 8.dp, horizontal = 12.dp)
    }

    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        cells.forEachIndexed { index, cellContent ->
            val weight = weights.getOrElse(index) { 1f }
            Box(
                modifier = Modifier.weight(weight),
                contentAlignment = Alignment.CenterStart
            ) {
                cellContent()
            }
        }
    }
}

/**
 * Standard simple text-based Table Component Row.
 */
@Composable
fun PolishSimpleTableRow(
    texts: List<String>,
    weights: List<Float>,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    isEven: Boolean = false
) {
    val bg = if (isEven) Color(0xFFF3EDF7).copy(alpha = 0.5f) else Color.White
    val rowModifier = if (onClick != null) modifier.clickable(onClick = onClick) else modifier
    
    Card(
        colors = CardDefaults.cardColors(containerColor = bg),
        shape = RoundedCornerShape(0.dp),
        modifier = rowModifier
            .fillMaxWidth()
            .border(0.5.dp, Color(0xFFCAC4D0).copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            texts.forEachIndexed { index, txt ->
                val weight = weights.getOrElse(index) { 1f }
                Text(
                    text = txt,
                    modifier = Modifier.weight(weight),
                    fontSize = 12.sp,
                    color = Color(0xFF1D1B20),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
