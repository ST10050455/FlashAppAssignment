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

    private lateinit var db: AppDatabase
    private lateinit var binding: ActivityAddExpenseBinding
    private var selectedImageUri: Uri? = null
    private var selectedCategory: String = ""

    companion object {
        const val IMAGE_PICK_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        setupCategoryDropdown()
        setupDatePicker()

        binding.uploadBtn.setOnClickListener { pickImageFromGallery() }

        binding.saveBtn.setOnClickListener { saveExpense() }

        val homeIcon = findViewById<ImageView>(R.id.homeIcon)
        homeIcon.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupCategoryDropdown() {
        lifecycleScope.launch {
            val categories = withContext(Dispatchers.IO) {
                db.categoryDao().getAll()
            }.map { it.name }

            val adapter = ArrayAdapter(this@AddExpenseActivity, android.R.layout.simple_spinner_item, categories)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.inputCategorySpinner.adapter = adapter

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

    private fun setupDatePicker() {
        binding.inputDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, y, m, d ->
                val selected = String.format("%04d-%02d-%02d", y, m + 1, d) // YYYY-MM-DD
                binding.inputDate.setText(selected)
            }, year, month, day)

            datePicker.show()
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    @Deprecated("Deprecated in API 33+")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            binding.imagePreview.setImageURI(selectedImageUri)
        }
    }

    private fun saveExpense() {
        val type = if (binding.toggleType.isChecked) "INCOME" else "EXPENSE"
        val description = binding.inputDescription.text.toString().trim()
        val amountText = binding.inputAmount.text.toString().trim()
        val date = binding.inputDate.text.toString().trim()

        if (selectedCategory.isEmpty() || amountText.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null) {
            Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show()
            return
        }

        val expense = ExpenseEntity(
            type = type,
            category = selectedCategory,
            description = description,
            amount = amount,
            date = date,
            imagePath = selectedImageUri?.toString()
        )

        lifecycleScope.launch {
            db.expenseDao().insert(expense)
            Toast.makeText(this@AddExpenseActivity, "$type saved", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
