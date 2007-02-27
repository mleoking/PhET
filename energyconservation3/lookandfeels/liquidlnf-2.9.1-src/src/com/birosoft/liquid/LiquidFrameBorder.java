/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *        Liquid Look and Feel                                                   *
 *                                                                             *
 *  Author, Miroslav Lazarevic                                                 *
 *                                                                             *
 *   For licensing information and credits, please refer to the                *
 *   comment in file com.birosoft.liquid.LiquidLookAndFeel                     *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package com.birosoft.liquid;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;


/**
 * LiquidFrameBorder
 *
 * @version 0.1
 * @author Miroslav Lazarević
 */
public class LiquidFrameBorder extends AbstractBorder implements UIResource {
    private static LiquidFrameBorder onlyInstance;
//    private static Robot robot;
//    private static boolean robotsSupported = true;
    private static final Insets insets = new Insets(0, 4, 4, 4);
    private boolean prevState = false;
    private Window window;
    private int titleHeight;

    /** indicates whether the internal frame is active */
    private boolean isActive = true;

    public static LiquidFrameBorder getInstance() {
        if (onlyInstance == null) {
            onlyInstance = new LiquidFrameBorder();

// 20060202 MEV - What the hell is this for?!  It isn't even USED, yet 
// causes Permission exceptions when ran from Webstart!
/*
            if ((robot == null) && robotsSupported) {
                try {
                    robot = new Robot();
                } catch (AWTException e) {
                    robotsSupported = false;
                }
            }
*/
        }

        return onlyInstance;
    }

    private void isWindowRealActive(Window window) {
        isActive = window.isActive();

        if (isActive) {
            prevState = true;
        }

        if (!prevState && !isActive) {
            isActive = true;
        }
    }

    /**
     * Uses the skins to paint the border
     * @see javax.swing.border.Border#paintBorder(Component, Graphics, int, int, int, int)
     */
    public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
        window = SwingUtilities.getWindowAncestor(c);

        if (window instanceof JDialog) {
            JDialog d = (JDialog) window;

            if (d.isModal()) {
                isActive = true;
            } else {
                isWindowRealActive(window);
            }
        } else {
            isWindowRealActive(window);
        }

        int frameTitleHeight = UIManager.getInt(
                "InternalFrame.frameTitleHeight");

        int index = isActive ? 0 : 1;

        drawLeftTop(g, isActive, 4, frameTitleHeight);

        g.translate(0, frameTitleHeight);
        drawLeft(g, isActive, 4, h - frameTitleHeight - 4);
        g.translate(0, -frameTitleHeight);

        g.translate(0, h - 4);

        //getSkinBottom().draw(g, index, w, 4);
        drawBottom(g, isActive, w, 4);
        g.translate(0, -(h - 4));

        g.translate(w - 4, 0);

        drawRightTop(g, isActive, 4, frameTitleHeight);

        g.translate(0, frameTitleHeight);
        drawRight(g, isActive, 4, h - frameTitleHeight - 4);
        g.translate(0, -frameTitleHeight);

        g.translate(-(w - 4), 0);
    }

    private void drawLeftTop(Graphics g, boolean isSelected, int w, int h) {
        if (LiquidLookAndFeel.winDecoPanther) {
        	Color c = new Color(198, 198, 198);
        	g.setColor(c);
        	g.fillRect(0, 0, w, h);
            return;
        }

        Color c = isSelected ? new Color(62, 145, 235) : new Color(175, 214, 255);
        g.setColor(c);
        g.fillRect(0, 0, w, h);
        c = isSelected ? new Color(94, 172, 255) : new Color(226, 240, 255);
        g.setColor(c);
        g.drawLine(0, 0, w, 0);
        g.drawLine(0, 0, 0, h);
        c = isSelected ? new Color(60, 141, 228) : new Color(170, 207, 247);
        g.setColor(c);
        g.drawLine(1, 1, w, 1);

        for (int i = 4; i < h; i += 4) {
            c = isSelected ? new Color(59, 138, 223) : new Color(166, 203, 242);
            g.setColor(c);
            g.drawLine(1, i, w, i);
            c = isSelected ? new Color(60, 141, 228) : new Color(170, 207, 247);
            g.setColor(c);
            g.drawLine(1, i + 1, w, i + 1);
        }

        c = isSelected ? new Color(47, 111, 180) : new Color(135, 164, 196);
        g.setColor(c);
        g.drawLine(w - 1, h - 1, w - 1, h - 1);
    }

    private void drawLeft(Graphics g, boolean isSelected, int w, int h) {
        if (LiquidLookAndFeel.winDecoPanther) {
        	Color c = new Color(198, 198, 198);
        	g.setColor(c);
        	g.fillRect(0, 0, w, h);
            return;
        }

        Color c = isSelected ? new Color(62, 145, 235) : new Color(175, 214, 255);
        g.setColor(c);
        g.fillRect(0, 0, w, h);
        c = isSelected ? new Color(94, 172, 255) : new Color(226, 240, 255);
        g.setColor(c);
        g.drawLine(0, 0, 0, h);
        c = isSelected ? new Color(59, 138, 223) : new Color(166, 203, 242);
        g.setColor(c);
        g.drawLine(1, 0, w, 0);

        for (int i = 3; i < h; i += 4) {
            c = isSelected ? new Color(59, 138, 223) : new Color(166, 203, 242);
            g.setColor(c);
            g.drawLine(1, i, w, i);
            c = isSelected ? new Color(60, 141, 228) : new Color(170, 207, 247);
            g.setColor(c);
            g.drawLine(1, i + 1, w, i + 1);
        }

        c = isSelected ? new Color(47, 111, 180) : new Color(135, 164, 196);
        g.setColor(c);
        g.drawLine(w - 1, 0, w - 1, h);
    }

    private void drawRightTop(Graphics g, boolean isSelected, int w, int h) {
        if (LiquidLookAndFeel.winDecoPanther) {
        	Color c = new Color(198, 198, 198);
        	g.setColor(c);
        	g.fillRect(0, 0, w, h);
            return;
        }

        Color c = isSelected ? new Color(62, 145, 235) : new Color(175, 214, 255);
        g.setColor(c);
        g.fillRect(0, 0, w, h);
        c = isSelected ? new Color(94, 172, 255) : new Color(226, 240, 255);
        g.setColor(c);
        g.drawLine(0, 0, w - 2, 0);
        c = isSelected ? new Color(60, 141, 228) : new Color(170, 207, 247);
        g.setColor(c);
        g.drawLine(0, 1, w - 2, 1);

        for (int i = 4; i < h; i += 4) {
            c = isSelected ? new Color(59, 138, 223) : new Color(166, 203, 242);
            g.setColor(c);
            g.drawLine(0, i, w - 2, i);
            c = isSelected ? new Color(60, 141, 228) : new Color(170, 207, 247);
            g.setColor(c);
            g.drawLine(0, i + 1, w - 2, i + 1);
        }

        c = isSelected ? new Color(94, 172, 255) : new Color(226, 240, 255);
        g.setColor(c);
        g.drawLine(0, h - 1, 0, h - 1);
        c = isSelected ? new Color(47, 111, 180) : new Color(135, 164, 196);
        g.setColor(c);
        g.drawLine(w - 1, 0, w - 1, h);
    }

    private void drawRight(Graphics g, boolean isSelected, int w, int h) {
        if (LiquidLookAndFeel.winDecoPanther) {
        	Color c = new Color(198, 198, 198);
        	g.setColor(c);
        	g.fillRect(0, 0, w, h);
            return;
        }

        Color c = isSelected ? new Color(62, 145, 235) : new Color(175, 214, 255);
        g.setColor(c);
        g.fillRect(0, 0, w, h);
        c = isSelected ? new Color(94, 172, 255) : new Color(226, 240, 255);
        g.setColor(c);
        g.drawLine(0, 0, 0, h);
        c = isSelected ? new Color(59, 138, 223) : new Color(166, 203, 242);
        g.setColor(c);
        g.drawLine(1, 0, w, 0);

        for (int i = 3; i < h; i += 4) {
            c = isSelected ? new Color(59, 138, 223) : new Color(166, 203, 242);
            g.setColor(c);
            g.drawLine(1, i, w, i);
            c = isSelected ? new Color(60, 141, 228) : new Color(170, 207, 247);
            g.setColor(c);
            g.drawLine(1, i + 1, w, i + 1);
        }

        c = isSelected ? new Color(47, 111, 180) : new Color(135, 164, 196);
        g.setColor(c);
        g.drawLine(w - 1, 0, w - 1, h);
    }

    private void drawBottom(Graphics g, boolean isSelected, int w, int h) {
        if (LiquidLookAndFeel.winDecoPanther) {
        	Color c = new Color(198, 198, 198);
        	g.setColor(c);
        	g.fillRect(0, 0, w, h);
            return;
        }

        Color c = isSelected ? new Color(62, 145, 235) : new Color(175, 214, 255);
        g.setColor(c);
        g.fillRect(1, 0, w - 1, h - 1);
        c = isSelected ? new Color(94, 172, 255) : new Color(226, 240, 255);
        g.setColor(c);
        g.drawLine(3, 0, w - 4, 0);
        g.drawLine(0, 0, 0, h - 2);
        c = isSelected ? new Color(47, 111, 180) : new Color(135, 164, 196);
        g.setColor(c);
        g.drawLine(0, h - 1, w, h - 1);
        g.drawLine(w - 1, 0, w - 1, h - 1);
    }

    /**
     *
     * @see javax.swing.border.Border#getBorderInsets(Component)
     */
    public Insets getBorderInsets(Component c) {
        if (LiquidLookAndFeel.winDecoPanther) {
            return new Insets(0, 1, 1, 1);
        }

        return new Insets(0, 4, 4, 4);
    }

    /**
     *  inform the border whether the internal frame is active or not
     * @param isActive
     */
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
}
