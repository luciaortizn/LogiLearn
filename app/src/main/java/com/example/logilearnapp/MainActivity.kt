package com.example.logilearnapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.logilearnapp.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

import kotlin.math.log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val fab :FloatingActionButton = findViewById(R.id.floating_action_button)
        fab.setOnClickListener {
            // Respond to FAB click
            //abre una ventana emergente CardCreate
            val intent = Intent(applicationContext, EmptyEditableCard::class.java)
            // Iniciar la otra actividad
            startActivity(intent)
        }


    }
    var prueba = 1
    override fun onDestroy() {
        super.onDestroy()


    }
}