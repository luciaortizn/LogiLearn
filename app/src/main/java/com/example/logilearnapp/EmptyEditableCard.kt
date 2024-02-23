package com.example.logilearnapp

import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.forEach
import androidx.core.view.get
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar

class EmptyEditableCard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empty_editable_card)
        val navIcon: Icon// findViewById<>()
        val topBar: MaterialToolbar = findViewById(R.id.topAppBarCard)
        //hace que el botón x se produzca un retroceso en la activity
        topBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        topBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_carpeta -> {
                    //añqdir a carpeta
                    Toast.makeText(this, "Carpeta", Toast.LENGTH_SHORT).show()
                    // Handle edit text press
                    true
                }

                R.id.item_etiqueta -> {
                    // Handle favorite icon press
                    Toast.makeText(this, "Etiqueta", Toast.LENGTH_SHORT).show()
                    //añadir etiqueta
                    true
                }

                R.id.item_imagen -> {
                    Toast.makeText(this, "Imagen", Toast.LENGTH_SHORT).show()
                    //proporcionar acceso a su galería
                    // Handle more item (inside overflow menu) press
                    true
                }

                else -> false
            }
        }
    }
}

