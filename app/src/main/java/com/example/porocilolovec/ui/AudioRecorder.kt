package com.example.porocilolovec.ui
import android.content.Context
import android.media.MediaRecorder
import android.widget.Toast
import java.io.File


class AudioRecorder(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var audioFile: File? = null

    /**
     * Starts audio recording and saves it to a file.
     * @return File? - The audio file where recording will be saved, or null if an error occurs.
     */
    @Suppress("DEPRECATION")
    fun startRecording(): File? {
        return try {
            audioFile = File(context.filesDir, "EMP_audio.mp3")

            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(audioFile?.absolutePath)
                prepare()
                start()
            }

            //Toast.makeText(context, "Recording started", Toast.LENGTH_SHORT).show()
            audioFile
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error starting recording: ${e.message}", Toast.LENGTH_SHORT).show()
            null
        }
    }


    /**
     * Stops audio recording and finalizes the file.
     * @return File? - The saved audio file, or null if an error occurs.
     */
    fun stopRecording(): File? {
        return try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            //Toast.makeText(context, "Recording stopped and saved", Toast.LENGTH_SHORT).show()
            audioFile // Return the saved file reference
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error stopping recording: ${e.message}", Toast.LENGTH_SHORT).show()
            null
        }
    }

    fun getAudioFile(): File? {
        return audioFile
    }
}
