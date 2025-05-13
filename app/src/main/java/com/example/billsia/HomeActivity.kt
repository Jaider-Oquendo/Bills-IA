package com.example.billsia


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.billsia.fragments.EducativoContainerFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()

        // Verificar si el usuario está autenticado
        checkUserSession()

        // Inicializar los componentes
        initializeComponents()

        // Configurar el adapter para los fragments
        setupViewPager()

        // Deshabilitar la opción de deslizamiento manual
        disableSwipe()

        // Configurar el listener para BottomNavigation
        setupBottomNavigation()
    }

    // Verificar si hay sesión de usuario activa
    private fun checkUserSession() {
        try {
            if (auth.currentUser == null) {
                // Si no hay usuario, redirigir a AuthActivity
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
                finish()
            }
        } catch (e: Exception) {
            Log.e("HomeActivity", "Error verificando sesión del usuario", e)
        }
    }

    // Inicializar los componentes de la interfaz de usuario
    private fun initializeComponents() {
        viewPager = findViewById(R.id.viewPager)
        bottomNavigation = findViewById(R.id.bottom_Navigation)
    }

    // Configurar el ViewPager con los fragments
    private fun setupViewPager() {
        try {
            viewPager.adapter = object : FragmentStateAdapter(this) {
                override fun getItemCount(): Int = 5
                override fun createFragment(position: Int): Fragment {
                    return when (position) {
                        0 -> FragmentFinanciera()
                        1 -> EducativoContainerFragment()
                        2 -> TributariaFragment()
                        3 -> ChatbotFragment()
                        else -> FragmentPerfil()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("HomeActivity", "Error configurando el ViewPager", e)
        }
    }

    // Deshabilitar la opción de deslizamiento manual en el ViewPager
    private fun disableSwipe() {
        try {
            viewPager.isUserInputEnabled = false
        } catch (e: Exception) {
            Log.e("HomeActivity", "Error deshabilitando el deslizamiento", e)
        }
    }

    // Configurar el BottomNavigationView para cambiar de fragment
    private fun setupBottomNavigation() {
        try {
            bottomNavigation.setOnItemSelectedListener { item ->
                viewPager.currentItem = when (item.itemId) {
                    R.id.nav_financiera -> 0
                    R.id.nav_educativo -> 1
                    R.id.nav_tributaria -> 2
                    R.id.nav_chatbot -> 3
                    R.id.nav_perfil -> 4
                    else -> return@setOnItemSelectedListener false
                }
                true
            }
        } catch (e: Exception) {
            Log.e("HomeActivity", "Error configurando el BottomNavigation", e)
        }
    }

    // Métodos del ciclo de vida de la actividad
    override fun onPause() {
        super.onPause()
        // Aquí podrías detener tareas o animaciones no necesarias
        Log.d("HomeActivity", "onPause llamado")
    }

    override fun onStop() {
        super.onStop()
        // Liberar recursos si es necesario
        Log.d("HomeActivity", "onStop llamado")
    }

    override fun onDestroy() {
        super.onDestroy()
        // Liberar recursos adicionales si es necesario
        Log.d("HomeActivity", "onDestroy llamado")
    }

    override fun onResume() {
        super.onResume()
        // Reanudar tareas o actualizaciones si es necesario
        Log.d("HomeActivity", "onResume llamado")
    }
}
