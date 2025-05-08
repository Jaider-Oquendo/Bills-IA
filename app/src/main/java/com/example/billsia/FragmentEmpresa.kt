package com.example.billsia

import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.io.OutputStream
import android.view.View


class FragmentEmpresa : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_empresa, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etRazonSocial = view.findViewById<EditText>(R.id.etRazonSocial)
        val etSector = view.findViewById<EditText>(R.id.etSector)
        val etUtilidadOperacional = view.findViewById<EditText>(R.id.etUtilidadOperacional)
        val etDepreciaciones = view.findViewById<EditText>(R.id.etDepreciaciones)
        val etVentas = view.findViewById<EditText>(R.id.etVentas)
        val etInventarios = view.findViewById<EditText>(R.id.etInventarios)
        val etCuentasPorCobrar = view.findViewById<EditText>(R.id.etCuentasPorCobrar)
        val etProveedores = view.findViewById<EditText>(R.id.etProveedores)
        val etImpuestos = view.findViewById<EditText>(R.id.etImpuestos)
        val etIntereses = view.findViewById<EditText>(R.id.etIntereses)
        val etDividendos = view.findViewById<EditText>(R.id.etDividendos)
        val etDeudaFinanciera = view.findViewById<EditText>(R.id.etDeudaFinanciera)

        val btnGuardarEmpresa = view.findViewById<Button>(R.id.btnGuardarEmpresa)
        val btnRegresarSeleccion = view.findViewById<Button>(R.id.btnRegresarSeleccion)

        btnGuardarEmpresa.setOnClickListener {
            try {
                val utilidadOperacional = etUtilidadOperacional.text.toString().toDoubleOrNull() ?: 0.0
                val depreciaciones = etDepreciaciones.text.toString().toDoubleOrNull() ?: 0.0
                val ventas = etVentas.text.toString().toDoubleOrNull() ?: 0.0
                val inventarios = etInventarios.text.toString().toDoubleOrNull() ?: 0.0
                val cuentasPorCobrar = etCuentasPorCobrar.text.toString().toDoubleOrNull() ?: 0.0
                val proveedores = etProveedores.text.toString().toDoubleOrNull() ?: 0.0
                val impuestos = etImpuestos.text.toString().toDoubleOrNull() ?: 0.0
                val intereses = etIntereses.text.toString().toDoubleOrNull() ?: 0.0
                val dividendos = etDividendos.text.toString().toDoubleOrNull() ?: 0.0
                val deudaFinanciera = etDeudaFinanciera.text.toString().toDoubleOrNull() ?: 0.0

                // Cálculos
                val ebitda = utilidadOperacional + depreciaciones
                val margenEbitda = if (ventas != 0.0) (ebitda / ventas) * 100 else 0.0
                val ctno = cuentasPorCobrar + inventarios - proveedores
                val productividadTrabajo = if (ventas != 0.0) ctno / ventas else 0.0
                val estructuraCaja = ebitda - impuestos
                val flujoCajaBruto = intereses - dividendos
                val disponibleParaInversion = flujoCajaBruto
                val incidenciaIntereses = if (ebitda != 0.0) (intereses / ebitda) * 100 else 0.0
                val multipleDeuda = if (ebitda != 0.0) deudaFinanciera / ebitda else 0.0

                val resultado = """
                    ✅ Resultados Financieros

                    EBITDA: $ebitda
                    Margen EBITDA: ${"%.2f".format(margenEbitda)}%
                    Capital Trabajo Neto Operativo: $ctno
                    Productividad del Trabajo: ${"%.4f".format(productividadTrabajo)}
                    Estructura de Caja: $estructuraCaja
                    Flujo de Caja Bruto: $flujoCajaBruto
                    Disponible para Inversión: $disponibleParaInversion
                    Incidencia de Intereses: ${"%.2f".format(incidenciaIntereses)}%
                    Múltiplo de Deuda: ${"%.2f".format(multipleDeuda)}
                """.trimIndent()

                // Guardar archivo en Downloads
                val fileName = "resultados_empresa.txt"
                val resolver = requireContext().contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "Download")
                }
                val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

                uri?.let {
                    resolver.openOutputStream(it)?.use { outputStream ->
                        outputStream.write(resultado.toByteArray())
                    }

                    // Actualizar el estado del archivo
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(it, contentValues, null, null)

                    Toast.makeText(requireContext(), "Archivo guardado en Descargas", Toast.LENGTH_LONG).show()
                } ?: run {
                    Toast.makeText(requireContext(), "Error al guardar en Descargas", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        btnRegresarSeleccion.setOnClickListener {
            parentFragment?.childFragmentManager?.beginTransaction()
                ?.replace(R.id.innerFragmentContainer, FragmentFinancieraSeleccion())
                ?.addToBackStack(null)
                ?.commit()
        }
    }
}
