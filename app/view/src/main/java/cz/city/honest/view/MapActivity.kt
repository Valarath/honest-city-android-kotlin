package cz.city.honest.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import cz.city.honest.dto.User
import cz.city.honest.dto.WatchedSubject
import cz.city.honest.view.camera.CameraActivity
import cz.city.honest.view.filter.FilterActivity
import cz.city.honest.view.login.LoginActivity
import cz.city.honest.view.map.MapClickListener
import cz.city.honest.view.user.UserDetailActivity
import cz.city.honest.viewmodel.MapViewModel
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

@ActivityScope
class MapActivity : DaggerAppCompatActivity(), LocationListener, OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var mapViewModel: MapViewModel
    private lateinit var locationManager: LocationManager

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        supportActionBar!!.hide()
        mapViewModel = ViewModelProvider(this, viewModelFactory).get(MapViewModel::class.java)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1200000, 15.0f, this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        setMapProperty(googleMap)
        setLoginButton()
        setFilterButton()
        setAnalyzeSubjectButtonBehaviour()
        mapViewModel.watchedSubjects.observe(this, getWatchedSubjectObserver())
        mapViewModel.newExchangePointSuggestions.observe(this, getWatchedSubjectObserver())
        mapViewModel.loggedUser.observe(this, getLoggedUserObserver())
    }

    private fun setMapProperty(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMarkerClickListener(MapClickListener(this))
        map.isMyLocationEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = false
    }

    private fun getLoggedUserObserver(): Observer<User> {
        return Observer {
            setLoginButton(visibility = View.GONE)
            setUserDetailButton(it)
        }
    }

    private fun setLoginButton(visibility: Int = View.VISIBLE) =
        findViewById<Button>(R.id.login_map_button)
            .apply { this.visibility = visibility }
            .apply { this.setOnClickListener { setLoginButtonListener() } }

    private fun setLoginButtonListener() =
        this.startActivity(Intent(this, LoginActivity::class.java))


    private fun getWatchedSubjectObserver(): Observer<WatchedSubject> {
        return Observer {
             showOnMap(it)
        }
    }

    private fun setUserDetailButton(user: User) =
        findViewById<Button>(R.id.user_detail)
            .also {
                it.visibility = View.VISIBLE
                it.text = user.score.toString()
                it.setOnClickListener { setUserDetailButtonListener() }
            }

    private fun setFilterButton() =
        findViewById<Button>(R.id.filter)
            .also { it.setOnClickListener { this.startActivity(Intent(this, FilterActivity::class.java))} }

    private fun setUserDetailButtonListener() =
        this.startActivity(Intent(this, UserDetailActivity::class.java))

    private fun setAnalyzeSubjectButtonBehaviour() =
        findViewById<Button>(R.id.add_subject)
            .also {
                it.setOnClickListener {
                    this.startActivity(Intent(this, CameraActivity::class.java))
                }
            }

    private fun showOnMap(watchedSubject: WatchedSubject): Unit {
        MapPresenterProvider.provide(watchedSubject.javaClass).present(watchedSubject, map, this)
    }

    override fun onLocationChanged(location: Location) {
        map.moveCamera(CameraUpdateFactory.newLatLng(location.toLatLng()))
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

}

fun Location.toLatLng(): LatLng = LatLng(this.latitude, this.longitude)