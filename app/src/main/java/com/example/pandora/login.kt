package com.example.pandora

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class Login : AppCompatActivity() {
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignin: Button
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        // Inisialisasi view
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnSignin = findViewById(R.id.signin)
        dbHelper = DatabaseHelper(this)

        // Set click listener untuk tombol daftar
        btnSignin.setOnClickListener {
            startActivity(Intent(this@Login, Register::class.java))
        }

        // Set click listener untuk tombol login
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            // Validasi input
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Mohon isi semua field", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!dbHelper.isEmailExist(username)) {
                Toast.makeText(this, "Email tidak ditemukan", Toast.LENGTH_SHORT).show()
            } else if (!dbHelper.isPasswordCorrect(username, password)) {
                Toast.makeText(this, "Password salah", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, DashBoard::class.java)
                startActivity(intent)
                finish()
            }

        }
    }
}
