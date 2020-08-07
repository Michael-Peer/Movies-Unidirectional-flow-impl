package com.example.moviemviimpl.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moviemviimpl.R
import com.example.moviemviimpl.model.Cast
import kotlinx.android.synthetic.main.movie_cast_item.view.*

class MovieCastAdapter :
    ListAdapter<Cast, MovieCastAdapter.MovieCastViewHolder>(
        DiffUtilCallBackCast()
    ) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieCastViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.movie_cast_item, parent, false)
        return MovieCastAdapter.MovieCastViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieCastViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }


    class MovieCastViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val castImage = itemView.cast_image
        private val castName = itemView.cast_name_text
        private val castChar = itemView.cast_char_text

        fun bind(cast: Cast) {
            castName.text = cast.name
            castChar.text = cast.character

            val currentUrl = "https://image.tmdb.org/t/p/w400${cast.profilePath}"

            Glide.with(itemView.context)
                .load(currentUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(castImage)

        }

    }
}


class DiffUtilCallBackCast : DiffUtil.ItemCallback<Cast>() {
    override fun areItemsTheSame(oldItem: Cast, newItem: Cast): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Cast, newItem: Cast): Boolean {
        return oldItem == newItem
    }
}
