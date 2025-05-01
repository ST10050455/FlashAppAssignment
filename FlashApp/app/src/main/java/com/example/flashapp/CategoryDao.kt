package com.example.flashapp

import androidx.room.*

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    fun getAll(): List<Category>

    @Insert
    fun insertAll(vararg category: Category)

    @Update
    fun update(vararg category: Category)

    @Delete
    fun delete(category: Category)
}
