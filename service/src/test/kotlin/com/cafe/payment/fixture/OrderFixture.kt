package com.cafe.payment.fixture

import com.cafe.payment.order.domain.Order
import com.cafe.payment.order.domain.OrderId
import com.cafe.payment.order.domain.OrderItem
import com.cafe.payment.product.domain.Product
import com.cafe.payment.user.domain.UserId
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicLong

object OrderFixture {
    private var orderId: AtomicLong = AtomicLong(1)

    fun generateOrderId() = OrderId(orderId.incrementAndGet())

    fun createOrder(
        id: OrderId = generateOrderId(),
        buyerId: UserId = UserFixture.generateUserId(),
        items: List<OrderItem> = listOf(createOrderItem()),
        now: LocalDateTime = LocalDateTime.now(),
    ) = Order.create(
        id = id,
        buyerId = buyerId,
        items = items,
        now = now,
    )

    fun createOrderItem(
        product: Product = ProductFixture.createProduct(),
        quantity: Int = 1,
    ): OrderItem {
        return OrderItem(
            productId = product.id,
            quantity = quantity,
            productName = product.name,
            amount = product.amount,
        )
    }
}
