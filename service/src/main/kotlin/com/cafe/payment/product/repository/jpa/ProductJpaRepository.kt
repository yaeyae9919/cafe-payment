package com.cafe.payment.product.repository.jpa

import org.springframework.data.repository.Repository

interface ProductJpaRepository : Repository<ProductJpaEntity, Long> {
    fun findById(id: Long): ProductJpaEntity?

    fun findByIdIn(ids: List<Long>): List<ProductJpaEntity>
}
