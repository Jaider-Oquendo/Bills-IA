package com.example.billsia.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.billsia.adapter.ArticuloAdapter
import com.example.billsia.databinding.FragmentEducativoBinding
import com.example.billsia.model.Articulo

class EducativoFragment : Fragment() {

    private var _binding: FragmentEducativoBinding? = null
    private val binding get() = _binding!!

    // Lista de artículos, ahora con el artículo de la DIAN agregado al inicio
    private val listaArticulos = listOf(
        Articulo(
            id = 0,
            titulo = "Haz tus consultas con la DIAN",
            resumen = "Consulta temas tributarios, impuestos y más en la página oficial de la DIAN.",
            url = "https://www.dian.gov.co"
        ),
        Articulo(
            id = 1,
            titulo = "¿Qué es el IVA y cómo funciona?",
            resumen = "Aprende cómo se aplica el IVA y su impacto en tus finanzas.",
            url = "https://www.seguracontadores.com/que-es-el-iva-y-como-funciona/"
        ),
        Articulo(
            id = 2,
            titulo = "Consejos para ahorrar impuestos",
            resumen = "Conoce estrategias legales para reducir tu carga tributaria.",
            url = "https://www.bing.com/search?q=Consejos+para+ahorrar+impuestos"
        ),
        Articulo(
            id = 3,
            titulo = "Actualización de tarifas tributarias 2025",
            resumen = "Infórmate sobre las últimas reformas tributarias.",
            url = "https://www.ambitojuridico.com/noticia/reformas-2025"
        ),
        Articulo(
            id = 4,
            titulo = "Deducciones tributarias que puedes aprovechar",
            resumen = "Descubre qué deducciones puedes aplicar como persona natural o jurídica.",
            url = "https://www.portafolio.co/negocios/que-deducciones-puedes-aplicar-en-tu-declaracion-de-renta-567108"
        ),
        Articulo(
            id = 5,
            titulo = "Declaración de renta: pasos clave para no equivocarte",
            resumen = "Guía rápida para presentar tu declaración sin errores comunes.",
            url = "https://www.larepublica.co/finanzas/como-presentar-la-declaracion-de-renta-en-2024-3532096"
        ),
        Articulo(
            id = 6,
            titulo = "¿Cómo afectan los impuestos a los emprendedores?",
            resumen = "Conoce los principales impuestos que afectan a quienes están iniciando un negocio.",
            url = "https://emprendimientoyempresa.com/impuestos-emprendedores/"
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentEducativoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Configuramos el RecyclerView para mostrar los artículos
        binding.recyclerEducativo.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerEducativo.adapter = ArticuloAdapter(listaArticulos)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
