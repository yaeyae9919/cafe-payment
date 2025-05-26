package com.cafe.payment.user.repository.jpa.converter

@JvmInline
value class EncryptedString(val value: String) {
    override fun toString(): String = value
}
