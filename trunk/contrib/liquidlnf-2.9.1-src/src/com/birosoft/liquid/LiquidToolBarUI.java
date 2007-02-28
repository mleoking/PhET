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

import com.birosoft.liquid.skin.Skin;
import com.birosoft.liquid.util.Colors;

import java.awt.*;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.basic.BasicToolBarUI;


/**
 * This class represents the UI delegate for the JToolBar component.
 *
 * @author Taoufik Romdhane
 */
public class LiquidToolBarUI extends BasicToolBarUI {
    /**
     * The Border used for buttons in a toolbar
     */
    private Border border = new EmptyBorder(4, 4, 4, 4);
    private int orientation = -1;
    private boolean changeBorder = true;

    //private Skin vbarHandler = new Skin("vtoolbarhandler.png", 1, 0);
    private Skin vbarHandler = new Skin("vtoolbarhandler.png", 1, 8, 3, 8, 3);
    private Skin hbarHandler = new Skin("htoolbarhandler.png", 1, 3, 8, 3, 8);

    /**
     * These insets are forced inner margin for the toolbar buttons.
     */
    private Insets insets = new Insets(2, 2, 2, 2);

    /**
     * Creates the UI delegate for the given component.
     *
     * @param c The component to create its UI delegate.
     * @return The UI delegate for the given component.
     */
    public static ComponentUI createUI(JComponent c) {
        return new LiquidToolBarUI();
    }

    /**
     * Installs some default values for the given toolbar.
     * The gets a rollover property.
     *
     * @param c The reference of the toolbar to install its default values.
     */
    public void installUI(JComponent c) {
        super.installUI(c);
        c.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
    }

    /**
     * Paints the given component.
     *
     * @param g The graphics context to use.
     * @param c The component to paint.
     */
    public void paint(Graphics g, JComponent c) {
        if (LiquidLookAndFeel.panelTransparency) {
            if (LiquidLookAndFeel.areStipplesUsed() &&
                    c.getParent() instanceof JPanel) {
                c.setOpaque(false);
            } else {
                c.setOpaque(true);
            }
        }

// 20060412 MEV - Added the check for stipples to correct issue #8
        if (c.isOpaque() && LiquidLookAndFeel.areStipplesUsed()) {
            Colors.drawStipples(g, c, c.getBackground());
        }

        if (!isFloating()) {
            if (toolBar.getOrientation() != orientation) {
                if (toolBar.getOrientation() == 0) {
                    if (toolBar.isFloatable()) {
                        toolBar.setBorder(new EmptyBorder(2, 11, 2, 2));
                    } else {
                        toolBar.setBorder(new EmptyBorder(2, 2, 2, 2));
                    }
                } else {
                    if (toolBar.isFloatable()) {
                        toolBar.setBorder(new EmptyBorder(12, 2, 2, 2));
                    } else {
                        toolBar.setBorder(new EmptyBorder(2, 2, 2, 2));
                    }
                }

                orientation = toolBar.getOrientation();
                changeBorder = true;
            }

            if (toolBar.getOrientation() == 0) {
                if (toolBar.isFloatable()) {
                    vbarHandler.draw(g, 0, 1, 2, 8, c.getHeight() - 4);
                }
            } else {
                if (toolBar.isFloatable()) {
                    hbarHandler.draw(g, 0, 1, 2, c.getWidth() - 4, 8);
                }
            }
        } else {
            if (changeBorder) {
                toolBar.setBorder(new EmptyBorder(1, 1, 1, 1));
                changeBorder = false;
                orientation = -1;
            }
        }
    }

    /*
    // Liquid vertical handler , nice try but ...
    protected void drawHandler(Graphics g, int height)
    {
                    // Little corrections of border
                    g.setColor(new Color(246,245,244));
                    g.drawLine(1,2,1,2);
                    g.drawLine(8,2,8,2);
                    g.setColor(new Color(238,237,236));
                    g.drawLine(1,c.getHeight()-3,1,c.getHeight()-3);
                    g.drawLine(8,c.getHeight()-3,8,c.getHeight()-3);
                    g.setColor(new Color(186,200,216));
                    g.drawLine(2,2,7,2);
                    g.setColor(new Color(143,154,166));
                    g.drawLine(2,height-3,6,height-3);
        g.setColor(new Color(205,210,217));
        g.drawLine(2,2,1,3);
        g.drawLine(7,2,8,3);
        g.drawLine(1,height-4,2,height-3);
        g.drawLine(8,height-4,6,height-3);
        g.setColor(new Color(162,174,188));
        g.drawLine(1,height-4,1,height-4);
        g.setColor(new Color(186,200,216));
        g.drawLine(1,3,1,3);
        g.drawLine(2,2,7,2);
        g.drawLine(1,4,1,height-5);
        g.setColor(new Color(143,154,166));
        g.drawLine(7,3,7,3);
        g.drawLine(7,height-4,7,height-4);
        g.drawLine(2,height-3,6,height-3);
        g.drawLine(8,4,8,height-5);
        // inner
        g.setColor(new Color(215,231,249));
        g.fillRect(3, 5, 6, height-10);
        g.setColor(new Color(226,240,255));
        g.drawLine(4,3,7,3);
        g.drawLine(3,4,8,4);
        g.drawLine(3,5,4,5);
        g.drawLine(7,5,8,5);
        g.drawLine(3,6,3,6);
        g.drawLine(8,6,8,6);
        g.setColor(new Color(238,246,255));
        g.drawLine(3,height-7,3,height-7);
        g.drawLine(8,height-7,8,height-7);
        g.drawLine(3,height-6,4,height-6);
        g.drawLine(7,height-6,8,height-6);
        g.drawLine(3,height-5,8,height-5);
        g.drawLine(4,height-4,7,height-4);
        Graphics2D g2 = (Graphics2D)g;
        GradientPaint grad = new GradientPaint(0,0,new Color(211,227,245),2, 10,new Color(200,216,234));
        g2.setPaint(grad);
        g2.fillRect(5, 5, 2, 10);
    }
     */

    /**
     * Sets the border of the given component to a rollover border.
     *
     * @param c The component to set its border.
     */
    protected void setBorderToRollover(Component c) {
        if (c instanceof AbstractButton) {
            AbstractButton b = (AbstractButton) c;
            if (b.getBorder() instanceof BasicBorders.MarginBorder) {
            	b.setBorder(border);
            }
// 20060412 MEV - Removing the force of not focusable because there isn't a visual 
// cue to the user when a toolbar button has focus AND clicking the button will
// not result in a FocusLostEvent being fired (Not even a temporary loss of focus).
// So if a user enters data into a JTextField and then clicks a toolbar button, 
// the JTextField won't receive a FocusLostEvent.
            if( LiquidLookAndFeel.toolbarButtonsFocusable ) {
                b.setFocusable(true);
            }
            else {
                b.setFocusable(false);
            }
            b.putClientProperty("JToolBar.isToolbarButton", Boolean.TRUE);
        }
    }

    protected void setBorderToNormal(Component c) {
        if (c instanceof AbstractButton) {
            AbstractButton b = (AbstractButton) c;
            if (b.getBorder() instanceof BasicBorders.MarginBorder) {
            	b.setBorder(border);
            }
// 20060412 MEV - Removing the force of not focusable because there isn't a visual 
// cue to the user when a toolbar button has focus AND clicking the button will
// not result in a FocusLostEvent being fired (Not even a temporary loss of focus).
// So if a user enters data into a JTextField and then clicks a toolbar button, 
// the JTextField won't receive a FocusLostEvent.
            if( LiquidLookAndFeel.toolbarButtonsFocusable ) {
                b.setFocusable(true);
            }
            else {
                b.setFocusable(false);
            }
            b.putClientProperty("JToolBar.isToolbarButton", Boolean.TRUE);
        }
    }
}
