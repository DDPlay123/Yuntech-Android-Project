package com.ddplay.yuntech.Dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import com.ddplay.yuntech.R

class DialogSort(context: Context): Dialog(context) {
    private var recentListener: IOnRecentListener? = null
    private var latestListener: IOnLatestListener? = null
    private var typeListener: IOnTypeListener? = null

    fun recent(Listener: IOnRecentListener): DialogSort {
        this.recentListener = Listener
        return this
    }
    fun latest(Listener: IOnLatestListener): DialogSort {
        this.latestListener = Listener
        return this
    }
    fun type(Listener: IOnTypeListener): DialogSort {
        this.typeListener = Listener
        return this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_sort)

        val btnRecent: Button = findViewById(R.id.btn_recent)
        val btnLatest: Button = findViewById(R.id.btn_latest)
        val btnType: Button = findViewById(R.id.btn_type)

        // 監聽按鈕，觸發功能
        btnRecent.setOnClickListener {
            recentListener?.recent(this)
        }
        // 監聽按鈕，觸發功能
        btnLatest.setOnClickListener {
            latestListener?.latest(this)
        }
        // 監聽按鈕，觸發功能
        btnType.setOnClickListener {
            typeListener?.type(this)
        }
    }
    // 監聽按鈕
    interface IOnRecentListener {
        fun recent(dialog: DialogSort?)
    }
    interface IOnLatestListener {
        fun latest(dialog: DialogSort?)
    }
    interface IOnTypeListener {
        fun type(dialog: DialogSort?)
    }
}