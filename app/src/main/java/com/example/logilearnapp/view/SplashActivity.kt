package com.example.logilearnapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.logilearnapp.MainActivity
import com.example.logilearnapp.R

class SplashActivity : AppCompatActivity() {

    private val SPLASH_DELAY: Long = 2000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //retrasar
        Handler().postDelayed({

            val intent  = Intent(this@SplashActivity, Login::class.java);
            startActivity(intent)
            finish(); //Cierra la actividad
        }, SPLASH_DELAY)
    }
}