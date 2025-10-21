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
import com.example.daycheck.R
import com.example.daycheck.databinding.FragmentArchiveBinding
import com.example.daycheck.ui.adapters.NotesAdapter
import com.example.daycheck.viewmodel.ArchiveViewModel

// ArchiveFragment displays completed and archived notes.
// Supports search, filtering, restore, and permanent delete.
class ArchiveFragment : Fragment() {

    private var _binding: FragmentArchiveBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ArchiveViewModel by viewModels()
    private lateinit var adapter: NotesAdapter
    private var searchView: SearchView? = null
    private var filter: String = "all"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArchiveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()
        setupAdapter()
        observeViewModel()

        viewModel.loadArchivedNotes()
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_archive, menu)
                val searchItem = menu.findItem(R.id.action_search)
                searchView = searchItem.actionView as SearchView
                searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean = true
                    override fun onQueryTextChange(newText: String?): Boolean {
                        viewModel.searchNotes(newText ?: "", filter, true) // true for archived
                        return true
                    }
                })

                // Filter menu
                menu.findItem(R.id.filter_all).setOnMenuItemClickListener {
                    filter = "all"
                    viewModel.searchNotes(searchView?.query.toString(), filter, true)
                    true
                }
                menu.findItem(R.id.filter_completed).setOnMenuItemClickListener {
                    filter = "completed"
                    viewModel.searchNotes(searchView?.query.toString(), filter, true)
                    true
                }
                menu.findItem(R.id.filter_incomplete).setOnMenuItemClickListener {
                    filter = "incomplete"
                    viewModel.searchNotes(searchView?.query.toString(), filter, true)
                    true
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean = false
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupAdapter() {
        adapter = NotesAdapter(
            onCheckChanged = { note, isChecked ->
                // In archive, perhaps no check change, or update if needed
            },
            onEdit = { note ->
                // No edit in archive, or open dialog
            },
            onDelete = { note ->
                viewModel.deleteNote(note)
            },
            onLinkClick = { link ->
                startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(link)))
            },
            isArchive = true, // Shows restore button
            onRestore = { note ->
                note.isArchived = false
                note.isCompleted = false
                viewModel.updateNote(note)
            }
        )
        binding.archiveList.adapter = adapter
        binding.archiveList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
    }

    private fun observeViewModel() {
        viewModel.archivedNotes.observe(viewLifecycleOwner, Observer { notes ->
            adapter.submitList(notes)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}