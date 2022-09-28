package com.atom.android.muvik.data.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Song(
    val id: String?,
    val name: String?,
    val artist: String?,
    val album: String?,
    val duration: String?,
    val url: String?,
    val image: ByteArray?
) : Parcelable
