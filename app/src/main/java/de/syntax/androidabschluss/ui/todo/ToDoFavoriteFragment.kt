package de.syntax.androidabschluss.ui.todo

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import de.syntax.androidabschluss.R
import de.syntax.androidabschluss.data.Adapter.ToDoAdapter
import de.syntax.androidabschluss.databinding.FragmentToDoFavoriteBinding
import de.syntax.androidabschluss.viewmodel.MeinViewModel


class ToDoFavoriteFragment : Fragment() {

    // ViewModel instance shared with the activity
    private val viewModel: MeinViewModel by activityViewModels()

    // binding for the fragment layout
    private lateinit var binding: FragmentToDoFavoriteBinding

    // MediaPlayer instance for playing audio
    private lateinit var mediaPlayer: MediaPlayer

    /**
     * Inflates the layout for this fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentToDoFavoriteBinding.inflate(layoutInflater)
        return binding.root
    }

    /**
     * Sets up UI components and event listeners after the view has been created.
     */
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the MediaPlayer
        mediaPlayer = MediaPlayer()


        // Variable to save the state of the RecyclerView list
        var recyclerViewState: Parcelable

        // Observe changes in the to.do list LiveData
        viewModel.todo.observe(viewLifecycleOwner) { todo ->

            // Save the state of the RecyclerView list
            recyclerViewState = binding.rvToDoFav.layoutManager?.onSaveInstanceState()!!
            // Restore state
            binding.rvToDoFav.layoutManager?.onRestoreInstanceState(recyclerViewState)

            // Filter and sort the to.do list to show only favorite items that are not completed
            val sortedAsFav =
                todo.sortedByDescending { it.favorite }.filter { it.favorite && !it.isSelected }

            // Set up the RecyclerView adapter with the filtered list
            binding.rvToDoFav.adapter = ToDoAdapter(sortedAsFav, viewModel, mediaPlayer)

            // Update the count of favorite items displayed
            binding.tvListCountFav.text =
                sortedAsFav.size.toString() + " ${getString(R.string.items)}"


            // Set up click listener for delete all button
            binding.btnDeleteAll.setOnClickListener {

                // Stop MediaPlayer if it's playing
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                }

                // Get the list of favorite to-do items
                val favoriteToDoList = viewModel.todo.value?.filter { it.favorite }
                if (favoriteToDoList.isNullOrEmpty()) {
                    // If the list of Favorites to-do items is empty, show a message
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.the_list_is_empty),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // If the list of Favorites to-do items is not empty,
                    // show the deleted confirmation dialog
                    // Builds an AlertDialog
                    AlertDialog.Builder(requireContext(), R.style.CustomDialogTheme)
                        .setTitle(getString(R.string.delete_all_notes))
                        .setMessage(getString(R.string.are_you_sure_delete_all))
                        .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                            // If user confirms, delete all notes
                            viewModel.deleteToDoFavorite(todo, deleteAllFavorites = true)
                            viewModel.unsetComplete()
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.all_notes_are_deleted),
                                Toast.LENGTH_SHORT
                            ).show()
                            dialog.dismiss() // Dismiss the dialog
                        }
                        .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                            // If a user cancels, do nothing
                            dialog.dismiss() // Dismiss the dialog
                        }
                        .show() // Show the AlertDialog
                }
            }
        }


        // Set up click listener for add button to navigate to ToDoDetailsFragment
        binding.btnAdd.setOnClickListener {
            // Stop MediaPlayer if it's playing
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            // Navigate to ToDoDetailsFragment
            findNavController().navigate(R.id.toDoDetailsFragment)
        }


        // Set up click listener for done button to navigate to ToDoCategoriesFragment
        binding.btnDone.setOnClickListener {
            // Stop MediaPlayer if it's playing
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            // Navigate to ToDoCategoriesFragment
            findNavController().navigate(R.id.toDoCategoriesFragment)
        }

        // Deactivate system back button
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {}
    }

}