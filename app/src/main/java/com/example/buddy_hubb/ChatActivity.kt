//
//
//package com.example.buddy_hubb
//
//import android.content.Context
//import android.content.Intent
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.buddy_hubb.adapter.ChatAdapter
//import com.example.buddy_hubb.models.*
//import com.example.buddy_hubb.utils.KeyboardVisibicdlityUtil
//import com.example.buddy_hubb.utils.isSameDayAs
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.*
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.ktx.Firebase
//import com.squareup.picasso.Picasso
//import com.vanniktech.emoji.EmojiManager
//import com.vanniktech.emoji.EmojiPopup
//import com.vanniktech.emoji.google.GoogleEmojiProvider
//import kotlinx.android.synthetic.main.activity_chat.*
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchersf
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import com.google.firebase.database.ChildEventListener as ChildEventListener
//
//const val NAME="name"
//const val IMAGE="Image"
//const val UID="uid"
//
//class ChatActivity : AppCompatActivity() {
//    val name by lazy {
//        intent.getStringExtra(NAME)
//    }
//
//    val image by lazy {
//        intent.getStringExtra(IMAGE)
//    }
//
//    val frindId by lazy {
//        intent.getStringExtra(UID)
//    }
//
//
//    val mCurrentUid by lazy {
//        FirebaseAuth.getInstance().uid
//    }
//
//    val database by lazy {
//        FirebaseDatabase.getInstance()
//    }
//    lateinit var currentUser: User
//    private val message: MutableList<ChatEvent> = mutableListOf()
//
//    lateinit var chatAdapter: ChatAdapter
//    private lateinit var keyboardVisibilityHelper: KeyboardVisibilityUtil
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        EmojiManager.install(GoogleEmojiProvider())
//        setContentView(R.layout.activity_chat)
//
//        keyboardVisibilityHelper = KeyboardVisibilityUtil(rootView) {
//            msgRv.scrollToPosition(message.size - 1)
//        }
//
//        FirebaseFirestore.getInstance().collection("user").document(mCurrentUid!!).get()
//            .addOnSuccessListener {
//            currentUser=it.toObject(User::class.java)!!
//            }
//
//        chatAdapter= ChatAdapter(message, mCurrentUid!!)
//        msgRv.apply {
//            layoutManager=LinearLayoutManager(this@ChatActivity)
//            adapter=chatAdapter
//            msgRv.scrollToPosition(message.size + 1)
//
//        }
//
//        val emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(msgEdtv)
//        smileBtn.setOnClickListener {
//            emojiPopup.toggle()
//        }
//        swipeToLoad.setOnRefreshListener {
//            val workerScope = CoroutineScope(Dispatchers.Main)
//            workerScope.launch {
//                delay(2000)
//                swipeToLoad.isRefreshing = false
//            }
//        }
//
//
//        markAsRead()
//        listenToMessage()
//        nameTv.text=name
//        Picasso.get().load(image).into(userImgView)
//
//        sendBtn.setOnClickListener {
//            msgEdtv.text?.let {
//                if(it.isNotEmpty())
//                {
//                    sendMessage(it.toString())
//                    it.clear()
//
//                }
//            }
//        }
//
//
//    }
//
//
//    private fun listenToMessage()
//    {
//        getMessage(frindId!!)
//            .orderByKey()
//            .addChildEventListener(object : ChildEventListener{
//                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//                    val msg=snapshot.getValue(Message::class.java)!!
//                    addMessages(msg)
//                }
//
//                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
//                    TODO("Not yet implemented")
//                }
//
//                override fun onChildRemoved(snapshot: DataSnapshot) {
//                    TODO("Not yet implemented")
//                }
//
//                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
//                    TODO("Not yet implemented")
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    TODO("Not yet implemented")
//                }
//
//            })
//
//    }
//
//    private fun addMessages(msg: Message) {
//        val eventBefore= message.lastOrNull()
//        if((eventBefore!=null && !eventBefore.sentAt.isSameDayAs(msg.sentAt) )|| eventBefore==null)
//        {
//            message.add(
//                DateHeader(
//                    msg.sentAt,context = this
//                )
//            )
//        }
//        message.add(msg)
//        chatAdapter.notifyItemInserted(message.size-1)
//        msgRv.scrollToPosition(message.size-1)
//
//    }
//
//
//    private fun sendMessage(msg: String) {
//        val id=getMessage(frindId = frindId!!).push().key
//        checkNotNull(id)
//        { "cant be null"}
//        val msgMap=Message(msg,mCurrentUid!!,id)
//        getMessage(frindId!!).child(id).setValue(msgMap).addOnCanceledListener {
//        }
//        updateLastMessage(msgMap)
//
//
//    }
//
//    private fun updateLastMessage(message: Message) {
//        val inboxMap = Inbox(
//            message.msg,
//            frindId!!,
//            name!!,
//            image!!,
//            count = 0
//        )
//
//        getInbox(mCurrentUid!!,frindId!!).setValue(inboxMap).addOnSuccessListener {
//            getInbox(frindId!!,mCurrentUid!!).addListenerForSingleValueEvent(object :
//            ValueEventListener{
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val value=snapshot.getValue(Inbox::class.java)
//                    inboxMap.apply {
//                        from=message.senderId
//                        name=currentUser.name
//                        image=currentUser.thumbImage
//                        count=1
//                    }
//                    value?.let {
//                        if(it.from==message.senderId)
//                            inboxMap.count=value.count+1
//                    }
//                    getInbox(frindId!!,mCurrentUid!!).setValue(inboxMap)
//
//
//                }
//                override fun onCancelled(error: DatabaseError) {}
//
//
//
//            })
//        }
//    }
//    private fun markAsRead()
//    {
//        getInbox(frindId!!,mCurrentUid!!).child("count").setValue(0)
//    }
//
//
//
//
//    private fun getMessage(frindId: String)=database.reference.child("messages/${getId(frindId)}")
//
//private fun getInbox(toUser:String , fromUser:String)=database.reference.child("chats/$toUser/$fromUser")
//
//   private fun getId(frindId:String):String{
//
//        if(frindId >mCurrentUid!!  )
//        {
//            return mCurrentUid +frindId
//        }
//        else
//            return frindId+ mCurrentUid
//    }
//
//
//    companion object {
//
//        fun createChatActivity(context: Context, id: String, name: String, image: String): Intent
//        {
//            val intent = Intent(context, ChatActivity::class.java)
//            intent.putExtra(UID, id)
//            intent.putExtra(NAME, name)
//            intent.putExtra(IMAGE, image)
//
//            return intent
//        }
//    }
//
//}

package com.example.buddy_hubb
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.buddy_hubb.adapter.ChatAdapter
import com.example.buddy_hubb.models.*
import com.example.buddy_hubb.utils.KeyboardVisibilityUtil
import com.example.buddy_hubb.utils.isSameDayAs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.EmojiPopup
import com.vanniktech.emoji.google.GoogleEmojiProvider
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val USER_ID = "userId"
const val USER_THUMB_IMAGE = "thumbImage"
const val USER_NAME = "userName"

class ChatActivity : AppCompatActivity() {

    private val friendId by lazy {
        intent.getStringExtra(USER_ID)!!
    }
    private val name by lazy {
        intent.getStringExtra(USER_NAME)!!
    }
    private val image by lazy {
        intent.getStringExtra(USER_THUMB_IMAGE)!!
    }
    private val mCurrentUid: String by lazy {
        FirebaseAuth.getInstance().uid!!
    }
    private val db: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }
    lateinit var currentUser: User
    lateinit var chatAdapter: ChatAdapter


    private lateinit var keyboardVisibilityHelper: KeyboardVisibilityUtil
    private val mutableItems: MutableList<ChatEvent> = mutableListOf()
    private val mLinearLayout: LinearLayoutManager by lazy { LinearLayoutManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EmojiManager.install(GoogleEmojiProvider())
        setContentView(R.layout.activity_chat)

        keyboardVisibilityHelper = KeyboardVisibilityUtil(rootView) {
            msgRv.scrollToPosition(mutableItems.size - 1)
        }

        FirebaseFirestore.getInstance().collection("user").document(mCurrentUid!!).get()
                .addOnSuccessListener {
                    currentUser=it.toObject(User::class.java)!!
                }

        chatAdapter= ChatAdapter(mutableItems, mCurrentUid!!)
        msgRv.apply {
            layoutManager=LinearLayoutManager(this@ChatActivity)
            adapter=chatAdapter
            msgRv.scrollToPosition(mutableItems.size + 1)
        }

        nameTv.text = name
        Picasso.get().load(image).into(userImgView)

        val emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(msgEdtv)
        smileBtn.setOnClickListener {
            emojiPopup.toggle()
        }

        swipeToLoad.setOnRefreshListener {
            val workerScope = CoroutineScope(Dispatchers.Main)
            workerScope.launch {
                delay(2000)
                swipeToLoad.isRefreshing = false
            }
        }


        sendBtn.setOnClickListener {
            msgEdtv.text?.let {
                if (it.isNotEmpty()) {
                    sendMessage(it.toString())
                    it.clear()
                }
            }
        }



        updateReadCount()
        listenMessages() { msg, update ->
            if (update) {
                updateMessage(msg)
            } else {
                addMessage(msg)
            }
        }
        chatAdapter.highFiveClick = { id, status ->
            updateHighFive(id, status)
        }


    }

    private fun updateReadCount() {
        getInbox(mCurrentUid, friendId).child("count").setValue(0)
    }


    private fun updateHighFive(id: String, status: Boolean) {
        getMessages(friendId).child(id).updateChildren(mapOf("liked" to status))
    }


    private fun addMessage(msg: Message) {
        val eventBefore= mutableItems.lastOrNull()
        if((eventBefore!=null && !eventBefore.sentAt.isSameDayAs(msg.sentAt) )|| eventBefore==null)
        {
            mutableItems.add(
                DateHeader(
                    msg.sentAt,context = this
                )
            )
        }
        mutableItems.add(msg)
        chatAdapter.notifyItemInserted(mutableItems.size-1)
        msgRv.scrollToPosition(mutableItems.size-1)

    }

    private fun updateMessage(msg: Message) {
        val position = mutableItems.indexOfFirst {
            when (it) {
                is Message -> it.msgId == msg.msgId
                else -> false
            }
        }
        mutableItems[position] = msg

        chatAdapter.notifyItemChanged(position)
        msgRv.scrollToPosition(mutableItems.size + 1)
    }


    private fun listenMessages(newMsg: (msg: Message, update: Boolean) -> Unit) {
        getMessages(friendId)
                .orderByKey()
                .addChildEventListener(object : ChildEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onChildMoved(p0: DataSnapshot, p1: String?) {

                    }

                    override fun onChildChanged(data: DataSnapshot, p1: String?) {
                        val msg = data.getValue(Message::class.java)!!
                        newMsg(msg, true)
                    }

                    override fun onChildAdded(data: DataSnapshot, p1: String?) {
                        val msg = data.getValue(Message::class.java)!!
                        newMsg(msg, false)
                    }

                    override fun onChildRemoved(p0: DataSnapshot) {
                    }

                })

    }



        private fun sendMessage(msg: String) {
        val id=getMessages(friendId!!).push().key
        checkNotNull(id)
        { "cant be null"}
        val msgMap=Message(msg,mCurrentUid!!,id)
        getMessages(friendId!!).child(id).setValue(msgMap).addOnCanceledListener {
        }
        updateLastMessage(msgMap,mCurrentUid)
    }
    private fun updateLastMessage(message: Message, mCurrentUid: String) {
        val inboxMap = Inbox(
                message.msg,
                friendId,
                name,
                image,
                message.sentAt,
                0
        )

        getInbox(mCurrentUid, friendId).setValue(inboxMap)

        getInbox(friendId, mCurrentUid).addListenerForSingleValueEvent(object :
                ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                val value = p0.getValue(Inbox::class.java)
                inboxMap.apply {
                    from = message.senderId
                    name = currentUser.name
                    image = currentUser.thumbImage
                    count = 1
                }
                if (value?.from == message.senderId) {
                    inboxMap.count = value.count + 1
                }
                getInbox(friendId, mCurrentUid).setValue(inboxMap)
            }

        })
    }

    private fun getMessages(friendId: String) = db.reference.child("messages/${getId(friendId)}")

    private fun getInbox(toUser: String, fromUser: String) =
            db.reference.child("chats/$toUser/$fromUser")


    private fun getId(friendId: String): String {
        return if (friendId > mCurrentUid) {
            mCurrentUid + friendId
        } else {
            friendId + mCurrentUid
        }
    }

    override fun onResume() {
        super.onResume()
        rootView.viewTreeObserver
                .addOnGlobalLayoutListener(keyboardVisibilityHelper.visibilityListener)
    }


    override fun onPause() {
        super.onPause()
        rootView.viewTreeObserver
                .removeOnGlobalLayoutListener(keyboardVisibilityHelper.visibilityListener)
    }

    companion object {

        fun createChatActivity(context: Context, id: String, name: String, image: String): Intent
        {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(USER_ID, id)
            intent.putExtra(USER_NAME, name)
            intent.putExtra(USER_THUMB_IMAGE, image)

            return intent
        }
    }

}