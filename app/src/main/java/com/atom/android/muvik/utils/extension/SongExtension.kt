package com.atom.android.muvik.utils.extension

import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView
import com.atom.android.muvik.R
import com.atom.android.muvik.data.model.Song
import java.io.ByteArrayInputStream
import java.io.InputStream

fun Song.setImage(imageView: ImageView ){
    imageView.setImageResource(R.drawable.image_loading_item)
    image?.let {
        val byteArray: InputStream = ByteArrayInputStream(it)
        val songImage = BitmapFactory.decodeStream(byteArray)
        imageView.setImageBitmap(songImage)
    }
}
