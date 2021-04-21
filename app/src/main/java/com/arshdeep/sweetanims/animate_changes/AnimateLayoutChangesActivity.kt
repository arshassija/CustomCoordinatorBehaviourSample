package com.arshdeep.sweetanims.animate_changes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.arshdeep.sweetanims.databinding.ActivityAnimateLayoutChangesBinding

class AnimateLayoutChangesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnimateLayoutChangesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnimateLayoutChangesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.producer.setOnClickListener {
            if (binding.consumer.visibility == View.VISIBLE) {
                binding.consumer.visibility = View.GONE
                binding.producer.text = "SHOW"
            } else {
                binding.consumer.visibility = View.VISIBLE
                binding.producer.text = "HIDE"
            }
        }
    }
}