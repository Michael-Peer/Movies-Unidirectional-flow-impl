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
import com.example.moviemviimpl.model.Movie
import com.example.resclassex.adapters.OnMovieClickListener
import kotlinx.android.synthetic.main.movie_item.view.*

class SimilarMoviesAdapter(
    private val onMovieClickListener: OnMovieClickListener
) :
//    ListAdapter<Movie, SimilarMoviesAdapter.SimilarMoviesViewHolder>(
    ListAdapter<Movie, RecyclerView.ViewHolder>(
        DiffUtilCallBackSimilar()
    ) {

    private var emptyData = false

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        if (!emptyData) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
            return SimilarMoviesViewHolder(view, onMovieClickListener)
        }

        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_empty_data_item, parent, false)
        return EmptyDataViewHolder(view)


    }

    class EmptyDataViewHolder(view: View?): RecyclerView.ViewHolder(view!!) {

    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        if (!emptyData) {
            val item = getItem(position)
            (holder as SimilarMoviesViewHolder).bind(item)
        }

    }

    fun getCurrentItem(position: Int): Movie? {
        return if (currentList.size > 0) {
            getItem(position)
        } else {
            null
        }
    }


    fun setPlaceHolderEmptyData() {
        emptyData = true
        submitList(emptyList())
    }

    class SimilarMoviesViewHolder(
        itemView: View,
        private val onMovieClickListener: OnMovieClickListener
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {


        private var movieImage = itemView.movie_image
//        private var movieTitleText = itemView.movie_title_text

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(movie: Movie?) {
            movie?.let { movie ->
//                movieTitleText.text = movie.title

                val currentUrl = "https://image.tmdb.org/t/p/original${movie.posterPath}"

                Glide.with(itemView.context)
                    .load(currentUrl)
                    .apply(RequestOptions.overrideOf(350, 550))
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(movieImage)
            }
        }

        override fun onClick(v: View?) {
            onMovieClickListener.onMovieClick(adapterPosition)
        }
    }


}

class DiffUtilCallBackSimilar : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem == newItem
    }

}
