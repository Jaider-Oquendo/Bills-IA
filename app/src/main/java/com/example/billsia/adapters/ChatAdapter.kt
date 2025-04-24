package com.example.billsia.adapters

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.billsia.databinding.ItemMensajeBinding
import com.example.billsia.data.entities.Mensaje
import com.example.billsia.R
import com.example.billsia.GeminiApiUtils

class ChatAdapter(
    private val context: Context,
    private val mensajes: MutableList<Mensaje>,
    private val eliminarMensaje: (Mensaje) -> Unit
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemMensajeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val mensaje = mensajes[position]
        holder.bind(mensaje)
    }

    override fun getItemCount(): Int = mensajes.size

    inner class ChatViewHolder(private val binding: ItemMensajeBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(mensaje: Mensaje) {
            val textoFormateado = formatearTexto(mensaje.texto)
            binding.textoMensaje.text = Html.fromHtml(textoFormateado, Html.FROM_HTML_MODE_LEGACY)

            // Estilo y alineación
            if (mensaje.esUsuario) {
                binding.textoMensaje.setBackgroundResource(R.drawable.bubble_user)
            } else {
                binding.textoMensaje.setBackgroundResource(R.drawable.bubble_api)
            }

            val params = binding.textoMensaje.layoutParams as ConstraintLayout.LayoutParams
            if (mensaje.esUsuario) {
                params.startToStart = ConstraintLayout.LayoutParams.UNSET
                params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            } else {
                params.endToEnd = ConstraintLayout.LayoutParams.UNSET
                params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            }
            binding.textoMensaje.layoutParams = params

            binding.root.setOnLongClickListener {
                mostrarDialogoOpciones(mensaje)
                true
            }

        }

        private fun formatearTexto(texto: String): String {
            var html = texto.trim()

            // Asteriscos mal escritos como "* *"
            html = html.replace(Regex("\\*\\s+\\*"), "**")
            // Títulos numerados tipo "**1. Texto:**"
            html = html.replace(Regex("\\*\\*(\\d+\\. .+?):\\*\\*")) {
                "<h3 style='margin-top: 1em; font-size: 10px; color: #2c3e50;'>${it.groupValues[1]}</h3>"
            }
            // Títulos de cierre tipo "**En resumen...**"
            html = html.replace(Regex("\\*\\*(En resumen.*?)\\*\\*")) {
                "<h3 style='margin-top: 1.5em; font-size: 10px; color: #2c3e50;'>${it.groupValues[1]}</h3>"
            }
            // Subtítulos en negrita (**texto**)
            html = html.replace(Regex("\\*\\*([^*]+?)\\*\\*")) {
                "<strong>${it.groupValues[1]}</strong>"
            }
            // Procesar listas con viñetas y agruparlas en <ul>
            html = html.replace(Regex("(?m)(^\\*\\s+.+(?:\\n\\*\\s+.+)*)")) { match ->
                val items = match.value.trim().lines().joinToString("\n") { line ->
                    val content = line.replace(Regex("^\\*\\s+"), "")
                    "<li style='list-style-type: disc; margin-left: 1em;'>$content</li>"
                }
                "<ul style='margin-left: 1.2em; margin-bottom: 1em;'>$items</ul>"
            }
            // Reemplazar cualquier * que no forme parte de lista o negrita por •
            html = html.replace(Regex("(?<!\\*)\\*(?!\\*)"), "•")
            // Saltos de línea dobles como separación de párrafos (evitar <br> después de <ul>)
            html = html.replace(Regex("(?<!</ul>)\\n\\n+")) {
                "<br><br>"
            }
            // Saltos de línea simples → separación de elementos visuales
            html = html.replace("\n", "<br>")
            // Retornar HTML envuelto en un contenedor estilizado
            return """
        <div style="font-family: sans-serif; font-size: 10px; line-height: 1.5; color: #2d2d2d;">
            $html
        </div>
    """.trimIndent()
        }


        private fun mostrarDialogoOpciones(mensaje: Mensaje) {
            val opciones = arrayOf("Copiar", "Eliminar")

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Selecciona una opción")
            builder.setItems(opciones) { dialog, which ->
                when (which) {
                    0 -> copiarAlPortapapeles(mensaje.texto)
                    1 -> confirmarEliminacion(mensaje)
                }
            }
            builder.setNegativeButton("Cancelar", null)
            builder.show()
        }

    }
    private fun copiarAlPortapapeles(texto: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Mensaje copiado", texto)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Mensaje copiado al portapapeles", Toast.LENGTH_SHORT).show()
    }

    private fun confirmarEliminacion(mensaje: Mensaje) {
        AlertDialog.Builder(context)
            .setMessage("¿Estás seguro de eliminar este mensaje?")
            .setPositiveButton("Sí") { _, _ ->
                eliminarMensaje(mensaje)
                mensajes.remove(mensaje)
                notifyDataSetChanged()
                Toast.makeText(context, "Mensaje eliminado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    fun enviarMensaje(mensaje: String) {
        val mensajeUsuario = Mensaje(texto = mensaje, esUsuario = true)
        mensajes.add(mensajeUsuario)
        notifyItemInserted(mensajes.size - 1)
        obtenerRespuestaIA(mensaje)
    }

    private fun obtenerRespuestaIA(prompt: String) {
        Thread {
            val respuestaIA = GeminiApiUtils.getGeminiResponse(prompt, context)
            Handler(Looper.getMainLooper()).post {
                val mensajeIA = Mensaje(texto = respuestaIA, esUsuario = false)
                mensajes.add(mensajeIA)
                notifyItemInserted(mensajes.size - 1)
            }
        }.start()
    }
}
