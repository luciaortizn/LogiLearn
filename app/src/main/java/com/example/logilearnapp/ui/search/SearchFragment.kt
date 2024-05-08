package com.example.logilearnapp.ui.search

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.logilearnapp.R
import com.example.logilearnapp.database.FirebaseCallback
import com.example.logilearnapp.ui.card.Card
import com.example.logilearnapp.ui.card.CardAdapter
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
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
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //prueba
        layoutNoCards = view.findViewById(R.id.layout_no_cards_displayed)
        recyclerView = view.findViewById(R.id.recycler_view_card)
        val numCol = 1
        val layoutManager = GridLayoutManager(context, numCol)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        //get Data (de bd)
        val sv: SearchView = view.findViewById(R.id.search_view)
        val sb: SearchBar = view.findViewById(R.id.search_bar)
        var cardAdapter :CardAdapter
        //obtengo to-do de una y se actualiza la interfaz
        activity?.runOnUiThread {
            getCardsByUser(object : FirebaseCallback {
                @SuppressLint("NotifyDataSetChanged")
                override fun onCallback(cardList: ArrayList<Card>) {
                    //se añaden
                    setBackgroundForEmptyCards(cardList)
                    cardAdapter = CardAdapter(cardList, requireContext())
                    //meto to-do el código?
                    //aquí verifico si debo de mostrar el mensaje
                    //com.google.android.material.search.SearchView

                    /**
                    sv.editText.setOnEditorActionListener(object : TextView.OnEditorActionListener{

                        override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                            val text = v?.text.toString()
                            sv.hide()
                            sb.setText(text)
                            if(v?.text.isNullOrBlank()){
                                cardList.clear()
                                //metodo?

                            }else {
                                cardList.clear()
                                val cardAdapte = CardAdapter(cardList, requireContext())
                                cardAdapte.setOnClickListener(object :CardAdapter.OnClickListener {
                                    override fun onClick(position: Int, model: Card) {
                                        // val intent = Intent(context, CardEditView::class.java)
                                        //intent.putExtra("cardID", model.id)
                                        //startActivity(intent)
                                    }
                                })
                                recyclerView.adapter= cardAdapte

                            }
                            return true
                        }

                    })**/
                    recyclerView.adapter = cardAdapter
                    recyclerView.adapter?.notifyDataSetChanged()

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
        //firebaseDatabase.reference.child("user").child("id").equalTo(userId).ref.child("cards")
       //accedes a cards
        databaseReference.addValueEventListener(object: ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                //garantiza que no se añadan los elementos anteriores cuando hay una modificación
                cardListDB.clear()
                for (card in snapshot.children){
                    val id = card.child("id").getValue(String::class.java)
                    val title = card.child("title").getValue(String::class.java)
                    val input = card.child("input").getValue(String::class.java)
                    val result = card.child("result").getValue(String::class.java)

                    cardListDB.add(Card(id.toString(), title.toString(), input.toString(), result.toString()))

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
            val title = card.title
            val cardInput = card.input
            //no hago nada para folder de momento

        }
    }

    fun getData(cardList:ArrayList<Card>?){

        val cardAdapter = CardAdapter(cardList, requireContext())
        recyclerView.adapter= cardAdapter

    }

}