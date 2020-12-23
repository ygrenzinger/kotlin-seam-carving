package seamcarving

import kotlin.math.pow
import kotlin.math.sqrt

class ImageEnergies(val width: Int, val height: Int, private val energies: DoubleArray) {
    val maxEnergy = energies.maxOf { it }

    fun getEnergy(x: Int, y: Int) = energies[y * width + x]

    companion object {
        fun fromPixels(pixels: ImagePixels): ImageEnergies {
            return ImageEnergies(pixels.width, pixels.height, energies(pixels))
        }

        private fun energies(pixels: ImagePixels): DoubleArray {
            return (0 until pixels.width * pixels.height)
                .toList()
                .parallelStream()
                .mapToDouble { i ->
                    val y = (i / pixels.width)
                    val x = (i % pixels.width)
                    pixelEnergy(x, y, pixels)
                }.toArray()
        }

        private fun pixelEnergy(x: Int, y: Int, imagePixels: ImagePixels): Double {
            return sqrt(xGradient(x, y, imagePixels) + yGradient(x, y, imagePixels))
        }

        private fun xGradient(x: Int, y: Int, pixels: ImagePixels): Double {
            val leftIndex = before(x, pixels.width)
            val rightIndex = after(x, pixels.width)
            val left = pixels.get(leftIndex, y)
            val right = pixels.get(rightIndex, y)
            return (red(right) - red(left)).toDouble().pow(2) +
                    (green(right) - green(left)).toDouble().pow(2) +
                    (blue(right) - blue(left)).toDouble().pow(2)
        }

        private fun yGradient(x: Int, y: Int, pixels: ImagePixels): Double {
            val top = pixels.get(x, before(y, pixels.height))
            val bottom = pixels.get(x, after(y, pixels.height))
            return (red(bottom) - red(top)).toDouble().pow(2) +
                    (green(bottom) - green(top)).toDouble().pow(2) +
                    (blue(bottom) - blue(top)).toDouble().pow(2)
        }

        private fun before(i: Int, max: Int) = when (i) {
            0 -> 0
            max - 1 -> if (max - 3 < 0) {
                i - 1
            } else {
                max - 3
            }
            else -> i - 1
        }

        private fun after(i: Int, max: Int) = when (i) {
            0 -> if (2 >= max) {
                i + 1
            } else {
                2
            }
            max - 1 -> max - 1
            else -> i + 1
        }

        private fun red(rgb: Int): Int {
            return rgb shr 16 and 0xFF
        }

        private fun green(rgb: Int): Int {
            return rgb shr 8 and 0xFF
        }

        private fun blue(rgb: Int): Int {
            return rgb shr 0 and 0xFF
        }
    }

}