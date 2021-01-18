package com.example.buddy_hubb

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.buddy_hubb.models.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item.view.*

class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    fun bind(user:User,onClick:(name:String,photo:String,id:String)->Unit)=
        with(itemView)
    {
        countTv.isVisible=false
        timeTv.isVisible=false
        titleTv.text=user.name
        subTitleTv.text=user.status

        Picasso.get()
            .load(user.thumbImage)
            .placeholder(R.drawable.avatar)
            .error(R.drawable.avatar)
            .into(userImgView)

        setOnClickListener {
            onClick.invoke(user.name,user.imageUrl,user.uid)
        }


    }
}
