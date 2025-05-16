package com.example.billsia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.billsia.data.AppDatabase
import com.example.billsia.data.entities.PersonaNatural
import com.example.billsia.databinding.FragmentPersonaNaturalBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale

class FragmentPersonaNatural : Fragment() {

    private lateinit var binding: FragmentPersonaNaturalBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPersonaNaturalBinding.inflate(inflater, container, false)
        val db = AppDatabase.getDatabase(requireContext())
        val dao = db.personaNaturalDao()

        binding.btnCalcular.setOnClickListener {
            val cedula = binding.etCedula.text.toString()
            if (cedula.isBlank()) {
                binding.etCedula.error = "Ingrese la cédula"
                return@setOnClickListener
            }

            val ingresosStr = binding.etIngresos.text.toString()
            val gastosStr = binding.etGastos.text.toString()
            val ahorrosStr = binding.etAhorros.text.toString()
            val deudasStr = binding.etDeudas.text.toString()

            if (ingresosStr.isBlank() || gastosStr.isBlank() || ahorrosStr.isBlank() || deudasStr.isBlank()) {
                binding.tvResultado.text = "Por favor, completa todos los campos."
                return@setOnClickListener
            }

            val ingresos = ingresosStr.toDouble()
            val gastos = gastosStr.toDouble()
            val ahorros = ahorrosStr.toDouble()
            val deudas = deudasStr.toDouble()

            val puntaje = (ahorros - deudas) + (ingresos - gastos)
            val resultado = when {
                puntaje >= ingresos * 0.5 -> "💪 Excelente salud financiera: ¡Tus ahorros están bien por encima de tus deudas y gastos! Puedes enfrentar cualquier situación inesperada."
                puntaje >= ingresos * 0.2 -> "🙂 Salud financiera aceptable: Tus ahorros cubren tus deudas y gastos, pero podrías mejorar para estar más tranquilo a largo plazo."
                else -> "⚠️ Necesita mejorar su salud financiera: Tus gastos superan tus ahorros y deudas, lo que indica un posible riesgo en tu estabilidad financiera."
            }

            binding.tvResultado.text = """
                $resultado
                Recomendación: Intenta reducir tus deudas o aumentar tus ahorros para mejorar tu salud financiera.
            """.trimIndent()

            // Guardar en Room
            val persona = PersonaNatural(cedula, ingresos, gastos, ahorros, deudas)
            lifecycleScope.launch {
                dao.insertar(persona)
                Toast.makeText(requireContext(), "Datos guardados correctamente", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCargar.setOnClickListener {
            val cedula = binding.etCedula.text.toString()
            if (cedula.isBlank()) {
                binding.etCedula.error = "Ingrese la cédula para cargar"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val persona = withContext(Dispatchers.IO) { dao.buscarPorCedula(cedula) }
                if (persona != null) {
                    // Formatear números con separadores de miles y 2 decimales
                    val formatoNumero = NumberFormat.getNumberInstance(Locale("es", "CO")).apply {
                        minimumFractionDigits = 2
                        maximumFractionDigits = 2
                    }
                    binding.etIngresos.setText(formatoNumero.format(persona.ingresos))
                    binding.etGastos.setText(formatoNumero.format(persona.gastos))
                    binding.etAhorros.setText(formatoNumero.format(persona.ahorros))
                    binding.etDeudas.setText(formatoNumero.format(persona.deudas))
                    Toast.makeText(requireContext(), "Datos cargados correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    binding.tvResultado.text = "Cédula sin datos registrados."
                }
            }
        }

        binding.btnRegresar.setOnClickListener {
            parentFragment?.childFragmentManager?.beginTransaction()
                ?.replace(R.id.innerFragmentContainer, FragmentFinancieraSeleccion())
                ?.addToBackStack(null)
                ?.commit()
        }

        binding.btnLimpiar.setOnClickListener {
            binding.etCedula.text?.clear()
            binding.etIngresos.text?.clear()
            binding.etGastos.text?.clear()
            binding.etAhorros.text?.clear()
            binding.etDeudas.text?.clear()
            binding.tvResultado.text = ""
        }


        return binding.root
    }
}
