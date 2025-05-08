package com.example.billsia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.billsia.FragmentPersonaNatural

class FragmentFinancieraSeleccion : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_financiera_seleccion, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnPersonaNatural = view.findViewById<Button>(R.id.btnPersonaNatural)
        val btnEmpresa = view.findViewById<Button>(R.id.btnEmpresa)

        // Al presionar el botón de Persona Natural
        btnPersonaNatural.setOnClickListener {
            Toast.makeText(requireContext(), "Botón Persona natural presionado", Toast.LENGTH_SHORT).show()
            // Reemplazamos el fragmento de selección por el fragmento de Persona Natural
            parentFragment?.childFragmentManager?.beginTransaction()
                ?.replace(R.id.innerFragmentContainer, FragmentPersonaNatural())  // Reemplazamos el fragmento
                ?.commit()  // No agregamos al back stack
        }

        // Al presionar el botón de Empresa
        btnEmpresa.setOnClickListener {
            Toast.makeText(requireContext(), "Botón Empresa presionado", Toast.LENGTH_SHORT).show()
            // Reemplazamos el fragmento de selección por el fragmento de Empresa
            parentFragment?.childFragmentManager?.beginTransaction()
                ?.replace(R.id.innerFragmentContainer, FragmentEmpresa())  // Reemplazamos el fragmento
                ?.commit()  // No agregamos al back stack
        }
    }
}
