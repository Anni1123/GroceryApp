package com.example.groceryapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groceryapp.ModelCartItem;
import com.example.groceryapp.R;
import com.example.groceryapp.ShopDetailActivity;

import java.util.List;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterCartItem extends RecyclerView.Adapter<AdapterCartItem.HodelCart> {

    public AdapterCartItem(List<ModelCartItem> cartItems, Context context) {
        this.cartItems = cartItems;
        this.context = context;
    }

    List<ModelCartItem> cartItems;
    private Context context;

    @NonNull
    @Override
    public HodelCart onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cartiem,parent,false);
        return new HodelCart(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HodelCart holder, final int position) {

        ModelCartItem item=cartItems.get(position);
        final String id=item.getId();
        String pid=item.getPid();
        String title=item.getName();
        final String cost=item.getCost();
        String price=item.getPrice();
        String quantity=item.getQuantity();
        holder.itemtitletv.setText(title);
        holder.itempricetv.setText(cost);
        holder.itempriceeach.setText(price);
        holder.itemquantity.setText(quantity);
        holder.itemremove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyDB easyDB=EasyDB.init(context,"ITEMSDB")
                        .setTableName("Items_Table")
                        .addColumn(new Column("Items_id",new String[]{"text","unique"}))
                        .addColumn(new Column("Items_pid",new String[]{"text","not null"}))
                        .addColumn(new Column("Items_name",new String[]{"text","not null"}))
                        .addColumn(new Column("Items_price_each",new String[]{"text","not null"}))
                        .addColumn(new Column("Items_price",new String[]{"text","not null"}))
                        .addColumn(new Column("Items_quantity",new String[]{"text","not null"}))
                        .doneTableColumn();
                easyDB.deleteRow(1,id);
                Toast.makeText(context,"Deleted",Toast.LENGTH_LONG).show();
                cartItems.remove(position);
                notifyItemChanged(position);
                notifyDataSetChanged();
                double tx=Double.parseDouble((((ShopDetailActivity)context).allTotalPriceh.getText().toString().trim().replace("Rs","")));
                double totalprice=tx-Double.parseDouble(cost.replace("Rs",""));
                double delifee=Double.parseDouble(((ShopDetailActivity)context).deliveryFee.replace("Rs",""));
                double totalprices=Double.parseDouble(String.format("%.2f",totalprice))- Double.parseDouble(String.format("%.2f",delifee));
                (((ShopDetailActivity)context)).allTotalPrice=0.00;
                ((ShopDetailActivity)context).totalswd.setText("Rs"+String.format("%.2f",totalprices));
                ((ShopDetailActivity)context).allTotalPriceh.setText("Rs"+String.format("%.2f",Double.parseDouble(String.format("%.2f",totalprice))));

            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class HodelCart extends RecyclerView.ViewHolder{

        private TextView itemtitletv,itempricetv,itempriceeach,
                itemquantity,itemremove;
        public HodelCart(@NonNull View itemView) {
            super(itemView);
            itemtitletv=itemView.findViewById(R.id.itemtitletv);
            itempricetv=itemView.findViewById(R.id.itempricetv);
            itempriceeach=itemView.findViewById(R.id.itempriceeach);
            itemquantity=itemView.findViewById(R.id.itemquantity);
            itemremove=itemView.findViewById(R.id.itemremove);

        }
    }
}
