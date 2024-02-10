package com.example.logilearnapp.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import com.example.logilearnapp.MainActivity
import com.example.logilearnapp.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    //funcion para cambiar a otra pagina
    /*requireContext() es un método proporcionado por la clase
    Fragment que devuelve el contexto asociado al fragmento.
    Es una forma segura de obtener el contexto de la actividad desde
    un fragmento, y se utiliza comúnmente en lugar de this cuando necesitas operar
     con el contexto de la actividad.*/
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }
    //intent
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginbtn: Button = view.findViewById(R.id.loginBtn)
        // Configurar el clic del botón
        loginbtn.setOnClickListener {
            // Crear un Intent para la otra actividad
            val intent = Intent(activity, MainActivity::class.java)

            // Iniciar la otra actividad
            startActivity(intent)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    //val textView: TextView = find
    fun cambiarFragment( fragment: Fragment, txtView: TextView, Login: View){

        txtView.setOnClickListener{
            /*
             val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()

            //reemplazar con el nuevo fragment
            transaction.replace(R.id.viewer, fragment)

            //no se que es back stack
            transaction.addToBackStack(null)
            transaction.commit()

            * */

        }
    }
}