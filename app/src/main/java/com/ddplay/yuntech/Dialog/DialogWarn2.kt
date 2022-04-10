package com.ddplay.yuntech.Dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.ddplay.yuntech.R

class DialogWarn2(context: Context): Dialog(context) {
    private var message: String?= null
    private var positiveListener: IOnConfirmListener? = null
    private var closeListener: IOnCloseListener? = null
    // 設定功能
    fun setMessage(message: String?): DialogWarn2 {
        this.message = message
        return this
    }
    fun positive(Listener: IOnConfirmListener): DialogWarn2 {
        this.positiveListener = Listener
        return this
    }
    fun close(Listener: IOnCloseListener): DialogWarn2 {
        this.closeListener = Listener
        return this
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_warn_2)

        val btn_positive: Button = findViewById(R.id.btn_positive)
        val btn_close: ImageButton = findViewById(R.id.btn_close)
        val tv_msg: TextView = findViewById(R.id.tv_msg)
        // setMessage使用
        message?.let {
            tv_msg.text= it
        }
        // 監聽按鈕，觸發功能
        btn_positive.setOnClickListener {
            positiveListener?.positive(this)
        }
        // 監聽按鈕，觸發功能
        btn_close.setOnClickListener {
            closeListener?.close(this)
        }

    }
    interface IOnConfirmListener {
        fun positive(dialog: DialogWarn2?)
    }
    interface IOnCloseListener {
        fun close(dialog: DialogWarn2?)
    }
}