package com.example.logilearnapp.ui.card
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.logilearnapp.R
import com.example.logilearnapp.database.CardDao
import com.example.logilearnapp.database.FirebaseCallback
import com.example.logilearnapp.database.FolderDao
import com.example.logilearnapp.ui.folder.Folder
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CardAdapter(private val dataList:ArrayList<Card>?,private val context: Context):RecyclerView.Adapter<CardAdapter.ViewHolderClass>() {
   private var onClickListener: OnClickListener?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cards_list_item, parent, false)
        return ViewHolderClass(itemView)
    }
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener

    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList?.get(position)
        holder.rvInput.text = currentItem?.input
        holder.rvResult.text = currentItem?.result
        holder.rvMenu.setOnMenuItemClickListener{ menuItem ->
            when (menuItem.itemId) {
                R.id.item_addToFolder_card -> {
                    showFolderSelectionDialog(currentItem)
                    true
                }
                R.id.item_edit_card -> {
                    val dialogView = LayoutInflater.from(context).inflate(R.layout.edit_card_layout, null)
                    MaterialAlertDialogBuilder(context)
                        .setTitle("Editar card")
                        .setView(dialogView)
                        .setNegativeButton("Cancelar") { dialog, which ->
                            //alogView.rem
                            dialog.cancel()
                            dialog.dismiss()
                            // Cierra el diálogo
                            // Respond to negative button press
                        }
                        .setPositiveButton("Guardar cambios") { dialog, which ->
                            // cardDao.editCard
                            /**
                            val editText1 = dialogView.findViewById<EditText>(R.id.editText1)
                            val editText2 = dialogView.findViewById<EditText>(R.id.editText2)
                            val newValue1 = editText1.text.toString()
                            val newValue2 = editText2.text.toString()**/
                            dialog.dismiss()
                        }
                        .create().show()
                    true
                }
                R.id.item_delete_card -> {
                    deleteItem(currentItem)
                    true
                }
                else -> false
            }
        }

        holder.rvInput.setOnClickListener{
            if(onClickListener!= null){
                if (currentItem != null) {
                    onClickListener!!.onClick(position, currentItem)
                }
            }
        }

    }
    override fun getItemCount(): Int {
        return dataList!!.size
    }
    //obtiene los elementos de la vista
    class ViewHolderClass(itemView: View):RecyclerView.ViewHolder(itemView){
        val rvResult :TextView = itemView.findViewById(R.id.result_card)
        val rvInput: TextView = itemView.findViewById(R.id.input_card)
        val rvMenu: MaterialToolbar = itemView.findViewById(R.id.toolbar_card)
    }
    interface OnClickListener {
        fun onClick(position: Int, model: Card)
    }
    //métodos de RV
    private fun deleteItem(item: Card?) {
        var folderDao = FolderDao()
        //eliminar de bd
        val cardDao = CardDao()
        val userId = cardDao.getUserIdSharedPreferences(context)
        //refactorizar
        val  firebaseDatabase = FirebaseDatabase.getInstance()
        //obtengo la referencia hasta el id de la card
        val  databaseReference :DatabaseReference = firebaseDatabase.reference.child("user").child(
            userId!!
        ).child("cards").child(item!!.id)
        //obtengo id y referencia para eliminar en bd
        cardDao.deleteCard( databaseReference)
        folderDao.deleteCardId(firebaseDatabase.reference, userId,item.id)
        //esto lo elimina de la lista
        val position = dataList?.indexOf(item)
        if (position != null && position != -1) {
            dataList?.removeAt(position)
            notifyItemRemoved(position)
        }
    }
    private fun showFolderSelectionDialog( currentItem: Card?) {
        //obtener una lista de base de datos
        val folderdao = FolderDao()
        val id =  folderdao.getUserIdSharedPreferences(context)!!
        // folderdao.getAllFolderTitles() pasar a array creo y pasarle ARGS
        val  firebaseDatabase = FirebaseDatabase.getInstance()
        //para nueva función que admite callback
        folderdao.getFoldersByUser(object : FirebaseCallback {

            override fun onCallback(cardList: ArrayList<Card>) {

            }
            //llamo al callback y efectúo resto
            override fun onFolderCallback(folderList: ArrayList<Folder>) {
                val folderTitleList :ArrayList<String> = arrayListOf()
                for (folder in folderList ){
                    folderTitleList.add(folder.dataTitle)
                }

                //resto de código:
                val  folders = arrayOfNulls<CharSequence>(folderTitleList.size)
                folderTitleList.forEachIndexed { index, title ->
                    folders[index] = title
                }
                var selectedItem = -1 // Elemento seleccionado inicialmente
                //configurar el texto de addfolder
                val dialogView = LayoutInflater.from(context).inflate(R.layout.add_folder_layout, null)
                val textNoFolders  = dialogView.findViewById<TextView>(R.id.txtMessageNoFolders)
                val name = dialogView.findViewById<TextInputLayout>(R.id.inputLayoutAddFolder)
                var selectedFolderText = ""
                if(folderTitleList.isEmpty()){
                    textNoFolders.text = context.getString(R.string.vaya_todav_a_no_tienes_ning_na_carpeta)
                    //modifico la longitud del array ya que no tiene contenido

                }else{
                    textNoFolders.text = ""
                }
                MaterialAlertDialogBuilder(context)
                    .setTitle("Añadir a carpeta")
                    .setView(dialogView)

                    .setSingleChoiceItems(folders, selectedItem) { dialog, which ->
                        selectedItem = which
                        selectedFolderText = folders[which].toString()
                        //si folder existe
                        val nombre = selectedItem.toString()
                        val currentId =  currentItem!!.id
                    }
                        /**
                    .setPositiveButton("Guardar cambios") { dialog, which ->

                        // Handle positive button click
                        if (selectedItem != -1 && name.editText!!.text.isEmpty() ) {
                            // Seleccionar carpeta y añadir a folder EXISTENTE
                            var selectedFolder = folders[selectedItem]
                            var selectedFolderId :String  = ""
                            for (folder in folderList ){
                                if(folder.dataTitle == selectedFolderText){
                                    selectedFolderId = folder.id
                                    //se añade a la lista de folders ya creados
                                    folderdao.addNewCardIdValue(firebaseDatabase.reference,id, currentItem!!.id,selectedFolderId, context)
                                    dialog.cancel()
                                    dialog.dismiss()
                                }
                            }
                           // Toast.makeText(context, "Se ha añadido la tarjeta", Toast.LENGTH_SHORT).show()
                        }else if(name.editText!!.text.isNotEmpty() && !name.editText!!.isActivated && !name.editText!!.isSelected){
                            val listToAdd  = ArrayList<String>() //lista para el folder NUEVO
                            listToAdd.add(currentItem!!.id)
                            //add new folder
                            val folderToAdd = Folder("","false", name.editText!!.text.toString(), listToAdd)
                            folderdao.addFolder(firebaseDatabase.reference, id,folderToAdd, context)
                            //seleccionar carpeta nueva
                            textNoFolders.text =""
                            dialog.cancel()
                            dialog.dismiss()

                        }else{
                            //si no se ha seleccionado una carpeta ni introducido un nombre
                            Toast.makeText(context, "No has introducido un nombre", Toast.LENGTH_SHORT).show()

                        }
                    }**/
                    .setPositiveButton("Guardar cambios") { dialog, which ->
                        // Handle positive button click
                        if (selectedItem != -1) {
                            val selectedFolder = folders[selectedItem].toString()
                            val selectedFolderId = folderList[selectedItem].id
                            folderdao.addNewCardIdValue(firebaseDatabase.reference, id, currentItem!!.id, selectedFolderId, context)
                            //se supone que dialog.dismiss()

                        } else if (name.editText!!.text.isNotEmpty() && !name.editText!!.isActivated && !name.editText!!.isSelected) {
                            val listToAdd = ArrayList<String>()
                            listToAdd.add(currentItem!!.id)
                            val folderToAdd = Folder("", "false", name.editText!!.text.toString(), listToAdd)
                            folderdao.addFolder(firebaseDatabase.reference, id, folderToAdd, context)
                            textNoFolders.text = ""


                        } else {
                            Toast.makeText(context, "No has seleccionado una carpeta ni introducido un nombre", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Cancelar") { dialog, which ->
                        dialog.cancel()
                    }
                    .show()
            }
        },firebaseDatabase.reference,id)
      //  val folderTitles = folderdao.getAllFolderTitles(firebaseDatabase.reference, id).toTypedArray()

        // Lista de carpetas

    }

}
