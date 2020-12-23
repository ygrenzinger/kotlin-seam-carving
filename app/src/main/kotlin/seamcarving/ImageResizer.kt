package seamcarving

import java.awt.image.BufferedImage

class ImageResizer(private var imagePixels: ImagePixels, private val reducedWidth: Int, private val reducedHeight: Int) {

    fun resize(): ImagePixels {
        widthResizing()
        heightResizing()
        return imagePixels
    }

    private fun <R> timing(name: String, supplier: () -> R): R {
        val startTime = System.currentTimeMillis()
        val r = supplier()
        println("$name executed in ${System.currentTimeMillis() - startTime}")
        return r
    }

    private fun heightResizing() {
        repeat(reducedHeight) {
            // println("Current height ${imagePixels.height}")
            val energies = ImageEnergies.fromPixels(imagePixels)
            val seam = SeamCarving(energies).horizontalSeam()
            seam.let { imagePixels.removeHorizontalPixels(it) }
        }
    }

    private fun widthResizing() {
        repeat(reducedWidth) {
            /*
            println("Current width ${imagePixels.width}")
            val energies = timing("energies") { ImageEnergies.fromPixels(imagePixels) }
            val seam = timing("seamCarving") { SeamCarving(energies).verticalSeam() }
            timing("removingPixels") { seam.let { imagePixels.removeVerticalPixels(it) } }
             */
            val energies = ImageEnergies.fromPixels(imagePixels)
            val seam = SeamCarving(energies).verticalSeam()
            seam.let { imagePixels.removeVerticalPixels(it) }

        }
    }

    companion object {
        fun resize(image: BufferedImage, width: Int, height: Int) =
            ImageResizer(ImagePixels.fromImage(image), width, height)
                .resize()
                .toImage()
    }
}