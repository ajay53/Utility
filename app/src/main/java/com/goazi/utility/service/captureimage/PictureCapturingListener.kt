package com.goazi.utility.service.captureimage

interface PictureCapturingListener {
    fun onCaptureDone(pictureData: ByteArray)
}