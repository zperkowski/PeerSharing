package com.zperkowski.peersharing;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class SimpleCardAdapter extends RecyclerView.Adapter<SimpleCardAdapter.CardViewHolder> {
    final static private String TAG = "SimpleCardAdapter";

    private List<Phone> algorithms;
    private Context context;

    public static class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final static private String TAG = "CardViewHolder";

        Context context;

        CardView cardView;
        TextView name;
        TextView ip;

        public CardViewHolder(View itemView, Context context) {
            super(itemView);
            Log.d(TAG, "CardViewHolder()");
            itemView.setOnClickListener(this);
            this.context = context;
            cardView = itemView.findViewById(R.id.cv);
            name = itemView.findViewById(R.id.row_name);
            ip = itemView.findViewById(R.id.row_ip);
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick() " + String.valueOf(ip.getText()));
            Intent intent = new Intent(itemView.getContext(), NetworkService.class);
            intent.setAction(NetworkService.ACTION_GETFILES);
            intent.putExtra(NetworkService.EXTRA_IP, ip.getText());
            context.startService(intent);
        }
    }

    SimpleCardAdapter(List<Phone> algorithms, Context context) {
        Log.d(TAG, "SimpleCardAdapter()");
        this.algorithms = algorithms;
        this.context = context;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder()");
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_card_item, parent, false);
        return new CardViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder()");
        holder.name.setText(algorithms.get(position).getName());
        holder.ip.setText(algorithms.get(position).getAddress().toString());
    }

    @Override
    public int getItemCount() {
        //Log.d(TAG, "getItemCount()");
        return algorithms.size();
    }
}
