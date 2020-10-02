package com.example.myapplication

import android.content.Context
import com.google.android.exoplayer2.SimpleExoPlayer

class VideoPlayer constructor(){
    companion object {
        private val videoPlayer: SimpleExoPlayer? = null
        fun getPlayer(context: Context) = videoPlayer ?: synchronized(this) {
            videoPlayer ?: SimpleExoPlayer.Builder(context).build();
        }

//        @Volatile
//        private var INSTANCE: RequestHandler? = null
//        fun getInstance(context: Context) =
//            INSTANCE ?: synchronized(this) {
//                INSTANCE ?: RequestHandler(context).also {
//                    INSTANCE = it
//                }
//            }
    }
}