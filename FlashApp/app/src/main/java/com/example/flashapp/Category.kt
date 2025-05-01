package com.example.flashapp

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val limit: Double,
    val type: String, // "INCOME" or "EXPENSE"
    val iconResId: Int // Add this line to save the icon
) : Serializable
