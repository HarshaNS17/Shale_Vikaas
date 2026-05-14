package com.example.shaalevikaas.model

data class Alumni(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val passingYear: String = "",
    val profession: String = "",
    val donatedAmount: Double = 0.0,
    val profileImage: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
