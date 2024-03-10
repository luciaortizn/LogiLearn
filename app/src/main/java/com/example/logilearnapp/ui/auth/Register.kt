package com.example.logilearnapp.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.logilearnapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
class Register : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("user")

        mAuth = FirebaseAuth.getInstance()

        binding.registerBtn.setOnClickListener{

            val email = binding.emailField.editText?.text.toString()
            val name = binding.nameField.editText?.text.toString()
            val surname  = binding.surnameField.editText?.text.toString()
            val password = binding.passField.editText?.text.toString()
            val confirmPassword = binding.rePassField.editText?.text.toString()
            registerUser(email, name, surname, password, confirmPassword)



        }
        /**
        binding.changeLogIn.setOnClickListener {
            val intent = Intent(this@Register, Login::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()

        }**/
    }
    fun registerUser(email:String, name:String,surname:String ,password: String, rePassword: String){
         val validationResult =validateFields(email, name, surname, password, rePassword)
        if (validationResult == ValidationResult.SUCCESS) {


            databaseReference.orderByChild("name").equalTo(name).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (!dataSnapshot.exists()){
                        val id = databaseReference.push().key
                        //ver esta mierda
                        //falta encriptarla
                        val userData = com.example.logilearnapp.UserData(id, email, name, surname,password)

                        databaseReference.child(id!!).setValue(userData)

                        Toast.makeText(this@Register, "SingUp Successfull", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@Register, Login::class.java)
                        startActivity(intent)
                        finish()

                    } else {
                        showUserExistsAlert()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@Register, "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }

            })

        } else {
            showInvalidFieldsAlert(validationResult)
        }
    }
    private fun validateFields(email:String, name:String, surname:String, password: String, rePassword: String ):Any{
        return when {
            isAnyFieldEmpty(email, name, surname, password, rePassword) -> ValidationResult.EMPTY_FIELD
            containsWhiteSpace(email, name, password, rePassword) -> ValidationResult.WHITESPACE_IN_FIELD
            !isEmailValid(email) -> ValidationResult.INVALID_EMAIL
            containsNumbers(name, surname) -> ValidationResult.INVALID_NAME_OR_LASTNAME
            !doPasswordsMatch(password, rePassword) -> ValidationResult.PASSWORDS_NOT_MATCH
            !isPasswordStrongEnough(password) -> ValidationResult.WEAK_PASSWORD
            else -> ValidationResult.SUCCESS
        }
    }

    private fun containsWhiteSpace(vararg fields: String): Boolean {
        return fields.any { it.contains(" ") }
    }
    private fun isEmailValid(email: String): Boolean {
        val emailPattern = Patterns.EMAIL_ADDRESS
        return emailPattern.matcher(email).matches()
    }
    private fun isAnyFieldEmpty(vararg fields: String): Boolean {
        return fields.any { it.isBlank() }
    }
    private fun containsNumbers(vararg fields: String): Boolean {
        val regex = Regex("\\d")
        return fields.any { field ->
            regex.containsMatchIn(field)
        }
    }
    private fun doPasswordsMatch(password: String, repeatPassword: String): Boolean {
        return password == repeatPassword
    }

    private fun isPasswordStrongEnough(password: String): Boolean {
        val digitRegex = Regex("\\d")
        val upperCaseRegex = Regex("[A-Z]")
        val lowerCaseRegex = Regex("[a-z]")
        val specialCharacterRegex = Regex("[^A-Za-z0-9]")

        return password.length >= 8 &&
                digitRegex.containsMatchIn(password) &&
                upperCaseRegex.containsMatchIn(password) &&
                lowerCaseRegex.containsMatchIn(password) &&
                specialCharacterRegex.containsMatchIn(password) &&
                !password.contains(" ")
    }
    private fun showInvalidFieldsAlert(validationResult: Any) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Invalid Fields")

        val errorMessage = when (validationResult) {
            ValidationResult.EMPTY_FIELD -> "Please fill in all fields."
            ValidationResult.INVALID_EMAIL -> "Please enter a valid email address."
            ValidationResult.INVALID_NAME_OR_LASTNAME -> "Please enter a valid name/lastname."
            ValidationResult.PASSWORDS_NOT_MATCH -> "Passwords do not match."
            ValidationResult.WEAK_PASSWORD -> "Password does not meet the minimum requirements."

            //aquí hay que ver cuál es
            else -> "Please check your entries."
        }

        alertDialogBuilder.setMessage(errorMessage)
        alertDialogBuilder.setPositiveButton("OK") { _, _ ->
            // Dismiss the alert dialog if OK is pressed
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
    private fun showUserExistsAlert() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("User Exists")
        alertDialogBuilder.setMessage("User already exists")
        alertDialogBuilder.setPositiveButton("OK") { _, _ ->

        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
    enum class ValidationResult {
        SUCCESS,
        INVALID_EMAIL,
        EMPTY_FIELD,
        INVALID_NAME_OR_LASTNAME,
        PASSWORDS_NOT_MATCH,
        WEAK_PASSWORD,
        WHITESPACE_IN_FIELD
    }



}