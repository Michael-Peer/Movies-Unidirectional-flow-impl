package com.example.moviemviimpl.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.moviemviimpl.R
import com.example.moviemviimpl.model.Trailer
import kotlinx.android.synthetic.main.movie_video_item.view.*


class MovieDetailVideosRecyclerAdapter(private var onVideoClickListener: OnVideoClickListener) :
    ListAdapter<Trailer, MovieDetailVideosRecyclerAdapter.MovieVideoViewHolder>(
        DiffUtilCallBackMovieVideo()
    ) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MovieVideoViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.movie_video_item, parent, false)
        return MovieVideoViewHolder(view, onVideoClickListener)
    }

    override fun onBindViewHolder(
        holder: MovieDetailVideosRecyclerAdapter.MovieVideoViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        holder.bind(item)
    }

    fun getTrailerKey(pos: Int): String? {
        val video = getItem(pos)
        video?.let {
            it.key?.let { key ->
                return key

            }
        }
        return null
    }

    class MovieVideoViewHolder(
        itemView: View,
        private var onVideoClickListener: OnVideoClickListener
    ) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private val videoImage = itemView.video_image


        init {
            itemView.setOnClickListener(this)
        }


        fun bind(trailer: Trailer) {

//                val currentUrl = "https://image.tmdb.org/t/p/w400${trailer.name}"

            val currentUrl = "https://img.youtube.com/vi/${trailer.key}/0.jpg"

//            val currentUrl = "https://img.youtube.com/vi/4CJBuUwd0Os/1.jpg"
            Glide.with(itemView.context)
                .load(currentUrl)
                .apply(RequestOptions.overrideOf(800, 400))
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(videoImage)

        }

        override fun onClick(v: View?) {
            onVideoClickListener.onVideoClick(adapterPosition)
        }


    }
}

interface OnVideoClickListener {
    fun onVideoClick(position: Int)
}

class DiffUtilCallBackMovieVideo : DiffUtil.ItemCallback<Trailer>() {

    override fun areItemsTheSame(oldItem: Trailer, newItem: Trailer): Boolean {
        return oldItem.key == newItem.key
    }

    override fun areContentsTheSame(oldItem: Trailer, newItem: Trailer): Boolean {
        return oldItem == newItem
    }

}