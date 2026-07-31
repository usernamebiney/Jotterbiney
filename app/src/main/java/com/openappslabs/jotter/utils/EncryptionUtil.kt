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

package com.openappslabs.jotter.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import java.security.KeyStore
import java.security.SecureRandom

object EncryptionUtil {
    
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "jotter_vault_key"
    private const val ALGORITHM = "AES/GCM/NoPadding"
    private const val KEY_SIZE = 256
    private const val GCM_TAG_LENGTH = 128
    private const val GCM_IV_LENGTH = 12
    
    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        
        val existingKey = keyStore.getKey(KEY_ALIAS, null)
        if (existingKey != null) {
            return existingKey as SecretKey
        }
        
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        val keyGenParams = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setKeySize(KEY_SIZE)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .build()
        
        keyGenerator.init(keyGenParams)
        return keyGenerator.generateKey()
    }
    
    fun encryptData(plainText: String): String {
        try {
            val key = getOrCreateKey()
            val cipher = Cipher.getInstance(ALGORITHM)
            
            // Generate random IV
            val iv = ByteArray(GCM_IV_LENGTH)
            SecureRandom().nextBytes(iv)
            
            val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec)
            
            val encryptedBytes = cipher.doFinal(plainText.toByteArray())
            
            // Combine IV + encrypted data
            val combined = iv + encryptedBytes
            return Base64.encodeToString(combined, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
    
    fun decryptData(encryptedData: String): String {
        try {
            val key = getOrCreateKey()
            val cipher = Cipher.getInstance(ALGORITHM)
            
            val combined = Base64.decode(encryptedData, Base64.DEFAULT)
            
            // Extract IV and encrypted data
            val iv = combined.copyOfRange(0, GCM_IV_LENGTH)
            val encrypted = combined.copyOfRange(GCM_IV_LENGTH, combined.size)
            
            val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec)
            
            val decryptedBytes = cipher.doFinal(encrypted)
            return String(decryptedBytes)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}
