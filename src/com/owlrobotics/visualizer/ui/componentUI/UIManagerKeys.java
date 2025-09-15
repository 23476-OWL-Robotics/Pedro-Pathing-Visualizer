package com.owlrobotics.visualizer.ui.componentUI;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

public class UIManagerKeys {

    public static void main(String[] args) {
        UIDefaults defaults = UIManager.getDefaults();
        Enumeration<Object> keysEnumeration = defaults.keys();
        ArrayList<Object> keysList = Collections.list(keysEnumeration);
        for (Object key : keysList)
        {
            System.out.println(key);
        }
    }
}
