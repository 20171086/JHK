package com.example.test5;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> itemList=new ArrayList<>();

    public RecyclerAdapter(ArrayList list){
        itemList.clear();
        itemList = list;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView item;
        public ViewHolder(View itemView){
            super(itemView);
            item=(TextView)itemView.findViewById(R.id.item);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recycler_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder rholder=(ViewHolder)holder;

        rholder.item.setText(itemList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
