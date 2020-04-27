package com.example.groceryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    ImageView icon;
    EditText email,password;
    TextView forgep,noaccount;
    Button login;
    private FirebaseAuth firebaseAuth;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        icon=findViewById(R.id.iconeuser);
        email=findViewById(R.id.emailuser);
        password=findViewById(R.id.passworduser);
        forgep=findViewById(R.id.forgetp);
        firebaseAuth=FirebaseAuth.getInstance();
        dialog=new ProgressDialog(this);
        dialog.setTitle("Please Wait...");
        dialog.setCanceledOnTouchOutside(false);
        noaccount=findViewById(R.id.regsierseller);
        login=findViewById(R.id.login);
        noaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(
                        LoginActivity.this,RegisterUserActivity.class));
            }
        });
        forgep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ForgetPasswordActivity.class));
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

    }

    String emailuser,passworduser;
    private void loginUser() {
        emailuser=email.getText().toString().trim();
        passworduser=password.getText().toString().trim();
        if(!Patterns.EMAIL_ADDRESS.matcher(emailuser).matches()) {
            Toast.makeText(LoginActivity.this,"Email Cant be Empty",Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(passworduser)){
            Toast.makeText(LoginActivity.this,"Password Cant be Empty",Toast.LENGTH_LONG).show();
            return;
        }
        dialog.setMessage("Logging In....");
        dialog.show();
        firebaseAuth.signInWithEmailAndPassword(emailuser,passworduser)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        makeMeOnline();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(LoginActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
                return;
            }
        });
    }

    private void makeMeOnline() {
        dialog.setMessage("Checking User");
        dialog.show();
        HashMap<String ,Object> hashMap=new HashMap<>();
        hashMap.put("online","true");
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        checkUserType();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(LoginActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void checkUserType() {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("uid").equalTo(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    String accountType=""+dataSnapshot1.child("accountType").getValue();
                    if (accountType.equals("seller")){
                        dialog.dismiss();
                        startActivity(new Intent(LoginActivity.this,MainSellerActivity.class));
                        finish();
                    }
                    else {
                        dialog.dismiss();
                        startActivity(new Intent(LoginActivity.this,MainUserActivity.class));
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
