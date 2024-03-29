package cz.city.honest.application.view

import android.Manifest
import android.accounts.Account
import android.accounts.AccountManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.ContentResolver
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
import cz.city.honest.application.R
import cz.city.honest.application.job.UpdateScheduledJob
import cz.city.honest.application.model.property.ConnectionProperties
import cz.city.honest.application.view.camera.CameraActivity
import cz.city.honest.application.view.camera.CameraFragment
import cz.city.honest.application.view.camera.result.CameraResultActivity
import cz.city.honest.application.view.camera.result.CameraResultFragment
import cz.city.honest.application.view.detail.SubjectDetailActivity
import cz.city.honest.application.view.detail.ui.main.ShowSubjectCostFragment
import cz.city.honest.application.view.detail.ui.main.ShowSubjectSuggestionsFragment
import cz.city.honest.application.view.login.LoginActivity
import cz.city.honest.application.view.map.MapClickListener
import cz.city.honest.application.view.user.UserDetailActivity
import cz.city.honest.application.view.user.ui.main.UserDetailSuggestionsFragment
import cz.city.honest.application.viewmodel.MapViewModel
import cz.city.honest.application.viewmodel.ViewModelModule
import cz.city.honest.application.model.dto.User
import cz.city.honest.application.model.dto.WatchedSubject
import cz.city.honest.application.view.filter.FilterActivity
import cz.city.honest.application.view.filter.FilterFragment
import dagger.Module
import dagger.android.AndroidInjectionModule
import dagger.android.ContributesAndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject
import javax.inject.Scope

@ActivityScope
class MapActivity : DaggerAppCompatActivity(), OnMapReadyCallback, LocationListener {

    private lateinit var map: GoogleMap
    private lateinit var mapViewModel: MapViewModel
    private lateinit var locationManager: LocationManager

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var connectionProperties: ConnectionProperties

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
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CAMERA
                ), 1
            )
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            // scheduleJobs(this)
            return
        }
        //TODO you want these two
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.0f, this)
        scheduleJobs(this)
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
        map = googleMap
        map.setOnMarkerClickListener(MapClickListener(this))
        setLoginButton()
        setFilterButton()
        mapViewModel.watchedSubjects.observe(this, getWatchedSubjectObserver())
        mapViewModel.newExchangePointSuggestions.observe(this, getWatchedSubjectObserver())
        mapViewModel.loggedUser.observe(this, getLoggedUserObserver())
    }


    private fun getLoggedUserObserver(): Observer<User> {
        return Observer {
            setLoginButton(visibility = View.GONE)
            setUserDetailButton(it)
            setCreateSubjectButtonBehaviour()
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

    private fun scheduleJobs(context: Context) =
        context.getSystemService(JobScheduler::class.java)
            .schedule(getJobInfoUpdateBuilder(context))


    private fun getJobInfoUpdateBuilder(context: Context) =
        JobInfo.Builder(0, getUpdateScheduledJobComponentName(context))
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
            //.setPeriodic(connectionProperties.receiveDataAtHours * 60 * 60 * 1000)
            //.setPeriodic(connectionProperties.receiveDataAtHours  * 60 * 1000)
            .setMinimumLatency(1)
            .setOverrideDeadline(1)
            .setRequiresCharging(true)
            .build()

    private fun getUpdateScheduledJobComponentName(context: Context) =
        ComponentName(context, UpdateScheduledJob::class.java)

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

    //TODO uprav tak aby to mohl použít i nepřihlášený uživatel
    private fun setCreateSubjectButtonBehaviour() =
        findViewById<Button>(R.id.add_subject)
            .apply { this.visibility = View.VISIBLE }
            .also {
                it.setOnClickListener {
                    mapViewModel.suggestNewSubject()
                    this.startActivity(Intent(this, CameraActivity::class.java))
                }
            }

    private fun showOnMap(watchedSubject: WatchedSubject): Unit {
        MapPresenterProvider.provide(watchedSubject.javaClass).present(watchedSubject, map, this).also {
            println("suggestions id: " + watchedSubject.suggestions.first().id)
        }
    }

    override fun onLocationChanged(location: Location) {
        map.moveCamera(CameraUpdateFactory.newLatLng(location.toLatLng()))
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        TODO("Not yet implemented")
    }

}

fun Location.toLatLng(): LatLng = LatLng(this.latitude, this.longitude)

@Module(includes = [AndroidInjectionModule::class])
abstract class ActivityModule {

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun mapsActivity(): cz.city.honest.application.view.MapActivity

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun subjectDetailActivity(): SubjectDetailActivity

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun cameraActivity(): CameraActivity

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun filterActivity(): FilterActivity

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun userDetailActivity(): UserDetailActivity

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun loginActivity(): LoginActivity

    @ContributesAndroidInjector
    internal abstract fun showSubjectSuggestions(): ShowSubjectSuggestionsFragment

    @ContributesAndroidInjector
    internal abstract fun cameraFragment(): CameraFragment

    @ContributesAndroidInjector
    internal abstract fun filterFragment(): FilterFragment

    @ContributesAndroidInjector
    internal abstract fun cameraResultFragment(): CameraResultFragment

    @ContributesAndroidInjector
    internal abstract fun cameraResultActivity(): CameraResultActivity

    //TODO to fragment module
    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun showSubjectCostFragment(): ShowSubjectCostFragment

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun userDetailSuggestionsFragment(): UserDetailSuggestionsFragment


}

@Scope
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class ActivityScope