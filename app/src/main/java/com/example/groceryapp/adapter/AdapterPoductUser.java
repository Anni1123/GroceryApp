package com.example.groceryapp.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groceryapp.FilterProductUser;
import com.example.groceryapp.R;
import com.example.groceryapp.model.ModelProducts;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterPoductUser extends RecyclerView.Adapter<AdapterPoductUser.MyHolder> implements Filterable
{


    public AdapterPoductUser(Context context,ArrayList<ModelProducts> productsArrayList) {
        this.productsArrayList = productsArrayList;
        this.context = context;
        this.filterList=productsArrayList;

    }

    public ArrayList<ModelProducts> productsArrayList,filterList;
    private Context context;
    private FilterProductUser filter;

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_products_seller,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        ModelProducts products=productsArrayList.get(position);
        String id=products.getTimestamp();
        String uid=products.getProductId();
        String discountavailable=products.getDiscountAvailable();
        String discountnote=products.getDiscountnote();
        String discountPrice=products.getDiscountprice();
        String category=products.getCategory();
        String description=products.getDescription();
        String originalprice=products.getOriginalPrice();
        String icon=products.getProductIcon();
        String quantity=products.getQuantity();
        String title=products.getTitle();
        String timestamp=products.getTimestamp();
        holder.title.setText(title);
        holder.descrip.setText(description);
        holder.discountnote.setText(discountnote);
        holder.originalprice.setText(originalprice);
        holder.discountprice.setText(discountPrice);
        if(discountavailable.equals("true")){
            holder.discountprice.setVisibility(View.VISIBLE);
            holder.discountnote.setVisibility(View.VISIBLE);
            holder.originalprice.setPaintFlags(holder.originalprice.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {
            holder.discountprice.setVisibility(View.GONE);
            holder.discountnote.setVisibility(View.GONE);
            holder.originalprice.setPaintFlags(0);
        }
        try {
            Picasso.get().load(icon).into(holder.productIcon);
        }
        catch (Exception e){

        }
        holder.addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return productsArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null){
            filter=new FilterProductUser(this,filterList);
        }
        return null;
    }

    class MyHolder extends RecyclerView.ViewHolder{
        private ImageView productIcon;
        private TextView discountnote,title,descrip,addtocart,discountprice,originalprice;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            productIcon=itemView.findViewById(R.id.productIcontv);
            discountnote=itemView.findViewById(R.id.discountednote);
            title=itemView.findViewById(R.id.title);
            descrip=itemView.findViewById(R.id.descriptiont);
            addtocart=itemView.findViewById(R.id.addtocart);
            discountprice=itemView.findViewById(R.id.discount);
            originalprice=itemView.findViewById(R.id.originalprice);

        }
    }
}
