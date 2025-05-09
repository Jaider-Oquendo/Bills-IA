package com.example.billsia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.billsia.databinding.FragmentPersonaNaturalBinding

class FragmentPersonaNatural : Fragment() {

    private lateinit var binding: FragmentPersonaNaturalBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPersonaNaturalBinding.inflate(inflater, container, false)

        binding.btnCalcular.setOnClickListener {
            calcularSaludFinanciera()
        }

        binding.btnRegresar.setOnClickListener {
            parentFragment?.childFragmentManager?.beginTransaction()
                ?.replace(R.id.innerFragmentContainer, FragmentFinancieraSeleccion())
                ?.addToBackStack(null)
                ?.commit()
        }

        return binding.root
    }

    private fun calcularSaludFinanciera() {
        var esValido = true

        val ingresosStr = binding.etIngresos.text.toString()
        val gastosStr = binding.etGastos.text.toString()
        val ahorrosStr = binding.etAhorros.text.toString()
        val deudasStr = binding.etDeudas.text.toString()

        // Validar campos vacíos
        if (ingresosStr.isBlank()) {
            binding.etIngresos.error = "Campo requerido"
            esValido = false
        }

        if (gastosStr.isBlank()) {
            binding.etGastos.error = "Campo requerido"
            esValido = false
        }

        if (ahorrosStr.isBlank()) {
            binding.etAhorros.error = "Campo requerido"
            esValido = false
        }

        if (deudasStr.isBlank()) {
            binding.etDeudas.error = "Campo requerido"
            esValido = false
        }

        if (!esValido) {
            binding.tvResultado.text = "Por favor, completa todos los campos para calcular tu salud financiera."
            return
        }

        // Convertir a Double
        val ingresos = ingresosStr.toDoubleOrNull() ?: 0.0
        val gastos = gastosStr.toDoubleOrNull() ?: 0.0
        val ahorros = ahorrosStr.toDoubleOrNull() ?: 0.0
        val deudas = deudasStr.toDoubleOrNull() ?: 0.0

        val puntaje = (ahorros - deudas) + (ingresos - gastos)

        val resultado = when {
            puntaje >= ingresos * 0.5 -> {
                "💪 Excelente salud financiera: ¡Tus ahorros están bien por encima de tus deudas y gastos! Puedes enfrentar cualquier situación inesperada."
            }
            puntaje >= ingresos * 0.2 -> {
                "🙂 Salud financiera aceptable: Tus ahorros cubren tus deudas y gastos, pero podrías mejorar para estar más tranquilo a largo plazo."
            }
            else -> {
                "⚠️ Necesita mejorar su salud financiera: Tus gastos superan tus ahorros y deudas, lo que indica un posible riesgo en tu estabilidad financiera."
            }
        }

        binding.tvResultado.text = """
            $resultado
            Recomendación: Intenta reducir tus deudas o aumentar tus ahorros para mejorar tu salud financiera.
        """.trimIndent()
    }
}
