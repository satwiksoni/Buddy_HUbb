package com.example.buddy_hubb

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.buddy_hubb.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile_edit.*
import kotlinx.android.synthetic.main.setting.*
import kotlinx.android.synthetic.main.setting.status
import kotlinx.android.synthetic.main.setting.username_settings

class ProfileEditActivity : AppCompatActivity() {

    val database by lazy {
        FirebaseFirestore.getInstance()
    }
    val storage by lazy {
        FirebaseStorage.getInstance()
    }
    val auth by lazy {
        FirebaseAuth.getInstance()
    }

    lateinit var downloadUrl:String

    private var imageuri: Uri?=null
    private var RequestCode=438
    private var coverchecker:String?=null
    private var socialchecker:String?=null
    lateinit var currentUser:User
    private var storageReference: StorageReference?=null
    private val mCurrentUid: String by lazy {
        FirebaseAuth.getInstance().uid!!
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        FirebaseFirestore.getInstance().collection("user").document(mCurrentUid).get()
            .addOnSuccessListener {

                //UserName\
                currentUser=it.toObject(User::class.java)!!
                UserName.setText(currentUser.name)
                status.setText(currentUser.status)
                phoneNo.setText(currentUser.phoneNumber)
                Emailid.setText(currentUser.Email)
                Facebookurl.setText(currentUser.Facebook)
                Instaurl.setText(currentUser.Insta)
                Linkedinurl.setText(currentUser.LinkedIn)
            }


        usernameET.setOnClickListener {
            val key="name"
            val value=UserName.text
            onEditClicked(key,value = value.toString())
            startActivity(Intent(this,ProfileEditActivity::class.java))
            finish()
        }
        statusET.setOnClickListener {
            val key="status"
            val value=status.text
            onEditClicked(key,value = value.toString())
            startActivity(Intent(this,ProfileEditActivity::class.java))
            finish()
        }

        emailEtBT.setOnClickListener {
            val key="Email"
            val value=Emailid.text
            onEditClicked(key,value = value.toString())
            startActivity(Intent(this,ProfileEditActivity::class.java))
            finish()
        }
        facebooketdone.setOnClickListener {
            val key="Facebook"
            val value=Facebookurl.text
            onEditClicked(key,value = "https://m.facebook.com/${value.toString()}")
            startActivity(Intent(this,ProfileEditActivity::class.java))
            finish()
        }

        instabutton.setOnClickListener {
            val key="Insta"
            val value=Instaurl.text
            onEditClicked(key,value = "https://m.instagram.com/${value.toString()}")
            startActivity(Intent(this,ProfileEditActivity::class.java))
            finish()
        }

        link.setOnClickListener {
            val key="LinkedIn"
            val value=Emailid.text
            onEditClicked(key,value = value.toString())
            startActivity(Intent(this,ProfileEditActivity::class.java))
            finish()
        }





    }



    fun onEditClicked(key:String,value:String)
    {

        database.collection("user").document(auth.uid!!).update(key,value).addOnSuccessListener {
            Toast.makeText(this,"Success...", Toast.LENGTH_LONG).show()
        }.addOnFailureListener{
            Toast.makeText(this,"Failed try again..!", Toast.LENGTH_LONG).show()
        }



    }
}