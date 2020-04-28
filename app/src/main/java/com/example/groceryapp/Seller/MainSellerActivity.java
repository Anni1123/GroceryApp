package com.example.groceryapp.Seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.groceryapp.AdapterProductSeller;
import com.example.groceryapp.AddProductActivity;
import com.example.groceryapp.Constants;
import com.example.groceryapp.LoginActivity;
import com.example.groceryapp.ModelProducts;
import com.example.groceryapp.ProfieEditSellerActivity;
import com.example.groceryapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainSellerActivity extends AppCompatActivity {

    TextView name,shopname,email,order,products,filetrrt;
    ImageButton logout,edit,add,filter;
    ImageView profile;
    RecyclerView recyclerView;
    EditText search;
    FirebaseAuth firebaseAuth;
    RelativeLayout orderslayout,productlayout;

    ArrayList<ModelProducts> modelProducts;
    AdapterProductSeller productSeller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_seller);
        name=findViewById(R.id.name);
        logout=findViewById(R.id.logout);
        edit=findViewById(R.id.edit);
        filter=findViewById(R.id.filterProduct);
        search=findViewById(R.id.search);
        filetrrt=findViewById(R.id.filterptv);
        add=findViewById(R.id.addp);
        recyclerView=findViewById(R.id.recycle);
        profile=findViewById(R.id.profiletv);
        order=findViewById(R.id.taborders);
        email=findViewById(R.id.emails);
        shopname=findViewById(R.id.shopname);
        products=findViewById(R.id.tabproducts);
        orderslayout=findViewById(R.id.orderlayout);
        productlayout=findViewById(R.id.productslayout);
        firebaseAuth= FirebaseAuth.getInstance();
        checkUSer();
        showProductsUi();
        loadProducts();
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                loadSearchProducts(String.valueOf(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
                startActivity(new Intent(MainSellerActivity.this, ProfieEditSellerActivity.class));
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainSellerActivity.this, AddProductActivity.class));
            }
        });
        products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProductsUi();
            }
        });
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrdersUi();
            }
        });
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(MainSellerActivity.this);
                builder.setTitle("Select Category")
                        .setItems(Constants.options1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String selected=Constants.options1[which];
                                filetrrt.setText(selected);
                                if(selected.equals("All")){
                                    loadProducts();
                                }
                                else {
                                    loadFilterProducts(selected);
                                }
                            }
                        }).show();
            }
        });
    }

    private void loadSearchProducts(final String valueOf) {
        modelProducts=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        modelProducts.clear();
                        for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                            String categories=""+dataSnapshot1.child("Category").getValue();
                            String title=""+dataSnapshot1.child("title").getValue();
                            categories=categories.toLowerCase();
                            title=title.toLowerCase();
                            if(valueOf.equals(categories)||valueOf.equals(title)) {
                                ModelProducts modelProduc = dataSnapshot1.getValue(ModelProducts.class);
                                modelProducts.add(modelProduc);
                            }

                        }
                        productSeller=new AdapterProductSeller(MainSellerActivity.this,modelProducts);
                        recyclerView.setAdapter(productSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadFilterProducts(final String selected) {
        modelProducts=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        modelProducts.clear();
                        for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                            String categories=""+dataSnapshot1.child("Category").getValue();
                            if(selected.equals(categories)) {
                                ModelProducts modelProduc = dataSnapshot1.getValue(ModelProducts.class);
                                modelProducts.add(modelProduc);
                            }

                        }
                        productSeller=new AdapterProductSeller(MainSellerActivity.this,modelProducts);
                        recyclerView.setAdapter(productSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadProducts() {
        modelProducts=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        modelProducts.clear();
                        for (DataSnapshot data1:dataSnapshot.getChildren()){
                            ModelProducts model=data1.getValue(ModelProducts.class);
                            modelProducts.add(model);

                        }
                        productSeller=new AdapterProductSeller(MainSellerActivity.this,modelProducts);
                        recyclerView.setAdapter(productSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void showProductsUi() {
        productlayout.setVisibility(View.VISIBLE);
        orderslayout.setVisibility(View.GONE);
        products.setTextColor(getResources().getColor(R.color.colorBlack));
        products.setBackgroundResource(R.drawable.shape_rec04);
        order.setTextColor(getResources().getColor(R.color.colorWhite));
        order.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showOrdersUi() {
        productlayout.setVisibility(View.GONE);
        orderslayout.setVisibility(View.VISIBLE);
        products.setTextColor(getResources().getColor(R.color.colorWhite));
        products.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        order.setTextColor(getResources().getColor(R.color.colorBlack));
        order.setBackgroundResource(R.drawable.shape_rec04);
    }



    private void checkUSer() {
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user==null){
            startActivity(new Intent(MainSellerActivity.this, LoginActivity.class));
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
                    String emaill=""+dataSnapshot1.child("email").getValue();
                    String shopnames=""+dataSnapshot1.child("shopName").getValue();
                    String profiles=""+dataSnapshot1.child("profileImage").getValue();
                    name.setText(names );
                    email.setText(emaill);
                    shopname.setText(shopnames);
                    try {
                        Picasso.get().load(profiles).into(profile);
                    }
                    catch (Exception e){

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
