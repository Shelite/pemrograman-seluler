package com.example.pandora

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class DashBoard : AppCompatActivity() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ImageAdapter
    private var tempImagePath: String? = null
    private val PICK_IMAGE_REQUEST = 1
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard) // Ganti dari activity_dashboard ke dashboard
        
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ImageAdapter(mutableListOf(), this, true)
        recyclerView.adapter = adapter
        
        // Load images from API
        loadImagesFromAPI()
        
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_upload -> {
                    openGallery()
                    true
                }
                R.id.nav_home -> {
                    loadImagesFromAPI()
                    true
                }
                R.id.nav_logout -> {
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
                Toast.makeText(this@DashBoard, "Error loading images: ${e.message}", Toast.LENGTH_SHORT).show()
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
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Upload Gambar")
        
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        
        val inputName = EditText(this)
        inputName.hint = "Nama gambar"
        layout.addView(inputName)
        
        val inputPrice = EditText(this)
        inputPrice.hint = "Harga gambar"
        layout.addView(inputPrice)
        
        builder.setView(layout)
        builder.setPositiveButton("Upload") { dialog, which ->
            val imageName = inputName.text.toString()
            val imagePrice = inputPrice.text.toString()
            
            if (imageName.isNotEmpty() && imagePrice.isNotEmpty()) {
                uploadImageToAPI(imageName, imagePrice)
            } else {
                Toast.makeText(this, "Nama dan harga harus diisi", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Batal") { dialog, which -> dialog.cancel() }
        builder.show()
    }
    
    private fun uploadImageToAPI(imageName: String, imagePrice: String) {
        tempImagePath?.let { path ->
            lifecycleScope.launch {
                try {
                    val response = NetworkModule.apiService.uploadImage(
                        ImageRequest(path, imageName, imagePrice)
                    )
                    if (response.isSuccessful) {
                        val uploadResponse = response.body()
                        if (uploadResponse?.success == true) {
                            Toast.makeText(this@DashBoard, "Upload berhasil", Toast.LENGTH_SHORT).show()
                            loadImagesFromAPI() // Refresh list
                        } else {
                            Toast.makeText(this@DashBoard, uploadResponse?.message ?: "Upload gagal", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@DashBoard, "Error upload: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
