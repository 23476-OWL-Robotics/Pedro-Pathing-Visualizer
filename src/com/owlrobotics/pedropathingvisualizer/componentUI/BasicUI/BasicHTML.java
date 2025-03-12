package com.owlrobotics.pedropathingvisualizer.componentUI.BasicUI;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.HTML;
import java.awt.*;

public class BasicHTML {

    public static int getHTMLBaseline(View view, int w, int h) {
        if (w < 0 || h < 0) {
            throw new IllegalArgumentException(
                    "Width and height must be >= 0");
        }
        if (view instanceof BasicHTML.Renderer) {
            return getBaseline(view.getView(0), w, h);
        }
        return -1;
    }

    static int getBaseline(JComponent c, int y, int ascent,
                           int w, int h) {
        View view = (View)c.getClientProperty(javax.swing.plaf.basic.BasicHTML.propertyKey);
        if (view != null) {
            int baseline = getHTMLBaseline(view, w, h);
            if (baseline < 0) {
                return baseline;
            }
            return y + baseline;
        }
        return y + ascent;
    }

    /**
     * Gets the baseline for the specified View.
     */
    public static int getBaseline(View view, int w, int h) {
        if (hasParagraph(view)) {
            view.setSize(w, h);
            return getBaseline(view, new Rectangle(0, 0, w, h));
        }
        return -1;
    }

    private static int getBaseline(View view, Shape bounds) {
        if (view.getViewCount() == 0) {
            return -1;
        }
        AttributeSet attributes = view.getElement().getAttributes();
        Object name = null;
        if (attributes != null) {
            name = attributes.getAttribute(StyleConstants.NameAttribute);
        }
        int index = 0;
        if (name == HTML.Tag.HTML && view.getViewCount() > 1) {
            // For html on widgets the header is not visible, skip it.
            index++;
        }
        bounds = view.getChildAllocation(index, bounds);
        if (bounds == null) {
            return -1;
        }
        View child = view.getView(index);
        if (view instanceof javax.swing.text.ParagraphView) {
            Rectangle rect;
            if (bounds instanceof Rectangle) {
                rect = (Rectangle)bounds;
            }
            else {
                rect = bounds.getBounds();
            }
            return rect.y + (int)(rect.height *
                    child.getAlignment(View.Y_AXIS));
        }
        return getBaseline(child, bounds);
    }

    private static boolean hasParagraph(View view) {
        if (view instanceof javax.swing.text.ParagraphView) {
            return true;
        }
        if (view.getViewCount() == 0) {
            return false;
        }
        AttributeSet attributes = view.getElement().getAttributes();
        Object name = null;
        if (attributes != null) {
            name = attributes.getAttribute(StyleConstants.NameAttribute);
        }
        int index = 0;
        if (name == HTML.Tag.HTML && view.getViewCount() > 1) {
            // For html on widgets the header is not visible, skip it.
            index = 1;
        }
        return hasParagraph(view.getView(index));
    }

    static class Renderer extends View {

        Renderer(JComponent c, ViewFactory f, View v) {
            super(null);
            host = c;
            factory = f;
            view = v;
            view.setParent(this);
            // initially layout to the preferred size
            setSize(view.getPreferredSpan(X_AXIS), view.getPreferredSpan(Y_AXIS));
        }

        /**
         * Fetches the attributes to use when rendering.  At the root
         * level there are no attributes.  If an attribute is resolved
         * up the view hierarchy this is the end of the line.
         */
        public AttributeSet getAttributes() {
            return null;
        }

        /**
         * Determines the preferred span for this view along an axis.
         *
         * @param axis may be either X_AXIS or Y_AXIS
         * @return the span the view would like to be rendered into.
         *         Typically the view is told to render into the span
         *         that is returned, although there is no guarantee.
         *         The parent may choose to resize or break the view.
         */
        public float getPreferredSpan(int axis) {
            if (axis == X_AXIS) {
                // width currently laid out to
                return width;
            }
            return view.getPreferredSpan(axis);
        }

        /**
         * Determines the minimum span for this view along an axis.
         *
         * @param axis may be either X_AXIS or Y_AXIS
         * @return the span the view would like to be rendered into.
         *         Typically the view is told to render into the span
         *         that is returned, although there is no guarantee.
         *         The parent may choose to resize or break the view.
         */
        public float getMinimumSpan(int axis) {
            return view.getMinimumSpan(axis);
        }

        /**
         * Determines the maximum span for this view along an axis.
         *
         * @param axis may be either X_AXIS or Y_AXIS
         * @return the span the view would like to be rendered into.
         *         Typically the view is told to render into the span
         *         that is returned, although there is no guarantee.
         *         The parent may choose to resize or break the view.
         */
        public float getMaximumSpan(int axis) {
            return Integer.MAX_VALUE;
        }

        /**
         * Specifies that a preference has changed.
         * Child views can call this on the parent to indicate that
         * the preference has changed.  The root view routes this to
         * invalidate on the hosting component.
         * <p>
         * This can be called on a different thread from the
         * event dispatching thread and is basically unsafe to
         * propagate into the component.  To make this safe,
         * the operation is transferred over to the event dispatching
         * thread for completion.  It is a design goal that all view
         * methods be safe to call without concern for concurrency,
         * and this behavior helps make that true.
         *
         * @param child the child view
         * @param width true if the width preference has changed
         * @param height true if the height preference has changed
         */
        public void preferenceChanged(View child, boolean width, boolean height) {
            host.revalidate();
            host.repaint();
        }

        /**
         * Determines the desired alignment for this view along an axis.
         *
         * @param axis may be either X_AXIS or Y_AXIS
         * @return the desired alignment, where 0.0 indicates the origin
         *     and 1.0 the full span away from the origin
         */
        public float getAlignment(int axis) {
            return view.getAlignment(axis);
        }

        /**
         * Renders the view.
         *
         * @param g the graphics context
         * @param allocation the region to render into
         */
        public void paint(Graphics g, Shape allocation) {
            Rectangle alloc = allocation.getBounds();
            view.setSize(alloc.width, alloc.height);
            view.paint(g, allocation);
        }

        /**
         * Sets the view parent.
         *
         * @param parent the parent view
         */
        public void setParent(View parent) {
            throw new Error("Can't set parent on root view");
        }

        /**
         * Returns the number of views in this view.  Since
         * this view simply wraps the root of the view hierarchy
         * it has exactly one child.
         *
         * @return the number of views
         * @see #getView
         */
        public int getViewCount() {
            return 1;
        }

        /**
         * Gets the n-th view in this container.
         *
         * @param n the number of the view to get
         * @return the view
         */
        public View getView(int n) {
            return view;
        }

        /**
         * Provides a mapping from the document model coordinate space
         * to the coordinate space of the view mapped to it.
         *
         * @param pos the position to convert
         * @param a the allocated region to render into
         * @return the bounding box of the given position
         */
        public Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException {
            return view.modelToView(pos, a, b);
        }

        /**
         * Provides a mapping from the document model coordinate space
         * to the coordinate space of the view mapped to it.
         *
         * @param p0 the position to convert >= 0
         * @param b0 the bias toward the previous character or the
         *  next character represented by p0, in case the
         *  position is a boundary of two views.
         * @param p1 the position to convert >= 0
         * @param b1 the bias toward the previous character or the
         *  next character represented by p1, in case the
         *  position is a boundary of two views.
         * @param a the allocated region to render into
         * @return the bounding box of the given position is returned
         * @throws BadLocationException  if the given position does
         *   not represent a valid location in the associated document
         * @throws IllegalArgumentException for an invalid bias argument
         * @see View#viewToModel
         */
        public Shape modelToView(int p0, Position.Bias b0, int p1,
                                 Position.Bias b1, Shape a) throws BadLocationException {
            return view.modelToView(p0, b0, p1, b1, a);
        }

        /**
         * Provides a mapping from the view coordinate space to the logical
         * coordinate space of the model.
         *
         * @param x x coordinate of the view location to convert
         * @param y y coordinate of the view location to convert
         * @param a the allocated region to render into
         * @return the location within the model that best represents the
         *    given point in the view
         */
        public int viewToModel(float x, float y, Shape a, Position.Bias[] bias) {
            return view.viewToModel(x, y, a, bias);
        }

        /**
         * Returns the document model underlying the view.
         *
         * @return the model
         */
        public Document getDocument() {
            return view.getDocument();
        }

        /**
         * Returns the starting offset into the model for this view.
         *
         * @return the starting offset
         */
        public int getStartOffset() {
            return view.getStartOffset();
        }

        /**
         * Returns the ending offset into the model for this view.
         *
         * @return the ending offset
         */
        public int getEndOffset() {
            return view.getEndOffset();
        }

        /**
         * Gets the element that this view is mapped to.
         *
         * @return the view
         */
        public Element getElement() {
            return view.getElement();
        }

        /**
         * Sets the view size.
         *
         * @param width the width
         * @param height the height
         */
        public void setSize(float width, float height) {
            this.width = (int) width;
            view.setSize(width, height);
        }

        /**
         * Fetches the container hosting the view.  This is useful for
         * things like scheduling a repaint, finding out the host
         * components font, etc.  The default implementation
         * of this is to forward the query to the parent view.
         *
         * @return the container
         */
        public Container getContainer() {
            return host;
        }

        /**
         * Fetches the factory to be used for building the
         * various view fragments that make up the view that
         * represents the model.  This is what determines
         * how the model will be represented.  This is implemented
         * to fetch the factory provided by the associated
         * EditorKit.
         *
         * @return the factory
         */
        public ViewFactory getViewFactory() {
            return factory;
        }

        private int width;
        private View view;
        private ViewFactory factory;
        private JComponent host;

    }
}
