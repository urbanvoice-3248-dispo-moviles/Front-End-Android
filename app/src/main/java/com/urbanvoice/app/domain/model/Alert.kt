package com.urbanvoice.app.domain.model

data class Alert(
    val id: Int,
    val userId: Int,
    val type: String,
    val title: String,
    val message: String,
    val latitude: Double?,
    val longitude: Double?,
    val isRead: Boolean,
    val createdAt: String
)
