package com.example.flashapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flashapp.databinding.ActivityViewAllExpensesBinding
import kotlinx.coroutines.launch

class ViewAllExpensesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewAllExpensesBinding
    private lateinit var db: AppDatabase
    private lateinit var adapter: ExpenseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewAllExpensesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        binding.recyclerViewAll.layoutManager = LinearLayoutManager(this)
        adapter = ExpenseAdapter(emptyList(), this)
        binding.recyclerViewAll.adapter = adapter

        loadAllExpenses()

        binding.homeIcon.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loadAllExpenses() {
        lifecycleScope.launch {
            val allExpenses = db.expenseDao().getAll()
            adapter.setData(allExpenses)
        }
    }

    override fun onResume() {
        super.onResume()
        loadAllExpenses()
    }
}
