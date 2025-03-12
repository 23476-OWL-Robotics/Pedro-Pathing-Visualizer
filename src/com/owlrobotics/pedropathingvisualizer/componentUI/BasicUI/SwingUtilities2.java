package com.owlrobotics.pedropathingvisualizer.componentUI.BasicUI;

import sun.awt.AppContext;
import sun.font.FontUtilities;
import sun.print.ProxyPrintGraphics;
import sun.swing.PrintColorUIResource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.font.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.print.PrinterGraphics;
import java.beans.PropertyChangeEvent;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.BreakIterator;
import java.text.CharacterIterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.awt.RenderingHints.*;

public class SwingUtilities2 {

    //all access to  charsBuffer is to be synchronized on charsBufferLock
    private static final int CHAR_BUFFER_SIZE = 100;
    private static final Object charsBufferLock = new Object();
    private static char[] charsBuffer = new char[CHAR_BUFFER_SIZE];

    public static final FontRenderContext DEFAULT_FRC =
            new FontRenderContext(null, false, false);

    public static boolean isScaleChanged(final PropertyChangeEvent ev) {
        return isScaleChanged(ev.getPropertyName(), ev.getOldValue(),
                ev.getNewValue());
    }

    /**
     * Returns whether or not the scale used by {@code GraphicsConfiguration}
     * was changed.
     *
     * @param  name the name of the property
     * @param  oldValue the old value of the property
     * @param  newValue the new value of the property
     * @return whether or not the scale was changed
     * @since 11
     */
    public static boolean isScaleChanged(final String name,
                                         final Object oldValue,
                                         final Object newValue) {
        if (oldValue == newValue || !"graphicsConfiguration".equals(name)) {
            return false;
        }
        var newGC = (GraphicsConfiguration) oldValue;
        var oldGC = (GraphicsConfiguration) newValue;
        var newTx = newGC != null ? newGC.getDefaultTransform() : null;
        var oldTx = oldGC != null ? oldGC.getDefaultTransform() : null;
        return !Objects.equals(newTx, oldTx);
    }

    public static int setAltGraphMask(int modifier) {
        return (modifier | InputEvent.ALT_GRAPH_DOWN_MASK);
    }

    /**
     * Returns the FontMetrics for the current Font of the passed
     * in Graphics.  This method is used when a Graphics
     * is available, typically when painting.  If a Graphics is not
     * available the JComponent method of the same name should be used.
     * <p>
     * Callers should pass in a non-null JComponent, the exception
     * to this is if a JComponent is not readily available at the time of
     * painting.
     * <p>
     * This does not necessarily return the FontMetrics from the
     * Graphics.
     *
     * @param c JComponent requesting FontMetrics, may be null
     * @param g Graphics Graphics
     */
    public static FontMetrics getFontMetrics(JComponent c, Graphics g) {
        return getFontMetrics(c, g, g.getFont());
    }


    /**
     * Returns the FontMetrics for the specified Font.
     * This method is used when a Graphics is available, typically when
     * painting.  If a Graphics is not available the JComponent method of
     * the same name should be used.
     * <p>
     * Callers should pass in a non-null JComponent, the exception
     * to this is if a JComponent is not readily available at the time of
     * painting.
     * <p>
     * This does not necessarily return the FontMetrics from the
     * Graphics.
     *
     * @param c JComponent requesting FontMetrics, may be null
     * @param g Graphics
     * @param font Font to get FontMetrics for
     */
    @SuppressWarnings("deprecation")
    public static FontMetrics getFontMetrics(JComponent c, Graphics g,
                                             Font font) {
        if (c != null) {
            // Note: We assume that we're using the FontMetrics
            // from the widget to lay out text, otherwise we can get
            // mismatches when printing.
            return c.getFontMetrics(font);
        }
        return Toolkit.getDefaultToolkit().getFontMetrics(font);
    }

    public static String clipStringIfNecessary(JComponent c, FontMetrics fm,
                                               String string,
                                               int availTextWidth) {
        if (string == null || string.isEmpty())  {
            return "";
        }
        int textWidth = stringWidth(c, fm, string);
        if (textWidth > availTextWidth) {
            return SwingUtilities2.clipString(c, fm, string, availTextWidth);
        }
        return string;
    }

    private static int syncCharsBuffer(String s) {
        int length = s.length();
        if ((charsBuffer == null) || (charsBuffer.length < length)) {
            charsBuffer = s.toCharArray();
        } else {
            s.getChars(0, length, charsBuffer, 0);
        }
        return length;
    }

    public static final boolean isComplexLayout(char[] text, int start, int limit) {
        return FontUtilities.isComplexText(text, start, limit);
    }


    /**
     * Clips the passed in String to the space provided.  NOTE: this assumes
     * the string does not fit in the available space.
     *
     * @param c JComponent that will display the string, may be null
     * @param fm FontMetrics used to measure the String width
     * @param string String to display
     * @param availTextWidth Amount of space that the string can be drawn in
     * @return Clipped string that can fit in the provided space.
     */
    public static String clipString(JComponent c, FontMetrics fm,
                                    String string, int availTextWidth) {
        // c may be null here.
        String clipString = "...";
        availTextWidth -= sun.swing.SwingUtilities2.stringWidth(c, fm, clipString);
        if (availTextWidth <= 0) {
            //can not fit any characters
            return clipString;
        }

        boolean needsTextLayout;
        synchronized (charsBufferLock) {
            int stringLength = syncCharsBuffer(string);
            needsTextLayout =
                    isComplexLayout(charsBuffer, 0, stringLength);
            if (!needsTextLayout) {
                int width = 0;
                for (int nChars = 0; nChars < stringLength; nChars++) {
                    width += fm.charWidth(charsBuffer[nChars]);
                    if (width > availTextWidth) {
                        string = string.substring(0, nChars);
                        break;
                    }
                }
            }
        }
        if (needsTextLayout) {
            AttributedString aString = new AttributedString(string);
            if (c != null) {
                aString.addAttribute(TextAttribute.NUMERIC_SHAPING,
                        c.getClientProperty(TextAttribute.NUMERIC_SHAPING));
            }
            LineBreakMeasurer measurer = new LineBreakMeasurer(
                    aString.getIterator(), BreakIterator.getCharacterInstance(),
                    getFontRenderContext(c, fm));
            string = string.substring(0, measurer.nextOffset(availTextWidth));

        }
        return string + clipString;
    }

    public static FontRenderContext getFontRenderContext(Component c) {
        assert c != null;
        if (c == null) {
            return DEFAULT_FRC;
        } else {
            return c.getFontMetrics(c.getFont()).getFontRenderContext();
        }
    }

    /**
     * A convenience method to get FontRenderContext.
     * Returns the FontRenderContext for the passed in FontMetrics or
     * for the passed in Component if FontMetrics is null
     */
    private static FontRenderContext getFontRenderContext(Component c, FontMetrics fm) {
        assert fm != null || c!= null;
        return (fm != null) ? fm.getFontRenderContext()
                : getFontRenderContext(c);
    }

    public static void drawStringUnderlineCharAt(JComponent c,Graphics g,
                                                 String text, int underlinedIndex, int x, int y) {
        drawStringUnderlineCharAt(c, g, text, underlinedIndex, x, y, false);
    }
    /**
     * Draws the string at the specified location underlining the specified
     * character.
     *
     * @param c JComponent that will display the string, may be null
     * @param g Graphics to draw the text to
     * @param text String to display
     * @param underlinedIndex Index of a character in the string to underline
     * @param x X coordinate to draw the text at
     * @param y Y coordinate to draw the text at
     * @param useFPAPI use floating point API
     */
    public static void drawStringUnderlineCharAt(JComponent c, Graphics g,
                                                 String text, int underlinedIndex,
                                                 float x, float y,
                                                 boolean useFPAPI) {
        if (text == null || text.length() <= 0) {
            return;
        }
        drawString(c, g, text, x, y, useFPAPI);
        int textLength = text.length();
        if (underlinedIndex >= 0 && underlinedIndex < textLength ) {
            float underlineRectY = y;
            int underlineRectHeight = 1;
            float underlineRectX = 0;
            int underlineRectWidth = 0;
            boolean isPrinting = isPrinting(g);
            boolean needsTextLayout = isPrinting;
            if (!needsTextLayout) {
                synchronized (charsBufferLock) {
                    syncCharsBuffer(text);
                    needsTextLayout =
                            isComplexLayout(charsBuffer, 0, textLength);
                }
            }
            if (!needsTextLayout) {
                FontMetrics fm = g.getFontMetrics();
                underlineRectX = x +
                        sun.swing.SwingUtilities2.stringWidth(c,fm,
                                text.substring(0,underlinedIndex));
                underlineRectWidth = fm.charWidth(text.
                        charAt(underlinedIndex));
            } else {
                Graphics2D g2d = getGraphics2D(g);
                if (g2d != null) {
                    TextLayout layout =
                            createTextLayout(c, text, g2d.getFont(),
                                    g2d.getFontRenderContext());
                    if (isPrinting) {
                        float screenWidth = (float)g2d.getFont().
                                getStringBounds(text, getFontRenderContext(c)).getWidth();
                        // If text fits the screenWidth, then do not need to justify
                        if (sun.swing.SwingUtilities2.stringWidth(c, g2d.getFontMetrics(),
                                text) > screenWidth) {
                            layout = layout.getJustifiedLayout(screenWidth);
                        }
                    }
                    TextHitInfo leading =
                            TextHitInfo.leading(underlinedIndex);
                    TextHitInfo trailing =
                            TextHitInfo.trailing(underlinedIndex);
                    Shape shape =
                            layout.getVisualHighlightShape(leading, trailing);
                    Rectangle rect = shape.getBounds();
                    underlineRectX = x + rect.x;
                    underlineRectWidth = rect.width;
                }
            }
            g.fillRect((int) underlineRectX, (int) underlineRectY + 1,
                    underlineRectWidth, underlineRectHeight);
        }
    }

    public static int stringWidth(JComponent c, FontMetrics fm, String string) {
        return (int) stringWidth(c, fm, string, false);
    }

    public static boolean tabbedPaneChangeFocusTo(Component comp) {
        if (comp != null) {
            if (comp.isFocusTraversable()) {
                sun.swing.SwingUtilities2.compositeRequestFocus(comp);
                return true;
            } else if (comp instanceof JComponent
                    && ((JComponent)comp).requestDefaultFocus()) {

                return true;
            }
        }

        return false;
    }

    /**
     * Returns the width of the passed in String.
     * If the passed String is {@code null}, returns zero.
     *
     * @param c JComponent that will display the string, may be null
     * @param fm FontMetrics used to measure the String width
     * @param string String to get the width of
     * @param useFPAPI use floating point API
     */
    public static float stringWidth(JComponent c, FontMetrics fm, String string,
                                    boolean useFPAPI){
        if (string == null || string.isEmpty()) {
            return 0;
        }
        boolean needsTextLayout = ((c != null) &&
                (c.getClientProperty(TextAttribute.NUMERIC_SHAPING) != null));
        if (needsTextLayout) {
            synchronized(charsBufferLock) {
                int length = syncCharsBuffer(string);
                needsTextLayout = isComplexLayout(charsBuffer, 0, length);
            }
        }
        if (needsTextLayout) {
            TextLayout layout = createTextLayout(c, string,
                    fm.getFont(), fm.getFontRenderContext());
            return layout.getAdvance();
        } else {
            return getFontStringWidth(string, fm, useFPAPI);
        }
    }

    public static float getFontStringWidth(String data, FontMetrics fm,
                                           boolean useFPAPI)
    {
        if (useFPAPI) {
            Rectangle2D bounds = fm.getFont()
                    .getStringBounds(data, fm.getFontRenderContext());
            return (float) bounds.getWidth();
        } else {
            return fm.stringWidth(data);
        }
    }

    static boolean isPrinting(Graphics g) {
        return (g instanceof PrinterGraphics || g instanceof PrintGraphics);
    }

    public static Graphics2D getGraphics2D(Graphics g) {
        if (g instanceof Graphics2D) {
            return (Graphics2D) g;
        } else if (g instanceof ProxyPrintGraphics) {
            return (Graphics2D)(((ProxyPrintGraphics)g).getGraphics());
        } else {
            return null;
        }
    }

    private static TextLayout createTextLayout(JComponent c, String s,
                                               Font f, FontRenderContext frc) {
        Object shaper = (c == null ?
                null : c.getClientProperty(TextAttribute.NUMERIC_SHAPING));
        if (shaper == null) {
            return new TextLayout(s, f, frc);
        } else {
            Map<TextAttribute, Object> a = new HashMap<TextAttribute, Object>();
            a.put(TextAttribute.FONT, f);
            a.put(TextAttribute.NUMERIC_SHAPING, shaper);
            return new TextLayout(s, a, frc);
        }
    }

    public static float drawString(JComponent c, Graphics g,
                                   AttributedCharacterIterator iterator,
                                   int x, int y)
    {
        return drawStringImpl(c, g, iterator, x, y);
    }

    public static float drawString(JComponent c, Graphics g,
                                   AttributedCharacterIterator iterator,
                                   float x, float y)
    {
        return drawStringImpl(c, g, iterator, x, y);
    }

    private static float drawStringImpl(JComponent c, Graphics g,
                                        AttributedCharacterIterator iterator,
                                        float x, float y)
    {

        float retVal;
        boolean isPrinting = isPrinting(g);
        Color col = g.getColor();

        if (isPrinting) {
            /* Use alternate print color if specified */
            if (col instanceof PrintColorUIResource) {
                g.setColor(((PrintColorUIResource)col).getPrintColor());
            }
        }

        Graphics2D g2d = getGraphics2D(g);
        if (g2d == null) {
            g.drawString(iterator, (int)x, (int)y); //for the cases where advance
            //matters it should not happen
            retVal = x;

        } else {
            FontRenderContext frc;
            if (isPrinting) {
                frc = getFontRenderContext(c);
                if (frc.isAntiAliased() || frc.usesFractionalMetrics()) {
                    frc = new FontRenderContext(frc.getTransform(), false, false);
                }
            } else if ((frc = getFRCProperty(c)) != null) {
                /* frc = frc; ! */
            } else {
                frc = g2d.getFontRenderContext();
            }
            TextLayout layout;
            if (isPrinting) {
                FontRenderContext deviceFRC = g2d.getFontRenderContext();
                if (!isFontRenderContextPrintCompatible(frc, deviceFRC)) {
                    layout = new TextLayout(iterator, deviceFRC);
                    AttributedCharacterIterator trimmedIt =
                            getTrimmedTrailingSpacesIterator(iterator);
                    if (trimmedIt != null) {
                        float screenWidth = new TextLayout(trimmedIt, frc).
                                getAdvance();
                        layout = layout.getJustifiedLayout(screenWidth);
                    }
                } else {
                    layout = new TextLayout(iterator, frc);
                }
            } else {
                layout = new TextLayout(iterator, frc);
            }
            layout.draw(g2d, x, y);
            retVal = layout.getAdvance();
        }

        if (isPrinting) {
            g.setColor(col);
        }

        return retVal;
    }

    public static void drawString(JComponent c, Graphics g, String text,
                                  int x, int y) {
        drawString(c, g, text, x, y, false);
    }

    /**
     * Draws the string at the specified location.
     *
     * @param c JComponent that will display the string, may be null
     * @param g Graphics to draw the text to
     * @param text String to display
     * @param x X coordinate to draw the text at
     * @param y Y coordinate to draw the text at
     * @param useFPAPI use floating point API
     */
    public static void drawString(JComponent c, Graphics g, String text,
                                  float x, float y, boolean useFPAPI) {
        // c may be null

        // All non-editable widgets that draw strings call into this
        // methods.  By non-editable that means widgets like JLabel, JButton
        // but NOT JTextComponents.
        if ( text == null || text.length() <= 0 ) { //no need to paint empty strings
            return;
        }
        if (isPrinting(g)) {
            Graphics2D g2d = getGraphics2D(g);
            if (g2d != null) {
                /* The printed text must scale linearly with the UI.
                 * Calculate the width on screen, obtain a TextLayout with
                 * advances for the printer graphics FRC, and then justify
                 * it to fit in the screen width. This distributes the spacing
                 * more evenly than directly laying out to the screen advances.
                 */
                String trimmedText = text.stripTrailing();
                if (!trimmedText.isEmpty()) {
                    float screenWidth = (float) g2d.getFont().getStringBounds
                            (trimmedText, getFontRenderContext(c)).getWidth();
                    TextLayout layout = createTextLayout(c, text, g2d.getFont(),
                            g2d.getFontRenderContext());

                    // If text fits the screenWidth, then do not need to justify
                    if (sun.swing.SwingUtilities2.stringWidth(c, g2d.getFontMetrics(),
                            trimmedText) > screenWidth) {
                        layout = layout.getJustifiedLayout(screenWidth);
                    }
                    /* Use alternate print color if specified */
                    Color col = g2d.getColor();
                    if (col instanceof PrintColorUIResource) {
                        g2d.setColor(((PrintColorUIResource)col).getPrintColor());
                    }

                    layout.draw(g2d, x, y);

                    g2d.setColor(col);
                }

                return;
            }
        }

        // If we get here we're not printing
        if (g instanceof Graphics2D) {
            Graphics2D g2 = (Graphics2D)g;

            boolean needsTextLayout = ((c != null) &&
                    (c.getClientProperty(TextAttribute.NUMERIC_SHAPING) != null));

            if (needsTextLayout) {
                synchronized(charsBufferLock) {
                    int length = syncCharsBuffer(text);
                    needsTextLayout = isComplexLayout(charsBuffer, 0, length);
                }
            }

            Object aaHint = (c == null)
                    ? null
                    : c.getClientProperty(KEY_TEXT_ANTIALIASING);
            if (aaHint != null) {
                Object oldContrast = null;
                Object oldAAValue = g2.getRenderingHint(KEY_TEXT_ANTIALIASING);
                if (aaHint != oldAAValue) {
                    g2.setRenderingHint(KEY_TEXT_ANTIALIASING, aaHint);
                } else {
                    oldAAValue = null;
                }

                Object lcdContrastHint = c.getClientProperty(
                        KEY_TEXT_LCD_CONTRAST);
                if (lcdContrastHint != null) {
                    oldContrast = g2.getRenderingHint(KEY_TEXT_LCD_CONTRAST);
                    if (lcdContrastHint.equals(oldContrast)) {
                        oldContrast = null;
                    } else {
                        g2.setRenderingHint(KEY_TEXT_LCD_CONTRAST,
                                lcdContrastHint);
                    }
                }

                if (needsTextLayout) {
                    TextLayout layout = createTextLayout(c, text, g2.getFont(),
                            g2.getFontRenderContext());
                    layout.draw(g2, x, y);
                } else {
                    g2.drawString(text, x, y);
                }

                if (oldAAValue != null) {
                    g2.setRenderingHint(KEY_TEXT_ANTIALIASING, oldAAValue);
                }
                if (oldContrast != null) {
                    g2.setRenderingHint(KEY_TEXT_LCD_CONTRAST, oldContrast);
                }

                return;
            }

            if (needsTextLayout){
                TextLayout layout = createTextLayout(c, text, g2.getFont(),
                        g2.getFontRenderContext());
                layout.draw(g2, x, y);
                return;
            }
        }

        g.drawString(text, (int) x, (int) y);
    }

    private static AttributedCharacterIterator getTrimmedTrailingSpacesIterator
            (AttributedCharacterIterator iterator) {
        int curIdx = iterator.getIndex();

        char c = iterator.last();
        while(c != CharacterIterator.DONE && Character.isWhitespace(c)) {
            c = iterator.previous();
        }

        if (c != CharacterIterator.DONE) {
            int endIdx = iterator.getIndex();

            if (endIdx == iterator.getEndIndex() - 1) {
                iterator.setIndex(curIdx);
                return iterator;
            } else {
                AttributedString trimmedText = new AttributedString(iterator,
                        iterator.getBeginIndex(), endIdx + 1);
                return trimmedText.getIterator();
            }
        } else {
            return null;
        }
    }

    private static boolean
    isFontRenderContextPrintCompatible(FontRenderContext frc1,
                                       FontRenderContext frc2) {

        if (frc1 == frc2) {
            return true;
        }

        if (frc1 == null || frc2 == null) { // not supposed to happen
            return false;
        }

        if (frc1.getFractionalMetricsHint() !=
                frc2.getFractionalMetricsHint()) {
            return false;
        }

        /* If both are identity, return true */
        if (!frc1.isTransformed() && !frc2.isTransformed()) {
            return true;
        }

        /* That's the end of the cheap tests, need to get and compare
         * the transform matrices. We don't care about the translation
         * components, so return true if they are otherwise identical.
         */
        double[] mat1 = new double[4];
        double[] mat2 = new double[4];
        frc1.getTransform().getMatrix(mat1);
        frc2.getTransform().getMatrix(mat2);
        return
                mat1[0] == mat2[0] &&
                        mat1[1] == mat2[1] &&
                        mat1[2] == mat2[2] &&
                        mat1[3] == mat2[3];
    }

    private static FontRenderContext getFRCProperty(JComponent c) {
        if (c != null) {

            GraphicsConfiguration gc = c.getGraphicsConfiguration();
            AffineTransform tx = (gc == null) ? null : gc.getDefaultTransform();
            Object aaHint = c.getClientProperty(KEY_TEXT_ANTIALIASING);
            return getFRCFromCache(tx, aaHint);
        }
        return null;
    }

    private static final Object APP_CONTEXT_FRC_CACHE_KEY = new Object();

    private static FontRenderContext getFRCFromCache(AffineTransform tx,
                                                     Object aaHint) {
        if (tx == null && aaHint == null) {
            return null;
        }

        @SuppressWarnings("unchecked")
        Map<Object, FontRenderContext> cache = (Map<Object, FontRenderContext>)
                AppContext.getAppContext().get(APP_CONTEXT_FRC_CACHE_KEY);

        if (cache == null) {
            cache = new HashMap<>();
            AppContext.getAppContext().put(APP_CONTEXT_FRC_CACHE_KEY, cache);
        }

        Object key = (tx == null)
                ? aaHint
                : (aaHint == null ? tx : new KeyPair(tx, aaHint));

        FontRenderContext frc = cache.get(key);
        if (frc == null) {
            aaHint = (aaHint == null) ? VALUE_TEXT_ANTIALIAS_OFF : aaHint;
            frc = new FontRenderContext(tx, aaHint,
                    VALUE_FRACTIONALMETRICS_DEFAULT);
            cache.put(key, frc);
        }
        return frc;
    }

    private static class KeyPair {

        private final Object key1;
        private final Object key2;

        public KeyPair(Object key1, Object key2) {
            this.key1 = key1;
            this.key2 = key2;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof KeyPair)) {
                return false;
            }
            KeyPair that = (KeyPair) obj;
            return this.key1.equals(that.key1) && this.key2.equals(that.key2);
        }

        @Override
        public int hashCode() {
            return key1.hashCode() + 37 * key2.hashCode();
        }
    }
}
