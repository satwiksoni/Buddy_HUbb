package com.example.buddy_hubb


import com.google.android.gms.tasks.Continuation
import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
    private var Requestcode=438
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

                //Dp
                Picasso.get()
                    .load(currentUser.imageUrl)
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .into(profile_image_settings)
            }




        profile_image_settings.setOnClickListener{
            pickImageFromGallery()
        }

//        cover_image_settings.setOnClickListener{
//            coverchecker="cover"
//            pickImage()
//        }
//
//        set_facebook.setOnClickListener{
//            socialchecker="facebook"
//            setsocialLinks()
//        }
//
//        set_instagram.setOnClickListener{
//            socialchecker="instagram"
//            setsocialLinks()
//        }
//        set_website.setOnClickListener{
//            socialchecker="website"
//            setsocialLinks()
//        }

    }

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
                1000
        ) // GIVE AN INTEGER VALUE FOR IMAGE_PICK_CODE LIKE 100
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 1000) {
            data?.data?.let {
                profile_image_settings.setImageURI(it)
                startUpload(it)
            }
        }
    }



    private fun startUpload(filePath: Uri) {
        nextBtn.isEnabled = false
        val progressbar= ProgressDialog(this)
        progressbar.setMessage("image is uploading please wait...")
        progressbar.show()
        val ref = storage.reference.child("uploads/" + auth.uid.toString())
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
                nextBtn.isEnabled = true
            } else {
                nextBtn.isEnabled = true
                // Handle failures
            }
        }.addOnFailureListener {

        }
        progressbar.dismiss()
    }



}