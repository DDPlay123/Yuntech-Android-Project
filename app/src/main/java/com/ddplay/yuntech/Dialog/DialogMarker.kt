package com.ddplay.yuntech.Dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.util.Base64
import com.ddplay.yuntech.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

@SuppressLint("InflateParams")
class DialogMarker(context: Context): GoogleMap.InfoWindowAdapter {

    var mWindow: View = (context as Activity)
        .layoutInflater.inflate(R.layout.dialog_marker, null)

    private fun render(marker: Marker, view: View) {

        val tvName = view.findViewById<TextView>(R.id.tv_name)
        val tvTime = view.findViewById<TextView>(R.id.tv_time)
        val imgPicture = view.findViewById<ImageView>(R.id.img_picture)

        tvName.text = marker.title

        val data = marker.snippet.toString().split(",")
        tvTime.text = data[1]
        val decodedBytes: ByteArray = Base64.decode(data[0], Base64.NO_WRAP)
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        imgPicture.setImageBitmap(bitmap)

    }

    override fun getInfoContents(marker: Marker): View {
        render(marker, mWindow)
        return mWindow
    }

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }
}