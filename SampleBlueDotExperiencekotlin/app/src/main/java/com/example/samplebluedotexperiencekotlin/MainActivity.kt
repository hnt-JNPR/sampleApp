package com.example.samplebluedotexperiencekotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import butterknife.BindView
import com.mist.android.*

class MainActivity : AppCompatActivity() {
    val contants = Constants()
    private val ORG_SECRET :String = contants.org_secret
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Load BlueDot Map screen in fragment, permissions are checked inside this fragment.
        setUPMapFragment(ORG_SECRET)
    }

    private fun setUPMapFragment(ORG_SECRET:String) {
        TODO("Not yet implemented")
    }
}