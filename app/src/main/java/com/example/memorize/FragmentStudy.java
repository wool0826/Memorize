package com.example.memorize;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmResults;

public class FragmentStudy extends Fragment {
    private View view;
    private Context context;
    private String[] weekday = {"", "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
    private String[] monthString = {"", "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"};

    private TextView clock, date;
    private TextView wordView, meaningView;
    private TextView grade[] = new TextView[7];
    private int gradeId[] = {R.id.grade1, R.id.grade2, R.id.grade3, R.id.grade4, R.id.grade5, R.id.grade6, R.id.grade7};

    private ImageButton nextButton, prevButton, unlockButton;

    private boolean onVibration, hour24Method;
    private Vibrator vibrator;

    private RealmResults<RecyclerData> wordList;
    private ArrayList<RecyclerData> wordArrayList;
    private int currentWordIndex = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstaceState){
        view = inflater.inflate(R.layout.frame_study, container, false);
        context = view.getContext();

        bindElement();
        setListener();
        getPreference();

        setTimer();
        setWordView();

        return view;
    }

    private void bindElement(){
        clock = view.findViewById(R.id.clock);
        date = view.findViewById(R.id.date);

        wordView = view.findViewById(R.id.wordView);
        meaningView = view.findViewById(R.id.meaningView);

        for(int i=0; i<7; i++){
            grade[i] = view.findViewById(gradeId[i]);
        }

        nextButton = view.findViewById(R.id.nextWord);
        prevButton = view.findViewById(R.id.prevWord);
        unlockButton = view.findViewById(R.id.unlockButton);

        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        Realm.init(context);

        Realm realm = Realm.getDefaultInstance();
        wordList = realm.where(RecyclerData.class).findAll();
        wordArrayList = new ArrayList<>();
        wordArrayList.addAll(wordList);

        Collections.shuffle(wordArrayList, new Random(System.nanoTime()));
    }
    private void setListener(){
        nextButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(onVibration) vibrator.vibrate(20);

                currentWordIndex--;
                if(currentWordIndex < 0) currentWordIndex = wordArrayList.size()-1;

                setWordView();
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(onVibration) vibrator.vibrate(20);

                currentWordIndex++;
                if(currentWordIndex >= wordArrayList.size()) currentWordIndex = 0;

                setWordView();
            }
        });

        unlockButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

            }
        });
    }
    private void getPreference(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
        onVibration = sharedPreferences.getBoolean("vibrate", false);
        hour24Method = sharedPreferences.getBoolean("hour24Method", false);
    }
    private void setWordView(){
        getActivity().runOnUiThread(new Runnable(){
            public void run(){
                if(wordArrayList.isEmpty()) return;

                RecyclerData currentData = wordArrayList.get(currentWordIndex);
                wordView.setText(currentData.getWord());
                meaningView.setText(currentData.getMeaning());

                for(int i=0; i<7; i++){
                    if(i == 3) grade[i].setBackgroundResource(R.color.colorNormalStatus);
                    else grade[i].setBackgroundResource(R.color.bg);
                }

                for(int i=0; i<7; i++){
                    if(i == 3) continue;

                    if(i < 3 && currentData.getFailureCount() >= (3-i)*5) grade[i].setBackgroundResource(R.color.colorFail);
                    else if(i > 3 && currentData.getSuccessCount() >= (i-3)*5) grade[i].setBackgroundResource(R.color.colorSuccess);
                }
            }
        });
    }

    private void setTimer(){
        AsyncTimer asyncTimer = new AsyncTimer();
        asyncTimer.execute();
    }

    class AsyncTimer extends AsyncTask<Integer, Integer, Integer> {
        Calendar cal;
        String clockString;
        String dateString;

        @Override
        protected Integer doInBackground(Integer... params){
            while(!isCancelled()){
                cal = new GregorianCalendar();
                int month = cal.get(Calendar.MONTH) + 1, day = cal.get(Calendar.DAY_OF_MONTH);
                int hour = cal.get(Calendar.HOUR_OF_DAY), min = cal.get(Calendar.MINUTE);
                int day_of_week = cal.get(Calendar.DAY_OF_WEEK);

                dateString = monthString[month] + " " + day + " " + weekday[day_of_week];
                clockString = String.format(Locale.KOREA, "%02d:%02d", hour, min);

                publishProgress();

                try{
                    Thread.sleep(1000);
                } catch(Exception e){
                    e.printStackTrace();
                }

            }
            return null;
        }

        @Override
        protected void onPreExecute(){
            cal = new GregorianCalendar();
            int month = cal.get(Calendar.MONTH) + 1, day = cal.get(Calendar.DAY_OF_MONTH);
            int hour = cal.get(Calendar.HOUR_OF_DAY), min = cal.get(Calendar.MINUTE);
            int day_of_week = cal.get(Calendar.DAY_OF_WEEK);

            dateString = monthString[month] + "  " + day + "  " + weekday[day_of_week];
            clockString = String.format(Locale.KOREA, "%02d:%02d", hour, min);

            clock.setText(clockString);
            date.setText(dateString);

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer){
            super.onPostExecute(integer);
        }

        @Override
        protected void onProgressUpdate(Integer... values){
            clock.setText(clockString);
            date.setText(dateString);

            super.onProgressUpdate(values);
        }

    }
}


