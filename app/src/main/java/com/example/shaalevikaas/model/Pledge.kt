package com.example.shaalevikaas.model

import com.google.firebase.Timestamp

data class Pledge(
    val pledgeId: String = "",
    val alumniId: String = "",
    val alumniName: String = "",
    val needId: String = "",
    val amount: Double = 0.0,
    val timestamp: Timestamp = Timestamp.now()
)
