package com.example.logilearnapp.ui.folder

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.example.logilearnapp.R
import com.example.logilearnapp.data.Difficulty
import com.example.logilearnapp.repository.FolderDao
import com.example.logilearnapp.ui.study.StudyFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FolderAdapter(private val dataList:ArrayList<Folder>, private val context: Context):RecyclerView.Adapter<FolderAdapter.ViewHolderClass>() {
   //para poder manejar los clicks
    interface OnImageClickListener{
        fun onImageClick(position: Int, view: View)
    }

    //referencia al listener
    private var imageClickListener: OnImageClickListener? = null
    private val folderDao = FolderDao()

    // TODO: empezar modo estudio en el adapter:
    /*para el modo estudio:

    * */
    //establecer el listener
    fun setImageClickListener(listener: OnImageClickListener){
        this.imageClickListener = listener
    }

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

        setCardDifficulty(currentItem,holder.rvEasyTextView, holder.rvRegularTextView, holder.rvHardTextView)
        holder.rvTitle.setOnClickListener{
            // se cambia de fragmento
            replaceFragment(holder.itemView.context as FragmentActivity, StudyFragment(), currentItem)
        }

        holder.rvImage.setOnClickListener{
            holder.rvImage.isClickable = false
            // Llamar al método del listener de la imagen
            imageClickListener?.onImageClick(position, it)
            // view -> imageClickListener?.onImageClick(position,view)

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
                R.id.item_add_label -> {
                    val labelView = LayoutInflater.from(context).inflate(R.layout.add_label_layout, null)
                    showLabelDialog(folderDao,labelView,"Guardar", "Cancelar", currentItem )
                    true
                }

                else -> false
            }
        }.also {
            // Restablecer el OnClickListener de la imagen después de que se haya cerrado el menú
            (context as AppCompatActivity).registerForContextMenu(holder.rvImage)
        }
    }
    class ViewHolderClass(itemView: View):RecyclerView.ViewHolder(itemView){
        val rvImage:ImageView = itemView.findViewById(R.id.imgFolder)
        val rvTitle:TextView = itemView.findViewById(R.id.titleFolder)
        val rvMenu: MaterialToolbar = itemView.findViewById(R.id.toolbar_folder)
        val rvEasyTextView: TextView = itemView.findViewById(R.id.easy_card_number)
        val rvRegularTextView: TextView = itemView.findViewById(R.id.regular_card_number)
        val rvHardTextView: TextView = itemView.findViewById(R.id.hard_card_number)
    }
    private fun deleteFolder(item: Folder) {
        //eliminar de bd

        //refactorizar
        val  firebaseDatabase = FirebaseDatabase.getInstance()
        //obtengo la referencia hasta el id de la card
        val  databaseReference : DatabaseReference = firebaseDatabase.reference.child("user").child(
            folderDao.getUserIdSharedPreferences(context)!!
        ).child("folders").child(item.id.toString())
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
        val dialogTitle = dialogView.findViewById<TextInputLayout>(R.id.title_folder_edit)
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val userId = folderDao.getUserIdSharedPreferences(context)
        dialogTitle.editText!!.setText(currentItem!!.dataTitle)
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setView(dialogView)
            .setPositiveButton(positiveButton) { dialog, which ->
                val folder = Folder(currentItem.id,currentItem.isFavorite,dialogTitle.editText!!.text.toString(), currentItem.cardId )
                folderDao.updateFolder(firebaseDatabase.reference, userId!!, folder, context)
                dialog.cancel()
                dialog.dismiss()
            }
            .setNegativeButton(negativeButton){ dialog, which ->
                dialog.cancel()
                dialog.dismiss()

            }.create().show()
    }
    fun showLabelDialog(folderDao: FolderDao, dialogView:View, positiveButton:String, negativeButton:String, currentItem: Folder){

        val dialogTitle = dialogView.findViewById<TextInputLayout>(R.id.title_label_add)
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val userId = folderDao.getUserIdSharedPreferences(context)
        dialogTitle.editText!!.setText(currentItem!!.dataTitle)
        MaterialAlertDialogBuilder(context)
            .setTitle("Añadir etiquetas")
            .setView(dialogView)
            .setPositiveButton(positiveButton) { dialog, which ->
                folderDao.addLabel(firebaseDatabase.reference, userId!!,currentItem.id,  dialogTitle.editText!!.text.toString() , context)
                dialog.cancel()
                dialog.dismiss()
            }
            .setNegativeButton(negativeButton){ dialog, which ->
                dialog.cancel()
                dialog.dismiss()

            }.create().show()

    }
    private fun replaceFragment(activity: FragmentActivity, fragment: Fragment, folder:Folder) {
        // Pasa el objeto Folder al fragmento usando un Bundle
        val bundle = Bundle()
        bundle.putParcelable("folder", folder)
        fragment.arguments = bundle

        //necesita el activity actual para poder acceder a su contenido
        val fragmentManager: FragmentManager = activity.supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.viewerFragment, fragment)
        fragmentTransaction.commit()
    }
     private fun setCardDifficulty(currentItem: Folder,rvEasyTextView: TextView, rvRegularTextView: TextView, rvHardTextView : TextView){
        var easyCont=0
        var regularCont=0
        var hardCont = 0
        for (cardsList in currentItem.cardId.orEmpty()) {
            when (cardsList.difficulty) {
                Difficulty.EASY -> {
                    easyCont++
                }
                Difficulty.REGULAR -> {
                    regularCont++
                }
                Difficulty.HARD -> {
                    hardCont++
                }

                else -> {}
            }

        }
        rvEasyTextView.text = easyCont.toString()
        rvRegularTextView.text = regularCont.toString()
        rvHardTextView.text = hardCont.toString()
    }
}