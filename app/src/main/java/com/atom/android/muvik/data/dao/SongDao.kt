package com.atom.android.muvik.data.dao

import com.atom.android.muvik.data.model.Song

interface SongDao {
    fun findAll(): List<Song>
    fun findFavoriteSongs(): List<Song>
    fun insert(song: Song)
    fun update(id: String, name: String?, uri: String?): Int
    fun updateFavorite(id: String, favorite: Boolean): Int
    fun delete(id: String): Int
    fun findByName(name: String): List<Song>
    fun exists(id: String): Boolean
}
