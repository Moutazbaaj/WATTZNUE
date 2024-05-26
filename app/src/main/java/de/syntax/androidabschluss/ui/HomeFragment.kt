package de.syntax.androidabschluss.ui


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import coil.load
import de.syntax.androidabschluss.R
import de.syntax.androidabschluss.data.Adapter.ToDoAdapter
import de.syntax.androidabschluss.data.Adapter.TopHeadLineAdapter
import de.syntax.androidabschluss.viewmodel.MeinViewModel
import android.provider.Settings
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import de.syntax.androidabschluss.databinding.FragmentHomeBinding


// Fragment for the home screen
class HomeFragment : Fragment() {

    // ViewModel instances
    private val viewModel: MeinViewModel by activityViewModels()

    // ViewBinding
    private lateinit var binding: FragmentHomeBinding

    // MediaPlayer for playing media files
    private lateinit var mediaPlayer: MediaPlayer

    // LocationManager for managing location updates
    private lateinit var locationManager: LocationManager


    // Inflates the layout for the fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    // Initializes views and sets up necessary functionalities
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews()
        observeProfile()
        requestLocationPermission()
        observeLoadingStatus()
        loadSavedCountry()
        observeWeatherData()
        observeTopNewsData()
        observeToDoData()
        setOnClickListeners()

        // Deactivate system back button
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {}
    }

    // Initializes MediaPlayer and FusedLocationProviderClient
    private fun initializeViews() {
        viewModel.loadTopHeadLinesData()
        mediaPlayer = MediaPlayer()
        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    // Observes changes in profile
    private fun observeProfile() {
        viewModel.profile.observe(viewLifecycleOwner) { profil ->
            val user = profil.find { true }
            if (user != null) {
                binding.toolBar.title = user.userName
                binding.ivUser.load(user.profileImgUri)
            } else {
                binding.toolBar.title = getString(R.string.home)
                binding.ivUser.setImageResource(R.drawable.round_broken_image_25)
            }
        }
    }

    // Observes changes in loading status
    private fun observeLoadingStatus() {
        viewModel.loading.observe(viewLifecycleOwner) { status ->
            when (status!!) {
                MeinViewModel.ApiStatus.LOADING -> {
                    Log.d(TAG, "Loading status: LOADING")

                    binding.progressBar.visibility = View.VISIBLE
                    binding.errorImage.visibility = View.INVISIBLE
                }

                MeinViewModel.ApiStatus.DONE -> {
                    Log.d(TAG, "Loading status: DONE")

                    binding.progressBar.visibility = View.GONE
                    binding.errorImage.visibility = View.INVISIBLE
                }

                MeinViewModel.ApiStatus.ERROR -> {
                    Log.d(TAG, "Loading status: ERROR")

                    binding.errorImage.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    // Observes changes in weather data
    @SuppressLint("SetTextI18n")
    private fun observeWeatherData() {
        viewModel.weather.observe(viewLifecycleOwner) { weatherResponse ->
            weatherResponse?.let {
                // Update UI with weather data
                Log.d(TAG, "Weather data observed: $weatherResponse")
                val city = weatherResponse.name
                val temperature = String.format("%.2f °C", weatherResponse.main.temp)
                val humidity = weatherResponse.main.humidity
                val windSpeed = weatherResponse.wind.speed
                val cloudiness = it.clouds.all
                val weatherDescription = weatherResponse.weather.firstOrNull()?.description ?: ""
                val weatherIcon = weatherResponse.weather.firstOrNull()?.icon ?: ""

                binding.tvWeatherCity.text = "${getString(R.string.city)}: $city"
                binding.tvWeatherTemp.text = "${getString(R.string.temperature)} $temperature"
                binding.tvWeatherHumi.text = "${getString(R.string.humidity)} $humidity %"
                binding.tvWeatherWind.text = "${getString(R.string.wind_speed)} $windSpeed K/M"
                binding.tvCloudiness.text = "${getString(R.string.cloudiness)} $cloudiness %"
                binding.tvWeatherDis.text =
                    "${getString(R.string.weather_description)} $weatherDescription"

                val iconUrl = "https://openweathermap.org/img/w/$weatherIcon.png"
                binding.imWeather.load(iconUrl)
            }
        }
    }

    // Observe changes in top headlines data
    @SuppressLint("SetTextI18n")
    private fun observeTopNewsData() {
        viewModel.topArticle.observe(viewLifecycleOwner) { article ->
            article?.let {
                Log.d(TAG, "Top headlines observed: $article")


                binding.rvTopNews.adapter = TopHeadLineAdapter(article) { url ->

                    if (mediaPlayer.isPlaying) {
                        mediaPlayer.stop()
                    }

                    // Open URL in the app's WebView,
                    findNavController().navigate(
                        HomeFragmentDirections.actionHomeFragmentToWebViewFragment(
                            url
                        )
                    )
                }

                binding.tvListCountNews.text =
                    viewModel.topArticle.value?.size.toString() + getString(R.string.Article)

            }
        }
    }

    // Function to retrieve the selected country from SharedPreferences and update ViewModel
    private fun loadSavedCountry() {
        val sharedPreferences =
            requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val savedCountry = sharedPreferences.getString("Country", "us")
        savedCountry?.let { viewModel.updateCountry(it) }
    }

    // Observes changes in to-do data
    @SuppressLint("SetTextI18n")
    private fun observeToDoData() {

        // Variable to save the state of the RecyclerView list
        var recyclerViewState: Parcelable

        // Observe changes in the to.do list LiveData
        viewModel.todo.observe(viewLifecycleOwner) { todo ->

            // Save the state of the RecyclerView list
            recyclerViewState = binding.rvToDo.layoutManager?.onSaveInstanceState()!!
            // Restore state
            binding.rvToDo.layoutManager?.onRestoreInstanceState(recyclerViewState)

            val sortedAsImportant = todo.sortedByDescending { it.creationDateTime }
                .filter { it.reminderDateTime !== null && !it.isSelected }

            binding.rvToDo.adapter = ToDoAdapter(sortedAsImportant, viewModel, mediaPlayer)

            binding.tvListCount.text =
                viewModel.todo.value?.filter { it.reminderDateTime !== null && !it.isSelected }?.size.toString() + getString(
                    R.string.items
                )

            binding.tvListCountTotal.text =
                getString(R.string.totalItems) + viewModel.todo.value?.size.toString()
        }
    }

    // Sets onClickListeners for various UI elements
    private fun setOnClickListeners() {
        binding.cvWeather.setOnClickListener {

            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }

            if (locationPermissionGranted()) {
                findNavController().navigate(R.id.weatherFragment)
            } else {
                showSettingsDialog()
            }
        }


        binding.btnSeeAll.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            findNavController().navigate(R.id.toDoCategoriesFragment)
        }

        binding.btnAdd.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            findNavController().navigate(R.id.toDoDetailsFragment)
        }

        binding.btnRefresh.setOnClickListener {
            restartApp()
        }

    }

    // Requests location permission
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100
        )
    }


    // Function to check if location permission is granted
    private fun locationPermissionGranted(): Boolean {
        return (ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)
    }

    // Fetches location using LocationManager
    private fun fetchLocation() {
        try {
            // Register for location updates with a minimum distance of 10 meters
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                locationListener
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    // Initialize LocationListener
    private val locationListener = LocationListener { location ->
        // Update weather when location changed
        updateWeather(location)
    }

    // Update weather based on location
    private fun updateWeather(location: Location) {
        // Update weather data with the new location
        val lat = location.latitude
        val lon = location.longitude
        Log.d(TAG, "Location updated: Latitude - $lat, Longitude - $lon")

        // Dispatch to the main thread before updating LiveData
        viewModel.loadWeatherData(lat, lon)
        viewModel.unsetComplete()
    }

    // Function to Show dialogue to promote the user to Give access to the location
    private fun showSettingsDialog() {
        AlertDialog.Builder(requireContext(), R.style.CustomDialogTheme)
            .setTitle(getString(R.string.enabl_locatoin))
            .setMessage(getString(R.string.enabl_locatoin_text))
            .setPositiveButton(getString(R.string.settings)) { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", requireContext().packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
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

    override fun onResume() {
        super.onResume()
        // Check if location permission is granted
        if (locationPermissionGranted()) {
            // Fetch location and weather data if necessary
            fetchLocation()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
    }


    companion object {
        const val TAG = "HomeFragment"
        private const val MIN_TIME_BW_UPDATES: Long = 1000 * 60 * 1 // 1 minute
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 5f // 5 meters
    }


}

