package com.example.flashapp

import androidx.room.*


@Dao
interface ExpenseDao {

    @Query("SELECT * FROM expenses")
    fun getAll(): List<ExpenseEntity>

    @Insert
    fun insert(expense: ExpenseEntity)

    @Update
    fun update(expense: ExpenseEntity)

    @Delete
    fun delete(expense: ExpenseEntity)

    // Get the total spent between two dates
    @Query("SELECT SUM(amount) FROM expenses WHERE date BETWEEN :startDate AND :endDate AND type = 'EXPENSE'")
    fun getTotalSpentBetweenDates(startDate: String, endDate: String): Double

    // Get the category breakdown between two dates
    @Query("SELECT category, SUM(amount) FROM expenses WHERE date BETWEEN :startDate AND :endDate AND type = 'EXPENSE' GROUP BY category")
    fun getCategoryBreakdownBetweenDates(startDate: String, endDate: String): List<CategorySpent>
    }



