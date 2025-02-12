package com.moksh.imposterai.presentation.auth

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moksh.imposterai.presentation.auth.viewmodel.AuthViewModel
import com.moksh.imposterai.presentation.auth.viewmodel.LoginEvent
import com.moksh.imposterai.presentation.auth.viewmodel.LoginState
import com.moksh.imposterai.presentation.common.ObserveAsEvents
import com.moksh.imposterai.presentation.common.PrimaryButton
import com.moksh.imposterai.presentation.common.PrimaryTextField
import com.moksh.imposterai.presentation.core.theme.ImposterAiTheme

@Composable
fun LoginScreen(
    onSignUpClick: () -> Unit,
    onLoginSuccessful: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    ObserveAsEvents(
        flow = viewModel.loginSharedFlow
    ) { event ->
        when (event) {
            is LoginEvent.LoginSuccessful -> {
                onLoginSuccessful()
            }

            is LoginEvent.LoginError -> {
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
    LoginScreenView(
        state = viewModel.loginState.collectAsStateWithLifecycle().value,
        onSignUpClick = onSignUpClick,
        onLoginClick = viewModel::onLoginClick,
        onPasswordChange = viewModel::onLoginPasswordChange,
        onUsernameChange = viewModel::onLoginUsernameChange
    )
}

@Composable
private fun LoginScreenView(
    state: LoginState,
    onSignUpClick: () -> Unit,
    onLoginClick: () -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Login",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                )
            )

            Text(
                text = "Please Sign in to continue.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSecondary
                ),
                modifier = Modifier
                    .padding(vertical = 8.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))
            PrimaryTextField(
                value = state.username,
                onValueChange = { onUsernameChange(it) },
                label = "Username"
            )
            Spacer(modifier = Modifier.height(16.dp))
            PrimaryTextField(
                value = state.password,
                isPasswordField = true,
                onValueChange = { onPasswordChange(it) },
                label = "Password"
            )
            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(onClick = onLoginClick, text = "Login", isLoading = state.isLoading)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.align(Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Don't have an account? ",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSecondary
                    ),
                )

                Text(
                    text = "Sign Up",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color =
                        MaterialTheme.colorScheme.onSecondary
                    ),
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onSignUpClick() }
                )
            }
        }
    }
}


@Composable
@Preview
private fun LoginScreenPreview() {
    ImposterAiTheme {
        LoginScreenView(
            state = LoginState(),
            onSignUpClick = {}, onLoginClick = {}, onPasswordChange = {},
            onUsernameChange = {})
    }
}
