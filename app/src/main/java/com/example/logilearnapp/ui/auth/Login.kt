package com.example.logilearnapp.ui.auth


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.logilearnapp.MainActivity
import com.example.logilearnapp.R
import com.example.logilearnapp.view.RegisterFragment
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val fragment = RegisterFragment()
        val textView: TextView = findViewById(R.id.textoRegistrarse)
        //lo meto en metodo param fragment, desde fragment accedo al metodo
        textView.setOnClickListener{

            val intent = Intent(this@Login, Register::class.java)
            startActivity(intent)
            //no se que es back stack
        }
        val loginbtn: Button = findViewById(R.id.loginBtn)
        // Configurar el clic del botón
        loginbtn.setOnClickListener {
            // Crear un Intent para la otra actividad
            val intent = Intent(this@Login, MainActivity::class.java)
            // Iniciar la otra actividad
            startActivity(intent)
        }

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