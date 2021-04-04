package cz.city.honest.application.android.service.provider

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import cz.city.honest.application.model.service.PositionProvider
import cz.city.honest.mobile.model.dto.Position
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject


class AndroidPositionProvider( val context: Context):PositionProvider,
    LocationListener {

    private val latestLocation = PublishSubject.create<Location>()

    override fun provide(): Observable<Position> =
        Observable.just(isLocationRetrievalPermitted())
            .filter {it}
            .map { getLocationManager().requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.0f, this) }
            .flatMap {  latestLocation}
            .map { Position(it.longitude,it.latitude) }

    private fun getLocationManager() = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private fun isLocationRetrievalPermitted() = (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    )

    override fun onLocationChanged(location: Location?) = latestLocation.onNext(location)

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun onProviderEnabled(provider: String?) {
        TODO("Not yet implemented")
    }

    override fun onProviderDisabled(provider: String?) {
        TODO("Not yet implemented")
    }
}

