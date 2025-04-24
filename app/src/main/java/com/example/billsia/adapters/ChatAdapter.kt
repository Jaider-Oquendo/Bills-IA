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

        //funcion para formatear texto.
        private fun formatearTexto(texto: String): String {
            var html = texto.trim()

            // Normalizar espacios entre asteriscos mal escritos (ej. "* *" → "**")
            html = html.replace(Regex("\\*\\s+\\*"), "**")

            // Bloques de código
            html = html.replace(Regex("```(.?)```", RegexOption.DOT_MATCHES_ALL)) {
                "<pre><code>${it.groupValues[1]}</code></pre>"
            }

            // Código en línea
            html = html.replace(Regex("`([^`]+)`")) {
                "<code>${it.groupValues[1]}</code>"
            }

            // Títulos o encabezados marcados con **texto:**
            html = html.replace(Regex("\\*\\*\\s*(.+?)\\s*:\\s*\\*\\*", RegexOption.MULTILINE)) {
                "<b style='font-size: 16px;'>${it.groupValues[1].trim()}:</b>"
            }

            // Negrita general: **texto**
            html = html.replace(Regex("\\*\\*(.+?)\\*\\*")) {
                "<b>${it.groupValues[1].trim()}</b>"
            }

            // Negrita simple (mal escrita con espacios): * texto *
            html = html.replace(Regex("\\*\\s*(.+?)\\s*\\*")) {
                "<b>${it.groupValues[1].trim()}</b>"
            }

            // Cursiva: _texto_
            html = html.replace(Regex("_(.?)_")) {
                "<i>${it.groupValues[1]}</i>"
            }

            // Listas: líneas que inician con *
            html = html.replace(Regex("(?m)^\\*\\s+(.)")) {
                "<li>${it.groupValues[1]}</li>"
            }

            // Agrupar elementos <li> consecutivos en <ul>
            html = html.replace(Regex("((<li>.*?</li>\\s*)+)", RegexOption.DOT_MATCHES_ALL)) {
                "<ul style='margin-left: 1em;'>${it.groupValues[1].trim()}</ul>"
            }

            // Enlaces estilo Markdown
            html = html.replace(Regex("\\[(.*?)\\]\\((.*?)\\)")) {
                "<a href='${it.groupValues[2]}'>${it.groupValues[1]}</a>"
            }

            // Saltos de línea simples
            html = html.replace("\n", "<br>")

            return """
    <div style="font-family: sans-serif; line-height: 1.6; color: #212121;">
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
