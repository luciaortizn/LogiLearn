package com.example.logilearnapp.ui.favorites

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.logilearnapp.R
import com.example.logilearnapp.data.UserData
import com.example.logilearnapp.data.Label
import com.example.logilearnapp.database.FirebaseCallback
import com.example.logilearnapp.repository.FolderDao
import com.example.logilearnapp.ui.card.Card
import com.example.logilearnapp.ui.folder.Folder
import com.example.logilearnapp.ui.folder.FolderAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FavoritesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavoritesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var  recyclerView: RecyclerView

    lateinit var layoutNoFavoriteFolders: LinearLayout
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FavoritesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavoritesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutNoFavoriteFolders = view.findViewById(R.id.noFavoriteFoldersLayout)
        recyclerView = view.findViewById(R.id.recycler_view_favorites)
        val numCol = 1
        val layoutManager = GridLayoutManager(context, numCol)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        //configurar rv
        val firebaseDatabase = FirebaseDatabase.getInstance()
        var folderAdapter :FolderAdapter
        val folders = FolderDao()
        val userId = folders.getUserIdSharedPreferences(requireContext())
        activity?.runOnUiThread {
            folders.getFoldersByUser(object : FirebaseCallback {
                override fun onCallback(cardList: ArrayList<Card>) {
                    //no hago nada
                }

                override fun onLabelNameCallback(cardList: ArrayList<Label>) {
                }
                override fun onSingleUserCallback(user: UserData) {
                }

                @SuppressLint("NotifyDataSetChanged")
                override fun onFolderCallback(folderList: ArrayList<Folder>) {
                    folderList.removeIf { folder -> folder.isFavorite == "false" }
                    //Log.d("verificar ", "${folderList.size} ")
                    //set background for empty folders
                    val context = context
                    if (context != null) {
                        setBackgroundForEmptyFolders(folderList)
                        folderAdapter = FolderAdapter(folderList,context)
                        //manejo de favoritos
                        folderAdapter.setImageClickListener(object: FolderAdapter.OnImageClickListener{
                            override fun onImageClick(position: Int, view: View) {
                                //cambia de imagen y por tanto también de la lista favoritos
                                cambiarImagen(position, folderList, folders, userId!!, context, firebaseDatabase.reference)
                                //reinicia el fragmento
                                //requireActivity().recreate()
                            }
                        } )

                        recyclerView.adapter = folderAdapter
                       recyclerView.adapter?.notifyDataSetChanged()
                    }
                }
            },firebaseDatabase.reference, userId!!)
        }

    }
    fun setBackgroundForEmptyFolders(list:ArrayList<Folder>?){
        if(list!!.isEmpty()){
            layoutNoFavoriteFolders.visibility = LinearLayout.VISIBLE

        }else{
            layoutNoFavoriteFolders.visibility = LinearLayout.GONE

        }
    }
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
        folderList[position] = Folder("" , isFav, folder.dataTitle,folder.cardId )
        folderDao.updateIsFavorite( databaseReference,userId ,isFav,folder.id,context )
        // Notifica al adaptador sobre el cambio
        recyclerView.adapter?.notifyItemChanged(position)
    }
}