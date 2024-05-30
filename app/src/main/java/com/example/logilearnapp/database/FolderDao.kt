package com.example.logilearnapp.database

import android.content.Context
import android.service.autofill.UserData
import android.util.Log
import android.widget.Toast
import com.example.logilearnapp.ui.card.Card
import com.example.logilearnapp.ui.folder.Folder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.protobuf.Value

class FolderDao {
    /**
     * reference: /
     */
    fun getFoldersByUser(callback: FirebaseCallback,databaseReference: DatabaseReference, userId: String) {
        val folderListDB: ArrayList<Folder> = arrayListOf()
        val folderReference =  databaseReference.child("user").child(userId).child("folders")

        //ojo addValueEventListener
       folderReference.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                // Limpiar la lista antes de agregar nuevas carpetas
                folderListDB.clear()
                // Iterar sobre cada carpeta en la base de datos
                for (folder in snapshot.children) {
                    // Obtener los datos de la carpeta
                    val folderId = folder.child("id").getValue(String::class.java)
                    val folderTitle = folder.child("dataTitle").getValue(String::class.java)
                    val folderIsFavorite = folder.child("isFavorite").getValue(String::class.java)
                    //nuevo
                    val folderCardId = folder.child("cardId").getValue(object : GenericTypeIndicator<ArrayList<String>>() {}) ?: arrayListOf()
                    folderListDB.add(Folder(folderId.toString(), folderIsFavorite.toString(), folderTitle.toString(), folderCardId))
                }
                callback.onFolderCallback(folderListDB)
            }
            override fun onCancelled(error: DatabaseError) {
                // databaseError.getCode() == DatabaseError.DISCONNECTED || databaseError.getCode() == DatabaseError.NETWORK_ERROR
            }
        })
    }
    fun getFolderTitles(databaseReference: DatabaseReference, userId: String) {
        val listTitles = ArrayList<String>()
        getFoldersByUser(object : FirebaseCallback {

            override fun onCallback(cardList: ArrayList<Card>) {

            }

            override fun onSingleUserCallback(user: com.example.logilearnapp.UserData) {

            }

            override fun onFolderCallback(folderList: ArrayList<Folder>) {
                Log.d("verificar", "La lista de carpetas tiene ${folderList.size} elementos.")
                for (folder in folderList ){
                    listTitles.add(folder.dataTitle)
                }

            }
        },databaseReference,userId)
    }
    fun addFolder(databaseReference: DatabaseReference, userId: String, folder: Folder, context: Context) {
        val folderTitle = folder.dataTitle // Obtener el título de la carpeta nueva
        val userFoldersRef = databaseReference.child("user").child(userId).child("folders")

        userFoldersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var isFolderTitleDuplicate = false

                // Iterar sobre todas las carpetas del usuario
                for (folderSnapshot in snapshot.children) {
                    val existingFolderTitle = folderSnapshot.child("dataTitle").getValue(String::class.java)
                    if (existingFolderTitle == folderTitle) {
                        // Ya existe una carpeta con el mismo nombre
                        isFolderTitleDuplicate = true
                        Toast.makeText(context, "Ya existe una carpeta con ese nombre",Toast.LENGTH_SHORT).show()
                        break
                    }
                }

                if (!isFolderTitleDuplicate) {
                    // No hay ninguna carpeta con el mismo nombre, se puede crear la carpeta
                    val folderId = userFoldersRef.push().key // Generar un nuevo ID para la carpeta
                    val folderData = HashMap<String, Any>()
                    folderData["id"] = folderId.toString()
                    folderData["isFavorite"] = folder.isFavorite
                    folderData["dataTitle"] = folderTitle
                    folderData["cardId"] = folder.cardId.toList()

                    userFoldersRef.child(folderId!!).setValue(folderData).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                           Toast.makeText(context, "Carpeta guardada",Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar error de cancelación
                // Por ejemplo, puedes mostrar un mensaje de error
            }
        })
    }
    fun addNewCardIdValue(databaseReference: DatabaseReference, userId: String, newCardId: String,  folderId:String, context: Context) {
        val cardIdRef = databaseReference.child("user").child(userId).child("folders").child(folderId).child("cardId")
        // Verificar si el newCardId ya existe en la lista
        cardIdRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val currentCardIds: ArrayList<String> = dataSnapshot.getValue(object : GenericTypeIndicator<ArrayList<String>>() {}) ?: arrayListOf()
                if (currentCardIds.contains(newCardId)) {
                    Toast.makeText(context, "Ya existe esa tarjeta en la carpeta seleccionada", Toast.LENGTH_SHORT).show()
                } else {

                    currentCardIds.add(newCardId)
                    cardIdRef.setValue(currentCardIds).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Se ha guardado ", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Ha habido un problema", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar error de cancelación
            }
        })
    }

    fun deleteCardId(databaseReference: DatabaseReference, userId: String, cardIdToDelete: String) {
        val folderReference = databaseReference.child("user").child(userId).child("folders")

        folderReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val folderIds = dataSnapshot.children.map { it.key } // Obtener los IDs de todas las carpetas
                folderIds.forEach { folderId ->
                    val cardIdsRef = folderReference.child(folderId!!).child("cardId") // Referencia a la lista de cardId de la carpeta actual
                    cardIdsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(cardIdSnapshot: DataSnapshot) {
                            val currentCardIds: ArrayList<String> = cardIdSnapshot.getValue(object : GenericTypeIndicator<ArrayList<String>>() {}) ?: arrayListOf()
                            if (currentCardIds.contains(cardIdToDelete)) {
                                currentCardIds.remove(cardIdToDelete) // Eliminar el cardId de la lista
                                cardIdsRef.setValue(currentCardIds).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        // El cardId se eliminó con éxito
                                    } else {
                                        // Hubo un problema al eliminar el cardId
                                    }
                                }
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Manejar error de cancelación
                        }
                    })
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar error de cancelación
            }
        })
    }
    fun updateIsFavorite(databaseReference: DatabaseReference, userId: String, newIsFavoriteValue: String, folderId: String, context: Context) {
        val folderReference = databaseReference.child("user").child(userId).child("folders").child(folderId)

        folderReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val favoriteRef = folderReference.child("isFavorite")
                // Referencia a la lista de cardId de la carpeta actual
                favoriteRef.setValue(newIsFavoriteValue).addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                       Toast.makeText(context, "Ha habido un error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    fun updateFolder(databaseReference: DatabaseReference,userId:String, folder:Folder, context: Context){
        val folderReference  = databaseReference.child("user").child(userId).child("folders").child(folder.id)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()){
                   folderReference.child("dataTitle").setValue(folder.dataTitle)
                  Toast.makeText(context, "Carpeta actualizada", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
    fun deleteFolder(databaseReference: DatabaseReference){
       // val folderRef = databaseReference.child("user").child(userId).child("folders").child(folderId)
        databaseReference.removeValue().addOnSuccessListener {
        }
    }

    fun getUserIdSharedPreferences(context: Context):String?{
        val sharedPreferences =context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("id", "")
    }
}