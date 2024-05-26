package de.syntax.androidabschluss.ui.favorites

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import de.syntax.androidabschluss.R
import de.syntax.androidabschluss.data.Adapter.FavoNewsAdapter
import de.syntax.androidabschluss.databinding.FragmentFavoritesBinding
import de.syntax.androidabschluss.viewmodel.MeinViewModel


/**
 * A [Fragment] subclass responsible for displaying favorite articles.
 */
class FavoritesFragment : Fragment() {


    // View model instance for handling article operations
    private val viewModel: MeinViewModel by activityViewModels()


    // View binding instance for this fragment
    private lateinit var binding: FragmentFavoritesBinding


    /**
     * Inflates the layout for this fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(layoutInflater)
        return binding.root
    }


    /**
     * Sets up UI components and event listeners after the view has been created.
     */
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Save state of the RV list
        var recyclerViewState: Parcelable

        // Observe the list of favorite articles
        viewModel.newsArticle.observe(viewLifecycleOwner) { list ->

            recyclerViewState = binding.rvFavorite.layoutManager?.onSaveInstanceState()!!
            // Restore state
            binding.rvFavorite.layoutManager?.onRestoreInstanceState(recyclerViewState)

            val sortedList = list.asReversed()

            // Set up RecyclerView adapter with a sorted list of favorite articles
            binding.rvFavorite.adapter = FavoNewsAdapter(sortedList, findNavController())

            // Display the count of favorite articles
            binding.tvItemCount.text =
                viewModel.newsArticle.value?.size.toString() + " ${getString(R.string.items)}"

            // Handle delete all button click
            binding.btnDeleteAll.setOnClickListener {

                // Build an AlertDialog
                AlertDialog.Builder(requireContext(), R.style.CustomDialogTheme)
                    .setTitle(getString(R.string.articles_deleted_titel))
                    .setMessage(getString(R.string.are_you_sure_delete_all))
                    .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                        // If user confirms, delete all favorite articles
                        viewModel.deleteAllArticles()
                        viewModel.unsetComplete()
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.articles_deleted),
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

        // Deactivate system back button
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {}
    }
}