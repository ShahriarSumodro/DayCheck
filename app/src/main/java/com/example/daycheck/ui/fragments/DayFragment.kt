package com.example.daycheck.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.example.daycheck.databinding.FragmentDayBinding
import com.example.daycheck.ui.adapters.NotesAdapter
import com.example.daycheck.viewmodel.DayViewModel
import com.example.daycheck.utils.DateUtils
import com.example.daycheck.notifications.NotificationScheduler

// DayFragment displays notes for a specific date in a Google Keep-like card list.
// It separates active and completed notes, with completed in an expandable section. Supports creating, editing, deleting notes.
class DayFragment : Fragment() {

    private var _binding: FragmentDayBinding? = null
    private val binding get() = _binding!!
    private val args: DayFragmentArgs by navArgs()
    private val viewModel: DayViewModel by viewModels()
    private lateinit var activeAdapter: NotesAdapter
    private lateinit var completedAdapter: NotesAdapter

    private var date: Long = 0L
    private var highlightNoteId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        date = args.date
        highlightNoteId = args.noteId // From notification

        binding.dateTitle.text = DateUtils.formatDate(date)

        setupAdapters()
        observeViewModel()

        binding.fabAdd.setOnClickListener {
            CreateNoteDialogFragment.newInstance(date).show(childFragmentManager, "create_note")
        }

        binding.completedHeader.setOnClickListener {
            val visibility = if (binding.completedList.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            binding.completedList.visibility = visibility
        }

        viewModel.loadNotesForDate(date)
    }

    // Sets up adapters for active and completed notes.
    private fun setupAdapters() {
        activeAdapter = NotesAdapter(
            onCheckChanged = { note, isChecked ->
                note.isCompleted = isChecked
                viewModel.updateNote(note)
                if (note.reminderTimestamp != null) {
                    NotificationScheduler.cancel(requireContext(), note.id)
                }
            },
            onEdit = { note ->
                CreateNoteDialogFragment.newInstance(date, note.id).show(childFragmentManager, "edit_note")
            },
            onDelete = { note ->
                viewModel.deleteNote(note)
            },
            onLinkClick = { link ->
                startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(link)))
            },
            isArchive = false
        )
        binding.activeList.adapter = activeAdapter
        binding.activeList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)

        completedAdapter = NotesAdapter(
            onCheckChanged = { note, isChecked ->
                note.isCompleted = isChecked
                viewModel.updateNote(note)
            },
            onEdit = { note ->
                CreateNoteDialogFragment.newInstance(date, note.id).show(childFragmentManager, "edit_note")
            },
            onDelete = { note ->
                viewModel.deleteNote(note)
            },
            onLinkClick = { link ->
                startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(link)))
            },
            isArchive = false
        )
        binding.completedList.adapter = completedAdapter
        binding.completedList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
    }

    // Observes notes and updates adapters; highlights note if from notification.
    private fun observeViewModel() {
        viewModel.notesForDate.observe(viewLifecycleOwner, Observer { notes ->
            val active = notes.filter { !it.isCompleted }
            val completed = notes.filter { it.isCompleted }
            activeAdapter.submitList(active)
            completedAdapter.submitList(completed)

            if (completed.isNotEmpty()) {
                binding.completedSection.visibility = View.VISIBLE
            } else {
                binding.completedSection.visibility = View.GONE
            }

            // Highlight if needed
            if (highlightNoteId != 0) {
                // Find position and scroll to it
                val position = notes.indexOfFirst { it.id == highlightNoteId }
                if (position != -1) {
                    binding.activeList.scrollToPosition(position) // Assuming active, adjust if completed
                }
                highlightNoteId = 0
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}