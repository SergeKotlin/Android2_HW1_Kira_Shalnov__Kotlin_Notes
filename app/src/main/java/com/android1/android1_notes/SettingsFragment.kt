package com.android1.android1_notes;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        initRadioBackStack(view);
        initRadioBackAsRemove(view);
        initSwitchBlackTheme(view);
    }

    private void initRadioBackStack(View view) {
        RadioButton radioButtonUseBackStack = view.findViewById(R.id.radioButtonUseBackstack);
        radioButtonUseBackStack.setChecked(Settings.isBackStack);
        radioButtonUseBackStack.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Settings.isBackStack = isChecked;
            writeSettings();
        });
    }

    private void initRadioBackAsRemove(View view) {
        // Как можно заметить - инвертный выбор "isBackStack"
        RadioButton radioButtonBackAsRemove = view.findViewById(R.id.radioButtonBackAsRemove);
        radioButtonBackAsRemove.setChecked(!Settings.isBackStack);
        radioButtonBackAsRemove.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Settings.isBackStack = !isChecked;
            writeSettings();
        });
    }

    private void initSwitchBlackTheme(View view) {
        SwitchCompat switchBlackTheme = view.findViewById(R.id.switchBlackTheme);
        switchBlackTheme.setChecked(Settings.isBlackTheme);
        switchBlackTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Settings.isBlackTheme = isChecked;
            //TODO setTheme();
            writeSettings();
        });
    }

//    private void setTheme() {
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        getActivity().recreate();
//    }

    // Сохранение настроек приложения
    private void writeSettings(){
        SharedPreferences sharedPref = requireActivity().getSharedPreferences(Settings.SHARED_PREFERENCE_NAME, MODE_PRIVATE); // Специальный класс для хранения настроек
        SharedPreferences.Editor editor = sharedPref.edit(); // Настройки сохраняются посредством специального класса editor
        editor.putBoolean(Settings.IS_BACK_STACK_USED, Settings.isBackStack); // Задаём значения настроек
        editor.putBoolean(Settings.IS_BLACK_THEME_USED, Settings.isBlackTheme);
        editor.apply(); // Сохраняем значения настроек
    }
}