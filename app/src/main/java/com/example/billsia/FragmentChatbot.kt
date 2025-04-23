package com.example.billsia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class ChatbotFragment : Fragment() {

    private lateinit var inputTexto: EditText
    private lateinit var btnEnviar: TextView
    private lateinit var respuesta: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = inflater.inflate(R.layout.fragment_chatbot, container, false)

        inputTexto = binding.findViewById(R.id.inputTexto)
        btnEnviar = binding.findViewById(R.id.btnEnviar)
        respuesta = binding.findViewById(R.id.respuesta)

        btnEnviar.setOnClickListener {
            val prompt = inputTexto.text.toString()
            if (prompt.isNotEmpty()) {
                sendRequestToGemini(prompt)
            }
        }

        return binding
    }

    private fun sendRequestToGemini(prompt: String) {
        lifecycleScope.launch {
            try {
                val response = GeminiApiUtils.getGeminiResponse(prompt, requireContext())
                respuesta.text = response
            } catch (e: Exception) {
                respuesta.text = "Error al hacer la solicitud: ${e.message}"
            }
        }
    }
}
