package com.example.flashapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class CategoryActivity : AppCompatActivity() {

    private lateinit var labelInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var amountInput: EditText
    private lateinit var addTransactionBtn: Button
    private lateinit var toggleType: ToggleButton
    private lateinit var iconBulb: ImageView
    private lateinit var iconGasStation: ImageView
    private lateinit var iconDeliveryMan: ImageView
    private lateinit var iconPlane: ImageView
    private lateinit var iconGrocery: ImageView

    private var selectedIcon: Int? = null
    private var categoryType: String = "EXPENSE" // Default

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        labelInput = findViewById(R.id.cate_name)
        descriptionInput = findViewById(R.id.cate_description)
        amountInput = findViewById(R.id.cate_limit)
        addTransactionBtn = findViewById(R.id.cate_save_btn)
        toggleType = findViewById(R.id.toggle_type)
        iconBulb = findViewById(R.id.imageView3)
        iconGasStation = findViewById(R.id.imageView5)
        iconDeliveryMan = findViewById(R.id.imageView6)
        iconPlane = findViewById(R.id.imageView7)
        iconGrocery = findViewById(R.id.imageView8)

        labelInput.addTextChangedListener { labelInput.error = null }
        amountInput.addTextChangedListener { amountInput.error = null }

        toggleType.setOnCheckedChangeListener { _, isChecked ->
            categoryType = if (isChecked) "INCOME" else "EXPENSE"
        }

        // Icon click handlers
        iconBulb.setOnClickListener { selectIcon(R.drawable.bulb, R.id.imageView3) }
        iconGasStation.setOnClickListener { selectIcon(R.drawable.gasstation, R.id.imageView5) }
        iconDeliveryMan.setOnClickListener { selectIcon(R.drawable.deliveryman, R.id.imageView6) }
        iconPlane.setOnClickListener { selectIcon(R.drawable.plane, R.id.imageView7) }
        iconGrocery.setOnClickListener { selectIcon(R.drawable.grocery, R.id.imageView8) }

        addTransactionBtn.setOnClickListener { saveCategory() }

        findViewById<ImageView>(R.id.homeIcon).setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }

    private fun selectIcon(resId: Int, viewId: Int) {
        selectedIcon = resId
        updateIconSelection(viewId)
    }

    private fun updateIconSelection(selectedId: Int) {
        iconBulb.alpha = 0.5f
        iconGasStation.alpha = 0.5f
        iconDeliveryMan.alpha = 0.5f
        iconPlane.alpha = 0.5f
        iconGrocery.alpha = 0.5f

        when (selectedId) {
            R.id.imageView3 -> iconBulb.alpha = 1.0f
            R.id.imageView5 -> iconGasStation.alpha = 1.0f
            R.id.imageView6 -> iconDeliveryMan.alpha = 1.0f
            R.id.imageView7 -> iconPlane.alpha = 1.0f
            R.id.imageView8 -> iconGrocery.alpha = 1.0f
        }
    }

    private fun saveCategory() {
        val name = labelInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()
        val limit = amountInput.text.toString().toDoubleOrNull()

        if (name.isEmpty()) {
            labelInput.error = "Please enter a valid name"
            return
        }

        if (limit == null) {
            amountInput.error = "Please enter a valid limit"
            return
        }

        if (selectedIcon == null) {
            Toast.makeText(this, "Please choose an icon", Toast.LENGTH_SHORT).show()
            return
        }

        val category = Category(
            name = name,
            description = description,
            limit = limit,
            type = categoryType,
            iconResId = selectedIcon!!
        )

        val db = AppDatabase.getDatabase(this)
        lifecycleScope.launch {
            db.categoryDao().insertAll(category)
            Toast.makeText(this@CategoryActivity, "Category saved!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
