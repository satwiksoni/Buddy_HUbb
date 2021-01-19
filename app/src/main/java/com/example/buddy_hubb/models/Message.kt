package com.example.buddy_hubb.models

import android.content.Context
import com.example.buddy_hubb.utils.formatAsFull
import com.example.buddy_hubb.utils.formatAsHeader
import java.util.*


interface chatEvent
{
    val sentAt: Date
}
data class Message(
    val msg:String,
    val senderId:String,
    val msgId:String,
    val type:String="TEXT",
    val status:Int=1,
    val liked:Boolean=false,
    override val sentAt:Date=Date()
):chatEvent
{
    constructor() : this("","","","",1,false,Date())
}

data class DateHeader(
    override val sentAt: Date= Date(), val context:Context
):chatEvent
{
    val datw=sentAt.formatAsHeader(context)
}
