package com.example.memorize;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import io.realm.RealmResults;

import static android.speech.tts.TextToSpeech.ERROR;

public class LockScreen extends Activity{
    private String[] weekdayString = {"", "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
    private String[] monthString = {"", "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"};

    private TextView clock, date;
    private TextView wordView, meaningView;
    private TextView grade[] = new TextView[7];
    private int gradeId[] = {R.id.grade1, R.id.grade2, R.id.grade3, R.id.grade4, R.id.grade5, R.id.grade6, R.id.grade7};

    private Button knowButton, dontKnowButton, nextButton, prevButton, unlockButton, speechButton;

    private Vibrator vibrator;

    private RealmResults<RecyclerData> wordList;
    private int currentWordIndex = 0;

    private SharedPreferences sharedPreferences;

    private Realm realm;
    private Random r = new Random();

    private Handler mHandler = new Handler();
    private Timer mTimer;
    private TextToSpeech tts;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_study);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        bindElement();
        setListener();

        setTimer();
        setWordView();
    }

    private void bindElement(){

        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        clock = findViewById(R.id.clock);
        date = findViewById(R.id.date);

        wordView = findViewById(R.id.wordView);
        meaningView = findViewById(R.id.meaningView);

        for(int i=0; i<7; i++){
            grade[i] = findViewById(gradeId[i]);
        }

        nextButton = findViewById(R.id.nextWord);
        prevButton = findViewById(R.id.prevWord);
        unlockButton = findViewById(R.id.unlockButton);

        knowButton = findViewById(R.id.knowButton);
        dontKnowButton = findViewById(R.id.dontknowButton);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        Realm.init(this);

        realm = Realm.getDefaultInstance();
        wordList = realm.where(RecyclerData.class).findAll();

        speechButton = findViewById(R.id.speechButton);
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.ENGLISH);
                }
            }
        });

        currentWordIndex = r.nextInt(wordList.size());
    }
    private void setListener(){
        nextButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(sharedPreferences.getBoolean("vibrate", false)) vibrator.vibrate(20);

                currentWordIndex = r.nextInt(wordList.size());

                setWordView();
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(sharedPreferences.getBoolean("vibrate", false)) vibrator.vibrate(20);

                currentWordIndex = r.nextInt(wordList.size());

                setWordView();
            }
        });

        unlockButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });

        knowButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                realm.beginTransaction();
                wordList.get(currentWordIndex).setSuccessCount(wordList.get(currentWordIndex).getSuccessCount() + 1);
                realm.commitTransaction();

                nextButton.callOnClick();
            }
        });

        dontKnowButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                realm.beginTransaction();
                wordList.get(currentWordIndex).setFailureCount(wordList.get(currentWordIndex).getFailureCount() + 1);
                realm.commitTransaction();
                nextButton.callOnClick();

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
        runOnUiThread(new Runnable(){
            public void run(){
                if(wordList.isEmpty()) return;

                RecyclerData currentData = wordList.get(currentWordIndex);
                wordView.setText(currentData.getWord());
                meaningView.setText(currentData.getMeaning());

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

    public void setTimer(){
        mTimer = new Timer();
        MyTimerTask task = new MyTimerTask();

        mTimer.schedule(task, 500, 1000);
    }

    class MyTimerTask extends TimerTask{
        public void run(){
            mHandler.post(new Runnable(){
                public void run(){
                    Calendar cal = Calendar.getInstance();
                    int month = cal.get(Calendar.MONTH) + 1, day = cal.get(Calendar.DAY_OF_MONTH), weekday = cal.get(Calendar.DAY_OF_WEEK);
                    SimpleDateFormat clockFormat = new SimpleDateFormat("hh:mm", Locale.KOREA);

                    String clockString = clockFormat.format(cal.getTime());
                    String dateString = monthString[month] + " " + day + " " + weekdayString[weekday];

                    clock.setText(clockString);
                    date.setText(dateString);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        //Close the Text to Speech Library
        if(tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
