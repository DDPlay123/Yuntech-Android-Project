package com.ddplay.yuntech.Dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import com.ddplay.yuntech.R

class DialogLoading{
    lateinit var dialog: CustomDialog
    // define ui
    private lateinit var cpTitle: TextView
    private lateinit var cpCardview: CardView
    private lateinit var cpPbar: ProgressBar

    fun show(context: Context): Dialog {
        return show(context, null)
    }

    @SuppressLint("InflateParams")
    fun show(context: Context, title: CharSequence?): Dialog {
        val inflater = (context as Activity).layoutInflater
        val view = inflater.inflate(R.layout.dialog_loading, null)
        cpTitle = view.findViewById(R.id.cp_title)
        cpCardview = view.findViewById(R.id.cp_cardview)
        cpPbar = view.findViewById(R.id.cp_pbar)

        if (title != null) {
            cpTitle.text = title
        }

        // Card Color
        cpCardview.setCardBackgroundColor(Color.WHITE)

        // Progress Bar Color
        setColorFilter(cpPbar.indeterminateDrawable, ResourcesCompat.getColor(context.resources, R.color.yuntech, null))

        // Text Color
        cpTitle.setTextColor(Color.BLACK)

        dialog = CustomDialog(context)
        dialog.setContentView(view)
        dialog.show()
        return dialog
    }

    private fun setColorFilter(drawable: Drawable, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
        } else {
            @Suppress("DEPRECATION")
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }

    class CustomDialog(context: Context) : Dialog(context, R.style.CustomDialogTheme) {
        init {
            // Set Semi-Transparent Color for Dialog Background
            window?.decorView?.rootView?.setBackgroundResource(R.color.transparent)
            window?.decorView?.setOnApplyWindowInsetsListener { _, insets ->
                insets.consumeSystemWindowInsets()
            }
        }
    }
}