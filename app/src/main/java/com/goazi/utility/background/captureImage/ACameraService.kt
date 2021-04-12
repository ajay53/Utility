package com.goazi.utility.background.captureImage

import android.app.Activity
import android.app.Service
import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Build
import android.util.SparseIntArray
import android.view.Display
import android.view.Surface
import android.view.WindowManager

abstract class ACameraService(private val activity: Activity, val context: Context) {

    val ac = activity
//    val context: Context = activity.applicationContext
    val manager: CameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private val orientations = SparseIntArray()

    init {
        orientations.append(Surface.ROTATION_0, 90)
        orientations.append(Surface.ROTATION_90, 0)
        orientations.append(Surface.ROTATION_180, 270)
        orientations.append(Surface.ROTATION_270, 180)
    }

    fun getOrientation(): Int {
        val display: Display? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.display
        } else {
            (activity.getSystemService(Service.WINDOW_SERVICE) as WindowManager).defaultDisplay
        }
        val rotation: Int? = display?.rotation
        return if (rotation != null) {
            orientations.get(rotation)
        } else {
            0
        }
    }

    abstract fun startCapturing(listener: PictureCapturingListener)
}