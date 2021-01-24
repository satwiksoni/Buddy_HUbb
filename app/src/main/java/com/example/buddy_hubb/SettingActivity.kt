package com.example.buddy_hubb


import com.google.android.gms.tasks.Continuation
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.example.buddy_hubb.auth.createProgressDialog
import com.example.buddy_hubb.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_signin.*
import kotlinx.android.synthetic.main.list_item.view.*
import kotlinx.android.synthetic.main.setting.*
import kotlinx.android.synthetic.main.setting.view.*

class SettingActivity : AppCompatActivity() {
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



    private var imageuri:Uri?=null
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
        setContentView(R.layout.setting)

        FirebaseFirestore.getInstance().collection("user").document(mCurrentUid).get()
            .addOnSuccessListener {
                Log.i("HHH","Success")

                //UserName
                currentUser=it.toObject(User::class.java)!!
                username_settings.setText(currentUser.name)
                status.setText(currentUser.status)

                //Dp
                Picasso.get()
                    .load(currentUser.thumbImage)
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .into(profile_image_settings)

                //Cover
                Picasso.get()
                        .load(currentUser.coverImage)
                        .placeholder(R.drawable.one)
                        .error(R.drawable.one)
                        .into(cover_image_settings)
            }




        profile_image_settings.setOnClickListener{
            coverchecker="dp"
            createPermissionForImage()
        }

        cover_image_settings.setOnClickListener{
            coverchecker="cover"
            createPermissionForImage()
        }
        statuspen.setOnClickListener {
            sendToEditPage()
        }

        set_facebook.setOnClickListener{
            socialchecker="facebook"
            sendToEditPage()
        }

        set_instagram.setOnClickListener{
            socialchecker="instagram"
            sendToEditPage()
        }
        set_linkedin.setOnClickListener{
            socialchecker="website"
            sendToEditPage()
        }

    }

    fun sendToEditPage()
    {
        startActivity(Intent(this,ProfileEditActivity::class.java))

    }


//    private fun setsocialLinks() {
//        val builder: AlertDialog.Builder= AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog_Alert)
//        if(socialchecker=="website")
//        {
//            builder.setTitle("Write Url")
//        }
//        else{
//            builder.setTitle("Write username")
//
//        }
//        val edittext= EditText(this)
//        if(socialchecker=="website")
//        {
//            edittext.hint="e.g www.goole.com"
//            edittext.setHintTextColor(resources.getColor(R.color.black))
//            edittext.setTextColor(resources.getColor(R.color.black))
//
//
//        }
//        else{
//            edittext.hint="e.g xyz123"
//            edittext.setHintTextColor(resources.getColor(R.color.black))
//            edittext.setTextColor(resources.getColor(R.color.black))
//
//
//        }
//        builder.setView(edittext)
//        builder.setPositiveButton("Create", DialogInterface.OnClickListener{
//            dialog, which ->
//            val str=edittext.text.toString()
//            if(str=="")
//            {
//                Toast.makeText(this,"Please Write Something...",Toast.LENGTH_SHORT).show()
//            }
//            else{
//                savesocialLinks(str)
//            }
//        })
//        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener{
//            dialog, which ->
//            dialog.cancel()
//
//        })
//        builder.show()
//
//
//    }
//
//    private fun savesocialLinks(str: String) {
//        val mapsocial=HashMap<String,Any>()
//        when(socialchecker)
//        {
//            "Facebook"->{
//                database.collection("user").document(auth.uid!!).update("FB","https://m.facebook.com/$str").addOnSuccessListener {
//
//                    Toast.makeText(this,"Success...",Toast.LENGTH_LONG).show()
//
//                }.addOnFailureListener{}
//            }
//            "Instagram"->{
//                database.collection("user").document(auth.uid!!).update("Insta","https://m.instagram.com/$str").addOnSuccessListener {
//
//                    Toast.makeText(this,"Success...",Toast.LENGTH_LONG).show()
//
//                }.addOnFailureListener{}
//
//            }
//            "Linkedin"->{
//                database.collection("user").document(auth.uid!!).update("Insta","https://$str").addOnSuccessListener {
//
//                    Toast.makeText(this,"Success...",Toast.LENGTH_LONG).show()
//
//                }.addOnFailureListener{}
//            }
//        }
//    }


    private fun createPermissionForImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                    && (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            ) {
                val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                val permissionWrite = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

                requestPermissions(
                        permission,
                        1001
                ) // GIVE AN INTEGER VALUE FOR PERMISSION_CODE_READ LIKE 1001
                requestPermissions(
                        permissionWrite,
                        1002

                ) // GIVE AN INTEGER VALUE FOR PERMISSION_CODE_WRITE LIKE 1002
            } else {
                pickImageFromGallery()
            }
        }
    }


    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"

        startActivityForResult(
                intent,
                RequestCode
        ) // GIVE AN INTEGER VALUE FOR IMAGE_PICK_CODE LIKE 100
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==RequestCode && resultCode == Activity.RESULT_OK && data!!.data!=null)
        {
            Log.i("IDHR","success")
            imageuri=data.data
            Toast.makeText(this,"Uploading",Toast.LENGTH_SHORT).show()
            startUpload(imageuri!!)
        }
    }



    private fun startUpload(filePath: Uri) {
        val progressDialog = createProgressDialog("Uploading profile picture..", false)
        progressDialog.show()


       // Toast.makeText(this,"image is uploading please wait...",Toast.LENGTH_LONG).show()


        var ref = storage.reference.child("uploads/" + auth.uid.toString())
        if(coverchecker.equals("cover"))
        {
            ref = storage.reference.child("coveruploads/" + auth.uid.toString())
        }
        val uploadTask = ref.putFile(filePath)
        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation ref.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                downloadUrl = task.result.toString()
                if(coverchecker.equals("dp"))
                {
                    database.collection("user").document(auth.uid!!).update("imageUrl",downloadUrl).addOnSuccessListener {
                    }.addOnFailureListener{
                        progressDialog.dismiss()

                        Toast.makeText(this,"Failed try again..!",Toast.LENGTH_LONG).show()
                    }


                    database.collection("user").document(auth.uid!!).update("thumbImage",downloadUrl).addOnSuccessListener {

                        Toast.makeText(this,"Success...",Toast.LENGTH_LONG).show()
                        progressDialog.dismiss()

                    }.addOnFailureListener{
                        progressDialog.dismiss()
                        Toast.makeText(this,"Failed try again..!",Toast.LENGTH_LONG).show()
                    }

                }

                else if(coverchecker.equals("cover"))
                {

                    database.collection("user").document(auth.uid!!).update("coverImage",downloadUrl).addOnSuccessListener {

                        Toast.makeText(this,"Success...",Toast.LENGTH_LONG).show()
                        progressDialog.dismiss()
                    }.addOnFailureListener{
                        progressDialog.dismiss()
                        Toast.makeText(this,"Failed try again..!",Toast.LENGTH_LONG).show()
                    }



                }



            }
        }.addOnFailureListener {

        }
    }






}