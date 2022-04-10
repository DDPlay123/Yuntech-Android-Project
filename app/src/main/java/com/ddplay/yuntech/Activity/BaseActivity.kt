package com.ddplay.yuntech.Activity

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.viewpager2.widget.ViewPager2
import com.ddplay.yuntech.Adapter.ViewPagerAdapter
import com.ddplay.yuntech.Dialog.DialogWarn
import com.ddplay.yuntech.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class BaseActivity : AppCompatActivity() {
    // UI Views
    private lateinit var viewPager: ViewPager2
    private lateinit var navigation: BottomNavigationView
    // Request
    val permissions1 = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )
    val permissions2 = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private val requestCode = 0x1
    private val checkPermission = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        // Request permissions
        if (Build.VERSION.SDK_INT < 30) {
            requestPermission(permissions1)
        }else {
            requestPermission(permissions2)
        }
    }
    private fun requestPermission(permissions: Array<String>) {
        checkPermission.clear()
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
                    checkPermission.add(permission)

            }
        }
        if (checkPermission.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissions, requestCode)
        }else {
            mainFunction()
        }
    }
    private fun mainFunction() {
        // Define ViewPager2 and Adapter
        val adapter = ViewPagerAdapter(this)
        viewPager = findViewById(R.id.baseFragment)
        viewPager.adapter = adapter
        viewPager.isUserInputEnabled = false
        // Listener ViewPager2
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                // Listener Fragment slide to change BottomNavigationView
                navigation.menu.getItem(position).isChecked = true
            }
        })
        // Define BottomNavigationView
        navigation = findViewById(R.id.navigation)
        // Listener BottomNavigationView
        navigation.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.navigation_home ->{
                    viewPager.currentItem = 0
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_history ->{
                    viewPager.currentItem = 1
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_map ->{
                    viewPager.currentItem = 2
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_info ->{
                    viewPager.currentItem = 3
                    return@setOnItemSelectedListener true
                }else -> viewPager.currentItem = 0
            }
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            0x1 -> for (i in grantResults.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    val warnDialog = DialogWarn(this)
                    // 定義 message為的內容
                    val message = "有權限未啟用!!!"
                    // 背景透明
                    warnDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    // 顯示自定義的dialog
                    warnDialog
                        .setMessage(message) // 執行setMessage()改變此dialog的標題
                        .negative(object : DialogWarn.IOnCancelListener {
                            override fun negative(dialog: DialogWarn?) {
                                warnDialog.dismiss()
                            }
                        })
                        .positive(object : DialogWarn.IOnConfirmListener {
                            override fun positive(dialog: DialogWarn?) {
                                // 前往定位權限設定
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                val uri = Uri.fromParts("package", packageName, null)
                                intent.data = uri
                                startActivity(intent)
                                warnDialog.dismiss()
                            }
                        })
                        .close(object : DialogWarn.IOnCloseListener{
                            override fun close(dialog: DialogWarn?) {
                                warnDialog.dismiss()
                            }
                        })
                        .show()
                }else {
                    mainFunction()
                }
            }
        }
    }
}