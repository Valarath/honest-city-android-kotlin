package cz.city.honest.application.android.service.provider

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import cz.city.honest.application.model.dto.Position
import cz.city.honest.application.model.service.subject.PositionProvider
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject


class AndroidPositionProvider( val context: Context):
    PositionProvider,
    LocationListener {

    private var latestLocation:PublishSubject<Location> = PublishSubject.create()

    override fun provide(): Observable<Position> =
        Observable.fromCallable{getLocationManager().requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.0f, this); }
            .flatMap {  latestLocation}
            .map { Position(it.longitude,it.latitude) }

    private fun getLocationManager() = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    override fun onLocationChanged(location: Location) = latestLocation.onNext(location )

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

