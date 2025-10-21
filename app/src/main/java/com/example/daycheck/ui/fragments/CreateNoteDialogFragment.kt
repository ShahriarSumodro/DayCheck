package com.example.daycheck.ui.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.daycheck.data.models.ChecklistItem
import com.example.daycheck.data.models.Note
import com.example.daycheck.databinding.DialogCreateNoteBinding
import com.example.daycheck.ui.adapters.ChecklistAdapter
import com.example.daycheck.viewmodel.DayViewModel
import androidx.fragment.app.activityViewModels
import com.example.daycheck.notifications.NotificationScheduler
import java.util.Calendar

// CreateNoteDialogFragment is a modal for creating or editing notes.
// It supports title, body, link, reminder, and checklist items. Dismissible by outside tap or cancel.
class CreateNoteDialogFragment : DialogFragment() {

    private var _binding: DialogCreateNoteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DayViewModel by activityViewModels()
    private lateinit var checklistAdapter: com.example.daycheck.ui.adapters.ChecklistAdapter

    private var date: Long = 0L
    private var noteId: Int = 0
    private var isEdit = false
    private var reminderCal: Calendar? = null

    companion object {
        fun newInstance(date: Long, noteId: Int = 0): CreateNoteDialogFragment {
            val fragment = CreateNoteDialogFragment()
            val args = Bundle().apply {
                putLong("date", date)
                putInt("noteId", noteId)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, android.R.style.ThemeOverlay_Material_Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCreateNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        date = arguments?.getLong("date") ?: 0L
        noteId = arguments?.getInt("noteId") ?: 0
        isEdit = noteId != 0

        setupChecklist()
        setupButtons()

        if (isEdit) {
            loadNote()
        }
    }

    private fun setupChecklist() {
        checklistAdapter = ChecklistAdapter(
            onItemChecked = { item, isChecked ->
                item.isChecked = isChecked
                viewModel.updateChecklistItem(item)
            },
            onItemTextChanged = { item, text ->
                item.text = text
                viewModel.updateChecklistItem(item)
            },
            isEditable = true
        )
        binding.checklistList.adapter = checklistAdapter
        binding.checklistList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)

        binding.addChecklistItem.setOnClickListener {
            val item = ChecklistItem(noteId = noteId, text = "")
            viewModel.insertChecklistItem(item)
        }
    }

    private fun setupButtons() {
        binding.pickDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, year, month, day ->
                reminderCal = Calendar.getInstance().apply {
                    set(year, month, day)
                }
                pickTime()
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.save.setOnClickListener {
            saveNote()
            dismiss()
        }

        binding.cancel.setOnClickListener {
            dismiss()
        }
    }

    private fun pickTime() {
        val cal = reminderCal ?: Calendar.getInstance()
        TimePickerDialog(requireContext(), { _, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            reminderCal = cal
            binding.reminderText.text = DateUtils.formatDateTime(cal.timeInMillis)
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
    }

    // Loads existing note for editing.
    private fun loadNote() {
        viewModel.getNoteById(noteId).observe(viewLifecycleOwner) { note ->
            if (note != null) {
                binding.title.setText(note.title)
                binding.body.setText(note.body)
                binding.link.setText(note.link)
                if (note.reminderTimestamp != null) {
                    reminderCal = Calendar.getInstance().apply { timeInMillis = note.reminderTimestamp }
                    binding.reminderText.text = DateUtils.formatDateTime(note.reminderTimestamp)
                }
            }
        }
        viewModel.getChecklistForNote(noteId).observe(viewLifecycleOwner) { items ->
            checklistAdapter.submitList(items)
        }
    }

    // Saves or updates the note, schedules reminder if set.
    private fun saveNote() {
        val title = binding.title.text.toString().takeIf { it.isNotBlank() }
        val body = binding.body.text.toString().takeIf { it.isNotBlank() }
        val link = binding.link.text.toString().takeIf { it.isNotBlank() }
        val hasChecklist = checklistAdapter.itemCount > 0
        val reminder = reminderCal?.timeInMillis
        val isCompleted = if (hasChecklist) checklistAdapter.currentList.all { it.isChecked } else false

        val note = if (isEdit) {
            viewModel.getNoteById(noteId).value!!.copy(
                title = title,
                body = body,
                link = link,
                hasChecklist = hasChecklist,
                isCompleted = isCompleted,
                reminderTimestamp = reminder,
                updatedAt = System.currentTimeMillis()
            )
        } else {
            Note(
                title = title,
                body = body,
                date = date,
                hasChecklist = hasChecklist,
                reminderTimestamp = reminder,
                link = link
            )
        }

        if (isEdit) {
            viewModel.updateNote(note)
        } else {
            viewModel.insertNote(note)
        }

        if (reminder != null && requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE).getBoolean("notifications_enabled", true)) {
            NotificationScheduler.schedule(requireContext(), note)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}