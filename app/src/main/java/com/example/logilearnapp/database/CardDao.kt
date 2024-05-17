package com.example.logilearnapp.database

import android.content.Context
import android.content.SharedPreferences
import com.example.logilearnapp.ui.card.Card
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.protobuf.Value

class CardDao {
    //referncia directa al id y voy iterando
    fun getCardsByIdList(callback: FirebaseCallback,databaseReference: DatabaseReference, userId: String,cardIdList :ArrayList<String>){
        val cardListDB: ArrayList<Card> = arrayListOf()
         val cardsRef = databaseReference.child("user").child(userId).child("cards")
        cardsRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               cardListDB.clear()
                for (cardId in cardIdList) {
                    // Buscar la tarjeta correspondiente en la base de datos
                    val cardSnapshot = snapshot.child(cardId)
                    // Verificar si la tarjeta existe en la base de datos
                    if (cardSnapshot.exists()) {
                        // Obtener los datos de la tarjeta
                        val id = cardSnapshot.child("id").getValue(String::class.java)
                        val input = cardSnapshot.child("input").getValue(String::class.java)
                        val result = cardSnapshot.child("result").getValue(String::class.java)
                        val title = cardSnapshot.child("title").getValue(String::class.java)

                        // Crear el objeto Card y agregarlo a la lista
                        cardListDB.add(Card(id.toString(), title.toString(), input.toString(), result.toString()))
                    }
                }
                callback.onCallback(cardListDB)
            }

            override fun onCancelled(error: DatabaseError) {
               //toast?
            }
        })

    }
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