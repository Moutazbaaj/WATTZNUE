package de.syntax.androidabschluss.ui


import android.annotation.SuppressLint
import android.graphics.Rect
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng
import de.syntax.androidabschluss.R
import de.syntax.androidabschluss.databinding.FragmentWeatherBinding
import de.syntax.androidabschluss.viewmodel.MeinViewModel
import java.util.Date
import java.util.Locale


class WeatherFragment : Fragment() {

    private val viewModel: MeinViewModel by activityViewModels()

    private lateinit var binding: FragmentWeatherBinding

    private lateinit var mapView: MapView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeatherBinding.inflate(layoutInflater)

        return binding.root
    }


    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.onResume()

        try {
            MapsInitializer.initialize(requireActivity().applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }




        viewModel.weather.observe(viewLifecycleOwner) { weatherResponse ->
            weatherResponse?.let {


                // Get the current time in milliseconds
                val currentTimeMillis = System.currentTimeMillis()


                // Convert the current time to a readable format
                val currentTime = Date(currentTimeMillis)



                val city = it.name

                val timeOfDataCalculation = it.dt
                val timezone = it.timezone
                val cityID = it.id
                val visibility = it.visibility

                // coordination

                val latitudeLocation = it.coord.lat
                val longitudeLocation = it.coord.lon

                // main

                val temperature = String.format("%.2f °C", it.main.temp)
                val humidity = it.main.humidity
                val seaLevelHPa = it.main.pressure
                val maximumTemperatureAtTheMoment = it.main.temp_max
                val minimumTemperatureAtTheMoment = it.main.temp_min

                val maximumTemperatureAtTheMomentString = "${it.main.temp_max}°C"
                val minimumTemperatureAtTheMomentString = "${it.main.temp_min}°C"

                // weather

                val weatherDescription = it.weather.firstOrNull()?.description ?: "0"
                val weatherIcon = it.weather.firstOrNull()?.icon ?: "0"
                val weatherConditionId = it.weather.firstOrNull()?.id ?: 0
                val weatherParametersState = it.weather.firstOrNull()?.main ?: "0"

                // sys

                val countryCode = it.sys.country
                val internalParameterType = it.sys.type
                val sunriseTime = it.sys.sunrise
                val sunsetTime = it.sys.sunset

                // cloud

                val cloudiness = it.clouds.all


                // wind

                val windDirection = it.wind.deg
                val windSpeed = it.wind.speed
                val windGust = it.wind.gust ?: 0



                //(not used in this Version)

                val internalParameterCod = it.cod
                val internalParameter = it.base
                val internalParameterMessage = it.sys.message ?: 0
                val internalParameterId = it.sys.id
                // rain
                val rain1h = it.rain?.rain1 ?: 0
                val rain3h = it.rain?.rain3 ?: 0
                // snow
                val snow1h = it.snow?.snow1 ?: 0
                val snow3h = it.snow?.snow3 ?: 0



                // Set up refresh listener for SwipeRefreshLayout
                binding.swipeRefreshlayoutWeather.setOnRefreshListener {
                    viewModel.loadWeatherData(latitudeLocation,longitudeLocation)
                    viewModel.unsetComplete()
                    binding.swipeRefreshlayoutWeather.isRefreshing = false
                }



                // Load weather icon using Coil
                val iconUrl = "https://openweathermap.org/img/w/$weatherIcon.png"
                binding.imWeather.load(iconUrl)


                binding.tvCity.text = "${getString(R.string.city)}:\n\n $city"

                val timeOfDataCalculationMillis = timeOfDataCalculation * 1000L
                binding.tvTimeOfDataCalculation.text =
                    "${getString(R.string.time_of_data_calculation)}\n ${
                        viewModel.convertMillisToDateString(
                            timeOfDataCalculationMillis
                        )
                    }"
                binding.tvTimezone.text = "${getString(R.string.timezone)}\n $timezone"
                binding.tvCityID.text = "${getString(R.string.city_id)}\n $cityID"
                binding.tvVisibility.text = "${getString(R.string.visibility)}\n $visibility M"


                // coordination

                binding.tvLatitudeLocation.text =
                    "${getString(R.string.latitude_location)}\n $latitudeLocation"
                binding.tvLongitudeLocation.text =
                    "${getString(R.string.longitude_location)}\n $longitudeLocation"


                // Temperature

                binding.tvMaximumTemperatureChart.text = getString(R.string.maximum_temperature_at_the_moment ) + maximumTemperatureAtTheMomentString
                binding.tvMinimumTemperatureeChart.text = getString(R.string.minimum_temperature_at_the_moment ) + minimumTemperatureAtTheMomentString



                // main

                binding.tvTemperature.text = "${getString(R.string.temperature)}\n $temperature"
                binding.tvHumidity.text = "${getString(R.string.humidity)}\n $humidity %"
                binding.tvSeaLevelHPa.text =
                    "${getString(R.string.sea_level_hpa)}\n $seaLevelHPa HPa"


                // weather

                binding.tvWeatherDescription.text =
                    "${getString(R.string.weather_description)}\n\n $weatherDescription"
                binding.tvWeatherConditionId.text =
                    "${getString(R.string.weather_condition_id)}\n $weatherConditionId"
                "${getString(R.string.weather_parameters_state)}\n $weatherParametersState"


                // sys

                binding.tvCountryCode.text = "${getString(R.string.country_code)}\n $countryCode"
                val sunriseTimeMillis = sunriseTime * 1000L
                binding.tvSunriseTime.text = "${getString(R.string.sunrise_time)}\n ${
                    viewModel.convertMillisToDateString(sunriseTimeMillis)
                }"
                val sunsetTimeMillis = sunsetTime * 1000L
                binding.tvSunsetTime.text = "${getString(R.string.sunset_time)}\n ${
                    viewModel.convertMillisToDateString(sunsetTimeMillis)
                }"


                // cloud

                binding.tvCloudiness.text = "${getString(R.string.cloudiness)}\n $cloudiness %"


                // wind

                binding.tvWindDirection.text =
                    "${getString(R.string.wind_direction)}\n $windDirection"
                binding.tvWindSpeed.text = "${getString(R.string.wind_speed)}\n $windSpeed K/M"
                binding.tvWindGust.text = "${getString(R.string.wind_gust)}\n $windGust K/M"


                /// Charts ///


                ////////// temp Chart //////////

                // list of temperature data points
                val entries = mutableListOf<Entry>()
                entries.add(
                    Entry(
                        0f, minimumTemperatureAtTheMoment.toFloat()
                    )
                ) // Minimum temperature
                entries.add(Entry(1f, it.main.temp.toFloat())) // Actual temperature
                entries.add(
                    Entry(
                        2f, maximumTemperatureAtTheMoment.toFloat()
                    )
                ) // Maximum temperature


                // Create a data set for the line chart
                val dataSet = LineDataSet(entries, "Temperature of the day")
                dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()

                // Customize the appearance of the data set
                dataSet.valueTextSize = 10f

                // Create a line data object and set the data a set
                val lineData = LineData(dataSet)

                // Get a reference to the line chart view
                val lineChart = binding.weatherLineChart

                // Remove the right Y-axis
                lineChart.axisRight.isEnabled = false
                lineChart.axisLeft.isEnabled = true

                // Set data to the line chart
                lineChart.data = lineData

                // Customize X-axis
                val xAxis = lineChart.xAxis
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(true)
                xAxis.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return when (value.toInt()) {
                            0 -> "Min"
                            1 -> "Max"
                            2 -> "Actual"
                            else -> ""
                        }
                    }
                }


                // Customize Y-axis
                val yAxis = lineChart.axisRight
                yAxis.setDrawGridLines(true)
                yAxis.axisMinimum = minimumTemperatureAtTheMoment.toFloat() - 10
                yAxis.axisMaximum = maximumTemperatureAtTheMoment.toFloat() + 10
                // Refresh the chart
                lineChart.invalidate()


                /////////////// Sun chart //////////////


                // Custom value formatter for time conversion
                val axisValueFormatter = object : ValueFormatter() {
                    @SuppressLint("ConstantLocale")
                    private val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())

                    override fun getFormattedValue(value: Float): String {
                        // Convert the timestamp to a formatted time string
                        return sdf.format(value.toLong() * 1000) // Convert seconds to milliseconds
                    }
                }

                // list of time data points
                val entriesSun = mutableListOf<Entry>()
                entriesSun.add(Entry(0f, sunriseTime.toFloat())) // sunrise
                entriesSun.add(Entry(1f, currentTime.time.toFloat())) // Actual time
                entriesSun.add(Entry(2f, sunsetTime.toFloat())) // sunset

                // Create a data set for the line chart
                val dataSetSun = LineDataSet(entriesSun, "Sun today")
                dataSetSun.colors = ColorTemplate.COLORFUL_COLORS.toList()


                // Customize the appearance of the data set
                dataSetSun.valueTextSize = 10f

                // Create a line data object and set the data a set
                val lineDataSun = LineData(dataSetSun)

                // Get a reference to the line chart view
                val lineChartSun = binding.weatherLineChartSun

                // Remove the right Y-axis
                lineChartSun.axisRight.isEnabled = false
                lineChartSun.axisLeft.isEnabled = true


                // Customize X-axis
                val xAxisSun = lineChartSun.xAxis
                xAxisSun.position = XAxis.XAxisPosition.BOTTOM
                xAxisSun.setDrawGridLines(true)
                xAxisSun.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return when (value.toInt()) {
                            0 -> "Reis"
                            1 -> "Set"
                            2 -> "Actual"
                            else -> ""
                        }
                    }
                }

                // Customize Y-axis
                val yAxisSun = lineChartSun.axisLeft // Use axisLeft instead of axisRight
                yAxisSun.setDrawGridLines(true)
                yAxisSun.valueFormatter = axisValueFormatter

                // Set data to the line chart
                lineChartSun.data = lineDataSun

                // Refresh the chart
                lineChartSun.invalidate()




                /////// MapView /////////


                mapView.getMapAsync { googleMap ->
                    googleMap.let { map ->
                        // Add marker for your location
                        val location = LatLng(latitudeLocation, longitudeLocation)
                        map.addMarker(MarkerOptions().position(location).title("My Location"))
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))

                        // Create weighted LatLng point with intensity as color
                        val weightedLatLng = WeightedLatLng(location)

                        // Log the weighted LatLng for debugging
                        Log.d("WeatherFragment", "Weighted LatLng: $weightedLatLng")

                        // Add weighted LatLng point to heatmap data
                        val heatmapData = mutableListOf<WeightedLatLng>()
                        heatmapData.add(weightedLatLng)

                        // Log the heatmap data size and content for debugging
                        Log.d("WeatherFragment", "Heatmap Data Size: ${heatmapData.size}")
                        Log.d("WeatherFragment", "Heatmap Data Content: $heatmapData")

                        // Create HeatmapTileProvider with the heatmap data
                        val heatmapTileProvider = HeatmapTileProvider.Builder()
                            .weightedData(heatmapData)
                            .radius(50) // Set the radius according to your preference
                            .build()

                        // Add HeatmapTileOverlay to the map
                        val heatmapOverlay =
                            map.addTileOverlay(TileOverlayOptions().tileProvider(heatmapTileProvider))
                        Log.d("WeatherFragment", "Heatmap Overlay: $heatmapOverlay")
                    }
                }
            }
        }



        binding.btnDone.setOnClickListener {
            findNavController().navigateUp()
        }

        // Deactivate system back button
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {}
    }
}
