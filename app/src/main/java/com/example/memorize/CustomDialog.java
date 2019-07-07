package com.example.memorize;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class CustomDialog extends Dialog {

    private Button mPositiveButton;
    private Button mNegativeButton;
    private EditText word, meaning;

    private String positiveString;
    private String negativeString;

    private View.OnClickListener mPositiveListener;
    private View.OnClickListener mNegativeListener;
    private View.OnKeyListener onKeyListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //다이얼로그 밖의 화면은 흐리게 만들어줌
        /*WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);*/

        setContentView(R.layout.dialog_addword);

        mPositiveButton = findViewById(R.id.cancel);
        mNegativeButton = findViewById(R.id.agree);

        //클릭 리스너 셋팅 (클릭버튼이 동작하도록 만들어줌.)
        this.mPositiveButton.setOnClickListener(this.mPositiveListener);
        this.mNegativeButton.setOnClickListener(this.mNegativeListener);

        this.word = findViewById(R.id.inputWord);
        this.meaning = findViewById(R.id.inputMeaning);

        this.meaning.setOnKeyListener(this.onKeyListener);

        this.mPositiveButton.setText(positiveString);
        this.mNegativeButton.setText(negativeString);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }


    //생성자 생성
    public CustomDialog(@NonNull Context context) {
        super(context);
    }

    public Button getPositiveButton(){
        return this.mPositiveButton;
    }
    public void setPositiveListener(View.OnClickListener listener){
        this.mPositiveListener = listener;
    }
    public void setNegativeListener(View.OnClickListener listener){
        this.mNegativeListener = listener;
    }

    public void setOnKeyListener(View.OnKeyListener listener){
        this.onKeyListener = listener;
    }
    public void setPositiveText(String text){
        this.positiveString = text;
    }
    public void setNegativeText(String text){
        this.negativeString = text;
    }

    public String getWord(){
        return this.word.getText().toString();
    }
    public String getMeaning(){
        return this.meaning.getText().toString();
    }
}