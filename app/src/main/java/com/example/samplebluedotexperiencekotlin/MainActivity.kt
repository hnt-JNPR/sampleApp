package com.example.samplebluedotexperiencekotlin

import android.os.Bundle
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.example.samplebluedotexperiencekotlin.databinding.ActivityMainBinding
import com.example.samplebluedotexperiencekotlin.fragment.MapFragment
import com.mist.android.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    val contants = Constants()
    private val ORG_SECRET :String = contants.org_secret
    //val progressBar: ProgressBar =findViewById(R.id.progress_bar)
   // val progressBar=findViewById<ProgressBar>(R.id.progress_bar)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        //setContentView(R.layout.activity_main)
        setContentView(binding.root)

        // Load BlueDot Map screen in fragment, permissions are checked inside this fragment.
        setUPMapFragment(ORG_SECRET)
    }

    private fun setUPMapFragment(ORG_SECRET:String) {
        val MapFragment = MapFragment()
        val mapFragment = supportFragmentManager.findFragmentByTag(MapFragment.TAG)
        if (mapFragment == null) {
            supportFragmentManager.beginTransaction().replace(R.id.frame_fragment, MapFragment.newInstance(ORG_SECRET), MapFragment.TAG).addToBackStack(MapFragment.TAG).commit()
        }
    }

}