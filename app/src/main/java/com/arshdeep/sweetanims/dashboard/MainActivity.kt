package com.arshdeep.sweetanims.dashboard

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.arshdeep.sweetanims.R
import com.arshdeep.sweetanims.custom_view.DividerItemDecoration
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        val animationList = resources.getStringArray(R.array.animation_list)
        var list = mutableListOf<String>()
        animationList.forEach { list.add(it) }
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = AnimationListAdapter(this, list)
        recycler_view.addItemDecoration(DividerItemDecoration(this, R.drawable.divider))
    }
}
