package com.example.logilearnapp
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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
                /**
                R.id.item_imagen -> {
                    Toast.makeText(this, "Imagen", Toast.LENGTH_SHORT).show()
                    //proporcionar acceso a su galería
                    // Handle more item (inside overflow menu) press
                    true
                }**/

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
                val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
                val userId = sharedPreferences.getString("id", "")
                val databaseReference = FirebaseDatabase.getInstance().reference.child("user")

                databaseReference.orderByChild("id").equalTo(userId).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (userSnapshot in dataSnapshot.children) {

                                //mete el id como key:
                                val id = databaseReference.child(userId.toString()).child("cards").push().key

                               // val userData = com.example.logilearnapp.UserData(id, email, name, surname,password)

                                val userData = userSnapshot.getValue(UserData::class.java)
                                if (userData != null) {
                                    val cardData = HashMap<String, Any>()
                                    cardData["id"]= id.toString()
                                    cardData["title"] = title_text.text.toString()
                                    cardData["input"] = input_text.text.toString()
                                    cardData["result"] = result_text.text.toString()
                                    // Obtenemos una referencia al nodo del usuario actual
                                    val currentUserRef = databaseReference.child(userId.toString()).child("cards")

                                    // Añadimos el nodo "card" con sus campos hijos al mismo nivel que el nodo "id"
                                    // databaseReference.child(id!!).setValue(userData)
                                    currentUserRef.child(id!!).setValue(cardData)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Toast.makeText(this@EmptyEditableCard, "Información guardada.",Toast.LENGTH_SHORT).show()
                                                val intent = Intent(this@EmptyEditableCard, MainActivity::class.java)
                                                startActivity(intent)
                                            } else {
                                               Toast.makeText(this@EmptyEditableCard, "Error, inténtalo más tarde.",Toast.LENGTH_SHORT).show()
                                            }
                                        }


                                }
                            }


                        } else {
                            // el usuario no se encuentra se muestra alert

                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                })

                //te lleva al home fragment

            }else{
                Toast.makeText(this, "No has rellenado todos los campos", Toast.LENGTH_SHORT).show()
            }

        }
    }
}

