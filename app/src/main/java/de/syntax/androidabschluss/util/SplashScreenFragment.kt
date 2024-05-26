package de.syntax.androidabschluss.util

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.view.animation.LinearInterpolator
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import de.syntax.androidabschluss.R
import de.syntax.androidabschluss.databinding.FragmentSplashScreenBinding
import de.syntax.androidabschluss.viewmodel.FirebaseViewModel


/**
 * SplashScreenFragment displays a splash screen with animations while initializing the app.
 */
@SuppressLint("CustomSplashScreen")
class SplashScreenFragment : Fragment() {

    private lateinit var binding: FragmentSplashScreenBinding

    private val viewModel: FirebaseViewModel by activityViewModels()

    val TAG = "SplashScreen"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler(Looper.myLooper()!!).postDelayed({
            // Start animations
            rotate()
            scale()
        }, 1000)

        // Navigate to the appropriate destination after a delay
        Handler(Looper.myLooper()!!).postDelayed({
            try {
                viewModel.currentUser.observe(viewLifecycleOwner) { user ->
                    if (user != null) {
                        findNavController().navigate(R.id.homeFragment)
                    } else {
                        findNavController().navigate(R.id.loginFragment)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "Navigation error: ${e.message}")
            }
        }, 4000)
    }


    //////// Animation /////////

    /**
     * Rotate animation for the splash screen ImageView.
     */
    private fun rotate() {
        val animator = ObjectAnimator.ofFloat(binding.imageView, View.ROTATION, 0f, 360f)
        animator.duration = 1000
        animator.repeatCount = 1
        animator.repeatMode = ObjectAnimator.RESTART
        animator.interpolator = LinearInterpolator()
        animator.start()
    }


    /**
     * Scale animation for the splash screen ImageView.
     */
    private fun scale() {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.5f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.5f)
        val animator = ObjectAnimator.ofPropertyValuesHolder(binding.imageView, scaleX, scaleY)
        animator.duration = 1000
        animator.repeatCount = 1
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.interpolator = BounceInterpolator()
        animator.start()
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

}

/*

//translate()

/**
 * Translate animation for the splash screen ImageView.
 */
private fun translate() {
    val animator = ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_Y, 500f)
    animator.duration = 1000
    animator.repeatCount = 1
    animator.repeatMode = ObjectAnimator.REVERSE
    animator.interpolator = BounceInterpolator()
    animator.start()
}
*/
