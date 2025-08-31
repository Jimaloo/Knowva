package com.knowva.app.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.knowva.app.presentation.ui.theme.*

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onImeActionPerformed: (() -> Unit)? = null
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            isError = isError,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    onImeActionPerformed?.invoke()
                },
                onNext = {
                    onImeActionPerformed?.invoke()
                }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TriviaColors.Primary,
                focusedLabelColor = TriviaColors.Primary,
                cursorColor = TriviaColors.Primary,
                errorBorderColor = TriviaColors.Error,
                errorLabelColor = TriviaColors.Error
            ),
            shape = TriviaShapes.Small
        )

        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = TriviaColors.Error,
                style = TriviaTypography.LabelSmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun AuthPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
    isPasswordVisible: Boolean = false,
    onTogglePasswordVisibility: () -> Unit,
    imeAction: ImeAction = ImeAction.Next,
    onImeActionPerformed: (() -> Unit)? = null
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            isError = isError,
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    onImeActionPerformed?.invoke()
                },
                onNext = {
                    onImeActionPerformed?.invoke()
                }
            ),
            trailingIcon = {
                TextButton(onClick = onTogglePasswordVisibility) {
                    Text(
                        text = if (isPasswordVisible) "👁️" else "🫥",
                        color = if (isError) TriviaColors.Error else TriviaColors.OnSurfaceVariant
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TriviaColors.Primary,
                focusedLabelColor = TriviaColors.Primary,
                cursorColor = TriviaColors.Primary,
                errorBorderColor = TriviaColors.Error,
                errorLabelColor = TriviaColors.Error
            ),
            shape = TriviaShapes.Small
        )

        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = TriviaColors.Error,
                style = TriviaTypography.LabelSmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun AuthPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(Dimensions.ButtonHeight),
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = TriviaColors.Primary,
            contentColor = Color.White,
            disabledContainerColor = TriviaColors.OnSurfaceVariant,
            disabledContentColor = TriviaColors.Surface
        ),
        shape = TriviaShapes.Button
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                style = TriviaTypography.LabelLarge
            )
        }
    }
}

@Composable
fun AuthSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(Dimensions.ButtonHeight),
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = TriviaColors.Primary,
            disabledContentColor = TriviaColors.OnSurfaceVariant
        ),
        shape = TriviaShapes.Button
    ) {
        Text(
            text = text,
            style = TriviaTypography.LabelLarge
        )
    }
}

@Composable
fun AuthTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors(
            contentColor = TriviaColors.Primary,
            disabledContentColor = TriviaColors.OnSurfaceVariant
        )
    ) {
        Text(
            text = text,
            style = TriviaTypography.LabelLarge
        )
    }
}

@Composable
fun AuthErrorMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = TriviaColors.Error.copy(alpha = 0.1f)
        ),
        shape = TriviaShapes.Small
    ) {
        Row(
            modifier = Modifier.padding(Dimensions.SpacingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⚠️",
                modifier = Modifier.padding(end = Dimensions.SpacingSmall)
            )
            Text(
                text = message,
                color = TriviaColors.Error,
                style = TriviaTypography.BodyMedium
            )
        }
    }
}

@Composable
fun AuthSuccessMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = TriviaColors.Success.copy(alpha = 0.1f)
        ),
        shape = TriviaShapes.Small
    ) {
        Row(
            modifier = Modifier.padding(Dimensions.SpacingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "✅",
                modifier = Modifier.padding(end = Dimensions.SpacingSmall)
            )
            Text(
                text = message,
                color = TriviaColors.Success,
                style = TriviaTypography.BodyMedium
            )
        }
    }
}

@Composable
fun AuthHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = TriviaTypography.HeadlineLarge,
            color = TriviaColors.OnSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(Dimensions.SpacingSmall))
        Text(
            text = subtitle,
            style = TriviaTypography.BodyLarge,
            color = TriviaColors.OnSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AuthDivider(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = TriviaColors.OnSurfaceVariant.copy(alpha = 0.3f)
        )
        Text(
            text = text,
            style = TriviaTypography.LabelMedium,
            color = TriviaColors.OnSurfaceVariant,
            modifier = Modifier.padding(horizontal = Dimensions.SpacingMedium)
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = TriviaColors.OnSurfaceVariant.copy(alpha = 0.3f)
        )
    }
}