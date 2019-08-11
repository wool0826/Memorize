package com.example.memorize;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import static android.speech.tts.TextToSpeech.ERROR;

public class FragmentStudy extends Fragment {
    private View view;
    private Context context;
    private String[] weekday = {"", "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
    private String[] monthString = {"", "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"};

    private TextView clock, date, unlockHint;
    private TextView wordView, meaningView;
    private TextView grade[] = new TextView[7];
    private int gradeId[] = {R.id.grade1, R.id.grade2, R.id.grade3, R.id.grade4, R.id.grade5, R.id.grade6, R.id.grade7};

    private Button knowButton, dontKnowButton, nextButton, prevButton, unlockButton, speechButton;

    private Vibrator vibrator;

    private RealmResults<RecyclerData> wordList;

    private SharedPreferences sharedPreferences;

    private Realm realm;
    private Random r = new Random();
    private TextToSpeech tts;

    private ArrayList<Integer> shuffleList;
    private int currentWordIndex = 0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstaceState){
        view = inflater.inflate(R.layout.frame_study, container, false);
        context = view.getContext();

        bindElement();
        setListener();

        setWordView();

        return view;
    }

    private void bindElement(){

        sharedPreferences = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
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
        unlockHint = view.findViewById(R.id.unlockHint);

        knowButton = view.findViewById(R.id.knowButton);
        dontKnowButton = view.findViewById(R.id.dontknowButton);
        speechButton = view.findViewById(R.id.speechButton);

        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        Realm.init(context);

        realm = Realm.getDefaultInstance();
        wordList = realm.where(RecyclerData.class).findAll();

        clock.setVisibility(View.INVISIBLE);
        date.setVisibility(View.INVISIBLE);

        unlockButton.setVisibility(View.INVISIBLE);
        unlockHint.setVisibility(View.INVISIBLE);

        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.ENGLISH);
                }
            }
        });

        shuffleList = new ArrayList<>();
        for(int i=0; i<wordList.size(); i++){
            shuffleList.add(i);
        }

        Collections.shuffle(shuffleList);
        currentWordIndex = 0;
    }
    private void setListener(){
        nextButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(sharedPreferences.getBoolean("vibrate", false)) vibrator.vibrate(20);

                currentWordIndex++;
                if(currentWordIndex >= shuffleList.size()) currentWordIndex = 0;

                setWordView();
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(sharedPreferences.getBoolean("vibrate", false)) vibrator.vibrate(20);

                currentWordIndex--;
                if(currentWordIndex < 0) currentWordIndex = shuffleList.size() - 1;

                setWordView();
            }
        });

        unlockButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

            }
        });

        knowButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                realm.beginTransaction();
                wordList.get(shuffleList.get(currentWordIndex)).setSuccessCount(wordList.get(shuffleList.get(currentWordIndex)).getSuccessCount() + 1);
                realm.commitTransaction();

                wordView.setVisibility(View.VISIBLE);
            }
        });

        dontKnowButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                realm.beginTransaction();
                wordList.get(shuffleList.get(currentWordIndex)).setFailureCount(wordList.get(shuffleList.get(currentWordIndex)).getFailureCount() + 1);
                realm.commitTransaction();

                wordView.setVisibility(View.VISIBLE);
            }
        });
        speechButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    tts.speak(wordView.getText().toString(),TextToSpeech.QUEUE_FLUSH,null,null);
                } else {
                    tts.speak(wordView.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
    }

    private void setWordView(){
        getActivity().runOnUiThread(new Runnable(){
            public void run(){
                if(wordList.isEmpty()) return;

                RecyclerData currentData = wordList.get(shuffleList.get(currentWordIndex));
                wordView.setText(currentData.getWord());
                meaningView.setText(currentData.getMeaning());

                if(sharedPreferences.getBoolean("hideWord", false)){
                    wordView.setVisibility(View.INVISIBLE);
                }

                for(int i=0; i<7; i++){
                    if(i == 3) grade[i].setBackgroundResource(R.color.colorNormalStatus);
                    else grade[i].setBackgroundResource(R.color.bg);
                }

                int diff = currentData.getSuccessCount() - currentData.getFailureCount();

                for(int i=-3; i<=-1; i++) if(diff <= i) grade[i+3].setBackgroundResource(R.color.colorFail);
                for(int i=1; i<=3; i++) if(diff >= i) grade[i+3].setBackgroundResource(R.color.colorSuccess);
            }
        });
    }

    @Override
    public void onDestroy(){
        if(tts != null){
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}


