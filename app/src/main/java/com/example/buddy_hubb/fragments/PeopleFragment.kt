package com.example.buddy_hubb.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.buddy_hubb.*
import com.example.buddy_hubb.auth.SigninActivity
import com.example.buddy_hubb.models.User
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.firebase.ui.firestore.paging.LoadingState.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.protobuf.Internal
import kotlinx.android.synthetic.main.fragment_chats.*
import java.lang.Exception

private const val DELETED_VIEW_TYPE = 1
private const val NORMAL_VIEW_TYPE = 2
class PeopleFragment : Fragment() {
    lateinit var mapAdapter: FirestorePagingAdapter<User,RecyclerView.ViewHolder>
    val auth by lazy {
        FirebaseAuth.getInstance()
    }

    val database by lazy {
        FirebaseFirestore.getInstance().collection("user")
            .orderBy("name",Query.Direction.ASCENDING)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setupAdapter()
        return inflater.inflate(R.layout.fragment_chats,container,false)
    }

    private fun setupAdapter() {
        val config=PagedList.Config.Builder()
            .setPrefetchDistance(2)
            .setPageSize(10)
            .setEnablePlaceholders(false)
            .build()
        val option=FirestorePagingOptions.Builder<User>()
            .setLifecycleOwner(viewLifecycleOwner)
            .setQuery(database,config,User::class.java)
            .build()
        mapAdapter=object:FirestorePagingAdapter<User,RecyclerView.ViewHolder>(option)
        {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                when(viewType)
                {
                    NORMAL_VIEW_TYPE->{
                        val view=layoutInflater.inflate(R.layout.list_item,parent,false)
                        return UserViewHolder( view)
                    }
                    else->
                    {
                        val view=layoutInflater.inflate(R.layout.empty_view,parent,false)
                        return EmptyViewHolder(view)
                    }
                }


            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: User) {

                if(holder is UserViewHolder) {
                    holder.bind(user = model){ name: String, image: String, uid: String ->
                       val intent=Intent(requireContext(),ChatActivity::class.java)
                        intent.putExtra(USER_NAME,name)
                        intent.putExtra(USER_THUMB_IMAGE,image)
                        intent.putExtra(USER_ID,uid)
                        startActivity(intent)

                    }
                }
                else
                {

                }
            }

            override fun onLoadingStateChanged(state: LoadingState) {
                super.onLoadingStateChanged(state)
                when(state)
                {
                    LOADING_INITIAL -> {}
                    LOADING_MORE -> {}
                    LOADED -> {}
                    FINISHED -> {}
                    ERROR -> {
                        Toast.makeText(
                            requireContext(),
                            "Error Occurred!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onError(e: Exception) {
                super.onError(e)

            }

            override fun getItemViewType(position: Int): Int {
                val item=getItem(position)?.toObject(User::class.java)
                return if(auth.uid==item!!.uid)
                {
                    DELETED_VIEW_TYPE
                }

                else
                {
                    NORMAL_VIEW_TYPE
                }
            }


        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            layoutManager=LinearLayoutManager(requireContext())
            adapter=mapAdapter
        }
    }

}
