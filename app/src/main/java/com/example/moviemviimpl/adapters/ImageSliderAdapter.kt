package com.example.moviemviimpl.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moviemviimpl.R
import com.example.moviemviimpl.model.Backdrop
import kotlinx.android.synthetic.main.slider_item.view.imageViewSlider
import kotlinx.android.synthetic.main.slider_item_trailer.view.*


//class ImageSliderAdapter :
//    RecyclerView.Adapter<ImageSliderAdapter.MyViewHolder>() {

class ImageSliderAdapter(
    private var onPlayButtonClick: OnPlayButtonClickListener
) :
    ListAdapter<Backdrop, ImageSliderAdapter.ImageSliderViewHolder>(DiffUtilCallBack()) {

//    var list: List<Backdrop> = listOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImageSliderAdapter.ImageSliderViewHolder {
        val view: View
        when (viewType) {
            0 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(com.example.moviemviimpl.R.layout.slider_item_trailer, parent, false)
            }

            1 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(com.example.moviemviimpl.R.layout.slider_item, parent, false)
            }
            else -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(com.example.moviemviimpl.R.layout.slider_item, parent, false)
            }
        }

        return ImageSliderAdapter.ImageSliderViewHolder(view, onPlayButtonClick)
    }

    override fun onBindViewHolder(
        holder: ImageSliderViewHolder,
        position: Int
    ) {
//        holder.bind(list[position])
        val item = getItem(position)
        holder.bind(item)
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return 0
        }
        return 1
    }

//    override fun getItemCount(): Int {
//        return list.size
//    }

//    fun setItem(list: List<Backdrop>) {
//
//        this.list = list
//        notifyDataSetChanged()
//    }

    class ImageSliderViewHolder(
        itemView: View,
        private var onPlayButtonClickListener: OnPlayButtonClickListener
    ) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private val movieImageSlider = itemView.imageViewSlider

        init {
            val playButton = itemView.play_icon
            playButton?.setOnClickListener(this)
        }

        fun bind(movieImage: Backdrop) {
            val currentUrl = "https://image.tmdb.org/t/p/w400${movieImage.filePath}"

            Glide.with(itemView.context)
                .load(currentUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(movieImageSlider)
        }

        override fun onClick(v: View?) {
            onPlayButtonClickListener.onPlayButtonClick(adapterPosition)
        }


    }


}


class DiffUtilCallBack : DiffUtil.ItemCallback<Backdrop>() {
    override fun areItemsTheSame(oldItem: Backdrop, newItem: Backdrop): Boolean {
        return oldItem.filePath == newItem.filePath
    }

    override fun areContentsTheSame(oldItem: Backdrop, newItem: Backdrop): Boolean {
        return oldItem == newItem
    }

}

interface OnPlayButtonClickListener {
    fun onPlayButtonClick(position: Int)
}