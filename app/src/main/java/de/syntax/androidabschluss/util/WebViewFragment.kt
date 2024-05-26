package de.syntax.androidabschluss.util

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import de.syntax.androidabschluss.databinding.FragmentWebviewBinding
import java.util.Locale


/**
 * WebViewFragment displays a web page inside a WebView.
 */
class WebViewFragment : Fragment() {

    // ViewBinding
    private lateinit var binding: FragmentWebviewBinding

    private val TAG = "WebViewFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWebviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val url = arguments?.getString("url")


        // Save the current configuration
        val configuration = resources.configuration

        // Create a new configuration object
        val newConfiguration = Configuration(configuration)

        // Set the locale to the default locale
        newConfiguration.locale = Locale.getDefault()

        // Apply the new configuration to the WebView
        binding.webView.applyConfiguration(newConfiguration)

        // Load the URL into the WebView
        val secureUrl = url?.let { enforceHttps(it) }
        if (secureUrl != null) {
            binding.webView.loadUrl(secureUrl)
        }

        if (!url.isNullOrEmpty()) {
            try {
                // Setup WebViewClient to handle URL loading
                binding.webView.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        // Return false to let the WebView handle the URL
                        return false
                    }
                }

                // Enable JavaScript if required
                binding.webView.settings.javaScriptEnabled = true


                // Setup navigation controls
                binding.btnBack.setOnClickListener { binding.webView.goBack() }
                binding.btnForward.setOnClickListener { binding.webView.goForward() }
                binding.btnRefresh.setOnClickListener { binding.webView.reload() }
                binding.btnStop.setOnClickListener { binding.webView.stopLoading() }

                binding.btnShare.setOnClickListener {
                    shareUrl(url)
                }

                // Load the URL into the WebView
                val secureUrls = enforceHttps(url)
                binding.webView.loadUrl(secureUrls)

            } catch (e: Exception) {
                Log.e(TAG, "Error loading URL: $url", e)
            }
        }

        // Navigate back when the "Done" button is clicked
        binding.btnDone.setOnClickListener {
            // de-initialize the WebView
            binding.webView.destroy()
            it.findNavController().navigateUp()
        }
    }

    //Shares the URL using the Android's built-in sharing mechanism.
    private fun shareUrl(url: String?) {
        if (!url.isNullOrEmpty()) {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, url)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, "Share URL with")
            startActivity(shareIntent)
        } else {
            Log.e(TAG, "URL is null or empty")
        }
    }


    private fun enforceHttps(url: String): String {
        // If the URL starts with "http://", replace it with "https://"
        return if (url.startsWith("http://")) {
            url.replace("http://", "https://")
        } else {
            url
        }
    }

    // Extension function to apply configuration to WebView
    @SuppressLint("SetJavaScriptEnabled")
    fun WebView.applyConfiguration(configuration: Configuration) {
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.loadsImagesAutomatically = true
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        // Set the new configuration
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

}
