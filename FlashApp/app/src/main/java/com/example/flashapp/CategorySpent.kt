package com.example.flashapp

import androidx.room.ColumnInfo

data class CategorySpent(
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "SUM(amount)") val totalAmount: Double
)

