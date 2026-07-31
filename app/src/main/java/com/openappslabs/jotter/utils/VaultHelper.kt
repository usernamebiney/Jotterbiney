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

import com.openappslabs.jotter.data.model.Note

/**
 * Utility class for managing encrypted note operations
 * Handles encryption/decryption logic for vault notes
 */
object VaultHelper {
    
    /**
     * Encrypt a note's content
     * @param note The note to encrypt
     * @return Note with encrypted content and encryption flag set
     */
    fun encryptNote(note: Note): Note {
        return if (note.content.isNotEmpty()) {
            try {
                val encryptedContent = EncryptionUtil.encryptData(note.content)
                note.copy(
                    isEncrypted = true,
                    encryptedContent = encryptedContent,
                    content = "" // Clear original content after encryption
                )
            } catch (e: Exception) {
                e.printStackTrace()
                note // Return unchanged if encryption fails
            }
        } else {
            note
        }
    }
    
    /**
     * Decrypt a note's content
     * @param note The encrypted note
     * @return Note with decrypted content
     */
    fun decryptNote(note: Note): Note {
        return if (note.isEncrypted && note.encryptedContent.isNotEmpty()) {
            try {
                val decryptedContent = EncryptionUtil.decryptData(note.encryptedContent)
                note.copy(content = decryptedContent)
            } catch (e: Exception) {
                e.printStackTrace()
                note // Return unchanged if decryption fails
            }
        } else {
            note
        }
    }
    
    /**
     * Toggle encryption status of a note
     * @param note The note to toggle encryption for
     * @return Note with toggled encryption state
     */
    fun toggleNoteEncryption(note: Note): Note {
        return if (note.isEncrypted) {
            decryptNote(note)
        } else {
            encryptNote(note)
        }
    }
    
    /**
     * Check if a note is encrypted and safe to display
     * @param note The note to check
     * @return true if note is encrypted and locked, false if accessible
     */
    fun isNoteSafelyEncrypted(note: Note): Boolean {
        return note.isEncrypted && note.content.isEmpty() && note.encryptedContent.isNotEmpty()
    }
    
    /**
     * Validate vault password against stored hash
     * @param password The password to validate
     * @param storedHash The stored password hash
     * @return true if password matches, false otherwise
     */
    fun validateVaultPassword(password: String, storedHash: String): Boolean {
        return if (storedHash.isNotEmpty()) {
            PasswordAuthUtil.verifyPassword(password, storedHash)
        } else {
            false
        }
    }
    
    /**
     * Create password hash for vault
     * @param password The password to hash
     * @return Hashed password string
     */
    fun createVaultPasswordHash(password: String): String {
        return PasswordAuthUtil.hashPassword(password)
    }
    
    /**
     * Filter notes by encryption status
     * @param notes List of notes to filter
     * @param encrypted true to get encrypted notes, false for unencrypted
     * @return Filtered list of notes
     */
    fun filterByEncryptionStatus(notes: List<Note>, encrypted: Boolean): List<Note> {
        return notes.filter { it.isEncrypted == encrypted }
    }
    
    /**
     * Decrypt multiple notes
     * @param notes List of encrypted notes
     * @return List of decrypted notes
     */
    fun decryptNotes(notes: List<Note>): List<Note> {
        return notes.map { note ->
            if (note.isEncrypted) decryptNote(note) else note
        }
    }
    
    /**
     * Encrypt multiple notes
     * @param notes List of unencrypted notes
     * @return List of encrypted notes
     */
    fun encryptNotes(notes: List<Note>): List<Note> {
        return notes.map { note ->
            if (!note.isEncrypted && note.content.isNotEmpty()) encryptNote(note) else note
        }
    }
}
