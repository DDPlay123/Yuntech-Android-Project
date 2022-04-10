package com.ddplay.yuntech.Adapter

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.ddplay.yuntech.Common.CommonData
import com.ddplay.yuntech.R

class InfoAdapter(private val data: ArrayList<CommonData.Information>):
    RecyclerView.Adapter<InfoAdapter.ViewHolder>(){
    private lateinit var mListener: OnItemClickListener

    class ViewHolder(v: View, listener: OnItemClickListener): RecyclerView.ViewHolder(v) {
        val progressBar = v.findViewById<ProgressBar>(R.id.progressBar)
        val imgPicture = v.findViewById<ImageView>(R.id.img_picture)
        val tvVariety = v.findViewById<TextView>(R.id.tv_variety)

        init {
            v.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_info, parent, false)
        return ViewHolder(v, mListener)
    }

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(data[position].img)
            .listener(object : RequestListener<Drawable> {
                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.progressBar.visibility = View.INVISIBLE
                    return false
                }

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.progressBar.visibility = View.VISIBLE
                    return false
                }
            })
            .into(holder.imgPicture)

        holder.tvVariety.text = data[position].Name
    }

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(Listener: OnItemClickListener) {
        mListener = Listener
    }
}