package com.example.logilearnapp.ui.card

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.logilearnapp.R
import com.example.logilearnapp.ui.folder.Folder


/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */

class MyItemRecyclerViewAdapter(private val folderList:List<Folder>):
RecyclerView.Adapter<MyItemRecyclerViewAdapter.FolderViewHolder>(){
    inner class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val folderNameTextView: TextView = itemView.findViewById(R.id.folder_name_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.folder_list_item, parent, false)

        return FolderViewHolder(itemView)

    }

    override fun getItemCount(): Int {

        return folderList.size.toInt()
        /* inner class ViewHolder(binding: FragmentItemBinding) : RecyclerView.ViewHolder(binding.root) {
            val idView: TextView = binding.itemNumber
            val contentView: TextView = binding.content

            override fun toString(): String {
                return super.toString() + " '" + contentView.text + "'"
            }
        * */

    }

    //para cada elemento  en la lista de folder se establece texto en la textView de la vista asociada
    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folderName = folderList[position]
        holder.folderNameTextView.text = folderName.toString()
    }
}


/*

        private val values: List<PlaceholderItem>)
    : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

    return ViewHolder(FragmentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.id
        holder.contentView.text = item.content
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val contentView: TextView = binding.content

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

*/
