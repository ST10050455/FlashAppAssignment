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

        category = intent.getSerializableExtra("category") as Category

        labelInput = findViewById(R.id.labelInput)
        amountInput = findViewById(R.id.amountInput)
        descriptionInput = findViewById(R.id.descriptionInput)
        updateBtn = findViewById(R.id.updateBtn)
        closeBtn = findViewById(R.id.closeBtn)
        toggleType = findViewById(R.id.toggle_type_detailed)
        iconPreview = findViewById(R.id.icon_preview_detailed)

        labelInput.setText(category.name)
        amountInput.setText(category.limit.toString())
        descriptionInput.setText(category.description)

        // Set the toggle state based on the category type
        toggleType.isChecked = category.type == "INCOME"

        // Display the current icon
        iconPreview.setImageResource(category.iconResId)

        updateBtn.setOnClickListener {
            val name = labelInput.text.toString()
            val description = descriptionInput.text.toString()
            val limit = amountInput.text.toString().toDoubleOrNull()
            val type = if (toggleType.isChecked) "INCOME" else "EXPENSE"

            if (name.isEmpty()) {
                labelInput.error = "Enter a valid name"
            } else if (limit == null) {
                amountInput.error = "Enter a valid limit"
            } else {
                val updatedCategory = Category(
                    id = category.id,
                    name = name,
                    description = description,
                    limit = limit,
                    type = type,
                    iconResId = category.iconResId // Keep the existing icon for now
                )
                update(updatedCategory)
            }
        }

        closeBtn.setOnClickListener {
            finish()
        }
    }

    private fun update(category: Category) {
        val db = AppDatabase.getDatabase(this)
        GlobalScope.launch {
            // Update the category in the database
            db.categoryDao().update(category)

            // Notify CategoryListActivity to refresh the displayed data
            runOnUiThread {
                // Call this to finish the activity and refresh the list in CategoryListActivity
                setResult(RESULT_OK)
                finish()
            }
        }
    }}