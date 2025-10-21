package com.daycheck.app.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daycheck.app.databinding.ItemChecklistInputBinding

/**
 * Adapter for managing checklist item inputs in the create note dialog.
 * Allows users to add, edit, and remove checklist items.
 */
class ChecklistInputAdapter(
    private val onItemRemoved: (Int) -> Unit
) : RecyclerView.Adapter<ChecklistInputAdapter.ChecklistInputViewHolder>() {
    
    private val items = mutableListOf<String>()
    
    /**
     * Adds a new checklist item.
     */
    fun addItem(text: String) {
        items.add(text)
        notifyItemInserted(items.size - 1)
    }
    
    /**
     * Gets all checklist items.
     */
    fun getItems(): List<String> = items.toList()
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistInputViewHolder {
        val binding = ItemChecklistInputBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChecklistInputViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ChecklistInputViewHolder, position: Int) {
        holder.bind(items[position], position)
    }
    
    override fun getItemCount(): Int = items.size
    
    /**
     * ViewHolder for checklist input items.
     */
    inner class ChecklistInputViewHolder(
        private val binding: ItemChecklistInputBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(text: String, position: Int) {
            binding.checklistItemInput.setText(text)
            
            binding.checklistItemInput.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && text.isEmpty()) {
                    binding.checklistItemInput.hint = "Enter checklist item"
                }
            }
            
            binding.checklistItemInput.addTextChangedListener(object : android.text.TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    items[position] = s?.toString() ?: ""
                }
                
                override fun afterTextChanged(s: Editable?) {}
            })
            
            binding.btnRemoveItem.setOnClickListener {
                if (items.size > 1) {
                    items.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, items.size)
                    onItemRemoved(position)
                }
            }
        }
    }
}
