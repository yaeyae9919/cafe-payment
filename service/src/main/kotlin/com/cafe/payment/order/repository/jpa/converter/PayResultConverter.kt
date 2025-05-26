package com.cafe.payment.order.repository.jpa.converter

import com.cafe.payment.billing.external.PayResult
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class PayResultConverter : AttributeConverter<PayResult?, String?> {
    companion object {
        private val objectMapper =
            ObjectMapper()
                .registerKotlinModule()
                .registerModule(JavaTimeModule())
    }

    override fun convertToDatabaseColumn(attribute: PayResult?): String? {
        return attribute?.let { objectMapper.writeValueAsString(it) }
    }

    override fun convertToEntityAttribute(dbData: String?): PayResult? {
        return dbData?.let { objectMapper.readValue(it, PayResult::class.java) }
    }
} 
