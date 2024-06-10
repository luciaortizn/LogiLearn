package com.example.logilearnapp.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.logilearnapp.data.UserData
import com.example.logilearnapp.databinding.ActivityRegisterBinding
import com.example.logilearnapp.util.Validator
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
class Register : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private lateinit var firebaseDatabase: FirebaseDatabase
    //realtime database
    private lateinit var databaseReference: DatabaseReference

    //database auth
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("user")
        mAuth = FirebaseAuth.getInstance()
        val editEmail = binding.emailField
        val editName = binding.nameField
        val editSurname  = binding.surnameField
        val editPassword = binding.passField
        val editRepeatPassword = binding.rePassField

        //validaciones dinámicas:
        editEmail.editText!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val mail = s.toString()
                if (editEmail.editText?.text.toString().isBlank()) {
                    editEmail.error = "Campo vacío"
                }else if(!Validator.isValidEmail(mail)) {
                    editEmail.error = "Correo no válido"
                }else{
                    editEmail.error = null
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        editName.editText!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val name = s.toString()
                if (editName.editText?.text.toString().isBlank()) {
                    editName.error = "Campo vacío"
                }else if(!Validator.isValidName(name)) {
                    editName.error = "Nombre no válido"
                }else{
                    editName.error = null
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        editSurname.editText!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val surname = s.toString()
                if (editSurname.editText?.text.toString().isBlank()) {
                    editSurname.error = "Campo vacío"
                }else if(!Validator.isValidSurname(surname)){
                    editSurname.error = "Apellido no válido"
                }else{
                    editSurname.error = null
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        editPassword.editText!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (editPassword.editText?.text.toString().isBlank()) {
                    editPassword.error = "Campo vacío"
                }else if(!Validator.isValidPassword(editPassword.editText!!.text.toString())){
                    editPassword.error = "Contraseña no válida"
                }else{
                    editPassword.error = null
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        editRepeatPassword.editText!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (editRepeatPassword.editText?.text.toString().isBlank()) {
                    editRepeatPassword.error = "Campo vacío"
                } else if (editPassword.editText!!.text.toString() != editRepeatPassword.editText!!.text.toString()) {
                    editRepeatPassword.error = "Las contraseñas no coinciden"
                } else {
                    editRepeatPassword.error = null
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.registerBtn.setOnClickListener{
            if(editEmail.error.isNullOrBlank() && editName.error.isNullOrBlank() && editSurname.error.isNullOrBlank() && editPassword.error.isNullOrBlank() && editRepeatPassword.error.isNullOrBlank()){
                registerUser(editEmail.editText?.text.toString(), editName.editText?.text.toString(), editSurname.editText?.text.toString(), editPassword.editText?.text.toString(), editRepeatPassword.editText?.text.toString())
            }else{
                MaterialAlertDialogBuilder(this)
                    //hacer validaciones en editar perfil
                    .setTitle("Campos inválidos")
                    .setMessage("No puedes registrarte hasta que no rellenes correctamente los campos")
                    .setPositiveButton("De acuerdo") { dialog, which ->
                        dialog.dismiss()

                    }.create().apply {
                        show()
                    }
            }
        }
        /**
        binding.registerBtn.setOnClickListener {
            val intent = Intent(this@Register, Login::class.java)
            startActivity(intent)
           // overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()

        }*/
    }
    fun registerUser(email:String, name:String,surname:String ,password: String, rePassword: String){

            databaseReference.orderByChild("name").equalTo(name).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (!dataSnapshot.exists()){
                        val id = databaseReference.push().key
                        //falta encriptarla
                        val userData = UserData(id, email, name, surname,password)

                        databaseReference.child(id!!).setValue(userData)

                        Toast.makeText(this@Register, "Te has registrado", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@Register, Login::class.java)
                        startActivity(intent)
                        finish()

                    } else {
                        //lo dejo
                        showUserExistsAlert()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@Register, "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }

            })
    }
    private fun showUserExistsAlert() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Usuario en uso")
        alertDialogBuilder.setMessage("Este usuario ya ha sido creado")
        alertDialogBuilder.setPositiveButton("OK") { _, _ ->
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}