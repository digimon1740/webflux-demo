package com.kakaopay.kazuya.webfluxdemo.dto

import com.kakaopay.kazuya.webfluxdemo.entity.Content
import java.time.LocalDateTime

data class ContentResponse(
    val id: Long? = null,
    val memo: String,
    val updatedAt: LocalDateTime,
) {

    companion object {
        operator fun invoke(entity: Content) = ContentResponse(
            id = entity.id,
            memo = entity.memo,
            updatedAt = entity.updatedAt,
        )
    }
}