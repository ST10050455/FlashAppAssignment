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

    private lateinit var db: AppDatabase
    private lateinit var expenseAdapter: ExpenseAdapter
    private lateinit var binding: ActivityDashboardBinding

    private var allExpenses: List<ExpenseEntity> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        expenseAdapter = ExpenseAdapter(emptyList(), this)
        binding.recyclerView.adapter = expenseAdapter

        val filterOptions = arrayOf("ALL", "INCOME", "EXPENSE")
        val spinnerAdapter = ArrayAdapter(this, R.layout.spinner_item, filterOptions)
        binding.filterSpinner.adapter = spinnerAdapter

        binding.filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedFilter = filterOptions[position]
                applyFilter(selectedFilter)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.categoriesLayout.setOnClickListener {
            startActivity(Intent(this, CategoryActivity::class.java))
        }

        binding.expenseLayout.setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }

        binding.CategorylistLayout.setOnClickListener {
            Toast.makeText(this, "Going to ReportActivity", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, ReportActivity::class.java))
        }

        binding.viewAllText.setOnClickListener {
            startActivity(Intent(this, ViewAllExpensesActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadExpenses()
    }

    private fun loadExpenses() {
        lifecycleScope.launch {
            allExpenses = db.expenseDao().getAll().takeLast(5)
            applyFilter(binding.filterSpinner.selectedItem.toString())
        }
    }

    private fun applyFilter(filter: String) {
        val filteredList = when (filter) {
            "INCOME" -> allExpenses.filter { it.type == "INCOME" }
            "EXPENSE" -> allExpenses.filter { it.type == "EXPENSE" }
            else -> allExpenses
        }
        expenseAdapter.setData(filteredList)
    }
}
