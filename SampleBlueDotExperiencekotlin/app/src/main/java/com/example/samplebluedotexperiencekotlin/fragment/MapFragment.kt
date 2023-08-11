package com.example.samplebluedotexperiencekotlin.fragment

import android.app.Application
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.Unbinder
import com.example.samplebluedotexperiencekotlin.R
import com.example.samplebluedotexperiencekotlin.initializer.MistSdkManager
import com.mist.android.ErrorType
import com.mist.android.IndoorLocationCallback
import com.mist.android.MistMap
import com.mist.android.MistPoint
import kotlin.math.absoluteValue


class MapFragment : Fragment(),IndoorLocationCallback {
   /* companion object{
        const val floorplanb= R.id.floorplanbluedot
    }*/

    private lateinit var mistSdkManager : MistSdkManager

    val TAG = MapFragment::class.java.simpleName

    private var PERMISSION_REQUEST_BLUETOOTH_LOCATION : Int = 1

    private val REQUEST_ENABLE_BLUETOOTH : Int = 1

    private val SDK_TOKEN : String = "sdktoken"

    private lateinit var mainApplication : Application

    private lateinit var orgSecret : String

    private var floorPlanImageUrl : String = ""

    private val addedMap : Boolean = false

    private var scaleXFactor: Double = 0.0

    private var scaleYFactor : Double = 0.0

    private var floorImageLeftmargin : Float = 0.0F

    private var floorImageTopmargin : Float = 0.0F

    private lateinit var unbinder: Unbinder

    lateinit var currentmap : MistMap

    lateinit var floorplanBluedotview : FrameLayout

    @BindView(R.id.floorplanbluedot)
    lateinit var floorplanBluedotView : FrameLayout

    //@BindView(R.id.floorplan_image)







    override fun onMapUpdated(map: MistMap?) {
        TODO("Not yet implemented")
    }

    override fun onRelativeLocationUpdated(relativeLocation: MistPoint?) {
        TODO("Not yet implemented")
    }

    override fun onError(errorType: ErrorType?, errorMessage: String?) {
        TODO("Not yet implemented")
    }
}