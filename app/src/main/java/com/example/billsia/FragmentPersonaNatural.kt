package com.example.billsia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.billsia.FragmentFinancieraSeleccion
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

        // Configurar el botón de regreso
        binding.btnRegresar.setOnClickListener {
            // Regresar al fragmento de selección (FragmentFinancieraSeleccion)
            parentFragment?.childFragmentManager?.beginTransaction()
                ?.replace(R.id.innerFragmentContainer, FragmentFinancieraSeleccion())
                ?.addToBackStack(null)
                ?.commit()
        }

        return binding.root
    }

    private fun calcularSaludFinanciera() {
        val ingresos = binding.etIngresos.text.toString().toDoubleOrNull() ?: 0.0
        val gastos = binding.etGastos.text.toString().toDoubleOrNull() ?: 0.0
        val ahorros = binding.etAhorros.text.toString().toDoubleOrNull() ?: 0.0
        val deudas = binding.etDeudas.text.toString().toDoubleOrNull() ?: 0.0

        // Cálculo de la salud financiera con un puntaje basado en ahorros, deudas, ingresos y gastos.
        val puntaje = (ahorros - deudas) + (ingresos - gastos)

        // Mensajes basados en el puntaje calculado
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

        // Mostramos el resultado en la interfaz con una recomendación adicional
        binding.tvResultado.text = """
            $resultado
            Recomendación: Intenta reducir tus deudas o aumentar tus ahorros para mejorar tu salud financiera.
        """.trimIndent()
    }
}
