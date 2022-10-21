package cz.honest.city.internal.provider

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import cz.city.honest.dto.Position
import cz.city.honest.service.subject.PositionProvider
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject


class AndroidPositionProvider( val context: Context):
    PositionProvider,
    LocationListener {

    private var latestLocation:PublishSubject<Location> = PublishSubject.create()

    override fun provide(): Observable<Position> =
        Observable.fromCallable{getLocationManager().requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.0f, this); }
            .flatMap {  latestLocation}
            .map { Position(it.longitude,it.latitude) }
        //Observable.just(Position(longitude = 14.4788759, latitude = 50.0483267))

    private fun getLocationManager() = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    override fun onLocationChanged(location: Location) = latestLocation.onNext(location )

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }
}

