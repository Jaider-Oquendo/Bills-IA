package com.example.billsia

import android.content.ContentValues
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class FragmentEmpresa : Fragment() {

    // Método para crear la vista del fragmento
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_empresa, container, false)
    }

    // Método que se ejecuta cuando la vista ha sido creada
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Enlazar los EditText de la interfaz
        val etRazonSocial = view.findViewById<EditText>(R.id.etRazonSocial)
        val etNit = view.findViewById<EditText>(R.id.etNit)
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

        // Filtro para solo permitir letras (no números)
        val letrasSoloWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                charSequence?.let { text ->
                    if (text.any { it.isDigit() }) {
                        // Eliminar números si se ingresan
                        val cleanString = text.filterNot { it.isDigit() }
                        (charSequence as? EditText)?.setText(cleanString)
                        (charSequence as? EditText)?.setSelection(cleanString.length)
                    }
                }
            }
        }

        // Aplicar el filtro de solo letras a los campos de texto correspondientes
        etRazonSocial.addTextChangedListener(letrasSoloWatcher)
        etSector.addTextChangedListener(letrasSoloWatcher)

        // Enlazar los botones
        val btnGuardarEmpresa = view.findViewById<Button>(R.id.btnGuardarEmpresa)
        val btnRegresarSeleccion = view.findViewById<Button>(R.id.btnRegresarSeleccion)

        // Acción del botón "Guardar Empresa"
        btnGuardarEmpresa.setOnClickListener {
            val campos = listOf(
                etRazonSocial, etNit, etSector, etUtilidadOperacional,
                etDepreciaciones, etVentas, etInventarios, etCuentasPorCobrar,
                etProveedores, etImpuestos, etIntereses, etDividendos, etDeudaFinanciera
            )

            var hayCamposVacios = false

            // Verificar si hay campos vacíos
            for (campo in campos) {
                if (campo.text.toString().isBlank()) {
                    campo.error = "Campo requerido"
                    hayCamposVacios = true
                } else {
                    campo.error = null
                }
            }

            // Si hay campos vacíos, mostrar mensaje de error
            if (hayCamposVacios) {
                Toast.makeText(requireContext(), "Por favor completa todos los campos", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            try {
                // Convertir los campos numéricos a valores Double
                val utilidadOperacional = etUtilidadOperacional.text.toString().toDouble()
                val depreciaciones = etDepreciaciones.text.toString().toDouble()
                val ventas = etVentas.text.toString().toDouble()
                val inventarios = etInventarios.text.toString().toDouble()
                val cuentasPorCobrar = etCuentasPorCobrar.text.toString().toDouble()
                val proveedores = etProveedores.text.toString().toDouble()
                val impuestos = etImpuestos.text.toString().toDouble()
                val intereses = etIntereses.text.toString().toDouble()
                val dividendos = etDividendos.text.toString().toDouble()
                val deudaFinanciera = etDeudaFinanciera.text.toString().toDouble()

                // Realizar cálculos financieros
                val ebitda = utilidadOperacional + depreciaciones
                val margenEbitda = if (ventas != 0.0) (ebitda / ventas) * 100 else 0.0
                val ctno = cuentasPorCobrar + inventarios - proveedores
                val productividadTrabajo = if (ventas != 0.0) ctno / ventas else 0.0
                val estructuraCaja = ebitda - impuestos
                val flujoCajaBruto = intereses - dividendos
                val disponibleParaInversion = flujoCajaBruto
                val incidenciaIntereses = if (ebitda != 0.0) (intereses / ebitda) * 100 else 0.0
                val multipleDeuda = if (ebitda != 0.0) deudaFinanciera / ebitda else 0.0

                // Crear un texto con los resultados
                val resultado = """
                ✅ Resultados Financieros
            
                Razon Social: ${etRazonSocial.text}
                NIT: ${etNit.text}
                Actividad económica: ${etSector.text}
            
                EBITDA: $${"%.2f".format(ebitda)}
                Margen EBITDA: ${"%.2f".format(margenEbitda)}%
                Capital Trabajo Neto Operativo: $${"%.2f".format(ctno)}
                Productividad del Trabajo: $${"%.2f".format(productividadTrabajo)}
                Estructura de Caja: $${"%.2f".format(estructuraCaja)}
                Flujo de Caja Bruto: $${"%.2f".format(flujoCajaBruto)}
                Disponible para Inversión: $${"%.2f".format(disponibleParaInversion)}
                Incidencia de Intereses: ${"%.2f".format(incidenciaIntereses)}%
                Múltiplo de Deuda: $${"%.2f".format(multipleDeuda)}
            """.trimIndent()

                // Guardar los resultados en un archivo
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

                    // Finalizar el guardado del archivo
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(it, contentValues, null, null)

                    Toast.makeText(requireContext(), "Archivo guardado en Descargas", Toast.LENGTH_LONG).show()
                } ?: run {
                    Toast.makeText(requireContext(), "Error al guardar en Descargas", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                // Mostrar un mensaje de error si ocurre una excepción
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        // Acción del botón "Regresar Selección"
        btnRegresarSeleccion.setOnClickListener {
            parentFragment?.childFragmentManager?.beginTransaction()
                ?.replace(R.id.innerFragmentContainer, FragmentFinancieraSeleccion())
                ?.addToBackStack(null)
                ?.commit()
        }
    }
}
