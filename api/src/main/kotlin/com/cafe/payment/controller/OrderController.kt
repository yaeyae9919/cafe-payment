package com.cafe.payment.controller

import com.cafe.payment.auth.UserContext
import com.cafe.payment.order.application.OrderPayUsecase
import com.cafe.payment.order.domain.OrderId
import com.cafe.payment.product.domain.ProductId
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "상품 주문 및 주문 취소")
@RestController
@RequestMapping("/api")
class OrderController(
    private val orderPayUsecase: OrderPayUsecase,
) {
    @Operation(
        summary = "주문 및 결제 준비",
        description = "상품을 선택하고 주문을 준비합니다. 실제 결제가 이루어지기 전 요청합니다. 상품 id는 1,2,3으로 고정해주세요.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "주문 준비 성공",
                content = [Content(schema = Schema(implementation = OrderIdPresentation::class))],
            ),
        ],
    )
    @PostMapping("/order/prepare")
    fun prepare(
        @RequestBody form: OrderForm,
    ): OrderIdPresentation {
        val userId = UserContext.getCurrentUserId()
        val orderResult =
            orderPayUsecase.prepareOrder(
                buyerId = userId,
                orderItems =
                    form.orderItems.map {
                        OrderPayUsecase.OrderItem(
                            productId = ProductId(it.productId.toLong()),
                            quantity = it.quantity,
                        )
                    },
            )
        return OrderIdPresentation(orderId = orderResult.toString())
    }

    @Operation(
        summary = "상품 주문",
        description = "상품을 주문합니다. 결제 실패 시 주문도 취소됩니다.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "상품 주문 결과를 응답합니다.",
                content = [Content(schema = Schema(implementation = OrderResultPresentation::class))],
            ),
        ],
    )
    @PostMapping("/order/{orderId}/pay")
    fun order(
        @Parameter(description = "주문 ID", required = true, example = "1")
        @PathVariable orderId: String,
    ): OrderResultPresentation {
        val userId = UserContext.getCurrentUserId()
        val orderResult =
            orderPayUsecase.orderPay(
                requesterId = userId,
                orderId = OrderId(orderId.toLong()),
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

    @Operation(
        summary = "상품 주문 취소",
        description = "상품 주문 취소합니다. 결제도 함께 취소됩니다.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "상품 주문 취소 결과를 응답합니다.",
                content = [Content(schema = Schema(implementation = OrderResultPresentation::class))],
            ),
        ],
    )
    @PostMapping("/order/{orderId}/refund")
    fun refund(
        @Parameter(description = "주문 ID", required = true, example = "1")
        @PathVariable orderId: String,
    ): OrderResultPresentation {
        val userId = UserContext.getCurrentUserId()
        val orderResult =
            orderPayUsecase.orderPayRefund(
                requesterId = userId,
                orderId = OrderId(orderId.toLong()),
            )

        return OrderResultPresentation(
            status = orderResult.status,
            orderId = orderResult.orderId.toString(),
            message =
                when (orderResult.status) {
                    OrderPayUsecase.OrderPayStatus.SUCCESS -> "주문 취소 완료되었어요."
                    OrderPayUsecase.OrderPayStatus.FAILED -> "주문 취소 실패했어요."
                },
        )
    }
}

@Schema(description = "주문 ID 응답")
data class OrderIdPresentation(
    @Schema(description = "생성된 주문 ID", example = "1")
    val orderId: String,
)

@Schema(description = "주문 처리 결과")
data class OrderResultPresentation(
    @Schema(description = "처리 상태", example = "SUCCESS || FAILED")
    val status: OrderPayUsecase.OrderPayStatus,
    @Schema(description = "주문 ID", example = "1")
    val orderId: String,
    @Schema(description = "처리 결과 메시지", example = "주문 완료되었어요.")
    val message: String,
)

@Schema(description = "주문 요청")
data class OrderForm(
    @Schema(description = "주문할 상품 목록")
    val orderItems: List<OrderItemForm>,
) {
    @Schema(description = "주문 상품 정보")
    data class OrderItemForm(
        @Schema(description = "상품 ID", example = "1")
        val productId: String,
        @Schema(description = "주문 수량", example = "2", minimum = "1")
        val quantity: Int,
    )
}
