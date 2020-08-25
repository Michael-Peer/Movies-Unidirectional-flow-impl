package com.example.moviemviimpl.ui

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.moviemviimpl.R
import com.example.moviemviimpl.api.DummyRetrofit
import com.example.moviemviimpl.receivers.LocationBroadcastReceiver
import com.example.moviemviimpl.utils.BitmapHelper
import com.example.moviemviimpl.utils.Constants
import com.example.moviemviimpl.utils.UICommunicationListener
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.google.maps.android.PolyUtil
import kotlinx.android.synthetic.main.fragment_maps.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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

    private lateinit var map: GoogleMap

    /**
     *
     * lAT LNG ATTR
     *
     * **/
    private var lat: Double? = null
    private var lng: Double? = null
    private lateinit var originLocation: LatLng
    private lateinit var destLocation: LatLng

    var polyline: MutableList<LatLng>? = null

    /**
     *
     * Maps Arguments we're getting from DetailFragment.
     * Contains poster image url
     *
     * **/
    private val args: MapsFragmentArgs by navArgs()

    /**
     *
     * BroadcastReceivers
     *
     * **/
    private val locationBroadcastReceiver: LocationBroadcastReceiver = LocationBroadcastReceiver()


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * In this case, we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to
     * install it inside the SupportMapFragment. This method will only be triggered once the
     * user has installed Google Play services and returned to the app.
     */
    private val callback = OnMapReadyCallback { googleMap ->

        /**
         *
         * Set googleMap to global variable
         *
         * **/
        map = googleMap


        /**
         *
         * Set origin location of the user
         *
         * **/
        originLocation = if (lat == null || lng == null) {
            LatLng(-34.0, 151.0) //DEFAULT
        } else {
            LatLng(lat!!, lng!!)
        }

        /**
         *
         * Set destination location of the user
         * *(DUMMY POINTS)*
         * **/
        destLocation = LatLng(lat!! - 1, lng!! + 1)

        /**
         *
         * Get polyline points
         *
         * **/
        getDirectionData(origin = originLocation, dest = destLocation)


        /**
         *
         * Set markers on map
         *
         * **/
        setMapMarkers()

        /**
         *
         * Set camera data on map
         *
         * **/
        setMapCamera()

        /**
         *
         * Set poster image on dest location
         *
         * **/
        setOverlayImage()

        /**
         *
         * Set circle on origin location
         *
         * **/
        setCircle()


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
//                            getUrlPath(
//                                LatLng(it.latitude, it.longitude),
//                                LatLng(it.latitude - 1, it.longitude + 1)
//                            )
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

    /**
     *
     * In this function, we're getting the data from Direction API.
     * In this screen we only care about the points so we can draw polyline TODO: Remove all the Direction models except from the relevant data
     *
     * **/
    private fun getDirectionData(origin: LatLng, dest: LatLng) {
        val originStr = "${origin.latitude},${origin.longitude}"
        val destStr = "${dest.latitude},${dest.longitude}"
        val sensor = "false";
        val mode = "driving"

        val param = "$originStr&$destStr&$sensor&$mode"
        val outputFormat = "json"
        val apikey = Constants.API_KEY

//        return "\"https://maps.googleapis.com/maps/api/directions/$outputFormat?$param$apikey"


        CoroutineScope(Dispatchers.IO).launch {
            val res = DummyRetrofit.apiService.getRoute(
                output = outputFormat,
                apiKey = "AIzaSyBalRSBvHeMXcPTffLF3xVHGPYkeWF-SE0",
                origin = originStr,
                dest = destStr,
                sensor = sensor,
                mode = mode
            )


            /**
             *
             * Get the polyline points of the first route
             *
             * **/
            res.routes?.let {

                polyline = PolyUtil.decode(it[0].overviewPolyline?.points)
                Log.d(TAG, "DEBUG API $polyline")
            }

            /**
             *
             * ref: https://stackoverflow.com/questions/59491707/how-to-wait-for-end-of-a-coroutine
             *
             * Crucial concept about coroutine OR: Why do I call setPolylineOnMap here and not in map callback
             *
             * The main thing to understand here is that code within coroutine is by default executed sequentially.
             *  I.e. coroutine is executed asynchronously in relation to "sibling" code, but code within coroutine executes synchronously by default.
             *
             *  So to summarize - if the functions are inside the coroutine it will be execute synchronously: FunctionB will wait to FunctionA to end and then, and only then, FunctionA will execute
             *  and if function A is inside coroutine, And functionB is outside coroutine it will be execute asynchronously: FunctionB WON'T wait till functionA finish his job
             *
             *
             * **/
            withContext(Dispatchers.Main) {
                /**
                 *
                 * Draw polyline points
                 *
                 * **/
                setPolylineOnMap()
            }
        }
    }

    private fun setMapCamera() {
        map.moveCamera(CameraUpdateFactory.newLatLng(originLocation))

        val builder = LatLngBounds.Builder()
        builder.include(originLocation)
        builder.include(destLocation)
        val bounds = builder.build()
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100), 2000, null)
    }

    /**
     *
     * Circle are like polygons, but we can specify radius.
     *
     * For example: if we set circle with 1000m radius on the origin LatLng, we'll get 1000m radius circle drawn on origin LatLng
     *
     *
     * **/
    private fun setCircle() {
        val circleOptions = CircleOptions()
            .center(originLocation)
            .radius(10000.0)
            .fillColor(R.color.red)

        val circle = map.addCircle(circleOptions)
    }

    private fun setOverlayImage() {
        val currentUrl = "https://image.tmdb.org/t/p/w400${args.moviePostPathUrl}"

        val overlaySize = 15000f

        lifecycle.coroutineScope.launch(Dispatchers.IO) {
            val androidOverlay = GroundOverlayOptions()
                .image(
                    BitmapDescriptorFactory.fromBitmap(
                        Glide.with(requireContext())
                            .asBitmap()
                            .load(currentUrl)
                            .submit()
                            .get()
                    )
                )
                .position(destLocation, overlaySize)
            withContext(Dispatchers.Main) {
                map.addGroundOverlay(androidOverlay)

            }
        }
    }

    private fun setMapMarkers() {

        val movieIcon by lazy {
            val color = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
            BitmapHelper.vectorToBitmap(
                requireContext(),
                R.drawable.ic_baseline_local_movies_24,
                color
            )
        }

        map.addMarker(
            MarkerOptions().position(originLocation).title("Marker in Sydney")
                .snippet("!!!ORIGON!!!")
        )
        map.addMarker(
            MarkerOptions().position(destLocation).title("Marker in Sydney").snippet("!!!DEST!!!")
                .icon(movieIcon)
//                .alpha(0.5f)


        )
    }

    private fun setPolylineOnMap() {
        Log.d(TAG, "DEBUG setPolylineOnMap $polyline")

        val poly1 = map.addPolyline(
            PolylineOptions()
                .color(Color.BLACK)
                .width(15f)
                .addAll(polyline)
        )

        val poly2 = map.addPolyline(
            PolylineOptions()
                .color(Color.GREEN)
                .width(15f)
        )

        /**
         *
         * We will draw the black line first, which is represent the actual route between dest.
         * Then, we'll draw the green line in a duration of 4 sec(4000ms).
         *
         * We'll use LinearInterpolator that will give us percentages between 0 to 100 every 4 sec.
         * So after every 4 sec, we'll draw some part of the line with the help of interpolator value,
         * And some index from the points of the black line and this seems like we're animation the green line over the black line
         *
         * **/
//        val polylineAnimator = MapAnimationUtils.polylineAnimator()
//        polylineAnimator.addUpdateListener { valueAnimator ->
//            val percentValue = (valueAnimator.animatedValue as Int)
//            val index = (poly1?.points!!.size) * (percentValue / 100.0f).toInt()
//            poly2?.points = poly1.points!!.subList(0, index)
//        }
//        polylineAnimator.start()
//
//        updateCarLocation(destLocation)

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        val intentFilter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        context.registerReceiver(locationBroadcastReceiver, intentFilter)
        try {
            uiCommunicationListener = context as UICommunicationListener
        } catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement UICommunicationListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        context?.unregisterReceiver(locationBroadcastReceiver)

    }

    /**
     *
     * Register receivers
     *
     * **/
    override fun onStart() {
        super.onStart()


        /**
         * For Activity
         * **/
//        registerReceiver(gpsReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));

    }

    /**
     *
     * Unregister receivers
     *
     * **/
    override fun onStop() {
        super.onStop()

    }


}