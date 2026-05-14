package com.example.shaalevikaas.model

import com.google.firebase.Timestamp

data class Announcement(
    val title: String = "",
    val message: String = "",
    val imageUrl: String = "",
    val createdAt: Timestamp = Timestamp.now()
)
