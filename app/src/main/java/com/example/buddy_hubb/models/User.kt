package com.example.buddy_hubb.models

import com.google.firebase.firestore.FieldValue

data class User(
    val name:String,
    val imageUrl:String,
    val thumbImage:String,
    val uid:String,
    val deviceToken:String,
    val status:String,
    val onlineStatus: Boolean,
    val coverImage:String,
    val phoneNumber:String,
    val Email:String,
    val Facebook:String,
    val Insta:String,
    val LinkedIn:String


    )
{
    /** Empty [Constructor] for Firebase */
    constructor():this("","","","","","",false,"","","","","","")
//    constructor(name: String, imageUrl: String, thumbImage: String, uid: String) :
//            this(name, imageUrl, thumbImage, "", uid = uid, status = "Hey There, I am using whatsapp", onlineStatus = false)

    constructor( name:String,
                 imageUrl:String,
                 thumbImage:String,
                 uid:String,
                 phone:String
                 ):
            this(name,imageUrl,thumbImage,uid=uid,"",status="Hey..Just joined this cool platform.",onlineStatus=false,"",phone,"","","","")



}
