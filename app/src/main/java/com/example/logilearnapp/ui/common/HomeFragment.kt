package com.example.logilearnapp.ui.common

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.logilearnapp.ui.card.EmptyEditableCard
import com.example.logilearnapp.R
import com.example.logilearnapp.util.ConfigUtils
import com.example.logilearnapp.data.Definition
import com.example.logilearnapp.data.TranslateRequest
import com.example.logilearnapp.ui.favorites.FavoritesFragment
import com.example.logilearnapp.ui.profile.ProfileFragment
import com.example.logilearnapp.viewmodel.EmptyEditableCardViewModel
import com.example.logilearnapp.viewmodel.HomeViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.search.SearchBar
import java.util.Objects
import kotlin.math.sqrt


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
private lateinit var  titleInfoText: TextView

private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f
    private lateinit var listView: ListView
    private lateinit var wordAdapter: WordAdapter
    private lateinit var wordViewModel : EmptyEditableCardViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }


    }
     lateinit var rootview: View
    private var isDialogVisible = false
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
        titleInfoText = view.findViewById(R.id.dicTitle)
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
        wordViewModel = ViewModelProvider(this)[EmptyEditableCardViewModel::class.java]

        //set de sensor
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        Objects.requireNonNull(sensorManager)!!
            .registerListener(sensorListener, sensorManager!!
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
        acceleration = 10f
        currentAcceleration = SensorManager.GRAVITY_EARTH
        lastAcceleration = SensorManager.GRAVITY_EARTH


        sv.inflateMenu(R.menu.dictionary_search_menu)

        dictInfoBtn.setOnClickListener(){
            showInfoDialog("Información sobre el diccionario", "Explora una amplia variedad de palabras con nuestro diccionario en inglés y filtros personalizados.\t \nPuedes buscar: \n - Definiciones \n - Sinónimos,\n - Ejemplos \n - Pronunciaciones \nPara  usar el traductor, haz click en el botón '+'. ", "Entendido")
        }

       sv.editText
            .setOnEditorActionListener { v, actionId, event ->
                //hide el título
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
                   examplesText.text = ""
               }
           }

           viewModel.getPronunciations( apiKey, text.trim()){ p ->
              p?.let { pronunciations ->
                   val firstExample: String = pronunciations[0].raw
                  pronunciationText.text = removeBracketsAndContent(firstExample)

               } ?: run {
                  pronunciationText.text = ""
               }
           }
           true
       }

        val fab : ExtendedFloatingActionButton = view.findViewById(R.id.floating_action_button)
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
    //variable cofigurada del sensor
    private val sensorListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            // Fetching x,y,z values
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            lastAcceleration = currentAcceleration

            // Getting current accelerations
            // with the help of fetched x,y,z values

            currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta: Float = currentAcceleration - lastAcceleration
            acceleration = acceleration * 0.9f + delta

            // Display a Toast message if
            // acceleration value is over 12
            if (acceleration > 12 && !isDialogVisible) {
                isDialogVisible = true
                val dialogView = layoutInflater.inflate(R.layout.random_words_layout, null)
                //se muestran las palabras aleatorias, dialog o layout nuevo
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Palabras del día")
                    .setView(dialogView)
                    .setPositiveButton("Ok") { dialog, which ->
                        dialog.dismiss()
                        dialog.cancel()
                        isDialogVisible = false
                    }  .setOnDismissListener {
                                isDialogVisible = false

                    }.create().apply {
                        val listView = dialogView.findViewById<ListView>(R.id.wordList)
                        viewModel.getRandomWords("x9mi4zwbf3wkb7xx2n1pa26t8shz7v8bmtv1ojwnevuanb4dq") { words ->
                            if (words != null) {
                                val wordsSubList = words.subList(0, 2)
                                wordsSubList.forEachIndexed { index, word ->
                                    val requestBody = TranslateRequest(listOf(word.word), "ES")
                                    // Llama a postTranslation con todos los parámetros necesarios
                                    wordViewModel.postTranslation(
                                        requestBody,
                                        { translation ->
                                            // Maneja la traducción recibida
                                            translation?.let { transl ->
                                                if (transl.isNotEmpty() && transl.isNotBlank()) {
                                                    // Actualiza la traducción en la lista original
                                                    words[index].translation = transl
                                                    Log.d("palabra","tx: ${word.translation}" )

                                                } else {
                                                    words[index].translation = "No hay traducción"
                                                }
                                            } ?: run {
                                                words[index].translation = "Error al obtener la traducción"
                                            }

                                            // Notifica al adaptador cuando todas las traducciones estén listas
                                            if (wordsSubList.all { true }) {
                                                wordAdapter = WordAdapter(requireContext(), wordsSubList)
                                                listView.adapter = wordAdapter
                                            }
                                        }, ConfigUtils.getDeeplApiKey(requireContext()).toString())
                                }
                            } else {
                                Toast.makeText(requireContext(), "Error fetching words", Toast.LENGTH_SHORT).show()
                            }
                        }
                        show()
                    }
            }
        }
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    override fun onResume() {
        sensorManager?.registerListener(sensorListener, sensorManager!!.getDefaultSensor(
            Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL
        )
        super.onResume()
    }

    override fun onPause() {
        sensorManager!!.unregisterListener(sensorListener)
        super.onPause()
    }
    override fun onDestroy() {
        isDialogVisible = false
        super.onDestroy()
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
    fun setSensor(){
        // Getting the Sensor Manager instance

    }


}