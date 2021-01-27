package com.example.buddy_hubb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.buddy_hubb.adapter.ScreenSliderAdapter
import com.example.buddy_hubb.models.User
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.toolbar

class MainActivity : AppCompatActivity() {
    val database by lazy {
        FirebaseFirestore.getInstance()
    }
    val storage by lazy {
        FirebaseStorage.getInstance()
    }
    val auth by lazy {
        FirebaseAuth.getInstance()
    }
    lateinit var currentUser:User
    private val mCurrentUid: String by lazy {
        FirebaseAuth.getInstance().uid!!
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar!!.title=""

        viewPager.adapter= ScreenSliderAdapter(this)
        TabLayoutMediator(tabs,viewPager,TabLayoutMediator.TabConfigurationStrategy{ tab: TabLayout.Tab, pos: Int ->
            when(pos)
            {
                0->tab.text="CHATS"
                1->tab.text="SAFE MODE"
                2->tab.text="PEOPLE"
            }

        }).attach()



        database.collection("user").document(mCurrentUid).get()
                .addOnSuccessListener {

                    //UserName\
                    currentUser=it.toObject(User::class.java)!!
                    user_name.setText(currentUser.name)

                    Picasso.get()
                            .load(currentUser.thumbImage)
                            .placeholder(R.drawable.avatar)
                            .error(R.drawable.avatar)
                            .into(profile_image)
                }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return true
    }

    override fun onStart() {
        super.onStart()
        online.visibility=View.VISIBLE
        liveStatus("onlineStatus",true)

    }

    override fun onPause() {
        super.onPause()
        online.visibility=View.GONE
        liveStatus("onlineStatus",false)
    }

    override fun onDestroy() {
        super.onDestroy()
        online.visibility=View.GONE
        liveStatus("onlineStatus",false)

    }
    fun liveStatus(key:String,value:Boolean)
    {
        database.collection("user").document(auth.uid!!).update(key,value).addOnSuccessListener {
        }.addOnFailureListener{
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.Setting->{
                startActivity(Intent(this,SettingActivity::class.java))
            }
        }
        return true
    }

}