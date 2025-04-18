package com.example.billsia
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.example.billsia.data.viewmodel.UserViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
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
        bottomNavigation = findViewById(R.id.bottomNavigation)
    }

    private fun setupViewPager() {
        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 4
            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> FragmentFinanciera()
                    1 -> FragmentMensajes()
                    2 -> FragmentTributaria()
                    else -> FragmentPerfil()
                }
            }
        }
    }

    private fun disableSwipe() {
        viewPager.isUserInputEnabled = false
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_financiera -> viewPager.currentItem = 0
                R.id.nav_mensaje -> viewPager.currentItem = 1
                R.id.nav_tributaria -> viewPager.currentItem = 2
                R.id.nav_perfil -> viewPager.currentItem = 3
            }
            true
        }
    }
}
