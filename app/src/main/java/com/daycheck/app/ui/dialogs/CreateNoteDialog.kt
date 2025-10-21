package com.daycheck.app.ui.dialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.daycheck.app.R
import com.daycheck.app.databinding.DialogCreateNoteBinding
import com.daycheck.app.ui.adapters.ChecklistInputAdapter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Dialog for creating new notes.
 * Allows users to add title, body, links, checklist items, and reminders.
 */
class CreateNoteDialog(
    private val selectedDate: LocalDate,
    private val onNoteCreated: (
        title: String?,
        body: String,
        link: String?,
        hasChecklist: Boolean,
        reminderTimestamp: Long?,
        checklistItems: List<String>
    ) -> Unit
) : DialogFragment() {
    
    private var _binding: DialogCreateNoteBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var checklistAdapter: ChecklistInputAdapter
    private var reminderDate: LocalDate? = null
    private var reminderTime: LocalTime? = null
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogCreateNoteBinding.inflate(layoutInflater)
        return Dialog(requireContext()).apply {
            setContentView(binding.root)
            setCancelable(true)
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCreateNoteBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupChecklistRecycler()
        setupClickListeners()
    }
    
    /**
     * Sets up the checklist RecyclerView.
     */
    private fun setupChecklistRecycler() {
        checklistAdapter = ChecklistInputAdapter(
            onItemRemoved = { position ->
                // Handle item removal
            }
        )
        
        binding.checklistItemsRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = checklistAdapter
        }
    }
    
    /**
     * Sets up click listeners for all interactive elements.
     */
    private fun setupClickListeners() {
        // Checklist toggle
        binding.checklistSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.checklistContainer.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
        
        // Add checklist item
        binding.btnAddChecklistItem.setOnClickListener {
            checklistAdapter.addItem("")
        }
        
        // Reminder toggle
        binding.reminderSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.reminderContainer.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
        
        // Reminder date picker
        binding.btnReminderDate.setOnClickListener {
            showDatePicker()
        }
        
        // Reminder time picker
        binding.btnReminderTime.setOnClickListener {
            showTimePicker()
        }
        
        // Save button
        binding.btnSave.setOnClickListener {
            saveNote()
        }
        
        // Cancel button
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }
    
    /**
     * Shows the date picker for reminder date.
     */
    private fun showDatePicker() {
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                reminderDate = LocalDate.of(year, month + 1, dayOfMonth)
                updateReminderDateButton()
            },
            selectedDate.year,
            selectedDate.monthValue - 1,
            selectedDate.dayOfMonth
        )
        datePicker.show()
    }
    
    /**
     * Shows the time picker for reminder time.
     */
    private fun showTimePicker() {
        val timePicker = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                reminderTime = LocalTime.of(hourOfDay, minute)
                updateReminderTimeButton()
            },
            12, 0, false
        )
        timePicker.show()
    }
    
    /**
     * Updates the reminder date button text.
     */
    private fun updateReminderDateButton() {
        reminderDate?.let { date ->
            val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
            binding.btnReminderDate.text = date.format(formatter)
        }
    }
    
    /**
     * Updates the reminder time button text.
     */
    private fun updateReminderTimeButton() {
        reminderTime?.let { time ->
            val formatter = DateTimeFormatter.ofPattern("h:mm a")
            binding.btnReminderTime.text = time.format(formatter)
        }
    }
    
    /**
     * Saves the note with all provided information.
     */
    private fun saveNote() {
        val title = binding.titleInput.text?.toString()?.trim()
        val body = binding.bodyInput.text?.toString()?.trim()
        val link = binding.linkInput.text?.toString()?.trim()
        
        if (body.isNullOrBlank()) {
            Toast.makeText(context, "Please enter note content", Toast.LENGTH_SHORT).show()
            return
        }
        
        val hasChecklist = binding.checklistSwitch.isChecked
        val checklistItems = if (hasChecklist) {
            checklistAdapter.getItems().filter { it.isNotBlank() }
        } else {
            emptyList()
        }
        
        val reminderTimestamp = if (binding.reminderSwitch.isChecked && reminderDate != null && reminderTime != null) {
            val reminderDateTime = LocalDateTime.of(reminderDate!!, reminderTime!!)
            reminderDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        } else {
            null
        }
        
        onNoteCreated(title, body, link, hasChecklist, reminderTimestamp, checklistItems)
        dismiss()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}