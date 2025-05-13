package com.example.billsia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.billsia.adapters.ChatAdapter
import com.example.billsia.data.AppDatabase
import com.example.billsia.data.entities.Mensaje
import com.example.billsia.databinding.FragmentChatbotBinding
import kotlinx.coroutines.*

class ChatbotFragment : Fragment() {

    private var _binding: FragmentChatbotBinding? = null
    private val binding get() = _binding!!
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var mensajeDao: com.example.billsia.data.dao.MensajeDao
    private val mensajes = mutableListOf<Mensaje>()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatbotBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.getDatabase(requireContext())
        mensajeDao = db.mensajeDao()

        // Inicializar adaptador pasando el contexto y los mensajes
        chatAdapter = ChatAdapter(requireContext(), mensajes) { mensaje ->
            eliminarMensaje(mensaje)
        }

        binding.recyclerChat.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatAdapter
        }

        // Botón enviar
        binding.btnEnviar.setOnClickListener {
            val prompt = binding.inputTexto.text.toString().trim()
            if (prompt.isNotEmpty()) {
                binding.inputTexto.text.clear()
                enviarMensaje(prompt)
            }
        }

        cargarMensajes()
    }

    private fun enviarMensaje(prompt: String) {
        coroutineScope.launch {
            val mensajeUsuario = Mensaje(texto = prompt, esUsuario = true)

            withContext(Dispatchers.IO) {
                mensajeDao.insertarMensaje(mensajeUsuario)
            }

            // Obtener respuesta de Gemini API
            val respuesta: String = withContext(Dispatchers.IO) {
                GeminiApiUtils.getGeminiResponse(prompt, requireContext())
            }

            val mensajeApi = Mensaje(texto = respuesta, esUsuario = false)

            withContext(Dispatchers.IO) {
                mensajeDao.insertarMensaje(mensajeApi)
            }

            cargarMensajes()
        }
    }

    private fun eliminarMensaje(mensaje: Mensaje) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                mensajeDao.eliminarMensaje(mensaje)
            }
            cargarMensajes()
        }
    }

    private fun cargarMensajes() {
        coroutineScope.launch {
            val nuevosMensajes = withContext(Dispatchers.IO) {
                mensajeDao.obtenerMensajes()
            }
            mensajes.clear()
            mensajes.addAll(nuevosMensajes)
            chatAdapter.notifyDataSetChanged()
            binding.recyclerChat.scrollToPosition(mensajes.size - 1)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        coroutineScope.cancel()
        _binding = null
    }
}
