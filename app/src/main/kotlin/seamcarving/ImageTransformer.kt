package seamcarving

import java.awt.Color
import java.awt.image.BufferedImage

class ImageTransformer(
    pixels: ImagePixels
) {
    private val imageEnergy = ImageEnergies.fromPixels(pixels)

    companion object {
        fun imageToPixels(image: BufferedImage) = Array(image.height) { y ->
            Array(image.width) { x ->
                Color(image.getRGB(x, y))
            }
        }

        private fun fromImage(image: BufferedImage): ImageTransformer {
            return ImageTransformer(ImagePixels.fromImage(image))
        }

        fun colorizeVerticalSeam(image: BufferedImage): BufferedImage {
            return fromImage(image).colorizeVerticalSeam(image)
        }

        fun colorizeHorizontalSeam(image: BufferedImage): BufferedImage {
            return fromImage(image).colorizeHorizontalSeam(image)
        }

        fun energyImage(image: BufferedImage): BufferedImage {
            return fromImage(image).applyIntensityTo(image)
        }

        fun negateImage(image: BufferedImage): BufferedImage {
            return fromImage(image).negateImage(image)
        }
    }

    private fun colorizeVerticalSeam(image: BufferedImage): BufferedImage {
        val seamCarving = SeamCarving(imageEnergy)
        seamCarving.verticalSeam().let { seam ->
            seam.path().forEach {
                val (x, y) = it
                image.setRGB(x, y, Color(255, 0, 0).rgb)
            }
        }
        return image
    }

    private fun colorizeHorizontalSeam(image: BufferedImage): BufferedImage {
        val seamCarving = SeamCarving(imageEnergy)
        seamCarving.horizontalSeam().let { seam ->
            seam.path().forEach {
                val (x, y) = it
                image.setRGB(x, y, Color(255, 0, 0).rgb)
            }
        }
        return image
    }

    private fun negatePixel(pixelValue: Int): Int {
        val color = Color(pixelValue)
        val negate = Color(255 - color.red, 255 - color.green, 255 - color.blue, color.alpha)
        return negate.rgb
    }

    fun negateImage(image: BufferedImage): BufferedImage {
        for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                val pixel = image.getRGB(x, y)
                image.setRGB(x, y, negatePixel(pixel))
            }
        }
        return image
    }


    fun applyIntensityTo(image: BufferedImage): BufferedImage {
        val energy = ImageEnergies.fromPixels(ImagePixels.fromImage(image))
        val maxEnergy = energy.maxEnergy
        for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                val value = (255.0 * energy.getEnergy(x, y) / maxEnergy).toInt()
                image.setRGB(x, y, Color(value, value, value).rgb)
            }
        }
        return image
    }
}
