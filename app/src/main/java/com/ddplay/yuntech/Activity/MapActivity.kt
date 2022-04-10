package com.ddplay.yuntech.Activity

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.ImageButton
import com.ddplay.yuntech.Common.CommonFunction
import com.ddplay.yuntech.Dialog.DialogInfo
import com.ddplay.yuntech.Dialog.DialogMarker
import com.ddplay.yuntech.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var name: String
    private lateinit var time: String
    private lateinit var lat: String
    private lateinit var lng: String
    private lateinit var img: ByteArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        intent?.extras?.let {
            name = it.getString("name")!!
            time = it.getString("time")!!
            lat = it.getString("lat")!!
            lng = it.getString("lng")!!
            img = it.getByteArray("img")!!
        }
        loadMap()
    }
    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        val (longitude, latitude) = CommonFunction.MyLocation(this).findLocation()
        Mymap = map
        getData()
        // 開啟定位
        Mymap?.isMyLocationEnabled = true
        // 關閉原生定位鈕
        Mymap?.uiSettings?.isMyLocationButtonEnabled = false
        // 關閉原生按鈕
        Mymap?.uiSettings?.isMapToolbarEnabled = false
        // 當前位置
        btnMylocation.setOnClickListener {
            // animateCamera 平滑移動
            Mymap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(latitude, longitude), 15f))
        }
        Mymap?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(lat.toDouble(), lng.toDouble()), 15f))
        Mymap?.setInfoWindowAdapter(DialogMarker(this))
        Mymap?.setOnInfoWindowClickListener { marker ->
            val data = marker.snippet.toString().split(",")
            val decodedBytes: ByteArray = Base64.decode(data[0], Base64.NO_WRAP)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            val infoDialog = DialogInfo(this)
            infoDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            infoDialog
                .setBitmapImage(bitmap)
                .setContent(data[1])
                .close(object : DialogInfo.IOnCloseListener {
                    override fun close(dialog: DialogInfo?) {
                        infoDialog.dismiss()
                    }
                })
                .show()
        }
    }
    // get data
    private fun getData() {
        val marker = MarkerOptions()
        marker.position(LatLng(lat.toDouble(), lng.toDouble()))
        marker.title(name)
        val myBytes: ByteArray = img
        val encodedString: String = Base64.encodeToString(myBytes, Base64.NO_WRAP)
        marker.snippet("$encodedString, $time")
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
        Mymap?.addMarker(marker)
    }
    // load map
    private var Mymap: GoogleMap?= null
    private lateinit var btnMylocation: ImageButton
    private fun loadMap() {
        val map = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        btnMylocation = findViewById(R.id.btn_mylocation)
        map.getMapAsync(this)
    }

    fun back(view: View) {
        super.onBackPressed()
    }
}