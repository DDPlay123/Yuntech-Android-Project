package com.ddplay.yuntech.Fragment

import android.annotation.SuppressLint
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.ddplay.yuntech.Common.CommonFunction
import com.ddplay.yuntech.Database.SQLite
import com.ddplay.yuntech.Dialog.DialogMarker
import com.ddplay.yuntech.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.util.Base64
import com.ddplay.yuntech.Dialog.DialogInfo
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker


class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var btnRefresh: Button
    private lateinit var btnMylocation: ImageButton
    private var map: GoogleMap ?= null
    private var MyView: View ?= null
    private lateinit var dbrw: SQLiteDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // define view
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        MyView = view
        btnRefresh = view.findViewById(R.id.btn_refresh)
        btnMylocation = view.findViewById(R.id.btn_mylocation)
        val map = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        dbrw = SQLite(view.context).writableDatabase
        map.getMapAsync(this)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // main function
        btnRefresh.setOnClickListener {
            map?.clear()
            getData()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap) {
        map = p0
        getData()
        // initialize
        val (longitude, latitude) = CommonFunction.MyLocation(MyView!!.context).findLocation()
        // 開啟定位
        map?.isMyLocationEnabled = true
        // 關閉原生定位鈕
        map?.uiSettings?.isMyLocationButtonEnabled = false
        // 關閉原生按鈕
        map?.uiSettings?.isMapToolbarEnabled = false
        // 當前位置
        btnMylocation.setOnClickListener {
            // animateCamera 平滑移動
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(latitude, longitude), 15f))
        }
        map?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(latitude, longitude), 15f))
        map?.setInfoWindowAdapter(DialogMarker(MyView!!.context))
        map?.setOnInfoWindowClickListener { marker ->
            val data = marker.snippet.toString().split(",")
            val decodedBytes: ByteArray = Base64.decode(data[0], Base64.NO_WRAP)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            val infoDialog = DialogInfo(MyView!!.context)
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

    private fun getData() {
        activity?.runOnUiThread {
            // 讀取所有資料
            val c = dbrw.rawQuery("SELECT * FROM history", null)
            // 從第一筆開始輸出
            c.moveToFirst()
            for (i in 0 until c.count){
                // adder marker
                val marker = MarkerOptions()
                marker.position(LatLng(c.getString(4).toDouble(), c.getString(5).toDouble()))
                marker.title(c.getString(1))
                val myBytes: ByteArray = c.getBlob(2)
                val encodedString: String = Base64.encodeToString(myBytes, Base64.NO_WRAP)
                //val decodedBytes: ByteArray = Base64.decode(encodedString, Base64.NO_WRAP)
                marker.snippet("${encodedString}, ${c.getString(3)}")
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                map?.addMarker(marker)
                c.moveToNext()
            }
            c.close()
        }
    }

}