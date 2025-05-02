package com.example.flashapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class ForgotPasswordActivity : AppCompatActivity() {

    // Declare UI components
    private lateinit var usernameInput: EditText
    private lateinit var recoverButton: Button
    private lateinit var resultText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        // Initialize views
        usernameInput = findViewById(R.id.username_input)
        recoverButton = findViewById(R.id.recover_btn)
        resultText = findViewById(R.id.result_text)

        // Navigate back to login screen when home icon is clicked
        val homeIcon = findViewById<ImageView>(R.id.homeIcon)
        homeIcon.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // When recover button is clicked
        recoverButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()

            // Check if username field is empty
            if (username.isEmpty()) {
                Toast.makeText(this, "Please enter your username!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = AppDatabase.getDatabase(this)

            // Launch a coroutine to query the database
            CoroutineScope(Dispatchers.IO).launch {
                val user = db.userDao().findUserByName(username)

                // Switch back to the main thread to update UI
                withContext(Dispatchers.Main) {
                    if (user != null) {
                        // Display the user's password (not secure for real apps!)
                        resultText.text = "Your Password is: ${user.password}"
                    } else {
                        resultText.text = "No user found with that username!"
                    }
                }
            }
        }
    }
}
