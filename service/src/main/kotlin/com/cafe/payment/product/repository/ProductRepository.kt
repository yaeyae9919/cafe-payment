package com.cafe.payment.product.repository

import com.cafe.payment.product.domain.Product
import com.cafe.payment.product.domain.ProductId
import org.springframework.stereotype.Repository
import java.math.BigDecimal

interface ProductRepository {
    fun findById(id: ProductId): Product?

    fun findByIds(ids: List<ProductId>): List<Product>
}

@Repository
class InMemoryProductRepository : ProductRepository {
    private val products =
        mutableListOf<Product>(
            Product(
                id = ProductId(1L),
                name = "엽기떡볶이",
                amount = BigDecimal(14000),
            ),
            Product(
                id = ProductId(2L),
                name = "마라마라샹궈",
                amount = BigDecimal(25000),
            ),
            Product(
                id = ProductId(3L),
                name = "불닭볶음면",
                amount = BigDecimal(1800),
            ),
        )

    override fun findById(id: ProductId): Product? {
        return products.find { it.id == id }
    }

    override fun findByIds(ids: List<ProductId>): List<Product> {
        return products.filter { ids.contains(it.id) }
            .sortedBy { ids.indexOf(it.id) }
            .toList()
    }
}
