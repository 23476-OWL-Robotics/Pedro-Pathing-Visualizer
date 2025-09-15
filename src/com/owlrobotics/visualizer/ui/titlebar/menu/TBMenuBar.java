package com.owlrobotics.visualizer.ui.titlebar.menu;

import com.owlrobotics.visualizer.ui.titlebar.TBJFrame;

import javax.swing.*;

public class TBMenuBar extends JMenuBar {

    public TBMenuBar(TBJFrame frame) {
        setBackground(frame.getTheme().getTitleBarColorBackground);
        setBorder(BorderFactory.createEmptyBorder());

        this.add(new TBFileMenu(frame));
        //this.add(new TBEditMenu(frame));
        this.add(new TBViewMenu(frame));
        this.add(new TBHelpMenu(frame));
    }
}
