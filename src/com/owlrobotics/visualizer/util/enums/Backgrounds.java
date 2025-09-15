package com.owlrobotics.visualizer.util.enums;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public enum Backgrounds {
    PowerPlay_Dark,
    PowerPlay_Light,
    CenterStage_DARK,
    CenterStage_Light,
    IntoTheDeep_DARK,
    IntoTheDeep_LIGHT,
    Decode_DARK,
    Decode_LIGHT;

    public BufferedImage getImage() throws IOException{

        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        return switch (this) {
            case PowerPlay_Dark -> ImageIO.read(Objects.requireNonNull(loader.getResource("com/owlrobotics/visualizer/resources/field/Field2022/powerplay_dark.png")));
            case PowerPlay_Light -> ImageIO.read(Objects.requireNonNull(loader.getResource("com/owlrobotics/visualizer/resources/field/Field2022/powerplay_light.png")));
            case CenterStage_DARK -> ImageIO.read(Objects.requireNonNull(loader.getResource("com/owlrobotics/visualizer/resources/field/Field2023/centerstage_dark.png")));
            case CenterStage_Light -> ImageIO.read(Objects.requireNonNull(loader.getResource("com/owlrobotics/visualizer/resources/field/Field2023/centerstage_light.png")));
            case IntoTheDeep_DARK -> ImageIO.read(Objects.requireNonNull(loader.getResource("com/owlrobotics/visualizer/resources/field/Field2024/intothedeep_dark.png")));
            case IntoTheDeep_LIGHT -> ImageIO.read(Objects.requireNonNull(loader.getResource("com/owlrobotics/visualizer/resources/field/Field2024/intothedeep_light.png")));
            case Decode_DARK -> ImageIO.read(Objects.requireNonNull(loader.getResource("com/owlrobotics/visualizer/resources/field/Field2025/decode_dark.png")));
            case Decode_LIGHT -> ImageIO.read(Objects.requireNonNull(loader.getResource("com/owlrobotics/visualizer/resources/field/Field2025/decode_light.png")));
        };
    }
}
