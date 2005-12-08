/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.view.mousecontrol;

import java.util.ArrayList;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Oct 9, 2003
 * Time: 12:54:51 AM
 * Copyright (c) Oct 9, 2003 by Sam Reid
 */
public class CompositeMouseControl implements MouseControl {
    ArrayList controls = new ArrayList();

    public void addMouseControl(MouseControl mc) {
        controls.add(mc);
    }

    public void addHandCursor(AbstractShape sh) {
        addMouseControl(new HandCursorControl(sh));
    }

    public void addTranslation(AbstractShape sh, Translatable tc) {
        addMouseControl(new TranslationControl(tc, sh));
    }

    public void addHandAndTranslation(AbstractShape sh, Translatable tc) {
        addHandCursor(sh);
        addTranslation(sh, tc);
    }

    public boolean canHandleMousePress(MouseEvent event, Point2D.Double modelLoc) {
        for (int i = 0; i < controls.size(); i++) {
            MouseControl mouseControl = (MouseControl) controls.get(i);
            if (mouseControl.canHandleMousePress(event, modelLoc))
                return true;
        }
        return false;
    }

    public void mouseDragged(MouseEvent event, Point2D.Double modelLoc) {
        for (int i = 0; i < controls.size(); i++) {
            MouseControl mouseControl = (MouseControl) controls.get(i);
            mouseControl.mouseDragged(event, modelLoc);
        }
    }

    public void mousePressed(MouseEvent event, Point2D.Double modelLoc) {
        for (int i = 0; i < controls.size(); i++) {
            MouseControl mouseControl = (MouseControl) controls.get(i);
            mouseControl.mousePressed(event, modelLoc);
        }
    }

    public void mouseReleased(MouseEvent event, Point2D.Double modelLoc) {
        for (int i = 0; i < controls.size(); i++) {
            MouseControl mouseControl = (MouseControl) controls.get(i);
            mouseControl.mouseReleased(event, modelLoc);
        }
    }

    public void mouseEntered(MouseEvent event, Point2D.Double modelLoc) {
        for (int i = 0; i < controls.size(); i++) {
            MouseControl mouseControl = (MouseControl) controls.get(i);
            mouseControl.mouseEntered(event, modelLoc);
        }
    }

    public void mouseExited(MouseEvent event, Point2D.Double modelLoc) {
        for (int i = 0; i < controls.size(); i++) {
            MouseControl mouseControl = (MouseControl) controls.get(i);
            mouseControl.mouseExited(event, modelLoc);
        }
    }

}
