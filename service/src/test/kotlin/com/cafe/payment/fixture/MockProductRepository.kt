package com.cafe.payment.fixture

import com.cafe.payment.product.domain.Product
import com.cafe.payment.product.domain.ProductId
import com.cafe.payment.product.repository.ProductRepository

class MockProductRepository : ProductRepository {
    private val products = mutableListOf<Product>()

    fun clear() {
        products.clear()
    }

    override fun findById(id: ProductId): Product? {
        return products.find { it.id == id }
    }

    override fun findByIds(ids: List<ProductId>): List<Product> {
        return products.filter { ids.contains(it.id) }
    }
}
