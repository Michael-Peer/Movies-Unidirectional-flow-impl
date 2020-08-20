package com.example.moviemviimpl.ui

import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.moviemviimpl.R
import com.example.moviemviimpl.utils.UICommunicationListener
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.fragment_maps.*


class MapsFragment : Fragment() {

    private val TAG = "MapFragment"

    /**
     *
     * MainActivity communication
     *
     * **/

    private lateinit var uiCommunicationListener: UICommunicationListener

    /**
     *
     * Location
     *
     * **/
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    /**
     *
     * lAT LNG ATTR
     *
     * **/
    private var lat: Double? = null
    private var lng: Double? = null
    private lateinit var location: LatLng


    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        location = if (lat == null || lng == null) {
            LatLng(-34.0, 151.0)
        } else {
            LatLng(lat!!, lng!!)
        }


        googleMap.addMarker(MarkerOptions().position(location).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO: Move logic to viewModel

        /**
         *
         * Check if user grant us location permissions
         *
         * **/
        checkPermission()

        /**
         *
         * Set click listener to "Request Permission" button, in case the user didn't grant us permission
         *
         * **/
        setOnClickCheckPermissionButton()

    }

    /**
     *
     * Here we check for location permission.
     * If it's true(we've location permissions) - call show map function.
     *
     * If it's false(we don't have location permission) - ask for permission,
     * If we're getting permission we're calling showMap() method from activity,
     * If user denied the permission request, we show him dialog that he must grant us permission in order to use this screen.
     *
     *
     * **/
    private fun checkPermission() {
        if (uiCommunicationListener.isLocationPermissionGranted()) {
            showMap()
        }
    }

    private fun setOnClickCheckPermissionButton() {
        permission_button.setOnClickListener {
            checkPermission()
        }
    }

    //redunadt step, can call directly showMap() from main activity
    fun onPermissionGranted() {
        Log.d(TAG, "onPermissionGranted: YAY!")
        showMap()
    }

    /**
     *
     * showMap() can be called either from t onCreateView(in case the user grant is permission()
     * OR from onPermissionResult in MainActivity(in case user prompt to permissions dialog
     *
     * **/
    private fun showMap() {
        permission_button.visibility = View.GONE
        initLocationProvider()
    }


    /**
     * ref - https://www.geeksforgeeks.org/how-to-get-user-location-in-android/
     *
     * Here we going to use FusedLocationProviderClient.
     * It is actually a location service that combines GPS location and network location to achieve a balance between battery consumption and accuracy.
     * location is used to provide accuracy and network location is used to get location when the user is indoors.
     *
     * In conjunction with FusedLocationProviderClient, LocationRequest public class is used to get the last known location.
     * On this LocationRequest object, set a variety of methods such as set the priority of how accurate the location to be or in how many intervals, request of the location is to be made.
     *
     * If a very high accuracy is required, use PRIORITY_HIGH_ACCURACY as an argument to the setPriority(int) method.
     * For a city level accuracy(low accuracy), use PRIORITY_LOW_POWER.
     * Once the LocationRequest object is ready, set it on the FusedLocationProviderClient object to get the final location.
     *
     * **/

    private fun initLocationProvider() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getUserLastLocation()
    }


    /**
     *
     * This function aim to get the user location.
     * First, check if location is enabled(GPS)
     *
     * If location enabled - we check for the last location, set data to lat & lng vars, and call initMap() function
     *
     * If location disabled - we send intent to System settings screen, where the user can enable GPS //TODO: refresh after user navigate to setting and turn on location. maybe BroadcastReceiver?
     *
     * If location is enabled but location object is null(Can happen in some cases like new device, user doesn't turned on location service etc...you can see detailed explanation about this in location play services docs)
     * We call requestNewLocationData() (explanation on this method see at method)
     *
     * **/
    private fun getUserLastLocation() {
        if (isLocationEnabled()) {
            try {

                fusedLocationClient
                    .lastLocation
                    .addOnCompleteListener { task: Task<Location> ->
                        val location = task.result
                        location?.let {
                            Log.d(TAG, "getUserLastLocation: LAT ${it.latitude}")
                            Log.d(TAG, "getUserLastLocation: LNG ${it.longitude}")
                            setLngLat(it.longitude, it.latitude)
                            initMap()

                        }
                            ?: requestNewLocationData()
                    }
            } catch (e: SecurityException) {
                Toast.makeText(context, "Error Getting Location", Toast.LENGTH_SHORT).show()
                uiCommunicationListener.isLocationPermissionGranted()
            }

        } else {
            Toast
                .makeText(
                    context, "Please turn on"
                            + " your location...",
                    Toast.LENGTH_LONG
                )
                .show()

            val intent = Intent(
                Settings.ACTION_LOCATION_SOURCE_SETTINGS
            )
            startActivity(intent)
        }
    }

    /**
     *
     * Here we check if location(GPS) is turned on or not
     * If not - go to settings screen.
     *
     * **/
    private fun isLocationEnabled(): Boolean {
        val locationManager =
            context?.let {
                it.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            }
        return locationManager?.let {
            (locationManager
                .isProviderEnabled(
                    LocationManager.GPS_PROVIDER
                )
                    || locationManager
                .isProviderEnabled(
                    LocationManager.NETWORK_PROVIDER
                ))
        } ?: false
    }

    /**
     *
     * Set longitude & latitude
     *
     * **/
    private fun setLngLat(longitude: Double, latitude: Double) {
        lng = longitude
        lat = latitude
    }

    private fun requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 5
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        // setting LocationRequest
        // on FusedLocationClient
        fusedLocationClient = LocationServices
            .getFusedLocationProviderClient(requireActivity())
        try {
            fusedLocationClient
                .requestLocationUpdates(
                    mLocationRequest,
                    mLocationCallback,
                    Looper.myLooper()
                )
        } catch (e: SecurityException) {
            Toast.makeText(context, "Error Getting Location", Toast.LENGTH_SHORT).show()
            uiCommunicationListener.isLocationPermissionGranted()
        }

    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(
            locationResult: LocationResult
        ) {
            val mLastLocation = locationResult
                .lastLocation
            Log.d(TAG, "getUserLastLocation: LAT ${mLastLocation.latitude}")
            Log.d(TAG, "getUserLastLocation: LNG ${mLastLocation.longitude}")
            setLngLat(mLastLocation.longitude, mLastLocation.latitude)
        }
    }


    /**
     *
     * initialize Map
     *
     * **/

    private fun initMap() {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            uiCommunicationListener = context as UICommunicationListener
        } catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement UICommunicationListener")
        }
    }

}