package com.owlrobotics.pedropathingvisualizer

import java.awt.AlphaComposite
import java.awt.Color
import java.awt.RenderingHints
import java.awt.geom.RoundRectangle2D
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

object ImageRoundTest {
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val icon =
            ImageIO.read(File("./src/com.owlrobotics.pedropathingvisualizer.pedropathing/images/field/intothedeep.png"))
        val rounded = makeRoundedCorner(icon, 20)
        ImageIO.write(
            rounded,
            "png",
            File("C:\\Users\\maxwe\\IntelliJ\\PedroPathingVisualizer\\src\\com.owlrobotics.pedropathingvisualizer.pedropathing\\images\\field\\intothedeep_rounded.png")
        )
    }

    fun makeRoundedCorner(image: BufferedImage, cornerRadius: Int): BufferedImage {
        val w = image.width
        val h = image.height
        val output = BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)

        val g2 = output.createGraphics()

        // This is what we want, but it only does hard-clipping, i.e. aliasing
        // g2.setClip(new RoundRectangle2D ...)

        // so instead fake soft-clipping by first drawing the desired clip shape
        // in fully opaque white with antialiasing enabled...
        g2.composite = AlphaComposite.Src
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2.color = Color.WHITE
        g2.fill(
            RoundRectangle2D.Float(
                0f,
                0f,
                w.toFloat(),
                h.toFloat(),
                cornerRadius.toFloat(),
                cornerRadius.toFloat()
            )
        )

        // ... then compositing the image on top,
        // using the white shape from above as alpha source
        g2.composite = AlphaComposite.SrcAtop
        g2.drawImage(image, 0, 0, null)

        g2.dispose()

        return output
    }
}
