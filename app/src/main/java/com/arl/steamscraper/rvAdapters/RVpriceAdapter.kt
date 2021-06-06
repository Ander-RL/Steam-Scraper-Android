package com.arl.steamscraper.rvAdapters

import android.content.Context
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.arl.steamscraper.R
import com.arl.steamscraper.data.entity.Price
import com.arl.steamscraper.data.entity.relations.GameAndPrice
import kotlinx.coroutines.*
import java.io.InputStream
import java.net.URL

class RVpriceAdapter(val context: Context) : RecyclerView.Adapter<RVpriceAdapter.ViewHolder>() {

    var gameData = arrayListOf<Price>()
    private var originalPrice: Double = 0.0
    private var listener: OnItemClickListener? = null

    fun setData(games: List<Price>) {
        gameData = games as ArrayList<Price>
        notifyDataSetChanged()
    }

    // Nested class
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvPriceOriginal: TextView
        val tvPriceDiscount: TextView
        val tvDiscount: TextView
        val tvDate: TextView

        init {
            // Define click listener for the ViewHolder's View.
            tvPriceOriginal = view.findViewById(R.id.tv_price_original)
            tvPriceDiscount = view.findViewById(R.id.tv_price_discount)
            tvDiscount = view.findViewById(R.id.tv_discount)
            tvDate = view.findViewById(R.id.tv_price_date)

            view.setOnClickListener {
                val position = absoluteAdapterPosition
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener!!.onItemClick(gameData[position])
                }
            }
        }
    }

    //   ----- Outside the nested class -----
    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.recyclerview_pricehistory_layout, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val currentItem = gameData[position]

        val originalPrice = currentItem.originalPrice
        val currentPrice = currentItem.currentPrice
        val currentFormatted = "${currentPrice}€"
        val originalFormatted = "${originalPrice}€"
        val discount = currentItem.discount.toString() + "%"
        val date = currentItem.date


        viewHolder.tvPriceOriginal.text = originalPrice.toString()
        viewHolder.tvPriceDiscount.text = currentFormatted
        viewHolder.tvDiscount.text = discount
        viewHolder.tvDate.text = date // TODO Formatear fecha

        if (originalPrice > currentPrice) {
            viewHolder.tvPriceOriginal.paintFlags =
                (viewHolder.tvPriceOriginal.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG)
            viewHolder.tvPriceOriginal.text = originalFormatted
            viewHolder.tvPriceOriginal.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.red
                )
            )
            viewHolder.tvPriceDiscount.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.green
                )
            )
        } else {
            viewHolder.tvPriceOriginal.text = originalFormatted
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = gameData.size

    interface OnItemClickListener {
        fun onItemClick(price: Price)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

}