package com.urbanvoice.app.domain.model

data class UserProfile(
    val id: Int,
    val name: String,
    val lastName: String,
    val age: Int,
    val email: String,
    val phoneNumber: String,
    val profileImageUrl: String?,
    val createdAt: String,
    val updatedAt: String
)
