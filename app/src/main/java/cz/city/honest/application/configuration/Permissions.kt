package cz.city.honest.application.configuration

import android.Manifest

class Permissions {
    companion object{
        val PERMISSIONS = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_SYNC_SETTINGS,
            Manifest.permission.WRITE_SYNC_SETTINGS,
            "android.permission.AUTHENTICATE_ACCOUNTS",
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.CAMERA
        )
    }
}