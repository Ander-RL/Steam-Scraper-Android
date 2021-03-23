package com.arl.steamscraper

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.arl.steamscraper.data.entity.Game
import kotlinx.coroutines.*
import java.io.InputStream
import java.lang.Exception
import java.net.URL

class RVAdapter : RecyclerView.Adapter<RVAdapter.ViewHolder>() {

    var dataSet: List<Game> = arrayListOf()

    fun setData(data: List<Game>) {
        dataSet = data
        notifyDataSetChanged()
    }

    private suspend fun loadImageFromWeb(url: String): Drawable {
        return withContext(Dispatchers.IO) {
            val inputStream: InputStream = URL(url).content as InputStream
            // "src name" is a useless variable when not working with 9patch.
            Drawable.createFromStream(inputStream, "src name")
        }
    }

    // Nested class
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivGameImage: ImageView
        val ivGamePlatform1: ImageView
        val ivGamePlatform2: ImageView
        val ivGamePlatform3: ImageView
        val tvGameName: TextView
        val tvPriceOriginal: TextView
        val tvPriceDiscount: TextView
        val tvDiscount: TextView

        init {
            // Define click listener for the ViewHolder's View.
            ivGameImage = view.findViewById(R.id.iv_game_mini_image)
            ivGamePlatform1 = view.findViewById(R.id.iv_game_platform1)
            ivGamePlatform2 = view.findViewById(R.id.iv_game_platform2)
            ivGamePlatform3 = view.findViewById(R.id.iv_game_platform3)
            tvGameName = view.findViewById(R.id.tv_game_name)
            tvPriceOriginal = view.findViewById(R.id.tv_price_original)
            tvPriceDiscount = view.findViewById(R.id.tv_price_discount)
            tvDiscount = view.findViewById(R.id.tv_discount)
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

        try {
            MainScope().launch {
                val image = loadImageFromWeb(currentItem.imageUrl)
                viewHolder.ivGameImage.setImageDrawable(image)
            }
        } catch (e: Exception) {
            Log.d("RVAdapter", e.toString())
        }

        if (currentItem.isWindows) {
            viewHolder.ivGamePlatform1.setImageDrawable(
                ResourcesCompat.getDrawable(
                    viewHolder.ivGamePlatform1.resources,
                    R.drawable.windows_transparent,
                    null
                )
            )
        }
        if (currentItem.isMac) {
            viewHolder.ivGamePlatform2.setImageDrawable(
                ResourcesCompat.getDrawable(
                    viewHolder.ivGamePlatform2.resources,
                    R.drawable.mac_transparent,
                    null
                )
            )
        }
        if (currentItem.isLinux) {
            viewHolder.ivGamePlatform3.setImageDrawable(
                ResourcesCompat.getDrawable(
                    viewHolder.ivGamePlatform3.resources,
                    R.drawable.linux_transparent,
                    null
                )
            )
        }

        val discountString = currentItem.discount.toString() + "%"

        viewHolder.tvGameName.text = currentItem.name
        viewHolder.tvPriceOriginal.text = currentItem.initialPrice.toString()
        viewHolder.tvPriceDiscount.text = currentItem.finalPrice.toString()
        viewHolder.tvDiscount.text = discountString
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}