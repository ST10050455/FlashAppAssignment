package com.example.flashapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var usernameInput: EditText
    private lateinit var recoverButton: Button
    private lateinit var resultText: TextView
    private lateinit var backToLoginBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        usernameInput = findViewById(R.id.username_input)
        recoverButton = findViewById(R.id.recover_btn)
        resultText = findViewById(R.id.result_text)

        // Go back to login
        val homeIcon = findViewById<ImageView>(R.id.homeIcon)
        homeIcon.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Handle password recovery
        recoverButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()

            if (username.isEmpty()) {
                Toast.makeText(this, "Please enter your username!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = AppDatabase.getDatabase(this)

            // Run DB query in background
            CoroutineScope(Dispatchers.IO).launch {
                val user = db.userDao().findUserByName(username)
                withContext(Dispatchers.Main) {
                    if (user != null) {
                        resultText.text = "Your Password is: ${user.password}"
                    } else {
                        resultText.text = "No user found with that username!"
                    }
                }
            }
        }
    }
}
