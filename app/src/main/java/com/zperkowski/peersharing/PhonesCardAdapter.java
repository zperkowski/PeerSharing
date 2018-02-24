package com.zperkowski.peersharing;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class PhonesCardAdapter extends RecyclerView.Adapter<PhonesCardAdapter.CardViewHolder> {
    final static private String TAG = "PhonesCardAdapter";

    private static List<Phone> phoneList;
    private Context context;

    public static class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final static private String TAG = "PhonesCA.CardViewHolder";

        Context context;

        CardView cardView;
        TextView name;
        TextView ip;

        public CardViewHolder(View itemView, Context context) {
            super(itemView);
            Log.d(TAG, "CardViewHolder()");
            itemView.setOnClickListener(this);
            this.context = context;
            cardView = itemView.findViewById(R.id.phone_card_view);
            name = itemView.findViewById(R.id.row_phone_name);
            ip = itemView.findViewById(R.id.row_phone_ip);
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick() " + String.valueOf(ip.getText()));
            Intent intent = new Intent(itemView.getContext(), NetworkService.class);
            intent.setAction(NetworkService.ACTION_GETFILES);
            intent.putExtra(NetworkService.EXTRA_IP, ip.getText().toString().substring(1));
            context.startService(intent);
            intent = new Intent(itemView.getContext(), FilesActivity.class);
            intent.putExtra("deviceIP", ip.getText().toString().substring(1));
            context.startActivity(intent);
        }
    }

    PhonesCardAdapter(List<Phone> phoneList, Context context) {
        Log.d(TAG, "PhonesCardAdapter(phoneList)");
        this.phoneList = phoneList;
        this.context = context;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder()");
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.phone_card_item, parent, false);
        return new CardViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder()");
        String phonesName = context.getResources().getString(R.string.phone) + " " + String.valueOf(position+1);
        holder.name.setText(phonesName);
        holder.ip.setText(phoneList.get(position).getAddress().toString());
    }

    @Override
    public int getItemCount() {
        //Log.d(TAG, "getItemCount()");
        return phoneList.size();
    }
}
