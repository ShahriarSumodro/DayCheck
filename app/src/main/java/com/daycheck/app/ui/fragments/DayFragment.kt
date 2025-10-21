package com.daycheck.app.ui.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.daycheck.app.R
import com.daycheck.app.databinding.FragmentDayBinding
import com.daycheck.app.ui.adapters.NotesAdapter
import com.daycheck.app.ui.dialogs.CreateNoteDialog
import com.daycheck.app.viewmodel.DayViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Fragment that displays notes for a specific date.
 * Shows active notes and completed notes in a Google Keep-style layout.
 */
class DayFragment : Fragment() {
    
    private var _binding: FragmentDayBinding? = null
    private val binding get() = _binding!!
    
    private val args: DayFragmentArgs by navArgs()
    private val viewModel: DayViewModel by activityViewModels()
    
    private lateinit var activeNotesAdapter: NotesAdapter
    private lateinit var completedNotesAdapter: NotesAdapter
    
    private var isCompletedSectionExpanded = false
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDayBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupToolbar()
        setupRecyclerViews()
        setupClickListeners()
        observeViewModel()
        
        // Set the date from arguments
        val selectedDate = LocalDate.parse(args.selectedDate)
        viewModel.setDate(selectedDate)
    }
    
    /**
     * Sets up the toolbar with back navigation and date display.
     */
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        
        // Update date header
        val selectedDate = LocalDate.parse(args.selectedDate)
        val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")
        binding.dateHeader.text = selectedDate.format(formatter)
    }
    
    /**
     * Sets up the RecyclerViews for active and completed notes.
     */
    private fun setupRecyclerViews() {
        // Active notes adapter
        activeNotesAdapter = NotesAdapter(
            onNoteClick = { note ->
                // Handle note click - could show edit dialog
            },
            onNoteComplete = { note ->
                viewModel.toggleNoteCompletion(note)
            },
            onNoteDelete = { note ->
                viewModel.deleteNote(note)
            },
            onChecklistItemToggle = { item ->
                viewModel.toggleChecklistItem(item)
            }
        )
        
        binding.activeNotesRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = activeNotesAdapter
        }
        
        // Completed notes adapter
        completedNotesAdapter = NotesAdapter(
            onNoteClick = { note ->
                // Handle note click
            },
            onNoteComplete = { note ->
                viewModel.toggleNoteCompletion(note)
            },
            onNoteDelete = { note ->
                viewModel.deleteNote(note)
            },
            onChecklistItemToggle = { item ->
                viewModel.toggleChecklistItem(item)
            }
        )
        
        binding.completedNotesRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = completedNotesAdapter
        }
    }
    
    /**
     * Sets up click listeners for FAB and completed section toggle.
     */
    private fun setupClickListeners() {
        binding.fabAddNote.setOnClickListener {
            showCreateNoteDialog()
        }
        
        binding.completedHeader.setOnClickListener {
            toggleCompletedSection()
        }
    }
    
    /**
     * Observes the ViewModel for data changes.
     */
    private fun observeViewModel() {
        viewModel.activeNotes.observe(viewLifecycleOwner, Observer { notes ->
            activeNotesAdapter.submitList(notes)
        })
        
        viewModel.completedNotes.observe(viewLifecycleOwner, Observer { notes ->
            completedNotesAdapter.submitList(notes)
            updateCompletedSectionVisibility(notes.isNotEmpty())
        })
        
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            // Show/hide loading indicator
            binding.root.isEnabled = !isLoading
        })
    }
    
    /**
     * Shows the create note dialog.
     */
    private fun showCreateNoteDialog() {
        val selectedDate = LocalDate.parse(args.selectedDate)
        val dialog = CreateNoteDialog(
            selectedDate = selectedDate,
            onNoteCreated = { title, body, link, hasChecklist, reminderTimestamp, checklistItems ->
                viewModel.createNote(
                    title, body, link, hasChecklist, reminderTimestamp, checklistItems
                )
            }
        )
        dialog.show(parentFragmentManager, "CreateNoteDialog")
    }
    
    /**
     * Toggles the visibility of the completed notes section.
     */
    private fun toggleCompletedSection() {
        isCompletedSectionExpanded = !isCompletedSectionExpanded
        
        if (isCompletedSectionExpanded) {
            binding.completedNotesRecycler.visibility = View.VISIBLE
            binding.completedArrow.rotation = 180f
        } else {
            binding.completedNotesRecycler.visibility = View.GONE
            binding.completedArrow.rotation = 0f
        }
    }
    
    /**
     * Updates the visibility of the completed section based on whether there are completed notes.
     */
    private fun updateCompletedSectionVisibility(hasCompletedNotes: Boolean) {
        if (hasCompletedNotes) {
            binding.completedHeader.visibility = View.VISIBLE
            if (!isCompletedSectionExpanded) {
                binding.completedNotesRecycler.visibility = View.GONE
            }
        } else {
            binding.completedHeader.visibility = View.GONE
            binding.completedNotesRecycler.visibility = View.GONE
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}