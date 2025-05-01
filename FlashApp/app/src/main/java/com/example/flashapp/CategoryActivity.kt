package com.example.flashapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CategoryActivity : AppCompatActivity() {


    private lateinit var labelInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var amountInput: EditText
    private lateinit var addTransactionBtn: Button
    private lateinit var closeBtn: Button
    private lateinit var toggleType: ToggleButton
    private lateinit var iconBulb: ImageView
    private lateinit var iconGasStation: ImageView
    private lateinit var iconDeliveryMan: ImageView
    private lateinit var iconPlane: ImageView
    private lateinit var iconGrocery: ImageView

    private var selectedIcon: Int? = null
    private var categoryType: String = "EXPENSE" // Default value

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

        labelInput.addTextChangedListener {
            labelInput.error = null
        }

        amountInput.addTextChangedListener {
            amountInput.error = null
        }

        toggleType.setOnCheckedChangeListener { _, isChecked ->
            categoryType = if (isChecked) "INCOME" else "EXPENSE"
        }

        iconBulb.setOnClickListener {
            selectedIcon = R.drawable.bulb
            // Optionally provide visual feedback that this icon is selected
            updateIconSelection(R.id.imageView3)
        }

        iconGasStation.setOnClickListener {
            selectedIcon = R.drawable.gasstation
            updateIconSelection(R.id.imageView5)
        }

        iconDeliveryMan.setOnClickListener {
            selectedIcon = R.drawable.deliveryman
            updateIconSelection(R.id.imageView6)
        }

        iconPlane.setOnClickListener {
            selectedIcon = R.drawable.plane
            updateIconSelection(R.id.imageView7)
        }

        iconGrocery.setOnClickListener {
            selectedIcon = R.drawable.grocery
            updateIconSelection(R.id.imageView8)
        }

        addTransactionBtn.setOnClickListener {
            val name = labelInput.text.toString()
            val description = descriptionInput.text.toString()
            val limit = amountInput.text.toString().toDoubleOrNull()

            if (name.isEmpty()) {
                labelInput.error = "Please enter a valid name"
            } else if (limit == null) {
                amountInput.error = "Please enter a valid limit"
            } else if (selectedIcon == null) {
                Toast.makeText(this, "Please choose an icon", Toast.LENGTH_SHORT).show()
            } else {
                val category = Category(
                    name = name,
                    description = description,
                    limit = limit,
                    type = categoryType,
                    iconResId = selectedIcon!!
                )
                insert(category)
            }
        }

        val homeIcon = findViewById<ImageView>(R.id.homeIcon)
        homeIcon.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun updateIconSelection(selectedId: Int) {
        // Reset visual feedback for all icons
        iconBulb.alpha = 0.5f
        iconGasStation.alpha = 0.5f
        iconDeliveryMan.alpha = 0.5f
        iconPlane.alpha = 0.5f
        iconGrocery.alpha = 0.5f

        // Highlight the selected icon
        when (selectedId) {
            R.id.imageView3 -> iconBulb.alpha = 1.0f
            R.id.imageView5 -> iconGasStation.alpha = 1.0f
            R.id.imageView6 -> iconDeliveryMan.alpha = 1.0f
            R.id.imageView7 -> iconPlane.alpha = 1.0f
            R.id.imageView8 -> iconGrocery.alpha = 1.0f
        }
    }

    private fun insert(category: Category) {
        val db = AppDatabase.getDatabase(this)
        GlobalScope.launch {
            db.categoryDao().insertAll(category)
            runOnUiThread { finish() }
        }
    }
}