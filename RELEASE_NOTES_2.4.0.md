# Jotter v2.4.0 - Secure Vault Edition

## 🔐 NEW FEATURE: Secure Vault Encryption

This release adds military-grade AES-256 encryption to Jotter, allowing users to create a password-protected vault for sensitive notes.

### ✨ Features

- **AES-256 Encryption**: Military-grade encryption using Android KeyStore
- **Pull-Down Gesture Access**: Swipe down on home screen to access vault
- **Password Protection**: SHA-256 hashing with salt for secure authentication
- **Per-Note Encryption**: Toggle encryption on individual notes
- **Secure Settings UI**: Dedicated security settings screen for vault management
- **Zero Plain Text Storage**: Passwords never stored in plain text

### 🔧 Technical Improvements

- New `EncryptionUtil.kt` - AES-256 GCM encryption/decryption
- New `PasswordAuthUtil.kt` - Secure password hashing and verification
- New `VaultHelper.kt` - Vault utility functions
- New `SecuritySettingsScreen.kt` - Settings UI for vault configuration
- Enhanced `UserPreferencesRepository` - Vault settings persistence
- Enhanced `Note` model - Encryption fields support
- Enhanced `HomeScreen` - Pull-down gesture detection
- Enhanced `NotesRepositoryImpl` - Auto encrypt/decrypt operations

### 🛡️ Security Details

- **Key Storage**: Android KeyStore (hardware-backed when available)
- **Encryption Algorithm**: AES-256-GCM with NIST-approved parameters
- **IV Generation**: Cryptographically random 12-byte IVs per encryption
- **Password Hashing**: SHA-256 with 16-byte salt
- **Tag Length**: 128-bit GCM authentication tags

### 📱 How to Use

1. **Enable Vault**: Settings → Security Settings → Toggle "Secure Vault"
2. **Set Password**: Enter 6+ character password
3. **Access Vault**: Pull down (100dp+) on home screen
4. **Encrypt Notes**: Toggle encryption per-note in detail view

### 📋 Requirements

- Android 29+ (API level 29+)
- AndroidX libraries (already included)
- No external dependencies added

### 🐛 Bug Fixes

- N/A (New feature)

### 📦 Build Instructions

```bash
# Clone the repository
git clone https://github.com/usernamebiney/Jotterbiney.git
cd Jotterbiney

# Build with Gradle
./gradlew build

# Generate release APK
./gradlew assembleRelease

# Or use Android Studio
# File → Open → Select Jotterbiney folder → Build → Generate Signed Bundle/APK
```

### 🔗 Related Commits

- Add password hashing and verification utility for vault access
- Add AES-256 encryption utility for secure vault feature
- Add vault encryption settings to user preferences
- Add encryption fields to Note model
- Add VaultPasswordDialog UI component for password entry and setup
- Add pull-down gesture and vault access to HomeScreen
- Add SecuritySettingsScreen for vault encryption configuration
- Add encryption/decryption methods to NotesRepositoryImpl
- Add vault encryption state to HomeScreenViewModel
- Add VaultHelper utility for managing encrypted note operations

### 📝 Notes for Developers

The vault feature is fully integrated with the existing Jotter architecture:
- Uses Hilt for dependency injection
- Compatible with Room database
- Respects existing user preferences system
- Non-breaking changes to existing note structure
- Backward compatible with unencrypted notes

### ⚠️ Important

- **First Setup**: Setting up vault encryption for the first time will prompt users to create a password
- **Password Backup**: Ensure users remember their vault password (cannot be recovered if lost)
- **Battery Impact**: Minimal - only encrypts/decrypts on demand
- **Storage Impact**: Encrypted content slightly increases database size (~5-10%)

---

**Version**: 2.4.0-vault  
**Release Date**: 2026-07-31  
**Author**: Biney (usernamebiney)  
**License**: GNU General Public License v3.0
