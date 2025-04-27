package com.example.billsia

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.billsia.databinding.FragmentEducativoBinding
import com.google.firebase.database.*

class EducativoFragment : Fragment() {

    private var _binding: FragmentEducativoBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private lateinit var articuloAdapter: ArticuloAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEducativoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar Firebase Database
        database = FirebaseDatabase.getInstance().reference.child("Articulos")

        // Configurar RecyclerView
        articuloAdapter = ArticuloAdapter()
        binding.recyclerEducativo.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerEducativo.adapter = articuloAdapter

        // Escuchar cambios en la base de datos
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nuevosArticulos = mutableListOf<Articulo>()
                for (articuloSnapshot in snapshot.children) {
                    val articulo = articuloSnapshot.getValue(Articulo::class.java)
                    articulo?.let { nuevosArticulos.add(it) }
                }
                articuloAdapter.submitList(nuevosArticulos)
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar error si es necesario
            }
        })

        // Botón para abrir la página de la DIAN
        binding.btnIrADian.setOnClickListener {
            val url = "https://www.dian.gov.co/"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

        // Botón para abrir la pantalla de Noticias
        binding.btnIrANoticias.setOnClickListener {
            val intent = Intent(requireContext(), NoticiasActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
