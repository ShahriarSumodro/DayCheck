package com.daycheck.app.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.daycheck.app.R
import com.daycheck.app.databinding.FragmentArchiveBinding
import com.daycheck.app.ui.adapters.NotesAdapter
import com.daycheck.app.viewmodel.ArchiveViewModel
import com.google.android.material.chip.Chip

/**
 * Fragment that displays archived/completed notes.
 * Provides search and filtering functionality.
 */
class ArchiveFragment : Fragment() {
    
    private var _binding: FragmentArchiveBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: ArchiveViewModel by activityViewModels()
    private lateinit var notesAdapter: NotesAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArchiveBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupSearch()
        setupFilters()
        observeViewModel()
    }
    
    /**
     * Sets up the RecyclerView for archived notes.
     */
    private fun setupRecyclerView() {
        notesAdapter = NotesAdapter(
            onNoteClick = { note ->
                // Handle note click - could show details or edit
            },
            onNoteComplete = { note ->
                // Toggle completion status
                if (note.isCompleted) {
                    viewModel.restoreNote(note)
                } else {
                    // This shouldn't happen in archive, but handle gracefully
                }
            },
            onNoteDelete = { note ->
                showDeleteConfirmation(note)
            },
            onChecklistItemToggle = { item ->
                // Handle checklist item toggle if needed
            }
        )
        
        binding.archiveNotesRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = notesAdapter
        }
    }
    
    /**
     * Sets up the search functionality.
     */
    private fun setupSearch() {
        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setSearchQuery(s?.toString() ?: "")
            }
            
            override fun afterTextChanged(s: Editable?) {}
        })
    }
    
    /**
     * Sets up the filter chips.
     */
    private fun setupFilters() {
        binding.filterAll.setOnClickListener {
            setFilterType(ArchiveViewModel.FilterType.ALL)
        }
        
        binding.filterCompleted.setOnClickListener {
            setFilterType(ArchiveViewModel.FilterType.COMPLETED)
        }
        
        binding.filterIncomplete.setOnClickListener {
            setFilterType(ArchiveViewModel.FilterType.INCOMPLETE)
        }
        
        // Set default filter
        setFilterType(ArchiveViewModel.FilterType.ALL)
    }
    
    /**
     * Sets the active filter type and updates chip states.
     */
    private fun setFilterType(filterType: ArchiveViewModel.FilterType) {
        // Clear all selections
        binding.filterAll.isChecked = false
        binding.filterCompleted.isChecked = false
        binding.filterIncomplete.isChecked = false
        
        // Set the selected filter
        when (filterType) {
            ArchiveViewModel.FilterType.ALL -> binding.filterAll.isChecked = true
            ArchiveViewModel.FilterType.COMPLETED -> binding.filterCompleted.isChecked = true
            ArchiveViewModel.FilterType.INCOMPLETE -> binding.filterIncomplete.isChecked = true
        }
        
        viewModel.setFilterType(filterType)
    }
    
    /**
     * Observes the ViewModel for data changes.
     */
    private fun observeViewModel() {
        viewModel.archivedNotes.observe(viewLifecycleOwner, Observer { notes ->
            notesAdapter.submitList(notes)
            updateEmptyState(notes.isEmpty())
        })
        
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            // Show/hide loading indicator
            binding.root.isEnabled = !isLoading
        })
    }
    
    /**
     * Updates the empty state visibility.
     */
    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.archiveNotesRecycler.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }
    
    /**
     * Shows a confirmation dialog for deleting a note permanently.
     */
    private fun showDeleteConfirmation(note: com.daycheck.app.data.models.Note) {
        // In a real implementation, you would show an AlertDialog here
        // For now, we'll just delete the note
        viewModel.deleteNotePermanently(note)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}