package com.example.memorize;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

import static android.app.Activity.RESULT_OK;

public class FragmentAdd extends Fragment {
    private View view;
    private Context context;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private Realm realm;
    private ImageButton addButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstaceState){
        view = inflater.inflate(R.layout.frame_add, container, false);
        context = view.getContext();

        recyclerViewInit();
        setRecyclerViewData();

        setClickListener();

        return view;
    }

    private void recyclerViewInit(){
        recyclerView = view.findViewById(R.id.wordBookList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerAdapter = new RecyclerAdapter();
        recyclerView.setAdapter(recyclerAdapter);
    }
    private void setRecyclerViewData(){
        Realm.init(context);

        realm = Realm.getDefaultInstance();

        RealmResults<RecyclerData> results = realm.where(RecyclerData.class).findAll();

        for(RecyclerData data: results){
            recyclerAdapter.addItem(data);
        }

        recyclerAdapter.notifyDataSetChanged();
    }

    private void setClickListener(){
        addButton = view.findViewById(R.id.addButton);

        /*addWord = view.findViewById(R.id.addWord);
        addWordBook = view.findViewById(R.id.addWordBook);*/

        /*addWord.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                final CustomDialog dialog = new CustomDialog(context);
                dialog.setPositiveText("등록");
                dialog.setNegativeText("취소");
                dialog.setPositiveListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        realm.beginTransaction();

                        RecyclerData data = realm.createObject(RecyclerData.class);
                        data.setWord(dialog.getWord());
                        data.setMeaning(dialog.getMeaning());
                        data.setFailureCount(0);
                        data.setSuccessCount(0);

                        realm.commitTransaction();

                        recyclerAdapter.addItem(data);
                        recyclerAdapter.notifyDataSetChanged();

                        dialog.dismiss();
                    }
                });
                dialog.setNegativeListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        dialog.dismiss();
                    }
                });
                dialog.setOnKeyListener(new View.OnKeyListener(){
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        //Enter key Action
                        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                            dialog.getPositiveButton().callOnClick();
                            return true;
                        }
                        return false;
                    }
                });

                dialog.show();
            }
        });*/

        addButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent().setType("text/*").setAction(Intent.ACTION_GET_CONTENT);
                getActivity().startActivityForResult(Intent.createChooser(intent, "파일 선택"), 123);

            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 123 && resultCode == Activity.RESULT_OK){
            try {
                Uri uri = data.getData();
                InputStreamReader is = new InputStreamReader(getActivity().getContentResolver().openInputStream(uri));
                BufferedReader reader = new BufferedReader(is);
                CSVReader read = new CSVReader(reader);
                String[] record = null;

                realm.beginTransaction();
                while ((record = read.readNext()) != null) {

                    RecyclerData retrieve = realm.createObject(RecyclerData.class);
                    retrieve.setWord(record[0]);
                    retrieve.setMeaning(record[1]);
                    retrieve.setFailureCount(0);
                    retrieve.setSuccessCount(0);
                    recyclerAdapter.addItem(retrieve);
                }
                realm.commitTransaction();
                recyclerAdapter.notifyDataSetChanged();

                Toast.makeText(context, "단어장 추가 완료", Toast.LENGTH_SHORT).show();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
