package com.example.logilearnapp.ui.folder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.logilearnapp.R
import com.example.logilearnapp.database.CardDao
import com.example.logilearnapp.database.FolderDao
import com.example.logilearnapp.ui.card.Card
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FolderAdapter(private val dataList:ArrayList<Folder>, private val context: Context):RecyclerView.Adapter<FolderAdapter.ViewHolderClass>() {
   //para poder manejar los clicks
    interface OnImageClickListener{

        fun onImageClick(position: Int, view: View)

    }
    //referencia al listener
    private var imageClickListener: OnImageClickListener? = null
    //establecer el listener
    fun setImageClickListener(listener: OnImageClickListener){
        this.imageClickListener = listener
    }

//1
    /**
    override fun onBindViewHolder(
        holder: ViewHolderClass,
        position: Int,
        payloads: MutableList<Any>
    ) {
        //se configura la imagen y titulo en viewHolder
       val folder = dataList[position]
        holder.rvTitle.text = folder.dataTitle
        holder.rvImage.setImageResource(folder.dataImage)

        holder.rvImage.setOnClickListener{
        view -> imageClickListener?.onImageClick(position,view)
        }

        super.onBindViewHolder(holder, position, payloads)
    }**/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.folder_list_item, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
    //2
    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]

        if(currentItem.isFavorite == "true"){
            holder.rvImage.setImageResource( R.drawable.baseline_favorite_24)
        }else if (currentItem.isFavorite == "false"){
            holder.rvImage.setImageResource( R.drawable.baseline_favorite_border_24)
        }

        holder.rvTitle.text = currentItem.dataTitle

        holder.rvImage.setOnClickListener{
                view -> imageClickListener?.onImageClick(position,view)

        }

        holder.rvMenu.setOnMenuItemClickListener { menuItem ->
            val dialogView = LayoutInflater.from(context).inflate(R.layout.edit_folder_layout, null)
            when (menuItem.itemId) {
                R.id.item_edit_folder -> {
                    showEditFolderDialog("Editar Carpeta",dialogView, "Guardar cambios", "Cancelar" ,currentItem)
                    true
                }
                R.id.item_delete_folder -> {
                    deleteFolder(currentItem)
                    true
                }
                else -> false
            }
        }
    }
    class ViewHolderClass(itemView: View):RecyclerView.ViewHolder(itemView){
        val rvImage:ImageView = itemView.findViewById(R.id.imgFolder)
        val rvTitle:TextView = itemView.findViewById(R.id.titleFolder)
        val rvMenu: MaterialToolbar = itemView.findViewById(R.id.toolbar_folder)
    }
    private fun deleteFolder(item: Folder) {
        //eliminar de bd
        val folderDao = FolderDao()
        //refactorizar
        val  firebaseDatabase = FirebaseDatabase.getInstance()
        //obtengo la referencia hasta el id de la card
        val  databaseReference : DatabaseReference = firebaseDatabase.reference.child("user").child(
            folderDao.getUserIdSharedPreferences(context)!!
        ).child("folders").child(item.id)
        //obtengo id y referencia para eliminar en bd, la referencia es directa al id
        folderDao.deleteFolder(databaseReference)
        //esto lo elimina de la lista
        val position = dataList.indexOf(item)
        if (position != -1) {
            dataList.removeAt(position)
            notifyItemRemoved(position)
        }
    }
    private fun showEditFolderDialog(title:String, dialogView:View,positiveButton:String,negativeButton:String, currentItem: Folder){
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setView(dialogView)
            .setPositiveButton(positiveButton) { dialog, which ->
                dialog.cancel()
                dialog.dismiss()
            }
            .setNegativeButton(negativeButton){ dialog, which ->
                dialog.cancel()
                dialog.dismiss()

            }.create().show()

    }
}