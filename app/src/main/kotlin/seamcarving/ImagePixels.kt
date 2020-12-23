package seamcarving

import java.awt.image.BufferedImage

class ImagePixels(width: Int, height: Int, private var pixels: List<Int>) {
    var width: Int = width
        private set
    var height: Int = height
        private set

    companion object {
        fun fromImage(image: BufferedImage): ImagePixels {
            val pixels = MutableList(image.width * image.height) { i ->
                val y = (i / image.width)
                val x = (i % image.width)
                image.getRGB(x, y)
            }

            return ImagePixels(image.width, image.height, pixels)
        }
    }

    fun get(x: Int, y: Int) = pixels[y * width + x]

    fun toImage(): BufferedImage {
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        for (y in 0 until height) {
            for (x in 0 until width) {
                image.setRGB(x, y, get(x, y))
            }
        }
        return image
    }

    fun removeVerticalPixels(seamToRemove: Seam) {
        val pixelIndicesToRemove = pixelIndicesToRemove(seamToRemove)
        pixels = pixels.filterNot { it in pixelIndicesToRemove }
        width--
    }

    fun removeHorizontalPixels(seamToRemove: Seam) {
        val pixelIndicesToRemove = pixelIndicesToRemove(seamToRemove)
        pixels = pixels.filterNot { it in pixelIndicesToRemove }
        height--
    }

    private fun pixelIndicesToRemove(seamToRemove: Seam): Set<Int> {
        return seamToRemove
            .path()
            .map { it.second * width + it.first }
            .toSet()
    }

}