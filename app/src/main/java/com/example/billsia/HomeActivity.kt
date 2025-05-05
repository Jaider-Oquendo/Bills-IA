package com.example.billsia

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()

        // Redirigir si no hay sesión iniciada
        checkUserSession()

        // Inicializar componentes
        initializeComponents()

        // Configurar el adapter para los fragments
        setupViewPager()

        // Deshabilitar swipe manual
        disableSwipe()

        // Configurar el listener para BottomNavigation
        setupBottomNavigation()
    }

    private fun checkUserSession() {
        if (auth.currentUser == null) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
    }

    private fun initializeComponents() {
        viewPager = findViewById(R.id.viewPager)
        bottomNavigation = findViewById(R.id.bottom_Navigation)
    }

    private fun setupViewPager() {
        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 5  // Cambiar según la cantidad de fragmentos
            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> FragmentFinanciera()
                    1 -> EducativoFragment()
                    2 -> TributariaFragment()
                    3 -> ChatbotFragment()
                    else -> FragmentPerfil()
                }
            }
        }

        // Sincronizar el ViewPager con el BottomNavigationView
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                bottomNavigation.selectedItemId = when (position) {
                    0 -> R.id.nav_financiera
                    1 -> R.id.nav_educativo
                    2 -> R.id.nav_tributaria
                    3 -> R.id.nav_chatbot
                    else -> R.id.nav_perfil
                }
            }
        })
    }

    private fun disableSwipe() {
        viewPager.isUserInputEnabled = false  // Desactivar el deslizamiento manual
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_financiera -> viewPager.currentItem = 0
                R.id.nav_educativo -> viewPager.currentItem = 1
                R.id.nav_tributaria -> viewPager.currentItem = 2
                R.id.nav_chatbot -> viewPager.currentItem = 3
                R.id.nav_perfil -> viewPager.currentItem = 4
            }
            true
        }
    }
}
