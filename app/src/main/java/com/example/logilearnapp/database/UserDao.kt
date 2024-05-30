package com.example.logilearnapp.database

import com.example.logilearnapp.UserData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class UserDao {
    fun addUser(databaseReference: DatabaseReference){

    }
    fun getUser(callback: FirebaseCallback,databaseReference: DatabaseReference,userId :String){
        val userRef = databaseReference.child("user").child(userId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val id = dataSnapshot.child("id").getValue(String::class.java)
                val name = dataSnapshot.child("name").getValue(String::class.java)
                val surname = dataSnapshot.child("surname").getValue(String::class.java)
                val email = dataSnapshot.child("email").getValue(String::class.java)
                val password = dataSnapshot.child("password").getValue(String::class.java)
                val user = UserData(id, email, name, surname, password)

                callback.onSingleUserCallback(user)
            }
            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
    fun updateUser(databaseReference: DatabaseReference,userId :String, user:UserData ){
       val userRef = databaseReference.child("user").child(userId)
        // Crear un mapa con los valores del objeto UserData
        val userUpdates = mapOf(
            "id" to user.id,
            "name" to user.name,
            "surname" to user.surname,
            "email" to user.email,
            "password" to user.password
        )
        // Actualizar los valores en la base de datos
        userRef.updateChildren(userUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // La actualización fue exitosa
                println("User updated successfully.")
            } else {
                // La actualización falló
                println("Failed to update user: ${task.exception}")
            }
        }

    }
    fun deleteUser(databaseReference: DatabaseReference, userId: String){
         val userRef= databaseReference.child("user").child(userId)
        userRef.removeValue().addOnSuccessListener {
        }
    }
}
