package com.kakaopay.kazuya.webfluxdemo.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("contents")
data class Content(
    @Id val id: Long? = null,
    val userId: Long,
    val memo: String,
    @CreatedDate val createdAt: LocalDateTime,
    @LastModifiedDate val updatedAt: LocalDateTime,
)