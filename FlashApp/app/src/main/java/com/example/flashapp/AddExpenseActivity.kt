package com.example.flashapp

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.flashapp.databinding.ActivityAddExpenseBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class AddExpenseActivity : AppCompatActivity() {

    // Declare variables for database and view binding
    private lateinit var db: AppDatabase
    private lateinit var binding: ActivityAddExpenseBinding

    // Holds selected image URI and category
    private var selectedImageUri: Uri? = null
    private var selectedCategory: String = ""

    companion object {
        const val IMAGE_PICK_CODE = 1001 // Request code for picking image
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize database
        db = AppDatabase.getDatabase(this)

        // Setup dropdown and date picker
        setupCategoryDropdown()
        setupDatePicker()

        // Handle image upload
        binding.uploadBtn.setOnClickListener { pickImageFromGallery() }

        // Handle saving the expense
        binding.saveBtn.setOnClickListener { saveExpense() }

        // Handle navigation to home/dashboard screen
        val homeIcon = findViewById<ImageView>(R.id.homeIcon)
        homeIcon.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Populate category dropdown from database
    private fun setupCategoryDropdown() {
        lifecycleScope.launch {
            // Get category names from DB on background thread
            val categories = withContext(Dispatchers.IO) {
                db.categoryDao().getAll()
            }.map { it.name }

            // Set adapter and handle selection
            val adapter = ArrayAdapter(this@AddExpenseActivity, android.R.layout.simple_spinner_item, categories)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.inputCategorySpinner.adapter = adapter

            // Set selected category when user chooses from dropdown
            binding.inputCategorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                    selectedCategory = categories[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    selectedCategory = ""
                }
            }
        }
    }

    // Show date picker when date field is clicked
    private fun setupDatePicker() {
        binding.inputDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Create date picker dialog
            val datePicker = DatePickerDialog(this, { _, y, m, d ->
                val selected = String.format("%04d-%02d-%02d", y, m + 1, d)
                binding.inputDate.setText(selected)
            }, year, month, day)

            datePicker.show()
        }
    }

    // Launch gallery to pick an image
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    // Handle image result from gallery
    @Deprecated("Deprecated in API 33+")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            binding.imagePreview.setImageURI(selectedImageUri)
        }
    }

    // Save expense data to Room database
    private fun saveExpense() {
        val type = if (binding.toggleType.isChecked) "INCOME" else "EXPENSE"
        val description = binding.inputDescription.text.toString().trim()
        val amountText = binding.inputAmount.text.toString().trim()
        val date = binding.inputDate.text.toString().trim()

        // Validate required fields
        if (selectedCategory.isEmpty() || amountText.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate amount format
        val amount = amountText.toDoubleOrNull()
        if (amount == null) {
            Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show()
            return
        }

        // Create expense entity object
        val expense = ExpenseEntity(
            type = type,
            category = selectedCategory,
            description = description,
            amount = amount,
            date = date,
            imagePath = selectedImageUri?.toString()
        )

        // Insert into DB using coroutine
        lifecycleScope.launch {
            db.expenseDao().insert(expense)
            Toast.makeText(this@AddExpenseActivity, "$type saved", Toast.LENGTH_SHORT).show()
            finish() // Close activity after saving
        }
    }
}
