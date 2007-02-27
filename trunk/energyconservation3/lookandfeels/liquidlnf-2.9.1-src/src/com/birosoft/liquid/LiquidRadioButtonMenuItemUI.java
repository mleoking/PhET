/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*	Liquid Look and Feel                                                   *
*                                                                              *
*  Author, Miroslav Lazarevic                                                  *
*                                                                              *
*   For licensing information and credits, please refer to the                 *
*   comment in file com.birosoft.liquid.LiquidLookAndFeel                      *
*                                                                              *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.birosoft.liquid;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.plaf.ComponentUI;

public class LiquidRadioButtonMenuItemUI extends LiquidMenuItemUI
{
    
    public static ComponentUI createUI(JComponent b)
    {
        return new LiquidRadioButtonMenuItemUI();
    }
    
    protected String getPropertyPrefix()
    {
        return "RadioButtonMenuItem";
    }
    
    public void processMouseEvent(JMenuItem item, MouseEvent e, MenuElement path[], MenuSelectionManager manager)
    {
        Point p = e.getPoint();
        if (p.x >= 0 && p.x < item.getWidth() && p.y >= 0 && p.y < item.getHeight())
        {
            if (e.getID() == MouseEvent.MOUSE_RELEASED)
            {
                manager.clearSelectedPath();
                item.doClick(0);
                item.setArmed(false);
            } else
                manager.setSelectedPath(path);
        } else if (item.getModel().isArmed())
        {
            MenuElement newPath[] = new MenuElement[path.length - 1];
            int i, c;
            for (i = 0, c = path.length - 1; i < c; i++)
                newPath[i] = path[i];
            manager.setSelectedPath(newPath);
        }
    }
}