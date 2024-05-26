package de.syntax.androidabschluss.ui.authentication

import android.app.AlertDialog
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import de.syntax.androidabschluss.R
import de.syntax.androidabschluss.databinding.FragmentLoginBinding
import de.syntax.androidabschluss.viewmodel.FirebaseViewModel

/**
 * A [Fragment] subclass responsible for handling user login functionality.
 */
class LoginFragment : Fragment() {

    // View model instance for Firebase operations
    private val viewModel: FirebaseViewModel by activityViewModels()

    // View binding instance for this fragment
    private lateinit var binding: FragmentLoginBinding


    /**
     * Inflates the layout for this fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    /**
     * Sets up UI components and event listeners after the view has been created.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the context in the ViewModel
        viewModel.setContext(requireContext())

        // Observes the current user's authentication status
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                // Navigate to home fragment if user is authenticated
                findNavController().navigate(R.id.homeFragment)
            }
        }


        // Navigate to registration fragment when signup button is clicked
        binding.loginSignupButton.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegistrationFragment())
        }

        // Perform login when login button is clicked
        binding.loginButton.setOnClickListener {
            val email = binding.loginEmailEdit.text.toString()
            val password = binding.loginPasswordEdit.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(email, password)
            } else {
                // If email or password is empty, set error state for the respective fields
                if (email.isEmpty()) {
                    setErrorState(binding.loginEmailEdit, getString(R.string.valid_email))
                }
                if (password.isEmpty()) {
                    setErrorState(binding.loginPasswordEdit, getString(R.string.incorrect_password))
                }
            }
        }


        // Handle password recovery when "Recover Password" is clicked
        binding.recoverPass.setOnClickListener {
            val dialogView =
                LayoutInflater.from(context).inflate(R.layout.popup_restart_password, null)
            val emailEditText = dialogView.findViewById<EditText>(R.id.emailEditText)

            AlertDialog.Builder(context, R.style.CustomDialogTheme)
                .setTitle(getString(R.string.reset_titel))
                .setMessage(getString(R.string.enter_email_to_reset))
                .setView(dialogView)
                .setPositiveButton(getString(R.string.reset)) { dialog, _ ->
                    val email = emailEditText.text.toString().trim()
                    if (email.isNotEmpty()) {
                        viewModel.resetPassword(email)
                    } else {
                        // Show an error message if email is empty
                        Toast.makeText(
                            context,
                            getString(R.string.enter_email),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        // Deactivate system back button
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {}

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
