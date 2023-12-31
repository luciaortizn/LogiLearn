package com.example.logilearnapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import com.example.logilearnapp.R

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val fragment = RegisterFragment()

        val textView: TextView = findViewById(R.id.textoRegistrarse)
        textView.setOnClickListener{

            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()

            //reemplazar con el nuevo fragment
            transaction.replace(R.id.viewer, fragment)

            //no se que es back stack
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
}