
package com.example.billsia.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.billsia.databinding.FragmentTributariaBinding

class TributariaFragment : Fragment() {

    private var _binding: FragmentTributariaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTributariaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnEnviar.setOnClickListener {
            val ingresoStr = binding.inputIngresoAnual.text?.toString()?.trim()
            val dependientesStr = binding.inputDependientes.text?.toString()?.trim()

            // Validación de campos vacíos
            if (ingresoStr.isNullOrEmpty() || dependientesStr.isNullOrEmpty()) {
                binding.textResultado.text = "Por favor, complete todos los campos."
                return@setOnClickListener
            }

            try {
                val ingreso = ingresoStr.toDouble()
                val dependientes = dependientesStr.toInt()

                // Umbral ejemplo (cámbialo según normativa actual)
                val umbralDeclaracion = 53.2
                val debeDeclarar = ingreso >= umbralDeclaracion

                val resultado = if (debeDeclarar) {
                    "✅ Sí debes declarar renta.\nIncluye tus ingresos, deducciones y dependientes."
                } else {
                    "ℹ️ No estás obligado a declarar renta este año.\nVerifica si aplica alguna excepción."
                }

                binding.textResultado.text = resultado

            } catch (e: NumberFormatException) {
                binding.textResultado.text = "Por favor, ingrese valores válidos en los campos."
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
