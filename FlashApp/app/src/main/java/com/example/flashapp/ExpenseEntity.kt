package com.example.flashapp

import androidx.room.Entity
import androidx.room.PrimaryKey

// Define a table named "expenses" for Room Database
@Entity(tableName = "expenses")
data class ExpenseEntity(
    // Primary key that auto-generates unique IDs
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    // Type of entry: either "EXPENSE" or "INCOME"
    val type: String,
    // Category name (e.g., Food, Travel, etc.)
    val category: String,
    // Description of the transaction
    val description: String,
    // Amount spent or earned
    val amount: Double,
    // Date of the transaction (stored as a String)
    val date: String,
    // Optional image path for a receipt or related image
    val imagePath: String?
)
