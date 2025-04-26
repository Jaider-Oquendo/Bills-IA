package com.example.billsia

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.billsia.databinding.ItemArticuloBinding

class ArticuloAdapter : ListAdapter<Articulo, ArticuloAdapter.ArticuloViewHolder>(ArticuloDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticuloViewHolder {
        val binding = ItemArticuloBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticuloViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticuloViewHolder, position: Int) {
        val articulo = getItem(position)
        holder.bind(articulo)
    }

    inner class ArticuloViewHolder(private val binding: ItemArticuloBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(articulo: Articulo) {
            binding.textTitulo.text = articulo.titulo
            binding.textResumen.text = articulo.resumen

            binding.root.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(articulo.url))
                itemView.context.startActivity(intent)
            }
        }
    }
}
