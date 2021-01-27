package com.example.buddy_hubb.crypto;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.buddy_hubb.R;

public class Resetpassword extends AppCompatActivity {


    EditText old,newpwd,cnfpwd;
    Button save;
    String oldpassword,newpassword,cnfpassword;
    String pwd=AES.pwdtext.toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword);

        old=(EditText) findViewById(R.id.old);


        newpwd=(EditText) findViewById(R.id.newp);


        cnfpwd=(EditText) findViewById(R.id.cnfpwd);


        save=(Button) findViewById(R.id.save);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oldpassword=old.getText().toString();
                newpassword=newpwd.getText().toString();
                cnfpassword=cnfpwd.getText().toString();
                if(pwd.equals(oldpassword))
                {

                    if(newpassword.equals(cnfpassword))
                    {
                        AES.pwdtext=newpassword;
                        System.out.println("updated successfully");
                        Toast.makeText(com.example.buddy_hubb.crypto.Resetpassword.this,"Updated Successfully!!",Toast.LENGTH_LONG).show();
                        finish();
                    }
                    else
                    {
                        Toast.makeText(com.example.buddy_hubb.crypto.Resetpassword.this," password does not match",Toast.LENGTH_SHORT).show();
                        System.out.println("new password match ni karre");
                    }
                }
                else
                {
                    System.out.println(pwd);
                    Toast.makeText(com.example.buddy_hubb.crypto.Resetpassword.this," password does not match",Toast.LENGTH_SHORT).show();
                    System.out.println("purane password match ni karre");
                    System.out.println(oldpassword);
                }
            }
        });

    }
}
