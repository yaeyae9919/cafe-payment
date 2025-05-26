package com.cafe.payment.product.repository

import com.cafe.payment.product.domain.Product
import com.cafe.payment.product.domain.ProductId
import com.cafe.payment.product.repository.jpa.ProductJpaRepository
import org.springframework.stereotype.Repository

interface ProductRepository {
    fun findById(id: ProductId): Product?

    fun findByIds(ids: List<ProductId>): List<Product>
}

@Repository
class ProductRepositoryImpl(
    private val productJpaRepository: ProductJpaRepository,
) : ProductRepository {
    override fun findById(id: ProductId): Product? {
        return productJpaRepository.findById(id.value)?.toDomain()
    }

    override fun findByIds(ids: List<ProductId>): List<Product> {
        val longIds = ids.map { it.value }
        val entities = productJpaRepository.findByIdIn(longIds)

        // 원래 순서 유지
        return ids.mapNotNull { productId ->
            entities.find { it.id == productId.value }?.toDomain()
        }
    }
}
