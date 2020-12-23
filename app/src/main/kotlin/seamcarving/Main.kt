package seamcarving

import java.io.File
import javax.imageio.ImageIO

fun main(args: Array<String>) {
    val fileIn = args[1]
    val fileOut = args[3]
    val reducedWidth = args[5].toInt()
    val reducedHeight = args[7].toInt()
    val image = ImageIO.read(File(fileIn))
    val fileExtension = fileOut.split(".")[1]
    val outputImage = ImageResizer.resize(image, reducedWidth, reducedHeight)
    ImageIO.write(outputImage, fileExtension, File(fileOut))
}
