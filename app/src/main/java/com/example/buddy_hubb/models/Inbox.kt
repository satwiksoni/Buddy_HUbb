package com.example.buddy_hubb.models

import java.util.*

data class Inbox(
    val msg:String,
    var from:String,
    var name:String,
    var image:String,
    val time:Date=Date(),
    var count:Int

)
{
    constructor():this("","","","", Date(),0)
}
