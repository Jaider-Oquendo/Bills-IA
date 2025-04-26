package com.example.billsia

import androidx.recyclerview.widget.DiffUtil

class ArticuloDiffCallback : DiffUtil.ItemCallback<Articulo>() {
    override fun areItemsTheSame(oldItem: Articulo, newItem: Articulo): Boolean {
        return oldItem.titulo == newItem.titulo // Usa un ID único si lo tienes
    }

    override fun areContentsTheSame(oldItem: Articulo, newItem: Articulo): Boolean {
        return oldItem == newItem
    }
}
