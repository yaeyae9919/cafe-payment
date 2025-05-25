package com.cafe.payment.fixture

import com.cafe.payment.product.domain.Product
import com.cafe.payment.product.domain.ProductId

object ProductFixture {
    private var productId: Long = 1L

    fun generateProductId() = productId++

    fun createProduct(
        id: Long = generateProductId(),
        name: String = "아메리카노",
        amount: Long = 4500L,
    ) = Product(
        id = ProductId(id),
        name = name,
        amount = amount.toBigDecimal(),
    )
}
