package com.example.billsia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.billsia.databinding.FragmentChatbotBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatbotFragment : Fragment() {

    private var _binding: FragmentChatbotBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatbotBinding.inflate(inflater, container, false)

        binding.btnEnviar.setOnClickListener {
            val prompt = binding.inputTexto.text.toString()
            if (prompt.isNotEmpty()) {
                sendRequestToGemini(prompt)
            }
        }

        return binding.root
    }

    private fun sendRequestToGemini(prompt: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = GeminiApiUtils.getGeminiResponse(prompt, requireContext())
                withContext(Dispatchers.Main) {
                    binding.respuesta.text = response
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.respuesta.text = "Error al hacer la solicitud: ${e.message}"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
