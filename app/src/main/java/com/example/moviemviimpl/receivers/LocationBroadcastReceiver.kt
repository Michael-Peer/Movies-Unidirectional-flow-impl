package com.example.moviemviimpl.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.util.Log
import android.widget.Toast

class LocationBroadcastReceiver : BroadcastReceiver() {

    private val TAG = "LocationBroadcastRecei"

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive: ")
        Log.d(TAG, "onReceive: ${intent!!.action}")
        if (LocationManager.PROVIDERS_CHANGED_ACTION == intent.action) {
            Log.d(TAG, "onReceive: CALLED")
            Toast.makeText(context, "onReceive: CALLED", Toast.LENGTH_LONG).show()
        }
    }
}