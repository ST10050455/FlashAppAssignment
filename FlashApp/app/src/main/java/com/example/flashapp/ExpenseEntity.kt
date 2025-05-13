package com.example.flashapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "EXPENSE" or "INCOME"
    val category: String,
    val description: String,
    val amount: Double,
    val date: String,
    val imagePath: String?
)
