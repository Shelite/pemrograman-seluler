package com.example.pandora

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class Register : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register) // Pastikan nama layout benar
        
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etNama = findViewById<EditText>(R.id.etNama)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        
        btnRegister.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val nama = etNama.text.toString().trim()
            val password = etPassword.text.toString().trim()
            
            if (email.isEmpty() || nama.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // Gunakan API call
            lifecycleScope.launch {
                try {
                    val response = NetworkModule.apiService.register(
                        RegisterRequest(email, nama, password, "user")
                    )
                    if (response.isSuccessful) {
                        val registerResponse = response.body()
                        if (registerResponse?.success == true) {
                            Toast.makeText(this@Register, "Registrasi berhasil", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@Register, Login::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@Register, registerResponse?.message ?: "Registrasi gagal", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@Register, "Server error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@Register, "Error koneksi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
