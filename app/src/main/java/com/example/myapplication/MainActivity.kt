package com.example.myapplication

import android.annotation.SuppressLint
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.Util
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    private var playerView: PlayerView? = null

    private var player: SimpleExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        jsonParse(resources.getString(R.string.website)+"/api/signs/1")
        tryLogin("admin@example.com", "password")
        playerView = findViewById<PlayerView>(R.id.playerView)
    }

    private fun jsonParse(url: String) {
        val textView = findViewById<TextView>(R.id.centerText)

//        val url = "http://signtranslate.herokuapp.com/api/signs/1"

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                // Display the first 500 characters of the response string.
                val imageUrl = response.getString("image")
//                textView.text = "Response: %s".format(imageUrl)
                loadImage(resources.getString(R.string.website) + imageUrl)
            },
            Response.ErrorListener {
                textView.text = "That didn't work!"
            })

        RequestHandler.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    private fun loadImage(url: String) {
        val mImageView = findViewById<ImageView>(R.id.iv)
        val textView = findViewById<TextView>(R.id.imageText)

        val imageRequest = ImageRequest(url, Response.Listener<Bitmap>{ response ->
                textView.text = "It got here!"
                mImageView.setImageBitmap(response)
            },
            0,
            0,
            ImageView.ScaleType.CENTER_CROP,
            Bitmap.Config.RGB_565,
            Response.ErrorListener {
                textView.text = "That didn't work!"
            })

        RequestHandler.getInstance(this).addToRequestQueue(imageRequest)
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap) {
        val wrapper = ContextWrapper(applicationContext)
    }

    private fun tryLogin (email: String, password: String) {
        val url = resources.getString(R.string.website) + "/api/sessions"
        val textView = findViewById<TextView>(R.id.centerText)

        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url,
            JSONObject(
                mapOf(
                    "user" to mapOf(
                        "email" to email,
                        "password" to password
                    )
                )
            ),
            Response.Listener { response ->
                textView.text = "Name: %s".format(response.getString("name"))
            },
            Response.ErrorListener {
                textView.text = "That didn't work!"
            })

        RequestHandler.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            loadVideo()
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (Util.SDK_INT < 24 || player == null) {
            loadVideo()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        playerView?.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LOW_PROFILE
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        )
    }

    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0

    private fun releasePlayer() {
        if (player != null) {
            playWhenReady = player!!.getPlayWhenReady()
            playbackPosition = player!!.getCurrentPosition()
            currentWindow = player!!.getCurrentWindowIndex()
            player!!.release()
            player = null
        }
    }

    private fun loadVideo () {
        player = VideoPlayer.getPlayer(this)
        playerView?.player = player
        val mediaItem: MediaItem = MediaItem.fromUri(resources.getString(R.string.website) + "/video.mp4")

        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()

    }
}