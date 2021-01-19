package com.example.buddy_hubb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.buddy_hubb.models.Inbox
import com.example.buddy_hubb.models.Message
import com.example.buddy_hubb.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.google.GoogleEmojiProvider
import kotlinx.android.synthetic.main.activity_chat.*

const val NAME="name"
const val IMAGE="Image"
const val UID="uid"

class ChatActivity : AppCompatActivity() {
    val name by lazy {
        intent.getStringExtra(NAME)
    }

    val image by lazy {
        intent.getStringExtra(IMAGE)
    }

    val frindId by lazy {
        intent.getStringExtra(UID)
    }


    val mCurrentUid by lazy {
        FirebaseAuth.getInstance().uid
    }

    val database by lazy {
        FirebaseDatabase.getInstance()
    }
    lateinit var currentUser: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EmojiManager.install(GoogleEmojiProvider())
        setContentView(R.layout.activity_chat)
        FirebaseFirestore.getInstance().collection("user").document(mCurrentUid!!).get()
            .addOnSuccessListener {
            currentUser=it.toObject(User::class.java)!!
            }

        sendBtn.setOnClickListener {
            msgEdtv.text?.let {
                if(it.isNotEmpty())
                {
                    sendMessage(it.toString())
                    it.clear()

                }
            }
        }
        nameTv.text=name
        Picasso.get().load(image).into(userImgView)
    }

    private fun sendMessage(msg: String) {
        val id=getMessage(frindId = frindId!!).push().key
        checkNotNull(id)
        { "cant be null"}
        val msgMap=Message(msg,mCurrentUid!!,id)
        getMessage(frindId!!).child(id).setValue(msgMap).addOnCanceledListener {
        }
        updateLastMessage(msgMap)


    }

    private fun updateLastMessage(message: Message) {
        val inboxMap = Inbox(
            message.msg,
            frindId!!,
            name!!,
            image!!,
            count = 0
        )

        getInbox(mCurrentUid!!,frindId!!).setValue(inboxMap).addOnSuccessListener {
            getInbox(frindId!!,mCurrentUid!!).addListenerForSingleValueEvent(object :
            ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value=snapshot.getValue(Inbox::class.java)
                    inboxMap.apply {
                        from=message.senderId
                        name=currentUser.name
                        image=currentUser.thumbImage
                        count=1
                    }
                    value?.let {
                        if(it.from==message.senderId)
                            inboxMap.count=value.count+1
                    }
                    getInbox(frindId!!,mCurrentUid!!).setValue(inboxMap)


                }

                override fun onCancelled(error: DatabaseError) {}



            })
        }
    }
    private fun markAsRead()
    {
        getInbox(frindId!!,mCurrentUid!!).child("count").setValue(0)
    }




    private fun getMessage(frindId: String)=database.reference.child("messages/${getId(frindId)}")

private fun getInbox(toUser:String , fromUser:String)=database.reference.child("chats/$toUser/$fromUser")

   private fun getId(frindId:String):String{

        if(frindId >mCurrentUid!!  )
        {
            return mCurrentUid +frindId
        }
        else
            return frindId+ mCurrentUid
    }



}