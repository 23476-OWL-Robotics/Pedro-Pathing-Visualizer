package com.owlrobotics.pedropathingvisualizer.pedropathing.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public enum Backgrounds {
    IntoTheDeep_DARK;

    public BufferedImage getImage() throws IOException{

        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        return switch (this) {
            case IntoTheDeep_DARK -> ImageIO.read(Objects.requireNonNull(loader.getResource("com/owlrobotics/pedropathingvisualizer/pedropathing/images/field/intothedeep_dark.png")));
        };
    }
}
