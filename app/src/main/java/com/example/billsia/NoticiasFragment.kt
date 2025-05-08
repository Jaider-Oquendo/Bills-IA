package com.example.billsia.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
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

        val btnRegresarEducativo: Button = view.findViewById(R.id.btnRegresarEducativo)
        linearLayout = view.findViewById(R.id.linearNoticias)

        obtenerNoticiasDeLaAPI()

        btnRegresarEducativo.setOnClickListener {
            parentFragment?.childFragmentManager?.beginTransaction()
                ?.replace(R.id.innerFragmentContainer, EducativoFragment())
                ?.addToBackStack(null)
                ?.commit()
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
                    Toast.makeText(context, "Error al cargar las noticias", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<ApiResponse>, t: Throwable) {
                Toast.makeText(context, "Error en la conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostrarNoticias() {
        linearLayout.removeAllViews()
        for (noticia in noticias) {
            val noticiaView = LayoutInflater.from(context).inflate(R.layout.item_noticia, null)

            val titleTextView = noticiaView.findViewById<TextView>(R.id.textTitle)
            val descriptionTextView = noticiaView.findViewById<TextView>(R.id.textDescription)
            val imageView = noticiaView.findViewById<ImageView>(R.id.imageNoticia)

            titleTextView.text = noticia.title
            descriptionTextView.text = noticia.description

            if (!noticia.urlToImage.isNullOrEmpty()) {
                Glide.with(this)
                    .load(noticia.urlToImage)
                    .into(imageView)
                imageView.visibility = View.VISIBLE
            }

            noticiaView.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(noticia.url))
                startActivity(intent)
            }

            linearLayout.addView(noticiaView)
        }
    }
}
