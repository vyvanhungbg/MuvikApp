package com.atom.android.muvik.utils.extension

import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.view.View
import android.widget.ImageView
import com.atom.android.muvik.R
import com.atom.android.muvik.data.model.Song
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.lang.Exception

fun Song.setImage(imageView: ImageView) {
    imageView.setImageResource(R.drawable.image_loading_item)
    image?.let {
        val byteArray: InputStream = ByteArrayInputStream(it)
        val songImage = BitmapFactory.decodeStream(byteArray)
        imageView.setImageBitmap(songImage)
    }
}


fun Song.Companion.extractSong(id: String, path: String, favorite: Int = 0): Song {
    try {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(path)
        val image = mediaMetadataRetriever.embeddedPicture

        val album = mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
        val artist = mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
        val genre = mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)

        val duration = mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)

        val name = mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        val _favorite = if (favorite > 0) true else false
        val song = Song(id, name, artist, album, duration, path, image, _favorite)
        return song
    } catch (ex: Exception) {
        throw IllegalArgumentException()
    }
}
