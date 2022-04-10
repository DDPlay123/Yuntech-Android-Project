package com.ddplay.yuntech.Fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ddplay.yuntech.Activity.WebActivity
import com.ddplay.yuntech.Adapter.InfoAdapter
import com.ddplay.yuntech.Common.CommonData
import com.ddplay.yuntech.Dialog.DialogInfo
import com.ddplay.yuntech.R

class InfoFragment : Fragment() {
    private lateinit var recycelerInfo: RecyclerView
    private lateinit var btnWeb: Button

    private val Information = ArrayList<CommonData.Information>()
    private lateinit var adapter: InfoAdapter

    private var MyView: View ?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // define view
        loadData()
        val view: View = inflater.inflate(R.layout.fragment_info, container, false)
        recycelerInfo = view.findViewById(R.id.recyceler_info)
        btnWeb = view.findViewById(R.id.btn_web)
        recycelerInfo.setHasFixedSize(true)
        recycelerInfo.layoutManager = LinearLayoutManager(view.context)
        MyView = view
        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // main function
        adapter = InfoAdapter(Information)
        recycelerInfo.adapter = adapter
        adapter.notifyDataSetChanged()
        adapter.setOnItemClickListener(object : InfoAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                val infoDialog = DialogInfo(MyView!!.context)
                infoDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                infoDialog
                    .setIntImage(Information[position].img)
                    .setContent(Information[position].Content)
                    .close(object : DialogInfo.IOnCloseListener {
                        override fun close(dialog: DialogInfo?) {
                            infoDialog.dismiss()
                        }
                    })
                    .show()
            }
        })
        btnWeb.setOnClickListener {
            startActivity(Intent(MyView!!.context, WebActivity::class.java))
        }
    }
    private fun loadData() {
        for (i in 0 .. 10) {
            Information.add(CommonData.Information(
                CommonData().infoImg[i],
                CommonData().infoName[i],
                CommonData().infoContent[i]
            ))
        }
    }
}