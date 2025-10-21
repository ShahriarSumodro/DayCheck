package com.daycheck.app.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.daycheck.app.R
import com.daycheck.app.data.models.ChecklistItem
import com.daycheck.app.data.models.Note
import com.daycheck.app.databinding.ItemNoteCardBinding
import java.time.format.DateTimeFormatter

/**
 * Adapter for displaying notes in a RecyclerView.
 * Shows note cards with title, body, links, and checklist items.
 */
class NotesAdapter(
    private val onNoteClick: (Note) -> Unit,
    private val onNoteComplete: (Note) -> Unit,
    private val onNoteDelete: (Note) -> Unit,
    private val onChecklistItemToggle: (ChecklistItem) -> Unit
) : ListAdapter<Note, NotesAdapter.NoteViewHolder>(NoteDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NoteViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    /**
     * ViewHolder for individual note items.
     */
    inner class NoteViewHolder(
        private val binding: ItemNoteCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(note: Note) {
            // Set note content
            binding.noteTitle.text = note.title
            binding.noteTitle.visibility = if (note.title.isNullOrBlank()) View.GONE else View.VISIBLE
            
            binding.noteBody.text = note.body
            
            // Set link if present
            if (!note.link.isNullOrBlank()) {
                binding.noteLink.text = note.link
                binding.noteLink.visibility = View.VISIBLE
            } else {
                binding.noteLink.visibility = View.GONE
            }
            
            // Set completion status
            binding.noteCheckbox.isChecked = note.isCompleted
            
            // Set reminder if present
            if (note.reminderTimestamp != null) {
                val reminderTime = java.time.Instant.ofEpochMilli(note.reminderTimestamp)
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime()
                val formatter = DateTimeFormatter.ofPattern("MMM d, h:mm a")
                binding.reminderText.text = "Reminder: ${reminderTime.format(formatter)}"
                binding.reminderIndicator.visibility = View.VISIBLE
            } else {
                binding.reminderIndicator.visibility = View.GONE
            }
            
            // Set up checklist items if present
            if (note.hasChecklist) {
                // In a real implementation, you would load and display checklist items here
                binding.checklistRecycler.visibility = View.VISIBLE
            } else {
                binding.checklistRecycler.visibility = View.GONE
            }
            
            // Set up click listeners
            binding.root.setOnClickListener {
                onNoteClick(note)
            }
            
            binding.noteCheckbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked != note.isCompleted) {
                    onNoteComplete(note)
                }
            }
            
            binding.noteMenu.setOnClickListener { view ->
                showNoteMenu(view, note)
            }
        }
        
        /**
         * Shows the note options menu.
         */
        private fun showNoteMenu(view: View, note: Note) {
            val popup = PopupMenu(view.context, view)
            popup.menuInflater.inflate(R.menu.note_menu, popup.menu)
            
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_edit -> {
                        onNoteClick(note)
                        true
                    }
                    R.id.action_delete -> {
                        onNoteDelete(note)
                        true
                    }
                    else -> false
                }
            }
            
            popup.show()
        }
    }
    
    /**
     * DiffUtil callback for efficient list updates.
     */
    class NoteDiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }
}