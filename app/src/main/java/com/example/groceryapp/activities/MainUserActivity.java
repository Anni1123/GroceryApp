package com.example.groceryapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.groceryapp.R;
import com.example.groceryapp.adapter.AdapterShop;
import com.example.groceryapp.model.ModelShop;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainUserActivity extends AppCompatActivity {

    TextView name,email,phone,tabshops,tabordrer;
    ImageButton logout,edit;
    ImageView profile;
    RecyclerView recyclerView;
    FirebaseAuth firebaseAuth;
    ArrayList<ModelShop> modelShops;
    AdapterShop adapterShop;
    RelativeLayout orderslayout,shoplayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);
        name=findViewById(R.id.name);
        logout=findViewById(R.id.logout);
        edit=findViewById(R.id.edit);
        recyclerView=findViewById(R.id.recycle);
        profile=findViewById(R.id.profiletv);
        phone=findViewById(R.id.phones);
        tabshops=findViewById(R.id.tabshops);
        tabordrer=findViewById(R.id.taborders);
        email=findViewById(R.id.email);
        firebaseAuth=FirebaseAuth.getInstance();
        orderslayout=findViewById(R.id.orderlayout);
        shoplayout=findViewById(R.id.shopslayout);
        recyclerView=findViewById(R.id.shoprv);
        checkUSer();
        showProductUi();
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
        tabordrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrdersUi();
            }
        });
        tabshops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProductUi();
            }
        });
    }

    private void showProductUi() {
            shoplayout.setVisibility(View.VISIBLE);
            orderslayout.setVisibility(View.GONE);
            tabshops.setTextColor(getResources().getColor(R.color.colorBlack));
            tabshops.setBackgroundResource(R.drawable.shape_rec04);
            tabordrer.setTextColor(getResources().getColor(R.color.colorWhite));
            tabordrer.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showOrdersUi() {
        shoplayout.setVisibility(View.GONE);
        orderslayout.setVisibility(View.VISIBLE);
        tabshops.setTextColor(getResources().getColor(R.color.colorWhite));
        tabshops.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        tabordrer.setTextColor(getResources().getColor(R.color.colorBlack));
        tabordrer.setBackgroundResource(R.drawable.shape_rec04);
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
                    String emails=""+dataSnapshot1.child("email").getValue();
                    String phones=""+dataSnapshot1.child("phone").getValue();
                    String profileImage=""+dataSnapshot1.child("profileImage").getValue();
                    String city=""+dataSnapshot1.child("city").getValue();
                    name.setText(names);
                    email.setText(emails);
                    phone.setText(phones);
                    try {
                        Picasso.get().load(profileImage).into(profile);
                    }
                    catch (Exception e){

                    }
                    loadShops(city);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadShops(final String city) {
        modelShops=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("accountType").equalTo("seller").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelShops.clear();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    ModelShop shop=dataSnapshot1.getValue(ModelShop.class);
                    String shopcity=""+ dataSnapshot1.child("city").getValue();
                    if (shopcity.equals(city)){
                        modelShops.add(shop);
                    }
                    adapterShop=new AdapterShop(MainUserActivity.this,modelShops);
                    recyclerView.setAdapter(adapterShop);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
