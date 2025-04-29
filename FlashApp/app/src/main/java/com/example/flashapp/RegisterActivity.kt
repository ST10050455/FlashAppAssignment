package com.example.flashapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.flashapp.databinding.ActivityRegisterBinding
import java.util.concurrent.Executors

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var db: AppDatabase
    private val executor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        binding.regpageButton.setOnClickListener {
            val username = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()
            val confirmPassword = binding.confirmpasswordInput.text.toString()

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            executor.execute {
                val user = db.userDao().findUserByName(username)
                if(user == null){
                    db.userDao().insert(UserEntity(username = username, password = password))
                    runOnUiThread{
                        Toast.makeText(this,"User Registered", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else{
                    runOnUiThread{
                        Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}