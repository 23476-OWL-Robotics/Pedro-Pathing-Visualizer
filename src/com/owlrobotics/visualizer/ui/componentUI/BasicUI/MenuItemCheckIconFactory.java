package com.owlrobotics.visualizer.ui.componentUI.BasicUI;

/*
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

import javax.swing.Icon;
import javax.swing.JMenuItem;

/**
 *
 * @author Igor Kushnirskiy
 */

public interface MenuItemCheckIconFactory {
    Icon getIcon(JMenuItem component);
    boolean isCompatible(Object icon, String prefix);
}
