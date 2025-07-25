package com.example.pandora

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import android.content.SharedPreferences
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class ImageAdapter(
    private var images: MutableList<Triple<Int, Pair<String, String>, String>>,
    private val context: Context,
    private val isAdmin: Boolean
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.item_image)
        val nameView: TextView = view.findViewById(R.id.item_name)
        val btnEdit: ImageButton = view.findViewById(R.id.btn_edit)
        val btnDelete: ImageButton = view.findViewById(R.id.btn_delete)
        val btnBuy: Button = view.findViewById(R.id.btn_buy)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val (id, pair, price) = images[position]
        val (path, name) = pair
        val file = File(path)
        if (file.exists()) {
            holder.imageView.setImageURI(Uri.fromFile(file))
        } else {
            holder.imageView.setImageResource(R.drawable.ic_launcher_background)
        }
        holder.nameView.text = "$name\nHarga: Rp $price"

        // Tampilkan tombol sesuai role
        if (isAdmin) {
            holder.btnEdit.visibility = View.VISIBLE
            holder.btnDelete.visibility = View.VISIBLE
            holder.btnBuy.visibility = View.GONE
        } else {
            holder.btnEdit.visibility = View.GONE
            holder.btnDelete.visibility = View.GONE
            holder.btnBuy.visibility = View.VISIBLE
        }

        holder.btnBuy.setOnClickListener {
            val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val email = prefs.getString("email", null)
            if (email == null) {
                Toast.makeText(context, "User tidak ditemukan!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // Get saldo from API
            if (context is androidx.lifecycle.LifecycleOwner) {
                (context as androidx.lifecycle.LifecycleOwner).lifecycleScope.launch {
                    try {
                        val saldoResponse = NetworkModule.apiService.getSaldo(email)
                        if (saldoResponse.isSuccessful && saldoResponse.body()?.success == true) {
                            val saldo = saldoResponse.body()?.saldo ?: 0
                            val harga = price.toIntOrNull() ?: 0
                            
                            if (saldo < harga) {
                                Toast.makeText(context, "Saldo tidak cukup! Saldo Anda: Rp $saldo", Toast.LENGTH_SHORT).show()
                                return@launch
                            }
                            
                            // Show confirmation dialog
                            AlertDialog.Builder(context)
                                .setTitle("Konfirmasi Pembelian")
                                .setMessage("Apakah Anda yakin ingin membeli gambar '$name' seharga Rp $price?\n\nSaldo saat ini: Rp $saldo")
                                .setPositiveButton("Beli") { _, _ ->
                                    // Purchase image
                                    (context as androidx.lifecycle.LifecycleOwner).lifecycleScope.launch {
                                        try {
                                            val purchaseResponse = NetworkModule.apiService.purchaseImage(
                                                PurchaseRequest(email, id)
                                            )
                                            
                                            if (purchaseResponse.isSuccessful && purchaseResponse.body()?.success == true) {
                                                val newSaldo = purchaseResponse.body()?.new_saldo ?: 0
                                                Toast.makeText(context, 
                                                    "Berhasil membeli gambar: $name\nSisa saldo: Rp $newSaldo", 
                                                    Toast.LENGTH_LONG).show()
                                            } else {
                                                Toast.makeText(context, 
                                                    purchaseResponse.body()?.message ?: "Pembelian gagal", 
                                                    Toast.LENGTH_SHORT).show()
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                                .setNegativeButton("Batal", null)
                                .show()
                        } else {
                            Toast.makeText(context, "Gagal mengambil data saldo", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        holder.btnEdit.setOnClickListener {
            val layout = LinearLayout(context)
            layout.orientation = LinearLayout.VERTICAL

            val inputName = EditText(context)
            inputName.setText(name)
            inputName.hint = "Nama gambar"
            layout.addView(inputName)

            val inputPrice = EditText(context)
            inputPrice.setText(price)
            inputPrice.hint = "Harga gambar"
            inputPrice.inputType = android.text.InputType.TYPE_CLASS_NUMBER
            layout.addView(inputPrice)

            AlertDialog.Builder(context)
                .setTitle("Edit Nama & Harga Gambar")
                .setView(layout)
                .setPositiveButton("Simpan") { _, _ ->
                    val db = DatabaseHelper(context)
                    db.updateImageName(id, inputName.text.toString())
                    db.updateImagePrice(id, inputPrice.text.toString())
                    images[position] = Triple(id, Pair(path, inputName.text.toString()), inputPrice.text.toString())
                    notifyItemChanged(position)
                }
                .setNegativeButton("Batal", null)
                .show()
        }

        holder.btnDelete.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Hapus Gambar")
                .setMessage("Yakin ingin menghapus gambar ini?")
                .setPositiveButton("Hapus") { _, _ ->
                    val db = DatabaseHelper(context)
                    db.deleteImage(id)
                    images.removeAt(position)
                    notifyItemRemoved(position)
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    fun updateData(newImages: List<Triple<Int, Pair<String, String>, String>>) {
        images.clear()
        images.addAll(newImages)
        notifyDataSetChanged()
    }
}
