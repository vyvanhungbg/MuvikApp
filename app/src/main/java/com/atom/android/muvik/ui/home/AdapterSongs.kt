package com.atom.android.muvik.ui.home

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atom.android.muvik.R
import com.atom.android.muvik.data.model.Song
import com.atom.android.muvik.databinding.ItemSongBinding
import com.atom.android.muvik.databinding.PartialEmptyRecyclerViewBinding
import com.atom.android.muvik.utils.extension.setImage
import java.io.ByteArrayInputStream
import java.io.InputStream

class AdapterSongs(private val listener: AdapterSongs.ItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val songs = mutableListOf<Song>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == EMPTY_VIEW) {
            val binding = PartialEmptyRecyclerViewBinding.inflate(inflater, parent, false)
            return ViewHolderEmpty(binding)
        } else {
            val binding = ItemSongBinding.inflate(inflater, parent, false)
            return ViewHolderNormal(binding)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolderNormal) {
            val song = songs[position]
            holder.bind(song, position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (songs.size == 0) EMPTY_VIEW else NORMAL_VIEW
    }

    override fun getItemCount(): Int = if (songs.size > 0) songs.size else 1

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setData(list: MutableList<Song>) {
        songs.apply {
            clear()
            addAll(list)
        }
        notifyDataSetChanged()
    }

    fun getData() = songs

    inner class ViewHolderNormal(var binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(song: Song, position: Int) {
            binding.apply {
                textViewArtistSong.text = song.artist
                textViewNameSong.text = song.name
            }
            binding.imageSong.setImageResource(R.drawable.image_loading_item)
            song.setImage(binding.imageSong)
            binding.root.setOnClickListener {
                listener.onItemClick(position)
            }
        }
    }

    inner class ViewHolderEmpty(var binding: PartialEmptyRecyclerViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    companion object {
        private const val EMPTY_VIEW = 1
        private const val NORMAL_VIEW = 2
    }

}
