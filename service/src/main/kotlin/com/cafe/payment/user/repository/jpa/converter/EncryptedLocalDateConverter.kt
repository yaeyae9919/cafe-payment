package com.cafe.payment.user.repository.jpa.converter

import com.cafe.payment.user.repository.jpa.crypto.CryptoUtils
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.time.LocalDate

@Converter
class EncryptedLocalDateConverter : AttributeConverter<LocalDate, String> {
    override fun convertToDatabaseColumn(attribute: LocalDate?): String? {
        return attribute?.let { CryptoUtils.encrypt(it.toString()) }
    }

    override fun convertToEntityAttribute(dbData: String?): LocalDate? {
        return dbData?.let { LocalDate.parse(CryptoUtils.decrypt(it)) }
    }
}
