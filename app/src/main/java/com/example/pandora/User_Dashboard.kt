package com.example.pandora

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class UserDashboardActivity : AppCompatActivity() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ImageAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_dashboard) // Pastikan nama layout benar
        
        recyclerView = findViewById(R.id.recyclerViewUser)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ImageAdapter(mutableListOf(), this, false)
        recyclerView.adapter = adapter
        
        // Load images from API
        loadImagesFromAPI()
        
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation_user)
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home_user -> {
                    loadImagesFromAPI()
                    true
                }
                R.id.nav_saldo_user -> {
                    showSaldoDialog()
                    true
                }
                R.id.nav_logout_user -> {
                    val intent = Intent(this, Login::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }
    }
    
    private fun loadImagesFromAPI() {
        lifecycleScope.launch {
            try {
                val response = NetworkModule.apiService.getImages()
                if (response.isSuccessful) {
                    val imagesResponse = response.body()
                    if (imagesResponse?.success == true) {
                        val images = imagesResponse.data.map { imageData ->
                            Triple(imageData.id, Pair(imageData.image_path, imageData.image_name), imageData.image_price)
                        }
                        adapter.updateData(images)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@UserDashboardActivity, "Error loading images: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun showSaldoDialog() {
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val email = prefs.getString("email", null)
        
        if (email != null) {
            lifecycleScope.launch {
                try {
                    val response = NetworkModule.apiService.getSaldo(email)
                    if (response.isSuccessful && response.body()?.success == true) {
                        val saldo = response.body()?.saldo ?: 0
                        androidx.appcompat.app.AlertDialog.Builder(this@UserDashboardActivity)
                            .setTitle("Saldo Anda")
                            .setMessage("Saldo: Rp $saldo")
                            .setPositiveButton("OK", null)
                            .show()
                    } else {
                        Toast.makeText(this@UserDashboardActivity, "Gagal mengambil data saldo", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@UserDashboardActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
