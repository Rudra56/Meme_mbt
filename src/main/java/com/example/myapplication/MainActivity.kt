package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private lateinit var fetchButton: Button
    private lateinit var previousButton: Button
    private lateinit var nextButton: Button
    private lateinit var titleTextView: TextView
    private lateinit var memeImageView: ImageView

    private var memes: List<Meme> = emptyList()
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fetchButton = findViewById(R.id.button_fetch)
        previousButton = findViewById(R.id.button_previous)
        nextButton = findViewById(R.id.button_next)
        titleTextView = findViewById(R.id.text_title)
        memeImageView = findViewById(R.id.image_meme)

        fetchButton.setOnClickListener { fetchMemes() }
        previousButton.setOnClickListener { showPreviousMeme() }
        nextButton.setOnClickListener { showNextMeme() }


    }

    private fun fetchMemes() {
        lifecycleScope.launch {
            try {
                Log.d(TAG, "Starting API call")
                val response: Response<Meme> = RetrofitInstance.api.getWholesomeMeme()
                if (response.isSuccessful) {
                    val meme = response.body()
                    Log.d(TAG, "API call successful: Meme: $meme")
                    if (meme != null) {
                        memes = listOf(meme) // Convert single meme to list
                        Log.d(TAG, "Fetched 1 meme")
                        currentIndex = 0
                        showMeme()
                        Toast.makeText(
                            this@MainActivity,
                            "Fetched 1 meme",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Log.d(TAG, "No meme found")
                        Toast.makeText(this@MainActivity, "No meme found", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    logErrorResponse(response.errorBody())
                    Log.e(TAG, "API call failed with response code: ${response.code()}")
                    Toast.makeText(
                        this@MainActivity,
                        "API call failed with response code: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error during API call", e)
                Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun showMeme() {
        if (memes.isNotEmpty()) {
            val meme = memes.getOrNull(currentIndex)
            if (meme != null) {
                titleTextView.text = meme.title
                Glide.with(this).load(meme.url).into(memeImageView)
            } else {
                Log.e(TAG, "Meme at index $currentIndex is null")
            }
        } else {
            Log.e(TAG, "Meme list is empty")
        }
        // Update button states after showing meme

    }

    private fun showPreviousMeme() {
        if (currentIndex > 0) {
            currentIndex--
            showMeme()
        }
    }

    private fun showNextMeme() {
        if (currentIndex < memes.size - 1) {
            currentIndex++
            showMeme()
        }
    }


    private fun logErrorResponse(errorBody: ResponseBody?) {
        errorBody?.let {
            try {
                Log.e(TAG, "Error body: ${it.string()}")
            } catch (e: Exception) {
                Log.e(TAG, "Error reading error body", e)
            }
        }
    }
}