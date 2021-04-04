package com.arl.steamscraper

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
import com.arl.steamscraper.data.entity.Game
import com.arl.steamscraper.data.entity.Price
import com.arl.steamscraper.data.entity.relations.GameAndPrice
import kotlinx.coroutines.*
import java.io.InputStream
import java.net.URL

class RVAdapter(val context: Context) : RecyclerView.Adapter<RVAdapter.ViewHolder>() {

    var gameData = arrayListOf<Game>()
    var listPrice = HashMap<Game, List<Price>>()
    private var listener: OnItemClickListener? = null

    fun setData(games: List<GameAndPrice>) {
        for (element in games) {
            if (!gameData.contains(element.game)) {
                gameData.add(element.game)
                listPrice[element.game] = element.listPrice
            }
        }
        Log.d("RCV", listPrice.toString())
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
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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
            .inflate(R.layout.recyclerview_layout, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val currentItem = gameData[position]

        try {
            MainScope().launch {
                val image = loadImageFromWeb(currentItem.imageUrl)
                viewHolder.ivGameImage.setImageDrawable(image)
            }
        } catch (e: Exception) {
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

        for (game in listPrice) {
            if (game.key.appId == currentItem.appId) {

                val originalPrice = listPrice.getValue(game.key).last().originalPrice
                val originalFormatted = "${originalPrice}€"
                val currentPrice = listPrice.getValue(game.key).last().currentPrice
                val currentFormatted = "${currentPrice}€"
                val discount = listPrice.getValue(game.key).last().discount.toString() + "%"


                viewHolder.tvGameName.text = currentItem.name
                viewHolder.tvPriceDiscount.text = currentFormatted
                viewHolder.tvDiscount.text = discount

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
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = gameData.size

    interface OnItemClickListener {
        fun onItemClick(game: Game)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

}