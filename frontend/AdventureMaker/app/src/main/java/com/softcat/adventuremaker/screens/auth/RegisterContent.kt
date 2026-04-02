package com.softcat.adventuremaker.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Person

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.softcat.adventuremaker.R

@Composable
fun RegisterContent(
    state: AuthState.Register,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRepeatPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onSwitchToEnter: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.someone),
            contentDescription = null,
            modifier = Modifier
                .size(240.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Имя с иконкой человечка
        AuthTextField(
            value = state.name,
            onValueChange = onNameChange,
            label = stringResource(R.string.register_name_label),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Email с иконкой письма
        AuthTextField(
            value = state.email,
            onValueChange = onEmailChange,
            label = stringResource(R.string.register_email_label),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Пароль с замочком и глазом
        AuthTextField(
            value = state.password,
            onValueChange = onPasswordChange,
            label = stringResource(R.string.register_password_label),
            isPassword = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Повтор пароля с замочком и глазом
        AuthTextField(
            value = state.repeatedPassword,
            onValueChange = onRepeatPasswordChange,
            label = stringResource(R.string.register_repeat_password_label),
            isPassword = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(text = stringResource(R.string.register_submit), onClick = onRegisterClick)

        Spacer(modifier = Modifier.height(32.dp))

        SecondaryButton(text = stringResource(R.string.login_switch), onClick = onSwitchToEnter)
    }
}