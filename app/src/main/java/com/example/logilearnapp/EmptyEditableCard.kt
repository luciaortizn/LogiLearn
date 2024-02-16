package com.example.logilearnapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.appbar.AppBarLayout

class EmptyEditableCard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empty_editable_card)
       // val topAppBar: MenuItem = findViewById(R.id.topAppBar)
        //ojo con el menuitem
        /*topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_carpeta -> {
                    //añqdir a carpeta
                    // Handle edit text press
                    true
                }
                R.id.item_etiqueta -> {
                    // Handle favorite icon press
                    //añadir etiqueta
                    true
                }
                R.id.item_imagen -> {
                    //proporcionar acceso a su galería
                    // Handle more item (inside overflow menu) press
                    true
                }
                else -> false
            }}
        * */


    }
}