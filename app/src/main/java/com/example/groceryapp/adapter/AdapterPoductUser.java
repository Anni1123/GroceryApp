package com.example.groceryapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groceryapp.FilterProductUser;
import com.example.groceryapp.R;
import com.example.groceryapp.model.ModelProducts;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

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

        final ModelProducts products=productsArrayList.get(position);
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

                showQuantityDialog(products);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private double cont=0;
    private double finalCost=0;
    private int quantitys=0;

    private void showQuantityDialog(ModelProducts products) {
        View view=LayoutInflater.from(context).inflate(R.layout.disalog_quantity,null);
        ImageView productTv=view.findViewById(R.id.producttv);
        final TextView titletv=view.findViewById(R.id.titletv);
        TextView quantiy=view.findViewById(R.id.quantiy);
        TextView description=view.findViewById(R.id.description);
        TextView discountnotetv=view.findViewById(R.id.discountnotetv);
        final TextView originalprice=view.findViewById(R.id.originalprice);
        final TextView finaltv=view.findViewById(R.id.finaltv);
        TextView pricedicounttv=view.findViewById(R.id.pricedicounttv);
        final TextView quantitytv=view.findViewById(R.id.quantitytv);
        ImageButton decrement=view.findViewById(R.id.decrementbtn);
        ImageButton increment=view.findViewById(R.id.incrementbtn);
        Button addtocart=view.findViewById(R.id.cartbtn);


        String icon=products.getProductIcon();
        String discountnote=products.getDiscountnote();
        String descriptionp=products.getDescription();
        final String quantity=products.getQuantity();
        String title=products.getTitle();
        final String uid=products.getProductId();
        final String price;

        if(products.getDiscountAvailable().equals("true")){
            price=products.getDiscountprice();
            discountnotetv.setVisibility(View.VISIBLE);
            originalprice.setPaintFlags(originalprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }
        else {
            discountnotetv.setVisibility(View.INVISIBLE);
            pricedicounttv.setVisibility(View.INVISIBLE);
            price=products.getOriginalPrice();
        }
        cont=Double.parseDouble(price.replaceAll("Rs",""));
        finalCost=Double.parseDouble(price.replaceAll("Rs",""));
        quantitys=1;
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setView(view);
        try {

            Picasso.get().load(icon).into(productTv);
        }
        catch (Exception e){

        }
        titletv.setText(title);
        quantitytv.setText(quantity);
        description.setText(descriptionp);
        discountnotetv.setText(discountnote);
        quantitytv.setText(quantity);
        originalprice.setText("Rs"+products.getOriginalPrice());
        pricedicounttv.setText("Rs"+products.getDiscountprice());
        finaltv.setText("Rs"+finalCost);

        final AlertDialog dialog=builder.create();
        dialog.show();
        increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalCost=finalCost+cont;
                quantitys++;
                finaltv.setText("Rs"+finalCost);
                quantitytv.setText(" "+ quantity);
            }
        });
        decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantitys > 1) {
                    finalCost = finalCost - cont;
                    quantitys--;
                    finaltv.setText("Rs" + finalCost);
                    quantitytv.setText(" " + quantity);
                }
            }
        });
        addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title=titletv.getText().toString().trim();
                String priceEach=price;
                String totalprice=finaltv.getText().toString().trim().replace("Rs","");
                String quantity=quantitytv.getText().toString().trim();
                addtoCart(uid,title,priceEach,totalprice,quantity);
                dialog.dismiss();
            }
        });
    }

    private int itemid=1;

    private void addtoCart(String uid, String title, String priceEach, String price, String quantity) {
        itemid++;
        EasyDB easyDB=EasyDB.init(context,"ITEMSDB")
                .setTableName("Items_Table")
                .addColumn(new Column("Items_id",new String[]{"text","unique"}))
                .addColumn(new Column("Items_pid",new String[]{"text","not null"}))
                .addColumn(new Column("Items_name",new String[]{"text","not null"}))
                .addColumn(new Column("Items_price_each",new String[]{"text","not null"}))
                .addColumn(new Column("Items_price",new String[]{"text","not null"}))
                .addColumn(new Column("Items_quantity",new String[]{"text","not null"}))
                .doneTableColumn();
        Boolean b=easyDB.addData("Items_id",itemid)
                .addData("Items_pid",uid)
                .addData("Items_name",title)
                .addData("Items_price_each",priceEach)
                .addData("Items_price",price)
                .addData("Items_quantity",quantity).doneDataAdding();
        if(b) {
            Toast.makeText(context, "Added to cart", Toast.LENGTH_LONG).show();
        }

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
