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

class ImageAdapter(
    private var images: MutableList<Pair<Int, Pair<String, String>>>,
    private val context: Context
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.item_image)
        val nameView: TextView = view.findViewById(R.id.item_name)
        val btnEdit: ImageButton = view.findViewById(R.id.btn_edit)
        val btnDelete: ImageButton = view.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val (id, pair) = images[position]
        val (path, name) = pair
        val file = File(path)
        if (file.exists()) {
            holder.imageView.setImageURI(Uri.fromFile(file))
        } else {
            holder.imageView.setImageResource(R.drawable.ic_launcher_background)
        }
        holder.nameView.text = name

        holder.btnEdit.setOnClickListener {
            val input = EditText(context)
            input.setText(name)
            AlertDialog.Builder(context)
                .setTitle("Edit Nama Gambar")
                .setView(input)
                .setPositiveButton("Simpan") { _, _ ->
                    val db = DatabaseHelper(context)
                    db.updateImageName(id, input.text.toString())
                    images[position] = Pair(id, Pair(path, input.text.toString()))
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

    fun updateData(newImages: List<Pair<Int, Pair<String, String>>>) {
        images.clear()
        images.addAll(newImages)
        notifyDataSetChanged()
        // Tidak perlu aksi lain, biarkan list kosong
    }
}