package com.example.groceryapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groceryapp.R;
import com.example.groceryapp.ShopDetailActivity;
import com.example.groceryapp.model.ModelShop;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterShop extends RecyclerView.Adapter<AdapterShop.MyHolder>{

    Context context;

    public AdapterShop(Context context, ArrayList<ModelShop> modelShops) {
        this.context = context;
        this.modelShops = modelShops;
    }

    ArrayList<ModelShop> modelShops;

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_shop,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        ModelShop shop=modelShops.get(position);
        String accounttype=shop.getAccountType();
        String adress=shop.getAddress();
        String city=shop.getCity();
        String country=shop.getCountry();
        String deliveryfee=shop.getDelivery();
        String email=shop.getEmail();
        String online=shop.getOnline();
        String name=shop.getName();
        String phone=shop.getPhone();
        final String uid=shop.getUid();
        String timestamp=shop.getTimestamp();
        String shopopen=shop.getShopOpen();
        String state=shop.getState();
        String profileImage=shop.getProfileImage();
        String shopname=shop.getShopName();

        holder.shopname.setText(shopname);
        holder.shopphone.setText(phone);
        holder.address.setText(adress);
        if(online.equals("true")){
            holder.online.setVisibility(View.VISIBLE);
        }
        else {
            holder.online.setVisibility(View.INVISIBLE);
        }
        if(shopopen.equals("true")){
            holder.shopclosed.setVisibility(View.GONE);
        }
        else {
            holder.shopclosed.setVisibility(View.VISIBLE);
        }
        try {
            Picasso.get().load(profileImage).into(holder.shoptv);
        }
        catch (Exception e){

        }
    holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(context, ShopDetailActivity.class);
            intent.putExtra("shopuid",uid);
            context.startActivity(intent);
        }
    });
    }

    @Override
    public int getItemCount() {
        return modelShops.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        ImageView online,shoptv;
        TextView shopclosed,shopphone,shopname,address;
        RatingBar ratingBar;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            online=itemView.findViewById(R.id.onlinetv);
            shoptv=itemView.findViewById(R.id.shoptv);
            shopclosed=itemView.findViewById(R.id.shopClosed);
            shopphone=itemView.findViewById(R.id.shopno);
            shopname=itemView.findViewById(R.id.shopname);
            address=itemView.findViewById(R.id.address);
            ratingBar=itemView.findViewById(R.id.ratingbar);
        }
    }
}
