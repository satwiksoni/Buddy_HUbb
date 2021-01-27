package com.example.buddy_hubb.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.buddy_hubb.ChatActivity
import com.example.buddy_hubb.R
import com.example.buddy_hubb.crypto.AES
import com.example.buddy_hubb.crypto.DES
import com.example.buddy_hubb.crypto.MD5
import com.example.buddy_hubb.crypto.RSA
import kotlinx.android.synthetic.main.safemode.view.*

class SafeModeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {



        val view= inflater.inflate(R.layout.safemode,container,false)
        view.aes.setOnClickListener {
            val intent= Intent(requireContext(), AES::class.java)
            startActivity(intent)
        }

        view.rsa.setOnClickListener {
            val intent= Intent(requireContext(), RSA::class.java)
            startActivity(intent)
        }

        view.md.setOnClickListener {
            val intent= Intent(requireContext(), MD5::class.java)
            startActivity(intent)
        }
        view.des.setOnClickListener {
            val intent= Intent(requireContext(),DES::class.java)
            startActivity(intent)
        }


        return view
    }
}
