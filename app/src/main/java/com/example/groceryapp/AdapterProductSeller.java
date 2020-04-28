package com.example.groceryapp;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdapterProductSeller extends RecyclerView.Adapter<AdapterProductSeller.MyHolder> implements Filterable {

    private Context context;
    private FilterProduct product;

    public AdapterProductSeller(Context context, ArrayList<ModelProducts> modelProducts) {
        this.context = context;
        this.modelProducts = modelProducts;
        this.filterlist=modelProducts;
    }

    public ArrayList<ModelProducts> modelProducts,filterlist;
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_products,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

       final ModelProducts products=modelProducts.get(position);
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
        holder.quantity.setText(quantity);
        holder.discountnote.setText(discountnote);
        holder.discountprice.setText(discountPrice);
        holder.originalprice.setText(originalprice);
        if(discountavailable.equals("true")){
            holder.discountprice.setVisibility(View.VISIBLE);
            holder.discountnote.setVisibility(View.VISIBLE);
            holder.originalprice.setPaintFlags(holder.originalprice.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {
            holder.discountprice.setVisibility(View.GONE);
            holder.discountnote.setVisibility(View.GONE);
        }
        try {
            Picasso.get().load(icon).into(holder.productImage);
        }
        catch (Exception e){

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailsBottomSheet(products);
            }
        });
    }

    private void detailsBottomSheet(ModelProducts products) {
        final BottomSheetDialog sheetDialog=new BottomSheetDialog(context);
        View view=LayoutInflater.from(context).inflate(R.layout.bs_product_detail_seller,null);
        sheetDialog.setContentView(view);
        ImageButton back=view.findViewById(R.id.backbtnuser);
        ImageButton edit=view.findViewById(R.id.edit);
        ImageButton delete=view.findViewById(R.id.delete);
        ImageView profile=view.findViewById(R.id.iconeuser);
        TextView discountnote=view.findViewById(R.id.discountednote);
        TextView title=view.findViewById(R.id.title);
        TextView desc=view.findViewById(R.id.description);
        TextView category=view.findViewById(R.id.category);
        TextView quantity=view.findViewById(R.id.quantity);
        TextView discount=view.findViewById(R.id.discount);
        TextView originalprice=view.findViewById(R.id.originalprice);

        String id=products.getTimestamp();
        String uid=products.getProductId();
        String discountavailable=products.getDiscountAvailable();
        String discountnotes=products.getDiscountnote();
        String discountPrice=products.getDiscountprice();
        String categories=products.getCategory();
        String description=products.getDescription();
        String originalpric=products.getOriginalPrice();
        String icon=products.getProductIcon();
        String quantitites=products.getQuantity();
        String titles=products.getTitle();
        String timestamp=products.getTimestamp();
        title.setText(titles);
        desc.setText(description);
        category.setText(categories);
        quantity.setText(quantitites);
        discount.setText(discountnotes);
        discountnote.setText(discountnotes);
        originalprice.setText(originalpric);
        if(discountavailable.equals("true")){
            discount.setVisibility(View.VISIBLE);
            discountnote.setVisibility(View.VISIBLE);
            originalprice.setPaintFlags(originalprice.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {
            discount.setVisibility(View.GONE);
            discountnote.setVisibility(View.GONE);
        }
        try {
            Picasso.get().load(icon).into(profile);
        }
        catch (Exception e){

        }
        sheetDialog.show();
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetDialog.dismiss();
            }
        });
    }



    @Override
    public int getItemCount() {
        return modelProducts.size();
    }

    @Override
    public Filter getFilter() {
        if(product==null){
            product=new FilterProduct(this,filterlist);
        }
        return null;
    }

    class MyHolder extends RecyclerView.ViewHolder{

        ImageView productImage;
        TextView discountnote,title,quantity,discountprice
                ,originalprice;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            productImage=itemView.findViewById(R.id.productIcontv);
            discountnote=itemView.findViewById(R.id.discountednote);
            title=itemView.findViewById(R.id.title);
            quantity=itemView.findViewById(R.id.quantity);
            discountprice=itemView.findViewById(R.id.discount);
            originalprice=itemView.findViewById(R.id.originalprice);
        }
    }
}
