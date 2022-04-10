package com.ddplay.yuntech.Dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import com.ddplay.yuntech.R

class DialogSearch(context: Context): Dialog(context) {
    private var negativeListener: IOnCancelListener? = null
    private var positiveListener: IOnConfirmListener? = null
    private var searchListener: IOnSearchListener? = null
    private var editListener: IOnInputListener? = null
    private var message: String?= null

    fun negative(Listener: IOnCancelListener): DialogSearch {
        this.negativeListener = Listener
        return this
    }
    fun positive(Listener: IOnConfirmListener): DialogSearch {
        this.positiveListener = Listener
        return this
    }
    fun search(Listener: IOnSearchListener): DialogSearch {
        this.searchListener = Listener
        return this
    }
    fun editText(Listener: IOnInputListener): DialogSearch {
        this.editListener = Listener
        return this
    }
    fun recordSearch(msg: String?): DialogSearch {
        this.message = msg
        return this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_search)

        val btnNegative: Button = findViewById(R.id.btn_negative)
        val btnPositive: Button = findViewById(R.id.btn_positive)
        val imgSearch: ImageButton = findViewById(R.id.img_search)
        val imgDelete: ImageButton = findViewById(R.id.img_delete)
        val searchBar: EditText = findViewById(R.id.search_bar)

        // 記住上個搜尋紀錄
        message.let {
            searchBar.setText(it)
        }

        // 監聽按鈕，觸發功能
        btnNegative.setOnClickListener {
            negativeListener?.negative(this)
        }
        // 監聽按鈕，觸發功能
        btnPositive.setOnClickListener {
            positiveListener?.positive(this)
            editListener?.editText(this, searchBar.text.toString())
        }
        // 監聽按鈕，觸發功能
        imgSearch.setOnClickListener {
            searchListener?.search(this)
            editListener?.editText(this, searchBar.text.toString())
        }
        imgDelete.setOnClickListener {
            searchBar.setText("")
        }
    }
    // 監聽按鈕
    interface IOnCancelListener {
        fun negative(dialog: DialogSearch?)
    }
    interface IOnConfirmListener {
        fun positive(dialog: DialogSearch?)
    }
    interface IOnSearchListener {
        fun search(dialog: DialogSearch?)
    }
    interface IOnInputListener {
        fun editText(dialog: DialogSearch?, msg: String)
    }
}