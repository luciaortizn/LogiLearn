package com.example.logilearnapp.repository

import com.example.logilearnapp.data.UserData
import com.example.logilearnapp.database.FirebaseCallback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class UserDao {
    fun getAllUsers(callback: FirebaseCallback, databaseReference: DatabaseReference){
        val userRef = databaseReference.child("user")
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userList: ArrayList<UserData> = arrayListOf()
                for(userKey in dataSnapshot.children){

                    val id = userKey.child("id").getValue(String::class.java)
                    val name = userKey.child("name").getValue(String::class.java)
                    val surname = userKey.child("surname").getValue(String::class.java)
                    val email = userKey.child("email").getValue(String::class.java)
                    val password = userKey.child("password").getValue(String::class.java)
                    val user = UserData(id, email, name, surname, password)
                    userList.add(user)
                }
                callback.onUsersCallback(userList)
            }
            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
    fun getUser(callback: FirebaseCallback, databaseReference: DatabaseReference, userId :String){
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
    fun updateUser(databaseReference: DatabaseReference,userId :String, user: UserData){
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
