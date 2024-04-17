package com.example.logilearnapp.ui.common

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.logilearnapp.EmptyEditableCard
import com.example.logilearnapp.ProfileFragment
import com.example.logilearnapp.R
import com.example.logilearnapp.data.Definition
import com.example.logilearnapp.data.WordOfTheDayResponse
import com.example.logilearnapp.ui.favorites.FavoritesFragment
import com.example.logilearnapp.viewmodel.HomeViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.Duration

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
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
private lateinit var result: EditText
private lateinit var input: EditText

private lateinit var definitionButton : Button
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
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        result = view.findViewById(R.id.Result)
        definitionButton = view.findViewById(R.id.Definition_button)
        input = view.findViewById(R.id.Input)
        //mover de sitio el api key
        val apiKey = "x9mi4zwbf3wkb7xx2n1pa26t8shz7v8bmtv1ojwnevuanb4dq";

        definitionButton.setOnClickListener() {
            //Toast.makeText(requireContext(),"has pulsado", Toast.LENGTH_SHORT).show();


            viewModel.getDefinitions( apiKey,  input.text.toString().trim()) { definitions ->
                // Maneja el resultado aquí en el hilo principal
                definitions?.let { definitionsList ->


                    Log.d("API RESPONSE", definitionsList.toString())

                    if (definitionsList.isNotEmpty()) {
                        val firstDefinition: Definition = definitionsList[0]
                        val definitionText = firstDefinition.text
                        result.setText(definitionText)
                    } else {
                        result.setText("No se encontraron definiciones")
                    }

                } ?: run {
                    result.setText("Error al obtener la definición")
                }

            }
            viewModel.getRandomWord(apiKey){ wordOfTheDayResponse ->
               wordOfTheDayResponse?.let{obj ->
                   Toast.makeText(requireContext(), obj.word, Toast.LENGTH_SHORT).show()


               }

            }

        }

        val fab : FloatingActionButton = view.findViewById(R.id.floating_action_button)
        fab.setOnClickListener {
            // Respond to FAB click
            //abre una ventana emergente CardCreate
            val intent = Intent(activity, EmptyEditableCard::class.java)
            // Iniciar la otra actividad
            startActivity(intent)
        }
        //navegación de topmenu
        val topBar: MaterialToolbar =rootview.findViewById<MaterialToolbar>(R.id.toolbar)
        topBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
            R.id.navigation_favorites -> {
                //requireContext() en fragments

                Toast.makeText(requireContext(), "favoritos", Toast.LENGTH_SHORT).show()
               replaceFragment(requireActivity(), FavoritesFragment())

                true
            }
            R.id.navigation_profile -> {
                // Handle favorite icon press
                Toast.makeText(requireContext(), "perfil", Toast.LENGTH_SHORT).show()
               replaceFragment(requireActivity(), ProfileFragment())
                //añadir etiqueta
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

}