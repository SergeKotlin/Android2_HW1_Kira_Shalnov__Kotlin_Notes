package com.android1.android1_notes;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Random;

public class MainFragment extends Fragment {

    public static final String CURRENT_NOTE = "CurrentNote";
    private Note currentNote;    // Выбранная заметка
    private boolean isLandscape;

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }

    // Сохраним текущую позицию (вызывается перед выходом из фрагмента)
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(CURRENT_NOTE, currentNote);
        super.onSaveInstanceState(outState);
    }

    //TODO Почему в этом фрагменте этот метод is deprecated, когда в другом всё нормально
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Определение, можно ли будет открыть рядом заметку в другом фрагменте
        isLandscape = getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;

        // Если это не первое создание, то восстановим текущую позицию
        if (savedInstanceState != null) {
            // Восстановление текущей позиции.
            currentNote = savedInstanceState.getParcelable(CURRENT_NOTE);
        } else {
            // Если восстановить не удалось, можно показать объект с первым индексом
            currentNote = new Note(0, getResources().getStringArray(R.array.notes)[0]);
        }

        //TODO Не работает. Не получается организовать верную передачу и обработку заметки между ориентациями

        // Если можно показать текст заметки рядом, сделаем это. Первое отображение
        // showNote(currentNote);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true); // Регестрируем меню! Не забываем
        return view;
    }

    // Вызывается после создания макета фрагмента
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initList(view); // проинициализируем список заметок
    }

    // Создаём список городов на экране из массива в ресурсах
    private void initList(View view) {
        LinearLayout layoutView = (LinearLayout)view;
        String[] notes = getResources().getStringArray(R.array.notes_names);

        for(int i=0; i < notes.length; i++){
            String note = notes[i];
            TextView tv = new TextView(getContext());
            tv.setText(note);
            tv.setTextSize(30);

            setNoteColor(tv);

            layoutView.addView(tv);

            final int fi = i; // не можем внутрь анонимного класса передать не final - иначе гонка потоков
            tv.setOnClickListener(v -> {
                currentNote = new Note(fi, getResources().getStringArray(R.array.notes)[fi]);
                showNote(currentNote);
            });

            registerForContextMenu(tv); // Для контекстного меню регистрируем таргет
        }
    }

    //TODO #1_Not_important ИСКЛЮЧИТЬ ПОВТОР ЦВЕТОВ ДРУГ ЗА ДРУГОМ
    private void setNoteColor(TextView tv) {
        String[] notes_colors = getResources().getStringArray(R.array.notes_colors);
        int note_color;
        int size_colors_arr = notes_colors.length;
        if (size_colors_arr <= 0) {
            note_color = R.color.yellow_note;
        } else {
            Random random = new Random();
            int ind_note_color = random.nextInt(size_colors_arr);
            note_color = Color.parseColor(notes_colors[ind_note_color]);
        }
        tv.setTextColor(note_color);
    }

    private void showNote(Note currentNote) {
        NoteFragment detail = NoteFragment.newInstance(currentNote); // Создаём новый фрагмент с текущей позицией для открытия заметки
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager(); // Выполняем транзакцию по замене фрагмента
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null); // Перед показом заметки закинем лист заметок, с которого переходим, в БэкСтэк
        replacingFragment(detail, fragmentTransaction);
        // fragmentTransaction.add(R.id.notes, detail); // TODO сделать мульти-оконное открытие заметок (опционально, по выбору).
        // Такого не видел у конкурентов. P.s. в некоторых ситуациях - определенно удобная штука.
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }

    private void replacingFragment(NoteFragment detail, FragmentTransaction fragmentTransaction) {
        isLandscape = getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
        if (isLandscape) {
            fragmentTransaction.replace(R.id.note_container, detail);
        } else {
            fragmentTransaction.replace(R.id.fragment_container, detail);  // Замена фрагмента
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //TODO На ландщафтной ориентации добавленные меню дублируются, так как их фрагмент остаётся присутствовать на экране
        // Постоянно "пересоздаётся" экран/фрагмент при выборе меню
        // К тому же, фрагмент для списка заметок будто добавляется дважды, тогда это мешает и корректному SaveInstance
        int id = item.getItemId();
        switch (id) {
            case R.id.notes_view_choice__main_menu:
                toastOnOptionsItemSelected("Выбор вида представления");
                return true;
            //TODO Сортировка должна быть здесь, на main фрагменте, но toasts срабатывают лишь на активити
        }
        return super.onOptionsItemSelected(item);
    }

    private void toastOnOptionsItemSelected(CharSequence text) {
        Toast toast = Toast.makeText(getContext(),
                text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.END, 0, 0);
        toast.show();
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        requireActivity().getMenuInflater().inflate(R.menu.context_main, menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.copy_note__context_main:
                toastOnOptionsItemSelected("Заметка скопирована");
                return true;
            case R.id.share_note__context_main:
                toastOnOptionsItemSelected("Заметка передана / Открыта через..");
                return true;
            case R.id.new_label__context_main:
                toastOnOptionsItemSelected("Добавлена новая метка");
                return true;
            case R.id.pin_to_top__context_main:
                toastOnOptionsItemSelected("Заметка закреплена");
                return true;
            case R.id.search__context_main:
                toastOnOptionsItemSelected("Поиск в заметке");
                return true;
            case R.id.info__context_main:
                toastOnOptionsItemSelected("Детали заметки");
                return true;
            case R.id.rename__context_main:
                toastOnOptionsItemSelected("Заметка переименована");
                return true;
            case R.id.delete__context_main:
                toastOnOptionsItemSelected("Заметка удалена");
                return true;
        }
        return super.onContextItemSelected(item);
    }

}
