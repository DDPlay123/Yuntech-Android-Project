package com.ddplay.yuntech.Dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.ddplay.yuntech.R

class DialogInfo(context: Context): Dialog(context) {
    private var imgInt: Int ?= null
    private var imgBitmap: Bitmap ?= null
    private var content: String ?= null

    // set int img
    fun setIntImage(img: Int): DialogInfo {
        this.imgInt = img
        return this
    }
    // set bitmap img
    fun setBitmapImage(img: Bitmap): DialogInfo {
        this.imgBitmap = img
        return this
    }
    // set content
    fun setContent(content: String): DialogInfo {
        this.content = content
        return this
    }
    // set close function
    private var closeListener: IOnCloseListener? = null
    fun close(Listener: IOnCloseListener): DialogInfo {
        this.closeListener = Listener
        return this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_info)

        val image: ImageView = findViewById(R.id.img)
        val tvContent: TextView = findViewById(R.id.tv_content)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        val btnClose: ImageButton = findViewById(R.id.btn_close)

        if (imgInt != null){
            imgInt.let {
                Glide.with(context)
                    .load(it)
                    .listener(object : RequestListener<Drawable> {
                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean,
                        ): Boolean {
                            progressBar.visibility = View.INVISIBLE
                            return false
                        }
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean,
                        ): Boolean {
                            progressBar.visibility = View.VISIBLE
                            return false
                        }
                    })
                    .into(image)
            }
        }else {
            imgBitmap.let {
                Glide.with(context)
                    .load(it)
                    .listener(object : RequestListener<Drawable> {
                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean,
                        ): Boolean {
                            progressBar.visibility = View.INVISIBLE
                            return false
                        }
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean,
                        ): Boolean {
                            progressBar.visibility = View.VISIBLE
                            return false
                        }
                    })
                    .into(image)
            }
        }
        content.let {
            tvContent.text = it
        }
        btnClose.setOnClickListener {
            closeListener?.close(this)
        }
    }
    interface IOnCloseListener {
        fun close(dialog: DialogInfo?)
    }
}