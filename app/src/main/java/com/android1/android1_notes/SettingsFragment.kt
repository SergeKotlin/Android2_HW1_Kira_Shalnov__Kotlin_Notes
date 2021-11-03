package com.android1.android1_notes

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.RadioButton
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        initRadioBackStack(view)
        initRadioBackAsRemove(view)
        initSwitchBlackTheme(view)
    }

    private fun initRadioBackStack(view: View) {
        val radioButtonUseBackStack = view.findViewById<RadioButton>(R.id.radioButtonUseBackstack)
        radioButtonUseBackStack.isChecked = Settings.isBackStack
        radioButtonUseBackStack.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            Settings.isBackStack = isChecked
            writeSettings()
        }
    }

    private fun initRadioBackAsRemove(view: View) {
        // Как можно заметить - инвертный выбор "isBackStack"
        val radioButtonBackAsRemove = view.findViewById<RadioButton>(R.id.radioButtonBackAsRemove)
        radioButtonBackAsRemove.isChecked = !Settings.isBackStack
        radioButtonBackAsRemove.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            Settings.isBackStack = !isChecked
            writeSettings()
        }
    }

    private fun initSwitchBlackTheme(view: View) {
        val switchBlackTheme: SwitchCompat = view.findViewById(R.id.switchBlackTheme)
        switchBlackTheme.isChecked = Settings.isBlackTheme
        switchBlackTheme.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            Settings.isBlackTheme = isChecked
            //TODO setTheme();
            writeSettings()
        }
    }

    //    private void setTheme() {
    //        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    //        getActivity().recreate();
    //    }
    // Сохранение настроек приложения
    private fun writeSettings() {
        val sharedPref = requireActivity().getSharedPreferences(Settings.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE) // Специальный класс для хранения настроек
        val editor = sharedPref.edit() // Настройки сохраняются посредством специального класса editor
        editor.putBoolean(Settings.IS_BACK_STACK_USED, Settings.isBackStack) // Задаём значения настроек
        editor.putBoolean(Settings.IS_BLACK_THEME_USED, Settings.isBlackTheme)
        editor.apply() // Сохраняем значения настроек
    }
}