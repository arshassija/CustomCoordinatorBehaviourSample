package com.arshdeep.sweetanims.coordinator_bahviour

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.arshdeep.sweetanims.R

class CustomCoordinatorLayoutBehaviourActivity : AppCompatActivity() {

    lateinit var view: View
    lateinit var fab: FloatingActionButton
    lateinit var snackbar: Snackbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coordinator_custom_behaviour)
        view = findViewById(R.id.parent_layout)
        fab = findViewById(R.id.fab)
        fab.setOnClickListener { snackbar.show() }
        snackbar = Snackbar.make(view, getString(R.string.snackbar_msg), Snackbar.LENGTH_LONG)
    }
}
