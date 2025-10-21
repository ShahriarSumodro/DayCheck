package com.example.daycheck.ui.adapters
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.daycheck.data.models.ChecklistItem
import com.example.daycheck.databinding.ItemChecklistRowBinding
// ChecklistAdapter displays editable checklist items with checkboxes.
class ChecklistAdapter(
    private val onItemChecked: (ChecklistItem, Boolean) -> Unit,
    private val onItemTextChanged: (ChecklistItem, String) -> Unit,
    private val isEditable: Boolean
) : ListAdapter<ChecklistItem, ChecklistAdapter.ViewHolder>(DiffCallback()) {
    class ViewHolder(private val binding: ItemChecklistRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: ChecklistItem,
            onChecked: (ChecklistItem, Boolean) -> Unit,
            onTextChanged: (ChecklistItem, String) -> Unit,
            isEditable: Boolean
        ) {
            binding.checkbox.isChecked = item.isChecked
            binding.text.setText(item.text)
            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                onChecked(item, isChecked)
            }
            if (isEditable) {
                binding.text.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        onTextChanged(item, s.toString())
                    }
                })
            } else {
                binding.text.isEnabled = false
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChecklistRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, onItemChecked, onItemTextChanged, isEditable)
    }
    class DiffCallback : DiffUtil.ItemCallback<ChecklistItem>() {
        override fun areItemsTheSame(oldItem: ChecklistItem, newItem: ChecklistItem): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ChecklistItem, newItem: ChecklistItem): Boolean = oldItem == newItem
    }
}