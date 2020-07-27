package com.example.resclassex.adapters

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
import kotlinx.android.synthetic.main.movie_item.view.*


class MoviesRecyclerViewAdapter(private var onMovieClickListener: OnMovieClickListener) :
    ListAdapter<Movie, MoviesRecyclerViewAdapter.MoviesViewHolder>(
        DiffUtilCallBack()
    ) {

    private var listRef: List<Movie> = ArrayList()


    fun getCurrentItem(position: Int): Movie? {
        if (currentList.size > 0) {
            return getItem(position)
        } else {
            return null
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
        return MoviesViewHolder(view, onMovieClickListener)
    }

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    fun modifyList(list: List<Movie>) {
        listRef = list
        submitList(list)
    }


    fun filter(query: String?) {

        var filteredList = ArrayList<Movie>()

        if (!query.isNullOrEmpty()) {
            filteredList.addAll(listRef.filter { movie ->
                movie.title!!.toLowerCase().contains(query.toString().toLowerCase().trim())
            })
        } else {
            filteredList.addAll(listRef)
        }

        submitList(filteredList)
    }

    class MoviesViewHolder(itemView: View, private var onMovieClickListener: OnMovieClickListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private var movieImage = itemView.movie_image
        private var movieTitleText = itemView.movie_title_text

        init {
            itemView.setOnClickListener(this)
        }


        fun bind(item: Movie?) {
            item?.let { movie ->
                movieTitleText.text = movie.title

                val currentUrl = "https://image.tmdb.org/t/p/w400${movie.posterPath}"

                Glide.with(itemView.context)
                    .load(currentUrl)
                    .apply(RequestOptions.overrideOf(400, 600))
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

interface OnMovieClickListener {
    fun onMovieClick(position: Int)
}

class DiffUtilCallBack : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem == newItem
    }
}