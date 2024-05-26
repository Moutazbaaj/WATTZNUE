package de.syntax.androidabschluss.ui.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import de.syntax.androidabschluss.data.Adapter.SourcesAdapter
import de.syntax.androidabschluss.databinding.FragmentNewsSourcesBinding
import de.syntax.androidabschluss.viewmodel.MeinViewModel


/**
 * A [Fragment] subclass responsible for displaying news sources.
 */
class NewsSourcesFragment : Fragment() {


    // View model instance for handling news operations
    private val viewModel: MeinViewModel by activityViewModels()

    // View binding instance for this fragment
    private lateinit var binding: FragmentNewsSourcesBinding


    /**
     * Inflates the layout for this fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewsSourcesBinding.inflate(layoutInflater)
        return binding.root
    }


    /**
     * Sets up UI components and event listeners after the view has been created.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve category from arguments
        val category = arguments?.getString("category").toString()

        // Observe the list of news sources
        viewModel.sources.observe(viewLifecycleOwner) { sources ->
            sources?.let {

                val name = viewModel.sources.value?.find { it.category == category }

                // Set up RecyclerView adapter with the list of news sources
                binding.rvSources.adapter = SourcesAdapter(it) { url ->

                    // Open URL in the app's WebView
                    val action = NewsSourcesFragmentDirections.actionNewsArticleFragmentToWebViewFragment(url)
                    findNavController().navigate(action)
                }

                // Set up refresh listener for SwipeRefreshLayout
                binding.swiperefreshlayout.setOnRefreshListener {
                    if (name != null) {
                        viewModel.loadSourcesData(category)
                        viewModel.unsetComplete()
                    }
                    binding.swiperefreshlayout.isRefreshing = false
                }
            }
        }

        // Navigate up when "Done" button is clicked
        binding.btnDone.setOnClickListener {
            val action = binding.btnDone.findNavController()
            action.navigateUp()
        }

        // Deactivate system back button
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {}
    }

}