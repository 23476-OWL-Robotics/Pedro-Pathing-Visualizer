package com.owlrobotics.pedropathingvisualizer.componentUI.BasicUI;

import sun.awt.SunToolkit;

import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.awt.event.ActionEvent;

public class BasicLookAndFeel {

    public static Object getUIOfType(ComponentUI ui, Class<?> klass) {
        if (klass.isInstance(ui)) {
            return ui;
        }
        return null;
    }

    public static int getFocusAcceleratorKeyMask() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        if (tk instanceof SunToolkit) {
            return ((SunToolkit)tk).getFocusAcceleratorKeyMask();
        }
        return ActionEvent.ALT_MASK;
    }
}
