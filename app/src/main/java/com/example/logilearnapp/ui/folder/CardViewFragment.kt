package com.example.logilearnapp.ui.folder

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.logilearnapp.R
import com.example.logilearnapp.data.Folder
import com.example.logilearnapp.data.UserData
import com.example.logilearnapp.data.Label
import com.example.logilearnapp.database.FirebaseCallback
import com.example.logilearnapp.repository.FolderDao
import com.example.logilearnapp.ui.card.Card
import com.example.logilearnapp.util.Validator
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CardViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
private lateinit var  recyclerView: RecyclerView



class CardViewFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var layoutNoFolders: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_folder_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        layoutNoFolders = view.findViewById(R.id.layout_no_folders)
        var folderAdapter :FolderAdapter
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val folders = FolderDao()
        val userId = folders.getUserIdSharedPreferences(requireContext())

       activity?.runOnUiThread {
           folders.getFoldersByUser(object : FirebaseCallback {
               override fun onCallback(cardList: ArrayList<Card>) {
               }

               override fun onLabelNameCallback(cardList: ArrayList<Label>) {
               }

               override fun onSingleUserCallback(user: UserData) {
               }

               @SuppressLint("NotifyDataSetChanged")
               override fun onFolderCallback(folderList: ArrayList<Folder>) {
                 //  setBackgroundForEmptyFolders(folderList)
                   Log.d("foldernum",folderList.size.toString())
                   folderAdapter = FolderAdapter(folderList,context!!)
                   //chips
                   folders.getLabels(object : FirebaseCallback {
                       override fun onLabelNameCallback(labels: ArrayList<Label>) {

                           val chipsContainer = view.findViewById<ChipGroup>(R.id.chip_group)
                           chipsContainer.setPadding(10,0,10,0)
                           val chipList = mutableListOf<Chip>()
                           val handler = Handler(Looper.getMainLooper())
                           var isLongPress = false
                           // Itera sobre los nombres de las etiquetas y crea un Chip para cada uno
                           for (labelChildren in labels) {
                               var chip = Chip(context)
                               chip.text = labelChildren.name
                               chip.isClickable = true
                               chip.isCheckable = true
                               val context = context
                               chipList.add(chip)

                               chip.setOnLongClickListener{
                               isLongPress = true
                               handler.postDelayed({
                                   if (isLongPress) {
                                           val dialogView = LayoutInflater.from(context).inflate(R.layout.edit_folder_layout, null)
                                           val dialogTitle = dialogView.findViewById<TextInputLayout>(R.id.title_folder_edit)
                                            val folderDao = FolderDao()
                                           dialogTitle.editText!!.addTextChangedListener(object :
                                               TextWatcher {
                                               override fun afterTextChanged(s: Editable?) {
                                                   val name = s.toString()
                                                   if (!Validator.isValidLabelName(name)) {
                                                       dialogTitle.error = "No es válido"
                                                   }else {
                                                       dialogTitle.error = null
                                                   }
                                               }

                                               override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                                               override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                                           })

                                           dialogTitle.editText!!.setText(chip.text.toString())
                                           MaterialAlertDialogBuilder(requireContext())
                                               .setTitle("Editar Etiqueta")
                                               .setView(dialogView)
                                               .setPositiveButton("Guardar") { dialog, _ ->
                                                   if(dialogTitle.error.isNullOrBlank()){
                                                       folderDao.updateLabel(firebaseDatabase.reference, userId!!, chip.text.toString(),dialogTitle.editText!!.text.toString(), requireContext() )
                                                       dialog.cancel()
                                                       dialog.dismiss()
                                                   }
                                               }.show()
                                       }
                                   }, 500)
                                   true
                               }

                               chip.setOnCheckedChangeListener { buttonView, isChecked ->
                                   // Cambiar el color de fondo del chip cuando se seleccione
                                   if (isChecked) {
                                       for (otherChip in chipList) {
                                           if (otherChip != chip) {
                                               otherChip.isChecked = false
                                           }
                                       }
                                       var tempFolderList = ArrayList<Folder>()
                                       chip.setChipBackgroundColorResource(R.color.blue_light)
                                       chip.setTextColor(resources.getColor(R.color.black))
                                       for(folderIdItem in  labelChildren.folderIds){
                                           for(folderItem in folderList) {
                                               if(folderIdItem == folderItem.id){
                                                   tempFolderList.add(folderItem)
                                               }
                                           }

                                       }
                                       setBackgroundForEmptyFolders(tempFolderList)
                                       folderAdapter = FolderAdapter(tempFolderList,context!!)
                                       //manejo de favoritos
                                       folderAdapter.setImageClickListener(object: FolderAdapter.OnImageClickListener{
                                           override fun onImageClick(position: Int, view: View) {
                                               //cambia de imagen y por tanto también de la lista favoritos
                                               cambiarImagen(position, tempFolderList, folders, userId!!, context, firebaseDatabase.reference)
                                               //reinicia el fragmento
                                               // requireActivity().recreate()
                                           }
                                       } )

                                       recyclerView.adapter = folderAdapter
                                       recyclerView.adapter?.notifyDataSetChanged()



                                   } else {
                                       chip.setTextColor(resources.getColor(R.color.black))
                                       chip.setChipBackgroundColorResource(com.firebase.ui.auth.R.color.fui_transparent)
                                       //set background for empty folders
                                       if (context != null) {
                                           setBackgroundForEmptyFolders(folderList)
                                           folderAdapter = FolderAdapter(folderList,context)
                                           //manejo de favoritos
                                           folderAdapter.setImageClickListener(object: FolderAdapter.OnImageClickListener{
                                               override fun onImageClick(position: Int, view: View) {
                                                   //cambia de imagen y por tanto también de la lista favoritos
                                                   cambiarImagen(position, folderList, folders, userId!!, context, firebaseDatabase.reference)
                                                   //reinicia el fragmento
                                                   // requireActivity().recreate()
                                               }
                                           } )

                                           recyclerView.adapter = folderAdapter
                                           recyclerView.adapter?.notifyDataSetChanged()
                                       }
                                   }
                               }
                               if (context != null) {
                                   setBackgroundForEmptyFolders(folderList)
                                   folderAdapter = FolderAdapter(folderList,context)
                                   //manejo de favoritos
                                   folderAdapter.setImageClickListener(object: FolderAdapter.OnImageClickListener{
                                       override fun onImageClick(position: Int, view: View) {
                                           //cambia de imagen y por tanto también de la lista favoritos
                                           cambiarImagen(position, folderList, folders, userId!!, context, firebaseDatabase.reference)
                                           //reinicia el fragmento
                                           // requireActivity().recreate()
                                       }
                                   } )

                                   recyclerView.adapter = folderAdapter
                                   recyclerView.adapter?.notifyDataSetChanged()
                               }
                               chipsContainer.addView(chip)
                           }

                       }
                       override fun onCallback(cardList: ArrayList<Card>) {
                       }
                       override fun onSingleUserCallback(user: UserData) {
                       }
                       override fun onFolderCallback(folderList: ArrayList<Folder>) {
                       }

                       override fun onUsersCallback(userList: ArrayList<UserData>) {

                       }
                   }, firebaseDatabase.reference, userId!!, requireContext())

                   recyclerView.adapter = folderAdapter
                   recyclerView.adapter?.notifyDataSetChanged()

               }
               override fun onUsersCallback(userList: ArrayList<UserData>) {

               }
           },firebaseDatabase.reference, userId!!)
       }

    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CardView.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CardViewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    //Se obtienen los datos que se van a mostrar en el recyclerview y recorren y añaden a la lista que tiene un array para cada elemento
   /**
    private fun getData() {
        for (i in imageList.indices){
            val folder= Folder(idFolderList[i],imageList[i] , titleList[i], idCardList[i])
            folderList.add(folder)
        }
        recyclerView.adapter = FolderAdapter(folderList, requireContext())
    }*/
    private fun cambiarImagen(position: Int, folderList :ArrayList<Folder>, folderDao: FolderDao, userId:String, context: Context, databaseReference: DatabaseReference, ) {
        val folder = folderList[position]
        var isFav = folder.isFavorite
        val nuevaImagen = if (isFav == "false") {
            R.drawable.baseline_favorite_24
           isFav = "true"
        } else {
            R.drawable.baseline_favorite_border_24
           isFav = "false"
        }
        // Actualiza la lista con la nueva imagen
        folderList[position] = Folder(folder.id, isFav, folder.dataTitle,folder.cardId )
       //   (databaseReference: DatabaseReference, userId: String, newIsFavoriteValue: String, folderId: String, context: Context) {
       Log.d("folder", "id: ${folder.id.toString()} solo id")
       if(folder.id.toString().isNotBlank()){
           folderDao.updateIsFavorite( databaseReference,userId ,isFav,folder.id.toString(),context )
           recyclerView.post {
               recyclerView.adapter?.notifyItemChanged(position)
           }
       }
    }
    private fun setBackgroundForEmptyFolders(list:ArrayList<Folder>?){

        if(list!!.isEmpty()){
          layoutNoFolders.visibility = LinearLayout.VISIBLE

        }else{
            layoutNoFolders.visibility = LinearLayout.GONE
        }

    }
}
