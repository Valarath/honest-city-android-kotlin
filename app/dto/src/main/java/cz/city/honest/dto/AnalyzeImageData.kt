package cz.city.honest.dto

import android.media.Image

data class AnalyzeImageData(val rotationDegrees:Int, val height:Int, val width:Int, val format: Int, val image: Image)