package com.goazi.utility.background.captureImage

interface PictureCapturingListener {
    fun onCaptureDone(pictureData: ByteArray)
}