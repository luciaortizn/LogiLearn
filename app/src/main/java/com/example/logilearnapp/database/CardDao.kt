package com.example.logilearnapp.database

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
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
                    val cardSnapshot = snapshot.child(cardId)
                    if (cardSnapshot.exists()) {
                        val id = cardSnapshot.child("id").getValue(String::class.java)
                        val input = cardSnapshot.child("input").getValue(String::class.java)
                        val result = cardSnapshot.child("result").getValue(String::class.java)
                        val title = cardSnapshot.child("title").getValue(String::class.java)
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
    fun updateCard(databaseReference: DatabaseReference, card: Card, userId:String, context: Context){
        val cardReference  = databaseReference.child("user").child(userId).child("cards").child(card.id)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    cardReference.child("title").setValue(card.title)
                    cardReference.child("input").setValue(card.input)
                    cardReference.child("result").setValue(card.result)
                    Toast.makeText(context, "Valores actualizados", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {

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