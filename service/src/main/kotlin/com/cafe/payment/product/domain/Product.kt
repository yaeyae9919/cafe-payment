package com.cafe.payment.product.domain

import java.math.BigDecimal

@JvmInline
value class ProductId(val value: Long) {
    override fun toString(): String {
        return value.toString()
    }
}

/**
 * 상품 데이터는 모두 유효한 값(상품명, 가격 등)을 가지고 있다고 가정한다.
 */
class Product(
    val id: ProductId,
    val name: String,
    val price: BigDecimal,
)
