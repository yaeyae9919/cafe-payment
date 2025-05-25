package com.cafe.payment.product

import com.cafe.payment.library.HttpStatusCode
import com.cafe.payment.library.exception.CustomException
import com.cafe.payment.product.domain.ProductId

sealed class ProductException(
    statusCode: HttpStatusCode,
    errorCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(statusCode, errorCode, message, cause)

class ProductNotFoundException(
    errorCode: String,
    message: String,
) : ProductException(HttpStatusCode.NOT_FOUND, errorCode, message, null) {
    companion object {
        fun notFound(productId: ProductId): ProductNotFoundException {
            return ProductNotFoundException(
                errorCode = "PRODUCT_NOT_FOUND_001",
                message = "존재하지 않는 상품이 있어요. ($productId)",
            )
        }
    }
}
