package com.example.logilearnapp.database

import android.content.Context
import android.service.autofill.UserData
import android.widget.Toast
import com.example.logilearnapp.ui.folder.Folder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.protobuf.Value

class FolderDao {
    /**
     * La referncia es .reference
     */
    fun getAllFolders(databaseReference: DatabaseReference, userId: String):ArrayList<Folder>{
        val folderList : ArrayList<Folder> = ArrayList()
        databaseReference.child("user").child(userId).child("folders")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //llego al id
                    for (folderSnapshot in snapshot.children) {
                        val folderId = folderSnapshot.key // Obtener el ID de la carpeta
                        for(idSnapshot in folderSnapshot.children){
                            val folderTitle = idSnapshot.child("title").getValue(String::class.java)
                            val folderDataImage = idSnapshot.child("isFavorite").getValue(String::class.java)
                            // manejo de lista de CARD-ID
                            val folderCardId = idSnapshot.child("cardId").getValue(object : GenericTypeIndicator<ArrayList<String>>() {})
                            // Aquí puedes obtener más atributos de la carpeta según tu estructura de datos
                            if (folderId != null && folderTitle != null && folderCardId!= null  && folderDataImage != null  ) {
                                val folder = Folder(folderId, folderDataImage, folderTitle, folderCardId) // Crear un objeto Folder
                                folderList.add(folder) // Agregar la carpeta a la lista
                            }
                        }

                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    // Manejar errores de cancelación
                }
            })
        return folderList
    }
    fun getAllFolderTitles(databaseReference: DatabaseReference, userId: String):ArrayList<String>{
        val listTitles = ArrayList<String>()
        val listFolders =  getAllFolders(databaseReference, userId)
        for (folder in listFolders ){
           listTitles.add(folder.dataTitle)
        }
        return listTitles
    }
    fun addFolder(databaseReference: DatabaseReference, userId: String, folder:Folder ){
        databaseReference.orderByChild("id").equalTo(userId).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for (userSnapshot in snapshot.children){
                       val id = databaseReference.child(userId.toString()).child("folders").push().key
                        val userData = userSnapshot.getValue(com.example.logilearnapp.UserData::class.java)
                        if(userData!=null){
                            val folderData = HashMap<String, Any>()
                            //manejo repetidos
                            if(!folderData.isEmpty() || !folder.dataTitle.equals("repetido")){
                                folderData["id"] = id.toString()
                                folderData["isFavorite"] = folder.isFavorite
                                folderData["dataTitle"] = folder.dataTitle
                                val cardIdArray = folder.cardId.toTypedArray()
                                folderData["cardId"] = cardIdArray
                                val currentUserRef = databaseReference.child(userId.toString()).child("folders")
                                currentUserRef.child(id!!).setValue(folderData).addOnCompleteListener{ task ->
                                    if(task.isSuccessful){
                                        //  ??? start intent o set toast...
                                    }else{

                                    }
                                }
                            }

                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    fun addNewCardIdValue(databaseReference: DatabaseReference, userId: String, newCardId: String, folderId:String) {
        val foldersRef = databaseReference.child("user").child("id").child(userId).child("folders").child(folderId).child("cardId")
        // Verificar si el newCardId ya existe en la lista
        foldersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val currentCardIds: ArrayList<String> = dataSnapshot.getValue(object : GenericTypeIndicator<ArrayList<String>>() {}) ?: arrayListOf()
                if (currentCardIds.contains(newCardId)) {
                    println("Error: El cardId ya existe en la lista.")
                } else {
                    currentCardIds.add(newCardId)
                    foldersRef.setValue(currentCardIds).addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                        } else {

                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar error de cancelación
            }
        })
    }
    fun deleteCardId(databaseReference: DatabaseReference, userId: String, cardIdToDelete: String, folderId:String) {
        databaseReference.child("user").child("id").child(userId).child("folders").child(folderId).child("cardId").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Obtener la lista actual de cardId
                val currentCardIds: ArrayList<String> = dataSnapshot.getValue(object : GenericTypeIndicator<ArrayList<String>>() {}) ?: arrayListOf()
                currentCardIds.remove(cardIdToDelete)
                databaseReference.child("user").child("id").equalTo(userId).ref.child("folders").child("cardId").setValue(currentCardIds).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                    } else {

                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
    fun editFolder(databaseReference: DatabaseReference, folder:Folder){
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
    fun deleteFolder(databaseReference: DatabaseReference){
        databaseReference.removeValue().addOnSuccessListener {
        }
    }
    fun getUserIdSharedPreferences(context: Context):String?{
        val sharedPreferences =context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("id", "")
    }
}