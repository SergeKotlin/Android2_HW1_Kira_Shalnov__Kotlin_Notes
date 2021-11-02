package com.android1.android1_notes;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private boolean isLandscape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        readSettings();
        initView();
        inflateListNotes(); // Наполняем список заметок
    }

    private void readSettings(){
        // Специальный класс для хранения настроек
        SharedPreferences sharedPref = getSharedPreferences(Settings.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        // Считываем значения настроек
        Settings.isBackStack = sharedPref.getBoolean(Settings.IS_BACK_STACK_USED, true);
        Settings.isBlackTheme = sharedPref.getBoolean(Settings.IS_BLACK_THEME_USED, false);
    }

    private void initView() {
        // Регистрируем навигационное (левое) меню
        initDrawer(initToolbar());
    }

    private Toolbar initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        return toolbar;
    }

    private void initDrawer(Toolbar toolbar) {
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Обработка навигационного меню
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (navigateFragment(id)){
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
            return false;
        });
    }

    // Действие кнопки назад в соответствии с хранимыми настройками
    private void initBackAsRemoveFragment(int id) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if ((id == android.R.id.home) && !Settings.isBackStack) {
            Fragment fragment = getVisibleFragment(fragmentManager);
            if (fragment != null) {
                fragmentManager.beginTransaction().remove(fragment).commit();
            }
        }
        else {
            //TODO Если бэкстэк уже пуст, ничего не делать. Выход из приложения буднь в левом меню if (fragmentManager.getBackStackEntryCount() != 0) {}
            fragmentManager.popBackStack();
        }
    }

    @SuppressLint("NonConstantResourceId")
    private boolean navigateFragment(int id) {
            switch(id){
                case R.id.action_settings:
                    addFragment(new SettingsFragment());
                    return true;
                case R.id.action_main:
                    addFragment(new MainFragment());
                    return true;
            }
            return false;
        }

    private void inflateListNotes() {
        addFragment(new MainFragment());
    };

    private void addFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager(); //Получить менеджер фрагментов
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); // Открыть транзакцию
        isLandscape = getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
        if (isLandscape) {
            fragmentTransaction.replace(R.id.notes_container, fragment);
        } else {
            fragmentTransaction.replace(R.id.fragment_container, fragment); // Для однострочной простоты спользуем replace() вместо add()
        }
        if (Settings.isBackStack){ // Добавить транзакцию в бэкстек
            fragmentTransaction.addToBackStack(null);
                // Пояснение:
                // внутри созданной fragmentTransaction происходят действия .replace(),
                // которые можно отправить в стек обратного вызова с помощью .addToBackStack(null).
                // Если указать имя, можно будет перепрыгивать сразу к именованной транзакции с
                // определённым фрагментом fragmentManager.popBackStack(name, flags). Где flags - признак,
                // будем ли включать именованную транзакцию. 0 - фрагмент с именованной транзакцией, 1 или
                // POP_BACK_STACK_INCLUSIVE - предыдущее состояние относительно именованной транзакции.
        }
        fragmentTransaction.commit(); // Закрыть транзакцию
    }

    private Fragment getVisibleFragment(FragmentManager fragmentManager){
        List<Fragment> fragments = fragmentManager.getFragments();
        int countFragments = fragments.size();
        for(int i = countFragments - 1; i >= 0; i--){
            Fragment fragment = fragments.get(i);
            if(fragment.isVisible())
                return fragment;
        }
        return null;
    }

    // Здесь определяем меню приложения (активити)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        toSearching(menu);
        return true; // false - руль фрагменту, true - рулим здесь
    }

    private void toSearching(Menu menu) {
        MenuItem search = menu.findItem(R.id.search__main_menu); // поиск пункта меню поиска
        SearchView searchText = (SearchView) search.getActionView(); // строка поиска
        searchText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { // реагирует на конец ввода поиска
                Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT).show();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) { // реагирует на нажатие каждой клавиши
                return true;
            }
        });
    }

    // Меню активити
    @Override @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId(); // getItemId() - берём id из нажатого item
        if (navigateFragment(id)) {
            return true;
        }
        switch (id) {
            case (R.id.add_note__main_menu):
                toastOnOptionsItemSelected("Добавление новой заметки");
                // Заглушка //TODO addFragment(new NoteFragment());
                return true;
            case (R.id.some_item):
                toastOnOptionsItemSelected("Новая фича");
                return true;
            //TODO Сортировка должна быть на main фрагменте, однако toasts срабатывают лишь на активити
            case R.id.sort_by_name_ascending__main_menu:
                toastOnOptionsItemSelected("Алфавитная сортировка - по возрастанию");
                return true;
            case R.id.sort_by_name_descending__main_menu:
                toastOnOptionsItemSelected("Алфавитная сортировка - по убыванию");
                return true;
            case R.id.sort_by_date_ascending__main_menu:
                toastOnOptionsItemSelected("Временная сортировка - по возрастанию");
                return true;
            case R.id.sort_by_date_descending__main_menu:
                toastOnOptionsItemSelected("Временная сортировка - по убыванию");
                return true;
        }
        initBackAsRemoveFragment(id);
        return super.onOptionsItemSelected(item);
    }

    private void toastOnOptionsItemSelected(CharSequence text) {
        Toast toast = Toast.makeText(getApplicationContext(),
                text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.END, 0, 0);
        toast.show();
    }

}

// Функционал. В целом хотелось бы надёжности хранения заметок.  (Резервирование. Гибкую корзину. И резерирование корзины)
// По графике - фрагменты-мозаика с текстом/текстом+названием, с разным размером и количеством в ряд.
// Хочется кнопку копирования заметки + стрелки-клавиши для быстрого выделения фрагмента текста. Картинки мне не нужны в заметках (но я - не все), рисование можно.
// По удержанию - перемещение, выделение заметок, либо на первое время - копирование.
// Хорошая штука - ярлыки и цветовая дифференциация, аналог каталогов файловой организации.

// Итого: поиск +всплывающее меню: копировать, сохранить в файл;  поделиться; +всплывающее меню: ярлык, закреп ввверху - меню на заметке;
// ✓ копирование заметки, поделиться, ярлык, закреп, +переименовать  - контекстное меню списка, //TODO: мультивыделение в списке
// ✓ поиск, +всплывающее меню: сортировки - меню на вьюхе списка,
// ✓ +меню приложения на списке - новая заметка, поиск, +вид отображения
//
// NavigationDrawer - аватарка, скины, список и счётчики ярлыков, список изменений (в т.ч сохранения в файл),
// размер занятой памяти.. и объём заметок приложением, корзина(архив), резервные стеки и их размер/объём.


// https://gb.ru/lessons/117302
// (Оригинальное задание:)
/* ✓1. Подумайте о функционале вашего приложения заметок. Какие экраны там могут быть, помимо основного со списком заметок?
   ✓ Как можно использовать меню и всплывающее меню в вашем приложении?
        Не обязательно сразу пытаться реализовать весь этот функционал, достаточно создать макеты и структуру,
   ✓    [от себя:] Задать все кнопки и меню. Украсить, скомпоновать, заменить пикчами часть надписей. Чисто разметить.
   ✓    а реализацию пока заменить на заглушки или всплывающие уведомления (Toast).
   - Используйте подход Single Activity для отображения экранов.
    ✓   В качестве ПРИМЕРА: на главном экране приложения у вас список всех заметок, при нажатии на заметку открывается экран с этой заметкой.
    ✓   В меню главного экрана у вас есть иконка поиска по заметкам и сортировка.
    ✓   В меню «Заметки» у вас есть иконки «Переслать» (или «Поделиться»), «Добавить ссылку или фотографию к заметке».
   ✓2. Создайте боковое навигационное меню для своего приложения и добавьте туда хотя бы один экран, например «Настройки» или «О приложении».
   ✓3.(*доп.) Создайте полноценный заголовок для NavigationDrawer’а. К примеру, аватарка пользователя, его имя и какая-то дополнительная информация.*/
// Serega, sure