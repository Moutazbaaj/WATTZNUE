package de.syntax.androidabschluss.ui.favorites


import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.RoundedCornersTransformation
import de.syntax.androidabschluss.R
import de.syntax.androidabschluss.databinding.FragmentFavoritesDetailsBinding
import de.syntax.androidabschluss.ui.HomeFragmentDirections
import de.syntax.androidabschluss.viewmodel.MeinViewModel


/**
 * A [Fragment] subclass responsible for displaying details of favorite articles.
 */

class FavoritesDetailsFragment : Fragment() {


    // View model instance for handling article operations
    private val viewModel: MeinViewModel by activityViewModels()


    // View binding instance for this fragment
    private lateinit var binding: FragmentFavoritesDetailsBinding


    /**
     * Inflates the layout for this fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    /**
     * Sets up UI components and event listeners after the view has been created.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve article ID from arguments
        val articleId = arguments?.getInt("id")

        // Observe the list of news articles
        viewModel.newsArticle.observe(viewLifecycleOwner) { articles ->

            // Find the article with the given ID
            val article = articles.find { it.id == articleId }
            article?.let {

                // Populate the UI with article details
                binding.tvTitle.text = it.titel
                binding.tvDescription.text = it.description
                binding.tvContent.text = it.content
                binding.tvAuthorAuthor.text = it.author
                binding.tvSourceSource.text = it.sourceName
                binding.tvDateDate.text = it.publishedAt
                binding.ivImge.load(it.imgUrl) {
                    placeholder(android.R.drawable.ic_menu_crop)
                    error(R.drawable.round_broken_image_25)
                    transformations(RoundedCornersTransformation(30f))
                }

                // Open URL in WebView when button is clicked
                binding.btnLink.setOnClickListener {
                    val url = article.url
                    if (url.isNotEmpty()) {
                        // Open URL in the app's WebView
                        val action =
                            FavoritesDetailsFragmentDirections.actionFavoritesDetailsFragmentToWebViewFragment(
                                url
                            )
                        findNavController().navigate(action)
                    }
                }
            }

            // Handle deleting article when the delete button is clicked
            binding.btnDeleteFav.setOnClickListener {
                // Build an AlertDialog
                AlertDialog.Builder(requireContext(), R.style.CustomDialogTheme)
                    .setTitle(getString(R.string.deleted_article))
                    .setMessage(getString(R.string.deleted_article_text))
                    .setPositiveButton(getString(R.string.yes)) { dialog, _ ->

                        // Delete the article if confirmed
                        if (article != null) {
                            viewModel.deleteArticle(article)
                            viewModel.unsetComplete()
                            findNavController().navigateUp()
                        }
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.article_deleted),
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

        // Navigate to favorite fragment when done button is clicked
        binding.btnDone.setOnClickListener {
            findNavController().navigate(R.id.favoritesFragment)
        }

        // Deactivate system back button
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {}
    }

}