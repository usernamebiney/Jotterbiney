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

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom

object PasswordAuthUtil {
    
    fun hashPassword(password: String): String {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(salt)
        val hashedPassword = digest.digest(password.toByteArray())
        
        val combined = salt + hashedPassword
        return Base64.encodeToString(combined, Base64.DEFAULT)
    }
    
    fun verifyPassword(password: String, hash: String): Boolean {
        try {
            val combined = Base64.decode(hash, Base64.DEFAULT)
            val salt = combined.copyOfRange(0, 16)
            val storedHash = combined.copyOfRange(16, combined.size)
            
            val digest = MessageDigest.getInstance("SHA-256")
            digest.update(salt)
            val computedHash = digest.digest(password.toByteArray())
            
            return computedHash.contentEquals(storedHash)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}
