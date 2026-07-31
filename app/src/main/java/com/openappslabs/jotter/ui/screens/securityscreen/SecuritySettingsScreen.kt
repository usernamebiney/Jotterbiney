/*
 * Copyright (c) 2026 Open Apps Labs
 *
 * This file is part of Jotter
 *
 * Jotter is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Jotter is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Jotter.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.openappslabs.jotter.ui.screens.securityscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openappslabs.jotter.data.repository.UserPreferencesRepository
import com.openappslabs.jotter.ui.components.VaultPasswordDialog
import com.openappslabs.jotter.utils.PasswordAuthUtil
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecuritySettingsScreen(
    onBackClick: () -> Unit,
    userPreferencesRepository: UserPreferencesRepository
) {
    val coroutineScope = rememberCoroutineScope()
    val userPreferences by userPreferencesRepository.userPreferencesFlow.collectAsStateWithLifecycle(initialValue = null)
    
    var isVaultEnabled by remember { mutableStateOf(userPreferences?.isVaultEncryptionEnabled ?: false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var isSettingUpPassword by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Security Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Secure Vault Encryption Section
            Text(
                "Secure Vault",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Enable AES-256 encryption for sensitive notes. Access via pull-down gesture.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Toggle Encryption
            androidx.compose.material3.Surface(
                modifier = Modifier.align(Alignment.End)
            ) {
                Switch(
                    checked = isVaultEnabled,
                    onCheckedChange = { enabled ->
                        if (enabled) {
                            isSettingUpPassword = true
                            showPasswordDialog = true
                        } else {
                            isVaultEnabled = false
                            coroutineScope.launch {
                                userPreferencesRepository.setVaultEncryption(false)
                                userPreferencesRepository.setVaultPasswordHash("")
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Info Section
            Text(
                "How it works",
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "• Pull down on the home screen to access your vault\n" +
                "• Enter your password to unlock encrypted notes\n" +
                "• Notes are encrypted with AES-256 (military-grade)\n" +
                "• Your password is never stored in plain text\n" +
                "• No data loss - only access control",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (isVaultEnabled && userPreferences?.vaultPasswordHash?.isNotEmpty() == true) {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        isSettingUpPassword = false
                        showPasswordDialog = true
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Change Vault Password")
                }
            }
        }
    }

    if (showPasswordDialog) {
        VaultPasswordDialog(
            onAuthenticate = { password ->
                val passwordHash = PasswordAuthUtil.hashPassword(password)
                coroutineScope.launch {
                    userPreferencesRepository.setVaultEncryption(true)
                    userPreferencesRepository.setVaultPasswordHash(passwordHash)
                    isVaultEnabled = true
                    showPasswordDialog = false
                }
            },
            onDismiss = {
                showPasswordDialog = false
                if (isSettingUpPassword) {
                    isVaultEnabled = false
                }
            },
            isSetupMode = true
        )
    }
}
