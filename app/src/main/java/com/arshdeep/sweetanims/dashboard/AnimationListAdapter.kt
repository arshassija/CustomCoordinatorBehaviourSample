package com.arshdeep.sweetanims.dashboard

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.arshdeep.sweetanims.R
import com.arshdeep.sweetanims.circular_reveal.CircularRevealActivity
import com.arshdeep.sweetanims.coordinator_bahviour.CustomCoordinatorLayoutBehaviourActivity

class AnimationListAdapter(var context: Context, var list: List<String>) : RecyclerView.Adapter<AnimationListAdapter.AnimationListVH>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): AnimationListVH {
        var view: View = LayoutInflater.from(context).inflate(R.layout.single_item_animation_list, viewGroup, false)
        var viewHolder = AnimationListVH(view)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: AnimationListVH, position: Int) {
        holder.title?.text = list[position]
        holder.title?.setOnClickListener {
            var intent: Intent? = null
            when (position) {
                0 -> {
                    intent = Intent(context, CustomCoordinatorLayoutBehaviourActivity::class.java)
                    context.startActivity(intent)
                }
                1 -> {
                    intent = Intent(context, CircularRevealActivity::class.java)
                    context.startActivity(intent)
                }
            }
        }
    }


    class AnimationListVH(itemview: View) : RecyclerView.ViewHolder(itemview) {

        var title: TextView? = null

        init {
            title = itemview.findViewById(R.id.title)
        }
    }
}
