package de.syntax.androidabschluss.ui.search


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.RoundedCornersTransformation
import de.syntax.androidabschluss.R
import de.syntax.androidabschluss.databinding.FragmentSearchDetaliBinding
import de.syntax.androidabschluss.viewmodel.MeinViewModel

/**
 * A [Fragment] subclass responsible for displaying details of search results.
 */
class SearchDetailsFragment : Fragment() {

    // View model instance for handling article operations
    private val viewModel: MeinViewModel by activityViewModels()

    // View binding instance for this fragment
    private lateinit var binding: FragmentSearchDetaliBinding


    /**
     * Inflates the layout for this fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchDetaliBinding.inflate(inflater, container, false)
        return binding.root
    }


    /**
     * Sets up UI components and event listeners after the view has been created.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve article ID from arguments
        val id = arguments?.getString("id")

        // Observe changes in the music data and update the RecyclerView adapter
        viewModel.article.observe(viewLifecycleOwner) { articles ->
            val article = articles.find { it.title == id }
            article?.let {
                // Populate the UI with article details
                binding.tvTitle.text = it.title
                binding.tvDescription.text = it.description
                binding.tvContent.text = it.content
                binding.tvAuthorAuthor.text = it.author
                binding.tvSourceSource.text = it.source.name
                binding.tvDateDate.text = it.publishedAt

                binding.ivImge.load(it.urlToImage) {
                    placeholder(android.R.drawable.ic_menu_crop)
                    error(R.drawable.round_broken_image_25)
                    transformations(RoundedCornersTransformation(30f))
                }

                // Set up click listener for opening article URL
                binding.btnLink.setOnClickListener {
                    val url = article.url
                    if (!url.isNullOrEmpty()) {
                        // Open URL in the app's WebView
                        val action = SearchDetailsFragmentDirections.actionSearchDetailsFragmentToWebViewFragment(url)
                        findNavController().navigate(action)
                    }
                }


                // Set up click listener for saving article to favorites
                binding.btnSave.setOnClickListener {
                    article.source.name?.let { name ->
                        viewModel.insertArticle(
                            article.title.toString(),
                            article.description.toString(),
                            article.content.toString(),
                            article.author.toString(),
                            name,
                            article.publishedAt.toString(),
                            article.url.toString(),
                            article.urlToImage.toString()
                        )
                    }
                    viewModel.unsetComplete()
                    Toast.makeText(
                        context,
                        getString(R.string.article_fav),
                        Toast.LENGTH_SHORT
                    ).show()

                }

            }

            // Navigate up when "Done" button is clicked
            binding.btnDone.setOnClickListener {
                findNavController().navigateUp()
            }
        }

        // Deactivate system back button
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {}
    }

}
