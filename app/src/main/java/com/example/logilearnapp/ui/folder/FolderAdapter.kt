package com.example.logilearnapp.ui.folder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.logilearnapp.R

class FolderAdapter(private val dataList:ArrayList<Folder>):RecyclerView.Adapter<FolderAdapter.ViewHolderClass>() {
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
        holder.rvImage.setImageResource(currentItem.dataImage)
        holder.rvTitle.text = currentItem.dataTitle

        holder.rvImage.setOnClickListener{
                view -> imageClickListener?.onImageClick(position,view)

        }

    }
    class ViewHolderClass(itemView: View):RecyclerView.ViewHolder(itemView){
        val rvImage:ImageView = itemView.findViewById(R.id.imgFolder)
        val rvTitle:TextView = itemView.findViewById(R.id.titleFolder)


    }
}