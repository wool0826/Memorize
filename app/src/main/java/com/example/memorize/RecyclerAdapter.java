package com.example.memorize;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.realm.Realm;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {

    private ArrayList<RecyclerData> listData = new ArrayList<>();
    private Realm realm;

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_wordbook, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        realm = Realm.getDefaultInstance();

        holder.onBind(listData.get(position));

        final RecyclerData data = listData.get(position);

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(data.getChecked());

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                realm.beginTransaction();
                data.setChecked(isChecked);
                realm.commitTransaction();
            }
        });
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
        private CheckBox checkBox;
        ItemViewHolder(final View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.checkBox);
            word = itemView.findViewById(R.id.word);
            meaning = itemView.findViewById(R.id.meaning);
            failureCount = itemView.findViewById(R.id.failureCount);
            successCount = itemView.findViewById(R.id.successCount);

        }

        void onBind(RecyclerData data) {
            checkBox.setChecked(data.getChecked());
            word.setText(data.getWord());
            meaning.setText(data.getMeaning());

            String f = data.getFailureCount() + "", s = data.getSuccessCount() + "";

            failureCount.setText(f);
            successCount.setText(s);
        }
    }
}