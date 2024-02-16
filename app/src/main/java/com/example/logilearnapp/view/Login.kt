package com.example.logilearnapp.view


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.logilearnapp.R
import com.example.logilearnapp.view.RegisterFragment
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //auth
       // auth = Firebase.auth

        val fragment = RegisterFragment()
       /* val textView: TextView = findViewById(R.id.textoRegistrarse)
        //lo meto en metodo param fragment, desde fragment accedo al metodo
        textView.setOnClickListener{

            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()

            //reemplazar con el nuevo fragment
            transaction.replace(R.id.viewer, fragment)

            //no se que es back stack
            transaction.addToBackStack(null)
            transaction.commit()


        }*/

        fun cambiarFragment( fragment: Fragment){
            /*

              textView.setOnClickListener{

                val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()

                //reemplazar con el nuevo fragment
                transaction.replace(R.id.viewer, fragment)

                //no se que es back stack
                transaction.addToBackStack(null)
                transaction.commit()

            }
            * */

        }

    }

    override fun onStart() {
        super.onStart()
       // var currentUser = auth.currentUser;
        //updateUI(currentUser);
    }
}