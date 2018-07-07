package com.example.prarthana.myapplication_restaurant;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

public class ResultsRecyclerAdapter extends RecyclerView.Adapter<ResultsRecyclerAdapter.ResultAdapterViewHolder> implements Serializable {

    final private Context mContext;
    ResultAdapterViewHolder holder;
    ArrayList<Results> results;


    public ResultsRecyclerAdapter(Context Context, ArrayList<Results> results) {
        mContext = Context;
        this.results=results;
    }

    @NonNull
    @Override
    public ResultAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (parent instanceof RecyclerView) {
            //added volley in gradle..only then result_list_item was accepted
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_list_item, parent, false);
            holder = new ResultAdapterViewHolder(view);
            return holder;
        } else {
            throw new RuntimeException("Not bound to RecyclerView");
        }
    }



    @Override
    public void onBindViewHolder(@NonNull ResultAdapterViewHolder holder, int position) {
        Results result= results.get(position);
        holder.name.setText(result.getName());
       // holder.address.setText(result.getAddress());
        Picasso.with(mContext).load(result.getIcon()).into(holder.imageView);
        //added picasso in gradle
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public void addAll(ArrayList<Results> results) {
        this.results=results;
    }

    public class ResultAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
       // TextView address;
        ImageView imageView;


        public ResultAdapterViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.nameOfRest);
           // address = (TextView) view.findViewById(R.id.adressofRest);
            imageView = view.findViewById(R.id.icon);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Results result = results.get(getAdapterPosition());

            Intent intent = new Intent(mContext, Detail.class);
            intent.putExtra("image", result.getIcon());
            intent.putExtra("name", result.getName());
            intent.putExtra("address", result.getAddress());
            intent.putExtra("rating", result.getRating());
           // intent.putExtra("Results", (Parcelable) result);
             mContext.startActivity(intent);

        }
    }
}
