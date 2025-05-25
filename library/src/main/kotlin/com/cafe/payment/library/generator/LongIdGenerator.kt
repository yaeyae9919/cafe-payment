package com.cafe.payment.library.generator

import java.util.UUID

object LongIdGenerator {
    /**
     * UUID 생성
     * 다양한 ID 생성기가 있겠지만, 해당 프로젝트에서는 임시로 UUID 를 사용합니다.
     */
    fun generate(): Long {
        return kotlin.math.abs(UUID.randomUUID().mostSignificantBits)
    }
}
