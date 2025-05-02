package com.example.flashapp

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DetailedActivity : AppCompatActivity() {

    // Declare UI components and category object
    private lateinit var category: Category
    private lateinit var labelInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var amountInput: EditText
    private lateinit var updateBtn: Button
    private lateinit var closeBtn: ImageButton
    private lateinit var toggleType: ToggleButton
    private lateinit var iconPreview: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed)

        // Retrieve the category passed from the previous activity
        category = intent.getSerializableExtra("category") as Category

        // Initialize UI components
        labelInput = findViewById(R.id.labelInput)
        amountInput = findViewById(R.id.amountInput)
        descriptionInput = findViewById(R.id.descriptionInput)
        updateBtn = findViewById(R.id.updateBtn)
        closeBtn = findViewById(R.id.closeBtn)
        toggleType = findViewById(R.id.toggle_type_detailed)
        iconPreview = findViewById(R.id.icon_preview_detailed)

        // Fill in existing category data into input fields
        labelInput.setText(category.name)
        amountInput.setText(category.limit.toString())
        descriptionInput.setText(category.description)

        // Set toggle button based on category type (income or expense)
        toggleType.isChecked = category.type == "INCOME"

        // Show current icon
        iconPreview.setImageResource(category.iconResId)

        // Handle update button click
        updateBtn.setOnClickListener {
            val name = labelInput.text.toString()
            val description = descriptionInput.text.toString()
            val limit = amountInput.text.toString().toDoubleOrNull()
            val type = if (toggleType.isChecked) "INCOME" else "EXPENSE"

            // Validate inputs
            if (name.isEmpty()) {
                labelInput.error = "Enter a valid name"
            } else if (limit == null) {
                amountInput.error = "Enter a valid limit"
            } else {
                // Create updated category object
                val updatedCategory = Category(
                    id = category.id,
                    name = name,
                    description = description,
                    limit = limit,
                    type = type,
                    iconResId = category.iconResId
                )

                // Save updated category to database
                update(updatedCategory)
            }
        }

        // Handle close button click
        closeBtn.setOnClickListener {
            finish() // Close the activity
        }
    }

    // Update category in the database
    private fun update(category: Category) {
        val db = AppDatabase.getDatabase(this)
        GlobalScope.launch {
            db.categoryDao().update(category)

            // Return to previous activity and signal refresh
            runOnUiThread {
                setResult(RESULT_OK)
                finish()
            }
        }
    }
}
