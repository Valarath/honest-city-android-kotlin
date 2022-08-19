package cz.city.honest.view.map

import android.content.Context
import android.content.Intent
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import cz.city.honest.dto.WatchedSubject
import cz.city.honest.view.detail.SubjectDetailActivity

class MapClickListener(val context: Context) : GoogleMap.OnMarkerClickListener {

    override fun onMarkerClick(marker: Marker): Boolean {
        val intent = Intent(context, SubjectDetailActivity::class.java)
        intent.putExtra(SubjectDetailActivity.WATCHED_SUBJECT, marker.tag as WatchedSubject)
        context.startActivity(intent)
        return true
    }

}