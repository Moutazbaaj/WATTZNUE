package de.syntax.androidabschluss.ui.news

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import de.syntax.androidabschluss.R
import de.syntax.androidabschluss.databinding.FragmentNewsBinding
import de.syntax.androidabschluss.viewmodel.MeinViewModel


/**
 * A [Fragment] subclass responsible for displaying news categories.
 */
class NewsFragment : Fragment() {

    // View model instance for handling news operations
    private val viewModel: MeinViewModel by activityViewModels()

    // View binding instance for this fragment
    private lateinit var binding: FragmentNewsBinding


    /**
     * Inflates the layout for this fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentNewsBinding.inflate(layoutInflater)
        return binding.root
    }


    /**
     * Sets up UI components and event listeners after the view has been created.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Set up listeners for each news category
        with(binding) {
            ivBusiness.setImageResource(R.drawable.business)
            cvBusiness.setOnClickListener { navigateToNewsSourcesFragment("business") }

            ivEntertainment.setImageResource(R.drawable.entertainment)
            cvEntertainment.setOnClickListener { navigateToNewsSourcesFragment("entertainment") }

            ivHealth.setImageResource(R.drawable.health)
            cvGeneral.setOnClickListener { navigateToNewsSourcesFragment("general") }

            ivGenera.setImageResource(R.drawable.general)
            cvHealth.setOnClickListener { navigateToNewsSourcesFragment("health") }

            ivScience.setImageResource(R.drawable.science)
            cvScience.setOnClickListener { navigateToNewsSourcesFragment("science") }

            ivSports.setImageResource(R.drawable.sports)
            cvSports.setOnClickListener { navigateToNewsSourcesFragment("sports") }

            ivTechnology.setImageResource(R.drawable.technology)
            cvTechnology.setOnClickListener { navigateToNewsSourcesFragment("technology") }
        }

        // Deactivate system back button
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {}
    }


    /**
     * Locks the screen orientation to portrait mode when the fragment is resumed.
     */
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onResume() {
        super.onResume()

        // Lock the screen orientation to portrait mode
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    /**
     * Resets the screen orientation to allow normal behavior when the fragment is paused.
     */
    override fun onPause() {
        super.onPause()

        // Reset the screen orientation to allow normal behavior
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    /**
     * Navigates to the news sources fragment with the selected category.
     */
    private fun navigateToNewsSourcesFragment(category: String) {
        viewModel.loadSourcesData(category)
        viewModel.unsetComplete()
        val action = findNavController()
        action.navigate(NewsFragmentDirections.actionNewsFragmentToNewsArticleFragment(category))
    }
}
