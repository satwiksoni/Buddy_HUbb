package com.example.buddy_hubb.auth

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.example.buddy_hubb.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_otp.*
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity() {
    lateinit var phoneNumber:String
    private var timeLeft: Long = -1

    private var mCounterDown: CountDownTimer? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var callbacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var storedVerificationId :String
    private lateinit var progressDialog: ProgressDialog
    lateinit var resendToken:PhoneAuthProvider.ForceResendingToken

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        sentcodeEt.addTextChangedListener {
            verificationBtn.isEnabled = !(it.isNullOrEmpty() || it.length<6)
        }

        initial_things()

        Toast.makeText(applicationContext,phoneNumber,Toast.LENGTH_LONG).show()



        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {


                val code=credential.smsCode
                if(code!=null)
                {
                    sentcodeEt.setText(code)
                }
                else

                    Toast.makeText(applicationContext,"Authantication failed!",Toast.LENGTH_LONG).show()

                if (::progressDialog.isInitialized) {
                    progressDialog.dismiss()
                }

            }


            override fun onVerificationFailed(e: FirebaseException) {
                if (::progressDialog.isInitialized) {
                    progressDialog.dismiss()
                }
                Toast.makeText(applicationContext,"Auth failed",Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                if (::progressDialog.isInitialized) {
                    progressDialog.dismiss()
                }
                Toast.makeText(applicationContext,"Code has beed sent !",Toast.LENGTH_LONG).show()

                storedVerificationId = verificationId
                resendToken = token

            }
        }

        //here is sending verification
       sendVerificationCode(phoneNumber)

        //Now otp is set on otp et sooo now we will check it and call other activity

        verificationBtn.setOnClickListener {
            var otp= sentcodeEt.text.toString().trim()
            if(otp.isNotEmpty())
            {
                verifyVerificationCode(otp)
            }
            else
                Toast.makeText(applicationContext,"Wrong OTP",Toast.LENGTH_LONG).show()
        }

    }
    override fun onBackPressed() {

    }



    private fun initial_things() {
        phoneNumber=intent.getStringExtra(PHONE_NUMBER).toString()
        auth= FirebaseAuth.getInstance()
        verifyTv.text=getString(R.string.verify,phoneNumber)
        setSpannableString()
        showTimer(60000)

    }
    private fun setSpannableString() {
        val span = SpannableString(getString(R.string.Waiting_text, phoneNumber))
        val clickSpan: ClickableSpan = object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                ds.color = ds.linkColor // you can use custom color
                ds.isUnderlineText = false // this remove the underline
            }

            override fun onClick(textView: View) { // handle click event
                showLoginActivity()
            }
        }

        span.setSpan(clickSpan, span.length - 13, span.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        waitingTv.movementMethod = LinkMovementMethod.getInstance()
        waitingTv.text = span
    }

    private fun sendVerificationCode(phoneNo:String)
    {

        Toast.makeText(applicationContext,"ver 1",Toast.LENGTH_LONG).show()
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNo,
            60,
            TimeUnit.SECONDS,
            this,
            callbacks
        )

    }


    private fun verifyVerificationCode(code:String)
    {
        val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)
        signUp(credential)
    }

    private fun signUp(credential: PhoneAuthCredential)
    {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    Toast.makeText(applicationContext,"Successful",Toast.LENGTH_LONG).show()
                    val intent= Intent(applicationContext, SigninActivity::class.java)
                    startActivity(intent)
                    finish()


                }
                else {

                    if (task.exception is FirebaseAuthInvalidCredentialsException)
                    {
                        Toast.makeText(applicationContext,"Code is incorrect",Toast.LENGTH_LONG).show()
                        sentcodeEt.setText("")
                    }

                }
            }



    }


    private fun showLoginActivity() {
        startActivity(
            Intent(this, PhoneActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    }



    private fun showTimer(milliesInFuture: Long) {
        resendBtn.isEnabled = false
        mCounterDown = object : CountDownTimer(milliesInFuture, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished
                counterTv.isVisible = true
                counterTv.text = "Seconds remaining: " + millisUntilFinished / 1000


                //here you can have your logic to set text to edittext
            }

            override fun onFinish() {
                resendBtn.isEnabled = true
                counterTv.isVisible = false
            }
        }.start()
    }
    override fun onDestroy() {
        super.onDestroy()
        if (mCounterDown != null) {
            mCounterDown!!.cancel()
        }
    }


    private fun resendVerificationCode(
        phoneNumber: String,
        mResendToken: PhoneAuthProvider.ForceResendingToken?
    ) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber, // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this, // Activity (for callback binding)
            callbacks, // OnVerificationStateChangedCallbacks
            mResendToken
        ) // ForceResendingToken from callbacks
    }

}
fun Context.createProgressDialog(message: String, isCancelable: Boolean): ProgressDialog {
    return ProgressDialog(this).apply {
        setCancelable(isCancelable)
        setCanceledOnTouchOutside(false)
        setMessage(message)
    }
}
//progressDialog = createProgressDialog("Detecting Verification Code", false)
//progressDialog.show()
