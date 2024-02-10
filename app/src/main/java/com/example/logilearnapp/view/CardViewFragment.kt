package com.example.logilearnapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.graphics.Color
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.logilearnapp.R
import com.example.logilearnapp.databinding.ActivityMainBinding
import com.example.logilearnapp.model.Card
import com.example.logilearnapp.model.Folder
import com.example.logilearnapp.model.Label

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CardViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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
        val binding = ActivityMainBinding.inflate(layoutInflater)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_folder_view, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(requireContext(),3);
        //se establece el  adaptador aquí.
        //necesitaría hacer una llamada a la base de datos pero bueno
        val folderList = listOf(
            Folder("Folder 1", mutableListOf(Card("Card 1","a"), Card("Card 2","d")), Label("Label 1", Color.Cyan)),
            Folder("Folder 2", mutableListOf(Card("Card 3", "b"), Card("Card 4","e")), Label("Label 2",Color.Gray)),
            Folder("Folder 3", mutableListOf(Card("Card 5","c"), Card("Card 6", "f")), Label("Label 3", Color.Black))
        )
        val adapter = MyItemRecyclerViewAdapter(folderList)
        recyclerView.adapter = adapter
        println("Layout inflated correctly")
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
}