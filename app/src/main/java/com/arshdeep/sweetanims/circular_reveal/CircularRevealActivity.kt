package com.arshdeep.sweetanims.circular_reveal

import android.animation.Animator
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.arshdeep.sweetanims.R
import com.arshdeep.sweetanims.databinding.ActivityCircularRevealBinding

class CircularRevealActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCircularRevealBinding

    private var isOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCircularRevealBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fab.setOnClickListener { viewMenu() }
    }

    private fun viewMenu() {

        if (!isOpen) {

            val x = binding.layoutContent!!.right
            val y = binding.layoutContent!!.bottom

            val startRadius = 0
            val endRadius = Math.hypot(binding.layoutMain!!.width.toDouble(), binding.layoutMain!!.height.toDouble()).toInt()

            binding.fab!!.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, android.R.color.white, null))
            binding.fab!!.setImageResource(R.drawable.ic_close_grey)

            val anim = ViewAnimationUtils.createCircularReveal(binding.layoutButtons, x, y, startRadius.toFloat(), endRadius.toFloat())

            binding.layoutButtons!!.visibility = View.VISIBLE
            anim.start()

            isOpen = true

        } else {

            val x = binding.layoutButtons!!.right
            val y = binding.layoutButtons!!.bottom

            val startRadius = Math.max(binding.layoutContent!!.width, binding.layoutContent!!.height)
            val endRadius = 0

            binding.fab!!.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.colorAccent, null))
            binding.fab!!.setImageResource(R.drawable.ic_plus_white)

            val anim = ViewAnimationUtils.createCircularReveal(binding.layoutButtons, x, y, startRadius.toFloat(), endRadius.toFloat())
            anim.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {

                }

                override fun onAnimationEnd(animator: Animator) {
                    binding.layoutButtons!!.visibility = View.GONE
                }

                override fun onAnimationCancel(animator: Animator) {

                }

                override fun onAnimationRepeat(animator: Animator) {

                }
            })
            anim.start()

            isOpen = false
        }
    }
}