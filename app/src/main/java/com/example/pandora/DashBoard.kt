package com.example.pandora

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.ImageView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File
import java.io.FileOutputStream
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DashBoard : AppCompatActivity() {
    private val PICK_IMAGE_REQUEST = 100
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ImageAdapter
    private var tempImagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ImageAdapter(mutableListOf(), this)
        recyclerView.adapter = adapter
        val btnDashboard: ImageButton = findViewById(R.id.btn_Hamburger)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        btnDashboard.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, setting())
                .commit()
        }

        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_upload -> {
                    openGallery()
                    true
                }
                R.id.nav_home -> {
                    // Tampilkan list gambar di RecyclerView
                    val db = DatabaseHelper(this)
                    val images = db.getAllImages()
                    adapter.updateData(images)
                    true
                }
                else -> false
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data

            imageUri?.let {
                val inputStream = contentResolver.openInputStream(it)
                val file = File(filesDir, "uploaded_${System.currentTimeMillis()}.jpg")
                val outputStream = FileOutputStream(file)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()

                tempImagePath = file.absolutePath
                showImageNameDialog()
            }
        }
    }

    private fun showImageNameDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Masukkan Nama Gambar")
        val input = android.widget.EditText(this)
        input.hint = "Nama gambar"
        builder.setView(input)
        builder.setPositiveButton("Simpan") { dialog, which ->
            val imageName = input.text.toString()
            tempImagePath?.let { path ->
                val db = DatabaseHelper(this)
                db.insertImage(path, imageName)
            }
        }
        builder.setNegativeButton("Batal") { dialog, which -> dialog.cancel() }
        builder.show()
    }
}