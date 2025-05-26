package com.cafe.payment.fixture

import com.cafe.payment.product.domain.Product
import com.cafe.payment.product.domain.ProductId

object ProductFixture {
    private var productId: Long = 1L

    fun generateProductId() = ProductId(productId++)

    fun createProduct(
        id: ProductId = generateProductId(),
        name: String = "아메리카노",
        amount: Long = 4500L,
    ) = Product(
        id = id,
        name = name,
        amount = amount.toBigDecimal(),
    )
}
