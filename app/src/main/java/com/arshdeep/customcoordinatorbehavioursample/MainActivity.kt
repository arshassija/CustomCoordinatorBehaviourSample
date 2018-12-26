package com.arshdeep.customcoordinatorbehavioursample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.view.View

class MainActivity : AppCompatActivity() {

    lateinit var view: View
    lateinit var fab: FloatingActionButton
    lateinit var snackbar: Snackbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        view = findViewById(R.id.parent_layout)
        fab = findViewById(R.id.fab)
        fab.setOnClickListener(View.OnClickListener { snackbar.show() })
        snackbar = Snackbar.make(view, "This is snackbar layout", Snackbar.LENGTH_LONG)
    }
}
