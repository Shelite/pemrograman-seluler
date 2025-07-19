package com.example.pandora

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.content.Intent

class UserDashboardActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_dashboard)

        recyclerView = findViewById(R.id.recyclerViewUser)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ImageAdapter(mutableListOf(), this, false)
        recyclerView.adapter = adapter

        // Tampilkan list gambar di RecyclerView
        val db = DatabaseHelper(this)
        val images = db.getAllImages()
        adapter.updateData(images)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation_user)
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home_user -> {
                    // Refresh list gambar
                    val db = DatabaseHelper(this)
                    val images = db.getAllImages()
                    adapter.updateData(images)
                    true
                }
                R.id.nav_saldo_user -> {
                    // Tampilkan dialog saldo user
                    val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                    val email = prefs.getString("email", null)
                    val db = DatabaseHelper(this)
                    val saldo = if (email != null) db.getSaldo(email) else 0
                    androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Saldo Anda")
                        .setMessage("Saldo: Rp $saldo")
                        .setPositiveButton("OK", null)
                        .show()
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
}
