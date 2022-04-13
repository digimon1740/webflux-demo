package com.kakaopay.kazuya.webfluxdemo.dto

data class CompositeResponse(
    val user: UserResponse,
    val contents: List<ContentResponse>,
) {

    companion object {
        operator fun invoke(user: UserResponse, contents: List<ContentResponse>) =
            CompositeResponse(
                user = user,
                contents = contents,
            )
    }
}