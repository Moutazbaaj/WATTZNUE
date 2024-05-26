package de.syntax.androidabschluss.data.Adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import de.syntax.androidabschluss.R.*
import de.syntax.androidabschluss.data.datamodels.todomodels.ToDo
import de.syntax.androidabschluss.databinding.BottomSheetEditTodoBinding
import de.syntax.androidabschluss.databinding.ListItemTodoBinding
import de.syntax.androidabschluss.viewmodel.MeinViewModel
import okio.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


/**
 * Adapter class for the RecyclerView used to display To-Do items.
 *
 * @property dataset List<To.Do> The list of To-Do items to display.
 * @property viewModel MeinViewModel The ViewModel used for data operations.
 */

data class ToDoAdapter(
    private val dataset: List<ToDo>,
    private val viewModel: MeinViewModel,
    private val mediaPlayer: MediaPlayer,
) : RecyclerView.Adapter<ToDoAdapter.ItemViewHolder>() {



    private var isPlaying: Boolean = false
    private var currentPlayingFilePath: String? = null

    private val TAG = "ToDoAdapter"

    /**
     * ViewHolder class for each item in the RecyclerView.
     *
     * @property binding ListItemTodoBinding The binding object for the item layout.
     */

    inner class ItemViewHolder(val binding: ListItemTodoBinding) :
        RecyclerView.ViewHolder(binding.root)


    /**
     * Creates a new ViewHolder instance for the item view.
     *
     * @param parent ViewGroup The parent view group.
     * @param viewType Int The type of the view.
     * @return ItemViewHolder The new ViewHolder instance.
     */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            ListItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    /**
     * Gets the total number of items in the dataset.
     *
     * @return Int The total number of items.
     */

    override fun getItemCount(): Int {
        return dataset.size
    }


    /**
     * Binds the data to the ViewHolder.
     *
     * @param holder ItemViewHolder The ViewHolder to bind the data to.
     * @param position Int The position of the item in the dataset.
     */

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val todo = dataset[position]


        // Bind data to ViewHolder
        holder.binding.tvTitel.text = todo.titel
        holder.binding.tvText.text = todo.text
        holder.binding.tvDate.text = todo.creationDateTime

        val reminderTimeMillis = todo.reminderDateTime
        val reminderTimeString = reminderTimeMillis?.let { viewModel.convertMillisToDateString(it) }
        holder.binding.tvReminderTime.text = reminderTimeString
        holder.binding.tvReminderTime.setTextColor(
            ContextCompat.getColor(
                holder.itemView.context,
                color.red
            )
        )

        if (todo.reminderDateTime != null) {

            holder.binding.tvReminderTime.visibility = View.VISIBLE
            holder.binding.tvAlarm.visibility = View.VISIBLE

        } else {
            holder.binding.tvReminderTime.visibility = View.GONE
            holder.binding.tvAlarm.visibility = View.GONE
        }


        // Set the card Alpha based on the selection state
        if (todo.isSelected) {
            holder.binding.listTodo.alpha = 0.3f
        } else {
            holder.binding.listTodo.alpha = 1f
        }

        // Set a click listener for the listTodo item
        holder.binding.listTodo.setOnLongClickListener {

            // Check if media player is playing
            if (mediaPlayer.isPlaying) {
                // Media player is playing, so return without processing the click event
                return@setOnLongClickListener true
            }

            holder.binding.listTodo.alpha = 0.3f
            val isSelected = todo.isSelected

            val dialog = AlertDialog.Builder(holder.itemView.context, style.CustomDialogTheme)
            dialog.setTitle(holder.itemView.context.getString(string.movingConfirm))
            dialog.setMessage(
                "${holder.itemView.context.getString(string.movingConfirm_text1)} ${
                    if (isSelected) holder.itemView.context.getString(string.restore) else holder.itemView.context.getString(
                        string.move
                    )
                } ${holder.itemView.context.getString(string.movingConfirm_text2)} ${
                    if (isSelected) holder.itemView.context.getString(string.originallist) else holder.itemView.context.getString(
                        string.DoneList
                    )
                }?"
            )
            dialog.setPositiveButton(string.yes) { _, _ ->
                // Toggle the selection state
                todo.isSelected = !isSelected
                // Update the alpha value directly based on the selection state
                notifyDataSetChanged()
                viewModel.updateTODoIsSelected(id = todo.id, isSelected = todo.isSelected)
            }
            dialog.setNegativeButton(string.no) { _, _ -> }
            dialog.setOnDismissListener {
                // Restore the original card alpha when the dialog is dismissed
                if (todo.isSelected) {
                    holder.binding.listTodo.alpha = 0.3f
                } else {
                    holder.binding.listTodo.alpha = 1f
                }

            }
            dialog.show()

            true
        }


        // Set the icon based on the selection state
        if (todo.favorite) {
            holder.binding.btnFav.setImageResource(drawable.iconheartred)
        } else {
            holder.binding.btnFav.setImageResource(drawable.iconheart)
        }

        // Set a click listener for the favorite button
        holder.binding.btnFav.setOnClickListener {
            // Check if media player is playing
            if (mediaPlayer.isPlaying) {
                // Media player is playing, so return without processing the click event
                return@setOnClickListener
            }

            // Toggle the favorite state
            todo.favorite = !todo.favorite
            // Update the UI to reflect the change
            notifyDataSetChanged()
            viewModel.updateTODoIsFavorites(id = todo.id, favorite = todo.favorite)
        }


        // Set the icon based on the selection state
        if (todo.isImportant) {
            holder.binding.btnImpo.setColorFilter(
                ContextCompat.getColor(
                    holder.itemView.context,
                    color.red
                )
            )
        } else {
            holder.binding.btnImpo.setColorFilter(
                ContextCompat.getColor(
                    holder.itemView.context,
                    color.black
                )
            )
        }

        // Set a click listener for the important button
        holder.binding.btnImpo.setOnClickListener {
            // Check if media player is playing
            if (mediaPlayer.isPlaying) {
                // Media player is playing, so return without processing the click event
                return@setOnClickListener
            }

            // Toggle the important state
            todo.isImportant = !todo.isImportant
            notifyDataSetChanged()
            viewModel.updateTODoIsImportant(id = todo.id, important = todo.isImportant)
        }


        // Set play button icon tint
        if (todo.audioFilePath != null) {
            holder.binding.btnPlay.setColorFilter(Color.RED)
        } else {
            holder.binding.btnPlay.setColorFilter(Color.BLACK)
        }


        // Handle click event on play button
        holder.binding.btnPlay.setOnClickListener {
            if (todo.audioFilePath.isNullOrEmpty()) {
                showToast(
                    holder.itemView.context.getString(string.NoVoiceNoteavailable),
                    holder.itemView.context
                )
                return@setOnClickListener
            }
            if (mediaPlayer.isPlaying) {
                if (todo.audioFilePath == currentPlayingFilePath && mediaPlayer.isPlaying) {
                    // If the same audio file is playing, stop playback
                    stopPlayback(holder)
                } else {
                    showToast(
                        holder.itemView.context.getString(string.recordPlay),
                        holder.itemView.context
                    )
                }
            } else {
                startPlayback(todo.audioFilePath!!, holder)
            }
        }


        holder.binding.btnEdit.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                // Media player is playing, so return without processing the click event
                return@setOnClickListener
            }
            // Inflate the layout for the bottom sheet dialog using ViewBinding
            val bottomSheetBinding =
                BottomSheetEditTodoBinding.inflate(LayoutInflater.from(holder.itemView.context))

            // Set the text of the EditText fields to the current text of the to-do item
            bottomSheetBinding.editTextTitle.setText(todo.titel)
            bottomSheetBinding.editTextText.setText(todo.text)

            // Create a BottomSheetDialog
            val bottomSheetDialog = BottomSheetDialog(holder.itemView.context)
            bottomSheetDialog.setContentView(bottomSheetBinding.root)

            // Set dialog window attributes
            val window = bottomSheetDialog.window
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, // Width matches parent
                ViewGroup.LayoutParams.WRAP_CONTENT // Height wraps content
            )
            window?.setGravity(Gravity.BOTTOM) // Align dialog at the bottom
            window?.attributes?.windowAnimations =
                style.BottomSheetDialogAnimation // Apply animation style

            // Set background color
            bottomSheetBinding.root.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    color.white
                )
            )

            // Set click listener for the "Save" button
            bottomSheetBinding.btnSave.setOnClickListener {
                // Save the edited title and text
                val newTitle = bottomSheetBinding.editTextTitle.text.toString()
                val newText = bottomSheetBinding.editTextText.text.toString()
                val creationDateTime =
                    SimpleDateFormat("dd-MM-yyyy hh:mm:ss a", Locale.getDefault()).format(Date())

                // Update the to-do item in the database
                if (newText.isNotEmpty() && newTitle.isNotEmpty()) {
                    val updatedToDo = todo.copy(
                        titel = newTitle,
                        text = newText,
                        creationDateTime = creationDateTime
                    )
                    viewModel.updateTODo(updatedToDo)
                }

                // Dismiss the BottomSheetDialog after saving
                bottomSheetDialog.dismiss()
            }

            // Set click listener for the "Cancel" button
            bottomSheetBinding.btnCancel.setOnClickListener {
                // Dismiss the BottomSheetDialog without saving
                bottomSheetDialog.dismiss()
            }

            // Show the BottomSheetDialog
            bottomSheetDialog.show()
        }



        holder.binding.btnDelete.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                // Media player is playing, so return without processing the click event
                return@setOnClickListener
            }
            // Darken the card alpha of the list item
            holder.binding.listTodo.alpha = 0.3f
            val dialog = AlertDialog.Builder(holder.itemView.context, style.CustomDialogTheme)
            dialog.setTitle(holder.itemView.context.getString(string.ConfirmDeletion))
            dialog.setMessage(holder.itemView.context.getString(string.ConfirmDeletionText))
            dialog.setPositiveButton(string.yes) { _, _ ->
                viewModel.deleteToDo(todo)
                Toast.makeText(
                    holder.itemView.context,
                    (string.deletedtodo),
                    Toast.LENGTH_SHORT
                ).show()
            }
            dialog.setNegativeButton(string.no) { _, _ -> }
            dialog.setOnDismissListener {
                // Restore the original card alpha when the dialog is dismissed
                holder.binding.listTodo.alpha = 1f
            }
            dialog.show()
        }

    }


    /**
     * Starts playback of the audio file associated with the to-do item.
     *
     * @param audioFilePath String The file path of the audio file.
     * @param holder ItemViewHolder The ViewHolder of the item being played.
     */
    private fun startPlayback(audioFilePath: String, holder: ItemViewHolder) {
        try {
            mediaPlayer.apply {
                reset() // Reset MediaPlayer before starting new playback
                setDataSource(audioFilePath)
                prepare()
                start()

                // Set tag to keep track of the currently playing audio file path
                currentPlayingFilePath = audioFilePath


                // Set isPlaying flag to true
                this@ToDoAdapter.isPlaying = true
                holder.binding.btnPlay.setColorFilter(Color.GREEN)

                setOnCompletionListener {
                    stopPlayback(holder)
                }

                holder.binding.tvRemainingTime.visibility = View.VISIBLE

                updateRemainingTime(holder)
            }
        } catch (e: IOException) {
            handlePlaybackError(
                holder.itemView.context.getString(string.ErrorloadingVoiceNote),
                e,
                holder.itemView.context
            )
        } catch (e: IllegalStateException) {
            handlePlaybackError(
                holder.itemView.context.getString(string.Somethingwentwrong),
                e,
                holder.itemView.context
            )
        }
    }


    /**
     * Stops playback of the audio file.
     *
     * @param holder ItemViewHolder The ViewHolder of the item being played.
     */
    private fun stopPlayback(holder: ItemViewHolder) {
        mediaPlayer.stop()
        mediaPlayer.reset()

        // Clear isPlaying flag
        isPlaying = false

        holder.binding.btnPlay.setColorFilter(Color.RED)
        holder.binding.tvRemainingTime.visibility = View.GONE
    }


    /**
     * Updates the remaining playback time of the audio file.
     *
     * @param holder ItemViewHolder The ViewHolder of the item being played.
     */
    private fun updateRemainingTime(holder: ItemViewHolder) {
        val handler = Handler(Looper.myLooper()!!)
        handler.postDelayed(object : Runnable {
            @SuppressLint("SetTextI18n")
            override fun run() {
                mediaPlayer.let { player ->
                    val remainingTime = player.duration - player.currentPosition
                    val remainingTimeString = viewModel.formatTime(remainingTime.toLong())
                    holder.binding.tvRemainingTime.text =
                        holder.itemView.context.getString(string.RemainingTime) + remainingTimeString

                    if (player.isPlaying && remainingTime > 0) {
                        handler.postDelayed(this, 1000L)
                    } else {
                        holder.binding.tvRemainingTime.visibility = View.GONE
                    }
                }
            }
        }, 1000L)
    }


    /**
     * Displays a toast message.
     *
     * @param message String The message to be displayed.
     * @param context Context The context in which the toast will be shown.
     */
    private fun showToast(message: String, context: Context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Handles errors that occur during audio playback.
     *
     * @param message String The error message.
     * @param exception Exception The exception that occurred.
     * @param context Context The context in which the error occurred.
     */
    private fun handlePlaybackError(message: String, exception: Exception, context: Context) {
        Log.e(TAG, "Error during playback", exception)
        showToast(message, context)
    }


}

