package com.goazi.utility.background.captureImage

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.hardware.camera2.*
import android.hardware.camera2.CameraCaptureSession.CaptureCallback
import android.hardware.camera2.params.OutputConfiguration
import android.hardware.camera2.params.SessionConfiguration
import android.media.ImageReader
import android.media.ImageReader.OnImageAvailableListener
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.util.Log
import android.util.Size
import android.util.SparseIntArray
import android.view.Display
import android.view.Surface
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import com.goazi.utility.misc.Constant.Companion.UNLOCK_DIRECTORY
import com.goazi.utility.model.Unlock
import com.goazi.utility.repository.cache.DatabaseHandler
import com.goazi.utility.repository.cache.dao.UnlockDao
import com.google.android.gms.common.util.concurrent.HandlerExecutor
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class CameraService : Service() {

    companion object {
        private const val TAG = "CameraService"
    }

    private lateinit var frontCameraId: String
    private var isCameraClosed: Boolean = false
    private var cameraDevice: CameraDevice? = null
    private var captureBuilder: CaptureRequest.Builder? = null
    private val orientations = SparseIntArray()
    private var cameraHandler: Handler? = null
    private val cameraClosed = false
    private lateinit var currImage: ByteArray
    private lateinit var manager: CameraManager

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: ")

        manager = applicationContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: ")
        startCapturing()
        return START_NOT_STICKY
    }

    init {
        orientations.append(Surface.ROTATION_0, 90)
        orientations.append(Surface.ROTATION_90, 0)
        orientations.append(Surface.ROTATION_180, 270)
        orientations.append(Surface.ROTATION_270, 180)
    }

    private fun startCapturing() {
        try {
//            manager = applicationContext.getSystemService(CAMERA_SERVICE) as CameraManager
            val ht = HandlerThread("Camera Handler Thread")
            ht.start()
            cameraHandler = Handler(ht.looper)
            val cameraIds: Array<String> = manager.cameraIdList as Array<String>
            //___________Front Cam
            for (cameraId in cameraIds) {
                val characteristics: CameraCharacteristics =
                    manager.getCameraCharacteristics(cameraId)
                val cOrientation = characteristics.get(CameraCharacteristics.LENS_FACING)!!
                if (cOrientation == CameraCharacteristics.LENS_FACING_FRONT) {
                    frontCameraId = cameraId
                }
            }
            if (frontCameraId.isNotEmpty()) {
                openCamera()
            } else {
                //No camera detected!
                Log.d(TAG, "startCapturing: No Camera Detected")
            }
        } catch (e: Exception) {
            Log.e(TAG, "startCapturing: e: $e")
        }
    }

    private fun openCamera() {
//        Log.d(TAG, "opening camera " + currentCameraId);
        Log.d(TAG, "opening camera $frontCameraId")
        try {
            if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            ) {
                manager.openCamera(frontCameraId, stateCallback, cameraHandler)
            }
        } catch (e: CameraAccessException) {
            Log.e(
                TAG,
                " exception occurred while opening camera $frontCameraId", e
            )
        }
    }

    private val stateCallback: CameraDevice.StateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            isCameraClosed = false
            Log.d(TAG, "camera " + camera.id + " opened")
            cameraDevice = camera
            Log.i(TAG, "Taking picture from camera " + camera.id)
            //Take the picture after some delay. It may resolve getting a black dark photos.
            takePicture()
            /*cameraHandler?.postDelayed({
                try {
                    takePicture()
                } catch (e: CameraAccessException) {
                    Log.e(
                        TAG,
                        " exception occurred while taking picture from $frontCameraId", e
                    )
                }
            }, 500)*/
        }

        override fun onDisconnected(camera: CameraDevice) {
            Log.d(TAG, " camera " + camera.id + " disconnected")
            if (cameraDevice != null && !isCameraClosed) {
                isCameraClosed = true
                cameraDevice?.close()
            }
        }

        override fun onClosed(camera: CameraDevice) {
            isCameraClosed = true
            Log.d(TAG, "camera " + camera.id + " closed")
        }

        override fun onError(camera: CameraDevice, error: Int) {
            Log.e(TAG, "camera in error, int code $error")
            if (cameraDevice != null && !isCameraClosed) {
                cameraDevice?.close()
            }
        }
    }

    private val takePictureStateCallback: CameraCaptureSession.StateCallback =
        object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(session: CameraCaptureSession) {
                Log.d(TAG, "onConfigured: ")
                try {
                    session.capture(
                        captureBuilder!!.build(),
                        captureListener,
                        cameraHandler
                    )
                } catch (e: CameraAccessException) {
                    Log.e(
                        TAG,
                        " exception occurred while accessing $frontCameraId", e
                    )
                }
            }

            override fun onConfigureFailed(session: CameraCaptureSession) {
                Log.e(TAG, "onConfigureFailed: ")
            }
        }

    @Throws(CameraAccessException::class)
    private fun takePicture() {
        if (null == cameraDevice) {
            Log.e(TAG, "cameraDevice is null")
            return
        }
        val characteristics = manager.getCameraCharacteristics(cameraDevice!!.id)
        var jpegSizes: Array<Size>? = null
        val streamConfigurationMap =
            characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
        if (streamConfigurationMap != null) {
            jpegSizes = streamConfigurationMap.getOutputSizes(ImageFormat.JPEG)
        }
        val jpegSizesNotEmpty = jpegSizes != null && jpegSizes.isNotEmpty()
        val width = if (jpegSizesNotEmpty) jpegSizes!![0].width else 640
        val height = if (jpegSizesNotEmpty) jpegSizes!![0].height else 480
        val reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1)
        val outputSurfaces: MutableList<Surface> = ArrayList()
        outputSurfaces.add(reader.surface)
        captureBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
        captureBuilder!!.addTarget(reader.surface)
        captureBuilder!!.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
        captureBuilder!![CaptureRequest.JPEG_ORIENTATION] = 0
//        captureBuilder!![CaptureRequest.JPEG_ORIENTATION] = getOrientation()
        reader.setOnImageAvailableListener(onImageAvailableListener, cameraHandler)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val outputConfiguration = OutputConfiguration(reader.surface)
            val sessionConfiguration = SessionConfiguration(
                SessionConfiguration.SESSION_REGULAR,
                listOf(outputConfiguration),
                HandlerExecutor(cameraHandler?.looper),
                takePictureStateCallback
            )
            cameraDevice!!.createCaptureSession(sessionConfiguration)
        } else {
            cameraDevice!!.createCaptureSession(
                outputSurfaces,
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        try {
                            session.capture(
                                captureBuilder!!.build(),
                                captureListener,
                                cameraHandler
                            )
                        } catch (e: CameraAccessException) {
                            Log.e(
                                TAG,
                                " exception occurred while accessing $frontCameraId", e
                            )
                        }
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {}
                },
                cameraHandler
            )
        }
    }

    private val onImageAvailableListener = OnImageAvailableListener { imReader: ImageReader ->
        val image = imReader.acquireLatestImage()
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.capacity())

        try {
            Log.d(TAG, ": onImageAvailableListener: bytes: $bytes")
            buffer[bytes]
            processImage(bytes)
            image.close()
        } catch (e: Exception) {
            Log.e(TAG, "onImageAvailableListener: e: $e")
        }
    }

    private val captureListener: CaptureCallback = object : CaptureCallback() {
        override fun onCaptureCompleted(
            session: CameraCaptureSession, request: CaptureRequest,
            result: TotalCaptureResult
        ) {
            try {
                //delaying as this code is executed before onImageAvailableListener thus avoiding null currImage
                cameraHandler?.postDelayed({
                    super.onCaptureCompleted(session, request, result)
                    Log.d(TAG, "onCaptureCompleted: currImage: $currImage")
                    closeCamera()
                }, 500)
                /*GlobalScope.launch { // launch a new coroutine in background and continue
                    delay(500)
                    try {
                        super.onCaptureCompleted(session, request, result)
                        Log.d(TAG, "onCaptureCompleted: currImage: $currImage")
                        capturingListener?.onCaptureDone(currImage)
                        closeCamera()
                    } catch (e: Exception) {
                        Log.e(
                            TAG, " exception occurred while taking picture from $frontCameraId", e
                        )
                    }
                }*/
            } catch (e: UninitializedPropertyAccessException) {
                Log.e(TAG, "onCaptureCompleted: e: $e")

            } catch (e: Exception) {
                Log.e(TAG, "onCaptureCompleted: e: $e")
            }
        }
    }

    private fun processImage(bytes: ByteArray) {
        Log.d(TAG, "processImage: bytes: $bytes")
        //rotate bitmap_____
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        val matrix = Matrix()
        matrix.postRotate(-90f)
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width, bitmap.height, true)
        val rotatedBitmap = Bitmap.createBitmap(
            scaledBitmap,
            0,
            0,
            scaledBitmap.width,
            scaledBitmap.height,
            matrix,
            true
        )

        val stream = ByteArrayOutputStream()
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        currImage = stream.toByteArray()
        //saving bitmap_____
        saveBitmap(rotatedBitmap)
    }

    private fun saveBitmap(bitmap: Bitmap) {
        //saving bitmap in app data
        val contextWrapper = ContextWrapper(applicationContext)
        val directory = contextWrapper.getDir(UNLOCK_DIRECTORY, MODE_PRIVATE)
        val unixTime = System.currentTimeMillis() / 1000L
        val file = File(directory, "$unixTime.jpg")

        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        //insert path in room
        val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        val now: LocalDateTime = LocalDateTime.now()
        val time: String = dtf.format(now)
        val wrongUnlockEvent = Unlock(0, file.absolutePath, time)
        val unlockDao: UnlockDao = DatabaseHandler.getInstance(application)!!.unlockDao()
        unlockDao.insert(wrongUnlockEvent)
    }

    private fun getOrientation(): Int {
        val display: Display? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            applicationContext.display
        } else {
            (applicationContext.getSystemService(Service.WINDOW_SERVICE) as WindowManager).defaultDisplay
        }
        val rotation: Int? = display?.rotation
        return if (rotation != null) {
            orientations.get(rotation)
        } else {
            0
        }
    }

    private fun closeCamera() {
        Log.d(TAG, "closing camera " + cameraDevice!!.id)
        if (null != cameraDevice && !cameraClosed) {
            cameraDevice!!.close()
            cameraDevice = null
        }
        /*if (null != imageReader) {
            imageReader.close()
            imageReader = null
        }*/
    }
}