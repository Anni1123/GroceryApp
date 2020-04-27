package com.example.groceryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {

    ImageButton back;
    EditText email;
    Button recover;
    private FirebaseAuth firebaseAuth;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        back=findViewById(R.id.backbtnuser);
        email=findViewById(R.id.emailuser);
        recover=findViewById(R.id.recoverbtn);
        firebaseAuth=FirebaseAuth.getInstance();
        dialog=new ProgressDialog(this);
        dialog.setTitle("Please Wait...");
        dialog.setCanceledOnTouchOutside(false);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        recover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoverPassword();
            }
        });
    }

    String emaill;
    private void recoverPassword() {
        emaill=email.getText().toString().trim();

        if(!Patterns.EMAIL_ADDRESS.matcher(emaill).matches()) {
            Toast.makeText(ForgetPasswordActivity.this,"Email Cant be Empty",Toast.LENGTH_LONG).show();
            return;
        }
        dialog.setMessage("Sending Instruction to reset");
        dialog.show();
        firebaseAuth.sendPasswordResetEmail(emaill)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.dismiss();
                        Toast.makeText(ForgetPasswordActivity.this,"Reset Insytruction Sent...",Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(ForgetPasswordActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
}
