package com.arshdeep.sweetanims.animate_changes

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.arshdeep.sweetanims.R
import kotlinx.android.synthetic.main.activity_animate_layout_changes.*

class AnimateLayoutChangesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animate_layout_changes)
        producer.setOnClickListener {
            if (consumer.visibility == View.VISIBLE) {
                consumer.visibility = View.GONE
                producer.text = "SHOW"
            } else {
                consumer.visibility = View.VISIBLE
                producer.text = "HIDE"
            }
        }
    }
}