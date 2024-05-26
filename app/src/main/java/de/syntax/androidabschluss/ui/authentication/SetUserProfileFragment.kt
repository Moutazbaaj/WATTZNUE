package de.syntax.androidabschluss.ui.authentication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import de.syntax.androidabschluss.R
import de.syntax.androidabschluss.data.datamodels.profilemodels.Profile
import de.syntax.androidabschluss.databinding.FragmentSerUserProfileBinding
import de.syntax.androidabschluss.viewmodel.FirebaseViewModel
import de.syntax.androidabschluss.viewmodel.MeinViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


/**
 * A [Fragment] subclass responsible for setting up user profile.
 */
class SetUserProfileFragment : Fragment() {

    // View model instance for Firebase operations
    private val viewModel: MeinViewModel by activityViewModels()

    // View binding instance for this fragment
    private lateinit var binding: FragmentSerUserProfileBinding

    // Variable to store selected image URI
    private var selectedImageUri: Uri? = null

    /**
     * Inflates the layout for this fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSerUserProfileBinding.inflate(layoutInflater)
        return binding.root
    }


    /**
     * Sets up UI components and event listeners after the view has been created.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Observes the profile LiveData to update UI with selected image
        viewModel.profile.observe(viewLifecycleOwner) { profil ->

            profil?.let {
                binding.profileImg.setImageURI(selectedImageUri)
            }

        }

        // Open image chooser when cardView is clicked
        binding.cardView.setOnClickListener {
            openImageChooser()
        }

        // Perform signup completion when "Done" button is clicked
        binding.btnSignupDone.setOnClickListener {
            signupDone()
        }

        // Deactivate system back button
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {}
    }


    /**
     * Function to open image chooser.
     */
    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        resultLauncher.launch(intent)
    }

    // Activity Result Launcher to handle the result of image picker
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                // Handle the selected image URI
                data?.data?.let { uri ->
                    selectedImageUri = uri
                    // Set the selected image
                    binding.profileImg.setImageURI(uri)
                }
            }
        }


    /**
     * Perform signup completion by saving user profile information.
     */
    private fun signupDone() {
        val userName = binding.userName.text.toString()
        if (userName.isNotEmpty() && selectedImageUri != null) {
            val imageFileName = "profile_${System.currentTimeMillis()}.jpg"
            val imageFile = File(requireContext().filesDir, imageFileName)

            try {
                requireContext().contentResolver.openInputStream(selectedImageUri!!)
                    ?.use { inputStream ->
                        FileOutputStream(imageFile).use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }

                // Insert profile into ViewModel
                viewModel.insertProfile(userName, imageFile.absolutePath)

                // restart the app and Navigate to home fragment
                restartApp()


            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(requireContext(), getString(R.string.error_saving_img), Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.username_empty),
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun restartApp() {
        val intent = requireActivity().baseContext.packageManager
            .getLaunchIntentForPackage(requireActivity().baseContext.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        if (intent != null) {
            startActivity(intent)
        }
        requireActivity().finish()
    }
}

