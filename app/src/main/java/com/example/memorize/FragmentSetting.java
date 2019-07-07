package com.example.memorize;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


public class FragmentSetting extends Fragment {
    private View view;
    private Context context;

    private Switch toggleLockScreen, hour24Method, toggleVibrate;
    private Button lockScreenCardView, lockScreenChoiceView, backupWordBook;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstaceState){
        view = inflater.inflate(R.layout.frame_setting, container, false);
        context = view.getContext();

        bindElement();
        setListener();

        getPreference();

        return view;
    }

    private void bindElement(){
        toggleLockScreen = view.findViewById(R.id.switch_lockScreen);
        hour24Method = view.findViewById(R.id.switch_hourMethod);
        toggleVibrate = view.findViewById(R.id.switch_haptic);

        lockScreenCardView = view.findViewById(R.id.enableCardView);
        lockScreenChoiceView = view.findViewById(R.id.enableChoice);
        backupWordBook = view.findViewById(R.id.backupWordBook);
    }
    private void setListener(){

        toggleLockScreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setEnabledComponentRelatedToLockScreen(isChecked);

                SharedPreferences.Editor editor = context.getSharedPreferences("settings",Context.MODE_PRIVATE).edit();

                editor.putBoolean("lockScreen", isChecked);
                editor.apply();
            }
        });

        hour24Method.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = context.getSharedPreferences("settings",Context.MODE_PRIVATE).edit();
                editor.putBoolean("hour24Method", isChecked);
                editor.apply();
            }
        });

        toggleVibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = context.getSharedPreferences("settings",Context.MODE_PRIVATE).edit();
                editor.putBoolean("vibrate", isChecked);
                editor.apply();

                if(isChecked) {
                    final Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(100);
                }
            }
        });

        lockScreenCardView.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                lockScreenCardView.setSelected(true);
                lockScreenChoiceView.setSelected(false);

                SharedPreferences.Editor editor = context.getSharedPreferences("settings",Context.MODE_PRIVATE).edit();

                editor.putString("lockScreenMethod", "card");
                editor.apply();
            }
        });

        lockScreenChoiceView.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                lockScreenCardView.setSelected(false);
                lockScreenChoiceView.setSelected(true);

                SharedPreferences.Editor editor = context.getSharedPreferences("settings",Context.MODE_PRIVATE).edit();

                editor.putString("lockScreenMethod", "choice");
                editor.apply();
            }
        });

        backupWordBook.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){

            }
        });
    }
    private void setEnabledComponentRelatedToLockScreen(boolean status){
        hour24Method.setEnabled(status);
        lockScreenCardView.setEnabled(status);
        lockScreenChoiceView.setEnabled(status);
    }
    private void getPreference(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);

        if(sharedPreferences.getString("lockScreenMethod", "card").equals("card")) lockScreenCardView.callOnClick();
        else lockScreenChoiceView.callOnClick();

        if(!sharedPreferences.getBoolean("lockScreen", false)){
            toggleLockScreen.setChecked(false);
            setEnabledComponentRelatedToLockScreen(false);
        } else {
            toggleLockScreen.setChecked(true);
            setEnabledComponentRelatedToLockScreen(true);
        }

        toggleVibrate.setChecked(sharedPreferences.getBoolean("vibrate",false));
    }
}
