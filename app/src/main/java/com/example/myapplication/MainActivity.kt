package com.example.myapplication

import android.content.ContextWrapper
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        jsonParse(resources.getString(R.string.website)+"/api/signs/1")
        tryLogin("admin@example.com", "password")
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
}