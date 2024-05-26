package de.syntax.androidabschluss.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialog
import de.syntax.androidabschluss.R
import de.syntax.androidabschluss.data.datamodels.profilemodels.Profile
import de.syntax.androidabschluss.databinding.FragmentProfileBinding
import de.syntax.androidabschluss.viewmodel.FirebaseViewModel
import de.syntax.androidabschluss.viewmodel.MeinViewModel
import java.io.File
import java.io.FileOutputStream
import java.util.Locale


/**
 * A fragment representing the user's profile.
 */
class ProfileFragment : Fragment() {

    private val viewModel: MeinViewModel by activityViewModels()
    private val firebaseViewModel: FirebaseViewModel by activityViewModels()
    private lateinit var binding: FragmentProfileBinding


    // Variable to store selected image URI
    private var selectedImageUri: Uri? = null

    // Activity Result Launcher to handle the result of image picker
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    // Language options
    private val languages =
        listOf("en - English", "de - Deutsch", "ar - العربية", "fr - français", "es - español")

    // Country options
    private val countries = listOf(
        // List of countries with their codes

        "ae - الإمارات العربية المتحدة", // United Arab Emirates
        "ar - Argentina", // Argentina
        "at - Österreich", // Austria
        "au - Australia", // Australia
        "be - Belgique", // Belgium
        "bg - България", // Bulgaria
        "br - Brasil", // Brazil
        "ca - Canada", // Canada
        "ch - Schweiz", // Switzerland
        "cn - 中国", // China
        "co - Colombia", // Colombia
        "cu - Cuba", // Cuba
        "cz - Česká republika", // Czech Republic
        "de - Deutschland", // Germany
        "eg - مصر", // Egypt
        "es - España", // Spain
        "fr - France", // France
        "gb - United Kingdom", // United Kingdom
        "gr - Ελλάδα", // Greece
        "hk - 香港", // Hong Kong
        "hu -  Magyarország", // Hungary
        "id - Indonesia", // Indonesia
        "ie - Éire", // Ireland
        "in - भारत", // India
        "it - Italia", // Italy
        "jp - 日本", // Japan
        "kr - 대한민국", // South Korea
        "lt - Lietuva", // Lithuania
        "lv - Latvija", // Latvia
        "ma - المغرب", // Morocco
        "mx - Méxic", // Mexico
        "my - Malaysia", // Malaysia
        "ng - Nigeria", // Nigeria
        "nl - Nederland", // Netherlands
        "nz - New Zealand", // New Zealand
        "ph - Pilipinas", // Philippines
        "pl - Polska", // Poland
        "pt - Portugal", // Portugal
        "ro - România", // Romania
        "ru - Россия", // Russia
        "sa - المملكة العربية السعودية", // Saudi Arabia
        "se - Sverige", // Sweden
        "sg - Singapore", // Singapore
        "sk - Slovensko", // Slovakia
        "th - ประเทศไทย", // Thailand
        "tr - Türkiye", // Turkey
        "tw - 臺灣", // Taiwan
        "ua - Україна", // Ukraine
        "us - United States", // United States
        "ve - Venezuela", // Venezuela
        "za - South Africa" // South Africa
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Register for activity result launcher
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    // Handle the selected image URI
                    data?.data?.let { uri ->
                        selectedImageUri = uri

                    }
                }
            }

        //SharedPreferences to lode data from for the binding
        val sharedPreferences =
            requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)


        // Load user's profile data
        firebaseViewModel.currentUser.observe(viewLifecycleOwner) {
            if (it == null) {
                findNavController().navigate(R.id.loginFragment)
            }
        }


        viewModel.profile.observe(viewLifecycleOwner) { profil ->
            val user = profil.find { true }
            if (user != null) {
                binding.profileImg.load(user.profileImgUri)
                binding.userName.text = user.userName

                binding.profileEdit.setOnClickListener {

                    showChangeUsedDataDialog(user)
                }
            } else {
                binding.profileImg.setImageResource(R.drawable.round_broken_image_25) // Set a default image
                binding.userName.text = getString(R.string.guest) // Set a default username
                binding.profileEdit.setOnClickListener {
                    showChangeUsedDataDialogIfProfileEmpty()
                }
            }
        }

        binding.btnAbout.setOnClickListener {
            findNavController().navigate(R.id.aboutUsFragment)
        }


        binding.topNewwsButton.setOnClickListener {
            showCountriesSelectionBottomSheet()
        }


        // Load the selected country from SharedPreferences
        val selectedCountry = sharedPreferences.getString("Country", "us")
        // Update the TextView with the selected country
        selectedCountry?.let {
            binding.tvTopNew.text = it
        }


        binding.languageButton.setOnClickListener {
            showLanguageSelectionBottomSheet()
        }
        // Load the selected language from SharedPreferences
        val selectedLanguage = sharedPreferences.getString("Language", "en")
        // Update the TextView or UI component with the selected language
        selectedLanguage?.let {
            binding.tvLanguage.text = it
        }


        binding.recoverPass.setOnClickListener {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.popup_restart_password, null)
            val emailEditText = dialogView.findViewById<EditText>(R.id.emailEditText)

            AlertDialog.Builder(context, R.style.CustomDialogTheme)
                .setTitle(getString(R.string.reset_titel))
                .setMessage(getString(R.string.enter_email_to_reset))
                .setView(dialogView)
                .setPositiveButton(getString(R.string.reset)) { dialog, _ ->
                    val email = emailEditText.text.toString().trim()
                    if (email.isNotEmpty()) {
                        firebaseViewModel.resetPassword(email)
                    } else {
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

        binding.logoutButton.setOnClickListener {
            // Build an AlertDialog
            AlertDialog.Builder(requireContext(), R.style.CustomDialogTheme)
                .setTitle(getString(R.string.logout))
                .setMessage(getString(R.string.logout_text))
                .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                    // If user confirms
                    firebaseViewModel.logout()
                    dialog.dismiss() // Dismiss the dialog
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    // If a user cancels, do nothing
                    dialog.dismiss() // Dismiss the dialog
                }
                .show() // Show the AlertDialog
        }

        binding.deleteAccButton.setOnClickListener {
            // Build an AlertDialog
            AlertDialog.Builder(requireContext(), R.style.CustomDialogTheme)
                .setTitle(getString(R.string.delete_account))
                .setMessage(getString(R.string.delete_acc_text))
                .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                    // If user confirms

                    viewModel.deleteAllToDo()
                    viewModel.deleteAllArticles()
                    viewModel.deleteProfile()
                    firebaseViewModel.deleteAccount()
                    firebaseViewModel.logout()
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

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onResume() {
        super.onResume()

        // Lock the screen orientation to portrait mode
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onPause() {
        super.onPause()

        // Reset the screen orientation to allow normal behavior
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }


    // Function to show bottom sheet dialog for country selection
    @SuppressLint("InflateParams")
    private fun showCountriesSelectionBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetView =
            layoutInflater.inflate(R.layout.bottom_sheet_countries_selection, null)
        bottomSheetDialog.setContentView(bottomSheetView)

        val countriesListView: ListView = bottomSheetView.findViewById(R.id.countriesListView)
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            countries
        )
        countriesListView.adapter = adapter

        countriesListView.setOnItemClickListener { _, _, position, _ ->
            val selectedCountry = countries[position]
            val countryCode = getCountryCode(selectedCountry)

            // Store the selected language preference
            val sharedPreferences =
                requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            sharedPreferences.edit().apply {
                putString("Country", countryCode)
                apply()
            }
            viewModel.updateCountry(countryCode)

            binding.tvTopNew.text = countryCode

            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    // Function to show bottom sheet dialog for language selection
    @SuppressLint("InflateParams")
    private fun showLanguageSelectionBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetView =
            layoutInflater.inflate(R.layout.bottom_sheet_language_selection, null)
        bottomSheetDialog.setContentView(bottomSheetView)

        val languageListView: ListView = bottomSheetView.findViewById(R.id.languageListView)
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            languages
        )
        languageListView.adapter = adapter

        languageListView.setOnItemClickListener { _, _, position, _ ->
            val selectedLanguage = languages[position]
            val languageCode = getLanguageCode(selectedLanguage)

            // Set locale immediately
            setLocale(requireContext(), languageCode)

            // Store the selected language preference
            val sharedPreferences =
                requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            sharedPreferences.edit().apply {
                putString("Language", languageCode)
                apply()
            }

            binding.tvLanguage.text = languageCode

            bottomSheetDialog.dismiss()
            // Restart the app after a short delay to ensure the dialog dismisses properly
            Handler(Looper.getMainLooper()).postDelayed({
                // Restart the app
                restartApp()
            }, 1000)
        }

        bottomSheetDialog.show()
    }

    // Function to restart the app
    private fun restartApp() {
        val intent = requireActivity().baseContext.packageManager
            .getLaunchIntentForPackage(requireActivity().baseContext.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        if (intent != null) {
            startActivity(intent)
        }
        requireActivity().finish()
    }

    // Function to get language code from language string
    private fun getLanguageCode(language: String): String {
        return when (language) {
            "en - English" -> "en"
            "de - Deutsch" -> "de"
            "ar - العربية" -> "ar"
            "fr - français" -> "fr"
            "es - español" -> "es"
            else -> "en"
        }
    }

    // Function to get country code from country string
    private fun getCountryCode(country: String): String {
        return when (country) {
            "ae - الإمارات العربية المتحدة" -> "ae" // United Arab Emirates
            "ar - Argentina" -> "ar" // Argentina
            "at - Österreich" -> "at" // Austria
            "au - Australia" -> "au" // Australia
            "be - Belgique" -> "be" // Belgium
            "bg - България" -> "bg" // Bulgaria
            "br - Brasil" -> "br" // Brazil
            "ca - Canada" -> "ca" // Canada
            "ch - Schweiz" -> "ch" // Switzerland
            "cn - 中国" -> "cn" // China
            "co - Colombia" -> "co" // Colombia
            "cu - Cuba" -> "cu" // Cuba
            "cz - Česká republika" -> "cz" // Czech Republic
            "de - Deutschland" -> "de" // Germany
            "eg - مصر" -> "eg" // Egypt
            "es - España" -> "es" // Spain
            "fr - France" -> "fr" // France
            "gb - United Kingdom" -> "gb" // United Kingdom
            "gr - Ελλάδα" -> "gr" // Greece
            "hk - 香港" -> "hk" // Hong Kong
            "hu - Magyarország" -> "hu" // Hungary
            "id - Indonesia" -> "id" // Indonesia
            "ie - Éire" -> "ie" // Ireland
            "in - भारत" -> "in" // India
            "it - Italia" -> "it" // Italy
            "jp - 日本" -> "jp" // Japan
            "kr - 대한민국" -> "kr" // South Korea
            "lt - Lietuva" -> "lt" // Lithuania
            "lv - Latvija" -> "lv" // Latvia
            "ma - المغرب" -> "ma" // Morocco
            "mx - Méxic" -> "mx" // Mexico
            "my - Malaysia" -> "my" // Malaysia
            "ng - Nigeria" -> "ng" // Nigeria
            "nl - Nederland" -> "nl" // Netherlands
            "nz - New Zealand" -> "nz" // New Zealand
            "ph - Pilipinas" -> "ph" // Philippines
            "pl - Polska" -> "pl" // Poland
            "pt - Portugal" -> "pt" // Portugal
            "ro - România" -> "ro" // Romania
            "ru - Россия" -> "ru" // Russia
            "sa - المملكة العربية السعودية" -> "sa" // Saudi Arabia
            "se - Sverige" -> "se" // Sweden
            "sg - Singapore" -> "sg" // Singapore
            "sk - Slovensko" -> "sk" // Slovakia
            "th - ประเทศไทย" -> "th" // Thailand
            "tr - Türkiye" -> "tr" // Turkey
            "tw - 臺灣" -> "tw" // Taiwan
            "ua - Україна" -> "ua" // Ukraine
            "us - United States" -> "us" // United States
            "ve - Venezuela" -> "ve" // Venezuela
            "za - South Africa" -> "za" // South Africa
            else -> "us"
        }
    }

    // Function to set locale
    private fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        context.resources.updateConfiguration(
            configuration,
            context.resources.displayMetrics
        )
    }

    // Function to show dialog for editing user data
    @SuppressLint("MissingInflatedId")
    private fun showChangeUsedDataDialog(user: Profile) {
        val dialogBuilder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialogStyle)
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.popup_pofile_edit, null)
        dialogBuilder.setView(dialogView)

        val profileImgEdit = dialogView.findViewById<ImageView>(R.id.profile_img_edit)

        // Load and display the existing profile image
        profileImgEdit.load(user.profileImgUri)

        //val done = dialogView.findViewById<TextView>(R.id.textext)

        // Set click listener to select a new profile picture
        profileImgEdit.setOnClickListener {
            openImageChooserForProfilePicture()

            //done.text = getString(R.string.done)
        }


        val editTextUsername = dialogView.findViewById<EditText>(R.id.user_name_edit)
        editTextUsername.setText(user.userName)

        // Dialog title
        dialogBuilder.setTitle(getString(R.string.edit_profile))

        // Positive button (Save)
        dialogBuilder.setPositiveButton(getString(R.string.save)) { _, _ ->
            val newUsername = editTextUsername.text.toString()
            if (newUsername.isNotEmpty() || selectedImageUri != null) {

                // Update profile with the new username and selected image URI if available
                if (selectedImageUri != null) {
                    selectedImageUri?.let { uri ->

                        // Load and display the selected image in the profileImgEdit
                        profileImgEdit.load(uri)

                        val imagePath = saveImageInternally(uri)
                        imagePath?.let { path ->
                            viewModel.updateProfile(user.id, newUsername, path)
                        }
                    }
                } else {
                    // Only update the username
                    viewModel.updateProfile(user.id, newUsername, user.profileImgUri)
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.username_empty),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Negative button (Cancel)
        dialogBuilder.setNegativeButton(getString(R.string.cancel)) { _, _ ->
            // Do nothing
        }

        // Create and show the dialog
        dialogBuilder.create().show()
    }

    // Function to show dialog for editing user data when profile is empty
    @SuppressLint("MissingInflatedId")
    private fun showChangeUsedDataDialogIfProfileEmpty() {
        val dialogBuilder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialogStyle)
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.popup_pofile_edit, null)
        dialogBuilder.setView(dialogView)

        val profileImgEdit = dialogView.findViewById<ImageView>(R.id.profile_img_edit)


        //val done = dialogView.findViewById<TextView>(R.id.textext)

        // Set click listener to select a new profile picture
        profileImgEdit.setOnClickListener {
            openImageChooserForProfilePicture()

            //done.text = getString(R.string.done)
        }

        val editTextUsername = dialogView.findViewById<EditText>(R.id.user_name_edit)


        // Dialog title
        dialogBuilder.setTitle(getString(R.string.edit_profile))

        // Positive button (Save)
        dialogBuilder.setPositiveButton(getString(R.string.save)) { _, _ ->
            val newUsername = editTextUsername.text.toString()
            if (newUsername.isNotEmpty()) {

                // Update profile with the new username and selected image URI if available
                if (selectedImageUri != null) {
                    selectedImageUri?.let { uri ->

                        // Load and display the selected image in the profileImgEdit
                        profileImgEdit.load(uri)

                        val imagePath = saveImageInternally(uri)
                        imagePath?.let { path ->
                            viewModel.insertProfile(newUsername, path)
                        }
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.username_img_empty),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        // Negative button (Cancel)
        dialogBuilder.setNegativeButton(getString(R.string.cancel)) { _, _ ->
            // Do nothing
        }

        // Create and show the dialog
        dialogBuilder.create().show()
    }

    // Function to save the selected image internally and return the file path
    private fun saveImageInternally(uri: Uri): String? {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val file = File(requireContext().filesDir, "profile_image.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return file.absolutePath
    }

    // Function to open image chooser for profile picture
    private fun openImageChooserForProfilePicture() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        resultLauncher.launch(intent)
    }
}
