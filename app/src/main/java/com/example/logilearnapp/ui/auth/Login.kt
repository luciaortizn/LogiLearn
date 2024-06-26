package com.example.logilearnapp.ui.auth


import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.logilearnapp.ui.common.MainActivity
import com.example.logilearnapp.R
import com.example.logilearnapp.data.UserData
import com.example.logilearnapp.util.HashPassword
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var editEmail: TextInputLayout
    private lateinit var editPassword: TextInputLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("user")
        editPassword = findViewById(R.id.passField)
        editEmail = findViewById(R.id.emailField)

        val toRegisterBtn :MaterialButton = findViewById(R.id.btnRegister)
        //lo meto en metodo param fragment, desde fragment accedo al metodo
       toRegisterBtn.setOnClickListener{
            val intent = Intent(this@Login, Register::class.java)
            startActivity(intent)

        }
        val loginbtn: Button = findViewById(R.id.loginBtn)

        //validaciones dinámicas:
        editEmail.editText!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val email = s.toString()
                if (editEmail.editText?.text.toString().isBlank()) {
                    editEmail.error = "Campo vacío"
                }else {
                    editEmail.error = null
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        editPassword.editText!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val password = s.toString()
                if (editPassword.editText?.text.toString().isBlank()) {
                    editPassword.error = "Campo vacío"
                }else {
                    editPassword.error = null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        loginbtn.setOnClickListener {
            // Crear un Intent para la otra actividad
            if (editEmail.error.isNullOrBlank()) {
               val hashed = HashPassword.hashPassword(editPassword.editText?.text.toString())
                Log.d("hash", hashed)
                loginUser(editEmail.editText?.text.toString(),hashed)
            } else {
                MaterialAlertDialogBuilder(this)
                    //hacer validaciones en editar perfil
                    .setTitle("Campos no válidos")
                    .setMessage("Tienes email o contraseña incorrecta, prueba otra vez")
                    .setPositiveButton("De acuerdo") { dialog, which ->
                        dialog.dismiss()

                    }.create().apply {
                        show()
                    }
            }
        }

    }
    override fun onStart() {
        super.onStart()
       // var currentUser = auth.currentUser;
        //updateUI(currentUser);
    }
    private fun loginUser(email: String, password: String) {
        databaseReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (userSnapshot in dataSnapshot.children) {

                        val userData = userSnapshot.getValue(UserData::class.java)
                        // Si las credenciales son válidas, se inicia sesión y se almacenan datos del usuario
                        if (userData != null) {
                            val passwordDb = userData.password
                           // val enteredHash = PasswordEncrypt.hashPassword(password)
                            //println("Stored Hash: $storedHash")
                            //println("Entered Hash: $enteredHash")
                            if (passwordDb == password) {
                                editPassword.error = null
                                editEmail.error= null
                                // Login successful
                                val intent = Intent(this@Login, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                                saveLoginState(true)
                                saveUserData(userData)
                                return
                            }
                        }
                    }
                    //no coincide la contraseña se muestra alert
                   editPassword.error = "Contraseña no encontrada"
                } else {
                    // el usuario no se encuentra se muestra alert
                   editEmail.error = "Email no encontrado"
                }
            }



            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@Login, "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun saveLoginState(isLogged: Boolean) {
        val sharedPreferences = getSharedPreferences("myPrefs",  Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLogged", isLogged)
        editor.apply()
    }
    private fun saveUserData(userData: UserData) {
        //clave valor
        val sharedPreferences =
            getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("id", userData.id)
        editor.putString("email", userData.email)
        editor.putString("name", userData.name)
        editor.putString("surname", userData.surname)
        editor.putString("password", userData.password)
        editor.apply()
    }
}