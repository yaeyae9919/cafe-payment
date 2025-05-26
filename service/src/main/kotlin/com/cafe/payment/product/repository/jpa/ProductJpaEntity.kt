package com.cafe.payment.product.repository.jpa

import com.cafe.payment.product.domain.Product
import com.cafe.payment.product.domain.ProductId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "products")
class ProductJpaEntity(
    @Id
    val id: Long,
    @Column(nullable = false, length = 100)
    val name: String,
    @Column(nullable = false, length = 20)
    val amount: String,
) {
    fun toDomain(): Product {
        return Product(
            id = ProductId(this.id),
            name = this.name,
            amount = BigDecimal(this.amount),
        )
    }
}
