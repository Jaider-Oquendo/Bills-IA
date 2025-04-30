package com.example.billsia.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.billsia.EducativoFragment
import com.example.billsia.R
import com.example.billsia.api.ApiService
import com.example.billsia.models.ApiResponse
import com.example.billsia.models.Noticia

class NoticiasFragment : Fragment(R.layout.fragment_noticias) {

    private lateinit var linearLayout: LinearLayout
    private val noticias = mutableListOf<Noticia>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        linearLayout = view.findViewById(R.id.linearNoticias)

        // Llamamos a la API para obtener las noticias
        obtenerNoticiasDeLaAPI()

        // Configurar el botón para regresar a EducativoFragment
        val btnRegresarEducativo = view.findViewById<Button>(R.id.btnRegresarEducativo)
        btnRegresarEducativo.setOnClickListener {
            navegarAFragmentoEducativo()
        }
    }

    private fun obtenerNoticiasDeLaAPI() {
        val apiService = ApiService.create()
        apiService.getNoticias().enqueue(object : retrofit2.Callback<ApiResponse> {
            override fun onResponse(call: retrofit2.Call<ApiResponse>, response: retrofit2.Response<ApiResponse>) {
                if (response.isSuccessful) {
                    noticias.clear()
                    response.body()?.articles?.let {
                        noticias.addAll(it)
                    }
                    mostrarNoticias()
                } else {
                    manejarError("Error al cargar las noticias")
                }
            }

            override fun onFailure(call: retrofit2.Call<ApiResponse>, t: Throwable) {
                manejarError("Error en la conexión: ${t.message}")
            }
        })
    }

    private fun mostrarNoticias() {
        linearLayout.removeAllViews()  // Limpiar las vistas anteriores

        for (noticia in noticias) {
            val noticiaView = LayoutInflater.from(context).inflate(R.layout.item_noticia, null)

            val titleTextView = noticiaView.findViewById<TextView>(R.id.textTitle)
            val descriptionTextView = noticiaView.findViewById<TextView>(R.id.textDescription)

            titleTextView.text = noticia.title
            descriptionTextView.text = noticia.description

            noticiaView.setOnClickListener {
                // Abrir el enlace de la noticia directamente
                val url = noticia.url
                if (url != null) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                } else {
                    manejarError("No hay URL disponible para esta noticia")
                }
            }

            linearLayout.addView(noticiaView)
        }
    }

    private fun manejarError(mensaje: String) {
        context?.let {
            Toast.makeText(it, mensaje, Toast.LENGTH_SHORT).show()
        }
    }

    private fun navegarAFragmentoEducativo() {
        val fragmentTransaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        val educativoFragment = EducativoFragment()
        fragmentTransaction.replace(R.id.fragment_container, educativoFragment)
        fragmentTransaction.addToBackStack("EducativoFragment")
        fragmentTransaction.commit()
    }
}
