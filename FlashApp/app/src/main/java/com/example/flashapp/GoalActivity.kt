package com.example.flashapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class GoalActivity : AppCompatActivity() {

    private lateinit var currentGoalAmount: TextView
    private lateinit var minGoalInput: EditText
    private lateinit var maxGoalInput: EditText
    private lateinit var setGoalButton: Button
    private lateinit var monthSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal)

        // Initialize views
        currentGoalAmount = findViewById(R.id.current_goal_amount)
        minGoalInput = findViewById(R.id.minGoalInput)
        maxGoalInput = findViewById(R.id.maxGoalInput)
        setGoalButton = findViewById(R.id.set_goal_button)
        monthSpinner = findViewById(R.id.month_spinner)

        // Populate the spinner with months
        val months = arrayOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, months)
        monthSpinner.adapter = adapter

        // Set the button click listener
        setGoalButton.setOnClickListener {
            setGoal()
        }

        val homeBtn = findViewById<ImageView>(R.id.Homebtn)
        homeBtn.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish() // Optional: finish current activity so it doesn't stay in the back stack
        }
    }

    private fun setGoal() {
        val minGoalStr = minGoalInput.text.toString()
        val maxGoalStr = maxGoalInput.text.toString()

        if (minGoalStr.isEmpty() || maxGoalStr.isEmpty()) {
            Toast.makeText(this, "Please enter both minimum and maximum goals", Toast.LENGTH_SHORT).show()
            return
        }

        val minGoal = minGoalStr.toDoubleOrNull()
        val maxGoal = maxGoalStr.toDoubleOrNull()

        if (minGoal == null || maxGoal == null) {
            Toast.makeText(this, "Enter valid numeric values", Toast.LENGTH_SHORT).show()
            return
        }

        if (minGoal > maxGoal) {
            Toast.makeText(this, "Minimum goal cannot be greater than maximum goal", Toast.LENGTH_SHORT).show()
            return
        }

        // Display the current goal (showing max as the main reference)
        currentGoalAmount.text = "R${String.format("%.2f", maxGoal)}"
        Toast.makeText(this, "Goals set for ${monthSpinner.selectedItem}", Toast.LENGTH_SHORT).show()
    }
} 


