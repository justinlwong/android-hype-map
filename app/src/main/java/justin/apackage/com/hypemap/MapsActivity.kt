package justin.apackage.com.hypemap

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions



class MapsActivity :
        AppCompatActivity(),
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        AuthListener {
    override fun onMarkerClick(p0: Marker?) = false

    private lateinit var mMap: GoogleMap
    private lateinit var mCurLocation : Location
    private lateinit var mLocationClient : FusedLocationProviderClient
    private lateinit var authDialog : AuthDialog

    companion object {
        private const val PERMISSION_LOCATION_REQUEST_CODE = 1
        private const val TAG = "MapsActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Authentication Dialog
        val metrics = resources.displayMetrics
        val height = metrics.heightPixels
        authDialog = AuthDialog(listener = this, context = this)
        authDialog.setCancelable(true)
        authDialog.show()
        //authDialog.window?.setLayout( ActionBar.LayoutParams.WRAP_CONTENT , (6 * height) / 7)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)

        moveToCurrentLocation()
    }

    private fun addMarkerAtLocation(location: LatLng) {
        val markerOptions = MarkerOptions().position(location)
        mMap.addMarker(markerOptions)
    }

    private fun moveToCurrentLocation() {
        Log.d(TAG, "Getting Permissions")
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "Getting permissions")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_LOCATION_REQUEST_CODE
            )
            return
        } else {
            Log.d(TAG, "Set up current location on map")
            mMap.isMyLocationEnabled = true

            mLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                if (location != null) {
                    mCurLocation = location
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    addMarkerAtLocation(currentLatLng)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18f))
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_LOCATION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    moveToCurrentLocation()
                }
                return
            }
        }
    }

    override fun onCodeReceived(access_token: String?) {
        when (access_token) {
            null -> authDialog.dismiss()
            else -> {
                Log.d(TAG, access_token)
                authDialog.dismiss()
            }
        }
    }
}
