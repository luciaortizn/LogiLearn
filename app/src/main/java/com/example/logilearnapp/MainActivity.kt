package com.example.logilearnapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.logilearnapp.databinding.ActivityMainBinding
import com.example.logilearnapp.view.CardViewFragment

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //para el binding de fragments
        binding  = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())
        //viewer es el fragment que tiene la navegación
        binding.bottomNavigation.setOnItemSelectedListener { menuItem->
            when (menuItem.itemId) {
                R.id.navigation_home_item -> {
                    //añqdir a carpeta
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.navigation_buscar_item -> {
                    // Handle favorite icon press
                    replaceFragment(SearchFragment())
                    //añadir etiqueta
                    true
                }
                R.id.navigation_carpeta_item -> {
                    replaceFragment(CardViewFragment())
                    //proporcionar acceso a su galería
                    // Handle more item (inside overflow menu) press
                    true
                }
                else -> false
            }

        }

    }

    fun replaceFragment(fragment: Fragment){
        val fragmentManager:FragmentManager  = supportFragmentManager
        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.viewerFragment, fragment)
        fragmentTransaction.commit()
    }

    override fun onDestroy() {
        super.onDestroy()


    }
}