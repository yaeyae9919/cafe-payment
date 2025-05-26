package com.cafe.payment.fixture

import com.cafe.payment.billing.domain.PayId
import com.cafe.payment.order.domain.Order
import com.cafe.payment.order.domain.OrderId
import com.cafe.payment.order.domain.OrderItem
import com.cafe.payment.order.domain.OrderItemId
import com.cafe.payment.order.domain.OrderPayConfirmation
import com.cafe.payment.product.domain.Product
import com.cafe.payment.user.domain.UserId
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicLong

object OrderFixture {
    private var orderId: AtomicLong = AtomicLong(1)

    fun generateOrderId() = OrderId(orderId.incrementAndGet())

    private var orderItemId: AtomicLong = AtomicLong(1)

    fun generateOrderItemId() = OrderItemId(orderItemId.incrementAndGet())

    fun createOrder(
        id: OrderId = generateOrderId(),
        payId: PayId = PayFixture.generatePayId(),
        buyerId: UserId = UserFixture.generateUserId(),
        items: List<OrderItem> = listOf(createOrderItem()),
        now: LocalDateTime = LocalDateTime.now(),
    ) = Order.create(
        id = id,
        payId = payId,
        buyerId = buyerId,
        orderItems = items,
        now = now,
    )

    fun createOrderItem(
        id: OrderItemId = generateOrderItemId(),
        orderId: OrderId = generateOrderId(),
        product: Product = ProductFixture.createProduct(),
        quantity: Int = 1,
    ): OrderItem {
        return OrderItem(
            id = id,
            orderId = orderId,
            productId = product.id,
            quantity = quantity,
            productName = product.name,
            amount = product.amount,
        )
    }

    fun createConfirmation(
        order: Order = createOrder(),
        paidAt: LocalDateTime = LocalDateTime.now(),
    ) = OrderPayConfirmation.paid(
        order = order,
        paidAt = paidAt,
    )
}
