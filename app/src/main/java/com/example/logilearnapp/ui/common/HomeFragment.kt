package com.example.logilearnapp.ui.common

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.logilearnapp.EmptyEditableCard
import com.example.logilearnapp.ui.profile.ProfileFragment
import com.example.logilearnapp.R
import com.example.logilearnapp.data.Definition
import com.example.logilearnapp.ui.favorites.FavoritesFragment
import com.example.logilearnapp.viewmodel.HomeViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.search.SearchBar


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBE
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters

private lateinit var viewModel : HomeViewModel
private lateinit var definitionsText: TextView
private lateinit var examplesText: TextView
private lateinit var relatedWordsText: TextView
private lateinit var pronunciationText: TextView
private val cardMap = mutableMapOf<String, MaterialCardView>()
private lateinit var sv : com.google.android.material.search.SearchView
private lateinit var otherLayout: LinearLayout
private lateinit var dictInfoBtn: MaterialButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }


    }
     lateinit var rootview: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_home, container, false)

        return rootview
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        otherLayout = view.findViewById(R.id.layout_other_content_home)
        dictInfoBtn = view.findViewById(R.id.dictInfo)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        val apiKey = "x9mi4zwbf3wkb7xx2n1pa26t8shz7v8bmtv1ojwnevuanb4dq"
        sv = view.findViewById(R.id.search_view)
        definitionsText= view.findViewById(R.id.definitions_text)
        examplesText = view.findViewById(R.id.examples_text)
        relatedWordsText = view.findViewById(R.id.related_words_text)
        pronunciationText  =view.findViewById(R.id.pronunciation_text)
        cardMap["Definiciones"] = view.findViewById(R.id.definitions)
        cardMap["Sinónimos"] = view.findViewById(R.id.related_words)
        cardMap["Ejemplos"] = view.findViewById(R.id.examples)
        cardMap["Pronunciación"] = view.findViewById(R.id.pronunciation)

        val sb : SearchBar = view.findViewById(R.id.search_bar)

        sv.inflateMenu(R.menu.dictionary_search_menu)

        dictInfoBtn.setOnClickListener(){
            showInfoDialog("Información sobre el diccionario", "Explora una amplia variedad de palabras con nuestro diccionario en inglés y filtros personalizados.\t \nPuedes buscar: \n - Definiciones \n - Sinónimos,\n - Ejemplos \n - Pronunciaciones \nPara  usar el traductor, haz click en el botón '+'. ", "Entendido")
        }

       sv.editText
            .setOnEditorActionListener { v, actionId, event ->
                sb.setText(sv.text)
                sv.hide()
                false
            }
        sv.setOnMenuItemClickListener{menuItem ->
            val title = menuItem.title.toString()
            // Ocultar la tarjeta correspondiente al ítem del menú seleccionado
            cardMap.values.forEach { card ->
                if (cardMap.entries.firstOrNull { it.key == title }?.value == card) {
                    card.visibility = MaterialCardView.VISIBLE
                } else {
                    card.visibility = MaterialCardView.GONE
                }
            }
            return@setOnMenuItemClickListener true
        }
       sv.editText.setOnEditorActionListener { v, actionId, event ->
           val text: String = v?.text.toString();
           sv.setText(text)
           viewModel.getDefinitions( apiKey, text.trim()) { definitions ->
               // Maneja el resultado aquí en el hilo principal
               definitions?.let { definitionsList ->
                   if (definitionsList.isNotEmpty()) {
                       //no es así, hago foreach para la que no tiene atributo text?
                       val firstDefinition: Definition = definitionsList[1]
                       var definitionText = "Type: " + firstDefinition.partOfSpeech  +". \n"
                       if(firstDefinition.text!=null ){
                           definitionText += firstDefinition.text
                       }
                       definitionsText.text =removeBracketsAndContent(definitionText)
                   } else {
                       definitionsText.text = "No se encontraron definiciones"
                   }

               } ?: run {
                   definitionsText.text = "No hay resultados"
               }

           }
           viewModel.getRelatedWords( apiKey, text.trim()) { relatedWords ->
               // Maneja el resultado aquí en el hilo principal
               relatedWords?.let { relatedWordList ->
                   if (relatedWordList.isNotEmpty()) {
                       var allrelated :String = ""
                       for (lists in relatedWordList){
                           when (lists.relationshipType) {
                               "same-context" -> {
                                   allrelated += "\nSame Context: \n"
                                   for (word in lists.words) {
                                       allrelated += "$word, "
                                   }
                               }
                               "synonym" -> {
                                   allrelated += "\nSynonyms: \n"
                                   for (word in lists.words) {
                                       allrelated += "$word, "
                                   }
                               }
                               "has_topic" -> {
                                   allrelated += "\nTopic: \n"
                                   for (word in lists.words) {
                                       allrelated += "$word, "
                                   }
                               }
                               else -> {
                               }
                           }
                       }
                       relatedWordsText.text = allrelated

                   } else {
                       relatedWordsText.text = "No se encontraron"
                   }

               } ?: run {
                   relatedWordsText.text = " "
               }
           }
           viewModel.getExamples( apiKey, text.trim()){ examplesObject ->
               examplesObject?.let { ex ->
                   val firstExample: String = ex.examples[1].text
                   examplesText.text = removeBracketsAndContent(firstExample)

               } ?: run {
                   examplesText.text = "Error"
               }
           }

           viewModel.getPronunciations( apiKey, text.trim()){ p ->
              p?.let { pronunciations ->
                   val firstExample: String = pronunciations[0].raw
                  pronunciationText.text = removeBracketsAndContent(firstExample)

               } ?: run {
                  pronunciationText.text = "Error"
               }
           }
           true
       }

        val fab : FloatingActionButton = view.findViewById(R.id.floating_action_button)
        fab.setOnClickListener {
            val intent = Intent(activity, EmptyEditableCard::class.java)

            startActivity(intent)
        }
        //navegación de topmenu
        val topBar: MaterialToolbar =rootview.findViewById<MaterialToolbar>(R.id.toolbar)
        topBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
            R.id.navigation_favorites -> {
               replaceFragment(requireActivity(), FavoritesFragment())
                true
            }
            R.id.navigation_profile -> {
               replaceFragment(requireActivity(), ProfileFragment())
                true
            }
            else -> false
        }
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
    private fun replaceFragment(activity: FragmentActivity, fragment: Fragment) {
        //necesita el activity actual para poder acceder a su contenido
        val fragmentManager: FragmentManager = activity.supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.viewerFragment, fragment)
        fragmentTransaction.commit()
    }
    private fun removeBracketsAndContent(input: String): String {
        // Utilizamos una expresión regular para encontrar y reemplazar el contenido entre <>
        val pattern = Regex("<[^>]*>")
        return input.replace(pattern, "") // Reemplazamos el contenido entre <> con una cadena vacía
    }
    private fun showInfoDialog(title:String,message:String,positiveButton:String ){
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButton) { dialog, which ->
                dialog.cancel()
                dialog.dismiss()

            }.create().show()



    }

}