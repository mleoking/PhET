/** Sam Reid*/
package edu.colorado.phet.cck3.common;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.event.MouseEvent;

/**
 * User: Sam Reid
 * Date: Jul 17, 2004
 * Time: 6:33:13 PM
 * Copyright (c) Jul 17, 2004 by Sam Reid
 */
public class TooltipControl implements MouseInputListener {
    JComponent component;
    String text;

    public TooltipControl( JComponent component, String text ) {
        this.component = component;
        this.text = text;
    }

    public void mouseClicked( MouseEvent e ) {
    }

    public void mouseEntered( MouseEvent e ) {
        component.setToolTipText( text );
    }

    public void mouseExited( MouseEvent e ) {
        component.setToolTipText( null );
    }

    public void mousePressed( MouseEvent e ) {
    }

    public void mouseReleased( MouseEvent e ) {
    }

    public void mouseDragged( MouseEvent e ) {
    }

    public void mouseMoved( MouseEvent e ) {
    }

}