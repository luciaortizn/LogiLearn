package com.example.logilearnapp.ui.auth


import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.logilearnapp.MainActivity
import com.example.logilearnapp.R
import com.example.logilearnapp.UserData
import com.example.logilearnapp.view.RegisterFragment
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseDatabase = FirebaseDatabase.getInstance()

        databaseReference = firebaseDatabase.reference.child("user")

        val fragment = RegisterFragment()
        val textView: TextView = findViewById(R.id.textoRegistrarse)
        //lo meto en metodo param fragment, desde fragment accedo al metodo
        textView.setOnClickListener{
            val intent = Intent(this@Login, Register::class.java)
            startActivity(intent)

        }
        val loginbtn: Button = findViewById(R.id.loginBtn)

        loginbtn.setOnClickListener {

            val email : TextInputLayout = findViewById(R.id.emailField)  //  binding.editTextEmail.text.toString()
            val password :TextInputLayout= findViewById(R.id.passField)// binding.editTextPassword.text.toString()
            // Crear un Intent para la otra actividad
            if (email.editText?.text.toString().isBlank() || password.editText?.text.toString().isBlank()) {
                showEmptyFieldsAlert()
            } else {
                loginUser(email.editText?.text.toString(), password.editText?.text.toString())
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
                            val password_db = userData.password
                           // val enteredHash = PasswordEncrypt.hashPassword(password)

                            //println("Stored Hash: $storedHash")
                            //println("Entered Hash: $enteredHash")

                            if (password_db == password) {
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
                    showInvalidPasswordAlert()
                } else {
                    // el usuario no se encuentra se muestra alert
                    showInvalidUsernameAlert()
                }
            }



            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@Login, "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun showEmptyFieldsAlert() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Empty Fields")
        alertDialogBuilder.setMessage("Please fill in all fields.")
        alertDialogBuilder.setPositiveButton("OK") { _, _ ->
            // Dismiss the alert dialog if OK is pressed
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
    private fun showInvalidPasswordAlert() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Invalid Password")
        alertDialogBuilder.setMessage("Invalid password.")
        alertDialogBuilder.setPositiveButton("OK") { _, _ ->
            // Dismiss the alert dialog if OK is pressed
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
    private fun showInvalidUsernameAlert() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Invalid Email")
        alertDialogBuilder.setMessage("Invalid email.")
        alertDialogBuilder.setPositiveButton("OK") { _, _ ->
            // Dismiss the alert dialog if OK is pressed
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
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

        /*
        *  if (userData.moviesLiked != null) {
            // Convierte la lista a un String para almacenarla en Shared Preferences
            val moviesLikedString = userData.moviesLiked.joinToString(",")
            editor.putString("moviesLiked", moviesLikedString)
        }
        * */

        editor.apply()
    }
}