package com.example.logilearnapp
import android.graphics.drawable.Icon
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EmptyEditableCard : AppCompatActivity() {
    private lateinit var firebaseDatabase: FirebaseDatabase
    //realtime database
    private lateinit var databaseReference: DatabaseReference
   private lateinit var save_btn: ExtendedFloatingActionButton
   private lateinit var input_text: EditText
   private lateinit var result_text: EditText
   private lateinit var title_text: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empty_editable_card)
        val navIcon: Icon// findViewById<>()
        val topBar: MaterialToolbar = findViewById(R.id.topAppBarCard)
        //hace que el botón x se produzca un retroceso en la activity
        topBar.setNavigationOnClickListener{

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

        //gestión del botón de guardar y la llamada a la base de datos
        save_btn = findViewById(R.id.save_card_button)
        title_text = findViewById(R.id.SetTitle)
        input_text = findViewById(R.id.Input)
        result_text = findViewById(R.id.Result)
        save_btn.setOnClickListener(){
            if(title_text.text.isNotEmpty() && input_text.text.isNotEmpty() && result_text.text.isNotEmpty()){
                firebaseDatabase = FirebaseDatabase.getInstance()
                databaseReference = firebaseDatabase.reference.child("user")
                //obtener las shares preferences y compararlo con los usuarios de la bd
                //añado la carta a "cards" con child( card (child atributos))
                Toast.makeText(this, "Se guarda la card", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

