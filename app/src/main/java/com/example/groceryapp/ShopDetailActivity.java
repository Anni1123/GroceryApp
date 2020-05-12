package com.example.groceryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groceryapp.activities.MainSellerActivity;
import com.example.groceryapp.adapter.AdapterCartItem;
import com.example.groceryapp.adapter.AdapterPoductUser;
import com.example.groceryapp.adapter.AdapterProductSeller;
import com.example.groceryapp.model.ModelProducts;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class ShopDetailActivity extends AppCompatActivity {

    private ImageView shoptv;
    private TextView shopName,phone,email,deliveryfee,openclose,address,filterptv;
    private ImageButton call,map,cart,back,filter;
    private EditText searchproduct;
    private RecyclerView product;
    private String shopuid;
    private FirebaseAuth firebaseAuth;
    public String deliveryFee;
    String shopname,shopphone,shopemails,hopaddress;
    private ArrayList<ModelProducts> productsArrayList;
    private AdapterPoductUser poductUser;
    private ArrayList<ModelCartItem> cartItem;
    private AdapterCartItem adapterCartItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);
        shoptv=findViewById(R.id.shoptv);
        shopName=findViewById(R.id.shopNAmetv);
        phone=findViewById(R.id.phonetv);
        email=findViewById(R.id.emailv);
        openclose=findViewById(R.id.openclosev);
        deliveryfee=findViewById(R.id.deliveryfeeev);
        address=findViewById(R.id.address);
        call=findViewById(R.id.callbtn);
        map=findViewById(R.id.mapbtn);
        cart=findViewById(R.id.cart);
        back=findViewById(R.id.backbtn);
        product=findViewById(R.id.productRv);
        filter=findViewById(R.id.filterbtn);
        filterptv=findViewById(R.id.filterProduct);
        searchproduct=findViewById(R.id.searchproduct);
        shopuid=getIntent().getStringExtra("shopuid");
        firebaseAuth=FirebaseAuth.getInstance();
        loadMyInfo();
        loadShopDetails();
        loadShopProducts();
        deleteCart();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCartDialog();
            }
        });
    }

    private void deleteCart() {
        EasyDB easyDB=EasyDB.init(this,"ITEMSDB")
                .setTableName("Items_Table")
                .addColumn(new Column("Items_id",new String[]{"text","unique"}))
                .addColumn(new Column("Items_pid",new String[]{"text","not null"}))
                .addColumn(new Column("Items_name",new String[]{"text","not null"}))
                .addColumn(new Column("Items_price_each",new String[]{"text","not null"}))
                .addColumn(new Column("Items_price",new String[]{"text","not null"}))
                .addColumn(new Column("Items_quantity",new String[]{"text","not null"}))
                .doneTableColumn();
        easyDB.deleteAllDataFromTable();
    }

    public double allTotalPrice=0;
    public TextView discountc,totalswd,allTotalPriceh;
    private void showCartDialog() {
        cartItem=new ArrayList<>();
        View view= LayoutInflater.from(this).inflate(R.layout.dialog_cart,null);
        TextView shopNametv=view.findViewById(R.id.shopNametv);
         allTotalPriceh=view.findViewById(R.id.totaltv);
        discountc=view.findViewById(R.id.discountc);
        RecyclerView cartitemtv=view.findViewById(R.id.cartitemtv);
       totalswd=view.findViewById(R.id.totalswd);

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setView(view);
        shopNametv.setText(shopname);
        EasyDB easyDB=EasyDB.init(this,"ITEMSDB")
                .setTableName("Items_Table")
                .addColumn(new Column("Items_id",new String[]{"text","unique"}))
                .addColumn(new Column("Items_pid",new String[]{"text","not null"}))
                .addColumn(new Column("Items_name",new String[]{"text","not null"}))
                .addColumn(new Column("Items_price_each",new String[]{"text","not null"}))
                .addColumn(new Column("Items_price",new String[]{"text","not null"}))
                .addColumn(new Column("Items_quantity",new String[]{"text","not null"}))
                .doneTableColumn();
        Cursor cursor=easyDB.getAllData();
        while (cursor.moveToNext()){
            String id=cursor.getString(1);
            String pid=cursor.getString(2);
            String name=cursor.getString(3);
            String price=cursor.getString(4);
            String cost=cursor.getString(5);
            String quantity=cursor.getString(6);

            allTotalPrice=allTotalPrice+Double.parseDouble(cost);
            ModelCartItem cartItemss=new ModelCartItem(""+id,
                    ""+pid,""+name
                    ,""+price,""+cost,
                    ""+quantity);
            cartItem.add(cartItemss);
        }
        adapterCartItem=new AdapterCartItem(cartItem,this);
        cartitemtv.setAdapter(adapterCartItem);
        discountc.setText("Rs"+deliveryFee);
        allTotalPriceh.setText("Rs"+allTotalPrice+Double.parseDouble(deliveryFee.replace("Rs","")));
        totalswd.setText("Rs"+allTotalPrice);

        AlertDialog dialog=builder.create();
        dialog.show();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                allTotalPrice=0;
            }
        });



    }

    private void loadShopProducts() {

        productsArrayList=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopuid).child("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productsArrayList.clear();
                for (DataSnapshot data1:dataSnapshot.getChildren()){
                    ModelProducts model=data1.getValue(ModelProducts.class);
                    productsArrayList.add(model);

                }
                poductUser=new AdapterPoductUser(ShopDetailActivity.this,productsArrayList);
                product.setAdapter(poductUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialPhone();
            }
        });
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMap();
            }
        });
        searchproduct.addTextChangedListener(new TextWatcher() {
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
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(ShopDetailActivity.this);
                builder.setTitle("Select Category")
                        .setItems(Constants.options1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String selected=Constants.options1[which];
                                filterptv.setText(selected);
                                if(selected.equals("All")){
                                    loadShopProducts();
                                }
                                else {
                                    loadFilterProducts(selected);
                                }
                            }
                        }).show();
            }
        });
    }
    private void loadFilterProducts(final String selected) {
        productsArrayList=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopuid).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        productsArrayList.clear();
                        for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                            String categories=""+dataSnapshot1.child("Category").getValue();
                            if(selected.toLowerCase().equals(categories.toLowerCase())) {
                                ModelProducts modelProduc = dataSnapshot1.getValue(ModelProducts.class);
                                productsArrayList.add(modelProduc);
                            }

                        }
                        poductUser=new AdapterPoductUser(ShopDetailActivity.this,productsArrayList);
                        product.setAdapter(poductUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadSearchProducts(final String valueOf) {
       productsArrayList=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopuid).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        productsArrayList.clear();
                        for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                            String categories=""+dataSnapshot1.child("Category").getValue();
                            String title=""+dataSnapshot1.child("title").getValue();
                            categories=categories.toLowerCase();
                            title=title.toLowerCase();
                            if(valueOf.toLowerCase().equals(categories.toLowerCase())|| valueOf.toLowerCase().equals(title.toLowerCase())) {
                                ModelProducts modelProduc = dataSnapshot1.getValue(ModelProducts.class);
                                productsArrayList.add(modelProduc);
                            }

                        }
                        poductUser=new AdapterPoductUser(ShopDetailActivity.this,productsArrayList);
                        product.setAdapter(poductUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void showMap() {
        String address="https://:maps.google.com/maps?saddr=";
        Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(address));
        startActivity(intent);
    }

    private void dialPhone() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(shopphone))));
        Toast.makeText(this,""+shopphone,Toast.LENGTH_LONG).show();
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

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void loadShopDetails() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name=""+dataSnapshot.child("name").getValue();
                shopname=""+dataSnapshot.child("shopName").getValue();
                shopphone=""+dataSnapshot.child("phone").getValue();
                shopemails=""+dataSnapshot.child("email").getValue();
                hopaddress=""+dataSnapshot.child("address").getValue();
                deliveryFee=""+dataSnapshot.child("delivery").getValue();
                String profile=""+dataSnapshot.child("profileImage").getValue();
                String shopopen=""+dataSnapshot.child("shopOpen").getValue();
                shopName.setText(shopname);
                email.setText(shopemails);
                phone.setText(shopphone);
                address.setText(hopaddress);
                deliveryfee.setText("Delivery Fee is $"+deliveryFee);
                if(shopopen.equals("true")){
                    openclose.setText("Open");
                }
                else {
                    openclose.setText("Closed");
                }
                try {
                    Picasso.get().load(profile).into(shoptv);
                }
                catch (Exception e){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
