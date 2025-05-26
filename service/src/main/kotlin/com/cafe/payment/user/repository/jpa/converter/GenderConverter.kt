package com.cafe.payment.user.repository.jpa.converter

import com.cafe.payment.user.domain.Gender
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class GenderConverter : AttributeConverter<Gender, Int> {
    override fun convertToDatabaseColumn(attribute: Gender?): Int? {
        return when (attribute) {
            Gender.MALE -> 1
            Gender.FEMALE -> 2
            null -> null
        }
    }

    override fun convertToEntityAttribute(dbData: Int?): Gender? {
        return when (dbData) {
            1 -> Gender.MALE
            2 -> Gender.FEMALE
            else -> null
        }
    }
}
