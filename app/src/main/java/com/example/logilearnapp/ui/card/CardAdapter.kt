package com.example.logilearnapp.ui.card
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.icu.text.CaseMap.Fold
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.logilearnapp.R
import com.example.logilearnapp.UserData
import com.example.logilearnapp.data.CardWithDifficulty
import com.example.logilearnapp.data.Difficulty
import com.example.logilearnapp.data.Label
import com.example.logilearnapp.database.CardDao
import com.example.logilearnapp.database.FirebaseCallback
import com.example.logilearnapp.database.FolderDao
import com.example.logilearnapp.ui.folder.Folder
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CardAdapter(private var dataList:ArrayList<Card>?, private val context: Context):RecyclerView.Adapter<CardAdapter.ViewHolderClass>() {
   private var onClickListener: OnClickListener?=null
    private var isDialogShowing = false
    private val folderDao = FolderDao()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cards_list_item, parent, false)
        return ViewHolderClass(itemView)

    }
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener

    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val cardDao = CardDao()

        val currentItem = dataList?.get(position)
        holder.rvInput.text = currentItem?.input
        holder.rvResult.text = currentItem?.result
        holder.rvMenu.setOnMenuItemClickListener{ menuItem ->
            when (menuItem.itemId) {
                R.id.item_addToFolder_card -> {
                    holder.rvMenu.menu.close()
                    showFolderSelectionDialog(currentItem)
                    true
                }
                R.id.item_edit_card -> {
                    holder.rvMenu.menu.close()
                    val dialogView = LayoutInflater.from(context).inflate(R.layout.edit_card_layout, null)
                    val dialogInput = dialogView.findViewById<TextInputLayout>(R.id.input_edit)
                    val dialogResult = dialogView.findViewById<TextInputLayout>(R.id.result_edit)
                    val firebaseDatabase = FirebaseDatabase.getInstance()
                    val userId = cardDao.getUserIdSharedPreferences(context)
                    dialogInput.editText!!.setText(currentItem!!.input)
                    dialogResult.editText!!.setText(currentItem.result)
                    MaterialAlertDialogBuilder(context)
                        .setTitle("Editar tarjeta")
                        .setView(dialogView)
                        .setNegativeButton("Cancelar") { dialog, which ->
                            //alogView.rem
                            dialog.cancel()
                            dialog.dismiss()
                            // Cierra el diálogo
                            // Respond to negative button press
                        }
                        .setPositiveButton("Guardar cambios") { dialog, which ->
                            // cardDao.editCar
                            val card = Card(currentItem.id, dialogInput.editText!!.text.toString(), dialogResult.editText!!.text.toString())
                            cardDao.updateCard(firebaseDatabase.reference,card, userId!!, context )
                            dialog.dismiss()
                            holder.rvMenu.menu.close()
                        }
                        .create().show()
                    true
                }
                R.id.item_delete_card -> {
                    holder.rvMenu.menu.close()
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
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: ArrayList<Card>) {
        dataList = newList
        notifyDataSetChanged()
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
        val folderDao = FolderDao()
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
        if(isDialogShowing){
            return
        }

        isDialogShowing = true
        val id =  folderDao.getUserIdSharedPreferences(context)!!
        val  firebaseDatabase = FirebaseDatabase.getInstance()

        folderDao.getFoldersByUser(object : FirebaseCallback {
            override fun onCallback(cardList: ArrayList<Card>) {
            }

            override fun onLabelNameCallback(cardList: ArrayList<Label>) {

            }

            override fun onSingleUserCallback(user: UserData) {

            }

            //llamo al callback y efectúo resto
            override fun onFolderCallback(folderList: ArrayList<Folder>) {
                showAddFolderDialog(currentItem, folderList, id, firebaseDatabase)
            }
        },firebaseDatabase.reference,id)

    }
    fun showAddFolderDialog(currentItem: Card?, folderList: ArrayList<Folder>, id: String, firebaseDatabase: FirebaseDatabase){
        val folderTitleList :ArrayList<String> = arrayListOf()
        for (folder in folderList ){
            folderTitleList.add(folder.dataTitle)
        }

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

        val dialog=  MaterialAlertDialogBuilder(context)

            .setTitle("Añadir a carpeta")
            .setView(dialogView)
            .setSingleChoiceItems(folders, selectedItem) { dialog, which ->
                selectedItem = which

                selectedFolderText = folders[which].toString()
                //si folder existe
            }
            .setPositiveButton("Guardar cambios") { dialog, which ->
                // Handle positive button click
                if (selectedItem != -1) {
                    val selectedFolderId = folderList[selectedItem].id
                    //cardWithDiff
                    val currentCardId =  CardWithDifficulty(currentItem!!.id, Difficulty.EASY, 0)
                    folderDao.addNewCardIdValue(firebaseDatabase.reference, id, currentCardId, selectedFolderId, context)

                } else if (name.editText!!.text.isNotEmpty() && !name.editText!!.isActivated && !name.editText!!.isSelected) {
                    val listToAdd = ArrayList<CardWithDifficulty>()
                    val currentCardId =  CardWithDifficulty(currentItem!!.id, Difficulty.EASY, 0)
                    listToAdd.add(currentCardId)
                    val folderToAdd = Folder("", "false", name.editText!!.text.toString(), listToAdd)
                    if(folderToAdd.dataTitle.toString().isNotEmpty()){
                        folderDao.addFolder(firebaseDatabase.reference, id, folderToAdd, context)
                    }
                    textNoFolders.text = ""

                } else {
                    Toast.makeText(context, "No has seleccionado una carpeta ni introducido un nombre", Toast.LENGTH_SHORT).show()


                }
                isDialogShowing = false
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, which ->
                dialog.dismiss()
                dialog.cancel()
                isDialogShowing = false
            }.create().apply {
                setOnDismissListener {
                    isDialogShowing = false
                }
                show() }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateCards(newCards: ArrayList<Card>) {
        dataList = newCards
        notifyDataSetChanged()
    }
}
