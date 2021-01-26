package com.example.buddy_hubb.auth

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.buddy_hubb.MainActivity
import com.example.buddy_hubb.R
import com.example.buddy_hubb.models.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_signin.*

class SigninActivity : AppCompatActivity() {
    val storage by lazy {
        FirebaseStorage.getInstance()
    }
    val database by lazy {
        FirebaseFirestore.getInstance()
    }
    val auth by lazy {
        FirebaseAuth.getInstance()
    }
    lateinit var downloadUrl:String
    lateinit var phoneNumber:String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_signin)

        phoneNumber= intent.getStringExtra("phone").toString()

        userImgView.setOnClickListener {
            createPermissionForImage()
        }


        nextBtn.setOnClickListener {
            nextBtn.isEnabled=false
            val name:String=nameEt.text.toString()
            if(name.isEmpty())
            {
                Toast.makeText(this,"Name should not be empty..!",Toast.LENGTH_LONG).show()
            }
            else if(!::downloadUrl.isInitialized)
                Toast.makeText(this,"Image should not be empty..!",Toast.LENGTH_LONG).show()
            else
            {
                val user= User(name,downloadUrl,downloadUrl,auth.uid!!,phoneNumber)
                database.collection("user").document(auth.uid!!).set(user).addOnSuccessListener {
                    startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
                    finish()
                }.addOnFailureListener{
                    nextBtn.isEnabled=true
                    Toast.makeText(this,"Failed try again..!",Toast.LENGTH_LONG).show()
                }



            }

        }




        
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
                userImgView.setImageURI(it)
                startUpload(it)
            }
        }
    }

    private fun startUpload(filePath: Uri) {
        nextBtn.isEnabled = false
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
    }





    fun animate()
    {
        val animDrawable=signin.background as AnimationDrawable
        animDrawable.setEnterFadeDuration(10)
        animDrawable.setExitFadeDuration(5000)
        animDrawable.start()
    }
}