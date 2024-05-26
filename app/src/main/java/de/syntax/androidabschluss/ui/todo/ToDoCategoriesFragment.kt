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
import androidx.navigation.fragment.findNavController
import de.syntax.androidabschluss.R
import de.syntax.androidabschluss.data.Adapter.ToDoAdapter
import de.syntax.androidabschluss.databinding.FragmentToDoCategoriesBinding
import de.syntax.androidabschluss.viewmodel.MeinViewModel

/**
 * A [Fragment] responsible for displaying to-do items categorized by type.
 */
class ToDoCategoriesFragment : Fragment() {

    // ViewModel instance for handling to-do operations
    private val viewModel: MeinViewModel by activityViewModels()

    // View binding instance for this fragment
    private lateinit var binding: FragmentToDoCategoriesBinding

    // MediaPlayer instance for playing audio
    private lateinit var mediaPlayer: MediaPlayer


    /**
     * Inflates the layout for this fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentToDoCategoriesBinding.inflate(layoutInflater)
        return binding.root
    }


    /**
     * Sets up UI components and event listeners after the view has been created.
     */
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize MediaPlayer
        mediaPlayer = MediaPlayer()

        // Save state "position" of the RV list
        var recyclerViewState1: Parcelable
        var recyclerViewState2: Parcelable
        var recyclerViewState3: Parcelable
        var recyclerViewState4: Parcelable


        // Observe important to-do items and update UI accordingly
        viewModel.todo.observe(viewLifecycleOwner) { important ->
            recyclerViewState1 = binding.rvToDoImportant.layoutManager?.onSaveInstanceState()!!
            // Restore state
            binding.rvToDoImportant.layoutManager?.onRestoreInstanceState(recyclerViewState1)

            // sort the list after the type
            val sortedAsImportant =
                important.sortedByDescending { it.isImportant }
                    .filter { it.isImportant && !it.isSelected }

            // show the last 4 items in the list
            val sorted4Items = sortedAsImportant.take(4)

            binding.rvToDoImportant.adapter = ToDoAdapter(sorted4Items, viewModel, mediaPlayer)

            // show the total number of the items in the list
            binding.tvListCountImportant.text = sortedAsImportant.size.toString() + " ${getString(R.string.items)}"

            // navigate to the list with the filtered items
            binding.btnSeeAllImpo.setOnClickListener {

                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                }

                findNavController().navigate(R.id.toDoImportantFragment)
            }
        }

        // Observe done to-do items and update UI accordingly
        viewModel.todo.observe(viewLifecycleOwner) { done ->
            recyclerViewState2 = binding.rvToDoDone.layoutManager?.onSaveInstanceState()!!
            // Restore state
            binding.rvToDoDone.layoutManager?.onRestoreInstanceState(recyclerViewState2)

            // sort the list after the type
            val sortedAsDone = done.sortedByDescending { it.isSelected }.filter { it.isSelected }

            // show the last 4 items in the list
            val sorted4Items = sortedAsDone.take(4)

            binding.rvToDoDone.adapter = ToDoAdapter(sorted4Items, viewModel, mediaPlayer)

            // show the total number of the items in the list
            binding.tvListCountDone.text = sortedAsDone.size.toString() + " ${getString(R.string.items)}"

            // navigate to the list with the filtered items
            binding.btnSeeAllDone.setOnClickListener {

                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                }

                findNavController().navigate(R.id.toDoDoneFragment)
            }
        }

        // Observe all to-do items and update UI accordingly
        viewModel.todo.observe(viewLifecycleOwner) { all ->
            recyclerViewState3 = binding.rvToDoAll.layoutManager?.onSaveInstanceState()!!
            // Restore state
            binding.rvToDoAll.layoutManager?.onRestoreInstanceState(recyclerViewState3)

            // sort the list after the type
            val sortedAsAll =
                all.sortedByDescending { it.creationDateTime }.filter { !it.isSelected }

            // show the last 4 items in the list
            val sorted4Items = sortedAsAll.take(4)

            binding.rvToDoAll.adapter = ToDoAdapter(sorted4Items, viewModel, mediaPlayer)

            // show the total number of the items in the list
            binding.tvListCountAll.text =
                sortedAsAll.filter { !it.isSelected }.size.toString() + " ${getString(R.string.items)}"

            // navigate to the list with the filtered items
            binding.btnSeeAllAll.setOnClickListener {

                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                }

                findNavController().navigate(R.id.toDoFragment)
            }
        }

        // Observe favorite to-do items and update UI accordingly
        viewModel.todo.observe(viewLifecycleOwner) { fav ->
            recyclerViewState4 = binding.rvToDoFavorite.layoutManager?.onSaveInstanceState()!!
            // Restore state
            binding.rvToDoFavorite.layoutManager?.onRestoreInstanceState(recyclerViewState4)

            // sort the list after the type
            val sortedAsFav =
                fav.sortedByDescending { it.favorite }.filter { it.favorite && !it.isSelected }

            // show the last 4 items in the list
            val sorted4Items = sortedAsFav.take(4)
            binding.rvToDoFavorite.adapter = ToDoAdapter(sorted4Items, viewModel, mediaPlayer)

            // show the total number of the items in the list
            binding.tvListCountFavorite.text = sortedAsFav.size.toString() + " ${getString(R.string.items)}"

            // navigate to the list with the filtered items
            binding.btnSeeAllFav.setOnClickListener {

                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                }

                findNavController().navigate(R.id.toDoFavoriteFragment)
            }
        }


        // Navigate to the "Add To-Do" screen
        binding.btnAdd.setOnClickListener {

            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }

            findNavController().navigate(R.id.toDoDetailsFragment)
        }

        // Navigate to the home screen
        binding.btnDone.setOnClickListener {

            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }

            findNavController().navigate(R.id.homeFragment)
        }

        // Button to delete all To-Do items with AlertDialog confirmation
        binding.btnDeleteAll.setOnClickListener {

            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }

            // Build an AlertDialog
            AlertDialog.Builder(requireContext(), R.style.CustomDialogTheme)
                .setTitle(getString(R.string.delete_all_notes))
                .setMessage(getString(R.string.are_you_sure_delete_all))
                .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                    // If user confirms, delete all notes
                    viewModel.deleteAllToDo()
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

        // Deactivate system back button
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {}
    }
}


