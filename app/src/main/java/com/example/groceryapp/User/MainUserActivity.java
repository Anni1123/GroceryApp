package com.example.groceryapp.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.groceryapp.LoginActivity;
import com.example.groceryapp.ProfileUserEditActivity;
import com.example.groceryapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainUserActivity extends AppCompatActivity {

    TextView name;
    ImageButton logout,edit;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);
        name=findViewById(R.id.name);
        logout=findViewById(R.id.logout);
        edit=findViewById(R.id.edit);
        firebaseAuth=FirebaseAuth.getInstance();
        checkUSer();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUSer();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainUserActivity.this, ProfileUserEditActivity.class));
            }
        });
    }

    private void checkUSer() {
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user==null){
            startActivity(new Intent(MainUserActivity.this, LoginActivity.class));
            finish();
        }
        else {
            loadMyInfo();
        }
    }

    private void loadMyInfo() {

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("uid").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    String names=""+dataSnapshot1.child("name").getValue();
                    String accountType=""+dataSnapshot1.child("accountType").getValue();
                    name.setText(names);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
