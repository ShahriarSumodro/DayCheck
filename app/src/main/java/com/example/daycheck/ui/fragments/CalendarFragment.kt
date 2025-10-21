package com.example.daycheck.ui.fragments

package com.example.daycheck.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.daycheck.R
import com.example.daycheck.databinding.FragmentCalendarBinding
import com.example.daycheck.ui.adapters.CalendarAdapter
import com.example.daycheck.viewmodel.CalendarViewModel
import com.example.daycheck.utils.DateUtils
import java.util.Calendar

// CalendarFragment displays a monthly calendar grid with tappable dates.
// It marks dates with notes using green dots and supports search across all notes, switching to a list view when searching.
class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CalendarViewModel by viewModels()
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var notesAdapter: com.example.daycheck.ui.adapters.NotesAdapter  // Reused from day view

    private var currentMonth: Calendar = Calendar.getInstance()
    private var searchView: SearchView? = null
    private var filter: String = "all" // all, completed, incomplete

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()
        setupCalendarGrid()
        setupNotesList()
        observeViewModel()

        binding.prevMonth.setOnClickListener {
            currentMonth.add(Calendar.MONTH, -1)
            updateCalendar()
        }

        binding.nextMonth.setOnClickListener {
            currentMonth.add(Calendar.MONTH, 1)
            updateCalendar()
        }

        updateCalendar()
    }

    // Sets up the menu for search and filter.
    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_calendar, menu)
                val searchItem = menu.findItem(R.id.action_search)
                searchView = searchItem.actionView as SearchView
                searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean = true
                    override fun onQueryTextChange(newText: String?): Boolean {
                        viewModel.searchNotes(newText ?: "", filter, false) // false for not archived
                        showNotesListIfSearching(newText)
                        return true
                    }
                })

                // Filter menu items
                menu.findItem(R.id.filter_all).setOnMenuItemClickListener {
                    filter = "all"
                    viewModel.searchNotes(searchView?.query.toString(), filter, false)
                    true
                }
                menu.findItem(R.id.filter_completed).setOnMenuItemClickListener {
                    filter = "completed"
                    viewModel.searchNotes(searchView?.query.toString(), filter, false)
                    true
                }
                menu.findItem(R.id.filter_incomplete).setOnMenuItemClickListener {
                    filter = "incomplete"
                    viewModel.searchNotes(searchView?.query.toString(), filter, false)
                    true
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean = false
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    // Sets up the calendar RecyclerView with GridLayoutManager.
    private fun setupCalendarGrid() {
        calendarAdapter = CalendarAdapter { dateMillis ->
            val bundle = Bundle().apply { putLong("date", dateMillis) }
            findNavController().navigate(R.id.action_calendarFragment_to_dayFragment, bundle)
        }
        binding.calendarGrid.layoutManager = GridLayoutManager(context, 7)
        binding.calendarGrid.adapter = calendarAdapter
    }

    // Sets up the notes RecyclerView for search results.
    private fun setupNotesList() {
        notesAdapter = com.example.daycheck.ui.adapters.NotesAdapter(
            onCheckChanged = { note, isChecked ->
                // Handle check change if needed, but for search list, perhaps view only or navigate
            },
            onEdit = { note ->
                // Open edit dialog
            },
            onDelete = { note ->
                viewModel.deleteNote(note)
            },
            onLinkClick = { link ->
                // Open browser
            },
            isArchive = false
        )
        binding.notesList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        binding.notesList.adapter = notesAdapter
    }

    // Observes dates with notes to update calendar markers and searched notes.
    private fun observeViewModel() {
        viewModel.datesWithNotes.observe(viewLifecycleOwner, Observer { dates ->
            calendarAdapter.setDatesWithNotes(dates)
        })

        viewModel.searchedNotes.observe(viewLifecycleOwner, Observer { notes ->
            notesAdapter.submitList(notes)
        })
    }

    // Updates the calendar for the current month.
    private fun updateCalendar() {
        binding.monthYear.text = DateUtils.formatMonthYear(currentMonth.timeInMillis)
        val days = DateUtils.getDaysInMonth(currentMonth)
        calendarAdapter.submitList(days)
    }

    // Shows/hides calendar vs notes list based on search query.
    private fun showNotesListIfSearching(query: String?) {
        if (!query.isNullOrEmpty()) {
            binding.calendarGrid.visibility = View.GONE
            binding.notesList.visibility = View.VISIBLE
        } else {
            binding.calendarGrid.visibility = View.VISIBLE
            binding.notesList.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}