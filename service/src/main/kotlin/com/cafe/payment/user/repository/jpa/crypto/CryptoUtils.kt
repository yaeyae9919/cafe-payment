package com.cafe.payment.user.repository.jpa.crypto

import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object CryptoUtils {
    // 실제 운영에서는 환경변수나 설정파일에서 관리해야 합니다
    private val secretKey = "MySecretKey12345" // 16바이트 키
    private val algorithm = "AES"

    fun encrypt(plainText: String): String {
        val cipher = Cipher.getInstance(algorithm)
        val keySpec = SecretKeySpec(secretKey.toByteArray(), algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        val encrypted = cipher.doFinal(plainText.toByteArray())
        return Base64.getEncoder().encodeToString(encrypted)
    }

    fun decrypt(encryptedText: String): String {
        val cipher = Cipher.getInstance(algorithm)
        val keySpec = SecretKeySpec(secretKey.toByteArray(), algorithm)
        cipher.init(Cipher.DECRYPT_MODE, keySpec)
        val decoded = Base64.getDecoder().decode(encryptedText)
        val decrypted = cipher.doFinal(decoded)
        return String(decrypted)
    }
}
