package com.softcat.adventuremaker.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.softcat.adventuremaker.R

@Composable
fun EnterContent(
    state: AuthState.Enter,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onSwitchToRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.lock),
            contentDescription = null,
            modifier = Modifier
                .size(240.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

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

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(text = stringResource(R.string.login_switch), onClick = onLoginClick)

        Spacer(modifier = Modifier.height(32.dp))

        SecondaryButton(text = stringResource(R.string.register_submit), onClick = onSwitchToRegister)
    }
}