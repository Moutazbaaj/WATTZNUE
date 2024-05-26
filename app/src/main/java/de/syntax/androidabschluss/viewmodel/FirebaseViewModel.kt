package de.syntax.androidabschluss.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import de.syntax.androidabschluss.R
import java.util.regex.Pattern


/**
 * ViewModel for Firebase authentication-related operations.
 */
class FirebaseViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "FirebaseViewModel"



    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _currentUser = MutableLiveData<FirebaseUser?>(firebaseAuth.currentUser)

    val currentUser: LiveData<FirebaseUser?>
        get() = _currentUser



    @SuppressLint("StaticFieldLeak")
    private lateinit var context: Context

    // Function to set the context
    fun setContext(context: Context) {
        this.context = context
    }



    /**
     * Signs up a user with the provided email and password.
     */
    fun signup(email: String, password: String) {
        // Check if the email format is valid
        if (!isEmailValid(email)) {
            // Show a toast indicating invalid email format
            Toast.makeText(
                context,
                (R.string.valid_email),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Check if the password meets the requirements before attempting to create the user
        if (!isPasswordValid(password)) {
            // Show a toast indicating password requirements
            Toast.makeText(
                context,
                (R.string.password_length),
                Toast.LENGTH_LONG
            ).show()
            return
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    login(email, password)
                } else {
                    val exception = task.exception
                    if (exception is FirebaseAuthUserCollisionException) {
                        // Email already exists, shows toast
                        Toast.makeText(
                            context,
                            (R.string.email_exists),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Log.e(TAG, "Signup failed: ${exception?.message}")
                    }
                }
            }
    }



    /**
     * Logs in a user with the provided email and password.
     */
    fun login(email: String, password: String) {
        // Check if the email format is valid
        if (!isEmailValid(email)) {
            // Show a toast indicating invalid email format
            Toast.makeText(
                context,
                (R.string.valid_email),
                Toast.LENGTH_SHORT
            ).show()
            return
        }


        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _currentUser.value = firebaseAuth.currentUser
                } else {
                    when (val e = task.exception) {
                        is FirebaseAuthInvalidUserException -> {
                            // Email does not exist
                            Toast.makeText(
                                context,
                                (R.string.email_not_found),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is FirebaseAuthInvalidCredentialsException -> {
                            // Password is incorrect
                            Toast.makeText(
                                context,
                                (R.string.incorrect_password),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {
                            // Other errors
                            Log.e(TAG, "Login failed: ${e?.message}")
                        }
                    }
                }
            }
    }


    /**
     * Logs out the current user.
     */
    fun logout() {
        firebaseAuth.signOut()
        _currentUser.value = firebaseAuth.currentUser
    }


    /**
     * Resets the password for the user with the provided email.
     */
    fun resetPassword(email: String) {
        // Check if the email format is valid
        if (!isEmailValid(email)) {
            // Show a toast indicating invalid email format
            Toast.makeText(
                context,
                getApplication<Application>().getString(R.string.valid_email),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Password reset email sent successfully")
                    // Show a success message to the user using Toast
                    Toast.makeText(
                        context,
                        getApplication<Application>().getString(R.string.success_send_email),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Log.e(TAG, "Failed to send password reset email: ${task.exception}")
                    Toast.makeText(
                        context,
                        getApplication<Application>().getString(R.string.fails_send_email),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


    /**
     * Deletes the current user's account.
     */
    fun deleteAccount() {
        val user = firebaseAuth.currentUser
        user?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "User account deleted successfully")
                // Clear the current user LiveData
                _currentUser.value = null
            } else {
                Log.e(TAG, "Failed to delete user account: ${task.exception}")
            }
        }
    }

}


/**
 * Checks if the provided email has a valid format.
 */
private fun isEmailValid(email: String): Boolean {
    val pattern = Pattern.compile("^\\S+@\\S+\\.\\S+$")
    val matcher = pattern.matcher(email)
    return matcher.matches()
}


/**
 * Checks if the provided password meets the required criteria.
 */
private fun isPasswordValid(password: String): Boolean {
    // Define your password requirements here
    val pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z]).{8,}$")
    val matcher = pattern.matcher(password)
    return matcher.matches()
}
