package com.example.billsia.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.billsia.R
import com.example.billsia.models.Noticia

class NoticiasAdapter(private val context: Context, private val noticias: List<Noticia>) : RecyclerView.Adapter<NoticiasAdapter.NoticiaViewHolder>() {

    class NoticiaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.textTitle)
        val descriptionTextView: TextView = itemView.findViewById(R.id.textDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticiaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_noticia, parent, false)
        return NoticiaViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoticiaViewHolder, position: Int) {
        val noticia = noticias[position]
        holder.titleTextView.text = noticia.title
        holder.descriptionTextView.text = noticia.description

        // Agregar un listener para abrir el enlace cuando se hace clic en la noticia
        holder.itemView.setOnClickListener {
            // Abrir la URL de la noticia en un navegador
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(noticia.url))
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = noticias.size
}
