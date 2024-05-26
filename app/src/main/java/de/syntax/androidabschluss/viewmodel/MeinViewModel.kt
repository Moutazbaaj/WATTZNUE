package de.syntax.androidabschluss.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import de.syntax.androidabschluss.data.remote.WeatherApi
import de.syntax.androidabschluss.data.Repository
import de.syntax.androidabschluss.data.datamodels.newsmodels.NewsArticle
import de.syntax.androidabschluss.data.datamodels.profilemodels.Profile
import de.syntax.androidabschluss.data.datamodels.todomodels.ToDo
import de.syntax.androidabschluss.data.local.todoDatabase.getDatabaseToDo
import de.syntax.androidabschluss.data.local.newsDatabase.getDatabaseNews
import de.syntax.androidabschluss.data.local.profileDatabase.getDatabaseProfile
import de.syntax.androidabschluss.data.remote.NewsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


/**
 * ViewModel for managing application data and operations.
 */
class MeinViewModel(application: Application) : AndroidViewModel(application) {


    // Enum class to represent the status of API requests
    enum class ApiStatus { LOADING, ERROR, DONE }


    // TAG for logging
    private val TAG = "MainViewModelTAG"

    // instance for database
    private val toDoDatabase = getDatabaseToDo(application)
    private val newsDatabase = getDatabaseNews(application)
    private val profileDatabase = getDatabaseProfile(application)

    // Repository instance for data operations
    private val repository =
        Repository(NewsApi, WeatherApi, toDoDatabase, newsDatabase, profileDatabase)

    // Variable to save the country code for updating theHeadlines resources
    private var country = "us"


    // LiveData to represent the loading status of API requests
    private val _loading = MutableLiveData<ApiStatus>()
    val loading: LiveData<ApiStatus>
        get() = _loading


    // LiveData to indicate completion status
    private val _complete = MutableLiveData<Boolean>()
    val complete: LiveData<Boolean>
        get() = _complete


    // LiveData objects for various data entities:

    val profile = repository.profile

    val article = repository.article

    val topArticle = repository.topArticle

    val newsArticle = repository.newsArticle

    val sources = repository.sources

    val weather = repository.weather

    val todo = repository.todo




    ////////// profile Methods //////////


    /**
     * Inserts a new profile into the database.
     */
    fun insertProfile(userName: String, profileImg: String) {

        viewModelScope.launch {
            val profile = Profile(
                userName = userName,
                profileImgUri = profileImg
            )
            // Insert the profile into your repository
            repository.insertProfile(profile)
            _complete.value = true
            Log.i(TAG, "insert profile complete ")
        }

    }


    /**
     * Updates the profile in the database.
     */
    fun getProfile() {
        viewModelScope.launch {
            // get profile
            repository.getProfile()
            _complete.value = true
            Log.i(TAG, "get Profile complete ")
        }
    }

    fun updateProfile(profileId: Int, newUserName: String, newProfileImgUri: String?) {
        viewModelScope.launch {
            // update profile
            repository.updateProfile(profileId,newUserName,newProfileImgUri)
            _complete.value = true
            Log.i(TAG, "update profile complete ")
        }
    }


    /**
     * Deletes the profile from the database.
     */
    fun deleteProfile() {
        viewModelScope.launch {
            // Insert the reminder into your repository
            repository.deleteProfile()
            _complete.value = true
            Log.i(TAG, "delete Profile complete ")
        }
    }

    ////////// News Methods //////////


    /**
     * Loads news data based on the provided search term.
     */
    fun loadNewsData(searchTerm: String) {
        viewModelScope.launch {
            _loading.value = ApiStatus.LOADING
            Log.i(TAG, "Loading News data")
            try {
                repository.getNews(searchTerm)
                _complete.value = true
                _loading.value = ApiStatus.DONE
                Log.i(TAG, "Done loading News data")
            } catch (e: Exception) {
                _loading.value = ApiStatus.ERROR
                Log.e(TAG, "Error loading News data $e")
            }
        }
    }


    /**
     * Loads top headlines data.
     */
    fun loadTopHeadLinesData() {
        viewModelScope.launch(Dispatchers.Main) {
            _loading.postValue(ApiStatus.LOADING)
            Log.i(TAG, "Loading Top News data")
            try {
                repository.getTopHeadLine(country)
                withContext(Dispatchers.Main) {
                    _complete.value = true
                    _loading.value = ApiStatus.DONE
                }
                Log.i(TAG, "Done loading Top News data")
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _loading.value = ApiStatus.ERROR
                }
                Log.e(TAG, "Error loading Top News data $e")
            }
        }
    }


    /**
     * Loads sources data based on the provided category.
     */
    fun loadSourcesData(category: String) {
        viewModelScope.launch {
            _loading.value = ApiStatus.LOADING
            Log.i(TAG, "Loading Sources data")
            try {
                repository.getSources(category)
                _complete.value = true
                _loading.value = ApiStatus.DONE
                Log.i(TAG, "Done loading Sources data")
            } catch (e: Exception) {
                _loading.value = ApiStatus.ERROR
                Log.e(TAG, "Error loading Sources data $e")
            }
        }
    }

    ////////// Weather Methods //////////


    /**
     * Loads weather data for the provided latitude and longitude coordinates.
     */
    fun loadWeatherData(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.Main) {
            _loading.postValue(ApiStatus.LOADING)
            Log.i(TAG, "Loading Weather data")
            try {
                repository.getWeather(lat = lat, lon = lon)
                withContext(Dispatchers.Main) {
                    _complete.value = true
                    _loading.value = ApiStatus.DONE
                }
                Log.i(TAG, "Done loading Weather data")
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _loading.value = ApiStatus.ERROR
                }
                Log.e(TAG, "Error loading Weather data $e")
            }
        }
    }



    ////////// Database Methods //////////


    ////////// News //////////


    // Method to get articles
    fun getArticle() {
        viewModelScope.launch {
            repository.getArticle()
            _complete.value = true
            Log.i(TAG, "getting data list is complete ")
        }
    }


    // Method to insert Article
    fun insertArticle(
        title: String,
        description: String,
        content: String,
        author: String,
        sourceName: String,
        publishedAt: String,
        url: String,
        imgUrl: String,
    ) {
        viewModelScope.launch {
            val article = NewsArticle(
                id = 0,
                title,
                description,
                content,
                author,
                sourceName,
                publishedAt,
                url,
                imgUrl,
            )

            repository.insertArticle(article)
            _complete.value = true
            Log.i(TAG, "Insert completed : '${article.titel}'")
        }
    }


    // Method to delete To.Do
    fun deleteArticle(newsArticle: NewsArticle) {
        viewModelScope.launch {
            repository.deleteArticle(newsArticle)
            _complete.value = true
            Log.i(TAG, "Deleting Article with id: ${newsArticle.id} completed")

        }
    }


    // Method to delete all To.Dos
    fun deleteAllArticles() {
        viewModelScope.launch {
            repository.deleteAllArticles()
            _complete.value = true
            Log.i(TAG, "Deleting Articles list is complete ")
        }
    }


    ////////// To Do //////////

    // Method to get To.Do

    fun getTodo() {
        viewModelScope.launch {
            repository.getTodo()
            _complete.value = true
            Log.i(TAG, "getting data list is complete ")
        }
    }

    // Method to insert To.Do
    fun insertTODo(
        titel: String,
        text: String,
        creationDateTime: String,
        favorite: Boolean,
        important: Boolean,
        audioFilePath: String?,
        reminderDateTime: Long?,

        ) {
        viewModelScope.launch {
            // Create the To.Do object with multiple image file paths
            val todo = ToDo(
                titel = titel,
                text = text,
                creationDateTime = creationDateTime,
                favorite = favorite,
                isSelected = false,
                isImportant = important,
                audioFilePath = audioFilePath,
                reminderDateTime = reminderDateTime,

                )

            // Insert the To.Do object into the database
            repository.insert(todo)

            // Notify completion
            _complete.value = true
            Log.i(TAG, "Insert completed : '${todo.text}'")
        }
    }


    // Method to update To.Do
    fun updateTODo(todo: ToDo) {
        viewModelScope.launch {
            repository.update(todo)
            _complete.value = true
            Log.i(TAG, "update completed : '${todo.text}'")
        }
    }


    // Method to update To.Do
    fun updateTODoIsSelected(id: Int, isSelected: Boolean) {
        viewModelScope.launch {
            repository.updateSelection(id, isSelected)
            _complete.value = true
            Log.i(TAG, "update completed Selected")
        }
    }


    // Method to update To.Do
    fun updateTODoIsFavorites(id: Int, favorite: Boolean) {
        viewModelScope.launch {
            repository.updateFavorite(id, favorite)
            _complete.value = true
            Log.i(TAG, "update completed favorite ")
        }
    }


    // Method to update To.Do
    fun updateTODoIsImportant(id: Int, important: Boolean) {
        viewModelScope.launch {
            repository.updateImportant(id, important)
            _complete.value = true
            Log.i(TAG, "update completed important ")
        }
    }


    // Method to delete To.Do
    fun deleteToDo(todo: ToDo) {
        viewModelScope.launch {
            repository.deleteTodo(todo)
            _complete.value = true
            Log.i(TAG, "Deleting Note with id: ${todo.id} completed")

        }
    }

    // Method to delete Important To.Do
    fun deleteTodoImportant(todoList: List<ToDo>, deleteAllImportant: Boolean = false) {
        viewModelScope.launch {
            if (deleteAllImportant) {
                // If deleteAllImportant is true, delete all important to-do items
                repository.deleteTodoImportant(todoList, deleteAllImportant = true)
            }
            _complete.value = true
            Log.i(TAG, "Deleting Note with id: ${todoList.map { it.id }} completed")
        }
    }


    // Method to delete Favorites To.Do
    fun deleteToDoFavorite(todoList: List<ToDo>, deleteAllFavorites: Boolean = false) {
        viewModelScope.launch {
            if (deleteAllFavorites) {
                // If deleteAllImportant is true, delete all important to-do items
                repository.deleteTodoFavorite(todoList, deleteAllFavorites = true)
            }
            _complete.value = true
            Log.i(TAG, "Deleting Note with id: ${todoList.map { it.id }} completed")

        }
    }


    // Method to delete Done To.Do
    fun deleteToDoDone(todoList: List<ToDo>, deleteAllDone: Boolean = false) {
        viewModelScope.launch {
            if (deleteAllDone) {
                // If deleteAllImportant is true, delete all important to-do items
                repository.deleteTodoDone(todoList, deleteAllDone = true)
            }
            _complete.value = true
            Log.i(TAG, "Deleting Note with id: ${todoList.map { it.id }} completed")

        }
    }


    // Method to delete all To.Dos
    fun deleteAllToDo() {
        viewModelScope.launch {
            repository.deleteAllTodos()
            _complete.value = true
            Log.i(TAG, "Deleting Note list is complete ")
        }
    }


    // Method to unset completion status
    fun unsetComplete() {
        _complete.value = false
        Log.i(TAG, "unset Complete")
    }


    // Function to format time in minutes and seconds
    fun formatTime(milliseconds: Long): String {
        val minutes = (milliseconds / 1000) / 60
        val seconds = (milliseconds / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }


    // Function to convert milliseconds to a formatted date string
    fun convertMillisToDateString(millis: Long): String {
        // Create a Calendar instance
        val calendar = Calendar.getInstance()
        // Set the time in milliseconds
        calendar.timeInMillis = millis
        // Create a SimpleDateFormat instance for formatting the date
        val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
        // Format the date and return as a string
        return sdf.format(calendar.time)
    }

    // Method to update the country code for fetching weather data
    fun updateCountry(countryCode: String) {
        country = countryCode
    }
}
