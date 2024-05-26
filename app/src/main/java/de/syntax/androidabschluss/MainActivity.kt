package de.syntax.androidabschluss

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import de.syntax.androidabschluss.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load the selected language preference
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val selectedLanguage = sharedPreferences.getString("Language", "en")
        // Set the locale to the selected language
        setLocale(selectedLanguage ?: "en")

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up bottom navigation with NavController
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        //NavigationUI.setupWithNavController(binding.bottomNavigationView, navHost.navController,false)
        binding.bottomNavigationView.setupWithNavController(navHost.navController)

        // Remove The highlight from the selected item
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.itemActiveIndicatorColor = null

        // Set the app's theme to light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Configure visibility of bottom navigation based on destination
        navHost.navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment,
                R.id.newsFragment,
                R.id.favoritesFragment,
                R.id.profileFragment,
                R.id.searchFragment
                -> binding.bottomNavigationView.visibility = View.VISIBLE

                else -> binding.bottomNavigationView.visibility = View.GONE
            }
        }
    }

    //android:configChanges="orientation|screenSize"
    // in the manifest will prevent the activity from being destroyed
    // and recreated when the orientation or screen size changes,
    // such as when the phone is rotated to landscape mode
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView, navHost)
            commit()
        }
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)

        Log.d("Locale", "Locale set to: $languageCode")
    }
}
