package com.owlrobotics.visualizer.ui.theme;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class DarkTheme extends Theme {

    public DarkTheme() {
        //Top Bar Theme Colors
        getTitleBarColorBackground = new Color(25, 25, 25);
        getTitleBarBorder = new Color(25, 25, 25);
        getCloseButtonHoverBackground = new Color(173, 54, 54);
        getControlButtonHoverBackground = new Color(90, 90, 90);
        getControlButtonBackground = new Color(25, 25, 25);
        getTextColor = new Color(255, 255, 255);

        mainPanelBackgroundColor = new Color(0, 0, 0);
        textColor = new Color(255, 255, 255);

        controlPanelBackgroundColor = new Color(25, 25, 25);

        sliderTrackColor = new Color(50, 50, 50);

        editableTextBackgroundColor = new Color(0, 0, 0);
        uneditableTextBackgroundColor = new Color(25, 25, 25);
        textBoxBorderColor = new Color(50, 50, 50);

        tabbedPaneBorderColor = new Color(55, 55, 55);
        tabbedPaneSelectedTabColor = new Color(55, 55, 55);
        tabbedPaneUnselectedTabColor = new Color(0, 0, 0);

        scrollPanelThumbColor = new Color(75, 75, 75);
        scrollPanelTrackColor = new Color(0, 0, 0);
        scrollPanelButtonPressedColor = new Color(150, 150, 150);
        scrollPanelButtonUnpressedColor = new Color(75, 75, 75);

        getMenuBackgroundColor = new Color(55, 55, 55);
        menuSelectedBackgroundColor = new Color(75, 75, 75);
        getMenuBorderColor = new Color(35, 35, 35);
        menuBorderColor = getMenuBorderColor;

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            iconImage = ImageIO.read(Objects.requireNonNull(loader.getResource("com/owlrobotics/visualizer/resources/icon/pedropathinglogo-darkmode.png")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
