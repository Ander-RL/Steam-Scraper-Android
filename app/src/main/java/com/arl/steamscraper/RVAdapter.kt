package com.arl.steamscraper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arl.steamscraper.data.entity.Game

class RVAdapter : RecyclerView.Adapter<RVAdapter.ViewHolder>() {

    var dataSet: List<Game> = arrayListOf()

    fun setData(data: List<Game>) {
        dataSet = data
        notifyDataSetChanged()
    }

    // Nested class
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivGameImage: ImageView
        val tvGameName: TextView
        val tvGamePlatform: TextView
        val tvPriceOriginal: TextView
        val tvPriceDiscount: TextView

        init {
            // Define click listener for the ViewHolder's View.
            ivGameImage = view.findViewById(R.id.iv_game_mini_image)
            tvGameName = view.findViewById(R.id.tv_game_name)
            tvGamePlatform = view.findViewById(R.id.tv_game_platform)
            tvPriceOriginal = view.findViewById(R.id.tv_price_original)
            tvPriceDiscount = view.findViewById(R.id.tv_price_discount)
        }
    }

    //   ----- Outside the nested class -----
    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.recyclerview_layout, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val currentItem = dataSet[position]

        viewHolder.tvGameName.text = currentItem.name
        viewHolder.tvGamePlatform.text = currentItem.isWindows.toString()
        viewHolder.tvPriceOriginal.text = currentItem.initialPrice.toString()
        viewHolder.tvPriceDiscount.text = currentItem.finalPrice.toString()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}