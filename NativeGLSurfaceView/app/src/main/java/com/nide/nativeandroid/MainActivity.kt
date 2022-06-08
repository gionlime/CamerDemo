package com.nide.nativeandroid

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import kotlinx.android.synthetic.main.activity_main.*

import com.nide.naengine.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var renderer = NativeRenderer()
        var engine = NARender()

        // Example of a call to a native method
        sample_text.text = renderer.name + " and " + engine.name
        engine.compute()

        val intent = Intent(this, NAActivity::class.java)


        startActivity(intent)
    }
}
