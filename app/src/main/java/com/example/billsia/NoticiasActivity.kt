package com.example.billsia

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.billsia.fragments.NoticiasFragment

class NoticiasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noticias)

        // Solo se agrega el fragmento si la actividad se crea por primera vez
        if (savedInstanceState == null) {
            val noticiasFragment = NoticiasFragment()
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()

            // Reemplazar el fragmento en el contenedor
            transaction.replace(R.id.fragment_container, noticiasFragment)
            // Agregar a la pila de retroceso
            transaction.addToBackStack("NoticiasFragment")

            // Realizar la transacción
            transaction.commit()
        }
    }

    // Manejar el botón de retroceso
    override fun onBackPressed() {
        // Verificar si hay fragmentos en la pila de retroceso
        if (supportFragmentManager.backStackEntryCount > 0) {
            // Retroceder al fragmento anterior
            supportFragmentManager.popBackStack()
        } else {
            // Si no hay fragmentos, cerrar la actividad
            super.onBackPressed()
        }
    }
}
