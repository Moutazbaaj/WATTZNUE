package de.syntax.androidabschluss.ui.todo

import de.syntax.androidabschluss.util.AudioManager
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore.ACTION_IMAGE_CAPTURE
import android.provider.Settings
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.camera.core.ExperimentalGetImage
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions.DEFAULT_OPTIONS
import de.syntax.androidabschluss.R
import de.syntax.androidabschluss.databinding.FragmentToDoDetailsBinding
import de.syntax.androidabschluss.util.AlarmReceiver
import de.syntax.androidabschluss.util.AlarmReceiver.Companion.CHANNEL_ID
import de.syntax.androidabschluss.viewmodel.MeinViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


/**
 * Fragment responsible for displaying and managing the details of a to-do item.
 */
@ExperimentalGetImage
class ToDoDetailsFragment : Fragment(), AudioManager.AudioManagerCallback {

    // ViewModel instance for handling to-do operations
    private val viewModel: MeinViewModel by activityViewModels()

    // View binding instance for this fragment
    private lateinit var binding: FragmentToDoDetailsBinding

    //instance for popup
    private lateinit var popupWindow: PopupWindow

    //instance for AudioManger
    private lateinit var audioManager: AudioManager

    // Text recognition client for ML Kit
    private lateinit var textRecognitionClient: TextRecognizer

    // variable to save the selected time value
    private var selectedTimeInMillis: Long? = null

    // Flags to keep track of various states
    private var isFavorite = false
    private var isImportant = false
    private var isReminder = false

    /**
     * Inflates the layout for this fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentToDoDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    /**
     * Sets up UI components and event listeners after the view has been created.
     */
    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("SetTextI18n", "MissingInflatedId", "InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check and request necessary permissions
        checkAndRequestPermissionsMic()
        checkAndRequestPermissionsCam()


        // Initialize the text recognition client if it's not already initialized
        if (!::textRecognitionClient.isInitialized) {
            textRecognitionClient = TextRecognition.getClient(DEFAULT_OPTIONS)
        }

        // Initialize AudioManager
        audioManager = AudioManager(requireContext(), this)


        // Set up click listeners for various UI elements
        setOnClickListeners()

        // Deactivate system back button
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {}

    }

    /**
     * Sets click listeners for UI elements.
     */
    @SuppressLint("InflateParams")
    @RequiresApi(Build.VERSION_CODES.S)
    private fun setOnClickListeners() {
        // Save To-Do button click listener
        binding.btnSaveTodo.setOnClickListener {
            val titel = binding.tvTitel.text.toString()
            val text = binding.tvText.text.toString()
            val creationDateTime =
                SimpleDateFormat("dd-MM-yyyy hh:mm:ss a", Locale.getDefault()).format(Date())


            if (titel.isNotEmpty() && text.isNotEmpty()) {
                viewModel.insertTODo(
                    titel,
                    text,
                    creationDateTime,
                    isFavorite,
                    isImportant,
                    audioFilePath = audioManager.audioFilePath,
                    reminderDateTime = selectedTimeInMillis,
                )
                viewModel.unsetComplete()

                // Set the alarm if a reminder time is selected
                if (selectedTimeInMillis != null && selectedTimeInMillis != 0L) {
                    setAlarm(titel, text, selectedTimeInMillis!!)
                }

                // Reset selectedTimeInMillis after saving to.do item
                selectedTimeInMillis = 0

                // Stop audio playback and recording
                audioManager.stopPlayback()
                audioManager.stopRecording()

                // Show a toast message indicating the successful creation of to-do
                Toast.makeText(
                    requireContext(), getString(R.string.todo_created),
                    Toast.LENGTH_SHORT
                ).show()

                // Navigate back to To-Do list
                findNavController().navigate(R.id.toDoFragment)
            } else {
                // If the title or text is empty, show a toast message
                Toast.makeText(
                    context, getString(R.string.text_fild_shoudnot_be_empty), Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Done button click listener
        binding.btnDone.setOnClickListener {

            // Stop audio playback and recording
            audioManager.stopPlayback()
            audioManager.stopRecording()

            // Navigate back to To-Do categories
            findNavController().navigate(R.id.toDoCategoriesFragment)
        }

        // On back press, stop recording or playback if active
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {}

        // Favorite button click listener
        binding.btnFav.setOnClickListener {
            // Toggle the favorite flag
            isFavorite = !isFavorite
            // Update UI
            updateFavoriteButtonUI()
        }

        // Important button click listener
        binding.btnImpo.setOnClickListener {
            // Toggle the important flag
            isImportant = !isImportant
            // Update UI
            updateImportantButtonUI()
        }

        // Set reminder button click listener
        binding.btnSetReminder.setOnClickListener {
            // Check if notifications are enabled
            if (!isNotificationEnabled()) {
                // If notifications are not enabled, show the permission dialog
                showNotificationPermissionDialog()
            } else {
                // If notifications are enabled, proceed with setting or clearing the reminder
                if (!isReminder) {
                    // If a reminder is not set, show date and time picker
                    showDateTimePicker()
                } else {
                    // If a reminder is already set, clear the reminder
                    clearAlarm()
                }
            }
        }

        // Scan button click listener
        binding.btnScan.setOnClickListener {
            // Check camera permissions before launching camera intent
            if (!checkAndRequestPermissionsCam()) {
                // If notifications are not enabled, show the permission dialog
                showCameraPermissionDialog()
            } else {
                // Launch camera intent
                launchCameraIntent()
            }
        }

        // Show the popup window when the user clicks the Add Voice Note button
        binding.btnAddVoiceNote.setOnClickListener {
            // Check microphone permissions before showing the popup window
            if (!checkAndRequestPermissionsMic()) {
                // If microphone permission is not granted, show permission dialog
                showRecordPermissionDialog()
            } else {
                popupWindow.elevation = 20f // Set the elevation to increase shadow effect
                popupWindow.setBackgroundDrawable(ColorDrawable(Color.WHITE))
                popupWindow.showAtLocation(
                    it,
                    Gravity.CENTER,
                    0,
                    0
                )
            }
        }

        // Inflate the popup layout for voice note
        val popupView = layoutInflater.inflate(R.layout.popup_todo_voicenote, null)
        // Create and configure the popup window
        popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        // Set click listeners for buttons in the popup layout
        popupView.findViewById<Button>(R.id.btnRecord).setOnClickListener {
            audioManager.startRecording()
        }

        popupView.findViewById<Button>(R.id.btnStop).setOnClickListener {
            audioManager.stopRecording()
        }

        popupView.findViewById<Button>(R.id.btnPlay).setOnClickListener {
            audioManager.startPlayback()
        }

        popupView.findViewById<Button>(R.id.btnStopPlay).setOnClickListener {
            audioManager.stopPlayback()
        }

    }

    // Function to show the notification permission dialog
    private fun showNotificationPermissionDialog() {
        val builder = AlertDialog.Builder(requireContext(), R.style.CustomDialogTheme)
        builder.setTitle(getString(R.string.enable_notifcation))
        builder.setMessage(getString(R.string.enable_notifcation_text))
        builder.setPositiveButton(getString(R.string.settings)) { _, _ ->
            // Take the user to the app's notification settings
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", requireActivity().packageName, null)
            intent.data = uri
            startActivity(intent)
        }
        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    // Function to show the Recording permission dialog
    private fun showRecordPermissionDialog() {
        val builder = AlertDialog.Builder(requireContext(), R.style.CustomDialogTheme)
        builder.setTitle(getString(R.string.enabel_mic))
        builder.setMessage(getString(R.string.enabel_mic_text))
        builder.setPositiveButton(getString(R.string.settings)) { _, _ ->
            // Take the user to the app's notification settings
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", requireActivity().packageName, null)
            intent.data = uri
            startActivity(intent)
        }
        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    // Function to show the Camera permission dialog
    private fun showCameraPermissionDialog() {
        val builder = AlertDialog.Builder(requireContext(), R.style.CustomDialogTheme)
        builder.setTitle(getString(R.string.enabel_cam))
        builder.setMessage(getString(R.string.enabel_cam_text))
        builder.setPositiveButton(getString(R.string.settings)) { _, _ ->
            // Take the user to the app's notification settings
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", requireActivity().packageName, null)
            intent.data = uri
            startActivity(intent)
        }
        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    // Function to check if notifications are enabled
    private fun isNotificationEnabled(): Boolean {
        val notificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.importance != NotificationManager.IMPORTANCE_NONE
        } else {
            notificationManager.areNotificationsEnabled()
        }
    }

    //////// functions ////////

    // Function to update UI of favorite button based on its state
    private fun updateFavoriteButtonUI() {
        val text =
            if (isFavorite) getString(R.string.favorite) else getString(R.string.add_to_favorites)
        val textColor = if (isFavorite) Color.RED else Color.BLACK
        binding.btnFav.text = text
        binding.btnFav.setTextColor(textColor)
    }

    // Function to update UI of important button based on its state
    private fun updateImportantButtonUI() {
        val text =
            if (isImportant) getString(R.string.important) else getString(R.string.add_to_important)
        val textColor = if (isImportant) Color.RED else Color.BLACK
        binding.btnImpo.text = text
        binding.btnImpo.setTextColor(textColor)
    }

    // Function to handle recording state and update UI accordingly
    private fun updateRecordingUI(isRecording: Boolean) {
        // Update UI based on recording state
        val popupView = popupWindow.contentView
        if (isRecording) {
            popupView.findViewById<Button>(R.id.btnRecord).setTextColor(Color.RED)
            popupView.findViewById<Button>(R.id.btnStop).setTextColor(Color.BLACK)
            popupView.findViewById<Button>(R.id.btnStopPlay).setTextColor(Color.BLACK)
            popupView.findViewById<Button>(R.id.btnPlay).setTextColor(Color.BLACK)
        } else {
            popupView.findViewById<Button>(R.id.btnRecord).setTextColor(Color.BLACK)
            popupView.findViewById<Button>(R.id.btnStopPlay).setTextColor(Color.BLACK)
            popupView.findViewById<Button>(R.id.btnPlay).setTextColor(Color.BLACK)
            popupView.findViewById<Button>(R.id.btnStop).setTextColor(Color.BLACK)
        }
    }

    // Function to handle playback state and update UI accordingly
    private fun updatePlaybackUI(isPlaying: Boolean) {
        // Update UI based on playback state
        val popupView = popupWindow.contentView
        if (isPlaying) {
            popupView.findViewById<Button>(R.id.btnRecord).setTextColor(Color.BLACK)
            popupView.findViewById<Button>(R.id.btnStop).setTextColor(Color.BLACK)
            popupView.findViewById<Button>(R.id.btnStopPlay).setTextColor(Color.BLACK)
            popupView.findViewById<Button>(R.id.btnPlay).setTextColor(Color.RED)
        } else {
            popupView.findViewById<Button>(R.id.btnRecord).setTextColor(Color.BLACK)
            popupView.findViewById<Button>(R.id.btnStop).setTextColor(Color.BLACK)
            popupView.findViewById<Button>(R.id.btnPlay).setTextColor(Color.BLACK)
            popupView.findViewById<Button>(R.id.btnStopPlay).setTextColor(Color.BLACK)
        }
    }

    // Function to update UI of set reminder button based on its state
    private fun updateSetReminderButtonUI() {
        val text =
            if (selectedTimeInMillis != null) getString(R.string.reminder_remove) else getString(R.string.reminder_set)
        val textColor = if (selectedTimeInMillis != null) Color.RED else Color.BLACK
        binding.btnSetReminder.text = text
        binding.btnSetReminder.setTextColor(textColor)
    }

    override fun onRecordingStarted() {
        // Handle UI updates when recording starts
        updateRecordingUI(true)
    }

    override fun onRecordingStopped() {
        // Handle UI updates when recording stops
        updateRecordingUI(false)
    }

    override fun onPlaybackStarted() {
        // Handle UI updates when playback starts
        updatePlaybackUI(true)
    }

    override fun onPlaybackStopped() {
        // Handle UI updates when playback stops
        updatePlaybackUI(false)
    }

    // Function to update the progress of recording or playback
    @SuppressLint("SetTextI18n")
    override fun onProgressUpdated(
        elapsedTimeMillis: Long,
        remainingTimeMillis: Long,
        isRecording: Boolean
    ) {
        // Convert milliseconds to format time strings
        val formattedElapsedTime = viewModel.formatTime(elapsedTimeMillis)
        val formattedRemainingTime = viewModel.formatTime(remainingTimeMillis)

        // Update UI elements based on whether it's recording or playing
        val textToShow = if (isRecording) {
            // If recording, show formatted elapsed time
            formattedElapsedTime
        } else {
            // If playing, show formatted remaining time
            formattedRemainingTime
        }
        // Update the recordDurationTextView with the appropriate text
        val popupView = popupWindow.contentView
        popupView.findViewById<TextView>(R.id.recordDurationTextView).text = textToShow
    }


    // Function to show the date and time picker after notification permission is granted
    private fun showDateTimePicker() {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.CustomDialogTheme,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val timePickerDialog = TimePickerDialog(
                    requireContext(),
                    R.style.CustomDialogTheme,
                    { _: TimePicker, hourOfDay: Int, minute: Int ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        selectedTimeInMillis = calendar.timeInMillis

                        val timeFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())

                        Toast.makeText(
                            requireContext(),
                            "${getString(R.string.reminder_set_fo)} ${timeFormat.format(calendar.time)}",
                            Toast.LENGTH_SHORT
                        ).show()
                        updateSetReminderButtonUI()
                        isReminder = true

                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
                )

                timePickerDialog.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }


    // Function to set an alarm for the reminder
    @SuppressLint("ScheduleExactAlarm")
    @RequiresApi(Build.VERSION_CODES.S)
    private fun setAlarm(title: String, text: String, reminderDateTime: Long) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val requestCode = generateUniqueAlarmRequestCode()


        val alarmIntent = Intent(requireContext(), AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_TITLE, title)
            putExtra(AlarmReceiver.EXTRA_TEXT, text)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            requestCode,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Set the alarm at the selected time
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            reminderDateTime,
            pendingIntent
        )

        // Create the notification channel if it doesn't exist
        val uniqueChannelId = generateUniqueChannelId()
        createNotificationChannelIfNeeded(uniqueChannelId, title, text)
    }

    // Function to create notification channel if needed
    private fun createNotificationChannelIfNeeded(
        channelId: String,
        title: String,
        description: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val existingChannel = notificationManager.getNotificationChannel(channelId)
            if (existingChannel == null) {
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(channelId, title, importance).apply {
                    this.description = description
                }
                // Register the channel with the system
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    // Function to clear the set reminder
    private fun clearAlarm() {
        // Clear the selected timeInMillis and update UI
        selectedTimeInMillis = null
        updateSetReminderButtonUI()
        // Update isReminder flag
        isReminder = false
        // notify the user that the reminder has been cleared
        Toast.makeText(requireContext(), getString(R.string.reminder_cleard), Toast.LENGTH_SHORT)
            .show()
    }

    /// Function to check and request the necessary permissions Microphone
    private fun checkAndRequestPermissionsMic(): Boolean {
        // Check and request RECORD_AUDIO permissions
        val audioPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.RECORD_AUDIO
        )

        val permissionsToRequest = mutableListOf<String>()
        if (audioPermission != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.RECORD_AUDIO)
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissions(
                permissionsToRequest.toTypedArray(),
                1
            )
            return false
        }
        return true
    }


    /// Function to check and request the necessary permissions Camera
    private fun checkAndRequestPermissionsCam(): Boolean {
        // Check and request CAMERA permissions
        val cameraPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        )


        val permissionsToRequest = mutableListOf<String>()
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissions(
                permissionsToRequest.toTypedArray(),
                1
            )
            return false
        }
        return true
    }


    // Function to generate a unique alarm request code
    private fun generateUniqueAlarmRequestCode(): Int {
        return System.currentTimeMillis().toInt()
    }

    // Function to generate a unique notification channel ID
    private fun generateUniqueChannelId(): String {
        return "$CHANNEL_ID${System.currentTimeMillis()}"
    }

    // Function to launch the camera intent
    private fun launchCameraIntent() {
        // Create a new intent for capturing images
        val captureImageIntent = Intent(ACTION_IMAGE_CAPTURE)
        // Start the activity to capture the image
        startActivityForResult(captureImageIntent, 1)
    }

    // Function to handle the result of the camera intent
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            // Get the captured image from the data Intent
            val imageBitmap = data?.extras?.get("data") as Bitmap?
            // Convert the Bitmap to InputImage
            val inputImage = imageBitmap?.let { InputImage.fromBitmap(it, 0) }

            // Process the image for text recognition
            inputImage?.let { recognizeText(it) }
        }
    }

    // Function to process the captured image for text recognition
    private fun recognizeText(inputImage: InputImage) {
        textRecognitionClient.process(inputImage)
            .addOnSuccessListener { visionText ->
                // Task completed successfully
                // Extract and handle recognized text
                handleRecognizedText(visionText)
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                // Handle failure
                Toast.makeText(
                    requireContext(),
                    "Text recognition failed: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    // Function to handle recognized text
    private fun handleRecognizedText(visionText: Text) {
        // Extract text from the recognized text result
        val extractedText = visionText.text

        // display the text in a TextView
        binding.tvText.append(extractedText + "\n") // Append the recognized text to the existing text
    }

    // Function to lock screen orientation to portrait mode onResume
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onResume() {
        super.onResume()

        // Lock the screen orientation to portrait mode
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    // Function to reset screen orientation onPause
    override fun onPause() {
        super.onPause()

        // Reset the screen orientation to allow normal behavior
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    // Function to clean up resources onDestroy
    override fun onDestroy() {
        super.onDestroy()
        if (::audioManager.isInitialized) {
            audioManager.stopPlayback()
            audioManager.stopRecording()
        }
    }

}

