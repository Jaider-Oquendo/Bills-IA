package com.example.billsia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.billsia.databinding.FragmentTributariaBinding
import java.text.NumberFormat
import java.util.*

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
            val patrimonioStr = binding.inputPatrimonio.text?.toString()?.trim()
            val consumoStr = binding.inputConsumo.text?.toString()?.trim()
            val comprasStr = binding.inputCompras.text?.toString()?.trim()

            // Validación de campos vacíos
            if (ingresoStr.isNullOrEmpty() || patrimonioStr.isNullOrEmpty() ||
                consumoStr.isNullOrEmpty() || comprasStr.isNullOrEmpty()) {
                binding.textResultado.text = "⚠️ Por favor, complete todos los campos."
                return@setOnClickListener
            }

            try {
                val ingreso = ingresoStr.toDouble() //* 1_000_000  ingreso en pesos
                val patrimonio = patrimonioStr.toDouble() //* 1_000_000
                val consumo = consumoStr.toDouble()// * 1_000_000
                val compras = comprasStr.toDouble() //* 1_000_000

                val uvt = 49799  // Valor UVT año 2025
                val umbralIngreso = 1400 * uvt
                val umbralPatrimonio = 4500 * uvt
                val umbralConsumo = 1400 * uvt
                val umbralCompras = 1400 * uvt

                val formatoPesos = NumberFormat.getCurrencyInstance(Locale("es", "CO"))

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

            } catch (e: NumberFormatException) {
                binding.textResultado.text = "⚠️ Por favor, ingrese valores numéricos válidos en todos los campos."
            }
        }
        binding.btnLimpiar.setOnClickListener {
            binding.inputIngresoAnual.text?.clear()
            binding.inputPatrimonio.text?.clear()
            binding.inputConsumo.text?.clear()
            binding.inputCompras.text?.clear()
            binding.textResultado.text = ""
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
