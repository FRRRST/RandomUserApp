package com.mobiledev.randomuserapp.utils.qrcode

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import java.nio.charset.StandardCharsets
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set

object QRGenerator {
    fun generateQRCode(content: String, size: Int = 512): Bitmap? {
        return try {
            val hints = mapOf(
                EncodeHintType.CHARACTER_SET to StandardCharsets.UTF_8.name()
            )
            val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
            val bitmap = createBitmap(size, size, Bitmap.Config.RGB_565)

            for(x in 0 until size) {
                for(y in 0 until size) {
                    bitmap[x, y] =
                        if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
                }
            }
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}