package com.ddplay.yuntech.Common

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ddplay.yuntech.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.min
import kotlin.math.roundToInt

class CommonFunction {
    /*-------------------------------------------------------------------*/
    // My Location
    class MyLocation(private val context: Context) {
        // GPS
        private lateinit var locationManager: LocationManager
        private lateinit var defGPS: String
        private var longitude: Double = 0.0
        private var latitude: Double = 0.0
        // 尋找經緯度
        @SuppressLint("MissingPermission")
        fun findLocation(): CommonData.MyLocation {
            defGPS = LocationManager.GPS_PROVIDER //GPS定位
            defGPS = LocationManager.NETWORK_PROVIDER //網路定位
            //LocationManager可以用來獲取當前的位置，追蹤設備的移動路線
            locationManager = context.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(defGPS, 1000, 0f, locationListener)
            val location = locationManager.getLastKnownLocation(defGPS)
            if (location != null){
                longitude  = location.longitude
                latitude  = location.latitude
            }
            return CommonData.MyLocation(longitude, latitude)
        }
        fun findLat(): String {
            val Lat = findLocation().latitude
            return Lat.toString()
        }
        fun findLng(): String {
            val Lng = findLocation().longitude
            return Lng.toString()
        }
        // location監聽
        var locationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                longitude  = location.longitude
                latitude  = location.latitude
            }
            // Provider的轉態在可用、暫時不可用和無服務三個狀態直接切換時觸發此函數
            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            // Provider被enable時觸發此函數，比如GPS被打開
            override fun onProviderEnabled(provider: String) {}
            // Provider被disable時觸發此函數，比如GPS被關閉
            override fun onProviderDisabled(Provider: String) {}
        }
    }
    /*-------------------------------------------------------------------*/
    // 設定圖片大小 640*640
    fun scaleDown(bitmap: Bitmap): Bitmap? {
        val ratio = min(
            640F / bitmap.width,
            640F / bitmap.height
        )
        val width = (ratio * bitmap.width).roundToInt()
        val height = (ratio * bitmap.height).roundToInt()
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

    /*-------------------------------------------------------------------*/
    // 當前時間
    @RequiresApi(Build.VERSION_CODES.O)
    fun getTime(): String? {
        val currentDateTime = LocalDateTime.now()
        return currentDateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd_HH:mm"))
    }
    /*-------------------------------------------------------------------*/
    // 滑動背景刪除
    fun setDeleteIcon(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean,
    ) {
        val clearPaint = Paint()
        clearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        val backGround = ColorDrawable()
        val deleteDrawable = ContextCompat.getDrawable(viewHolder.itemView.context, R.drawable.delete)
        val intrinsicWidth: Int = deleteDrawable!!.intrinsicWidth
        val intrinsicHeight: Int = deleteDrawable.intrinsicHeight

        val itemView = viewHolder.itemView
        val itemHeight: Int = itemView.height

        val isCancelled: Boolean = dX == 0F && !isCurrentlyActive

        if (isCancelled) {
            c.drawRect(itemView.right + dX, itemView.top.toFloat(),
                itemView.right.toFloat(), itemView.bottom.toFloat(), clearPaint)
            return
        }
        backGround.color = Color.parseColor("#F80F0A")
        backGround.setBounds(itemView.left + dX.toInt(), itemView.top, itemView.left, itemView.bottom)
        backGround.draw(c)

        val deleteIconTop: Int = itemView.top + (itemHeight - intrinsicHeight) / 2
        val deleteIconMargin: Int = (itemHeight - intrinsicHeight) / 2
        val deleteIconLeft: Int = itemView.left + deleteIconMargin - intrinsicWidth
        val deleteIconRight: Int = itemView.left + deleteIconMargin
        val deleteIconBottom: Int = deleteIconTop + intrinsicHeight

        deleteDrawable.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
        deleteDrawable.draw(c)
    }
    /*-------------------------------------------------------------------*/
    // 滑動背景上傳
    fun setUploadIcon(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean,
    ) {
        val clearPaint = Paint()
        clearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        val backGround = ColorDrawable()
        val uploadDrawable = ContextCompat.getDrawable(viewHolder.itemView.context, R.drawable.upload)
        val intrinsicWidth: Int = uploadDrawable!!.intrinsicWidth
        val intrinsicHeight: Int = uploadDrawable.intrinsicHeight

        val itemView = viewHolder.itemView
        val itemHeight: Int = itemView.height

        val isCancelled: Boolean = dX == 0F && !isCurrentlyActive

        if (isCancelled) {
            c.drawRect(itemView.right + dX, itemView.top.toFloat(),
                itemView.right.toFloat(), itemView.bottom.toFloat(), clearPaint)
            return
        }
        backGround.color = Color.parseColor("#FF21D6B0")
        backGround.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
        backGround.draw(c)

        val uploadIconTop: Int = itemView.top + (itemHeight - intrinsicHeight) / 2
        val uploadIconMargin: Int = (itemHeight - intrinsicHeight) / 2
        val uploadIconLeft: Int = itemView.right - uploadIconMargin
        val uploadIconRight: Int = itemView.right - uploadIconMargin + intrinsicWidth
        val uploadIconBottom: Int = uploadIconTop + intrinsicHeight

        uploadDrawable.setBounds(uploadIconLeft, uploadIconTop, uploadIconRight, uploadIconBottom)
        uploadDrawable.draw(c)
    }
}