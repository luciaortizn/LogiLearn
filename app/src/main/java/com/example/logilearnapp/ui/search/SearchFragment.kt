package com.example.logilearnapp.ui.search

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.logilearnapp.R
import com.example.logilearnapp.UserData
import com.example.logilearnapp.database.FirebaseCallback
import com.example.logilearnapp.ui.card.Card
import com.example.logilearnapp.ui.card.CardAdapter
import com.example.logilearnapp.ui.folder.Folder
import com.google.android.material.search.SearchBar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class SearchFragment : Fragment() {

    private lateinit var firebaseDatabase: FirebaseDatabase
    //realtime database
    private lateinit var databaseReference: DatabaseReference

    private lateinit var  recyclerView: RecyclerView
    lateinit var titleList:Array<String>
    lateinit var inputList:Array<String>
    lateinit var resultList:Array<String>
    lateinit var layoutNoCards:LinearLayout
    lateinit var sv: SearchView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutNoCards = view.findViewById(R.id.layout_no_cards_displayed)
        recyclerView = view.findViewById(R.id.recycler_view_card)
        sv = view.findViewById(R.id.search_view_cards)
        val numCol = 1
        val layoutManager = GridLayoutManager(context, numCol)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        //get Data (de bd)
        var cardAdapter :CardAdapter
        //obtengo to-do de una y se actualiza la interfaz
        activity?.runOnUiThread {
            getCardsByUser(object : FirebaseCallback {
                @SuppressLint("NotifyDataSetChanged")
                override fun onCallback(cardList: ArrayList<Card>) {
                    //se añaden
                    setBackgroundForEmptyCards(cardList)
                    cardAdapter = CardAdapter(cardList, requireContext())

                    //aquí
                    /**
                    sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            if (query != null) {

                            }
                            return true
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            if (newText.isNullOrBlank()) {
                                for(card in cardList){
                                    if(card.input.contains(newText.toString()) || card.result.contains(newText.toString()) ){

                                    }
                                }
                            }else{

                            }
                            return true
                        }

                    })**/

                    recyclerView.adapter = cardAdapter
                    recyclerView.adapter?.notifyDataSetChanged()
                    /**
                    var cardAdapter = CardAdapter(emptyList(), requireContext())
                    recyclerView.adapter = cardAdapter

                    setupSearch(cardAdapter)

                    // Fetch data from the database and update the UI
                    fetchAllCards(cardAdapter)
**/
                }

                override fun onSingleUserCallback(user: UserData) {
                }

                override fun onFolderCallback(folderList: ArrayList<Folder>) {
                }

            })

        }

    }
    //obtiene lista cards por usuario y usa shared preferences para obtener el id

    private fun getCardsByUser(callback: FirebaseCallback) {
        val cardListDB: ArrayList<Card> = arrayListOf()
        firebaseDatabase = FirebaseDatabase.getInstance()
        val sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("id", "")
        databaseReference = firebaseDatabase.reference.child("user").child(userId.toString()).child("cards")
        //obtengo las cards
        databaseReference.addValueEventListener(object: ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                //garantiza que no se añadan los elementos anteriores cuando hay una modificación
                cardListDB.clear()
                for (card in snapshot.children){
                    val id = card.child("id").getValue(String::class.java)
                    val input = card.child("input").getValue(String::class.java)
                    val result = card.child("result").getValue(String::class.java)
                    cardListDB.add(Card(id.toString(), input.toString(), result.toString()))

                }
                callback.onCallback(cardListDB)
                recyclerView.adapter?.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun setBackgroundForEmptyCards(list:ArrayList<Card>?){

        if(list!!.isEmpty()){
            layoutNoCards.visibility = LinearLayout.VISIBLE

        }else{
            layoutNoCards.visibility = LinearLayout.GONE

        }
        //ver que pasa y ver si esto se mete o no en su campo del rv
        for(card in list){
            val cardInput = card.input
            //no hago nada para folder de momento

        }
    }
/**
    //buscador
    private fun setupSearch(cardAdapter: CardAdapter) {
        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    performSearch(query, cardAdapter)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    fetchAllCards(cardAdapter) // Reload all cards when search is cleared
                } else {
                    performSearch(newText, cardAdapter)
                }
                return true
            }
        })
    }


    private fun performSearch(query: String, cardAdapter: CardAdapter) {
        activity?.runOnUiThread {
            getFilteredCardsByUser(query, object : FirebaseCallback {
                @SuppressLint("NotifyDataSetChanged")
                override fun onCallback(cardList: ArrayList<Card>) {
                    setBackgroundForEmptyCards(cardList)
                    cardAdapter.updateCards(cardList)
                }

                override fun onSingleUserCallback(user: UserData) {}

                override fun onFolderCallback(folderList: ArrayList<Folder>) {}
            })
        }
    }

    private fun fetchAllCards(cardAdapter: CardAdapter) {
        activity?.runOnUiThread {
            getCardsByUser(object : FirebaseCallback {
                @SuppressLint("NotifyDataSetChanged")
                override fun onCallback(cardList: ArrayList<Card>) {
                    setBackgroundForEmptyCards(cardList)
                    cardAdapter.updateCards(cardList)
                }

                override fun onSingleUserCallback(user: UserData) {}

                override fun onFolderCallback(folderList: ArrayList<Folder>) {}
            })
        }


    }*/

}
