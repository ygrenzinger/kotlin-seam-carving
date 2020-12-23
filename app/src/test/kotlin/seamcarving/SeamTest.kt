package seamcarving

import org.junit.Test
import kotlin.random.Random
import kotlin.test.assertEquals

class SeamTest {
    @Test
    fun should_seam_carve_random_size_image() {
        seamCarve(Random.nextInt(2,100), Random.nextInt(2,100))
    }

    @Test
    fun should_seam_carve_large_image() {
//        repeat(5) {
//            resizeImage(1000, 1000)
//        }
        seamCarve(1200, 1200)
    }

    private fun seamCarve(width: Int, height: Int) {
        println("Resizing image of width $width and height $height")
        val energies = (0 until width * height).map {
            Random.nextInt(10, 99).toDouble()
        }.toDoubleArray()
        val randomPath: List<Pair<Int, Int>> = buildRandomPath(width, height)

        randomPath.forEach {
            val (x, y) = it
            val i = y * width + x
            energies[i] = 0.0
        }

        val seamCarving = SeamCarving(ImageEnergies(width, height, energies))
        val startTime = System.currentTimeMillis()
        val computedPath = seamCarving.verticalSeam().path()
        println("Elapsed time ${System.currentTimeMillis() - startTime}")
        assertEquals(randomPath, computedPath)
    }

    private fun buildRandomPath(width: Int, height: Int): List<Pair<Int, Int>> {
        val path = mutableListOf<Pair<Int, Int>>()
        var y = 0
        var x = Random.nextInt(0, width)
        while (y < height) {
            path.add(Pair(x, y))
            x = when (x) {
                0 -> Random.nextInt(x, x + 2)
                width - 1 -> Random.nextInt(x - 1, x + 1)
                else -> Random.nextInt(x - 1, x + 2)
            }
            y++
        }
        return path
    }

}