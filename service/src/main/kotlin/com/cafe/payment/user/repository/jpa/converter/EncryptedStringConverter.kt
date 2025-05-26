package com.cafe.payment.user.repository.jpa.converter

import com.cafe.payment.user.repository.jpa.crypto.CryptoUtils
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class EncryptedStringConverter : AttributeConverter<String, String> {
    // 필드 값을 데이터베이스 컬럼으로 변경(암호화)
    override fun convertToDatabaseColumn(attribute: String?): String? {
        return attribute?.let { CryptoUtils.encrypt(it) }
    }

    // DB 컬럼을 필드 값으로 변경 (복호화)
    override fun convertToEntityAttribute(dbData: String?): String? {
        return dbData?.let {
            CryptoUtils.decrypt(it)
        }
    }
}
