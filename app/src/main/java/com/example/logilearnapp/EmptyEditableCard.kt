package com.example.logilearnapp
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.logilearnapp.api.ConfigUtils
import com.example.logilearnapp.api.TranslationCallback
import com.example.logilearnapp.data.TranslateRequest
import com.example.logilearnapp.viewmodel.EmptyEditableCardViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputLayout
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
   private lateinit var translationBtn: MaterialButton
    private lateinit var viewModel : EmptyEditableCardViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empty_editable_card)
        val navIcon: Icon// findViewById<>()
        val topBar: MaterialToolbar = findViewById(R.id.topAppBarCard)
        //hace que el botón x se produzca un retroceso en la activity
        topBar.setNavigationOnClickListener {

            onBackPressedDispatcher.onBackPressed()
        }
        //gestión del botón de guardar y la llamada a la base de datos
        save_btn = findViewById(R.id.save_card_button)
        title_text = findViewById(R.id.SetTitle)
        input_text = findViewById(R.id.Input)
        result_text = findViewById(R.id.Result)
        translationBtn = findViewById(R.id.Translate)

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


        save_btn.setOnClickListener() {
            if (title_text.text.isNotEmpty() && input_text.text.isNotEmpty() && result_text.text.isNotEmpty()) {
                firebaseDatabase = FirebaseDatabase.getInstance()
                databaseReference = firebaseDatabase.reference.child("user")
                val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
                val userId = sharedPreferences.getString("id", "")
                val databaseReference = FirebaseDatabase.getInstance().reference.child("user")

                databaseReference.orderByChild("id").equalTo(userId)
                    .addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (userSnapshot in dataSnapshot.children) {

                                    //mete el id como key:
                                    val id =
                                        databaseReference.child(userId.toString()).child("cards")
                                            .push().key

                                    // val userData = com.example.logilearnapp.UserData(id, email, name, surname,password)

                                    val userData = userSnapshot.getValue(UserData::class.java)
                                    if (userData != null) {
                                        val cardData = HashMap<String, Any>()
                                        cardData["id"] = id.toString()
                                        cardData["title"] = title_text.text.toString()
                                        cardData["input"] = input_text.text.toString()
                                        cardData["result"] = result_text.text.toString()
                                        // Obtenemos una referencia al nodo del usuario actual
                                        val currentUserRef =
                                            databaseReference.child(userId.toString())
                                                .child("cards")

                                        // Añadimos el nodo "card" con sus campos hijos al mismo nivel que el nodo "id"
                                        // databaseReference.child(id!!).setValue(userData)
                                        currentUserRef.child(id!!).setValue(cardData)
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    Toast.makeText(
                                                        this@EmptyEditableCard,
                                                        "Información guardada.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    val intent = Intent(
                                                        this@EmptyEditableCard,
                                                        MainActivity::class.java
                                                    )
                                                    startActivity(intent)
                                                } else {
                                                    Toast.makeText(
                                                        this@EmptyEditableCard,
                                                        "Error, inténtalo más tarde.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
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

            } else {
                Toast.makeText(this, "No has rellenado todos los campos", Toast.LENGTH_SHORT).show()
            }

        }
        viewModel = ViewModelProvider(this)[EmptyEditableCardViewModel::class.java]
        val textInputLayout: TextInputLayout = findViewById(R.id.menu)
        val autoCompleteTextView: AutoCompleteTextView =
            textInputLayout.editText as AutoCompleteTextView
        autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            // Obtén el texto del elemento seleccionado
            val selectedItem = parent.getItemAtPosition(position).toString()
            val itemCountry = getRequiredLanguageName(selectedItem)
            //aquí obtengo el id del elemento de la lista de idiomas
            Toast.makeText(this@EmptyEditableCard,itemCountry , Toast.LENGTH_SHORT).show()
            //(requestBody: TranslateRequest,callback: TranslationCallback, apiKey:String
            val requestBody = TranslateRequest(input_text.text.toString(), itemCountry.toString())
            if(input_text.text.isNotEmpty()){
                translationBtn.setOnClickListener(){
                    viewModel.postTranslation(requestBody, { it: String? ->
                        // Maneja la traducción recibida
                        it?.let { translation ->
                            // Actualiza el texto del EditText con la traducción recibida
                            result_text.setText(translation)
                        } ?: run {
                            // Maneja el caso en el que no se pudo obtener la traducción
                            Toast.makeText(this@EmptyEditableCard, "Error al obtener la traducción", Toast.LENGTH_SHORT).show()
                        }
                    }, ConfigUtils.getDeeplApiKey(this).toString())
                }
            }

        }

    }
    fun getRequiredLanguageName(selectedText:String):String?{
        val regex = Regex("\\((.*?)\\)")
        val matchResult = regex.find(selectedText)
        return matchResult?.groupValues?.get(1)
    }


}

