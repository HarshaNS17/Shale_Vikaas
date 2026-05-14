package com.example.shaalevikaas.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude

data class Need(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val cost: Double = 0.0,
    val pledgedAmount: Double = 0.0,
    val imageUrl: String = "",
    val beforeImage: String = "",
    val afterImage: String = "",
    val status: String = "Open", // Open, In Progress, Completed
    val createdAt: Timestamp = Timestamp.now()
) {
    @get:Exclude
    val progress: Float
        get() = if (cost > 0) (pledgedAmount / cost).toFloat().coerceIn(0f, 1f) else 0f
}
