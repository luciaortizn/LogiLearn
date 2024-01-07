package com.example.logilearnapp.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.logilearnapp.R
import com.example.logilearnapp.databinding.CardListItemBinding
import com.example.logilearnapp.model.Card
import com.example.logilearnapp.view.MyItemRecyclerViewAdapter

class CardAdapter(private val cards: List<Card>): RecyclerView.Adapter<CardAdapter.CardViewHolder>(){


    //crear una vista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        //attach to route:false , si queremos que al inflar la vista se añada como un hijo de la vista padre o no-> no pq es trabajo del RV
        //también se puede hacer con binding
       val binding  = CardListItemBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return CardViewHolder(binding)
    }


    //devuelve el nº elementos que tiene el adapter
    override fun getItemCount(): Int {
        return cards.size
    }


    //actualizar una vista
    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(cards[position])
    }
    class CardViewHolder(binding : CardListItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(card:Card){
            //asignar a cada vista del card item, cargo aquí los elementos

        }
    }
}