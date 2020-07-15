package cz.city.honest.application.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import cz.city.honest.application.R
import cz.city.honest.mobile.model.dto.ExchangePoint
import cz.city.honest.mobile.model.dto.HonestyStatus
import cz.city.honest.mobile.model.dto.Position
import cz.city.honest.mobile.model.dto.WatchedSubject
import java.io.ByteArrayOutputStream


sealed class MapPresenter<Subject : WatchedSubject> {
    abstract fun present(
        subject: Subject,
        map: GoogleMap,
        context: MapActivity
    )

    companion object {
        val HONESTY_STATUS_COLOR_MAP = mapOf(
            HonestyStatus.DISHONEST to Color.RED,
            HonestyStatus.BE_CAUTION to Color.rgb(255, 165, 0),
            HonestyStatus.HONEST_WITH_RESERVE to Color.YELLOW,
            HonestyStatus.HONEST to Color.GREEN,
            HonestyStatus.UNKNOWN to Color.WHITE
        )
    }
}

class ExchangePointMapPresenter : MapPresenter<ExchangePoint>() {

    override fun present(
        subject: ExchangePoint,
        map: GoogleMap,
        context: MapActivity
    ) {
        val markerOptions = getMarkerOptions(context, subject)
        val marker = map.addMarker(markerOptions)
        marker.tag = subject
    }

    private fun getMarkerOptions(
        context: MapActivity,
        subject: ExchangePoint
    ): MarkerOptions = MarkerOptions()
        .icon(getMarkerIcon(context, subject))
        .position(subject.position.toLatLng())

    private fun getMarkerIcon(
        context: MapActivity,
        subject: ExchangePoint
    ): BitmapDescriptor = BitmapDescriptorFactory.fromBitmap(
        getIcon(
            toByteArray(context),
            subject.honestyStatus
        )
    )


    private fun toByteArray(context: MapActivity): ByteArray {
        val drawable: Drawable = context.resources.getDrawable(R.drawable.mock, null)
        val bitmap = (drawable as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }

    private fun getIcon(image: ByteArray, honestyStatus: HonestyStatus): Bitmap {
        val icon = BitmapFactory.decodeByteArray(image, 0, image.size)
        val iconWithBorder =
            Bitmap.createBitmap(icon.width + 2 * 2, icon.height + 2 * 2, icon.config)
        val canvas = Canvas(iconWithBorder)
        canvas.drawColor(HONESTY_STATUS_COLOR_MAP[honestyStatus]!!)
        canvas.drawBitmap(icon, 2.0f, 2.0f, null)
        return iconWithBorder
    }

}


fun Position.toLatLng(): LatLng = LatLng(this.latitude, this.longitude)