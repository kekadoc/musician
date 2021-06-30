package com.kekadoc.project.musician.ui

import android.widget.ImageView
import android.widget.TextView
import coil.load
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerControlView
import com.kekadoc.project.musician.R
import com.kekadoc.project.musician.feature.album.network.api.Track

fun PlayerControlView.setMediaItem(track: Track?) {
    val imageViewCover = findViewById<ImageView>(R.id.exo_icon)
    val textViewName = findViewById<TextView>(R.id.exo_name)

    imageViewCover?.load(track?.coverUrl)
    textViewName?.text = track?.name
}
fun PlayerControlView.setMediaItem(track: MediaItem?) {
    val imageViewCover = findViewById<ImageView>(R.id.exo_icon)
    val textViewName = findViewById<TextView>(R.id.exo_name)

    imageViewCover?.load(track?.mediaMetadata?.artworkUri)
    textViewName?.text = track?.mediaMetadata?.title
}