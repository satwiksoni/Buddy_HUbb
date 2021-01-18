package com.example.buddy_hubb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
const val NAME="name"
const val IMAGE="Image"
const val UID="uid"

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
    }
}