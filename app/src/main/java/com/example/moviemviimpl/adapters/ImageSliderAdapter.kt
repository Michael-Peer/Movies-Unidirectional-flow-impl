package com.example.moviemviimpl.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.moviemviimpl.R
import com.example.moviemviimpl.model.Backdrop
import kotlinx.android.synthetic.main.slider_item.view.*


class ImageSliderAdapter :
    RecyclerView.Adapter<ImageSliderAdapter.MyViewHolder>() {

    var list: List<Backdrop> = listOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImageSliderAdapter.MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(com.example.moviemviimpl.R.layout.slider_item, parent, false)
        return ImageSliderAdapter.MyViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setItem(list: List<Backdrop>) {

        this.list = list
        notifyDataSetChanged()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var movieImageSlider = itemView.imageViewSlider
        fun bind(movieImage: Backdrop) {
            val currentUrl = "https://image.tmdb.org/t/p/w400${movieImage.filePath}"

            Glide.with(itemView.context)
                .load(currentUrl)
                .apply(RequestOptions.overrideOf(400, 600))
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(movieImageSlider)
        }


    }


}