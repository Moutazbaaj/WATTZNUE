package de.syntax.androidabschluss.util

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.RequiresApi
import de.syntax.androidabschluss.R
import java.io.IOException


/**
 * AudioManager is a utility class responsible for managing audio recording and playback functionalities.
 *
 * @property context Context The context in which the AudioManager operates.
 * @property callback AudioManagerCallback A callback interface to communicate audio recording/playback events.
 */
class AudioManager(private val context: Context, private val callback: AudioManagerCallback) {

    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null


    // File path of the recorded audio
    var audioFilePath: String? = null
        private set

    private val handler = Handler(Looper.myLooper()!!)

    private var recordingStartTimeMillis: Long = 0
    private var isRecording = false


    /**
     * Starts audio recording.
     */
    fun startRecording() {
        // Check if MediaPlayer is playing
        if (mediaPlayer?.isPlaying == true) {
            Toast.makeText(context, R.string.PlaybackIsInProgress, Toast.LENGTH_SHORT).show()
            return
        }

        // Check if the recording is already in progress
        if (isRecording) {
            Toast.makeText(context, R.string.RecordingAlreadyInProgress, Toast.LENGTH_SHORT).show()
            return
        }

        // Define the audio file path
        audioFilePath =
            "${context.externalCacheDir?.absolutePath}/audio_${System.currentTimeMillis()}.3gp"

        // Initialize MediaRecorder and start recording

            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.DEFAULT)
                setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
                setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
                setOutputFile(audioFilePath)
                try {
                    prepare()
                    start()
                    recordingStartTimeMillis = System.currentTimeMillis()
                    handler.post(updateProgressTask)
                    isRecording = true
                    callback.onRecordingStarted()
                    Toast.makeText(context, R.string.RecordingStarted, Toast.LENGTH_SHORT)
                        .show() // Add this toast
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(context, R.string.FailedToStartRecording, Toast.LENGTH_SHORT).show()
                }
            }

    }


    /**
     * Stops audio recording.
     */
    fun stopRecording() {


        mediaRecorder?.apply {
            stop()
            release()
            handler.removeCallbacks(updateProgressTask)
            isRecording = false
            callback.onRecordingStopped()
            Toast.makeText(context, R.string.RecordingStopped, Toast.LENGTH_SHORT).show()
        }
        mediaRecorder = null
    }


    /**
     * Starts audio playback.
     */
    fun startPlayback() {

        // Check if recording is in progress
        if (isRecording) {
            Toast.makeText(context, R.string.RecordingIsInProgress, Toast.LENGTH_SHORT).show()
            return
        }

        // Check if MediaPlayer is already playing
        if (mediaPlayer?.isPlaying == true) {
            Toast.makeText(context, R.string.PlaybackAlreadyInProgress, Toast.LENGTH_SHORT).show()
            return
        }

        // Check if audioFilePath is null or empty
        if (audioFilePath.isNullOrEmpty()) {
            Toast.makeText(context, R.string.noAudio, Toast.LENGTH_SHORT).show()
            return
        }

        // Initialize MediaPlayer and start playback
        mediaPlayer = MediaPlayer().apply {
            setDataSource(audioFilePath)
            prepareAsync()
            setOnPreparedListener {
                Toast.makeText(context, R.string.AudioPlaying, Toast.LENGTH_SHORT)
                    .show() // Add this toast
                start()
                recordingStartTimeMillis = System.currentTimeMillis()
                handler.post(updateProgressTask)
                callback.onPlaybackStarted()
            }
            setOnCompletionListener {
                stopPlayback()
            }
            setOnErrorListener { _, _, _ ->
                Toast.makeText(context, R.string.AudioPlayingError, Toast.LENGTH_SHORT).show()
                stopPlayback()
                true
            }
        }
    }

    /**
     * Stops audio playback.
     */
    fun stopPlayback() {


        if (mediaPlayer == null) {
            // If mediaPlayer is null, nothing is playing
            return
        }


        if (mediaPlayer!!.isPlaying) {
            // If mediaPlayer is currently playing, stop it
            mediaPlayer?.stop()
            mediaPlayer?.release()

            handler.removeCallbacks(updateProgressTask)
            callback.onPlaybackStopped()
            Toast.makeText(context, R.string.playbackStop, Toast.LENGTH_SHORT).show()
        } else {
            // If mediaPlayer is not playing, it may have already been stopped manually
            Toast.makeText(context, R.string.playbackStop, Toast.LENGTH_SHORT).show()
        }

        // Reset mediaPlayer to null
        mediaPlayer = null
    }


    /**
     * Runnable to update progress or remaining time during recording/playback.
     */
    private val updateProgressTask = object : Runnable {
        override fun run() {
            val currentTimeMillis = System.currentTimeMillis()
            val elapsedTimeMillis = currentTimeMillis - recordingStartTimeMillis
            val remainingTimeMillis = if (isRecording) {
                elapsedTimeMillis
            } else {
                val remaining = mediaPlayer?.duration?.toLong()?.minus(elapsedTimeMillis) ?: 0L
                if (remaining < 0) 0L else remaining
            }

            // Update progress or remaining time as needed,
            callback.onProgressUpdated(elapsedTimeMillis, remainingTimeMillis, isRecording)

            // Continue updating if recording is in progress or remaining time > 0
            if (!isRecording || remainingTimeMillis > 0) {
                handler.postDelayed(this, 1000)
            } else {
                stopPlayback()
            }
        }
    }


    /**
     * Interface defining callbacks for audio recording/playback events.
     */
    interface AudioManagerCallback {
        fun onRecordingStarted()
        fun onRecordingStopped()
        fun onPlaybackStarted()
        fun onPlaybackStopped()
        fun onProgressUpdated(
            elapsedTimeMillis: Long,
            remainingTimeMillis: Long,
            isRecording: Boolean
        )
    }

}
