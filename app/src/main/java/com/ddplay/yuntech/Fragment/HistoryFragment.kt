package com.ddplay.yuntech.Fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ddplay.yuntech.Activity.MapActivity
import com.ddplay.yuntech.Adapter.HistoryAdapter
import com.ddplay.yuntech.Common.CommonData
import com.ddplay.yuntech.Common.CommonFunction
import com.ddplay.yuntech.Database.SQLite
import com.ddplay.yuntech.Dialog.*
import com.ddplay.yuntech.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class HistoryFragment : Fragment() {
    // define data
    private val History = ArrayList<CommonData.History>()
    // define adapter
    private lateinit var adapter: HistoryAdapter
    // define common parameter
    private var MyView: View ?= null
    private lateinit var dbrw: SQLiteDatabase

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // define view
        val view: View = inflater.inflate(R.layout.fragment_history, container, false)
        // setting common parameter
        MyView = view
        findView()
        // Initialize
        initial()
        // refresh
        refresh()
        // swap function
        swapDelete()
        swapUpload()
        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // main function
        // Refresh view
        swipeRefresh.setColorSchemeResources(R.color.yuntech)
        swipeRefresh.setOnRefreshListener {
            refresh()
        }
        btnRefresh.setOnClickListener {
            refresh()
        }
        var record: String ?= null
        tvSerach.setOnClickListener {
            var temp: ArrayList<CommonData.History>
            val searchDialog = DialogSearch(MyView!!.context)
            // 背景透明
            searchDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            // 顯示自定義的dialog
            searchDialog
                .recordSearch(record)
                .negative(object : DialogSearch.IOnCancelListener {
                    override fun negative(dialog: DialogSearch?) {
                        searchDialog.dismiss()
                    }
                })
                .positive(object : DialogSearch.IOnConfirmListener {
                    override fun positive(dialog: DialogSearch?) {
                        searchDialog.dismiss()
                    }
                })
                .search(object : DialogSearch.IOnSearchListener {
                    override fun search(dialog: DialogSearch?) {
                        searchDialog.dismiss()
                    }
                })
                .editText(object : DialogSearch.IOnInputListener {
                    override fun editText(dialog: DialogSearch?, msg: String) {
                        record = msg
                        temp = ArrayList()
                        History.forEachIndexed {_, items ->
                            if (items.variety.contains(msg)) temp.add(items)
                        }
                        adapter = HistoryAdapter(temp)
                        recycelerHistory.adapter = adapter
                        adapter.setOnItemClickListener(object : HistoryAdapter.OnItemClickListener{
                            override fun onItemClick(position: Int) {
                                val bundle = Bundle()
                                bundle.putString("name", temp[position].variety)
                                bundle.putString("time", temp[position].Time)
                                bundle.putString("lat", temp[position].Lat)
                                bundle.putString("lng", temp[position].Lng)
                                bundle.putByteArray("img", temp[position].Img)
                                val intent = Intent(MyView?.context, MapActivity::class.java)
                                intent.putExtras(bundle)
                                startActivity(intent)
                            }
                        })
                        adapter.notifyDataSetChanged()
                        if (temp.isEmpty()) notFound.visibility = View.VISIBLE
                        else notFound.visibility = View.INVISIBLE
                    }
                })
                .show()
            adapter.notifyDataSetChanged()
        }
        tvSort.setOnClickListener {
            val sortDialog = DialogSort(MyView!!.context)
            // 背景透明
            sortDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            // 顯示自定義的dialog
            sortDialog
                .recent(object : DialogSort.IOnRecentListener {
                    override fun recent(dialog: DialogSort?) {
                        recentData()
                        adapter.notifyDataSetChanged()
                        sortDialog.dismiss()
                    }
                })
                .latest(object : DialogSort.IOnLatestListener {
                    override fun latest(dialog: DialogSort?) {
                        latestData()
                        adapter.notifyDataSetChanged()
                        sortDialog.dismiss()
                    }
                })
                .type(object : DialogSort.IOnTypeListener {
                    override fun type(dialog: DialogSort?) {
                        History.sortBy { it.variety }
                        tvState.text = "排序方式：種類"
                        adapter.notifyDataSetChanged()
                        sortDialog.dismiss()
                    }
                })
                .show()
        }
    }
    // define ui views
    private lateinit var recycelerHistory: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var btnRefresh: Button
    private lateinit var tvSerach: TextView
    private lateinit var tvSort: TextView
    private lateinit var tvState: TextView
    private lateinit var notFound: ImageView

    private fun findView() {
        recycelerHistory = MyView!!.findViewById(R.id.recyceler_history)
        swipeRefresh = MyView!!.findViewById(R.id.swiperefresh)
        btnRefresh = MyView!!.findViewById(R.id.btn_refresh)
        tvSerach = MyView!!.findViewById(R.id.tv_serach)
        tvSort = MyView!!.findViewById(R.id.tv_sort)
        tvState = MyView!!.findViewById(R.id.tv_state)
        notFound = MyView!!.findViewById(R.id.not_found)
        dbrw = SQLite(MyView!!.context).writableDatabase
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refresh() {
        swipeRefresh.isRefreshing = true
        History.clear()
        initial()
        adapter.notifyDataSetChanged()
        adapter.setOnItemClickListener(object : HistoryAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                val bundle = Bundle()
                bundle.putString("name", History[position].variety)
                bundle.putString("time", History[position].Time)
                bundle.putString("lat", History[position].Lat)
                bundle.putString("lng", History[position].Lng)
                bundle.putByteArray("img", History[position].Img)
                val intent = Intent(MyView?.context, MapActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        })
        swipeRefresh.isRefreshing = false
    }

    private fun recentData() {
        History.clear()
        // 讀取所有資料
        val c = dbrw.rawQuery("SELECT * FROM history", null)
        // 從第一筆開始輸出
        c.moveToFirst()
        for (i in 0 until c.count){
            History.add(CommonData.History(
                c.getString(1),
                c.getBlob(2),
                c.getString(3),
                c.getString(4),
                c.getString(5)))
            c.moveToNext()
        }
        History.reverse()
        c.close()
        tvState.text = "排序方式：最近時間"
    }

    private fun latestData() {
        recentData()
        History.reverse()
        tvState.text = "排序方式：最晚時間"
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initial() {
        // setting LinearLayoutManager
        recycelerHistory.setHasFixedSize(true)
        recycelerHistory.layoutManager = LinearLayoutManager(MyView!!.context)
        recentData()
        adapter = HistoryAdapter(History)
        recycelerHistory.adapter = adapter
        adapter.notifyDataSetChanged()
        if (History.isEmpty()) notFound.visibility = View.VISIBLE
        else notFound.visibility = View.INVISIBLE
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun swapDelete() {
        val itemSwipe = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return false
            }

            override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
                return 1f
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean,
            ) {
                 CommonFunction().setDeleteIcon(c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive)
                super.onChildDraw(c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive)
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val warnDialog = DialogWarn(MyView!!.context)
                // 定義 message為的內容
                val message = "是否要刪除???"
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
                            dbrw = SQLite(MyView!!.context).writableDatabase
                            dbrw.execSQL("delete from history where " +
                                    "Variety = '${History[position].variety}' AND " +
                                    "Time = '${History[position].Time}'")
                            History.removeAt(position)
                            warnDialog.dismiss()
                            adapter.notifyDataSetChanged()
                        }
                    })
                    .close(object : DialogWarn.IOnCloseListener{
                        override fun close(dialog: DialogWarn?) {
                            warnDialog.dismiss()
                        }
                    })
                    .show()
                adapter.notifyDataSetChanged()
            }

        }
        val swap = ItemTouchHelper(itemSwipe)
        swap.attachToRecyclerView(recycelerHistory)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun swapUpload() {
        val itemSwipe = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return false
            }

            override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
                return 1f
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean,
            ) {
                CommonFunction().setUploadIcon(c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive)
                super.onChildDraw(c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive)
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val warnDialog = DialogWarn(MyView!!.context)
                // 定義 message為的內容
                val message = "上傳至伺服器?"
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
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun positive(dialog: DialogWarn?) {
                            warnDialog.dismiss()
                            upload(History[position].variety,
                                   History[position].Lng,
                                   History[position].Lat,
                                   History[position].Img)
                        }
                    })
                    .close(object : DialogWarn.IOnCloseListener{
                        override fun close(dialog: DialogWarn?) {
                            warnDialog.dismiss()
                        }
                    })
                    .show()
                adapter.notifyDataSetChanged()
            }

        }
        val swap = ItemTouchHelper(itemSwipe)
        swap.attachToRecyclerView(recycelerHistory)
    }
    // Upload
    @RequiresApi(Build.VERSION_CODES.O)
    private fun upload(uploadName: String,
                       uploadLng: String,
                       uploadLat: String,
                       uploadImg: ByteArray) {
        loading.show(MyView!!.context, "上傳中...")
        val currentDateTime = LocalDateTime.now()
        val uploadDate = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        val requestBody = FormBody.Builder()
            .add("name", uploadName)
            .add("longitude", uploadLng)
            .add("latitude", uploadLat)
            .add("date", uploadDate)
            .build()
        val request = Request.Builder()
            .addHeader("Content-Type", "application/json")
            .url(CommonData().detailUrl)
            .post(requestBody)
            .build()


        OkHttpClient().newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    loading.dialog.dismiss()
                    Looper.prepare()
                    dialog("上傳失敗")
                    Looper.loop()
                }
                override fun onResponse(call: Call, response: Response) {
                    activity?.runOnUiThread {
                        val prefix = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", Locale.TAIWAN).format(Date())
                        val suffix = ".jpg"
                        val imageName = "$prefix$suffix"
                        val body = MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart(
                                "imageFile",
                                imageName,
                                RequestBody.create("image/jpeg".toMediaTypeOrNull(), uploadImg)
                            )
                            .build()
                        val req = Request.Builder()
                            .url(CommonData().imageUrl)
                            .post(body)
                            .build()
                        OkHttpClient().newCall(req)
                            .enqueue(object : Callback {
                                override fun onFailure(call: Call, e: IOException) {
                                    loading.dialog.dismiss()
                                    e.printStackTrace()
                                    Looper.prepare()
                                    dialog("上傳失敗")
                                    Looper.loop()
                                }
                                override fun onResponse(call: Call, response: Response) {
                                    loading.dialog.dismiss()
                                    Looper.prepare()
                                    dialog("上傳成功")
                                    Looper.loop()
                                }
                            })
                    }
                }
            })
    }

    private val loading = DialogLoading()

    private fun dialog(msg: String) {
        activity?.runOnUiThread {
            val warnDialog = DialogWarn2(MyView!!.context)
            // 背景透明
            warnDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            // 顯示自定義的dialog
            warnDialog
                .setMessage(msg) // 執行setMessage()改變此dialog的標題
                .positive(object : DialogWarn2.IOnConfirmListener {
                    override fun positive(dialog: DialogWarn2?) {
                        warnDialog.dismiss()
                    }
                })
                .close(object : DialogWarn2.IOnCloseListener{
                    override fun close(dialog: DialogWarn2?) {
                        warnDialog.dismiss()
                    }
                })
                .show()
        }
    }
}