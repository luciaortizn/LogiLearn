package com.example.logilearnapp.ui.common

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.logilearnapp.R
import com.example.logilearnapp.data.Word

class WordAdapter(context: Context, words: List<Word>) : ArrayAdapter<Word>(context, 0, words) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val word = getItem(position)
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_word, parent, false)
        val textView = view.findViewById<TextView>(R.id.wordTextView)
        val translationView = view.findViewById<TextView>(R.id.translationTextView)
        textView.text = word?.word
        translationView.text = word?.translation
        Log.d("palabra", "${word?.word} y  ${word?.translation}")
        return view
    }
}
