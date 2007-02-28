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

import com.birosoft.liquid.util.Colors;

import java.awt.*;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;


public class LiquidPanelUI extends BasicPanelUI {
    // Shared UI object
    private static LiquidPanelUI panelUI;
    private static ArrayList panels = new ArrayList();

    public static ComponentUI createUI(JComponent c) {
        if (panelUI == null) {
            panelUI = new LiquidPanelUI();
        }

        return panelUI;
    }

    public void installUI(JComponent c) {
        JPanel p = (JPanel) c;
        super.installUI(p);
        installDefaults(p);
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);

        Iterator i = panels.iterator();

        while (i.hasNext()) {
            ((JPanel) i.next()).setOpaque(true);
        }

        panels.removeAll(panels);
    }

    public void paint(Graphics g, JComponent c) {
        Color bg = LiquidLookAndFeel.getBackgroundColor();

        if (LiquidLookAndFeel.areStipplesUsed()) {
            Container container = c.getParent();

            if (LiquidLookAndFeel.panelTransparency &&
                    container instanceof JPanel &&
                    (c.isOpaque() &&
                    (((JPanel) c).getClientProperty("panelTransparency") == null))) {
                panels.add(c);
                c.setOpaque(false);

                if (c.isOpaque()) {
                    ((JPanel) c).putClientProperty("panelTransparency", null);
                }
                container.invalidate();
                container.repaint();
            }

            if (LiquidLookAndFeel.getBackgroundColor().equals(c.getBackground()) &&
                    c.isOpaque()) {
                Colors.drawStipples(g, c, bg);
            }
            
// 20051020 MEV - Removed the forcing of opaque if parent is JLayeredPane because of reported
// issue #7.            
//            if (container instanceof JLayeredPane) {
//                c.setOpaque(true);
//            }
        }

        super.paint(g, c);
    }
}
