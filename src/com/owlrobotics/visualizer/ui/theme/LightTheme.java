package com.owlrobotics.visualizer.ui.theme;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class LightTheme extends Theme {

    public LightTheme() {
        //Top Bar Theme Colors
        getTitleBarColorBackground = new Color(255, 255, 255);
        getTitleBarBorder = new Color(255, 255, 255);
        getCloseButtonHoverBackground = new Color(173, 54, 54);
        getControlButtonHoverBackground = new Color(200, 200, 200);
        getControlButtonBackground = new Color(255, 255, 255);
        getTextColor = new Color(0, 0, 0);

        mainPanelBackgroundColor = new Color(225, 225, 225);
        textColor = new Color(0, 0, 0);

        controlPanelBackgroundColor = new Color(255, 255, 255);

        sliderTrackColor = new Color(200, 200, 200);

        editableTextBackgroundColor = new Color(225, 225, 225);
        uneditableTextBackgroundColor = new Color(255, 255, 255);
        textBoxBorderColor = new Color(150, 150, 150);

        tabbedPaneBorderColor = new Color(225, 225, 225);
        tabbedPaneSelectedTabColor = new Color(225, 225, 225);
        tabbedPaneUnselectedTabColor = new Color(200, 200, 200);

        scrollPanelThumbColor = new Color(175, 175, 175);
        scrollPanelTrackColor = new Color(225, 225, 225);
        scrollPanelButtonPressedColor = new Color(200, 200, 200);
        scrollPanelButtonUnpressedColor = new Color(175, 175, 175);

        getMenuBackgroundColor = new Color(255, 255, 255);
        menuSelectedBackgroundColor = new Color(225, 225, 225);
        getMenuBorderColor = new Color(175, 175, 175);
        menuBorderColor = getMenuBorderColor;

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            iconImage = ImageIO.read(Objects.requireNonNull(loader.getResource("com/owlrobotics/visualizer/resources/icon/pedropathinglogo-lightmode.png")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
