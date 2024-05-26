package de.syntax.androidabschluss.data

import de.syntax.androidabschluss.data.datamodels.newsmodels.Article
import de.syntax.androidabschluss.data.datamodels.newsmodels.Source
import WeatherResponse
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.syntax.androidabschluss.data.remote.WeatherApi
import de.syntax.androidabschluss.data.datamodels.newsmodels.NewsArticle
import de.syntax.androidabschluss.data.datamodels.profilemodels.Profile
import de.syntax.androidabschluss.data.datamodels.todomodels.ToDo
import de.syntax.androidabschluss.data.local.newsDatabase.NewsDatabase
import de.syntax.androidabschluss.data.local.profileDatabase.ProfileDatabase
import de.syntax.androidabschluss.data.local.todoDatabase.ToDoDatabase

import de.syntax.androidabschluss.data.remote.NewsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(
    private val newsApi: NewsApi,
    private val weatherApi: WeatherApi,
    private val toDoDatabase: ToDoDatabase,
    private val newsDatabase: NewsDatabase,
    private val profileDatabase: ProfileDatabase,
) {

    // TAG for logging
    private val TAG = "RepoTAG"


    //  key for API requests
    private val newsApiKey = "Enter your API key here"
    private val weatherApiKey = "Enter your API key here"


    // LiveData for profile
    private val _profile = profileDatabase.profileDatabaseDao.getAll()
    val profile: LiveData<List<Profile>>
        get() = _profile


    // LiveData for Article
    private val _article = MutableLiveData<List<Article>>()
    val article: LiveData<List<Article>>
        get() = _article


    // LiveData for top headline Article
    private val _topArticle = MutableLiveData<List<Article>>()
    val topArticle: LiveData<List<Article>>
        get() = _topArticle


    // LiveData for top headline Article
    private val _newsArticle = newsDatabase.newsDatabaseDao.getAll()
    val newsArticle: LiveData<List<NewsArticle>>
        get() = _newsArticle


    private val _sources = MutableLiveData<List<Source>>()
    val sources: LiveData<List<Source>>
        get() = _sources


    // LiveData for weather
    private val _weather = MutableLiveData<WeatherResponse?>()
    val weather: LiveData<WeatherResponse?>
        get() = _weather


    //LiveData for Notes
    private val _todo = toDoDatabase.toDoDatabaseDao.getAll()
    val todo: LiveData<List<ToDo>>
        get() = _todo


    ////////////// profile //////////////
    fun getProfile() {
        try {
            profileDatabase.profileDatabaseDao.getAll()
            Log.i(TAG, "get profile Completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error getting profile from  database: $e")
        }
    }


    // Function to insert the Profile to the database
    suspend fun insertProfile(profile: Profile) {
        try {
            profileDatabase.profileDatabaseDao.insert(profile)
            Log.i(TAG, "Profile Data insert Completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error writing Profile into database: $e")
        }
    }

    // Function to insert the Profile to the database
    suspend fun updateProfile(profileId: Int, newUserName: String, newProfileImgUri: String?) {
        try {
            profileDatabase.profileDatabaseDao.updateProfile(profileId,newUserName,newProfileImgUri)
            Log.i(TAG, "Profile Data update Completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error writing Profile into database: $e")
        }
    }

    // Function to delete profile
    suspend fun deleteProfile() {
        withContext(Dispatchers.IO) {
            try {
                profileDatabase.profileDatabaseDao.deleteAll()
                Log.i(TAG, "deleting profile data Successful")
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting profile data... $e")
            }
        }
    }


    ////////////// News API calls //////////////
    suspend fun getNews(searchTerm: String) {
        try {
            Log.i(TAG, "loading news data")
            val articleData = newsApi.retrofitService.getNews(searchTerm, newsApiKey).articles
            _article.value = articleData.filter {
                it.title != "[Removed]" && it.description != "[Removed]"
            }
            Log.i(TAG, "news Data Loaded ${articleData.size}")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading news data $e")
        }
    }


    suspend fun getTopHeadLine(country: String) {
        try {
            Log.i(TAG, "loading TopNews data")
            val topArticleData =
                newsApi.retrofitService.getTopHeadlines(country, newsApiKey).articles
            _topArticle.value = topArticleData
            Log.i(TAG, "TopNews Data Loaded ${topArticleData.size}")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading TopNews data $e")
        }
    }


    suspend fun getSources(category: String) {
        try {
            Log.i(TAG, "loading sources data")
            val sourcesData = newsApi.retrofitService.getNewsCategories(
                category,
                newsApiKey
            ).sources
            _sources.value = sourcesData
            Log.i(TAG, "sources Data Loaded ${sourcesData.size}")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading sources data $e")
        }
    }


    ////////////// Weather API calls //////////////

    suspend fun getWeather(lat: Double, lon: Double) {
        try {
            Log.i(TAG, "loading weather data")
            val weatherData = withContext(Dispatchers.IO) {
                weatherApi.retrofitService.getCurrentWeather(
                    lat = lat,
                    lon = lon,
                    units = "metric",
                    weatherApiKey
                )
                    .execute().body()
            }
            _weather.value = weatherData
            Log.i(TAG, "weather Data Loaded $weatherData")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading weather data $e")
        }
    }


    /////////////// news article ////////////

    // Function to get the article to the database

    fun getArticle() {
        try {
            newsDatabase.newsDatabaseDao.getAll()
            Log.i(TAG, "get article Data Completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error getting article data from  database: $e")
        }
    }


    // Function to insert Article to the database
    suspend fun insertArticle(newsArticle: NewsArticle) {
        try {
            newsDatabase.newsDatabaseDao.insert(newsArticle)
            Log.i(TAG, "article Data insert Completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error writing article into database: $e")
        }
    }


    // Function to delete on article at the time
    suspend fun deleteArticle(newsArticle: NewsArticle) {
        withContext(Dispatchers.IO) {
            try {
                newsDatabase.newsDatabaseDao.deleteById(newsArticle.id)
                Log.i(TAG, "deleting article data Successful")
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting the article data... $e")
            }
        }
    }


    // Function to delete all articles
    suspend fun deleteAllArticles() {
        withContext(Dispatchers.IO) {
            try {
                newsDatabase.newsDatabaseDao.deleteAll()
                Log.i(TAG, "deleting articles data Successful")
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting articles data... $e")
            }
        }
    }

    ////////////// To.Do //////////////

    // Function to get the notes to the database

    fun getTodo() {
        try {
            toDoDatabase.toDoDatabaseDao.getAll()
            Log.i(TAG, "get Data Completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error getting data from  database: $e")
        }
    }

    // Function to insert the notes to the database
    suspend fun insert(toDo: ToDo) {
        try {
            toDoDatabase.toDoDatabaseDao.insert(toDo)
            Log.i(TAG, "Data insert Completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error writing into database: $e")
        }
    }

    // Function to update the notes to the database
    suspend fun update(toDo: ToDo) {
        try {
            toDoDatabase.toDoDatabaseDao.update(toDo)
            Log.i(TAG, "Data updates Completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error writing into database: $e")
        }
    }

    // Function to update the notes selection state to the database
    suspend fun updateSelection(id: Int, isSelected: Boolean) {
        try {
            toDoDatabase.toDoDatabaseDao.updateSelection(id, isSelected)
            Log.i(TAG, "isSelected updates Completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error writing isSelected into database: $e")
        }
    }

    // Function to update the notes favorite state to the database
    suspend fun updateFavorite(id: Int, favorite: Boolean) {
        try {
            toDoDatabase.toDoDatabaseDao.updateFavorite(id, favorite)
            Log.i(TAG, "favorite updates Completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error writing favorite into database: $e")
        }
    }

    // Function to update the notes important state to the database
    suspend fun updateImportant(id: Int, important: Boolean) {
        try {
            toDoDatabase.toDoDatabaseDao.updateImportant(id, important)
            Log.i(TAG, "important updates Completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error writing important into database: $e")
        }
    }


    // Function to delete on note at the time
    suspend fun deleteTodo(toDo: ToDo) {
        withContext(Dispatchers.IO) {
            try {
                toDoDatabase.toDoDatabaseDao.deleteById(toDo.id)
                Log.i(TAG, "deleting todo data Successful ${toDo.id}")
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting the todo data... $e")
            }
        }
    }

    // Function to delete Important note at the time
    suspend fun deleteTodoImportant(toDo: List<ToDo>, deleteAllImportant: Boolean = false) {
        withContext(Dispatchers.IO) {
            try {
                if (deleteAllImportant) {
                    // Delete all important to-do items
                    toDoDatabase.toDoDatabaseDao.deleteImportant(true)
                }
                Log.i(TAG, "deleting todo data Successful")
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting the todo data... $e")
            }
        }
    }

    // Function to delete Favorites note at the time
    suspend fun deleteTodoFavorite(toDo: List<ToDo>, deleteAllFavorites: Boolean = false) {
        withContext(Dispatchers.IO) {
            try {
                if (deleteAllFavorites) {
                    toDoDatabase.toDoDatabaseDao.deleteFavorite(true)
                }
                Log.i(TAG, "deleting todo data Successful")
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting the todo data... $e")
            }
        }
    }

    // Function to delete Favorites note at the time
    suspend fun deleteTodoDone(toDo: List<ToDo>, deleteAllDone: Boolean = false) {
        withContext(Dispatchers.IO) {
            try {
                if (deleteAllDone) {
                    toDoDatabase.toDoDatabaseDao.deleteDone(true)
                }
                Log.i(TAG, "deleting todo data Successful")
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting the todo data... $e")
            }
        }
    }

    // Function to delete all notes
    suspend fun deleteAllTodos() {
        withContext(Dispatchers.IO) {
            try {
                toDoDatabase.toDoDatabaseDao.deleteAll()
                Log.i(TAG, "deleting todos data Successful")
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting the todo data... $e")
            }
        }
    }

}
