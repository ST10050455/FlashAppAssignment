package com.example.flashapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.flashapp.databinding.ActivityMainBinding
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: AppDatabase
    private val executor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen() // Important: call this first
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        binding.loginBtn.setOnClickListener {
            validateAndLogin()
        }

        binding.registerBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateAndLogin() {
        val username = binding.usernameLogin.text.toString().trim()
        val password = binding.passwordLogin.text.toString().trim()

        binding.usernameLogin.error = null
        binding.passwordLogin.error = null

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please Enter Username and Password", Toast.LENGTH_SHORT).show()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            Toast.makeText(this, "Invalid Email Format", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 Characters", Toast.LENGTH_SHORT).show()
            return
        }

        performLogin(username, password)
    }

    private fun performLogin(username: String, password: String) {
        executor.execute {
            val user = db.userDao().findUser(username, password)
            runOnUiThread {
                if (user != null) {
                    Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@MainActivity, DashboardActivity::class.java)
                    intent.putExtra("username", user.username)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Login Failed!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
