package com.example.logilearnapp.ui.folder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.logilearnapp.R

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
private lateinit var folderList:ArrayList<Folder>
lateinit var imageList:Array<String>
lateinit var titleList:Array<String>
lateinit var idCardList:ArrayList<ArrayList<String>>
lateinit var idFolderList:Array<String>


class CardViewFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        //  val binding = ActivityMainBinding.inflate(layoutInflater)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root  =  inflater.inflate(R.layout.fragment_folder_view, container, false)

        return root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageList = arrayOf("true", "false", "true", "false" , "true")

         idCardList  = arrayListOf(
            arrayListOf("", "", "", "", ""),
            arrayListOf("", "", "", "", ""),
            arrayListOf("", "", "", "", ""),
            arrayListOf("", "", "", "", ""),
            arrayListOf("", "", "", "", "")
        )

        idFolderList = arrayOf(
            "", "", "", "", ""
        )
        titleList = arrayOf(
            "Verbs",
            "Synonyms",
            "Vocab1",
            "Phrasal verbs",
            "Vocab2"
        )
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        folderList = arrayListOf<Folder>()
        getData()


        val adapter = FolderAdapter(folderList, requireContext())
        adapter.setImageClickListener(object: FolderAdapter.OnImageClickListener{
            override fun onImageClick(position: Int, view: View) {
                //cambia de imagen y por tanto también de la lista favoritos
                Toast.makeText(context,"Se ha hecho click",Toast.LENGTH_SHORT).show()
                cambiarImagen(position)
                //reinicia el fragmento
                requireActivity().recreate()
            }
        } )


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
    private fun getData() {
        for (i in imageList.indices){
            val folder= Folder(idFolderList[i],imageList[i] , titleList[i], idCardList[i])
            folderList.add(folder)
        }
        recyclerView.adapter = FolderAdapter(folderList, requireContext())
    }
    private fun cambiarImagen(position: Int) {
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
        Toast.makeText(context,"Se ha modificado",Toast.LENGTH_SHORT).show()
        // Notifica al adaptador sobre el cambio
        recyclerView.adapter?.notifyItemChanged(position)
    }
}
