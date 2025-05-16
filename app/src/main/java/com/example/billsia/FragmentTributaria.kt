package com.example.billsia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.billsia.data.AppDatabase
import com.example.billsia.data.dao.UsuarioDao
import com.example.billsia.data.entities.Usuario
import com.example.billsia.databinding.FragmentTributariaBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

class TributariaFragment : Fragment() {

    private var _binding: FragmentTributariaBinding? = null
    private val binding get() = _binding!!
    private lateinit var usuarioDao: UsuarioDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTributariaBinding.inflate(inflater, container, false)
        val database = AppDatabase.getDatabase(requireContext())
        usuarioDao = database.usuarioDao()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val formatoPesos = NumberFormat.getCurrencyInstance(Locale("es", "CO"))

        binding.btnEnviar.setOnClickListener {
            val cedula = binding.inputCedula.text?.toString()?.trim()
            val ingresoStr = binding.inputIngresoAnual.text?.toString()?.trim()?.replace("[^\\d.]".toRegex(), "")
            val patrimonioStr = binding.inputPatrimonio.text?.toString()?.trim()?.replace("[^\\d.]".toRegex(), "")
            val consumoStr = binding.inputConsumo.text?.toString()?.trim()?.replace("[^\\d.]".toRegex(), "")
            val comprasStr = binding.inputCompras.text?.toString()?.trim()?.replace("[^\\d.]".toRegex(), "")

            if (cedula.isNullOrEmpty() || ingresoStr.isNullOrEmpty() || patrimonioStr.isNullOrEmpty() ||
                consumoStr.isNullOrEmpty() || comprasStr.isNullOrEmpty()
            ) {
                binding.textResultado.text = "⚠️ Por favor, complete todos los campos."
                return@setOnClickListener
            }

            try {
                val ingreso = ingresoStr.toDouble()
                val patrimonio = patrimonioStr.toDouble()
                val consumo = consumoStr.toDouble()
                val compras = comprasStr.toDouble()

                val uvt = 49799  // Valor UVT año 2025
                val umbralIngreso = 1400 * uvt
                val umbralPatrimonio = 4500 * uvt
                val umbralConsumo = 1400 * uvt
                val umbralCompras = 1400 * uvt

                val razones = mutableListOf<String>()

                if (ingreso >= umbralIngreso) {
                    razones.add("✅ Tus ingresos anuales (${formatoPesos.format(ingreso)}) superan el umbral de ingresos (${formatoPesos.format(umbralIngreso)}).")
                }
                if (patrimonio >= umbralPatrimonio) {
                    razones.add("✅ Tu patrimonio bruto (${formatoPesos.format(patrimonio)}) supera el umbral de patrimonio (${formatoPesos.format(umbralPatrimonio)}).")
                }
                if (consumo >= umbralConsumo) {
                    razones.add("✅ Tu consumo con tarjeta de crédito (${formatoPesos.format(consumo)}) supera el umbral de consumo (${formatoPesos.format(umbralConsumo)}).")
                }
                if (compras >= umbralCompras) {
                    razones.add("✅ Tus compras o consignaciones bancarias (${formatoPesos.format(compras)}) superan el umbral de consignaciones/compras (${formatoPesos.format(umbralCompras)}).")
                }

                val resultado = if (razones.isNotEmpty()) {
                    "🚨 DEBES DECLARAR RENTA este año por las siguientes razones:\n\n" + razones.joinToString("\n\n")
                } else {
                    "✅ No estás obligado a declarar renta este año, ya que no superas ninguno de los umbrales establecidos."
                }

                binding.textResultado.text = resultado

                // Guardar en la base de datos
                val usuario = Usuario(
                    cedula = cedula,
                    ingreso = ingreso,
                    patrimonio = patrimonio,
                    consumo = consumo,
                    compras = compras
                )

                CoroutineScope(Dispatchers.IO).launch {
                    usuarioDao.insertarUsuario(usuario)
                }

            } catch (e: NumberFormatException) {
                binding.textResultado.text = "⚠️ Por favor, ingrese valores numéricos válidos en todos los campos."
            }
        }

        binding.btnLimpiar.setOnClickListener {
            binding.inputCedula.text?.clear()
            binding.inputIngresoAnual.text?.clear()
            binding.inputPatrimonio.text?.clear()
            binding.inputConsumo.text?.clear()
            binding.inputCompras.text?.clear()
            binding.textResultado.text = ""
        }

        binding.btnCargar.setOnClickListener {
            val cedula = binding.inputCedula.text?.toString()?.trim()
            if (cedula.isNullOrEmpty()) {
                binding.textResultado.text = "⚠️ Por favor, ingrese la cédula para cargar los datos."
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                val usuario = usuarioDao.obtenerUsuarioPorCedula(cedula)
                if (usuario != null) {
                    requireActivity().runOnUiThread {
                        // Mostrar los datos con formato de moneda
                        binding.inputIngresoAnual.setText(formatoPesos.format(usuario.ingreso))
                        binding.inputPatrimonio.setText(formatoPesos.format(usuario.patrimonio))
                        binding.inputConsumo.setText(formatoPesos.format(usuario.consumo))
                        binding.inputCompras.setText(formatoPesos.format(usuario.compras))
                        binding.textResultado.text = "✅ Datos cargados correctamente."
                    }
                } else {
                    requireActivity().runOnUiThread {
                        binding.textResultado.text = "⚠️ Cédula sin datos registrados."
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
