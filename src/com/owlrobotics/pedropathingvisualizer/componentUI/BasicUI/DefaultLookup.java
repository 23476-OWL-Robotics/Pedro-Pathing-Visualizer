package com.owlrobotics.pedropathingvisualizer.componentUI.BasicUI;

import java.awt.Color;
import java.awt.Insets;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import sun.awt.AppContext;

/**
 * DefaultLookup provides a way to customize the lookup done by the
 * UIManager. The default implementation of DefaultLookup forwards
 * the call to the UIManager.
 * <p>
 * <b>WARNING:</b> While this class is public, it should not be treated as
 * public API and its API may change in incompatible ways between dot dot
 * releases and even patch releases. You should not rely on this class even
 * existing.
 *
 * @author Scott Violet
 */
public class DefaultLookup {
    /**
     * Key used to store DefaultLookup for AppContext.
     */
    private static final Object DEFAULT_LOOKUP_KEY = new
            StringBuffer("DefaultLookup");
    /**
     * Thread that last asked for a default.
     */
    private static Thread currentDefaultThread;
    /**
     * DefaultLookup for last thread.
     */
    private static DefaultLookup currentDefaultLookup;

    /**
     * If true, a custom DefaultLookup has been set.
     */
    private static boolean isLookupSet;


    /**
     * Sets the DefaultLookup instance to use for the current
     * <code>AppContext</code>. Null implies the UIManager should be
     * used.
     */
    public static void setDefaultLookup(DefaultLookup lookup) {
        synchronized(DefaultLookup.class) {
            if (!isLookupSet && lookup == null) {
                // Null was passed in, and no one has invoked setDefaultLookup
                // with a non-null value, we don't need to do anything.
                return;
            }
            else if (lookup == null) {
                // null was passed in, but someone has invoked setDefaultLookup
                // with a non-null value, use an instance of DefaultLookup
                // which will fallback to UIManager.
                lookup = new DefaultLookup();
            }
            isLookupSet = true;
            AppContext.getAppContext().put(DEFAULT_LOOKUP_KEY, lookup);
            currentDefaultThread = Thread.currentThread();
            currentDefaultLookup = lookup;
        }
    }

    public static Object get(JComponent c, ComponentUI ui, String key) {
        boolean lookupSet;
        synchronized(DefaultLookup.class) {
            lookupSet = isLookupSet;
        }
        if (!lookupSet) {
            // No one has set a valid DefaultLookup, use UIManager.
            return UIManager.get(key, c.getLocale());
        }
        Thread thisThread = Thread.currentThread();
        DefaultLookup lookup;
        synchronized(DefaultLookup.class) {
            // See if we've already cached the DefaultLookup for this thread,
            // and use it if we have.
            if (thisThread == currentDefaultThread) {
                // It is cached, use it.
                lookup = currentDefaultLookup;
            }
            else {
                // Not cached, get the DefaultLookup to use from the AppContext
                lookup = (DefaultLookup)AppContext.getAppContext().get(
                        DEFAULT_LOOKUP_KEY);
                if (lookup == null) {
                    // Fallback to DefaultLookup, which will redirect to the
                    // UIManager.
                    lookup = new DefaultLookup();
                    AppContext.getAppContext().put(DEFAULT_LOOKUP_KEY, lookup);
                }
                // Cache the values to make the next lookup easier.
                currentDefaultThread = thisThread;
                currentDefaultLookup = lookup;
            }
        }
        return lookup.getDefault(c, ui, key);
    }

    //
    // The following are convenience method that all use getDefault.
    //
    public static int getInt(JComponent c, ComponentUI ui, String key,
                             int defaultValue) {
        Object iValue = get(c, ui, key);

        if (iValue instanceof Number number) {
            return number.intValue();
        }
        return defaultValue;
    }

    public static int getInt(JComponent c, ComponentUI ui, String key) {
        return getInt(c, ui, key, -1);
    }

    public static Insets getInsets(JComponent c, ComponentUI ui, String key,
                                   Insets defaultValue) {
        Object iValue = get(c, ui, key);

        if (iValue instanceof Insets insets) {
            return insets;
        }
        return defaultValue;
    }

    public static Insets getInsets(JComponent c, ComponentUI ui, String key) {
        return getInsets(c, ui, key, null);
    }

    public static boolean getBoolean(JComponent c, ComponentUI ui, String key,
                                     boolean defaultValue) {
        Object iValue = get(c, ui, key);

        if (iValue instanceof Boolean b) {
            return b;
        }
        return defaultValue;
    }

    public static boolean getBoolean(JComponent c, ComponentUI ui, String key) {
        return getBoolean(c, ui, key, false);
    }

    public static Color getColor(JComponent c, ComponentUI ui, String key,
                                 Color defaultValue) {
        Object iValue = get(c, ui, key);

        if (iValue instanceof Color color) {
            return color;
        }
        return defaultValue;
    }

    public static Color getColor(JComponent c, ComponentUI ui, String key) {
        return getColor(c, ui, key, null);
    }

    public static Icon getIcon(JComponent c, ComponentUI ui, String key,
                               Icon defaultValue) {
        Object iValue = get(c, ui, key);
        if (iValue instanceof Icon icon) {
            return icon;
        }
        return defaultValue;
    }

    public static Icon getIcon(JComponent c, ComponentUI ui, String key) {
        return getIcon(c, ui, key, null);
    }

    public static Border getBorder(JComponent c, ComponentUI ui, String key,
                                   Border defaultValue) {
        Object iValue = get(c, ui, key);
        if (iValue instanceof Border border) {
            return border;
        }
        return defaultValue;
    }

    public static Border getBorder(JComponent c, ComponentUI ui, String key) {
        return getBorder(c, ui, key, null);
    }

    public Object getDefault(JComponent c, ComponentUI ui, String key) {
        // basic
        return UIManager.get(key, c.getLocale());
    }
}
