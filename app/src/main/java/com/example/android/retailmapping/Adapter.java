package com.example.android.retailmapping;

/**
 * Created by Shaakya on 9/1/2017.
 */


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.LayoutInflater;

import java.util.Collections;
import java.util.List;


/**
 * Created by Shaakya on 4/25/2017.
 */

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {
    List<Information> data = Collections.emptyList();
    private LayoutInflater inflater;

    public Adapter(Context context, List<Information> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.customrow, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Adapter.MyViewHolder holder, int position) {
        Information current = data.get(position);
        holder.title.setText(current.title);
        holder.image.setImageResource(current.iconId);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.listText);
            image = (ImageView) itemView.findViewById(R.id.listIcon);
        }

    }
}
