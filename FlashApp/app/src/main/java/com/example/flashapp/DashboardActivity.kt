package com.example.flashapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flashapp.databinding.ActivityDashboardBinding
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {

    // Declare variables for database, adapter, and view binding
    private lateinit var db: AppDatabase
    private lateinit var expenseAdapter: ExpenseAdapter
    private lateinit var binding: ActivityDashboardBinding

    // Holds all expenses (latest 5)
    private var allExpenses: List<ExpenseEntity> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the database
        db = AppDatabase.getDatabase(this)

        // Setup RecyclerView with empty adapter and vertical layout
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        expenseAdapter = ExpenseAdapter(emptyList(), this)
        binding.recyclerView.adapter = expenseAdapter

        // Set up filter options for the spinner
        val filterOptions = arrayOf("ALL", "INCOME", "EXPENSE")
        val spinnerAdapter = ArrayAdapter(this, R.layout.spinner_item, filterOptions)
        binding.filterSpinner.adapter = spinnerAdapter

        // Handle filter selection
        binding.filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedFilter = filterOptions[position]
                applyFilter(selectedFilter)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Navigate to Goal screen
        binding.trophyIcon.setOnClickListener {
            val intent = Intent(this, GoalActivity::class.java)
            startActivity(intent)
        }

        // Navigate to Category screen
        binding.categoriesLayout.setOnClickListener {
            startActivity(Intent(this, CategoryActivity::class.java))
        }

        // Navigate to Add Expense screen
        binding.expenseLayout.setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }

        // Navigate to Report screen
        binding.CategorylistLayout.setOnClickListener {
            Toast.makeText(this, "Going to ReportActivity", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, ReportActivity::class.java))
        }

        // Navigate to View All Expenses screen
        binding.viewAllText.setOnClickListener {
            startActivity(Intent(this, ViewAllExpensesActivity::class.java))
        }
    }

    // Refresh expenses list when returning to this screen
    override fun onResume() {
        super.onResume()
        loadExpenses()
    }

    // Load the last 5 expenses from the database
    private fun loadExpenses() {
        lifecycleScope.launch {
            allExpenses = db.expenseDao().getAll().takeLast(5)
            applyFilter(binding.filterSpinner.selectedItem.toString())
        }
    }

    // Apply filter to show only INCOME, EXPENSE, or ALL expenses
    private fun applyFilter(filter: String) {
        val filteredList = when (filter) {
            "INCOME" -> allExpenses.filter { it.type == "INCOME" }
            "EXPENSE" -> allExpenses.filter { it.type == "EXPENSE" }
            else -> allExpenses
        }

        // Update the adapter with filtered data
        expenseAdapter.setData(filteredList)
    }
}
