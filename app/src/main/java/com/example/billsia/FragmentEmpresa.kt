package com.example.billsia

import androidx.lifecycle.lifecycleScope
import com.example.billsia.data.AppDatabase
import com.example.billsia.data.entities.EmpresaEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_empresa, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fun formatNumberForEditText(value: Double): String {
            return if (value % 1.0 == 0.0) {
                // Es un entero, mostrar sin decimales
                value.toInt().toString()
            } else {
                // Tiene decimales, mostrar con hasta 2 decimales
                "%.2f".format(value)
            }
        }



        val empresaDao = AppDatabase.getDatabase(requireContext()).empresaDao()


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
        val etTasaImpuestos = view.findViewById<EditText>(R.id.etTasaImpuestos)

        val tasaDefault = 0.35

        fun actualizarImpuestos() {
            val utilidadStr = etUtilidadOperacional.text.toString()
            val tasaStr = etTasaImpuestos.text.toString()

            val utilidad = utilidadStr.toDoubleOrNull()
            val tasa = tasaStr.toDoubleOrNull()?.div(100) ?: tasaDefault

            if (utilidad != null) {
                val impuestosCalculados = utilidad * tasa
                etImpuestos.setText("%.2f".format(impuestosCalculados))
            }
        }

        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                actualizarImpuestos()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        etUtilidadOperacional.addTextChangedListener(watcher)
        etTasaImpuestos.addTextChangedListener(watcher)

        val letrasSoloWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                charSequence?.let { text ->
                    if (text.any { it.isDigit() }) {
                        val cleanString = text.filterNot { it.isDigit() }
                        (charSequence as? EditText)?.setText(cleanString)
                        (charSequence as? EditText)?.setSelection(cleanString.length)
                    }
                }
            }
        }

        etRazonSocial.addTextChangedListener(letrasSoloWatcher)
        etSector.addTextChangedListener(letrasSoloWatcher)

        val btnGuardarEmpresa = view.findViewById<Button>(R.id.btnGuardarEmpresa)
        val btnRegresarSeleccion = view.findViewById<Button>(R.id.btnRegresarSeleccion)

        btnGuardarEmpresa.setOnClickListener {
            val campos = listOf(
                etRazonSocial, etNit, etSector, etUtilidadOperacional,
                etDepreciaciones, etVentas, etInventarios, etCuentasPorCobrar,
                etProveedores, etImpuestos, etIntereses, etDividendos, etDeudaFinanciera
            )

            var hayCamposVacios = false
            for (campo in campos) {
                if (campo.text.toString().isBlank()) {
                    campo.error = "Campo requerido"
                    hayCamposVacios = true
                } else {
                    campo.error = null
                }
            }

            if (hayCamposVacios) {
                Toast.makeText(requireContext(), "Por favor completa todos los campos", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            try {
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
                val tasaImpuestos = etTasaImpuestos.text.toString().toDoubleOrNull() ?: tasaDefault


                val ebitda = utilidadOperacional + depreciaciones
                val margenEbitda = if (ventas != 0.0) (ebitda / ventas) * 100 else 0.0
                val ctno = cuentasPorCobrar + inventarios - proveedores
                val productividadTrabajo = if (ventas != 0.0) ctno / ventas else 0.0
                val estructuraOperativa = ebitda - impuestos
                val estructuraFinanciera = intereses - dividendos
                val incidenciaIntereses = if (ebitda != 0.0) (intereses / ebitda) * 100 else 0.0
                val multipleDeuda = if (ebitda != 0.0) deudaFinanciera / ebitda else 0.0
                val palancaCrecimiento = if (productividadTrabajo != 0.0) margenEbitda / (productividadTrabajo * 100) else 0.0

                val interpretacion = StringBuilder("\n\n📊 Interpretación y Recomendaciones:\n")

                if (margenEbitda < 10) {
                    interpretacion.append("- El margen EBITDA es bajo. Revisa los gastos operativos y busca oportunidades de eficiencia.\n")
                } else {
                    interpretacion.append("- Buen margen EBITDA. Tu empresa está generando valor desde sus operaciones.\n")
                }

                if (productividadTrabajo < 0.1) {
                    interpretacion.append("- La productividad del capital de trabajo es baja. Podrías optimizar inventarios o cuentas por cobrar.\n")
                } else {
                    interpretacion.append("- Buena productividad del capital de trabajo. La operación está bien equilibrada.\n")
                }

                if (incidenciaIntereses > 20) {
                    interpretacion.append("- Alta incidencia de intereses sobre EBITDA. Considera renegociar tu deuda o mejorar el flujo operativo.\n")
                } else {
                    interpretacion.append("- La carga financiera es adecuada respecto al EBITDA.\n")
                }

                if (multipleDeuda > 3) {
                    interpretacion.append("- El múltiplo de deuda es alto. Puede haber riesgo financiero, evalúa tu estructura de deuda.\n")
                } else {
                    interpretacion.append("- Nivel de deuda razonable en relación al EBITDA.\n")
                }

                if (palancaCrecimiento < 1) {
                    interpretacion.append("- La palanca de crecimiento es débil. Se sugiere revisar cómo convertir eficiencias operativas en crecimiento.\n")
                } else {
                    interpretacion.append("- Buena palanca de crecimiento. Tus márgenes están bien alineados con el capital de trabajo.\n")
                }

                val resultado = """
            ✅ Resultados Financieros

            Razón Social: ${etRazonSocial.text}
            NIT: ${etNit.text}
            Actividad económica: ${etSector.text}

            EBITDA: $${"%,.2f".format(ebitda)}
            Margen EBITDA: ${"%,.2f".format(margenEbitda)}%
            Capital Trabajo Neto Operativo: $${"%,.2f".format(ctno)}
            Productividad de capital de trabajo: ${"%,.2f".format(productividadTrabajo * 100)}%
            Palanca de Crecimiento (Margen EBITDA / Productividad CT): ${"%,.2f".format(palancaCrecimiento)}
            Flujo de caja bruto: $${"%,.2f".format(estructuraOperativa)}
            Disponible para Inversión: $${"%,.2f".format(estructuraFinanciera)}
            Incidencia de Intereses: ${"%,.2f".format(incidenciaIntereses)}%
            Múltiplo de Deuda: ${"%,.2f".format(multipleDeuda)}

            $interpretacion
        """.trimIndent()

                // Guardar en Room dentro de una corrutina
                lifecycleScope.launch(Dispatchers.IO) {
                    val empresa = EmpresaEntity(
                        razonSocial = etRazonSocial.text.toString(),
                        nit = etNit.text.toString(),
                        sector = etSector.text.toString(),
                        utilidadOperacional = utilidadOperacional,
                        depreciaciones = depreciaciones,
                        ventas = ventas,
                        inventarios = inventarios,
                        cuentasPorCobrar = cuentasPorCobrar,
                        proveedores = proveedores,
                        impuestos = impuestos,
                        intereses = intereses,
                        dividendos = dividendos,
                        deudaFinanciera = deudaFinanciera,
                        tasaImpuestos = tasaImpuestos // <-- AQUÍ

                    )

                    empresaDao.insertEmpresa(empresa)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Datos guardados en base de datos", Toast.LENGTH_SHORT).show()
                    }
                }

                // Guardar archivo en Descargas (como ya tienes)
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

        val btnCargarEmpresa = view.findViewById<Button>(R.id.btnCargarEmpresa)

        btnCargarEmpresa.setOnClickListener {
            val nitIngresado = etNit.text.toString()

            if (nitIngresado.isBlank()) {
                Toast.makeText(requireContext(), "Por favor ingresa el NIT", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val empresa = empresaDao.getEmpresaByNit(nitIngresado)

                withContext(Dispatchers.Main) {
                    if (empresa != null) {
                        etUtilidadOperacional.setText(formatNumberForEditText(empresa.utilidadOperacional))
                        etDepreciaciones.setText(formatNumberForEditText(empresa.depreciaciones))
                        etVentas.setText(formatNumberForEditText(empresa.ventas))
                        etInventarios.setText(formatNumberForEditText(empresa.inventarios))
                        etCuentasPorCobrar.setText(formatNumberForEditText(empresa.cuentasPorCobrar))
                        etProveedores.setText(formatNumberForEditText(empresa.proveedores))
                        etImpuestos.setText(formatNumberForEditText(empresa.impuestos))
                        etIntereses.setText(formatNumberForEditText(empresa.intereses))
                        etDividendos.setText(formatNumberForEditText(empresa.dividendos))
                        etDeudaFinanciera.setText(formatNumberForEditText(empresa.deudaFinanciera))
                        etTasaImpuestos.setText(formatNumberForEditText(empresa.tasaImpuestos))
                        etRazonSocial.setText(empresa.razonSocial)
                        etSector.setText(empresa.sector)

                        Toast.makeText(requireContext(), "Datos cargados exitosamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "No se encontró una empresa con ese NIT", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        val btnLimpiarCampos = view.findViewById<Button>(R.id.btnLimpiarCampos)

        btnLimpiarCampos.setOnClickListener {
            // Limpia el texto de todos los EditText
            etRazonSocial.text.clear()
            etNit.text.clear()
            etSector.text.clear()
            etUtilidadOperacional.text.clear()
            etDepreciaciones.text.clear()
            etVentas.text.clear()
            etInventarios.text.clear()
            etCuentasPorCobrar.text.clear()
            etProveedores.text.clear()
            etImpuestos.text.clear()
            etIntereses.text.clear()
            etDividendos.text.clear()
            etDeudaFinanciera.text.clear()
            etTasaImpuestos.text.clear()

            // Opcional: eliminar errores si hay
            val campos = listOf(
                etRazonSocial, etNit, etSector, etUtilidadOperacional,
                etDepreciaciones, etVentas, etInventarios, etCuentasPorCobrar,
                etProveedores, etImpuestos, etIntereses, etDividendos, etDeudaFinanciera, etTasaImpuestos
            )
            campos.forEach { it.error = null }

            Toast.makeText(requireContext(), "Campos limpiados", Toast.LENGTH_SHORT).show()
        }


    }
}
