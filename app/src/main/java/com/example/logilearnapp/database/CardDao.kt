package com.example.logilearnapp.database

import android.content.Context
import android.content.SharedPreferences
import com.example.logilearnapp.ui.card.Card
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CardDao {
    //referncia directa al id y voy iterando
    fun editCard(databaseReference: DatabaseReference, card: Card){
        // TODO: terminar función 
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                
                for (childSnapshot in dataSnapshot.children) {
                    // la referencia al hijo específico
                    val childNodeReference = databaseReference.child(childSnapshot.key!!)
                    //childNodeReference.child().setValue(value)
                    //movidas 
                    
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Maneja errores de lectura de datos
            }
        })
    }
    //referencia directa al id, uso id para accedder a su chu
    fun deleteCard(databaseReference :DatabaseReference ){
        databaseReference.removeValue().addOnSuccessListener {
        }
    }
     fun getUserIdSharedPreferences(context: Context):String?{
        val sharedPreferences =context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("id", "")
    }
}