package de.syntax.androidabschluss.ui.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import de.syntax.androidabschluss.R
import de.syntax.androidabschluss.databinding.FragmentRegistrationBinding
import de.syntax.androidabschluss.viewmodel.FirebaseViewModel


/**
 * A [Fragment] subclass responsible for user registration.
 */
class RegistrationFragment : Fragment() {

    // View model instance for Firebase operations
    private val viewModel: FirebaseViewModel by activityViewModels()

    // View binding instance for this fragment
    private lateinit var binding: FragmentRegistrationBinding


    /**
     * Inflates the layout for this fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(layoutInflater)
        return binding.root
    }

    /**
     * Sets up UI components and event listeners after the view has been created.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the context in the ViewModel
        viewModel.setContext(requireContext())

        // Navigate back when the cancel button is clicked
        binding.signupCancelButton.setOnClickListener {
            findNavController().navigateUp()
        }

        // Perform signup when signup button is clicked
        binding.signupSignupButton.setOnClickListener {
            signup()
        }

        // Observes the current user's authentication status
        viewModel.currentUser.observe(viewLifecycleOwner) {
            if (it != null) {
                // Navigate to set user profile fragment if user is authenticated
                findNavController().navigate(R.id.setUserProfileFragment)
            }
        }

        // Deactivate system back button
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {}
    }


    /**
     * Performs user signup using provided email and password.
     */
    private fun signup() {
        val email = binding.signupMail.text.toString()
        val password = binding.signupPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            viewModel.signup(email, password)
        } else {
            // If email or password is empty, set error state for the respective fields
            if (email.isEmpty()) {
                setErrorState(binding.signupMail, getString(R.string.valid_email))
            }
            if (password.isEmpty()) {
                setErrorState(binding.signupPassword, getString(R.string.incorrect_password))
            }
        }
    }

    // Function to set error state for an EditText based on the error message
    private fun setErrorState(editText: EditText, errorMessage: String) {
        // Set the background drawable
        editText.setBackgroundResource(R.drawable.edit_text_background_error)

        // Set the error message
        editText.requestFocus()
        editText.setError(errorMessage, null)
    }

}