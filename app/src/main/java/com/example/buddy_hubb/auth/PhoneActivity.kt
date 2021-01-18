package com.example.buddy_hubb.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import com.example.buddy_hubb.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_phone.*

const val PHONE_NUMBER="ph"
class PhoneActivity : AppCompatActivity() {
    lateinit var countryCode:String
    lateinit var phoneNumber:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone)


        phoneNumber = phoneEt.text.toString()
        countryCode=ccp.selectedCountryCodeWithPlus


        phoneEt.addTextChangedListener {
            nextButton.isEnabled = !(it.isNullOrEmpty() || it.length < 10)
        }

        nextButton.setOnClickListener {
            phoneNumber = phoneEt.text.toString()
            countryCode=ccp.selectedCountryCodeWithPlus


            notifyUser()
        }


     }

    private fun notifyUser() {
        MaterialAlertDialogBuilder(this).apply {
            setMessage("We will be verifying the phone number:${countryCode+phoneNumber}\n"+ "Is it OK , Or would you like to edit the number ?")
            setPositiveButton("Ok") { _, _ ->
                showOtpActivity()
            }
            setNegativeButton("Edit") { dialog, which ->
                dialog.dismiss()
            }
            setCancelable(false)
            create()
            show()

        }
    }


    private fun showOtpActivity() {
        val i=Intent(this, OtpActivity::class.java)
        i.putExtra(PHONE_NUMBER,countryCode+phoneNumber)
        startActivity(i)
        finish()
    }




}
