package com.example.groceryapp;

import android.widget.Filter;

import com.example.groceryapp.adapter.AdapterProductSeller;
import com.example.groceryapp.model.ModelProducts;

import java.util.ArrayList;

public class FilterProduct extends Filter {
    private AdapterProductSeller adapter;

    public FilterProduct(AdapterProductSeller adapterProductSeller, ArrayList<ModelProducts> products) {
        this.adapter = adapterProductSeller;
        this.filterList = products;
    }

    private ArrayList<ModelProducts> filterList;

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();
        if(constraint!=null && constraint.length()>0){
            constraint=constraint.toString().toUpperCase();
            ArrayList<ModelProducts> filterModels=new ArrayList<>();
            for (int i=0; i<filterList.size(); i++){
                if(filterList.get(i).getTitle().toUpperCase().contains(constraint)||
                    filterList.get(i).getCategory().toUpperCase().contains(constraint) ){
                        filterModels.add(filterList.get(i));
                    }
                }
            results.count=filterModels.size();
            results.values=filterModels;
            }
        else {
            results.count=filterList.size();
            results.values=filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.modelProducts=(ArrayList<ModelProducts>)results.values;
        adapter.notifyDataSetChanged();
    }
}
