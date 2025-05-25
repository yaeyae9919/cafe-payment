package com.cafe.payment.controller

import com.cafe.payment.auth.UserContext
import com.cafe.payment.order.application.OrderPayUsecase
import com.cafe.payment.product.domain.ProductId
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class OrderController(
    private val orderPayUsecase: OrderPayUsecase,
) {
    @PostMapping("/order")
    fun order(
        @RequestBody form: OrderForm,
    ): OrderResultPresentation {
        val userId = UserContext.getCurrentUserId()
        val orderResult =
            orderPayUsecase.orderAndPay(
                buyerId = userId,
                orderItems =
                    form.orderItems.map {
                        OrderPayUsecase.OrderItem(
                            productId = ProductId(it.productId.toLong()),
                            quantity = it.quantity,
                        )
                    },
            )

        return OrderResultPresentation(
            status = orderResult.status,
            orderId = orderResult.orderId.toString(),
            message =
                when (orderResult.status) {
                    OrderPayUsecase.OrderPayStatus.SUCCESS -> "주문 완료되었어요."
                    OrderPayUsecase.OrderPayStatus.FAILED -> "주문 실패했어요."
                },
        )
    }
}

data class OrderResultPresentation(
    val status: OrderPayUsecase.OrderPayStatus,
    val orderId: String,
    val message: String,
)

data class OrderForm(
    val orderItems: List<OrderItemForm>,
) {
    data class OrderItemForm(
        val productId: String,
        val quantity: Int,
    )
}
