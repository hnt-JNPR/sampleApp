package com.example.samplebluedotexperiencekotlin.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.example.samplebluedotexperiencekotlin.databinding.MapFragmentBinding
import com.example.samplebluedotexperiencekotlin.initializer.MistSdkManager
import com.mist.android.ErrorType
import com.mist.android.IndoorLocationCallback
import com.mist.android.MistMap
import com.mist.android.MistPoint
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso


class MapFragment : Fragment(),IndoorLocationCallback{

    private var _binding : MapFragmentBinding?=null
    private val binding get() = _binding!!

    private lateinit var mistSdkManager : MistSdkManager

    val TAG = MapFragment::class.java.simpleName

    private var PERMISSION_REQUEST_BLUETOOTH_LOCATION : Int = 1

    private val REQUEST_ENABLE_BLUETOOTH : Int = 1

    private val SDK_TOKEN : String = "sdktoken"

    private lateinit var mainApplication : Application

    private lateinit var orgSecret : String

    private var floorPlanImageUrl : String = ""

    private var addedMap : Boolean = false

    private var scaleXFactor: Double = 0.0

    private var scaleYFactor : Double = 0.0

    private var scaleFactorCalled : Boolean = true

    private var floorImageLeftmargin : Float = 0.0F

    private var floorImageTopmargin : Float = 0.0F

    //private lateinit var unbinder: Unbinder

    lateinit var currentmap : MistMap

    //@BindView(R.id.floorplanbluedot)
    //var floorplanBluedotView : FrameLayout = binding.floorplanbluedot

    //@BindView(R.id.progress_bar)
    //var progressBar: ProgressBar = binding.pro

    //@BindView(R.id.floorplan_image)
    //var floorPlanImage: ImageView = bindin

    //@BindView(R.id.txt_error)
    //var txtError : TextView = txt_error

    fun newInstance(sdkToken: String): MapFragment {
        val bundle = Bundle()
        bundle.putString(SDK_TOKEN, sdkToken)
        val mapFragment = MapFragment()
        mapFragment.arguments = bundle
        return mapFragment
    }

    /**
     * Implementation of fragment lifecycle methods.
     * https://developer.android.com/guide/fragments/lifecycle
     * onCreateView
     * onViewCreated
     * onStart
     * onDestroyView
     * onStop
     */

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //val view:View=inflater.inflate(R.layout.map_fragment,container,false)
        _binding = MapFragmentBinding.inflate(inflater,container,false)
        //unbinder = ButterKnife.bind(this,view)
        binding.progressBar.visibility=View.VISIBLE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity != null) {
            mainApplication = requireActivity().application
        }
        if (arguments != null) {
            orgSecret = requireArguments().getString(SDK_TOKEN)!!
        }
       mistSdkManager = MistSdkManager().getInstance(mainApplication.applicationContext)!!
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG,"SampleBlueDot onStart called")
        checkPermissionAndStartSDK()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //unbinder.unbind()
        _binding = null
        mistSdkManager.destroyMistSdk()
    }

    override fun onStop() {
        super.onStop()
        mistSdkManager.stopMistSdk()
    }

    private fun showLocationBluetoothPermissionDialog(permissionRequired: ArrayList<String>) {
        if(activity!=null){
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("This app needs bluetooth and location permission")
            builder.setMessage("Please grant bluetooth/location access so this app can detect beacons in the background")
            builder.setPositiveButton(android.R.string.ok, null)
            builder.setOnDismissListener {
                //requestPermissions(permissionRequired.toArray() as Array<out String>, PERMISSION_REQUEST_BLUETOOTH_LOCATION)
            }
            builder.show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (activity != null) {
            when (requestCode) {
                PERMISSION_REQUEST_BLUETOOTH_LOCATION ->
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "fine location permission granted !!")
                        checkIfBluetoothEnabled()
                        // Start the SDK when permissions are provided
                        startSDK(orgSecret)
                    } else {
                        val builder = AlertDialog.Builder(activity)
                        builder.setTitle("Functionality Limited")
                        builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background")
                        builder.setPositiveButton(android.R.string.ok, null)
                        builder.setOnDismissListener{
                            fun onDismiss(dialog: DialogInterface?) {}
                        }
                        builder.show()
                    }
            }
        }
    }

    private fun checkIfBluetoothEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && activity != null && requireActivity().checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        val bluetoothAdapter : BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if(!(bluetoothAdapter!=null && bluetoothAdapter.isEnabled() && bluetoothAdapter.state== BluetoothAdapter.STATE_ON)){
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH)
        }
    }

    private fun startSDK(orgSecret: String) {
        Log.d(TAG, "SampleBlueDot startSdk called$orgSecret")
        if(orgSecret!=null){
            mistSdkManager.init(orgSecret,this,null)
            mistSdkManager.startMistSdk()
        }
    }

    private fun checkPermissionAndStartSDK() {
        val permissionRequired = ArrayList<String>()
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && activity!=null && requireActivity().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            permissionRequired.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
        // For API 31 we need BLUETOOTH_SCAN permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && activity != null && requireActivity().checkSelfPermission(android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            permissionRequired.add(android.Manifest.permission.BLUETOOTH_SCAN)
        }
        // For API 31 we need BLUETOOTH_CONNECT permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && activity != null && requireActivity().checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            permissionRequired.add(android.Manifest.permission.BLUETOOTH_CONNECT)
        }
        if(permissionRequired.size > 0){
            showLocationBluetoothPermissionDialog(permissionRequired)
        }
        else{
            checkIfBluetoothEnabled()
            Log.d(TAG,"SampleBlueDot initMistSDK called")
            startSDK(orgSecret)
        }
    }

    /** Implementation of Mist Location Sdk callback methods.
     * onRelativeLocationUpdated
     * onMapUpdated
     * onError
     * didRangeVirtualBeacon
     * onVirtualBeaconListUpdated
     */

    override fun onRelativeLocationUpdated(relativeLocation: MistPoint?) {
         //Returns updated location of the mobile client (as a point (X, Y) measured in meters from the map origin, i.e., relative X, Y)
        if(activity !=null){
            requireActivity().runOnUiThread {
                if (currentmap != null && addedMap) {
                    renderBlueDot(relativeLocation)
                }
            }
        }
    }

    override fun onMapUpdated(map: MistMap?) {
        // Returns update map for the mobile client as a []MSTMap object
        Log.d(TAG, "SampleBlueDot onMapUpdated called")
        floorPlanImageUrl = map!!.url
        Log.d(TAG,"SampleBlueDot "+ floorPlanImageUrl)
        // Set the current map
        if(activity!=null && (binding.floorplanImage.drawable==null || this.currentmap == null || !this.currentmap.id.equals(map.id))){
            this.currentmap = map
            requireActivity().runOnUiThread {
                renderImage(floorPlanImageUrl)
            }
        }
    }
    override fun onError(errorType: ErrorType?, errorMessage: String?) {
        Log.d(TAG,"SampleBlueDot onError called" + errorMessage + "errorType " + errorType)
        binding.floorplanbluedot.visibility = View.GONE
        binding.floorplanImage.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.txtError.visibility = View.VISIBLE
        binding.txtError.text = errorMessage

    }

    /**
     * Utility function for rendering bluedot and floor plan
     * renderImage
     * renderBlueDot
     * setupScaleFactorForFloorplan
     * convertCloudPointToFloorplanXScale
     * convertCloudPointToFloorplanYScale
     */

    /**
     * This method is used for rendering the map image using the url from the MSTMap object received
     * from OnMapUpdated callback.
     */

    private fun renderImage(floorPlanImageUrl: String?) {
        Log.d(TAG,"In Piccaso")
        addedMap = false
        binding.floorplanImage.visibility=View.VISIBLE
        Picasso.with(activity).load(floorPlanImageUrl).networkPolicy(NetworkPolicy.OFFLINE).into(binding.floorplanImage, object : Callback {
                override fun onSuccess() {
                    Log.d(TAG, "Image loaded successfully from the cached")
                    addedMap = true
                    binding.floorplanbluedot.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                    if (!scaleFactorCalled) {
                        setupScaleFactorForFloorplan()
                    }
                }

                override fun onError() {
                    Picasso.with(activity).load(floorPlanImageUrl)
                        .into(binding.floorplanImage, object : Callback {
                            override fun onSuccess() {
                                binding.floorplanbluedot.visibility = View.VISIBLE
                                binding.progressBar.visibility = View.GONE
                                addedMap = true
                                if (!scaleFactorCalled) {
                                    setupScaleFactorForFloorplan()
                                }
                                Log.d(TAG,"Image downloaded from server successfully !!")
                            }

                            override fun onError() {
                                binding.progressBar.visibility = View.GONE
                                Log.d(TAG, "Could not download the image from the server")
                            }
                        })
                }
            })

    }

    private fun renderBlueDot(point : MistPoint?) {
        binding.floorplanImage.visibility = View.VISIBLE
        if(activity!=null){
            requireActivity().runOnUiThread {
                if (binding.floorplanImage != null && binding.floorplanImage.drawable != null && point != null && addedMap) {
                    // When rendering bluedot hiding old error text
                    binding.txtError.visibility = View.GONE
                    val xPos: Float = convertCloudPointToFloorplanXScale(point.getX())
                    val yPos: Float = convertCloudPointToFloorplanYScale(point.getY())
                    // If scaleX and scaleY are not defined, check again
                    if (!scaleFactorCalled && (scaleXFactor == 0.0 || scaleYFactor == 0.0)) {
                        setupScaleFactorForFloorplan()
                    }
                    val leftMargin: Float = floorImageLeftmargin + (xPos - binding.floorplanbluedot.width) / 2
                    val topMargin: Float = floorImageTopmargin + (yPos - binding.floorplanbluedot.height) / 2
                    binding.floorplanbluedot.x = leftMargin
                    binding.floorplanbluedot.y = topMargin
                }
            }
        }
    }

    private fun setupScaleFactorForFloorplan(){
        if(binding.floorplanImage!=null){
            val vto:ViewTreeObserver = binding.floorplanImage.viewTreeObserver
            vto.addOnGlobalLayoutListener {
                if (binding.floorplanImage!= null) {
                    floorImageLeftmargin= binding.floorplanImage.left.toFloat()
                    floorImageTopmargin = binding.floorplanImage.top.toFloat()
                    if (binding.floorplanImage.drawable != null) {
                        scaleXFactor = binding.floorplanImage.width / binding.floorplanImage.drawable.intrinsicWidth.toDouble()
                        scaleYFactor = binding.floorplanImage.height / binding.floorplanImage.drawable.intrinsicHeight.toDouble()
                        scaleFactorCalled = true
                    }
                }
            }
        }
    }

    /**
     * converting the y point from meter's to pixel with the present scaling factor of the map
     * rendered in the imageview
     */
    private fun convertCloudPointToFloorplanYScale(y: Double): Float {
        return (y * scaleYFactor * currentmap.getPpm()).toFloat()
    }
    /**
     * Converting the x point from meter's to pixel with the present scaling factor of the map
     * rendered in the imageview
     */
    private fun convertCloudPointToFloorplanXScale(x: Double): Float {
        return (x * scaleXFactor * currentmap.getPpm()).toFloat()
    }

}

