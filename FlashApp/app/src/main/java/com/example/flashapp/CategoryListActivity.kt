package com.example.flashapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CategoryListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CategoryAdapter
    private lateinit var backBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_list)

        recyclerView = findViewById(R.id.categoryRecyclerView)
        adapter = CategoryAdapter(emptyList(), ::onDeleteClick)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val homeIcon = findViewById<ImageView>(R.id.homeIcon)
        homeIcon.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }

        fetchCategories()
    }

    private fun fetchCategories() {
        val db = AppDatabase.getDatabase(this)
        GlobalScope.launch {
            val categories = db.categoryDao().getAll()
            runOnUiThread {
                adapter.setData(categories)
            }
        }
    }

    // Handle delete operation
    private fun onDeleteClick(category: Category) {
        val db = AppDatabase.getDatabase(this)
        GlobalScope.launch {
            db.categoryDao().delete(category)
            fetchCategories()  // Refresh the list after deletion
        }
    }

    // Handle result when DetailedActivity is finished and the category is updated
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            fetchCategories()  // Refresh the list when update is made
        }
    }

    override fun onResume() {
        super.onResume()
        fetchCategories()  // Refresh the list every time the activity is resumed
    }
}
