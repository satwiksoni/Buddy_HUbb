package com.example.buddy_hubb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.buddy_hubb.auth.PhoneActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    val auth by lazy {
        FirebaseAuth.getInstance()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(auth.currentUser==null)
        {
            startActivity(Intent(this, PhoneActivity::class.java))
            finish()
        }
        else
        {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

    }
}