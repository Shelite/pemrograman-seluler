package com.example.pandora

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class Login : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegister = findViewById<Button>(R.id.signin) // Ganti dari btnRegister ke signin
        
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // Gunakan API call
            lifecycleScope.launch {
                try {
                    val response = NetworkModule.apiService.login(LoginRequest(username, password))
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        if (loginResponse?.success == true) {
                            val user = loginResponse.user
                            Toast.makeText(this@Login, "Login berhasil", Toast.LENGTH_SHORT).show()
                            
                            // Simpan data user
                            val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                            prefs.edit().putString("email", username).apply()
                            
                            if (user?.level == "admin") {
                                startActivity(Intent(this@Login, DashBoard::class.java))
                            } else {
                                startActivity(Intent(this@Login, UserDashboardActivity::class.java))
                            }
                            finish()
                        } else {
                            Toast.makeText(this@Login, loginResponse?.message ?: "Login gagal", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@Login, "Server error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@Login, "Error koneksi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        btnRegister.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }
    }
}
