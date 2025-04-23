package com.example.billsia.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.billsia.databinding.ItemArticuloBinding
import com.example.billsia.model.Articulo

class ArticuloAdapter(private val listaArticulos: List<Articulo>) : RecyclerView.Adapter<ArticuloAdapter.ArticuloViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticuloViewHolder {
        // Inflamos el layout del item
        val binding = ItemArticuloBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticuloViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticuloViewHolder, position: Int) {
        // Vinculamos los datos del articulo con la vista
        val articulo = listaArticulos[position]
        holder.bind(articulo)
    }

    override fun getItemCount(): Int = listaArticulos.size

    inner class ArticuloViewHolder(private val binding: ItemArticuloBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(articulo: Articulo) {
            // Asignamos el título y el resumen
            binding.textTitulo.text = articulo.titulo
            binding.textResumen.text = articulo.resumen

            // Acción al hacer clic sobre el artículo
            binding.root.setOnClickListener {
                // Abrimos el enlace del artículo en un navegador
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(articulo.url))
                itemView.context.startActivity(intent)
            }
        }
    }
}
