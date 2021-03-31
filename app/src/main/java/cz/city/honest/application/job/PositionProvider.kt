package cz.city.honest.application.job

import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat.getSystemService

class PositionProvider {

    fun provide(context: Context) {
        val lm = getSystemService(context,LocationManager::class.java)?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
    }

}