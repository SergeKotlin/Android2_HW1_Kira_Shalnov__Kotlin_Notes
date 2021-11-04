package com.android1.android1_notes

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.ContextMenu.ContextMenuInfo
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import java.util.*

class MainFragment : Fragment() {
    private var currentNote // Выбранная заметка
            : Note? = null
    private var isLandscape = false

    //    @Override
    //    public void onCreate(@Nullable Bundle savedInstanceState) {
    //        super.onCreate(savedInstanceState);
    //    }
    // Сохраним текущую позицию (вызывается перед выходом из фрагмента)
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(CURRENT_NOTE, currentNote)
        super.onSaveInstanceState(outState)
    }

    //TODO Почему в этом фрагменте этот метод is deprecated, когда в другом всё нормально
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Определение, можно ли будет открыть рядом заметку в другом фрагменте
        isLandscape = (resources.configuration.orientation
                == Configuration.ORIENTATION_LANDSCAPE)

        // Если это не первое создание, то восстановим текущую позицию
        currentNote = if (savedInstanceState != null) {
            // Восстановление текущей позиции.
            savedInstanceState.getParcelable(CURRENT_NOTE)
        } else {
            // Если восстановить не удалось, можно показать объект с первым индексом
            Note(0, resources.getStringArray(R.array.notes)[0])
        }

        //TODO Не работает. Не получается организовать верную передачу и обработку заметки между ориентациями

        // Если можно показать текст заметки рядом, сделаем это. Первое отображение
        // showNote(currentNote);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        setHasOptionsMenu(true) // Регестрируем меню! Не забываем
        return view
    }

    // Вызывается после создания макета фрагмента
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList(view) // проинициализируем список заметок
    }

    // Создаём список городов на экране из массива в ресурсах
    private fun initList(view: View) {
        val layoutView = view as LinearLayout
        val notes = resources.getStringArray(R.array.notes_names)
        for (i in notes.indices) {
            val note = notes[i]
            val tv = TextView(context)
            tv.text = note
            tv.textSize = 30f
            setNoteColor(tv)
            layoutView.addView(tv)
            val fi: Int = i // не можем внутрь анонимного класса передать не final - иначе гонка потоков
            tv.setOnClickListener { v: View? ->
                currentNote = Note(fi, resources.getStringArray(R.array.notes)[fi])
                showNote(currentNote!!)
            }
            registerForContextMenu(tv) // Для контекстного меню регистрируем таргет
        }
    }

    //TODO #1_Not_important ИСКЛЮЧИТЬ ПОВТОР ЦВЕТОВ ДРУГ ЗА ДРУГОМ
    private fun setNoteColor(tv: TextView) {
        val notes_colors = resources.getStringArray(R.array.notes_colors)
        val note_color: Int
        val size_colors_arr = notes_colors.size
        note_color = if (size_colors_arr <= 0) {
            R.color.yellow_note
        } else {
            val random = Random()
            val ind_note_color = random.nextInt(size_colors_arr)
            Color.parseColor(notes_colors[ind_note_color])
        }
        tv.setTextColor(note_color)
    }

    private fun showNote(currentNote: Note) {
        val detail = NoteFragment.newInstance(currentNote) // Создаём новый фрагмент с текущей позицией для открытия заметки
        val fragmentManager = requireActivity().supportFragmentManager // Выполняем транзакцию по замене фрагмента
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.addToBackStack(null) // Перед показом заметки закинем лист заметок, с которого переходим, в БэкСтэк
        replacingFragment(detail, fragmentTransaction)
        // fragmentTransaction.add(R.id.notes, detail); // TODO сделать мульти-оконное открытие заметок (опционально, по выбору).
        // Такого не видел у конкурентов. P.s. в некоторых ситуациях - определенно удобная штука.
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        fragmentTransaction.commit()
    }

    private fun replacingFragment(detail: NoteFragment, fragmentTransaction: FragmentTransaction) {
        isLandscape = (resources.configuration.orientation
                == Configuration.ORIENTATION_LANDSCAPE)
        if (isLandscape) {
            fragmentTransaction.replace(R.id.note_container, detail)
        } else {
            fragmentTransaction.replace(R.id.fragment_container, detail) // Замена фрагмента
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }

    @SuppressLint("NonConstantResourceId")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //TODO На ландщафтной ориентации добавленные меню дублируются, так как их фрагмент остаётся присутствовать на экране
        // Постоянно "пересоздаётся" экран/фрагмент при выборе меню
        // К тому же, фрагмент для списка заметок будто добавляется дважды, тогда это мешает и корректному SaveInstance
        val id = item.itemId
        when (id) {
            R.id.notes_view_choice__main_menu -> {
                toastOnOptionsItemSelected("Выбор вида представления")
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun toastOnOptionsItemSelected(text: CharSequence) {
        val toast = Toast.makeText(context,
                text, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.END, 0, 0)
        toast.show()
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        requireActivity().menuInflater.inflate(R.menu.context_main, menu)
    }

    @SuppressLint("NonConstantResourceId")
    override fun onContextItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.copy_note__context_main -> {
                toastOnOptionsItemSelected("Заметка скопирована")
                return true
            }
            R.id.share_note__context_main -> {
                toastOnOptionsItemSelected("Заметка передана / Открыта через..")
                return true
            }
            R.id.new_label__context_main -> {
                toastOnOptionsItemSelected("Добавлена новая метка")
                return true
            }
            R.id.pin_to_top__context_main -> {
                toastOnOptionsItemSelected("Заметка закреплена")
                return true
            }
            R.id.search__context_main -> {
                toastOnOptionsItemSelected("Поиск в заметке")
                return true
            }
            R.id.info__context_main -> {
                toastOnOptionsItemSelected("Детали заметки")
                return true
            }
            R.id.rename__context_main -> {
                toastOnOptionsItemSelected("Заметка переименована")
                return true
            }
            R.id.delete__context_main -> {
                toastOnOptionsItemSelected("Заметка удалена")
                return true
            }
        }
        return super.onContextItemSelected(item)
    }

    companion object {
        const val CURRENT_NOTE = "CurrentNote"
    }
}