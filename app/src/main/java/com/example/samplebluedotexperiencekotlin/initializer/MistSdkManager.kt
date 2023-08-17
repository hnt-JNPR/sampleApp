package com.example.samplebluedotexperiencekotlin.initializer

import android.content.Context
import android.util.Log
import com.mist.android.IndoorLocationCallback
import com.mist.android.IndoorLocationManager
import com.mist.android.VirtualBeaconCallback
import java.lang.ref.WeakReference

class MistSdkManager {

    /**
     * Required by Mist SDK for initialization
     * IndoorLocationManager
     * IndoorLocationCallback
     * VirtualBeaconCallback
     */

    private var indoorLocationManager: IndoorLocationManager?=null
    private var indoorLocationCallback : IndoorLocationCallback?=null
    private var virtualBeaconCallback: VirtualBeaconCallback?=null
    private var contextWeakReference: WeakReference<Context>?=null
    private var sdkInitializer : MistSdkManager? = null
    private var orgSecret : String? = null

    fun getInstance(context: Context): MistSdkManager? {
        contextWeakReference = WeakReference(context)
        if (sdkInitializer == null) {
            sdkInitializer = MistSdkManager()
        }
        return sdkInitializer
    }

    fun init(orgSecret:String, indoorLocationCallback: IndoorLocationCallback, virtualBeaconCallback: VirtualBeaconCallback?){
        if(orgSecret!=null && !orgSecret.isEmpty()){
            Log.d("","Sample Blue Dot Init"+orgSecret)
            this.orgSecret=orgSecret
            this.indoorLocationCallback=indoorLocationCallback
            if (virtualBeaconCallback != null) {
                this.virtualBeaconCallback=virtualBeaconCallback
            }
        }
        else{
            restartMistSdk()
        }
    }

    fun startMistSdk(){
        if(indoorLocationManager==null){
            Log.d("","IndoorLocationManager Start"+orgSecret)
            indoorLocationManager = IndoorLocationManager.getInstance(contextWeakReference?.get(),orgSecret)
            indoorLocationManager?.setVirtualBeaconCallback(virtualBeaconCallback)
            indoorLocationManager?.start(indoorLocationCallback)
        }
    }

    fun stopMistSdk(){
        if(indoorLocationManager!=null){
            indoorLocationManager?.stop()
        }
    }

    fun destroyMistSdk(){
        if(indoorLocationManager!=null){
            indoorLocationManager?.stop()
            indoorLocationManager=null
        }
    }

    private fun restartMistSdk() {
        if(indoorLocationManager!=null){
            stopMistSdk()
            indoorLocationManager?.setVirtualBeaconCallback((virtualBeaconCallback))
            indoorLocationManager?.start(indoorLocationCallback)
        }
    }
}