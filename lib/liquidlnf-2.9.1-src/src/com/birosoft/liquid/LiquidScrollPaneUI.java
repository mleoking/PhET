/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *        Liquid Look and Feel                                                   *
 *                                                                              *
 *  Author, Miroslav Lazarevic                                                  *
 *                                                                              *
 *   For licensing information and credits, please refer to the                 *
 *   comment in file com.birosoft.liquid.LiquidLookAndFeel                      *
 *                                                                              *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package com.birosoft.liquid;

import java.awt.event.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollPaneUI;


/**
 * This class represents the UI delegate for the JScrollPane component.
 *
 * @author Taoufik Romdhane
 */
public class LiquidScrollPaneUI extends BasicScrollPaneUI
    implements PropertyChangeListener {
    /**
     * Creates the UI delegate for the given component.
     *
     * @param c The component to create its UI delegate.
     * @return The UI delegate for the given component.
     */
    public static ComponentUI createUI(JComponent c) {
        return new LiquidScrollPaneUI();
    }

    /**
     * Creates an instance of MouseWheelListener, which is added to the
     * JScrollPane by installUI().  The returned MouseWheelListener is used
     * to handle mouse wheel-driven scrolling.
     *
     * @return      MouseWheelListener which implements wheel-driven scrolling
     * @see #installUI
     * @see MouseWheelHandler
     * @since 1.4
     */
    protected MouseWheelListener createMouseWheelListener() {
        return new MouseWheelHandler();
    }

    /**
     * Installs some default values for the given scrollpane.
     * The free standing property is disabled here.
     *
     * @param c The reference of the scrollpane to install its default values.
     */
    public void installUI(JComponent c) {
        super.installUI(c);

        scrollpane.getHorizontalScrollBar().putClientProperty(LiquidScrollBarUI.FREE_STANDING_PROP,
            Boolean.FALSE);
        scrollpane.getVerticalScrollBar().putClientProperty(LiquidScrollBarUI.FREE_STANDING_PROP,
            Boolean.FALSE);
    }

    /**
     * Creates a property change listener that does nothing inorder to prevent the
     * free standing scrollbars.
     *
     * @return An empty property change listener.
     */
    protected PropertyChangeListener createScrollBarSwapListener() {
        return this;
    }

    /**
     * Simply ignore any change.
     *
     * @param event The property change event.
     */
    public void propertyChange(PropertyChangeEvent event) {
    }

    /**
     * MouseWheelHandler is an inner class which implements the
     * MouseWheelListener interface.  MouseWheelHandler responds to
     * MouseWheelEvents by scrolling the JScrollPane appropriately.
     * If the scroll pane's
     * <code>isWheelScrollingEnabled</code>
     * method returns false, no scrolling occurs.
     *
     * @see javax.swing.JScrollPane#isWheelScrollingEnabled
     * @see #createMouseWheelListener
     * @see java.awt.event.MouseWheelListener
     * @see java.awt.event.MouseWheelEvent
     * @since 1.4
     */
    protected class MouseWheelHandler implements MouseWheelListener {
        /**
         * Called when the mouse wheel is rotated while over a
         * JScrollPane.
         *
         * @param e     MouseWheelEvent to be handled
         * @since 1.4
         */
        public void mouseWheelMoved(java.awt.event.MouseWheelEvent e) {
            if (scrollpane.isWheelScrollingEnabled() &&
                    (e.getScrollAmount() != 0)) {
                JScrollBar toScroll = scrollpane.getVerticalScrollBar();
                int direction = 0;
                int length = toScroll.getHeight();

                // find which scrollbar to scroll, or return if none
                if ((toScroll == null) || !toScroll.isVisible() ||
                        (e.getModifiers() == InputEvent.ALT_MASK)) {
                    toScroll = scrollpane.getHorizontalScrollBar();

                    if ((toScroll == null) || !toScroll.isVisible()) {
                        return;
                    }

                    length = toScroll.getWidth();
                }

                direction = (e.getWheelRotation() < 0) ? (-1) : 1;

                if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
                    int newValue = toScroll.getValue() +
                        ((e.getWheelRotation() * length) / (toScroll.getUnitIncrement() * 2));
                    toScroll.setValue(newValue);
                } else if (e.getScrollType() == MouseWheelEvent.WHEEL_BLOCK_SCROLL) {
                    int newValue = toScroll.getValue() +
                        ((e.getWheelRotation() * length) / (toScroll.getBlockIncrement() * 2));
                    toScroll.setValue(newValue);
                }
            }
        }
    }
}
