package com.urbanvoice.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.urbanvoice.app.domain.model.UserProfile

data class UserProfileDto(
    val id: Int,
    val name: String,
    @SerializedName("last_name") val lastName: String,
    val age: Int,
    val email: String,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("profile_image_url") val profileImageUrl: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

fun UserProfileDto.toDomain() = UserProfile(
    id = id, name = name, lastName = lastName, age = age,
    email = email, phoneNumber = phoneNumber,
    profileImageUrl = profileImageUrl, createdAt = createdAt, updatedAt = updatedAt
)

data class CreateProfileRequest(
    val name: String,
    @SerializedName("last_name") val lastName: String,
    val age: Int,
    val email: String,
    @SerializedName("phone_number") val phoneNumber: String,
    val password: String
)

data class UpdateProfileRequest(
    val name: String,
    @SerializedName("last_name") val lastName: String,
    val age: Int,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("profile_image_url") val profileImageUrl: String? = null
)
