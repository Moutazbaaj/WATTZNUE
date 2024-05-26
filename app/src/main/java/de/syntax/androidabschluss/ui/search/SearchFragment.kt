package de.syntax.androidabschluss.ui.search

import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import de.syntax.androidabschluss.R
import de.syntax.androidabschluss.data.Adapter.NewsAdapter
import de.syntax.androidabschluss.databinding.FragmentSearchBinding
import de.syntax.androidabschluss.viewmodel.MeinViewModel


/**
 * A [Fragment] subclass responsible for searching articles.
 */
class SearchFragment : Fragment() {

    // View model instance for handling article operations
    private val viewModel: MeinViewModel by activityViewModels()

    // View binding instance for this fragment
    private lateinit var binding: FragmentSearchBinding

    // TextWatcher for monitoring changes in the search input field
    private lateinit var textWatcher: TextWatcher


    /**
     * Inflates the layout for this fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Sets up UI components and event listeners after the view has been created.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Save state "position" of the RV list
        var recyclerViewState1: Parcelable


        // Observe changes in the music data and update the RecyclerView adapter
        viewModel.article.observe(viewLifecycleOwner) { article ->

            recyclerViewState1 = binding.rvResults.layoutManager?.onSaveInstanceState()!!
            // Restore state
            binding.rvResults.layoutManager?.onRestoreInstanceState(recyclerViewState1)

            binding.rvResults.adapter = NewsAdapter(article, findNavController())
        }


        // Set up text watcher for the search input field
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if ((s?.length ?: 0) >= 3) {
                    viewModel.loadNewsData(s.toString())
                    viewModel.unsetComplete()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }

        // Add text watcher to the search input field
        binding.tietSearch.addTextChangedListener(textWatcher)


        // Set up refresh listener for SwipeRefreshLayout
        binding.swiperefreshlayout.setOnRefreshListener {
            viewModel.loadNewsData(binding.tietSearch.text.toString())
            viewModel.unsetComplete()
            binding.swiperefreshlayout.isRefreshing = false
        }


        // Observe loading status and update UI accordingly
        viewModel.loading.observe(viewLifecycleOwner) {status->
            when (status!!) {
                MeinViewModel.ApiStatus.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.errorImage.visibility = View.INVISIBLE
                }

                MeinViewModel.ApiStatus.DONE -> {
                    binding.progressBar.visibility = View.GONE
                    binding.errorImage.visibility = View.INVISIBLE
                }

                MeinViewModel.ApiStatus.ERROR -> {
                    binding.errorImage.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE

                }
            }
        }

        // Deactivate system back button
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {}
    }

    /**
     * Removes the text watcher when the view is destroyed to prevent memory leaks.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        binding.tietSearch.removeTextChangedListener(textWatcher)
    }

}