/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *        Liquid Look and Feel                                                   *
 *                                                                             *
 *  Author, Miroslav Lazarevic                                                 *
 *                                                                             *
 *   For licensing information and credits, please refer to the                *
 *   comment in file com.birosoft.liquid.LiquidLookAndFeel                     *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
/*
 * @(#)BasicListUI.java        1.93 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.birosoft.liquid;

import java.awt.*;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;


/**
 * A Windows L&F implementation of ListUI.
 * <p>
 *
 * @version 1.93 01/23/03
 * @author Hans Muller
 * @author Philip Milne
 */
public class LiquidListUI extends BasicListUI {
    private Color defaultBackground;

    /**
     * Paint one List cell: compute the relevant state, get the "rubber stamp"
     * cell renderer component, and then use the CellRendererPane to paint it.
     * Subclasses may want to override this method rather than paint().
     *
     * @see #paint
     */
    protected void paintCell(Graphics g, int row, Rectangle rowBounds,
        ListCellRenderer cellRenderer, ListModel dataModel,
        ListSelectionModel selModel, int leadIndex) {
        Object value = dataModel.getElementAt(row);
        boolean cellHasFocus = list.hasFocus() && (row == leadIndex);
        boolean isSelected = selModel.isSelectedIndex(row);

        Component rendererComponent = cellRenderer.getListCellRendererComponent(list,
                value, row, isSelected, cellHasFocus);

        if (LiquidLookAndFeel.defaultRowBackgroundMode) {
            if ((row % 2) == 0) {
                if (LiquidLookAndFeel.getDesktopColor().equals(rendererComponent.getBackground())) {
                    rendererComponent.setBackground(defaultBackground);
                }
            } else {
                if (defaultBackground.equals(rendererComponent.getBackground())) {
                    rendererComponent.setBackground(LiquidLookAndFeel.getDesktopColor());
                }
            }
        }

        int cx = rowBounds.x;
        int cy = rowBounds.y;
        int cw = rowBounds.width;
        int ch = rowBounds.height;
        rendererPane.paintComponent(g, rendererComponent, list, cx, cy, cw, ch,
            true);
    }

    public void paint(Graphics g, JComponent c) {
        if (LiquidLookAndFeel.defaultRowBackgroundMode &
                (defaultBackground == null)) {
            defaultBackground = c.getBackground();
        }

        super.paint(g, c);
    }

    /**
     * Returns a new instance of LiquidListUI.  LiquidListUI delegates are
     * allocated one per JList.
     *
     * @return A new ListUI implementation for the Windows look and feel.
     */
    public static ComponentUI createUI(JComponent list) {
        return new LiquidListUI();
    }
}
