package com.mobiledev.randomuserapp.utils.qrcode

import android.content.Context
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.ui.geometry.Rect
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.mobiledev.randomuserapp.data.db.AppDatabase
import com.mobiledev.randomuserapp.data.db.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BarcodeAnalyzer(
    private val context: Context,
    private val onUserFound: (User, Rect) -> Unit
) : ImageAnalysis.Analyzer {
    private val scanner = BarcodeScanning.getClient()

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if(mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            scanner.process(image)
                .addOnSuccessListener {barcodes ->
                    for(barcode in barcodes) {
                        val userId = barcode.rawValue?.toIntOrNull()
                        if(userId != null) {
                            CoroutineScope(Dispatchers.IO).launch {
                                val user = AppDatabase.getInstance(context).userDao().getUSerById(userId)
                                if(user != null) {
                                    val box = barcode.boundingBox
                                    if(box != null) {
                                        val composeRect = Rect(
                                            left = box.left.toFloat(),
                                            top = box.top.toFloat(),
                                            right = box.right.toFloat(),
                                            bottom = box.bottom.toFloat()
                                        )
                                        withContext(Dispatchers.Main) {
                                            onUserFound(user, composeRect)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                .addOnFailureListener {
                        e -> e.printStackTrace()
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }

    }
}