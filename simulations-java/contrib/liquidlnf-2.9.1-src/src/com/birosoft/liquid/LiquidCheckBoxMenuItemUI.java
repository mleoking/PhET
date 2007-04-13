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

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

public class LiquidCheckBoxMenuItemUI extends LiquidMenuItemUI {

    public static ComponentUI createUI(JComponent c) {
        return new LiquidCheckBoxMenuItemUI();
    }

    protected String getPropertyPrefix() {
	return "CheckBoxMenuItem";
    }

//    public void processMouseEvent(JMenuItem item,MouseEvent e,MenuElement path[],MenuSelectionManager manager) {
//        Point p = e.getPoint();
//        if(p.x >= 0 && p.x < item.getWidth() &&
//           p.y >= 0 && p.y < item.getHeight()) {
//            if(e.getID() == MouseEvent.MOUSE_RELEASED) {
//                manager.clearSelectedPath();
//                item.doClick(0);
//            } else
//                manager.setSelectedPath(path);
//        } else if(item.getModel().isArmed()) {
//            MenuElement newPath[] = new MenuElement[path.length-1];
//            int i,c;
//            for(i=0,c=path.length-1;i<c;i++)
//                newPath[i] = path[i];
//            manager.setSelectedPath(newPath);
//        }
//    }
}
