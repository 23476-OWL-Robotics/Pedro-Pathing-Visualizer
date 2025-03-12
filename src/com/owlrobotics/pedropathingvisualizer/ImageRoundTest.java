package com.owlrobotics.pedropathingvisualizer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageRoundTest {

    public static void main(String[] args) throws IOException {
        BufferedImage icon = ImageIO.read(new File("./src/com.owlrobotics.pedropathingvisualizer.pedropathing/images/field/intothedeep.png"));
        BufferedImage rounded = makeRoundedCorner(icon, 20);
        ImageIO.write(rounded, "png", new File("C:\\Users\\maxwe\\IntelliJ\\PedroPathingVisualizer\\src\\com.owlrobotics.pedropathingvisualizer.pedropathing\\images\\field\\intothedeep_rounded.png"));
    }

    public static BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = output.createGraphics();

        // This is what we want, but it only does hard-clipping, i.e. aliasing
        // g2.setClip(new RoundRectangle2D ...)

        // so instead fake soft-clipping by first drawing the desired clip shape
        // in fully opaque white with antialiasing enabled...
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));

        // ... then compositing the image on top,
        // using the white shape from above as alpha source
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, 0, 0, null);

        g2.dispose();

        return output;
    }
}
