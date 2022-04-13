package com.kakaopay.kazuya.webfluxdemo.dto

import com.kakaopay.kazuya.webfluxdemo.entity.User

data class UserResponse(
    val id: Long? = null,
    val username: String,
) {

    companion object {
        operator fun invoke(entity: User) = UserResponse(
            id = entity.id,
            username = entity.username,
        )

    }
}