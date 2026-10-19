package com.laralbir.mdghnavigator.feature.reader.ui.component

import java.net.URLDecoder
import java.nio.charset.StandardCharsets

object ImageDataUriParser {

    /**
     * Resolves an image URL or data URI to a model supported by Coil.
     * If the input is a data URI (`data:image/svg+xml;base64,...` or `data:image/svg+xml;utf8,...`),
     * it decodes the payload into a [ByteArray] which Coil can process directly with SvgDecoder.
     * Otherwise, it returns the original [imageUrl].
     */
    fun resolveImageModel(imageUrl: String): Any {
        if (!imageUrl.startsWith("data:", ignoreCase = true)) {
            return imageUrl
        }

        return try {
            val commaIndex = imageUrl.indexOf(',')
            if (commaIndex == -1) return imageUrl

            val metadata = imageUrl.substring(5, commaIndex)
            val rawData = imageUrl.substring(commaIndex + 1)

            if (metadata.contains("base64", ignoreCase = true)) {
                decodeBase64(rawData.trim())
            } else {
                URLDecoder.decode(rawData, StandardCharsets.UTF_8.name())
                    .toByteArray(StandardCharsets.UTF_8)
            }
        } catch (_: Throwable) {
            imageUrl
        }
    }

    /**
     * Determines whether the given [imageUrl] represents an SVG image or vector diagram.
     */
    fun isSvgOrDiagram(imageUrl: String): Boolean {
        return imageUrl.contains("svg", ignoreCase = true) ||
                imageUrl.startsWith("data:image/svg", ignoreCase = true)
    }

    private fun decodeBase64(base64Str: String): ByteArray {
        return try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                java.util.Base64.getDecoder().decode(base64Str)
            } else {
                android.util.Base64.decode(base64Str, android.util.Base64.DEFAULT)
            }
        } catch (_: Throwable) {
            try {
                android.util.Base64.decode(base64Str, android.util.Base64.DEFAULT)
            } catch (_: Throwable) {
                java.util.Base64.getDecoder().decode(base64Str)
            }
        }
    }
}

