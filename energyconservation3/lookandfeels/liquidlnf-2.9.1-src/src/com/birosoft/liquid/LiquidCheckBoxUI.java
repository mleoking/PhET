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

import java.awt.*;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicCheckBoxUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;


/**
 * This class represents the UI delegate for the JCheckBox component.
 *
 * @author Taoufik Romdhane
 */
public class LiquidCheckBoxUI extends BasicCheckBoxUI {
    private static Dimension size = new Dimension();
    private static Rectangle viewRect = new Rectangle();
    private static Rectangle iconRect = new Rectangle();
    private static Rectangle textRect = new Rectangle();

    /**
     * The Cached UI delegate.
     */
    private final static LiquidCheckBoxUI checkBoxUI = new LiquidCheckBoxUI();

    /**
     * Installs some default values for the given button.
     * The button border is replaced by a metouia border.
     *
     * @param button The reference of the button to install its default values.
     */
    static LiquidCheckBoxIcon skinnedIcon = new LiquidCheckBoxIcon();
    static BasicStroke focusStroke = new BasicStroke(1.0f,
            BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1.0f,
            new float[] { 1.0f / 1.0f }, 1.0f);

    /**
     * Creates the UI delegate for the given component.
     *
     * @param c The component to create its UI delegate.
     * @return The UI delegate for the given component.
     */
    public static ComponentUI createUI(final JComponent c) {
        return checkBoxUI;
    }

    public void installDefaults(AbstractButton button) {
        super.installDefaults(button);
        icon = skinnedIcon;
        button.setRolloverEnabled(true);
    }

    protected void paintFocus(Graphics g, Rectangle t, Dimension arg2) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.black);
        g2d.setStroke(focusStroke);

        //g2d.drawRect(t.x - 1, t.y - 1, t.width + 1, t.height + 1);
        g2d.drawLine(t.x - 1, t.y - 1, t.x - 1 + t.width + 1, t.y - 1);
        g2d.drawLine(t.x - 1, t.y - 1 + t.height + 1, t.x - 1 + t.width + 1,
            t.y - 1 + t.height + 1);
        g2d.drawLine(t.x - 1, t.y - 1, t.x - 1, t.y - 1 + t.height + 1);
        g2d.drawLine(t.x - 1 + t.width + 1, t.y - 1, t.x - 1 + t.width + 1,
            t.y - 1 + t.height + 1);
    }

    public synchronized void paint(Graphics g, JComponent c) {
        AbstractButton b = (AbstractButton) c;
        ButtonModel model = b.getModel();

        Font f = c.getFont();
        g.setFont(f);

        FontMetrics fm = g.getFontMetrics();

        Insets i = c.getInsets();
        size = b.getSize(size);
        viewRect.x = i.left;
        viewRect.y = i.top;
        viewRect.width = size.width - (i.right + viewRect.x);
        viewRect.height = size.height - (i.bottom + viewRect.y);
        iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;
        textRect.x = textRect.y = textRect.width = textRect.height = 0;

        Icon altIcon = b.getIcon();
        Icon selectedIcon = null;
        Icon disabledIcon = null;

        String text = SwingUtilities.layoutCompoundLabel(c, fm, b.getText(),
                (altIcon != null) ? altIcon : getDefaultIcon(),
                b.getVerticalAlignment(), b.getHorizontalAlignment(),
                b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
                viewRect, iconRect, textRect,
                (b.getText() == null) ? 0 : b.getIconTextGap());

        if (c.getParent() instanceof javax.swing.CellRendererPane) {
            c.setOpaque(true);
        } else if (c.isOpaque()) {
            c.setOpaque(false);
            c.repaint();
        }

        // Paint the radio button
        if (altIcon != null) {
            if (!model.isEnabled()) {
                if (model.isSelected()) {
                    altIcon = b.getDisabledSelectedIcon();
                } else {
                    altIcon = b.getDisabledIcon();
                }
            } else if (model.isPressed() && model.isArmed()) {
                altIcon = b.getPressedIcon();

                if (altIcon == null) {
                    // Use selected icon
                    altIcon = b.getSelectedIcon();
                }
            } else if (model.isSelected()) {
                if (b.isRolloverEnabled() && model.isRollover()) {
                    altIcon = (Icon) b.getRolloverSelectedIcon();

                    if (altIcon == null) {
                        altIcon = (Icon) b.getSelectedIcon();
                    }
                } else {
                    altIcon = (Icon) b.getSelectedIcon();
                }
            } else if (b.isRolloverEnabled() && model.isRollover()) {
                altIcon = (Icon) b.getRolloverIcon();
            }

            if (altIcon == null) {
                altIcon = b.getIcon();
            }

            altIcon.paintIcon(c, g, iconRect.x, iconRect.y);
        } else {
            getDefaultIcon().paintIcon(c, g, iconRect.x, iconRect.y);
        }

        // Draw the Text
        if (text != null) {
            View v = (View) c.getClientProperty(BasicHTML.propertyKey);

            if (v != null) {
                v.paint(g, textRect);
            } else {
                paintText(g, b, textRect, text);

                if (b.hasFocus() && b.isFocusPainted() && (textRect.width > 0) &&
                        (textRect.height > 0)) {
                    paintFocus(g, textRect, size);
                }
            }
        }
    }
}
