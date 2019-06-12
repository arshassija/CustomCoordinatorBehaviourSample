package com.arshdeep.sweetanims.circular_reveal

import android.animation.Animator
import android.content.res.ColorStateList
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewAnimationUtils
import com.arshdeep.sweetanims.R
import kotlinx.android.synthetic.main.activity_circular_reveal.*

class CircularRevealActivity : AppCompatActivity() {

    private var isOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_circular_reveal)

        fab.setOnClickListener { viewMenu() }
    }

    private fun viewMenu() {

        if (!isOpen) {

            val x = layoutContent!!.right
            val y = layoutContent!!.bottom

            val startRadius = 0
            val endRadius = Math.hypot(layoutMain!!.width.toDouble(), layoutMain!!.height.toDouble()).toInt()

            fab!!.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, android.R.color.white, null))
            fab!!.setImageResource(R.drawable.ic_close_grey)

            val anim = ViewAnimationUtils.createCircularReveal(layoutButtons, x, y, startRadius.toFloat(), endRadius.toFloat())

            layoutButtons!!.visibility = View.VISIBLE
            anim.start()

            isOpen = true

        } else {

            val x = layoutButtons!!.right
            val y = layoutButtons!!.bottom

            val startRadius = Math.max(layoutContent!!.width, layoutContent!!.height)
            val endRadius = 0

            fab!!.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.colorAccent, null))
            fab!!.setImageResource(R.drawable.ic_plus_white)

            val anim = ViewAnimationUtils.createCircularReveal(layoutButtons, x, y, startRadius.toFloat(), endRadius.toFloat())
            anim.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {

                }

                override fun onAnimationEnd(animator: Animator) {
                    layoutButtons!!.visibility = View.GONE
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