package com.example.memorize;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {

    private ArrayList<RecyclerData> listData = new ArrayList<>();

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_wordbook, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    void addItem(RecyclerData data) {
        listData.add(data);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView word, meaning, failureCount, successCount;
        ItemViewHolder(View itemView) {
            super(itemView);

            word = itemView.findViewById(R.id.word);
            meaning = itemView.findViewById(R.id.meaning);
            failureCount = itemView.findViewById(R.id.failureCount);
            successCount = itemView.findViewById(R.id.successCount);
        }

        void onBind(RecyclerData data) {
            word.setText(data.getWord());
            meaning.setText(data.getMeaning());
            failureCount.setText(data.getFailureCount().toString());
            successCount.setText(data.getSuccessCount().toString());
        }
    }
}